package org.talend.designer.camel.dependencies.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.designer.camel.dependencies.ui.Messages;
import org.talend.designer.camel.dependencies.ui.UIActivator;
import org.talend.designer.camel.dependencies.ui.editor.RouterDependenciesEditor;
import org.talend.designer.camel.dependencies.ui.editor.RouterDependenciesEditorInput;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.AContextualAction;

public class EditDependenciesContextualAction extends AContextualAction {

	public EditDependenciesContextualAction() {
		setText(Messages.EditDependenciesContextualAction_ActionName);
		setImageDescriptor(UIActivator
				.getImageDescriptor(UIActivator.DEPEN_ICON));
	}

	@Override
	public void init(TreeViewer viewer, IStructuredSelection selection) {
		setEnabled(false);
		// size should be one
		if (selection.size() != 1) {
			return;
		}
		Object firstElement = ((IStructuredSelection) selection)
				.getFirstElement();
		// should be repository node
		if (!(firstElement instanceof RepositoryNode)) {
			return;
		}
		// should be simple element
		RepositoryNode node = (RepositoryNode) firstElement;
		if (ENodeType.REPOSITORY_ELEMENT != node.getType()) {
			return;
		}
		// should be route process
		ERepositoryObjectType objectType = node.getObjectType();
		if (objectType != ERepositoryObjectType.valueOf(
				ERepositoryObjectType.class, Messages.EditDependenciesContextualAction_1)) {
			return;
		}
		// should be not deleted
		if (node.getObject().getRepositoryStatus() == ERepositoryStatus.DELETED) {
			return;
		}
		// should be last version
		if (!isLastVersion(node)) {
			return;
		}
		setEnabled(true);
	}

	@Override
	protected void doRun() {
		RepositoryNode node = (RepositoryNode) ((IStructuredSelection) getSelection())
				.getFirstElement();
		if (node == null) {
			return;
		}
		try {
			IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			RouterDependenciesEditorInput input = new RouterDependenciesEditorInput(
					node);
			IEditorPart editor = activePage.findEditor(input);
			if (editor != null) {
				activePage.bringToTop(editor);
			} else
				activePage.openEditor(input, RouterDependenciesEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

}
