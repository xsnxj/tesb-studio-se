package org.talend.repository.services.action;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.ui.actions.metadata.AbstractCreateAction;
import org.talend.core.ui.images.OverlayImageProvider;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.utils.ESBImage;
import org.talend.repository.ui.dialog.RepositoryReviewDialog;

public class AssignJobAction extends AbstractCreateAction {

    private String createLabel = "Assign Job";

    private ERepositoryObjectType currentNodeType;

    private boolean creation = false;

    private Image folderImg = ImageProvider.getImage(ESBImage.SERVICE_ICON);

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

        this.setImageDescriptor(OverlayImageProvider.getImageWithNew(folderImg));
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
        this.setImageDescriptor(OverlayImageProvider.getImageWithNew(folderImg));
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
            String jobID = item.getProperty().getId();
            String jobName = item.getProperty().getLabel();
            ServiceItem serviceItem = (ServiceItem) repositoryNode.getParent().getObject().getProperty().getItem();
            List<ServiceOperation> listOperation = serviceItem.getServiceConnection().getServiceOperation();
            for (ServiceOperation operation : listOperation) {
                if (operation.getLabel().equals(repositoryNode.getLabel())) {
                    operation.setReferenceJobId(jobID);
                    operation.setLabel(operation.getOperationName() + "-" + jobName);
                }
            }
            IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
            try {
                factory.save(serviceItem);
            } catch (PersistenceException e) {
                e.printStackTrace();
            }
            RepositoryManager.refreshSavedNode(repositoryNode);
        }
    }

}
