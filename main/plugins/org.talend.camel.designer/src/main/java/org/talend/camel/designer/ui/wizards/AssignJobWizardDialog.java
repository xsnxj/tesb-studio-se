package  org.talend.camel.designer.ui.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

public class AssignJobWizardDialog extends WizardDialog {

	public AssignJobWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}
	
	@Override
	public Button getButton(int id) {
		return super.getButton(id);
	}
}
