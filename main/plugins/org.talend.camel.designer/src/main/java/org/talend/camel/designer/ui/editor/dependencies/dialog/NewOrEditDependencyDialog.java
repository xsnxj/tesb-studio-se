package org.talend.camel.designer.ui.editor.dependencies.dialog;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
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
import org.talend.camel.designer.ui.editor.dependencies.Messages;
import org.talend.designer.camel.dependencies.core.model.ExportPackage;
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
	private static final Pattern NAME_PATTERN = Pattern.compile("[^\\s;=\"\\[\\]\\(\\),:|]+"); //$NON-NLS-1$

	/** The input. */
	private final Collection<? extends IDependencyItem> input;

	/** The origin. */
	private final OsgiDependencies origin;

    /** The type. */
    private final int type;

	/** The name text. */
	private Text fNameText;
	
	/** The optional btn. */
	private Button fOptionalBtn;
	
	/** The version part. */
	private DependencyVersionPart fVersionPart;

    private final OsgiDependencies item;

    /**
	 * Instantiates a new new or edit dependency dialog. 
	 * Use for create a new dependency
	 *
	 * @param input the input
	 * @param parentShell the parent shell
	 * @param type the type
	 */
	public NewOrEditDependencyDialog(Collection<? extends IDependencyItem> input, Shell parentShell, int type) {
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
	public NewOrEditDependencyDialog(Collection<? extends IDependencyItem> input,
			OsgiDependencies sourceItem, Shell parentShell, int type) {
		super(parentShell);

		this.input = input;
		this.origin = sourceItem;
		this.type = type;

		boolean versionRange = true;
        switch (type) {
        case IDependencyItem.IMPORT_PACKAGE:
            item = new ImportPackage();
            break;
        case IDependencyItem.REQUIRE_BUNDLE:
            item = new RequireBundle();
            break;
        case IDependencyItem.EXPORT_PACKAGE:
            item = new ExportPackage();
            versionRange = false;
            break;
        default:
            item = null;
            break;
        }
        fVersionPart = new DependencyVersionPart(versionRange);
        if (null != origin) {
            fVersionPart.setVersion(origin.getVersion());
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

    private static String getItemName(int type) {
        switch (type) {
        case IDependencyItem.IMPORT_PACKAGE:
            return Messages.NewDependencyItemDialog_ImportPackage;
        case IDependencyItem.REQUIRE_BUNDLE:
            return Messages.NewDependencyItemDialog_RequireBundle;
        case IDependencyItem.EXPORT_PACKAGE:
            return Messages.NewDependencyItemDialog_ExportPackage;
        default:
            return null;
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final String name = getItemName(type);
        final boolean isNew = (origin == null);
        getShell().setText(name);
        setTitle(MessageFormat.format(isNew ? Messages.NewDependencyItemDialog_addTitle
            : Messages.NewDependencyItemDialog_editTitle, name));
        setMessage(MessageFormat.format(isNew ? Messages.NewDependencyItemDialog_addMsg
            : Messages.NewDependencyItemDialog_editMsg, name));

        parent.setLayout(new GridLayout());

        Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(new GridLayout(2, false));
        c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(c, SWT.NONE).setText(Messages.NewDependencyItemDialog_name);
        fNameText = new Text(c, SWT.BORDER);
        fNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if (type != IDependencyItem.EXPORT_PACKAGE) {
            Group propertiesGroup = new Group(parent, SWT.NONE);
            propertiesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            propertiesGroup.setText(Messages.NewOrEditDependencyDialog_properties);
            propertiesGroup.setLayout(new GridLayout());
            fOptionalBtn = new Button(propertiesGroup, SWT.CHECK);
            fOptionalBtn.setText(Messages.NewDependencyItemDialog_optional);
        }

        fVersionPart.createVersionFields(parent, true, true);

        preloadFields();
        addListeners();
        return c;
    }

	/**
	 * Preload fields.
	 */
	private void preloadFields() {
		if (null != origin) {
			fNameText.setText(origin.getName());
			if (null != fOptionalBtn) {
	            fOptionalBtn.setSelection(origin.isOptional());
			}
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
		if (null != fOptionalBtn) {
	        fOptionalBtn.addSelectionListener(selectionListener);
		}

		fVersionPart.addListeners(modifyListener, selectionListener);
	}

	/**
	 * Validate.
	 * 
	 * @return true, if successful valid
	 */
	private boolean validate() {
	    String errorMessage = validateName();
		if (null == errorMessage) {
		    IStatus status = fVersionPart.validateFullVersionRangeText();
	        if (!status.isOK()) {
	            errorMessage = status.getMessage();
	        }
		}
		if (null != errorMessage) {
			setErrorMessage(errorMessage);
			return false;
		}
        item.setName(getDependencyName());
        item.setVersion(getVersion());
        item.setOptional(getOptional());
		if (getDependencyItem().strictEqual(origin)) {
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
	public OsgiDependencies getDependencyItem() {
        return item;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#getInitialSize()
	 */
	protected Point getInitialSize() {
		Point computeSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
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
		return (null != fOptionalBtn) ? fOptionalBtn.getSelection() : false;
	}

	/**
	 * Validate name.
	 *
	 * @return the i status
	 */
	public String validateName() {
		String name = getDependencyName();
		if (origin != null && name.equals(origin.getName())) {
			return null;
		}
		for (IDependencyItem o : input) {
			if (name.equals(o.getName())) {
				return Messages.NewDependencyItemDialog_existCheckMessage;
			}
		}
		if (!NAME_PATTERN.matcher(name).matches()) {
			return Messages.NewDependencyItemDialog_nameInvalidMsg;
		}
		return null;
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
