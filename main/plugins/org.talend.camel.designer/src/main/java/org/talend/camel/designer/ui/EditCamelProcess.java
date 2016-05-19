// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.ui;

import java.util.Properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.designer.i18n.Messages;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.designer.ui.editor.CamelProcessEditorInput;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.model.utils.RepositoryManagerHelper;
import org.talend.core.repository.model.ProjectRepositoryNode;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.ui.branding.IBrandingConfiguration;
import org.talend.designer.core.DesignerPlugin;
import org.talend.designer.core.ui.action.AbstractProcessAction;
import org.talend.designer.runprocess.ItemCacheManager;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.BinRepositoryNode;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryService;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.ui.views.IRepositoryView;

/**
 * DOC smallet class global comment. Detailled comment <br/>
 * 
 * $Id: EditProcess.java 52559 2010-12-13 04:14:06Z nrousseau $
 * 
 */
public class EditCamelProcess extends AbstractProcessAction implements IIntroAction {

    private final String EDIT_LABEL = Messages.getString("EditProcess.editJob"); //$NON-NLS-1$

    private final String OPEN_LABEL = Messages.getString("EditProcess.openJob"); //$NON-NLS-1$

    private static final String DBPROJECT_LABEL = "teneo";

    private Properties params;

    public EditCamelProcess() {
        super();
        this.setText(EDIT_LABEL);
        this.setToolTipText(EDIT_LABEL);
        this.setImageDescriptor(ImageProvider.getImageDesc(ECamelCoreImage.ROUTES_ICON));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    protected void doRun() {
        ISelection selection = getSelectedObject();
        if (selection == null) {
            return;
        }
        Object obj = ((IStructuredSelection) selection).getFirstElement();
        if (obj == null) {
            return;
        }
        RepositoryNode node = (RepositoryNode) obj;
        Property property = node.getObject().getProperty();
        CamelProcessItem processItem = null;

        if (property != null) {
            ItemCacheManager.clearCache();
            Assert.isTrue(property.getItem() instanceof CamelProcessItem);

            processItem = (CamelProcessItem) property.getItem();

            IWorkbenchPage page = getActivePage();

            try {
                final CamelProcessEditorInput fileEditorInput = new CamelProcessEditorInput(processItem, true, true);
                checkUnLoadedNodeForProcess(fileEditorInput);

                IEditorPart editorPart = page.findEditor(fileEditorInput);

                if (editorPart == null) {
                    fileEditorInput.setRepositoryNode(node);
                    editorPart = page.openEditor(fileEditorInput, CamelMultiPageTalendEditor.ID, true);
                    /* MultiPageTalendEditor openEditor = (MultiPageTalendEditor) */
                    // List<AbstractProcessProvider> findAllProcessProviders =
                    // AbstractProcessProvider.findAllProcessProviders();
                    // boolean isImport = false;
                    // for (AbstractProcessProvider abstractProcessProvider : findAllProcessProviders) {
                    // if (abstractProcessProvider != null) {
                    // boolean update = abstractProcessProvider.updateProcessContexts((Process) fileEditorInput
                    // .getLoadedProcess());
                    // if (update) {
                    // isImport = true;
                    // }
                    // }
                    // }
                    // if (isImport) {
                    // openEditor.getTalendEditor().getCommandStack().execute(new Command() {
                    // });
                    // }
                } else {
                    ((CamelMultiPageTalendEditor) editorPart).setReadOnly(fileEditorInput.setForceReadOnly(false));
                    page.activate(editorPart);
                }
            } catch (PartInitException e) {
                MessageBoxExceptionHandler.process(e);
            } catch (PersistenceException e) {
                MessageBoxExceptionHandler.process(e);
            }
        } else {
            // TDI-21143 : Studio repository view : remove all refresh call to repo view
            // IRepositoryView viewPart = getViewPart();
            // if (viewPart != null) {
            // viewPart.refresh(ERepositoryObjectType.PROCESS);
            // }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse.jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(TreeViewer viewer, IStructuredSelection selection) {
        boolean canWork = !selection.isEmpty() && selection.size() == 1;
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        if (factory.isUserReadOnlyOnCurrentProject()) {
            canWork = false;
        }
        if (canWork) {
            Object o = selection.getFirstElement();
            RepositoryNode node = (RepositoryNode) o;
            if (RepositoryManager.isOpenedItemInEditor(node.getObject())) {
                canWork = false;
            } else{
	            switch (node.getType()) {
	            case REPOSITORY_ELEMENT:
	                if (node.getObjectType() != CamelRepositoryNodeType.repositoryRoutesType) {
	                    canWork = false;
	                } else {
	                    IRepositoryService service = DesignerPlugin.getDefault().getRepositoryService();
	                    IProxyRepositoryFactory repFactory = service.getProxyRepositoryFactory();
	                    if (repFactory.isPotentiallyEditable(node.getObject())) {
	                        this.setText(EDIT_LABEL);
	                    } else {
	                        this.setText(OPEN_LABEL);
	                    }
	                }
	                break;
	            default:
	                canWork = false;
	            }
            }
            RepositoryNode parent = node.getParent();
            if (canWork && parent != null && parent instanceof BinRepositoryNode) {
                canWork = false;
            }
            if (canWork && !ProjectManager.getInstance().isInCurrentMainProject(node)) {
                canWork = false;
            }

            // If the editProcess action canwork is true, then detect that the job version is the latest verison or not.
            if (canWork) {
                canWork = isLastVersion(node);
            }

        }
        setEnabled(canWork);
    }

    /**
     * 
     * DOC YeXiaowei EditProcess class global comment. Detailled comment
     */
    // @SuppressWarnings("unchecked")
    // private static class IRepositoryObjectComparator implements Comparator {
    //
    // public int compare(Object o1, Object o2) {
    // return VersionUtils.compareTo(((IRepositoryObject) o1).getVersion(), ((IRepositoryObject) o2).getVersion());
    // }
    // }
    //
    // @SuppressWarnings("unchecked")
    // private boolean isLastJobVersion(RepositoryNode repositoryObject) {
    // try {
    // List<IRepositoryObject> allVersion =
    // ProxyRepositoryFactory.getInstance().getAllVersion(repositoryObject.getId());
    // if (allVersion == null || allVersion.isEmpty()) {
    // return false;
    // }
    // // Collections.sort(allVersion, new IRepositoryObjectComparator());
    // IRepositoryObject lastVersion = allVersion.get(allVersion.size() - 1);
    // return lastVersion.getVersion().equals(repositoryObject.getObject().getVersion());
    // } catch (PersistenceException e) {
    // return false;
    // }
    // }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.AContextualView#getClassForDoubleClick()
     */
    @Override
    public Class getClassForDoubleClick() {
        return CamelProcessItem.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.intro.config.IIntroAction#run(org.eclipse.ui.intro.IIntroSite, java.util.Properties)
     */
    public void run(IIntroSite site, Properties params) {
        this.params = params;
        PlatformUI.getWorkbench().getIntroManager().closeIntro(PlatformUI.getWorkbench().getIntroManager().getIntro());
        do_SwitchPerspective_ExpandRepositoryNode_SelectNodeItem(IBrandingConfiguration.PERSPECTIVE_CAMEL_ID,
                CamelRepositoryNodeType.repositoryRoutesType, params.getProperty("nodeId"));
        doRun();
    }

    private ISelection getSelectedObject() {
        if (params == null) {
            return getSelection();
        } else {
            RepositoryNode repositoryNode = RepositoryNodeUtilities.getRepositoryNode(params.getProperty("nodeId"), false);
            IRepositoryView viewPart = getViewPart();
            if (repositoryNode != null && viewPart != null) {
                RepositoryNodeUtilities.expandParentNode(viewPart, repositoryNode);
                return new StructuredSelection(repositoryNode);
            }
            return null;
        }
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
                // view.getViewer().expandToLevel(nodeItem, 2);
                viewer.setSelection(new StructuredSelection(nodeItem));
            }
        }
    }
}
