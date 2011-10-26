package org.talend.repository.services.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.wsdl.ui.internal.InternalWSDLMultiPageEditor;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.IESBService;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.model.update.RepositoryUpdateManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.ResourceModelUtils;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.services.action.AssignJobAction;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.model.services.ServicesFactory;

public class LocalWSDLEditor extends InternalWSDLMultiPageEditor {

    private ServiceItem serviceItem;

    private RepositoryNode repositoryNode;

    public LocalWSDLEditor() {
        // TODO Auto-generated constructor stub
    }

    protected AdapterImpl dirtyListener = new AdapterImpl() {

        @Override
        public void notifyChanged(Notification notification) {
            if (notification.getEventType() == Notification.REMOVING_ADAPTER) {
                save();
            }
        }
    };

    @Override
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        save();
    }

    private void save() {
        if (serviceItem != null) {
            IProject currentProject;
            try {
                currentProject = ResourceModelUtils.getProject(ProjectManager.getInstance().getCurrentProject());
                String foldPath = serviceItem.getState().getPath();
                String folder = "";
                if (!foldPath.equals("")) {
                    folder = "/" + foldPath;
                }
                IFile fileTemp = currentProject.getFolder("services" + folder).getFile(
                        repositoryNode.getObject().getProperty().getLabel() + "_"
                                + repositoryNode.getObject().getProperty().getVersion() + ".wsdl");
                if (fileTemp.exists()) {
                    saveModel(fileTemp.getRawLocation().toOSString());
                }
                // if (isDirty()) {
                // update
                RepositoryUpdateManager.updateServices(serviceItem);
                // }

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
                        if (node.getObject().getLabel().equals(servicePort.getName())) {
                            portNode = node;
                        }
                    }
                    EList<ServiceOperation> operations = servicePort.getServiceOperation();
                    for (ServiceOperation operation : operations) {
                        String referenceJobId = operation.getReferenceJobId();
                        if (referenceJobId != null) {
                            for (IRepositoryNode operationNode : portNode.getChildren()) {
                                if (operationNode.getObject().getLabel().startsWith(operation.getName() + "-")) {
                                    RepositoryNode jobNode = RepositoryNodeUtilities.getRepositoryNode(referenceJobId, false);
                                    AssignJobAction action = new AssignJobAction((RepositoryNode) operationNode);
                                    action.assign(jobNode);
                                    break;
                                }
                            }
                        }
                    }
                }
                // ////////// TODO

            } catch (PersistenceException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveModel(String path) {
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        WSDLFactory wsdlFactory;
        try {
            wsdlFactory = WSDLFactory.newInstance();
            WSDLReader newWSDLReader = wsdlFactory.newWSDLReader();
            newWSDLReader.setExtensionRegistry(wsdlFactory.newPopulatedExtensionRegistry());
            newWSDLReader.setFeature(com.ibm.wsdl.Constants.FEATURE_VERBOSE, false);
            Definition definition = newWSDLReader.readWSDL(path);
            Map portTypes = definition.getAllPortTypes();
            Iterator it = portTypes.keySet().iterator();

            // changed for TDI-18005
            Map<String, String> portNameIdMap = new HashMap<String, String>();
            Map<String, Map<String, String>> portAdditionalMap = new HashMap<String, Map<String, String>>();
            Map<String, String> operNameIdMap = new HashMap<String, String>();
            Map<String, String> operJobMap = new HashMap<String, String>();

            EList<ServicePort> oldServicePorts = ((ServiceConnection) serviceItem.getConnection()).getServicePort();
            // get old service port item names and operation names under them
            HashMap<String, ArrayList<String>> oldPortItemNames = new HashMap<String, ArrayList<String>>();

            for (ServicePort servicePort : oldServicePorts) {
                // keep id
                portNameIdMap.put(servicePort.getName(), servicePort.getId());

                // keep additional infos
                Map<String, String> additionInfoMap = new HashMap<String, String>();
                EMap<String, String> oldInfo = servicePort.getAdditionalInfo();
                Iterator<Entry<String, String>> iterator = oldInfo.iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> next = iterator.next();
                    additionInfoMap.put(next.getKey(), next.getValue());
                }
                portAdditionalMap.put(servicePort.getId(), additionInfoMap);

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
            while (it.hasNext()) {
                QName key = (QName) it.next();
                PortType portType = (PortType) portTypes.get(key);
                if (portType.isUndefined()) {
                    continue;
                }

                ServicePort port = ServicesFactory.eINSTANCE.createServicePort();
                String portName = portType.getQName().getLocalPart();
                port.setName(portName);
                // set port id
                Iterator portIterator = portNameIdMap.keySet().iterator();
                while (portIterator.hasNext()) {
                    String oldportName = (String) portIterator.next();
                    if (oldportName.equals(portName)) {
                        String id = (String) portNameIdMap.get(oldportName);
                        port.setId(id);

                        // restore additional infos
                        Map<String, String> storedAdditions = portAdditionalMap.get(id);
                        Iterator<String> keySet = storedAdditions.keySet().iterator();
                        while (keySet.hasNext()) {
                            String next = keySet.next();
                            String value = storedAdditions.get(next);
                            port.getAdditionalInfo().put(next, value);
                        }
                    }
                }
                if (port.getId() == null || port.getId().equals("")) {
                    port.setId(factory.getNextId());
                }
                List<Operation> list = portType.getOperations();
                for (Operation operation : list) {
                    if (operation.isUndefined()) {
                        // means the operation has been removed already ,why ?
                        continue;
                    }
                    ServiceOperation serviceOperation = ServicesFactory.eINSTANCE.createServiceOperation();
                    serviceOperation.setName(operation.getName());
                    Iterator operationIterator = operNameIdMap.keySet().iterator();
                    while (operationIterator.hasNext()) {
                        String oldOperationName = (String) operationIterator.next();
                        String operationId = (String) operNameIdMap.get(oldOperationName);
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
                    if (operationNames != null) {
                        for (String name : operationNames) {
                            if (!name.equals(operation.getName()) && name.startsWith(operation.getName())) {
                                serviceOperation.setLabel(name);
                                hasAssignedjob = true;
                                break;
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
        } catch (WSDLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doSaveAs() {
        super.doSaveAs();
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);

    }

    @Override
    public boolean isDirty() {
        return super.isDirty();
    }

    @Override
    public boolean isSaveAsAllowed() {
        return super.isSaveAsAllowed();
    }

    @Override
    public void setFocus() {
        super.setFocus();
    }

    public RepositoryNode getRepositoryNode() {
        return this.repositoryNode;
    }

    public void setRepositoryNode(RepositoryNode repositoryNode) {
        this.repositoryNode = repositoryNode;
    }

    public ServiceItem getServiceItem() {
        return this.serviceItem;
    }

    public void setServiceItem(ServiceItem serviceItem) {
        this.serviceItem = serviceItem;
    }

    public void addListener() {
        // ResourcesPlugin.getWorkspace().addResourceChangeListener(changeListener);
        ((ReferenceFileItem) this.serviceItem.getReferenceResources().get(0)).eAdapters().add(dirtyListener);
    }

    public void removeListener() {
        ((ReferenceFileItem) this.serviceItem.getReferenceResources().get(0)).eAdapters().remove(dirtyListener);
        // ResourcesPlugin.getWorkspace().removeResourceChangeListener(changeListener);
    }

    public void setReadOnly(boolean isReadOnly) {
        if (isReadOnly) {
            IAction addMessage = getActionRegistry().getAction("ASDAddMessageAction");
            getActionRegistry().removeAction(addMessage);

            IAction addPart = getActionRegistry().getAction("ASDAddPartAction");
            getActionRegistry().removeAction(addPart);

            IAction setNewMessage = getActionRegistry().getAction("ASDSetNewMessageAction");
            getActionRegistry().removeAction(setNewMessage);

            IAction setMessageInterface = getActionRegistry().getAction("ASDSetMessageInterfaceAction");
            getActionRegistry().removeAction(setMessageInterface);

            IAction setNewType = getActionRegistry().getAction("ASDSetNewTypeAction");
            getActionRegistry().removeAction(setNewType);

            IAction setExistingType = getActionRegistry().getAction("ASDSetExistingTypeAction");
            getActionRegistry().removeAction(setExistingType);

            IAction setNewElement = getActionRegistry().getAction("ASDSetNewElementAction");
            getActionRegistry().removeAction(setNewElement);

            IAction setExistingElement = getActionRegistry().getAction("ASDSetExistingElementAction");
            getActionRegistry().removeAction(setExistingElement);

            IAction directEdit = getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT);
            getActionRegistry().removeAction(directEdit);
            // --
            IAction addService = getActionRegistry().getAction("ASDAddServiceAction");
            getActionRegistry().removeAction(addService);

            IAction addBinding = getActionRegistry().getAction("ASDAddBindingAction");
            getActionRegistry().removeAction(addBinding);

            IAction addInterface = getActionRegistry().getAction("ASDAddInterfaceAction");
            getActionRegistry().removeAction(addInterface);

            IAction addEndPoint = getActionRegistry().getAction("ASDAddEndPointAction");
            getActionRegistry().removeAction(addEndPoint);

            IAction addOperation = getActionRegistry().getAction("ASDAddOperationAction");
            getActionRegistry().removeAction(addOperation);

            IAction addInput = getActionRegistry().getAction("ASDAddInputActionn");
            getActionRegistry().removeAction(addInput);

            IAction addOutput = getActionRegistry().getAction("ASDAddOutputActionn");
            getActionRegistry().removeAction(addOutput);

            IAction addFault = getActionRegistry().getAction("ASDAddFaultActionn");
            getActionRegistry().removeAction(addFault);

            IAction addDelete = getActionRegistry().getAction("ASDDeleteAction");
            getActionRegistry().removeAction(addDelete);

            IAction setNewBinding = getActionRegistry().getAction("ASDSetNewBindingAction");
            getActionRegistry().removeAction(setNewBinding);

            IAction setExistingBinding = getActionRegistry().getAction("ASDSetExistingBindingAction");
            getActionRegistry().removeAction(setExistingBinding);

            IAction setNewInterface = getActionRegistry().getAction("ASDSetNewInterfaceAction");
            getActionRegistry().removeAction(setNewInterface);

            IAction setExistingInterface = getActionRegistry().getAction("ASDSetExistingInterfaceAction");
            getActionRegistry().removeAction(setExistingInterface);

            IAction generateBinding = getActionRegistry().getAction("ASDGenerateBindingActionn");
            getActionRegistry().removeAction(generateBinding);

            IAction addImport = getActionRegistry().getAction("ASDAddImportAction");
            getActionRegistry().removeAction(addImport);

            IAction addParameter = getActionRegistry().getAction("ASDAddParameterAction");
            getActionRegistry().removeAction(addParameter);

            IAction addSchema = getActionRegistry().getAction("ASDAddSchemaAction");
            getActionRegistry().removeAction(addSchema);

            IAction openSchema = getActionRegistry().getAction("ASDOpenSchemaAction");
            getActionRegistry().removeAction(openSchema);

            IAction openImport = getActionRegistry().getAction("ASDOpenImportAction");
            getActionRegistry().removeAction(openImport);

            IAction openInNewEditor = getActionRegistry().getAction("org.eclipse.wst.wsdl.ui.OpenInNewEditor");
            getActionRegistry().removeAction(openInNewEditor);
        }
    }
}
