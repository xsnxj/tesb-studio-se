package org.talend.designer.camel.resource.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

public class PropertyManagerWizardDialog extends WizardDialog {

	/**
	 * 
	 * @param parentShell
	 * @param newWizard
	 */
	public PropertyManagerWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

	public Button getFinishButton() {
		return getButton(IDialogConstants.FINISH_ID);
	}

}
