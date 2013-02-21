package org.talend.camel.designer.generator;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.talend.camel.designer.ui.wizards.AssignJobWizard;
import org.talend.camel.designer.ui.wizards.AssignJobWizardDialog;
import org.talend.core.properties.tab.IDynamicProperty;
import org.talend.designer.core.ui.editor.cmd.PropertyChangeCommand;
import org.talend.designer.core.ui.editor.properties.controllers.ProcessController;

public class RouteInputProcessTypeController extends ProcessController {

	public RouteInputProcessTypeController(IDynamicProperty dp) {
		super(dp);
	}

	protected Command createButtonCommand(Button button) {
		AssignJobWizard assignJobWizard = new AssignJobWizard();
		WizardDialog wizardDialog = new AssignJobWizardDialog(button.getShell(), assignJobWizard);
		if (wizardDialog.open() == WizardDialog.OK) {
			String id = assignJobWizard.getSelectedProcessId();
			if(id != null){
				String paramName = (String) button.getData(PARAMETER_NAME);
				return new PropertyChangeCommand(elem, paramName, id);
			}
		}
		return null;
	}

}
