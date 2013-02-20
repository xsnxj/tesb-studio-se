package org.talend.camel.designer.generator;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.talend.camel.designer.ui.wizards.AssignJobWizard;
import org.talend.camel.designer.ui.wizards.AssignJobWizardDialog;
import org.talend.core.properties.tab.IDynamicProperty;
import org.talend.designer.core.ui.editor.cmd.PropertyChangeCommand;
//import org.talend.designer.core.ui.editor.cmd.CreateProcessCommand;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.properties.controllers.ProcessController;

public class RouteInputProcessTypeController extends ProcessController {

	public RouteInputProcessTypeController(IDynamicProperty dp) {
		super(dp);
	}

	protected Command createButtonCommand(Button button) {
		
		String procssId = null;
		if (elem != null && elem instanceof Node) {
			Node runJobNode = (Node) elem;
			procssId = runJobNode.getProcess().getId();
		}
		
		AssignJobWizard assignJobWizard = new AssignJobWizard(procssId);
		WizardDialog wizardDialog = new AssignJobWizardDialog(null, assignJobWizard);
		if (wizardDialog.open() == WizardDialog.OK) {
			String id = assignJobWizard.getSelectedProcessId();
			String paramName = (String) button.getData(PARAMETER_NAME);
			return new PropertyChangeCommand(elem, paramName, id);
		}
		return null;
	}

}
