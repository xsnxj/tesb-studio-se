package org.talend.repository.services.ui.assign;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.repository.services.action.AssignJobAction;
import org.talend.repository.ui.dialog.RepositoryReviewDialog;

public class AssignJobPage extends WizardPage {

	private AssignJobAction assignJobAction;
	private AssignJobReviewDialog dialog;

	protected AssignJobPage(String pageName, AssignJobAction assignJobAction) {
		super(pageName);
		this.assignJobAction = assignJobAction;
	}

	public void createContents(Composite parent) {
	}

	public void createControl(Composite parent) {
		dialog = new AssignJobReviewDialog(
				(AssignJobWizardDialog) getContainer(), parent.getShell(),
				ERepositoryObjectType.PROCESS, "");
		dialog.setJobIDList(assignJobAction.getAllReferenceJobId());
		setControl(dialog.createDialogArea(parent));
	}

	@Override
	public IWizardPage getNextPage() {
		return null;
	}

	public boolean finish() {
		dialog.okPressed();
		assignJobAction.changeOldJob();
		return assignJobAction.assign(dialog.getResult());
	}

	class AssignJobReviewDialog extends RepositoryReviewDialog {

		private AssignJobWizardDialog container;

		public AssignJobReviewDialog(AssignJobWizardDialog container,
				Shell parentShell, ERepositoryObjectType type,
				String repositoryType) {
			super(parentShell, type, repositoryType);
			this.container = container;
		}

		@Override
		public Control createDialogArea(Composite parent) {
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
