package org.talend.repository.services.action;

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.designer.core.ui.action.EditProcess;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;

public class OpenJobAction extends EditProcess {

    private String createLabel = "Open Job";

    private ERepositoryObjectType currentNodeType;

    private RepositoryNode jobNode;

    public OpenJobAction() {
        super();

        this.setText(createLabel);
        this.setToolTipText(createLabel);
        currentNodeType = ERepositoryObjectType.SERVICESOPERATION;
    }

    public OpenJobAction(boolean isToolbar) {
        this();
        setToolbar(isToolbar);

        this.setText(createLabel);
        this.setToolTipText(createLabel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.ui.action.EditProcess#init(org.eclipse.jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(TreeViewer viewer, IStructuredSelection selection) {
        List<RepositoryNode> nodes = selection.toList();
        if (nodes == null || nodes.size() != 1) {
            setEnabled(false);
            return;
        }
        RepositoryNode node = nodes.iterator().next();
        ERepositoryObjectType nodeType = (ERepositoryObjectType) node.getProperties(EProperties.CONTENT_TYPE);
        if (!currentNodeType.equals(nodeType)) {
            setEnabled(false);
            return;
        }
        this.setText(createLabel);
        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.DEFAULT_IMAGE));
        String jobId = getReferenceJobId(node);
        if (jobId == null) {
            setEnabled(false);
            return;
        }
        jobNode = RepositoryNodeUtilities.getRepositoryNode(jobId, false);
        final IStructuredSelection jobSelection = new StructuredSelection(jobNode);
        setSpecialSelection(new ISelectionProvider() {

            public void setSelection(ISelection arg0) {
            }

            public void removeSelectionChangedListener(ISelectionChangedListener arg0) {
            }

            public ISelection getSelection() {
                return jobSelection;
            }

            public void addSelectionChangedListener(ISelectionChangedListener arg0) {
            }
        });
        super.init(viewer, jobSelection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.AContextualAction#getCurrentRepositoryNode()
     */
    @Override
    protected RepositoryNode getCurrentRepositoryNode() {
        return jobNode;
    }

    public Class getClassForDoubleClick() {
        try {
            RepositoryNode repositoryNode = super.getCurrentRepositoryNode();
            return (getReferenceJobId(repositoryNode) != null) ? ServiceOperation.class : Object.class;
        } catch (Exception e) {
            // do nothing just return default
        }
        return ServiceOperation.class;
    }

    protected static String getReferenceJobId(IRepositoryNode node) {
        String parentPortName = node.getParent().getLabel();
        ServiceItem serviceItem = (ServiceItem) node.getParent().getParent().getObject().getProperty().getItem();
        List<ServicePort> listPort = ((ServiceConnection) serviceItem.getConnection()).getServicePort();
        for (ServicePort port : listPort) {
            if (port.getPortName().equals(parentPortName)) {
                List<ServiceOperation> listOperation = port.getServiceOperation();
                for (ServiceOperation operation : listOperation) {
                    if (operation.getLabel().equals(node.getLabel())) {
                        return operation.getReferenceJobId();
                    }
                }
                break;
            }
        }
        return null;
    }

}
