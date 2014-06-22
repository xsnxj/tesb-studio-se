package org.talend.designer.camel.dependencies.ui.dialog;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.talend.designer.camel.dependencies.ui.Messages;

public class RelativeEditorsSaveDialog extends ListSelectionDialog {

	public RelativeEditorsSaveDialog(Shell parentShell, List<IEditorPart> input) {
		super(parentShell, input, new ArrayContentProvider(), createDialogLabelProvider(), null);
		setTitle(Messages.RelativeEditorsSaveDialog_saveEditorsTitle);
		setMessage(Messages.RelativeEditorsSaveDialog_saveEditorsMsg);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected List getInitialElementSelections() {
		return (List) getViewer().getInput();
	}
	
	private static ILabelProvider createDialogLabelProvider() {
		return new LabelProvider() {
			public Image getImage(Object element) {
				return ((IEditorPart) element).getTitleImage();
			}
			public String getText(Object element) {
				return ((IEditorPart) element).getTitle();
			}
		};
	}
	
	@Override
	protected void okPressed() {
		super.okPressed();
		Object[] willToSave = getResult();
		if(willToSave!=null){
			for(Object o:willToSave){
				if(o!=null && o instanceof IEditorPart){
					((IEditorPart)o).doSave(new NullProgressMonitor());
				}
			}
		}
	}
}
