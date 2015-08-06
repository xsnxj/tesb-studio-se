// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.resource.ui.wizards;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.designer.camel.resource.i18n.Messages;
import org.talend.repository.model.RepositoryConstants;
import org.talend.repository.ui.wizards.PropertiesWizardPage;

/**
 * 
 * New Route Resource Wizard Page
 * 
 * @author xpli
 * 
 */
public class NewRouteResourceWizardPage extends PropertiesWizardPage {

	private Button browseBtn;
	private Text filenameText;
	private URL url;

	/**
	 * Constructs a new NewProjectWizardPage.
	 * 
	 */
	public NewRouteResourceWizardPage(Property property, IPath destinationPath) {
		super("WizardPage", property, destinationPath); //$NON-NLS-1$

		setTitle(Messages.getString("NewRouteResourceWizardPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("NewRouteResourceWizardPage.desc")); //$NON-NLS-1$

	}

	@Override
	protected void addListeners() {
		super.addListeners();
		browseBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
				String filename = dlg.open();
				if (filename != null) {
					filenameText.setText(filename);
				}
			}
		});
		filenameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				File file = new File(filenameText.getText());
				String fileName = file.getName();
				if (nameText.getText().isEmpty()) {
					nameText.setText(fileName); //$NON-NLS-1$ //$NON-NLS-2$
				}
				evaluateFields();
			}
		});
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		GridData data;

		// Source file
		Label filenameLab = new Label(container, SWT.NONE);
		filenameLab.setText(Messages.getString("NewRouteResourceWizardPage.sourceFile")); //$NON-NLS-1$

		Composite filenameContainer = new Composite(container, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = layout.numColumns - 1;
		filenameContainer.setLayoutData(data);
		GridLayout filenameLayout = new GridLayout(2, false);
		filenameLayout.marginHeight = 0;
		filenameLayout.marginWidth = 0;
		filenameContainer.setLayout(filenameLayout);

		filenameText = new Text(filenameContainer, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		filenameText.setLayoutData(data);

		browseBtn = new Button(filenameContainer, SWT.PUSH);
		browseBtn.setText(Messages.getString("NewRouteResourceWizardPage.browse")); //$NON-NLS-1$
		browseBtn.setToolTipText(Messages.getString("NewRouteResourceWizardPage.browseTip")); //$NON-NLS-1$

		super.createControl(container);

		setControl(container);
		updateContent();
		addListeners();
		setPageComplete(false);

		nameText.setFocus();
	}

	@Override
	protected void evaluateTextField() {
		
		super.evaluateTextField();

		boolean isValid = true;
		String text = filenameText.getText();


		// An empty file, allowed
		if (text != null && !text.isEmpty()) {
			File file = new File(text);
			if (!file.exists()) {
				try {
					url = new URL(text);
					InputStream is = url.openStream();
					if (is == null) {
						url = null;
					}
					if (is != null) {
						is.close();
					}
				} catch (Exception e) {
					url = null;
				}
			} else {
				try {
					url = file.toURI().toURL();
				} catch (Exception e) {
					url = null;
				}
			}
			if (url == null) {
				nameStatus = createStatus(IStatus.ERROR,
						Messages.getString("NewRouteResourceWizardPage.errorLoadFile") + text + "."); //$NON-NLS-1$ //$NON-NLS-2$
				isValid = false;
			}
		}

		if (isValid && nameText.getText().isEmpty()) {
			isValid = false;
			nameStatus = createStatus(IStatus.ERROR, Messages.getString("NewRouteResourceWizardPage.nameEmpty")); //$NON-NLS-1$
		}

//		if (isValid
//				&& !Pattern.matches(RepositoryConstants
//						.getPattern(ERepositoryObjectType.PROCESS), nameText
//						.getText()) || nameText.getText().trim().contains(" ")) { //$NON-NLS-1$
//			nameStatus = createStatus(IStatus.ERROR,
//					Messages.getString("NewRouteResourceWizardPage.nameInvalid")); //$NON-NLS-1$
//		}
		if(isValid){
			String namePattern = getRepositoryObjectType().getNamePattern();
			if(namePattern != null){
				boolean matches = Pattern.matches(namePattern, nameText.getText());
				if(!matches){
					nameStatus = createStatus(IStatus.ERROR,
							Messages.getString("NewRouteResourceWizardPage.nameInvalid")); //$NON-NLS-1$
				}
			}
		}
		updatePageStatus();

	}

	public ERepositoryObjectType getRepositoryObjectType() {
		return CamelRepositoryNodeType.repositoryRouteResourceType;
	}

	public URL getUrl() {
		return url;
	}

}
