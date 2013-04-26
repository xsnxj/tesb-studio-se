package org.talend.repository.services.action;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.utils.RepositoryManagerHelper;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.ui.actions.metadata.AbstractCreateAction;
import org.talend.core.ui.branding.IBrandingConfiguration;
import org.talend.designer.core.DesignerPlugin;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.ProjectRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.util.EServiceCoreImage;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.services.utils.LocalWSDLEditor;
import org.talend.repository.services.utils.WSDLUtils;
import org.talend.repository.ui.views.IRepositoryView;

public class OpenWSDLEditorAction extends AbstractCreateAction implements IIntroAction {

    private final static String ID = "org.talend.repository.services.utils.LocalWSDLEditor";

    private ERepositoryObjectType currentNodeType;

    public OpenWSDLEditorAction() {
        this.setText("Open WSDL Editor");
        this.setToolTipText("Open WSDL Editor");

        this.setImageDescriptor(ImageProvider.getImageDesc(EServiceCoreImage.SERVICE_ICON));
        currentNodeType = ESBRepositoryNodeType.SERVICES;

        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(new IPartListener() {

            public void partActivated(IWorkbenchPart part) {

            }

            public void partBroughtToTop(IWorkbenchPart part) {

            }

            public void partClosed(IWorkbenchPart part) {
                if (part instanceof LocalWSDLEditor) {
                    LocalWSDLEditor localWSDLEditor = (LocalWSDLEditor) part;
                    // unlock
                    ServiceItem serviceItem = localWSDLEditor.getServiceItem();
                    if (null != serviceItem) {
                        try {
                            DesignerPlugin.getDefault().getProxyRepositoryFactory().unlock(serviceItem);
                        } catch (Exception e) {
                            ExceptionHandler.process(e);
                        }
                    }

                    localWSDLEditor.setServiceItem(null);
                    localWSDLEditor.setRepositoryNode(null);
                }
            }

            public void partDeactivated(IWorkbenchPart part) {

            }

            public void partOpened(IWorkbenchPart part) {

            }

        });
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
        ProxyRepositoryFactory.getInstance();
        switch (node.getType()) {
        case REPOSITORY_ELEMENT:
            break;
        default:
            return;
        }
        boolean flag = true;
        if (node.getObject() == null) {
            flag = false;
        }
        if (flag) {
            flag = isLastVersion(node);
        }
        setEnabled(flag);
    }

    @Override
    public Class getClassForDoubleClick() {
        return ServiceItem.class;
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
        if (repositoryNode.getObjectType() == ERepositoryObjectType.SERVICESPORT) {
            repositoryNode = repositoryNode.getParent();
        }
        if (isToolbar()) {
            if (repositoryNode != null && repositoryNode.getContentType() != currentNodeType) {
                repositoryNode = null;
            }
            if (repositoryNode == null) {
                repositoryNode = getRepositoryNodeForDefault(currentNodeType);
            }
        }
        if (repositoryNode.getObject() == null) {
            return;
        }
        try {
        	ServiceItem serviceItem = (ServiceItem) repositoryNode.getObject().getProperty().getItem();
        	IFile file = WSDLUtils.getWsdlFile(serviceItem);
        	ServiceEditorInput editorInput=new ServiceEditorInput(file, serviceItem);
        	if (DesignerPlugin.getDefault().getProxyRepositoryFactory().isEditableAndLockIfPossible(serviceItem)) {
        		editorInput.setReadOnly(false);
        	} else {
        		editorInput.setReadOnly(true);
        	}
        	IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IEditorPart editor = page.openEditor(editorInput, ID, true);
            if (editor instanceof LocalWSDLEditor) {
                LocalWSDLEditor wsdlEditor = (LocalWSDLEditor) editor;
                wsdlEditor.setServiceItem(serviceItem);
                wsdlEditor.setRepositoryNode(repositoryNode);
            }
        } catch (CoreException e) {
            ExceptionHandler.process(e);
        } 
    }

    public void setRepositoryNode(RepositoryNode repositoryNode) {
        this.repositoryNode = repositoryNode;
    }

    public void run(IIntroSite site, Properties params) {
        PlatformUI.getWorkbench().getIntroManager().closeIntro(PlatformUI.getWorkbench().getIntroManager().getIntro());

        do_SwitchPerspective_ExpandRepositoryNode_SelectNodeItem(IBrandingConfiguration.PERSPECTIVE_DI_ID,
                ESBRepositoryNodeType.SERVICES, params.getProperty("nodeId"));

        repositoryNode = RepositoryNodeUtilities.getRepositoryNode(params.getProperty("nodeId"), false);

        doRun();
    }

    private void do_SwitchPerspective_ExpandRepositoryNode_SelectNodeItem(String perspectiveId,
            ERepositoryObjectType repositoryNodeType, String nodeItemId) {

        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (null == workbenchWindow) {
            return;
        }
        IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
        if (null == workbenchPage) {
            return;
        }

        IPerspectiveDescriptor currentPerspective = workbenchPage.getPerspective();
        if (!perspectiveId.equals(currentPerspective.getId())) {
            // show required perspective
            try {
                workbenchWindow.getWorkbench().showPerspective(perspectiveId, workbenchWindow);
                workbenchPage = workbenchWindow.getActivePage();
            } catch (WorkbenchException e) {
                ExceptionHandler.process(e);
                return;
            }
        }

        // find repository node
        IRepositoryView view = RepositoryManagerHelper.getRepositoryView();
        RepositoryNode repositoryNode = ((ProjectRepositoryNode) view.getRoot()).getRootRepositoryNode(repositoryNodeType);
        if (null != repositoryNode) {
            // expand/select repository node
            setWorkbenchPart(view);
            final StructuredViewer viewer = view.getViewer();
            if (viewer instanceof TreeViewer) {
                ((TreeViewer) viewer).expandToLevel(repositoryNode, 1);
            }
            viewer.setSelection(new StructuredSelection(repositoryNode));

            // find node item
            RepositoryNode nodeItem = RepositoryNodeUtilities.getRepositoryNode(nodeItemId, false);
            if (null != nodeItem) {
                // expand/select node item
                if (viewer instanceof TreeViewer) {
                    ((TreeViewer) viewer).expandToLevel(nodeItem, 2);
                }
                viewer.setSelection(new StructuredSelection(nodeItem));
            }
        }
    }
}
