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
package org.talend.camel.designer.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.designer.i18n.Messages;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.designer.ui.editor.CamelProcessEditorInput;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.exception.SystemException;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.CorePlugin;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.properties.BusinessProcessItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.SQLPatternItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.ui.editor.RepositoryEditorInput;
import org.talend.designer.codegen.ICodeGeneratorService;
import org.talend.designer.codegen.ISQLPatternSynchronizer;
import org.talend.designer.codegen.ITalendSynchronizer;
import org.talend.expressionbuilder.ExpressionPersistance;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.routines.RoutineEditorInput;

/**
 * DOC xye class global comment. Detailled comment
 */
public class OpenCamelExistVersionProcessWizard extends Wizard {

    OpenAnotherVersionPage mainPage = null;

    private final IRepositoryViewObject processObject;

    private boolean alreadyEditedByUser = false;

    private String originaleObjectLabel = null;

    private String originalVersion = null;

    public OpenCamelExistVersionProcessWizard(IRepositoryViewObject processObject) {
        this.processObject = processObject;
        originaleObjectLabel = processObject.getProperty().getLabel();
        originalVersion = processObject.getProperty().getVersion();

        ERepositoryStatus status = processObject.getRepositoryStatus();
        if (status == ERepositoryStatus.READ_ONLY || status == ERepositoryStatus.LOCK_BY_OTHER
                || status.equals(ERepositoryStatus.LOCK_BY_USER) && RepositoryManager.isOpenedItemInEditor(processObject)) {
            alreadyEditedByUser = true;
        }
    }

    @Override
    public void addPages() {
        mainPage = new OpenAnotherVersionPage(alreadyEditedByUser, processObject);
        addPage(mainPage);
        setWindowTitle(Messages.getString("OpenExistVersionProcess.open.title")); //$NON-NLS-1$
    }

    /**
     * Returns the currently active page for this workbench window.
     * 
     * @return the active page, or <code>null</code> if none
     */
    public IWorkbenchPage getActivePage() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    }

    @Override
    public boolean performCancel() {
        return super.performCancel();
    }

    private void lockObject(IRepositoryViewObject object) {
        IProxyRepositoryFactory repositoryFactory = CorePlugin.getDefault().getRepositoryService().getProxyRepositoryFactory();
        try {
            repositoryFactory.lock(object);
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        } catch (BusinessException e) {
            ExceptionHandler.process(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        if (mainPage.isCreateNewVersionJob()) {
            try {
                ProxyRepositoryFactory.getInstance().updateLockStatus();
            } catch (PersistenceException e1) {
                ExceptionHandler.process(e1);
            }
            ERepositoryStatus repositoryStatus = ProxyRepositoryFactory.getInstance().getStatus(processObject);
            if ((repositoryStatus.equals(ERepositoryStatus.READ_ONLY)) || repositoryStatus == ERepositoryStatus.LOCK_BY_OTHER
                    || repositoryStatus.equals(ERepositoryStatus.LOCK_BY_USER)) {
                Display.getDefault().syncExec(new Runnable() {

                    public void run() {
                        MessageDialog.openWarning(getShell(), "Warning",
                                Messages.getString("OpenCamelExistVersionProcessWizard.labelContent"));
                    }

                });
                return false;
            } else {
                IWorkspaceRunnable runnable = new IWorkspaceRunnable() {

                    public void run(final IProgressMonitor monitor) throws CoreException {
                        if (!alreadyEditedByUser) {
                            getProperty().setVersion(mainPage.getNewVersion());
                            refreshNewJob();
                            try {
                                ProxyRepositoryFactory.getInstance()
                                        .saveProject(ProjectManager.getInstance().getCurrentProject());
                            } catch (Exception e) {
                                ExceptionHandler.process(e);
                            }
                        }
                        try {
                            ProxyRepositoryFactory.getInstance().lock(processObject);
                        } catch (PersistenceException e) {
                            ExceptionHandler.process(e);
                        } catch (LoginException e) {
                            ExceptionHandler.process(e);
                        }
                        boolean locked = processObject.getRepositoryStatus().equals(ERepositoryStatus.LOCK_BY_USER);
                        openAnotherVersion((RepositoryNode) processObject.getRepositoryNode(), !locked);
                        try {
                            ProxyRepositoryFactory.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());
                        } catch (Exception e) {
                            ExceptionHandler.process(e);
                        }
                    }
                };
                IWorkspace workspace = ResourcesPlugin.getWorkspace();
                try {
                    ISchedulingRule schedulingRule = workspace.getRoot();
                    // the update the project files need to be done in the workspace
                    // runnable to avoid all notification
                    // of changes before the end of the modifications.
                    workspace.run(runnable, schedulingRule, IWorkspace.AVOID_UPDATE, null);
                } catch (CoreException e) {
                    MessageBoxExceptionHandler.process(e);
                }
            }
        } else {
            StructuredSelection selection = (StructuredSelection) mainPage.getSelection();
            RepositoryNode node = (RepositoryNode) selection.getFirstElement();
            boolean lastVersion = node.getObject().getVersion().equals(processObject.getVersion());
            // processObject.getProperty().setVersion(originalVersion);
            if (lastVersion) {
                lockObject(processObject);
            }
            ERepositoryStatus status = node.getObject().getRepositoryStatus();
            boolean isLocked = false;
            if (status == ERepositoryStatus.LOCK_BY_USER) {
                isLocked = true;
            }

            // Only latest version can be editted
            openAnotherVersion(node, !lastVersion || !isLocked);
        }
        return true;
    }

    private boolean refreshNewJob() {
        if (alreadyEditedByUser) {
            return false;
        }
        IProxyRepositoryFactory repositoryFactory = CorePlugin.getDefault().getRepositoryService().getProxyRepositoryFactory();
        try {
            repositoryFactory.save(getProperty(), this.originaleObjectLabel, this.originalVersion);
            ExpressionPersistance.getInstance().jobNameChanged(originaleObjectLabel, processObject.getLabel());
            ProxyRepositoryFactory.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());
            return true;
        } catch (PersistenceException e) {
            MessageBoxExceptionHandler.process(e);
            return false;
        }
    }

    private void openAnotherVersion(final RepositoryNode node, final boolean readonly) {
        try {
            if (node.getObject() != null) {
                Item item = node.getObject().getProperty().getItem();
                IWorkbenchPage page = getActivePage();
                IEditorPart editorPart = null;
                RepositoryEditorInput fileEditorInput = null;
                ICodeGeneratorService codeGenService = (ICodeGeneratorService) GlobalServiceRegister.getDefault().getService(
                        ICodeGeneratorService.class);

                if (item instanceof CamelProcessItem) {
                    CamelProcessItem processItem = (CamelProcessItem) item;
                    fileEditorInput = new CamelProcessEditorInput(processItem, true, false, readonly);

                } else if (item instanceof BusinessProcessItem) {
                    BusinessProcessItem businessProcessItem = (BusinessProcessItem) item;
                    IFile file = CorePlugin.getDefault().getDiagramModelService()
                            .getDiagramFileAndUpdateResource(page, businessProcessItem);
                    fileEditorInput = new RepositoryEditorInput(file, businessProcessItem);
                } else if (item instanceof BeanItem) {
                    BeanItem routineItem = (BeanItem) item;
                    ITalendSynchronizer routineSynchronizer = codeGenService.createCamelBeanSynchronizer();
                    IFile file = routineSynchronizer.getFile(routineItem);
                    ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
                    String lastVersion = factory.getLastVersion(routineItem.getProperty().getId()).getVersion();
                    String curVersion = routineItem.getProperty().getVersion();
                    if (curVersion != null && curVersion.equals(lastVersion)) {
                        file = routineSynchronizer.getFile(routineItem);
                    } else {
                        file = routineSynchronizer.getRoutinesFile(routineItem);
                    }
                    if (file == null) {
                        return;
                    }
                    fileEditorInput = new RoutineEditorInput(file, routineItem);
                } else if (item instanceof SQLPatternItem) {
                    SQLPatternItem patternItem = (SQLPatternItem) item;
                    ISQLPatternSynchronizer SQLPatternSynchronizer = codeGenService.getSQLPatternSynchronizer();
                    SQLPatternSynchronizer.syncSQLPattern(patternItem, true);
                    IFile file = SQLPatternSynchronizer.getSQLPatternFile(patternItem);
                    if (file == null) {
                        return;
                    }
                    fileEditorInput = new RepositoryEditorInput(file, patternItem);
                }

                editorPart = page.findEditor(fileEditorInput);
                if (editorPart == null) {
                    // fileEditorInput.setView(getViewPart());
                    fileEditorInput.setRepositoryNode(node);
                    if (item instanceof CamelProcessItem) {
                        page.openEditor(fileEditorInput, CamelMultiPageTalendEditor.ID, readonly);

                    } else if (item instanceof BusinessProcessItem) {
                        CorePlugin.getDefault().getDiagramModelService().openBusinessDiagramEditor(page, fileEditorInput);
                    } else {
                        ECodeLanguage lang = ((RepositoryContext) CorePlugin.getContext().getProperty(
                                Context.REPOSITORY_CONTEXT_KEY)).getProject().getLanguage();
                        String talendEditorID = "org.talend.designer.core.ui.editor.StandAloneTalend" + lang.getCaseName() + "Editor"; //$NON-NLS-1$ //$NON-NLS-2$
                        page.openEditor(fileEditorInput, talendEditorID);
                    }
                } else {
                    page.activate(editorPart);
                }
            }
        } catch (PartInitException e) {
            MessageBoxExceptionHandler.process(e);
        } catch (PersistenceException e) {
            MessageBoxExceptionHandler.process(e);
        } catch (SystemException e) {
            MessageBoxExceptionHandler.process(e);
        }
    }

    private Property getProperty() {
        return processObject.getProperty();
    }

    public String getOriginVersion() {
        return this.originalVersion;
    }

}
