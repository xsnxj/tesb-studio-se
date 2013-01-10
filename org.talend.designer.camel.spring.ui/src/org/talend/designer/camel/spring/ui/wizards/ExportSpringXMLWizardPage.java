// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.spring.ui.wizards;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.designer.camel.spring.ui.i18n.Messages;

/**
 * Page for new project details. <br/>
 * 
 * $Id: ImportSpringXMLWizardPage.java 52559 2010-12-13 04:14:06Z nrousseau $
 * 
 */
public class ExportSpringXMLWizardPage extends WizardPage {

	private Text pathText;

	protected ExportSpringXMLWizardPage(String pageName) {
		super(pageName);
		setTitle(Messages.getString("ExportSpringXMLWizardPage.Title")); //$NON-NLS-1$
		setDescription(Messages
				.getString("ExportSpringXMLWizardPage.Description")); //$NON-NLS-1$
	}

	public void createControl(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.getString("ExportSpringXMLWizardPage.GroupText")); //$NON-NLS-1$
		group.setLayout(new GridLayout(3, false));

		Label pathLabel = new Label(group, SWT.NONE);
		pathLabel.setText(Messages.getString("ExportSpringXMLWizardPage.Path")); //$NON-NLS-1$

		pathText = new Text(group, SWT.BORDER);
		pathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pathText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setPageComplete(validate());
			}
		});

		Button pathBtn = new Button(group, SWT.PUSH);
		pathBtn.setText(Messages.getString("ExportSpringXMLWizardPage.Browse")); //$NON-NLS-1$
		pathBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openAndSpecifyOutput();
			}
		});

		setControl(group);
	}

	@Override
	public boolean isPageComplete() {
		return validate();
	}

	protected void openAndSpecifyOutput() {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
		fileDialog.setFilterPath(pathText.getText());
		fileDialog.setFilterExtensions(new String[] { "*.xml" }); //$NON-NLS-1$
		String open = fileDialog.open();
		if (null != open) {
			pathText.setText(open);
		}
	}

	protected boolean validate() {
		String outputPath = pathText.getText().trim();
		if (outputPath.equals("")) { //$NON-NLS-1$
			setErrorMessage(Messages
					.getString("ExportSpringXMLWizardPage.OutputIsEmpty")); //$NON-NLS-1$
			return false;
		}
		File file = new File(outputPath);
		File parentFile = file.getParentFile();
		if (parentFile == null) {
			setErrorMessage(Messages
					.getString("ExportSpringXMLWizardPage.OutputIsInvalid")); //$NON-NLS-1$
			return false;
		}
		if (!parentFile.exists()) {
			setErrorMessage(Messages
					.getString("ExportSpringXMLWizardPage.OutputFolderError")); //$NON-NLS-1$
			return false;
		}
		IStatus status = ResourcesPlugin.getWorkspace().validateName(
				file.getName(), IResource.FILE);
		if (!status.isOK()) {
			setErrorMessage(status.getMessage());
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	public String getOutputPath() {
		String trim = pathText.getText().trim();
		if (!trim.endsWith(".xml")) { //$NON-NLS-1$
			trim += ".xml"; //$NON-NLS-1$
		}
		return trim;
	}

}
