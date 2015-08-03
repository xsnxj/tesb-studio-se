package org.talend.camel.designer.ui.editor.dependencies.dialog;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.talend.camel.designer.CamelDesignerPlugin;
import org.talend.camel.designer.ui.editor.dependencies.Messages;
import org.talend.designer.camel.dependencies.core.model.IDependencyItem;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.OsgiDependencies;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;

/**
 * Dialog for create/edit bundle/package dependency.
 */
public class NewOrEditDependencyDialog extends TitleAreaDialog {

	// name regular expression
	/** The Constant NAME_PATTERN. */
	protected static final String NAME_PATTERN = "[^\\s;=\"\\[\\]\\(\\),:|]+"; //$NON-NLS-1$

	/** The type. */
	private int type;
	
	/** The item. */
	private OsgiDependencies<?> item;
	
	/** The input. */
	private List<?> input;

	/** The is new. */
	private boolean isNew = false;
	
	/** The origin. */
	private OsgiDependencies<?> origin;

	/** The name text. */
	private Text fNameText;
	
	/** The optional btn. */
	private Button fOptionalBtn;
	
	/** The version part. */
	private DependencyVersionPart fVersionPart;

	/**
	 * Instantiates a new new or edit dependency dialog. 
	 * Use for create a new dependency
	 *
	 * @param input the input
	 * @param parentShell the parent shell
	 * @param type the type
	 */
	public NewOrEditDependencyDialog(List<?> input, Shell parentShell, int type) {
		this(input, null, parentShell, type);
	}

	/**
	 * Instantiates a new new or edit dependency dialog.
	 * Use for edit an exist dependency
	 *
	 * @param input the input
	 * @param sourceItem the source item
	 * @param parentShell the parent shell
	 * @param type the type
	 */
	public NewOrEditDependencyDialog(List<?> input,
			OsgiDependencies<?> sourceItem, Shell parentShell, int type) {
		super(parentShell);

		this.input = input;
		this.origin = sourceItem;
		this.isNew = (origin == null);
		this.type = type;
		switch (type) {
		case IDependencyItem.IMPORT_PACKAGE:
			this.item = new ImportPackage((ImportPackage) sourceItem);
			break;
		case IDependencyItem.REQUIRE_BUNDLE:
			this.item = new RequireBundle((RequireBundle) sourceItem);
			break;
		default:
			break;
		}

		fVersionPart = new DependencyVersionPart(true);
		if (!isNew) {
			fVersionPart.setVersion(origin.getVersionRange());
		}
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(String version) {
		fVersionPart.setVersion(version);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		if (isNew) {
			switch (type) {
			case IDependencyItem.IMPORT_PACKAGE:
				getShell().setText(
						Messages.NewDependencyItemDialog_addImportPackage);
				setTitle(Messages.NewDependencyItemDialog_importPackage);
				setMessage(Messages.NewDependencyItemDialog_importPackageMessage);
				break;
			case IDependencyItem.REQUIRE_BUNDLE:
				getShell().setText(
						Messages.NewDependencyItemDialog_addRequireBundle);
				setTitle(Messages.NewDependencyItemDialog_requireBundle);
				setMessage(Messages.NewDependencyItemDialog_addRequireBundleMsg);
				break;

			default:
				break;
			}
		} else {
			switch (type) {
			case IDependencyItem.IMPORT_PACKAGE:
				getShell()
						.setText(
								Messages.NewDependencyItemDialog_editImportPackageTitle);
				setTitle(Messages.NewDependencyItemDialog_importPackage);
				setMessage(Messages.NewDependencyItemDialog_editImportPackageMsg);
				break;
			case IDependencyItem.REQUIRE_BUNDLE:
				getShell()
						.setText(
								Messages.NewDependencyItemDialog_editRequireBundleTitle);
				setTitle(Messages.NewDependencyItemDialog_requireBundle);
				setMessage(Messages.NewDependencyItemDialog_editRequireBundleMsg);
				break;

			default:
				break;
			}
		}

		parent.setLayout(new GridLayout(1, false));

		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));

		new Label(c, SWT.NONE).setText(Messages.NewDependencyItemDialog_name);
		fNameText = new Text(c, SWT.BORDER);
		GridData tGridData = new GridData(GridData.FILL_HORIZONTAL);
		fNameText.setLayoutData(tGridData);

		Group propertiesGroup = new Group(parent, SWT.NONE);
		propertiesGroup.setText(Messages.NewOrEditDependencyDialog_properties);
		propertiesGroup.setLayout(new GridLayout());
		propertiesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fOptionalBtn = new Button(propertiesGroup, SWT.CHECK);
		fOptionalBtn.setText(Messages.NewDependencyItemDialog_optional);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		fOptionalBtn.setLayoutData(gd);

		fVersionPart.createVersionFields(parent, true, true);

		preloadFields();
		addListeners();
		return c;
	}

	/**
	 * Preload fields.
	 */
	private void preloadFields() {

		if (!isNew) {
			fNameText.setText(item.getName() == null ? "" : item.getName()); //$NON-NLS-1$
			fOptionalBtn.setSelection(item.isOptional());
		}
		fNameText.selectAll();
		fNameText.setFocus();
	}

	/**
	 * Adds the listeners.
	 */
	private void addListeners() {
		ModifyListener modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getButton(OK).setEnabled(validate());
			}
		};
		fNameText.addModifyListener(modifyListener);

		SelectionListener selectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getButton(OK).setEnabled(validate());
			}
		};
		fOptionalBtn.addSelectionListener(selectionListener);

		fVersionPart.addListeners(modifyListener, selectionListener);
	}

	/**
	 * Validate.
	 * 
	 * @return true, if successful valid
	 */
	private boolean validate() {
		IStatus status = validateName();
		if (status.isOK()) {
			status = fVersionPart.validateFullVersionRangeText();
		}
		if (!status.isOK()) {
			setErrorMessage(status.getMessage());
			return false;
		}
		item.setOptional(getOptional());
		item.setName(getDependencyName());
		item.setVersionRange(getVersion());
		if (item.strictEqual(origin)) {
			setErrorMessage(null);
			// nothing changes.
			return false;
		}
		setErrorMessage(null);
		return true;
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
	 * Gets the dependency item.
	 *
	 * @return the dependency item
	 */
	public OsgiDependencies<?> getDependencyItem() {
		return item;
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

	/**
	 * Gets the dependency name.
	 *
	 * @return the dependency name
	 */
	private String getDependencyName() {
		return fNameText.getText().trim();
	}

	/**
	 * Gets the optional.
	 *
	 * @return the optional
	 */
	private boolean getOptional() {
		return fOptionalBtn.getSelection();
	}

	/**
	 * Validate name.
	 *
	 * @return the i status
	 */
	public IStatus validateName() {
		String name = getDependencyName();
		if (origin != null && name.equals(origin.getName())) {
			return Status.OK_STATUS;
		}
		for (Object o : input) {
			if (!(o instanceof OsgiDependencies<?>)) {
				continue;
			}
			OsgiDependencies<?> e = (OsgiDependencies<?>) o;
			if (name.equals(e.getName())) {
				return new Status(IStatus.ERROR, CamelDesignerPlugin.PLUGIN_ID,
						Messages.NewDependencyItemDialog_existCheckMessage);
			}
		}
		if (!name.matches(NAME_PATTERN)) {
			return new Status(IStatus.ERROR, CamelDesignerPlugin.PLUGIN_ID,
					Messages.NewOrEditDependencyDialog_nameInvalidMsg);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return fVersionPart.getVersion();
	}
}
