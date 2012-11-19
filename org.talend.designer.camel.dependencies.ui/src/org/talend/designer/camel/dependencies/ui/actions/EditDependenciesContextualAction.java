package org.talend.designer.camel.dependencies.ui.actions;

import java.util.Arrays;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.designer.ui.editor.CamelProcessEditorInput;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.designer.camel.dependencies.ui.Messages;
import org.talend.designer.camel.dependencies.ui.UIActivator;
import org.talend.designer.camel.dependencies.ui.dialog.RelativeEditorsSaveDialog;
import org.talend.designer.camel.dependencies.ui.editor.RouterDependenciesEditor;
import org.talend.designer.camel.dependencies.ui.editor.RouterDependenciesEditorInput;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryService;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.AContextualAction;
import org.talend.designer.core.DesignerPlugin;

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
		/*
		 * if the route is in a ref project
		 * then it can't edit
		 */
		IRepositoryService service = DesignerPlugin.getDefault().getRepositoryService();
		IProxyRepositoryFactory repFactory = service.getProxyRepositoryFactory();
		if (!repFactory.isPotentiallyEditable(node.getObject())) {
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
			Item item = node.getObject().getProperty().getItem();
			
			/*
			 * if corresponding camel editor is opened, then reuse the model with it
			 */
			CamelProcessEditorInput processEditorInput = new CamelProcessEditorInput((CamelProcessItem) item, true, true, false);
			
			IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			
			IEditorPart processEditor = activePage.findEditor((IEditorInput) processEditorInput);
			if(processEditor != null && processEditor.isDirty()){
				RelativeEditorsSaveDialog dialog = new RelativeEditorsSaveDialog(activePage.getWorkbenchWindow().getShell(), Arrays.asList(processEditor));
				int open = dialog.open();
				if(open != Dialog.OK){
					return;
				}
			}
			
			if(processEditor!=null){
				node = (RepositoryNode) processEditor.getEditorInput().getAdapter(RepositoryNode.class);
			}
			if(node == null){
				return;
			}
			
			RouterDependenciesEditorInput input = new RouterDependenciesEditorInput(
					node);
			IEditorPart editor = activePage.findEditor(input);
			if (editor != null) {
				activePage.bringToTop(editor);
			} else
				activePage.openEditor(input, RouterDependenciesEditor.ID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
