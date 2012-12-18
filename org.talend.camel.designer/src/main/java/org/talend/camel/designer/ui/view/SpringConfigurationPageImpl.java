package org.talend.camel.designer.ui.view;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.Page;
import org.talend.camel.designer.ui.editor.RouteProcess;
import org.talend.designer.core.ui.editor.AbstractTalendEditor;

public class SpringConfigurationPageImpl extends Page implements
		ISpringConfigurationPage, IPageBookViewPage {

	private RouteProcess process;
	private Composite composite;
	private AbstractTalendEditor editor;

	public SpringConfigurationPageImpl(AbstractTalendEditor abstractTalendEditor) {
		this.editor = abstractTalendEditor;
		this.process = (RouteProcess) abstractTalendEditor.getProcess();
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		composite.setLayout(new GridLayout(1, false));

		StyledText text = new StyledText(composite, SWT.BORDER | SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		text.setText(process.getSpringContent());

		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				editor.getCommandStack().execute(
						new ChangeSpringConfigurationCommand(
								((StyledText) e.widget).getText(), process));
			}
		});
	}

	public Control getControl() {
		return composite;
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {

	}

	public ISelection getSelection() {
		return null;
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {

	}

	public void setSelection(ISelection selection) {

	}

	@Override
	public void setFocus() {

	}

}
