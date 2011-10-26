// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.CorePlugin;
import org.talend.core.IESBService;
import org.talend.core.model.general.Project;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.IProcess2;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.designer.core.IDesignerCoreService;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.components.EmfComponent;
import org.talend.designer.core.ui.AbstractMultiPageTalendEditor;
import org.talend.designer.core.ui.editor.ProcessEditorInput;
import org.talend.designer.core.ui.editor.cmd.ChangeValuesFromRepository;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.repository.ProjectManager;
import org.talend.repository.RepositoryPlugin;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.ProjectRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.action.CreateNewJobAction;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.services.utils.OperationRepositoryObject;
import org.talend.repository.services.utils.PortRepositoryObject;
import org.talend.repository.services.utils.WSDLUtils;

/**
 * DOC nrousseau class global comment. Detailled comment
 */
public class ESBService implements IESBService {

    // public AbstractMetadataObject getServicesOperation(Connection connection, String operationName) {
    // List<ServiceOperation> list = new ArrayList<ServiceOperation>();
    // if (connection instanceof ServiceConnection) {
    // ServiceConnection serConnection = (ServiceConnection) connection;
    // EList<ServicePort> serPort = serConnection.getServicePort();
    // for (ServicePort port : serPort) {
    // list.addAll(port.getServiceOperation());
    // }
    // }
    // for (ServiceOperation ope : list) {
    // if (ope.getLabel().equals(operationName)) {
    // return ope;
    // }
    // }
    // return null;
    // }

    // public void changeOperationLabel(RepositoryNode newNode, INode node, Connection connection) {
    // if (!(connection instanceof ServiceConnection)) {
    // return;
    // }
    // ServiceConnection serConn = (ServiceConnection) connection;
    // changeOldOperationLabel(serConn, node);
    // changenewOperationLabel(newNode, node, serConn);
    // }

    private void changeOldOperationLabel(RepositoryNode topParent, INode node, ServiceOperation newOperation) {
        // here should be all the ports, not just ports of one connection
        List<IRepositoryNode> nodeList = topParent.getChildren();
        IElementParameter elePara = node.getElementParameter("PROPERTY:" + EParameterName.REPOSITORY_PROPERTY_TYPE.getName());
        if (elePara == null) {
            return;
        }
        ServiceConnection serConn = null;
        ServiceItem servicesItem = null;
        String paraValue = (String) elePara.getValue();
        if (paraValue == null || "".equals(paraValue)) {
            return;
        }
        String connID = null;
        if (paraValue.contains(" - ")) {
            connID = paraValue.split(" - ")[0];
        } else {
            connID = paraValue;
        }
        for (IRepositoryNode repNode : nodeList) {
            String id = repNode.getObject().getProperty().getId();
            if (id.equals(connID)) {
                servicesItem = (ServiceItem) repNode.getObject().getProperty().getItem();
                serConn = (ServiceConnection) servicesItem.getConnection();
                break;
            }
        }
        if (serConn == null) {
            return;
        }
        EList<ServicePort> portList = serConn.getServicePort();

        IElementParameter portPara = node.getElementParameter(WSDLUtils.PORT_NAME);
        IElementParameter opePara = node.getElementParameter(WSDLUtils.OPERATION_NAME);
        if (portPara != null && opePara != null) {
            String portValue = (String) portPara.getValue();
            String opeValue = (String) opePara.getValue();
            if (portValue != null && !"".equals(portValue) && opeValue != null && !"".equals(opeValue)) {
                out: for (ServicePort port : portList) {
                    if (port.getName().equals(portValue)) {
                        for (ServiceOperation ope : port.getServiceOperation()) {
                            if (ope.getName().equals(opeValue)) {
                                if (newOperation != null && !ope.getId().equals(newOperation.getId())) {
                                    ope.setLabel(opeValue);
                                    ope.setReferenceJobId(null);
                                    if (servicesItem != null) {
                                        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
                                        try {
                                            factory.save(servicesItem);
                                        } catch (PersistenceException e) {
                                            ExceptionHandler.process(e);
                                        }
                                    }
                                    break out;
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    public String getWsdlFilePath(Item item) {
        if (item != null && item instanceof ServiceItem) {
            ServiceItem si = (ServiceItem) item;
            ServiceConnection connection = (ServiceConnection) si.getConnection();
            return connection.getWSDLPath();
        }
        return null;
    }

    // private void changenewOperationLabel(RepositoryNode newNode, INode node, ServiceConnection serConn) {
    // String operationName = newNode.getObject().getLabel();
    // String parentPortName = newNode.getParent().getObject().getLabel();
    //
    // String wsdlPath = serConn.getWSDLPath();
    // try {
    // Map<String, String> serviceParameters = WSDLUtils.getServiceParameters(wsdlPath);
    // IRepositoryViewObject newObj = newNode.getObject();
    // if (newObj instanceof OperationRepositoryObject) {
    // ServiceOperation newOpe = (ServiceOperation) ((OperationRepositoryObject) newObj).getAbstractMetadataObject();
    //
    // IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
    //
    // if (newOpe.getReferenceJobId() != null) {
    // changeOtherJobSchemaValue(factory, newOpe, serConn);
    // MessageDialog.openWarning(new Shell(), "warning",
    // "This other job which based on the Operation will be unset!");
    // }
    //
    // IEditorPart activeEditor =
    // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    // IEditorInput input = activeEditor.getEditorInput();
    // if (input instanceof ProcessEditorInput) {
    // Item jobItem = ((ProcessEditorInput) input).getItem();
    // String jobID = jobItem.getProperty().getId();
    // String jobName = jobItem.getProperty().getLabel();
    //
    // newOpe.setReferenceJobId(jobID);
    // newOpe.setLabel(newOpe.getName() + "-" + jobName);
    //
    // serviceParameters.put(WSDLUtils.PORT_NAME, parentPortName);
    // serviceParameters.put(WSDLUtils.OPERATION_NAME, operationName);
    //
    // CreateNewJobAction.setProviderRequestComponentConfiguration(node, serviceParameters);
    //
    // try {
    // factory.save(jobItem);
    // } catch (PersistenceException e) {
    // e.printStackTrace();
    // }
    // try {
    // factory.save(newNode.getParent().getParent().getObject().getProperty().getItem());
    // } catch (PersistenceException e) {
    // e.printStackTrace();
    // }
    // RepositoryManager.refreshSavedNode(newNode);
    // }
    // }
    //
    // } catch (CoreException e1) {
    // ExceptionHandler.process(e1);
    // } catch (PersistenceException e) {
    // ExceptionHandler.process(e);
    // }
    // }

    private void changeOtherJobSchemaValue(IProxyRepositoryFactory factory, ServiceOperation newOpe, ServiceConnection serConn,
            RepositoryNode selectNode) throws PersistenceException, CoreException {
        IRepositoryViewObject jobObj = factory.getLastVersion(newOpe.getReferenceJobId());
        if (jobObj == null) {
            return;
        }
        ProcessItem processItem = (ProcessItem) jobObj.getProperty().getItem();

        IDesignerCoreService service = CorePlugin.getDefault().getDesignerCoreService();
        boolean foundInOpen = false;

        IProcess2 process = null;
        IEditorReference[] reference = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
        List<IProcess2> processes = RepositoryPlugin.getDefault().getDesignerCoreService().getOpenedProcess(reference);
        for (IProcess2 processOpen : processes) {
            if (processOpen.getProperty().getItem() == processItem) {
                foundInOpen = true;
                process = processOpen;
                break;
            }
        }
        if (!foundInOpen) {
            IProcess proc = service.getProcessFromProcessItem(processItem);
            if (proc instanceof IProcess2) {
                process = (IProcess2) proc;
            }
        }

        newOpe.setReferenceJobId(null);
        newOpe.setLabel(newOpe.getName());

        if (process != null) {
            List<? extends INode> nodelist = process.getGraphicalNodes();
            for (INode node : nodelist) {
                if (node.getComponent().getName().equals("tESBProviderRequest")) {
                    repositoryChange(selectNode, node);
                }
            }
            try {
                processItem.setProcess(process.saveXmlFile());
            } catch (IOException e1) {
                ExceptionHandler.process(e1);
            }

            try {
                factory.save(processItem);
            } catch (PersistenceException e) {
                ExceptionHandler.process(e);
            }
        }

        // ProcessType process = item.getProcess();
        // EList<NodeType> nodeList = process.getNode();
        //
        // for (NodeType node : nodeList) {
        // EList parameters = node.getElementParameter();
        // for (Object paramObj : parameters) {
        // ElementParameterType param = (ElementParameterType) paramObj;
        // String name = param.getName();
        // if (name.equals(WSDLUtils.OPERATION_NAME)) {
        // if (!newOpe.getName().equals(param.getValue())) {
        // break;
        // }
        // param.setValue(null);
        // }
        // if (name.equals("SCHEMA:SCHEMA_TYPE")) {
        // param.setValue("BUILT_IN");
        // break;
        // }
        //
        // }
        //
        // }
        // factory.save(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.IESBService#getServicesType()
     */
    public ERepositoryObjectType getServicesType() {
        return ESBRepositoryNodeType.SERVICES;
    }

    public String getServiceLabel(Item item, String linkedRepository) {
        String serviceName = item.getProperty().getLabel();
        if (item instanceof ServiceItem) {
            String[] ids = linkedRepository.split(" - ");
            ServiceConnection serConn = (ServiceConnection) ((ServiceItem) item).getConnection();
            if (ids.length == 3) {
                String portName = "";
                String operationName = "";
                EList<ServicePort> portList = serConn.getServicePort();
                out: for (ServicePort port : portList) {
                    if (port.getId().equals(ids[1])) {
                        portName = port.getName();
                        EList<ServiceOperation> opeList = port.getServiceOperation();
                        for (ServiceOperation ope : opeList) {
                            if (ope.getId().equals(ids[2])) {
                                operationName = ope.getName();
                                return serviceName + " - " + portName + " - " + operationName;
                            }
                        }
                    }
                }
            }

        }
        return serviceName;
    }

    public void updateOperation(INode node, String linkedRepository, RepositoryNode selectNode) {
        String[] ids = linkedRepository.split(" - ");
        if (ids.length == 3) {
            try {
                IRepositoryViewObject reViewObject = ProxyRepositoryFactory.getInstance().getLastVersion(ids[0]);
                ServiceItem servicesItem = (ServiceItem) reViewObject.getProperty().getItem();
                ServiceConnection serConn = (ServiceConnection) servicesItem.getConnection();

                String portName = "";
                EList<ServicePort> portList = serConn.getServicePort();
                ServiceOperation operation = null;
                out: for (ServicePort port : portList) {
                    if (port.getId().equals(ids[1])) {
                        portName = port.getName();
                        // node.getElementParameter("PORT")
                        EList<ServiceOperation> opeList = port.getServiceOperation();
                        for (ServiceOperation ope : opeList) {
                            if (ope.getId().equals(ids[2])) {
                                operation = ope;
                                break out;
                            }
                        }
                    }
                }

                RepositoryNode topParent = getServicesTopNode(selectNode);
                changeOldOperationLabel(topParent, node, operation);

                IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

                if (operation.getReferenceJobId() != null) {
                    changeOtherJobSchemaValue(factory, operation, serConn, selectNode);
                    MessageDialog.openWarning(new Shell(), "warning",
                            "This other job which based on the Operation will be unset!");
                }

                IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                IEditorInput input = activeEditor.getEditorInput();
                if (input instanceof ProcessEditorInput) {
                    Item jobItem = ((ProcessEditorInput) input).getItem();
                    String jobID = jobItem.getProperty().getId();
                    String jobName = jobItem.getProperty().getLabel();

                    operation.setReferenceJobId(jobID);
                    operation.setLabel(operation.getName() + "-" + jobName);

                    String wsdlPath = WSDLUtils.getWsdlFile(selectNode).getLocation().toPortableString();
                    Map<String, String> serviceParameters = WSDLUtils.getServiceOperationParameters(
                            wsdlPath, operation.getName(), portName);
                    CreateNewJobAction.setProviderRequestComponentConfiguration(node, serviceParameters);

                    try {
                        factory.save(jobItem);
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }
                    try {
                        factory.save(servicesItem);
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }
                    RepositoryManager.refreshSavedNode(selectNode);
                }
            } catch (PersistenceException e) {
                ExceptionHandler.process(e);
            } catch (CoreException e) {
                ExceptionHandler.process(e);
            }

        }
    }

    private void repositoryChange(RepositoryNode repNode, INode node) {
        IElementParameter param = node.getElementParameterFromField(EParameterFieldType.PROPERTY_TYPE);
        ConnectionItem connectionItem = (ConnectionItem) repNode.getObject().getProperty().getItem();
        if (param != null) {
            param.getChildParameters().get(EParameterName.PROPERTY_TYPE.getName()).setValue(EmfComponent.REPOSITORY);
            param.getChildParameters().get(EParameterName.REPOSITORY_PROPERTY_TYPE.getName()).setValue("");
            String serviceId = connectionItem.getProperty().getId();
            String portId = ((PortRepositoryObject) repNode.getParent().getObject()).getId();
            String operationId = ((OperationRepositoryObject) repNode.getObject()).getId();
            ChangeValuesFromRepository command2 = new ChangeValuesFromRepository(node, null, param.getName()
                    + ":" + EParameterName.PROPERTY_TYPE.getName(), "BUILT_IN"); //$NON-NLS-1$
            command2.execute();
        }
    }

    public Object getValue(Item connItem, String value, INode node) {
        if (connItem instanceof ServiceItem) {
            ServiceItem serviceItem = ((ServiceItem) connItem);
            if (WSDLUtils.WSDL_LOCATION.equals(value)) {
                return WSDLUtils.getWsdlFile(serviceItem).getLocation().toPortableString();
            } else if ("ENDPOINT".equals(value)) {
                String wsdlURI = WSDLUtils.getWsdlFile(serviceItem).getLocation().toPortableString();
                try {
                    return WSDLUtils.getServiceParameters(wsdlURI).get(WSDLUtils.ENDPOINT_URI);
                } catch (CoreException e) {
                    ExceptionHandler.process(e);
                }
            } else if (WSDLUtils.OPERATION_NAME.equals(value)) {
                String propertyValue = (String) node.getPropertyValue(EParameterName.REPOSITORY_PROPERTY_TYPE.getName());
                if (propertyValue != null) {
                    if (propertyValue.contains(" - ")) {
                        String portID = propertyValue.split(" - ")[1];
                        String operarionID = propertyValue.split(" - ")[2];
                        for (ServicePort port : ((ServiceConnection) serviceItem.getConnection()).getServicePort()) {
                            if (port.getId().equals(portID)) {
                                for (ServiceOperation operation : port.getServiceOperation()) {
                                    if (operation.getId().equals(operarionID)) {
                                        return operation.getName();
                                    }
                                }

                            }
                        }
                    }
                }
            } else if (WSDLUtils.OPERATION_NS.equals(value)) {
                String wsdlURI = WSDLUtils.getWsdlFile(serviceItem).getLocation().toPortableString();
                try {
                    return WSDLUtils.getServiceParameters(wsdlURI).get(WSDLUtils.OPERATION_NS);
                } catch (CoreException e) {
                    ExceptionHandler.process(e);
                }
            } else if (WSDLUtils.PORT_NAME.equals(value)) {
                String propertyValue = (String) node.getPropertyValue(EParameterName.REPOSITORY_PROPERTY_TYPE.getName());
                if (propertyValue != null) {
                    if (propertyValue.contains(" - ")) {
                        String portID = propertyValue.split(" - ")[1];
                        for (ServicePort port : ((ServiceConnection) serviceItem.getConnection()).getServicePort()) {
                            if (port.getId().equals(portID)) {
                                return port.getName();
                            }
                        }
                    }
                }
            } else if (WSDLUtils.PORT_NS.equals(value)) {
                String wsdlURI = WSDLUtils.getWsdlFile(serviceItem).getLocation().toPortableString();
                try {
                    return WSDLUtils.getServiceParameters(wsdlURI).get(WSDLUtils.PORT_NS);
                } catch (CoreException e) {
                    ExceptionHandler.process(e);
                }
            } else if (WSDLUtils.SERVICE_NAME.equals(value)) {
                String wsdlURI = WSDLUtils.getWsdlFile(serviceItem).getLocation().toPortableString();
                try {
                    return WSDLUtils.getServiceParameters(wsdlURI).get(WSDLUtils.SERVICE_NAME);
                } catch (CoreException e) {
                    ExceptionHandler.process(e);
                }
            } else if (WSDLUtils.SERVICE_NS.equals(value)) {
                String wsdlURI = WSDLUtils.getWsdlFile(serviceItem).getLocation().toPortableString();
                try {
                    return WSDLUtils.getServiceParameters(wsdlURI).get(WSDLUtils.SERVICE_NS);
                } catch (CoreException e) {
                    ExceptionHandler.process(e);
                }
            }

        }
        return null;
    }

    private RepositoryNode getServicesTopNode(RepositoryNode node) {
        RepositoryNode parent = node.getParent();
        if (parent.getParent() instanceof ProjectRepositoryNode) {
            return parent;
        }
        parent = getServicesTopNode(parent);
        return parent;
    }

    /**
     * When services connection is renamed, refresh the connection label in the component view of job.
     *
     * @param item
     */
    public void refreshComponentView(Item item) {
        try {
            IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
            IEditorReference[] editors = activePage.getEditorReferences();
            for (IEditorReference er : editors) {
                IEditorPart part = er.getEditor(false);
                if (part instanceof AbstractMultiPageTalendEditor) {
                    AbstractMultiPageTalendEditor editor = (AbstractMultiPageTalendEditor) part;
                    CommandStack stack = (CommandStack) editor.getTalendEditor().getAdapter(CommandStack.class);
                    if (stack != null) {
                        IProcess process = editor.getProcess();
                        for (final INode processNode : process.getGraphicalNodes()) {
                            if (processNode instanceof Node) {
                                checkRepository((Node) processNode, item, stack);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
    }

    private void checkRepository(final Node node, Item item, CommandStack stack) {
        final String updataComponentParamName = EParameterName.UPDATE_COMPONENTS.getName();
        final List<IElementParameter> repositoryParam = new ArrayList<IElementParameter>();

        for (IElementParameter param : node.getElementParameters()) {
            if (param.getFieldType().equals(EParameterFieldType.SCHEMA_TYPE)) {
                String value = (String) param.getChildParameters().get(EParameterName.SCHEMA_TYPE.getName()).getValue();

                if (value.equals(EmfComponent.REPOSITORY)) {
                    IElementParameter schema = param.getChildParameters().get(EParameterName.REPOSITORY_SCHEMA_TYPE.getName());
                    if (schema != null && schema.getValue() != null) {
                        String[] names = ((String) schema.getValue()).split(" - "); //$NON-NLS-1$
                        if (names.length > 0) {
                            if (names[0].equals(item.getProperty().getId())) {
                                repositoryParam.add(schema);
                            }
                        }
                    }
                }

            } else if (param.getFieldType().equals(EParameterFieldType.PROPERTY_TYPE)) {
                Object value = param.getChildParameters().get(EParameterName.PROPERTY_TYPE.getName()).getValue();
                if (value.equals(EmfComponent.REPOSITORY)) {
                    IElementParameter property = param.getChildParameters()
                            .get(EParameterName.REPOSITORY_PROPERTY_TYPE.getName());
                    if (property != null && property.getValue() != null) {
                        String proValue = (String) property.getValue();
                        if (proValue != null && proValue.contains(" - ")) {
                            proValue = proValue.split(" - ")[0];
                        }
                        if (proValue.equals(item.getProperty().getId())) {
                            repositoryParam.add(property);
                        }

                    }
                }
            }
        }

        if (repositoryParam.isEmpty()) {
            return;
        }

        stack.execute(new Command() {

            @Override
            public void execute() {

                node.setPropertyValue(updataComponentParamName, new Boolean(true));
                for (IElementParameter param : repositoryParam) {
                    // force to reload label
                    param.setListItemsDisplayName(new String[0]);
                    param.setListItemsValue(new String[0]);
                }
            }

        });
    }

    public void refreshOperationLabel(String jobID) {
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        try {
            List<IRepositoryViewObject> repList = factory.getAll(getServicesType());
            for (IRepositoryViewObject obj : repList) {
                ServiceItem item = (ServiceItem) obj.getProperty().getItem();
                ServiceConnection conn = (ServiceConnection) item.getConnection();
                for (ServicePort port : conn.getServicePort()) {
                    for (ServiceOperation operation : port.getServiceOperation()) {
                        if (operation.getReferenceJobId() != null && operation.getReferenceJobId().endsWith(jobID)) {
                            operation.setLabel(operation.getName());
                            operation.setReferenceJobId(null);
                            factory.save(item);
                            return;
                        }
                    }
                }
            }
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
    }

    public void editJobName(String originaleObjectLabel, String newLabel) {
        IProxyRepositoryFactory proxyRepositoryFactory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        Project project = ProjectManager.getInstance().getCurrentProject();
        List<IRepositoryViewObject> service = null;
        try {
            service = proxyRepositoryFactory.getAll(project, ESBRepositoryNodeType.SERVICES, true);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        if (service != null && service.size() > 0) {
            for (IRepositoryViewObject Object : service) {
                boolean flag = false;
                ServiceItem item = (ServiceItem) Object.getProperty().getItem();
                ServiceConnection serviceConnection = (ServiceConnection) item.getConnection();
                List<ServicePort> servicePorts = serviceConnection.getServicePort();
                for (ServicePort port : servicePorts) {
                    List<ServiceOperation> serviceOperations = port.getServiceOperation();
                    for (ServiceOperation operation : serviceOperations) {
                        String originaleItemLabel = operation.getLabel();
                        if (originaleItemLabel.contains("-")) {
                            String[] array = originaleItemLabel.split("-");
                            if (originaleObjectLabel.equals(array[1])) {
                                operation.setLabel(array[0] + "-" + newLabel);
                                flag = true;
                            }
                        }
                    }
                }
                if (flag) {
                    try {
                        proxyRepositoryFactory.save(item);
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        RepositoryManager.refresh(ESBRepositoryNodeType.SERVICES);
    }
}
