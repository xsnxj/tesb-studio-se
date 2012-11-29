package org.talend.designer.camel.dependencies.ui.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
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

	private List<?> input;

	private ExportPackage origin;
	
	public NewExportPackageDialog(Shell parentShell, List<?> input) {
		super(parentShell);
		this.isEdit = false;
		this.ep = new ExportPackage();
		this.input = input;
	}
	
	public NewExportPackageDialog(Shell parentShell, ExportPackage origin, List<?> input) {
		super(parentShell);
		this.origin  = origin;
		this.ep = new ExportPackage(origin);
		this.input = input;
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
		nameT.selectAll();
		
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
	
	private boolean validate() {
		if(isEdit){
			if(ep.strictEqual(origin)){
				setErrorMessage(null);
				return false;
			}
		}
		if(nameExist()){
			setErrorMessage(Messages.NewExportPackageDialog_nameAlreadyExist);
			return false;
		}
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
	
	private boolean nameExist(){
		String name = ep.getName();
		/*
		 * if it's editing, then ignore itself
		 * else compare with all items.
		 */
		if(isEdit){
			for(Object o: input){
				if(origin == o){
					continue;
				}
				if(!(o instanceof ExportPackage)){
					continue;
				}
				ExportPackage e = (ExportPackage) o;
				if(name.equals(e.getName())){
					return true;
				}
			}
		}else{
			for(Object o: input){
				if(!(o instanceof ExportPackage)){
					continue;
				}
				ExportPackage e = (ExportPackage) o;
				if(name.equals(e.getName())){
					return true;
				}
			}
		}
		return false;
	}
	
	public ExportPackage getExportPackage(){
		return ep;
	}
	
	protected Point getInitialSize() {
		Point computeSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		computeSize.x += 100;
		return computeSize;
	}
}
