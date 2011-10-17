package org.talend.repository.services.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.ui.actions.metadata.AbstractCreateAction;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.ProjectRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.utils.WSDLUtils;
import org.talend.repository.ui.dialog.RepositoryReviewDialog;

public class AssignJobAction extends AbstractCreateAction {

    private String createLabel = "Assign Job";

    private ERepositoryObjectType currentNodeType;

    public AssignJobAction() {
        super();

        this.setText(createLabel);
        this.setToolTipText(createLabel);

        this.setImageDescriptor(ImageProvider.getImageDesc(ECoreImage.PROCESS_ICON));

        currentNodeType = ERepositoryObjectType.SERVICESOPERATION;
    }

    public AssignJobAction(boolean isToolbar) {
        this();
        setToolbar(isToolbar);

        this.setText(createLabel);
        this.setToolTipText(createLabel);

        this.setImageDescriptor(ImageProvider.getImageDesc(ECoreImage.PROCESS_ICON));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.core.repository.ui.actions.metadata.AbstractCreateAction#init(org.talend.repository.model.RepositoryNode
     * )
     */
    @Override
    protected void init(RepositoryNode node) {
        ERepositoryObjectType nodeType = (ERepositoryObjectType) node.getProperties(EProperties.CONTENT_TYPE);
        if (!currentNodeType.equals(nodeType)) {
            return;
        }
        this.setText(createLabel);
        this.setImageDescriptor(ImageProvider.getImageDesc(ECoreImage.PROCESS_ICON));
        setEnabled(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.AContextualAction#doRun()
     */
    @Override
    protected void doRun() {
        if (repositoryNode == null) {
            repositoryNode = getCurrentRepositoryNode();
        }
        if (isToolbar()) {
            if (repositoryNode != null && repositoryNode.getContentType() != currentNodeType) {
                repositoryNode = null;
            }
            if (repositoryNode == null) {
                repositoryNode = getRepositoryNodeForDefault(currentNodeType);
            }
        }
        RepositoryReviewDialog dialog = new RepositoryReviewDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getShell(), ERepositoryObjectType.PROCESS, "");
        List<String> jobIDList = getAllReferenceJobId(repositoryNode);
        dialog.setJobIDList(jobIDList);
        if (dialog.open() == RepositoryReviewDialog.OK) {
            IRepositoryViewObject repositoryObject = dialog.getResult().getObject();
            final Item item = repositoryObject.getProperty().getItem();

            try {
                String jobID = item.getProperty().getId();
                String jobName = item.getProperty().getLabel();
                String operationName = repositoryNode.getObject().getLabel();
                String parentPortName = repositoryNode.getParent().getObject().getLabel();
                ServiceItem serviceItem = (ServiceItem) repositoryNode.getParent().getParent().getObject().getProperty()
                        .getItem();

                String wsdlPath = ((ServiceConnection) serviceItem.getConnection()).getWSDLPath();
                Map<String, String> serviceParameters = WSDLUtils.getServiceParameters(wsdlPath);
                serviceParameters.put(WSDLUtils.ENDPOINT_URI, '"' + serviceParameters.get(WSDLUtils.ENDPOINT_URI) + '"');

                List<ServicePort> listPort = ((ServiceConnection) serviceItem.getConnection()).getServicePort();
                for (ServicePort port : listPort) {
                    if (port.getName().equals(parentPortName)) {
                        List<ServiceOperation> listOperation = port.getServiceOperation();
                        for (ServiceOperation operation : listOperation) {
                            if (operation.getLabel().equals(operationName)) {
                                operation.setReferenceJobId(jobID);
                                operation.setLabel(operation.getName() + "-" + jobName);

                                serviceParameters.put(WSDLUtils.PORT_NAME, parentPortName);
                                serviceParameters.put(WSDLUtils.OPERATION_NAME, operationName);

                                break;
                            }
                        }
                        break;
                    }
                }

                ProcessItem processItem = (ProcessItem) item;
                ProcessType processType = processItem.getProcess();
                EList nodeList = processType.getNode();
                for (Object obj : nodeList) {
                    NodeType node = (NodeType) obj;
                    if (CreateNewJobAction.T_ESB_PROVIDER_REQUEST.equals(node.getComponentName())) {

                        EList parameters = node.getElementParameter();
                        for (Object paramObj : parameters) {
                            ElementParameterType param = (ElementParameterType) paramObj;
                            String name = param.getName();
                            if (serviceParameters.containsKey(name)) {
                                param.setValue(serviceParameters.get(name));
                            }
                        }
                        break;
                    }
                }

                IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

                try {
                    factory.save(processItem);
                } catch (PersistenceException e) {
                    e.printStackTrace();
                }

                try {
                    factory.save(serviceItem);
                } catch (PersistenceException e) {
                    e.printStackTrace();
                }
                RepositoryManager.refreshSavedNode(repositoryNode);
            } catch (Exception e) {
                ExceptionHandler.process(e);
            }
        }
    }

    public Class getClassForDoubleClick() {
        try {
            RepositoryNode repositoryNode = getCurrentRepositoryNode();
            return (OpenJobAction.getReferenceJobId(repositoryNode) == null) ? ServiceOperation.class : Object.class;
        } catch (Exception e) {
            // do nothing just return default
        }
        return ServiceOperation.class;
    }

    private RepositoryNode getTopParent(RepositoryNode repositoryNode) {
        repositoryNode = repositoryNode.getParent();
        if (repositoryNode.getParent() instanceof ProjectRepositoryNode) {
            return repositoryNode;
        }
        return getTopParent(repositoryNode);
    }

    private List<String> getAllReferenceJobId(RepositoryNode repositoryNode) {
        repositoryNode = getTopParent(repositoryNode);
        List<IRepositoryNode> nodeList = repositoryNode.getChildren();
        List<ServiceOperation> operaList = new ArrayList<ServiceOperation>();
        List<String> jobIDList = new ArrayList<String>();
        for (IRepositoryNode node : nodeList) {
            if (node.getObject().getProperty().getItem() instanceof ServiceItem) {
                ServiceItem item = (ServiceItem) node.getObject().getProperty().getItem();
                ServiceConnection conn = (ServiceConnection) item.getConnection();
                EList<ServicePort> portList = conn.getServicePort();
                for (ServicePort port : portList) {
                    operaList.addAll(port.getServiceOperation());
                }
            }
        }
        for (ServiceOperation operation : operaList) {
            String jobID = operation.getReferenceJobId();
            if (jobID != null) {
                jobIDList.add(jobID);
            }
        }
        return jobIDList;
    }

}
