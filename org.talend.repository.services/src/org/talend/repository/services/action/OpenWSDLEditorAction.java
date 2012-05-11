package org.talend.repository.services.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.wsdl.ui.internal.InternalWSDLMultiPageEditor;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.utils.RepositoryManagerHelper;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.ui.actions.metadata.AbstractCreateAction;
import org.talend.core.ui.branding.IBrandingConfiguration;
import org.talend.designer.core.DesignerPlugin;
import org.talend.repository.model.IProxyRepositoryFactory;
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

    private ERepositoryObjectType currentNodeType;

    private final static String ID = "org.talend.repository.services.utils.LocalWSDLEditor";

    private List<ServiceItem> scriptList = new ArrayList<ServiceItem>();

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
                if (part instanceof InternalWSDLMultiPageEditor) {
                    InternalWSDLMultiPageEditor editor = (InternalWSDLMultiPageEditor) part;
                    if (editor instanceof LocalWSDLEditor) {
                        LocalWSDLEditor localWSDLEditor = (LocalWSDLEditor) editor;
                        if (localWSDLEditor.getServiceItem() != null) {
                            localWSDLEditor.removeListener();
                        }
                        localWSDLEditor.setServiceItem(null);
                        localWSDLEditor.setRepositoryNode(null);
                    }
                    String editorName = editor.getEditorInput().getName();
                    IProxyRepositoryFactory repFactory = DesignerPlugin.getDefault().getProxyRepositoryFactory();
                    Iterator it = scriptList.iterator();
                    try {
                        while (it.hasNext()) {
                            ServiceItem serviceItem = (ServiceItem) it.next();
                            String name = editorName.substring(0, editorName.lastIndexOf("_"));
                            if (name.equals(serviceItem.getProperty().getLabel())) {
                                repFactory.unlock(serviceItem);
                                it.remove();
                            }
                        }
                    } catch (Exception e) {
                        ExceptionHandler.process(e);
                    }
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
        ServiceItem serviceItem = (ServiceItem) repositoryNode.getObject().getProperty().getItem();
        LocalWSDLEditor wsdlEditor = null;
        IFile file = WSDLUtils.getWsdlFile(repositoryNode);
        IEditorInput editorInput = new FileEditorInput(file);
        WorkbenchPage page = (WorkbenchPage) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try {
            IEditorPart editor = page.openEditor(editorInput, ID, true);
            if (editor instanceof LocalWSDLEditor) {
                wsdlEditor = (LocalWSDLEditor) editor;
                wsdlEditor.setServiceItem(serviceItem);
                wsdlEditor.setRepositoryNode(repositoryNode);
                wsdlEditor.addListener();
            }
            // lock
            if (DesignerPlugin.getDefault().getProxyRepositoryFactory().isEditableAndLockIfPossible(serviceItem)) {
                // TO BE REMOVED and Checked but the above line does the lock already so no need to do it twice.
                DesignerPlugin.getDefault().getProxyRepositoryFactory().lock(serviceItem);
                scriptList.add(serviceItem);
            } else {
                wsdlEditor.setReadOnly(true);
            }
        } catch (CoreException e) {
            ExceptionHandler.process(e);
        } catch (LoginException e) {
            ExceptionHandler.process(e);
        } catch (PersistenceException e) {
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
