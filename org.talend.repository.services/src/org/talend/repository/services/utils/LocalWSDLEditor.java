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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.wst.wsdl.ui.internal.InternalWSDLMultiPageEditor;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.IESBService;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.model.update.RepositoryUpdateManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.DesignerPlugin;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.services.Messages;
import org.talend.repository.services.action.AssignJobAction;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.model.services.ServicesFactory;

@SuppressWarnings("restriction")
public class LocalWSDLEditor extends InternalWSDLMultiPageEditor {

    private ServiceItem serviceItem;

    private RepositoryNode repositoryNode;

    @Override
    public void doSave(IProgressMonitor monitor) {
    	if(!isEditableCurrently()){
    		return;
    	}
        super.doSave(monitor);
        save();
    }
    
    private boolean isEditableCurrently(){
    	if (!DesignerPlugin.getDefault().getProxyRepositoryFactory().isEditableAndLockIfPossible(serviceItem)) {
    		MessageDialog.openWarning(getSite().getShell(),  Messages.WSDLFileIsReadOnly_Title,  Messages.WSDLFileIsReadOnly_Message);
            return false;
        }
    	return true;
    }

    private void save() {
    	if(!isEditableCurrently()){
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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveModel() throws CoreException {
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

        Definition definition = WSDLUtils.getDefinition(serviceItem);

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
        for (Object obj : definition.getAllPortTypes().values()) {
            PortType portType = (PortType) obj;
            if (portType.isUndefined()) {
                continue;
            }

            ServicePort port = ServicesFactory.eINSTANCE.createServicePort();
            String portName = portType.getQName().getLocalPart();
            port.setName(portName);
            // set port id
            Iterator<String> portIterator = portNameIdMap.keySet().iterator();
            while (portIterator.hasNext()) {
                String oldportName = portIterator.next();
                if (oldportName.equals(portName)) {
                    String id = portNameIdMap.get(oldportName);
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
                            if (name.equals(operation.getName() + "-" + repObj.getLabel())) {
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

    public void setReadOnly(boolean isReadOnly) {
        if (isReadOnly) {
        	setPartName(getPartName()+" (ReadOnly)");
            ActionRegistry actionRegistry = getActionRegistry();

            IAction ASDAddMessageAction = actionRegistry.getAction("ASDAddMessageAction");
            if (ASDAddMessageAction != null) {
                actionRegistry.removeAction(ASDAddMessageAction);
            }
            IAction ASDAddPartAction = actionRegistry.getAction("ASDAddPartAction");
            if (ASDAddPartAction != null) {
                actionRegistry.removeAction(ASDAddPartAction);
            }
            IAction ASDSetNewMessageAction = actionRegistry.getAction("ASDSetNewMessageAction");
            if (ASDSetNewMessageAction != null) {
                actionRegistry.removeAction(ASDSetNewMessageAction);
            }
            IAction ASDSetMessageInterfaceAction = actionRegistry.getAction("ASDSetMessageInterfaceAction");
            if (ASDSetMessageInterfaceAction != null) {
                actionRegistry.removeAction(ASDSetMessageInterfaceAction);
            }
            IAction ASDSetNewTypeAction = actionRegistry.getAction("ASDSetNewTypeAction");
            if (ASDSetNewTypeAction != null) {
                actionRegistry.removeAction(ASDSetNewTypeAction);
            }
            IAction ASDSetExistingTypeAction = actionRegistry.getAction("ASDSetExistingTypeAction");
            if (ASDSetExistingTypeAction != null) {
                actionRegistry.removeAction(ASDSetExistingTypeAction);
            }
            IAction ASDSetNewElementAction = actionRegistry.getAction("ASDSetNewElementAction");
            if (ASDSetNewElementAction != null) {
                actionRegistry.removeAction(ASDSetNewElementAction);
            }
            IAction ASDSetExistingElementAction = actionRegistry.getAction("ASDSetExistingElementAction");
            if (ASDSetExistingElementAction != null) {
                actionRegistry.removeAction(ASDSetExistingElementAction);
            }
            IAction directEditAction = actionRegistry.getAction(GEFActionConstants.DIRECT_EDIT);
            if (directEditAction != null) {
                actionRegistry.removeAction(directEditAction);
            }
            IAction ASDAddServiceAction = actionRegistry.getAction("ASDAddServiceAction");
            if (ASDAddServiceAction != null) {
                actionRegistry.removeAction(ASDAddServiceAction);
            }
            IAction ASDAddBindingAction = actionRegistry.getAction("ASDAddBindingAction");
            if (ASDAddBindingAction != null) {
                actionRegistry.removeAction(ASDAddBindingAction);
            }
            IAction ASDAddInterfaceAction = actionRegistry.getAction("ASDAddInterfaceAction");
            if (ASDAddInterfaceAction != null) {
                actionRegistry.removeAction(ASDAddInterfaceAction);
            }
            IAction ASDAddEndPointAction = actionRegistry.getAction("ASDAddEndPointAction");
            if (ASDAddEndPointAction != null) {
                actionRegistry.removeAction(ASDAddEndPointAction);
            }
            IAction ASDAddOperationAction = actionRegistry.getAction("ASDAddOperationAction");
            if (ASDAddOperationAction != null) {
                actionRegistry.removeAction(ASDAddOperationAction);
            }
            IAction ASDAddInputActionn = actionRegistry.getAction("ASDAddInputActionn");
            if (ASDAddInputActionn != null) {
                actionRegistry.removeAction(ASDAddInputActionn);
            }
            IAction ASDAddOutputActionn = actionRegistry.getAction("ASDAddOutputActionn");
            if (ASDAddInputActionn != null) {
                actionRegistry.removeAction(ASDAddOutputActionn);
            }
            IAction ASDAddFaultActionn = actionRegistry.getAction("ASDAddFaultActionn");
            if (ASDAddFaultActionn != null) {
                actionRegistry.removeAction(ASDAddFaultActionn);
            }
            IAction ASDDeleteAction = actionRegistry.getAction("ASDDeleteAction");
            if (ASDDeleteAction != null) {
                actionRegistry.removeAction(ASDDeleteAction);
            }
            IAction ASDSetNewBindingAction = actionRegistry.getAction("ASDSetNewBindingAction");
            if (ASDSetNewBindingAction != null) {
                actionRegistry.removeAction(ASDSetNewBindingAction);
            }
            IAction ASDSetExistingBindingAction = actionRegistry.getAction("ASDSetExistingBindingAction");
            if (ASDSetExistingBindingAction != null) {
                actionRegistry.removeAction(ASDSetExistingBindingAction);
            }
            IAction ASDSetNewInterfaceAction = actionRegistry.getAction("ASDSetNewInterfaceAction");
            if (ASDSetNewInterfaceAction != null) {
                actionRegistry.removeAction(ASDSetNewInterfaceAction);
            }
            IAction ASDSetExistingInterfaceAction = actionRegistry.getAction("ASDSetExistingInterfaceAction");
            if (ASDSetExistingInterfaceAction != null) {
                actionRegistry.removeAction(ASDSetExistingInterfaceAction);
            }
            IAction ASDGenerateBindingActionn = actionRegistry.getAction("ASDGenerateBindingActionn");
            if (ASDGenerateBindingActionn != null) {
                actionRegistry.removeAction(ASDGenerateBindingActionn);
            }
            IAction ASDAddImportAction = actionRegistry.getAction("ASDAddImportAction");
            if (ASDAddImportAction != null) {
                actionRegistry.removeAction(ASDAddImportAction);
            }
            IAction ASDAddParameterAction = actionRegistry.getAction("ASDAddParameterAction");
            if (ASDAddParameterAction != null) {
                actionRegistry.removeAction(ASDAddParameterAction);
            }
            IAction ASDAddSchemaAction = actionRegistry.getAction("ASDAddSchemaAction");
            if (ASDAddSchemaAction != null) {
                actionRegistry.removeAction(ASDAddSchemaAction);
            }
            IAction ASDOpenSchemaAction = actionRegistry.getAction("ASDOpenSchemaAction");
            if (ASDOpenSchemaAction != null) {
                actionRegistry.removeAction(ASDOpenSchemaAction);
            }
            IAction ASDOpenImportAction = actionRegistry.getAction("ASDOpenImportAction");
            if (ASDOpenImportAction != null) {
                actionRegistry.removeAction(ASDOpenImportAction);
            }
            IAction OpenInNewEditorAction = actionRegistry.getAction("org.eclipse.wst.wsdl.ui.OpenInNewEditor");
            if (OpenInNewEditorAction != null) {
                actionRegistry.removeAction(OpenInNewEditorAction);
            }
        }
    }
}
