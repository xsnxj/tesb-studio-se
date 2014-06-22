package org.talend.camel.designer.ui.view;

import org.eclipse.gef.commands.Command;
import org.talend.camel.designer.ui.editor.RouteProcess;

/**
 * this command used to change the spring configuration
 * of RouteBuilder
 */
public class ChangeSpringConfigurationCommand extends Command {

	private String newContent;

	private RouteProcess process;

	private String oldContent;

	public ChangeSpringConfigurationCommand(String newContent,
			RouteProcess process) {
		super();
		this.newContent = newContent;
		this.process = process;
		this.oldContent = process.getSpringContent();
	}

	@Override
	public void execute() {
		this.oldContent = process.getSpringContent();
		process.setSpringContent(newContent);
	}

	@Override
	public boolean canExecute() {
		return !newContent.trim().equals(process.getSpringContent());
	}

	@Override
	public void undo() {
		newContent = process.getSpringContent();
		process.setSpringContent(oldContent);
	}
}
