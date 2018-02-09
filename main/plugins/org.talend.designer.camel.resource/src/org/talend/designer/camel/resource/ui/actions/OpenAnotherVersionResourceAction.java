// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.resource.ui.actions;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.camel.model.CamelRepositoryNodeType;
import org.talend.core.model.repository.RepositoryObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.services.IUIRefresher;
import org.talend.designer.camel.resource.RouteResourceActivator;
import org.talend.designer.camel.resource.editors.input.RouteResourceInput;
import org.talend.designer.camel.resource.ui.wizards.OpenAnotherVersionResrouceWizard;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.BinRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.ui.actions.EditPropertiesAction;

/**
 * 
 * @author xpli
 * 
 */
public class OpenAnotherVersionResourceAction extends EditPropertiesAction {

	public OpenAnotherVersionResourceAction() {
        setText("Open another version");
        setToolTipText("Open another version");
        setImageDescriptor(RouteResourceActivator.createImageDesc("icons/open-another-version.png"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	protected void doRun() {

		ISelection selection = getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		RepositoryNode node = (RepositoryNode) obj;

		IPath path = RepositoryNodeUtilities.getPath(node);
		String originalName = node.getObject().getLabel();

		RepositoryObject repositoryObj = new RepositoryObject(node.getObject()
				.getProperty());
		repositoryObj.setRepositoryNode(node.getObject().getRepositoryNode());
		OpenAnotherVersionResrouceWizard wizard = new OpenAnotherVersionResrouceWizard(
				repositoryObj);
		WizardDialog dialog = new WizardDialog(
				Display.getCurrent().getActiveShell(), wizard);
		dialog.setPageSize(300, 250);
		dialog.setTitle("Open another version"); //$NON-NLS-1$
		if (dialog.open() == Dialog.OK) {
			refresh(node);
			// refresh the corresponding editor's name
			IEditorPart part = getCorrespondingEditor(node);
			if (part != null && part instanceof IUIRefresher) {
				((IUIRefresher) part).refreshName();
			} else {
				processRoutineRenameOperation(originalName, node, path);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.talend.repository.ui.actions.AContextualView#getClassForDoubleClick()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Class getClassForDoubleClick() {
		return RouteResourceItem.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse
	 * .jface.viewers.TreeViewer,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(TreeViewer viewer, IStructuredSelection selection) {
        boolean canWork = !selection.isEmpty() && (selection.size() == 1)
            && !ProxyRepositoryFactory.getInstance().isUserReadOnlyOnCurrentProject();
		if (canWork) {
			Object o = selection.getFirstElement();
			RepositoryNode node = (RepositoryNode) o;
			switch (node.getType()) {
			case REPOSITORY_ELEMENT:
				if (node.getObjectType() != CamelRepositoryNodeType.repositoryRouteResourceType) {
					canWork = false;
				}
				break;
			default:
				canWork = false;
			}
			RepositoryNode parent = node.getParent();
			if (canWork && parent != null
					&& parent instanceof BinRepositoryNode) {
				canWork = false;
			}
			if (canWork
					&& !ProjectManager.getInstance().isInCurrentMainProject(
							node)) {
				canWork = false;
			}

			// If the editProcess action canwork is true, then detect that the
			// job version is the latest verison or not.
			if (canWork) {
				canWork = isLastVersion(node);
			}

		}
		setEnabled(canWork);
	}

	protected IEditorPart getCorrespondingEditor(RepositoryNode node) {
		for (IEditorReference ref : getActivePage().getEditorReferences()) {
			try {
				IEditorInput input = ref.getEditorInput();
				if (!(input instanceof RouteResourceInput)) {
					continue;
				}

				RouteResourceInput repositoryInput = (RouteResourceInput) input;
				if (repositoryInput.getItem().equals(
						node.getObject().getProperty().getItem())) {
					return ref.getEditor(false);
				}
			} catch (PartInitException e) {
				continue;
			}
		}
		return null;
	}

}
