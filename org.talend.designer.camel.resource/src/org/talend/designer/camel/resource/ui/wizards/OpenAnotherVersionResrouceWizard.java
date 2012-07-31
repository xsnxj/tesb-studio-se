// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.resource.ui.wizards;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.camel.designer.ui.wizards.OpenAnotherVersionPage;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.CorePlugin;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.resource.editors.RouteResourceEditor;
import org.talend.designer.camel.resource.editors.input.RouteResourceInput;
import org.talend.designer.camel.resource.i18n.Messages;
import org.talend.expressionbuilder.ExpressionPersistance;
import org.talend.repository.ProjectManager;
import org.talend.repository.editor.RepositoryEditorInput;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;

/**
 * Open Route Resource with another version
 * 
 * @author xpli
 * 
 */
public class OpenAnotherVersionResrouceWizard extends Wizard {

	OpenAnotherVersionPage mainPage = null;

	private final IRepositoryViewObject repoObject;

	private boolean alreadyEditedByUser = false;

	private String originaleObjectLabel = null;

	private String originalVersion = null;

	public OpenAnotherVersionResrouceWizard(IRepositoryViewObject processObject) {
		this.repoObject = processObject;
		originaleObjectLabel = processObject.getProperty().getLabel();
		originalVersion = processObject.getProperty().getVersion();

		ERepositoryStatus status = processObject.getRepositoryStatus();
		if (status == ERepositoryStatus.LOCK_BY_OTHER
				|| status.equals(ERepositoryStatus.LOCK_BY_USER)
				&& RepositoryManager.isOpenedItemInEditor(processObject)) {
			alreadyEditedByUser = true;
		}
	}

	@Override
	public void addPages() {
		mainPage = new OpenAnotherVersionPage(alreadyEditedByUser,
				repoObject);
		addPage(mainPage);
		setWindowTitle(Messages.getString("OpenAnotherVersionResrouceWizard.Title")); //$NON-NLS-1$
	}

	/**
	 * Returns the currently active page for this workbench window.
	 * 
	 * @return the active page, or <code>null</code> if none
	 */
	public IWorkbenchPage getActivePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage();
	}

	@Override
	public boolean performCancel() {
		restoreVersion();
		return super.performCancel();
	}

	private void lockObject(IRepositoryViewObject object) {
		IProxyRepositoryFactory repositoryFactory = CorePlugin.getDefault()
				.getRepositoryService().getProxyRepositoryFactory();
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

			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {

				public void run(final IProgressMonitor monitor)
						throws CoreException {
					if (!alreadyEditedByUser) {
						refreshNewJob();
						try {
							ProxyRepositoryFactory.getInstance().saveProject(
									ProjectManager.getInstance()
											.getCurrentProject());
						} catch (Exception e) {
							ExceptionHandler.process(e);
						}
					}

					try {
						ProxyRepositoryFactory.getInstance().lock(repoObject);
					} catch (PersistenceException e) {
						ExceptionHandler.process(e);
					} catch (LoginException e) {
						ExceptionHandler.process(e);
					}

					boolean locked = repoObject.getRepositoryStatus().equals(
							ERepositoryStatus.LOCK_BY_USER);
					openAnotherVersion(
							(RepositoryNode) repoObject.getRepositoryNode(),
							!locked);
					try {
						ProxyRepositoryFactory.getInstance().saveProject(
								ProjectManager.getInstance()
										.getCurrentProject());
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
				workspace.run(runnable, schedulingRule,
						IWorkspace.AVOID_UPDATE, null);
			} catch (CoreException e) {
				MessageBoxExceptionHandler.process(e);
			}
		} else {
			StructuredSelection selection = (StructuredSelection) mainPage
					.getSelection();
			RepositoryNode node = (RepositoryNode) selection.getFirstElement();
			boolean lastVersion = node.getObject().getVersion()
					.equals(repoObject.getVersion());
			repoObject.getProperty().setVersion(originalVersion);
			if (lastVersion) {
				lockObject(repoObject);
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
		IProxyRepositoryFactory repositoryFactory = ProxyRepositoryFactory
				.getInstance();
		try {
			repositoryFactory.save(getProperty(), this.originaleObjectLabel,
					this.originalVersion);
			ExpressionPersistance.getInstance().jobNameChanged(
					originaleObjectLabel, repoObject.getLabel());
			ProxyRepositoryFactory.getInstance().saveProject(
					ProjectManager.getInstance().getCurrentProject());
			return true;
		} catch (PersistenceException e) {
			MessageBoxExceptionHandler.process(e);
			return false;
		}
	}

	private void openAnotherVersion(final RepositoryNode node,
			final boolean readonly) {
		try {
			if (node.getObject() != null) {
				Item item = node.getObject().getProperty().getItem();
				IWorkbenchPage page = getActivePage();
				IEditorPart editorPart = null;
				RepositoryEditorInput fileEditorInput = null;

				if (item instanceof RouteResourceItem) {
					fileEditorInput = RouteResourceInput
							.createInput((RouteResourceItem) item);
				}

				editorPart = page.findEditor(fileEditorInput);
				if (editorPart == null) {
					// fileEditorInput.setView(getViewPart());
					fileEditorInput.setRepositoryNode(node);
					page.openEditor(fileEditorInput, RouteResourceEditor.ID);
				} else {
					page.activate(editorPart);
				}
			}
		} catch (PartInitException e) {
			MessageBoxExceptionHandler.process(e);
		}
	}

	private Property getProperty() {
		return repoObject.getProperty();
	}

	public String getOriginVersion() {
		return this.originalVersion;
	}

	public void restoreVersion() {
		getProperty().setVersion(getOriginVersion());
	}

}
