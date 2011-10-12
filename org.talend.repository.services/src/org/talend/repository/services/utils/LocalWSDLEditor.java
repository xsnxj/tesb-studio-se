package org.talend.repository.services.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.wsdl.ui.internal.InternalWSDLMultiPageEditor;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.ResourceModelUtils;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.model.services.ServicesFactory;

public class LocalWSDLEditor extends InternalWSDLMultiPageEditor {

    private ServiceItem serviceItem;

    private RepositoryNode repositoryNode;

    private Map portStore = new HashMap();

    private Map operationStore = new HashMap();

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
                IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
                factory.save(serviceItem);
                RepositoryManager.refreshSavedNode(repositoryNode);
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
            EList<ServicePort> oldServicePorts = ((ServiceConnection) serviceItem.getConnection()).getServicePort();

            // get old service port item names and operation names under them
            HashMap<String, ArrayList<String>> oldPortItemNames = new HashMap<String, ArrayList<String>>();
            for (ServicePort servicePort : oldServicePorts) {
                EList<ServiceOperation> operations = servicePort.getServiceOperation();
                ArrayList<String> operationNames = new ArrayList<String>();
                for (ServiceOperation operation : operations) {
                    operationNames.add(operation.getLabel());
                }
                oldPortItemNames.put(servicePort.getName(), operationNames);
            }

            List<ServicePort> portList = ((ServiceConnection) serviceItem.getConnection()).getServicePort();
            for (int i = 0; i < portList.size(); i++) {
                ServicePort port = (ServicePort) portList.get(i);
                portStore.put(port.getPortName(), port.getId());
                List<ServiceOperation> operationList = port.getServiceOperation();
                for (int j = 0; j < operationList.size(); j++) {
                    ServiceOperation operation = (ServiceOperation) operationList.get(j);
                    operationStore.put(operation.getOperationName(), operation.getId());
                }
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
                port.setPortName(portName);
                // set port id
                Iterator portIterator = portStore.keySet().iterator();
                while (portIterator.hasNext()) {
                    String oldportName = (String) portIterator.next();
                    if (oldportName.equals(portName)) {
                        String id = (String) portStore.get(oldportName);
                        port.setId(id);
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
                    serviceOperation.setOperationName(operation.getName());
                    Iterator operationIterator = operationStore.keySet().iterator();
                    while (operationIterator.hasNext()) {
                        String oldOperationName = (String) operationIterator.next();
                        String operationId = (String) operationStore.get(oldOperationName);
                        if (oldOperationName.equals(operation.getName())) {
                            serviceOperation.setId(operationId);
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
        portStore.clear();
        operationStore.clear();
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
