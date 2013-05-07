package org.talend.repository.services.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.wsdl.ui.internal.InternalWSDLMultiPageEditor;
import org.eclipse.wst.wsdl.ui.internal.actions.OpenInNewEditor;
import org.eclipse.wst.wsdl.ui.internal.adapters.WSDLBaseAdapter;
import org.eclipse.wst.wsdl.ui.internal.asd.actions.BaseSelectionAction;
import org.eclipse.wst.wsdl.ui.internal.asd.util.IOpenExternalEditorHelper;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.IESBService;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.model.update.RepositoryUpdateManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.DesignerPlugin;
import org.talend.repository.editor.RepositoryEditorInput;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.Messages;
import org.talend.repository.services.action.AssignJobAction;
import org.talend.repository.services.action.ServiceEditorInput;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.model.services.ServicesFactory;

@SuppressWarnings("restriction")
public class LocalWSDLEditor extends InternalWSDLMultiPageEditor {

    private ServiceItem serviceItem;

    private RepositoryNode repositoryNode;

    public LocalWSDLEditor(){
    	super();
    	getEditDomain().setCommandStack(new LocalCommandStack(this));
    }
    
    @Override
    public void doSave(IProgressMonitor monitor) {
    	if(isEditorInputReadOnly()){
    		MessageDialog.openWarning(getSite().getShell(),  Messages.WSDLFileIsReadOnly_Title,  Messages.WSDLFileIsReadOnly_Message);
    		return;
    	}
        super.doSave(monitor);
        save();
    }
    
    public boolean isEditorInputReadOnly(){
    	return ((RepositoryEditorInput)getEditorInput()).isReadOnly();
    }

    private void save() {
    	if(isEditorInputReadOnly()){
    		MessageDialog.openWarning(getSite().getShell(),  Messages.WSDLFileIsReadOnly_Title,  Messages.WSDLFileIsReadOnly_Message);
    		return;
    	}
        if (serviceItem != null) {
            try {
                saveModel();

                // update
                RepositoryUpdateManager.updateServices(serviceItem);

                IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
                factory.save(serviceItem);
                RepositoryManager.refreshSavedNode(repositoryNode);

                if (GlobalServiceRegister.getDefault().isServiceRegistered(IESBService.class)) {
                    IESBService service = (IESBService) GlobalServiceRegister.getDefault().getService(IESBService.class);
                    if (service != null) {
                        service.refreshComponentView(serviceItem);
                    }
                }
                // ////////// TODO: remove this ugly patch! do correct changeset
                EList<ServicePort> servicePorts = ((ServiceConnection) serviceItem.getConnection()).getServicePort();
                for (ServicePort servicePort : servicePorts) {
                    List<IRepositoryNode> portNodes = repositoryNode.getChildren();
                    IRepositoryNode portNode = null;
                    for (IRepositoryNode node : portNodes) {
                        if ((node.getObject()).getLabel().equals(servicePort.getName())) {
                            portNode = node;
                        }
                    }
                    if (portNode == null) {
                        // for now, if the port has been renamed, we just lose all links (avoid an NPE for nothing)
                        continue;
                    }
                    EList<ServiceOperation> operations = servicePort.getServiceOperation();
                    for (ServiceOperation operation : operations) {
                        String referenceJobId = operation.getReferenceJobId();
                        if (referenceJobId != null) {
                            for (IRepositoryNode operationNode : portNode.getChildren()) {
                                if (operationNode.getObject().getLabel().startsWith(operation.getName() + "-")) {
                                    IRepositoryNode jobNode = org.talend.core.repository.seeker.RepositorySeekerManager.getInstance().searchRepoViewNode(referenceJobId, false);
                                    AssignJobAction action = new AssignJobAction((RepositoryNode) operationNode);
                                    action.assign(jobNode);
                                    break;
                                }
                            }
                        }
                    }
                }
                // ////////// TODO
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

	private boolean needRefreshBinding(Definition definition) {
		try {
			List<String> lstBindingOperations = new ArrayList<String>();
			for (Object obj : definition.getBindings().values()) {
				Binding binding = (Binding) obj;
				String ns = binding.getPortType().getQName().getNamespaceURI();
				@SuppressWarnings("unchecked")
				List<BindingOperation> list = binding.getBindingOperations();
				for (BindingOperation bindingoperation : list) {
					if (bindingoperation.getOperation().isUndefined()) {
						// show warning if operation was deleted
						return true;
					}
					String name = bindingoperation.getOperation().getName();
					lstBindingOperations.add(ns + ";" + name);
				}
			}

			for (Object obj : definition.getPortTypes().values()) {
				PortType portType = (PortType) obj;
				String ns = portType.getQName().getNamespaceURI();
				@SuppressWarnings("unchecked")
				List<Operation> list = portType.getOperations();
				for (Operation operation : list) {
					String name = operation.getName();
					if (!lstBindingOperations.contains(ns + ";" + name)) {
						// show warning if operation was added
						return true;
					}
				}
			}
		} catch (NullPointerException npe) {
			// Show warning if any object is null
			return true;
		}
		return false;
	}
    
    
    private void saveModel() throws CoreException {
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

        Definition definition = WSDLUtils.getDefinition(serviceItem);
		if (needRefreshBinding(definition)) {
			MessageDialog.openWarning(getSite().getShell(),
					Messages.LocalWSDLEditor_refreshBindingTitle,
					Messages.LocalWSDLEditor_refreshBindingMessage);
		}

        // changed for TDI-18005
        Map<String, String> portNameIdMap = new HashMap<String, String>();
        Map<String, EMap<String, String>> portAdditionalMap = new HashMap<String, EMap<String, String>>();
        Map<String, String> operNameIdMap = new HashMap<String, String>();
        Map<String, String> operJobMap = new HashMap<String, String>();

        EList<ServicePort> oldServicePorts = ((ServiceConnection) serviceItem.getConnection()).getServicePort();
        // get old service port item names and operation names under them
        HashMap<String, ArrayList<String>> oldPortItemNames = new HashMap<String, ArrayList<String>>();

        for (ServicePort servicePort : oldServicePorts) {
            // keep id
            portNameIdMap.put(servicePort.getName(), servicePort.getId());

            // keep additional infos
            portAdditionalMap.put(servicePort.getId(), servicePort.getAdditionalInfo());

            EList<ServiceOperation> operations = servicePort.getServiceOperation();
            ArrayList<String> operationNames = new ArrayList<String>();
            for (ServiceOperation operation : operations) {
                operNameIdMap.put(operation.getName(), operation.getId());
                operationNames.add(operation.getLabel());
                // record assigned job
                operJobMap.put(operation.getId(), operation.getReferenceJobId());
            }
            oldPortItemNames.put(servicePort.getName(), operationNames);
        }

        ((ServiceConnection) serviceItem.getConnection()).getServicePort().clear();
        for (Object obj : definition.getAllPortTypes().values()) {
            PortType portType = (PortType) obj;
            if (portType.isUndefined()) {
                continue;
            }

            ServicePort port = ServicesFactory.eINSTANCE.createServicePort();
            String portName = portType.getQName().getLocalPart();
            port.setName(portName);
            // set port id
            String id = portNameIdMap.get(portName);
            if (id != null) {
                port.setId(id);
                // restore additional infos
                port.getAdditionalInfo().putAll(portAdditionalMap.get(id));
            } else {
                port.setId(factory.getNextId());
            }

            @SuppressWarnings("unchecked")
			List<Operation> list = portType.getOperations();
            for (Operation operation : list) {
                if (operation.isUndefined()) {
                    // means the operation has been removed already ,why ?
                    continue;
                }
                ServiceOperation serviceOperation = ServicesFactory.eINSTANCE.createServiceOperation();
                serviceOperation.setName(operation.getName());
                Iterator<String> operationIterator = operNameIdMap.keySet().iterator();
                while (operationIterator.hasNext()) {
                    String oldOperationName = operationIterator.next();
                    String operationId = operNameIdMap.get(oldOperationName);
                    if (oldOperationName.equals(operation.getName())) {
                        serviceOperation.setId(operationId);
                        // re-assign job
                        String jobId = operJobMap.get(operationId);
                        if (jobId != null) {
                            serviceOperation.setReferenceJobId(jobId);
                        }
                    }
                }
                if (serviceOperation.getId() == null || serviceOperation.getId().equals("")) {
                    serviceOperation.setId(factory.getNextId());
                }
                if (operation.getDocumentationElement() != null) {
                    serviceOperation.setDocumentation(operation.getDocumentationElement().getTextContent());
                }
                boolean hasAssignedjob = false;
                ArrayList<String> operationNames = oldPortItemNames.get(portName);
                String referenceJobId = serviceOperation.getReferenceJobId();
                if (operationNames != null && referenceJobId != null) {
                    IRepositoryViewObject repObj = null;
                    try {
                        repObj = factory.getLastVersion(referenceJobId);
                    } catch (PersistenceException e) {
                        ExceptionHandler.process(e);
                    }
                    if (repObj != null) {
                        for (String name : operationNames) {
                            if (name.equals(operation.getName() + '-' + repObj.getLabel())) {
                                serviceOperation.setLabel(name);
                                hasAssignedjob = true;
                                break;
                            }
                        }
                    }
                }
                if (!hasAssignedjob) {
                    serviceOperation.setLabel(operation.getName());
                }
                port.getServiceOperation().add(serviceOperation);
            }
            ((ServiceConnection) serviceItem.getConnection()).getServicePort().add(port);
        }
    }
    
    @Override
    protected void createActions() {
    	super.createActions();
    	ActionRegistry registry = getActionRegistry();
    	BaseSelectionAction action = new OpenInNewEditor(this) {
    		@Override
    		public void run() {

    		    if (getSelectedObjects().size() > 0)
    		    {
    		      Object o = getSelectedObjects().get(0);
    		      // should make this generic and be able to get the owner from a facade object
    		      if (o instanceof WSDLBaseAdapter)
    		      {
    		        WSDLBaseAdapter baseAdapter = (WSDLBaseAdapter)o;
    		        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    		        IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
    		        Object object = editorPart.getAdapter(org.eclipse.wst.wsdl.Definition.class);
    		        if (object instanceof org.eclipse.wst.wsdl.Definition)
    		        {
    		          EObject eObject = (EObject)baseAdapter.getTarget();
    		          OpenOnSelectionHelper openHelper = new OpenOnSelectionHelper((org.eclipse.wst.wsdl.Definition)object);
    		          openHelper.openEditor(eObject);
    		        }
    		      }
    		    }
    		}
    	};
    	action.setSelectionProvider(getSelectionManager());
	    registry.registerAction(action);
    }

    @Override
    protected void initializeGraphicalViewer() {
    	super.initializeGraphicalViewer();
    	MenuManager contextMenu = getGraphicalViewer().getContextMenu();
    	if(isEditorInputReadOnly()){
    		contextMenu.setVisible(false);
    	}
    }
    
    public RepositoryNode getRepositoryNode() {
        return this.repositoryNode;
    }

    public void setRepositoryNode(RepositoryNode repositoryNode) {
        this.repositoryNode = repositoryNode;
    }

    public ServiceItem getServiceItem() {
        return serviceItem;
    }

    public void setServiceItem(ServiceItem serviceItem) {
        this.serviceItem = serviceItem;
    }

    @Override
    public boolean isFileReadOnly() {
    	return super.isFileReadOnly() || isEditorInputReadOnly();
    }
    
    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {
    	if(type == IOpenExternalEditorHelper.class  && isEditorInputReadOnly()){
    		return null;
    	}
    	return super.getAdapter(type);
    }
    
    @Override
    public void dispose() {
    	//unlock item if necessary
    	IEditorInput currentEditorInput=getEditorInput();
    	if(currentEditorInput!=null&&currentEditorInput instanceof ServiceEditorInput) {
    		ServiceEditorInput serviceEditorInput=(ServiceEditorInput) currentEditorInput;
    		Item currentItem = serviceEditorInput.getItem();
			if(currentItem!=null) {
    			//unlock item if no other editors open it.
				boolean openItemInOtherEditor=false;
    			IEditorReference[] editorRefs=getEditorSite().getPage().getEditorReferences();
    			for (IEditorReference editorRef : editorRefs) {
    				if(editorRef.getEditor(false)==this) {
    					continue;
    				}
    				try {
    					IEditorInput editorInput = editorRef.getEditorInput();
    					if(editorInput instanceof ServiceEditorInput) {
    						Item item=((ServiceEditorInput) editorInput).getItem();
    						if(item==currentItem) {
    							//open this item & not this one.
    							openItemInOtherEditor=true;
    						}
    					}
    				} catch (PartInitException e) {
    					//ignore and compare others
    				}
    			}
    			if(!openItemInOtherEditor) {
    				try {
    					DesignerPlugin.getDefault().getProxyRepositoryFactory().unlock(currentItem);
    				} catch (Exception e) {
    					ExceptionHandler.process(e);
    				}
    			}
    			
    		}
    	}
    	
    	super.dispose();
    }
    
}
