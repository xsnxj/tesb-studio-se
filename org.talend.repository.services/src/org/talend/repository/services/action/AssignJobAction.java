package org.talend.repository.services.action;

import java.util.List;
import java.util.Map;

import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.IProcess2;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.ui.actions.metadata.AbstractCreateAction;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.designer.core.IDesignerCoreService;
import org.talend.designer.core.ui.editor.cmd.PropertyChangeCommand;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
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

        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.DEFAULT_IMAGE));

        currentNodeType = ERepositoryObjectType.SERVICESOPERATION;
    }

    public AssignJobAction(boolean isToolbar) {
        this();
        setToolbar(isToolbar);

        this.setText(createLabel);
        this.setToolTipText(createLabel);

        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.DEFAULT_IMAGE));
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
        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.DEFAULT_IMAGE));
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
        if (dialog.open() == RepositoryReviewDialog.OK) {
            IRepositoryViewObject repositoryObject = dialog.getResult().getObject();
            final Item item = repositoryObject.getProperty().getItem();

            try {
                String jobID = item.getProperty().getId();
                String jobName = item.getProperty().getLabel();
                String operationName = repositoryNode.getLabel();
                String parentPortName = repositoryNode.getParent().getLabel();
                ServiceItem serviceItem = (ServiceItem) repositoryNode.getParent().getParent().getObject().getProperty()
                        .getItem();

                String wsdlPath = serviceItem.getServiceConnection().getWSDLPath();
                Map<String, String> serviceParameters = WSDLUtils.getServiceParameters(wsdlPath);

                List<ServicePort> listPort = serviceItem.getServiceConnection().getServicePort();
                for (ServicePort port : listPort) {
                    if (port.getName().equals(parentPortName)) {
                        List<ServiceOperation> listOperation = port.getServiceOperation();
                        for (ServiceOperation operation : listOperation) {
                            if (operation.getOperationLabel().equals(operationName)) {
                                operation.setReferenceJobId(jobID);
                                operation.setOperationLabel(operation.getOperationName() + "-" + jobName);

                                serviceParameters.put(WSDLUtils.PORT_NAME, parentPortName);
                                serviceParameters.put(WSDLUtils.OPERATION_NAME, operationName);

                                break;
                            }
                        }
                        break;
                    }
                }

                IDesignerCoreService designerCoreService = CoreRuntimePlugin.getInstance().getDesignerCoreService();
                if (designerCoreService != null) {
                    IProcess process = designerCoreService.getProcessFromProcessItem((ProcessItem) item);
                    List<? extends INode> providerRequestNodes = process
                            .getNodesOfType(CreateNewJobAction.T_ESB_PROVIDER_REQUEST);
                    PropertyChangeCommand pcc = null;
                    if (null != providerRequestNodes && !providerRequestNodes.isEmpty()) {

                        // INode providerRequestNode = providerRequestNodes.get(0);
                        // for (Map.Entry<String, String> property : serviceParameters.entrySet()) {
                        // pcc = new PropertyChangeCommand(
                        // providerRequestNode, property.getKey(), property.getValue());
                        // pcc.execute();
                        // // ((IProcess2) process).getCommandStack().execute(pcc);
                        // }

                        CreateNewJobAction.setProviderRequestComponentConfiguration(providerRequestNodes.get(0),
                                serviceParameters);
                    }
                    ((IProcess2) process).saveXmlFile();
                }

                IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
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

}
