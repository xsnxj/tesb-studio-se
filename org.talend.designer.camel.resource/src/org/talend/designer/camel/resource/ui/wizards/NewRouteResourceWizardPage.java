// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
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
	private IPath filePath;

	/**
	 * Constructs a new NewProjectWizardPage.
	 * 
	 */
	public NewRouteResourceWizardPage(Property property, IPath destinationPath) {
		super("WizardPage", property, destinationPath); //$NON-NLS-1$

		setTitle("Create Route Resource"); //$NON-NLS-1$
		setDescription("Create a new Route Resource");

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

					File file = new File(filename);
					String fileName = file.getName();
					nameText.setText(fileName);
					evaluateFields();
				}
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
		filenameLab.setText("Source File");

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
		browseBtn.setText("Browse"); //$NON-NLS-1$
		browseBtn.setToolTipText("Browse a file from file system."); //$NON-NLS-1$

		super.createControl(container);

		setControl(container);
		updateContent();
		addListeners();
		setPageComplete(false);
	}

	@Override
	protected void evaluateFields() {
		super.evaluateFields();

		filePath = new Path(filenameText.getText());
		NewRouteResourceWizard wizard = (NewRouteResourceWizard) getWizard();
		wizard.setFilePath(filePath);

	}

	public ERepositoryObjectType getRepositoryObjectType() {
		return CamelRepositoryNodeType.repositoryRouteResourceType;
	}

}
