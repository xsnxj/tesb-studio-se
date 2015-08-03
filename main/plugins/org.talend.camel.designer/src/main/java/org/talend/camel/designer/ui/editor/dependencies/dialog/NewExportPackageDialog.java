package org.talend.camel.designer.ui.editor.dependencies.dialog;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.talend.camel.designer.CamelDesignerPlugin;
import org.talend.camel.designer.ui.editor.dependencies.Messages;
import org.talend.designer.camel.dependencies.core.model.AbstractDependencyItem;
import org.talend.designer.camel.dependencies.core.model.ExportPackage;

/**
 * Dialog for set export package properties.
 */
public class NewExportPackageDialog extends TitleAreaDialog {

	// name regular expression
	/** The Constant NAME_PATTERN. */
	protected static final String NAME_PATTERN = "[^\\s;=\"\\[\\]\\(\\),:|]+"; //$NON-NLS-1$
		
	/** The ep. */
	private ExportPackage ep = null;

	/** The is edit. */
	private boolean isEdit = false;

	/** The input. */
	private List<?> input;

	/** The origin. */
	private ExportPackage origin;

	/** The version part. */
	private DependencyVersionPart fVersionPart;
	
	/** The name t. */
	private Text nameT;


	/**
	 * Instantiates a new new export package dialog.
	 *
	 * @param parentShell the parent shell
	 * @param input the input
	 */
	public NewExportPackageDialog(Shell parentShell, List<?> input) {
		this(parentShell, null, input);
	}

	/**
	 * Instantiates a new new export package dialog.
	 *
	 * @param parentShell the parent shell
	 * @param origin the origin
	 * @param input the input
	 */
	public NewExportPackageDialog(Shell parentShell, ExportPackage origin,
			List<?> input) {
		super(parentShell);
		this.origin = origin;
		this.isEdit= (origin!=null);
		this.ep = new ExportPackage(origin);
		this.input = input;

		fVersionPart = new DependencyVersionPart(false) {
			@Override
			protected String getGroupText() {
				return Messages.NewExportPackageDialog_exportGroupText;
			}
		};
		fVersionPart.setVersion(ep.getVersion());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(Messages.NewExportPackageDialog_dialogTitle);
		setTitle(isEdit ? Messages.NewExportPackageDialog_editTitle
				: Messages.NewExportPackageDialog_addTitle);
		setMessage(isEdit ? Messages.NewExportPackageDialog_editMsg
				: Messages.NewExportPackageDialog_addMsg);

		parent.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NONE)
				.setText(Messages.NewExportPackageDialog_name);
		nameT = new Text(composite, SWT.BORDER);
		nameT.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameT.setText(ep.getName() == null ? "" : ep.getName()); //$NON-NLS-1$
		nameT.selectAll();
		
		fVersionPart.createVersionFields(parent, true, true);

		ModifyListener modifyListener = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				getButton(OK).setEnabled(validate());
			}
		};
		nameT.addModifyListener(modifyListener);
		fVersionPart.addListeners(modifyListener, null);
		
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(OK).setEnabled(false);
	}

	/**
	 * Validate.
	 *
	 * @return true, if successful
	 */
	private boolean validate() {
		IStatus status=validateName();
		if(status.isOK()) {
			status=fVersionPart.validateFullVersionRangeText();
		}
		if(!status.isOK()) {
			setErrorMessage(status.getMessage());
			return false;
		}
		
		ep.setName(getPackageName());
		ep.setVersion(fVersionPart.getVersion());

		if (isEdit) {
			if (ep.strictEqual(origin)) {
				setErrorMessage(null);
				return false;
			}
		}
		setErrorMessage(null);
		return true;
	}

	/**
	 * Validate name.
	 *
	 * @return the i status
	 */
	private IStatus validateName() {
		String name=getPackageName();
		if(origin!=null&&name.equals(origin.getName())) {
			return Status.OK_STATUS;
		}
		for (Object o : input) {
			if (!(o instanceof AbstractDependencyItem)) {
				continue;
			}
			AbstractDependencyItem e = (AbstractDependencyItem) o;
			if (name.equals(e.getName())) {
				return new Status(IStatus.ERROR, CamelDesignerPlugin.PLUGIN_ID,
						Messages.NewExportPackageDialog_nameAlreadyExist);
			}
		}
		
		if(name == null || name.trim().equals("")){ //$NON-NLS-1$
			return new Status(IStatus.ERROR, CamelDesignerPlugin.PLUGIN_ID, Messages.NewExportPackageDialog_nameNullError);
		}
		
		if(!name.matches(NAME_PATTERN)) {
			return new Status(IStatus.ERROR, CamelDesignerPlugin.PLUGIN_ID, Messages.NewOrEditDependencyDialog_nameInvalidMsg);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Gets the package name.
	 *
	 * @return the package name
	 */
	private String getPackageName() {
		return nameT.getText().trim();
	}

	/**
	 * Gets the export package.
	 *
	 * @return the export package
	 */
	public ExportPackage getExportPackage() {
		return ep;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#getInitialSize()
	 */
	protected Point getInitialSize() {
		Point computeSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT,
				true);
		computeSize.x += 100;
		return computeSize;
	}
}
