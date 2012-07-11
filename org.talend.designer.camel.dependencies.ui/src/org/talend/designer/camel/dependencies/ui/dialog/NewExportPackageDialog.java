package org.talend.designer.camel.dependencies.ui.dialog;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.talend.designer.camel.dependencies.core.model.ExportPackage;
import org.talend.designer.camel.dependencies.ui.Messages;

public class NewExportPackageDialog extends TitleAreaDialog{

	private ExportPackage ep = null;
	
	private boolean isEdit = false;
	
	public NewExportPackageDialog(Shell parentShell) {
		super(parentShell);
		this.isEdit = false;
		this.ep = new ExportPackage();
	}
	
	public NewExportPackageDialog(Shell parentShell, ExportPackage ep) {
		super(parentShell);
		this.ep = new ExportPackage(ep);
		this.isEdit = true;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(Messages.NewExportPackageDialog_dialogTitle);
		setTitle(isEdit?Messages.NewExportPackageDialog_editTitle:Messages.NewExportPackageDialog_addTitle);
		setMessage(isEdit?Messages.NewExportPackageDialog_editMsg:Messages.NewExportPackageDialog_addMsg);
		
		parent.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(composite, SWT.NONE).setText(Messages.NewExportPackageDialog_name);
		Text nameT = new Text(composite, SWT.BORDER);
		nameT.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameT.setText(ep.getName()==null?"":ep.getName()); //$NON-NLS-1$
		
		new Label(composite, SWT.NONE).setText(Messages.NewExportPackageDialog_version);
		Text versionT = new Text(composite, SWT.BORDER);
		versionT.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		versionT.setText(ep.getVersion()==null?"":ep.getVersion()); //$NON-NLS-1$
		
		nameT.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				ep.setName(((Text)e.widget).getText());
				getButton(OK).setEnabled(validate());
			}
		});
		versionT.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				ep.setVersion(((Text)e.widget).getText());
				getButton(OK).setEnabled(validate());
			}
		});		
		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(OK).setEnabled(false);
	}
	
	@Override
	protected void okPressed() {
		super.okPressed();
	}

	private boolean validate() {
		int valid = ep.isValid();
		switch (valid) {
		case ExportPackage.NAME_NULL:
			setErrorMessage(Messages.NewExportPackageDialog_nameNullError);
			return false;
		case ExportPackage.VERSION_INVALID:
			setErrorMessage(Messages.NewExportPackageDialog_versionInvalidError);
			return false;
		case ExportPackage.OK:
			break;
		default:
			break;
		}
		setErrorMessage(null);
		return true;
	}
	
	public ExportPackage getExportPackage(){
		return ep;
	}
}
