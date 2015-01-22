package org.talend.camel.designer.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.talend.camel.designer.i18n.Messages;
import org.talend.camel.designer.util.CamelDesignerUtil;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.PluginChecker;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.ui.IJobletProviderService;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.ProjectRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.dialog.RepositoryReviewDialog;
import org.talend.repository.ui.utils.RecombineRepositoryNodeUtil;

public class AssignJobPage extends WizardPage {

    private AssignJobReviewDialog dialog;

    private String id;

    public String getId() {
        return id;
    }

    protected AssignJobPage(String pageName) {
        super(pageName);
    }

    public void createContents(Composite parent) {
    }

    public void createControl(Composite parent) {
        setTitle(Messages.getString("AssignJobPage_title"));//$NON-NLS-1$
        setDescription(Messages.getString("AssignJobPage_message"));//$NON-NLS-1$

        dialog = new AssignJobReviewDialog((AssignJobWizardDialog) getContainer(), parent.getShell(),
                ERepositoryObjectType.PROCESS, "", new ViewerFilter[] { new RouteInputContainedFilter() });
        setControl(dialog.createDialogArea(parent));
    }

    @Override
    public IWizardPage getNextPage() {
        return null;
    }

    public boolean finish() {
        dialog.okPressed();
        if (dialog.getResult() != null) {
            IRepositoryViewObject repositoryObject = dialog.getResult().getObject();
            final Item item = repositoryObject.getProperty().getItem();
            id = item.getProperty().getId();
            return true;
        }
        return false;
    }

    @Override
    public boolean isPageComplete() {
        if (dialog != null) {
            RepositoryNode result = dialog.getResult();
            if (result == null) {
                return false;
            }
            return result.getType() == ENodeType.REPOSITORY_ELEMENT;
        }
        return false;
    }

    private class RouteInputContainedFilter extends ViewerFilter {

        private List<IRepositoryNode> routeInputContainedJobs = new ArrayList<IRepositoryNode>();

        private IJobletProviderService service = null;

        private RouteInputContainedFilter() {

            if (PluginChecker.isJobLetPluginLoaded()) {
                service = (IJobletProviderService) GlobalServiceRegister.getDefault().getService(IJobletProviderService.class);
            }

            /*
             * find all RouteInput contained Jobs first
             */
            IRepositoryNode jobRoot = RecombineRepositoryNodeUtil.getFixingTypesInputRoot(ProjectRepositoryNode.getInstance(),
                    Arrays.asList(ERepositoryObjectType.PROCESS));
            addAllRouteInputContainedJob(routeInputContainedJobs, jobRoot);
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (!(element instanceof IRepositoryNode)) {
                return false;
            }
            IRepositoryNode node = (IRepositoryNode) element;
            ENodeType type = node.getType();
            if (type == ENodeType.SYSTEM_FOLDER) {
                return true;
            }
            /*
             * if it's an element and contains a tRouteInput then selected
             */
            if (type == ENodeType.REPOSITORY_ELEMENT) {
                for (IRepositoryNode rn : routeInputContainedJobs) {
                    if (rn.equals(node)) {
                        return true;
                    }
                }
                return false;
            }
            /*
             * if it's a container node, and some child of it contains a tRouteInput then selected
             */
            else {
                for (IRepositoryNode rn : routeInputContainedJobs) {
                    if (isAncestor(rn, node)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean isAncestor(IRepositoryNode jobNode, IRepositoryNode ancestor) {
            if (jobNode == null || ancestor == null) {
                return false;
            }
            IRepositoryNode current = jobNode;
            while (current != ancestor) {
                if (current == null) {
                    return false;
                }
                /*
                 * Compare via equals for jobs in sub folders because of different instances
                 * of RepositoryNode representing same object. (Maybe because of proxy)
                 */
                if (current.equals(ancestor)) {
            		return true;
                }
                current = current.getParent();
            }
            return true;
        }

        /**
         * find all Jobs which contains a tRouteInput component
         * 
         * @param routeInputContainedJobs
         * @param jobNode
         */
        private void addAllRouteInputContainedJob(List<IRepositoryNode> routeInputContainedJobs, IRepositoryNode jobNode) {
            if (jobNode == null) {
                return;
            }
            if (jobNode.getType() == ENodeType.REPOSITORY_ELEMENT) {
                try {
                    Item item = jobNode.getObject().getProperty().getItem();
                    if (!(item instanceof ProcessItem)) {
                        return;
                    }
                    ProcessItem pi = (ProcessItem) item;
                    if (CamelDesignerUtil.checkRouteInputExistInJob(pi)) {
                        routeInputContainedJobs.add(jobNode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            List<IRepositoryNode> children = jobNode.getChildren();
            for (IRepositoryNode child : children) {
                addAllRouteInputContainedJob(routeInputContainedJobs, child);
            }
        }
    }

    class AssignJobReviewDialog extends RepositoryReviewDialog {

        private AssignJobWizardDialog container;

        public AssignJobReviewDialog(AssignJobWizardDialog container, Shell parentShell, ERepositoryObjectType type,
                String repositoryType, ViewerFilter[] vf) {
            super(parentShell, type, repositoryType, vf);
            this.container = container;
        }

        @Override
        public Control createDialogArea(Composite parent) {

            IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

            if (activeWorkbenchWindow != null) {
                IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();

                if (activePage != null) {

                    String perspectiveId = activePage.getPerspective().getId();
                    if ("org.talend.camel.perspective".equals(perspectiveId)) {
                        return super.createDialogArea(parent, "org.talend.rcp.perspective");
                    }

                }
            }

            return super.createDialogArea(parent);
        }

        @Override
        protected Button getButton(int id) {
            if (id == OK) {
                return container.getButton(IDialogConstants.FINISH_ID);
            } else if (id == CANCEL) {
                return container.getButton(IDialogConstants.CANCEL_ID);
            }
            return super.getButton(id);
        }

        @Override
        public void okPressed() {
            super.okPressed();
        }
    }
}
