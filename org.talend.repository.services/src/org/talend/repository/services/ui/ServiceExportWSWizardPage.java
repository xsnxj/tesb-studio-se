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
package org.talend.repository.services.ui;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.utils.ESBRepositoryNodeType;

/**
 * DOC x class global comment. Detailled comment <br/>
 *
 */
public class ServiceExportWSWizardPage extends WizardPage {

	private String serviceName;
	private String serviceVersion;
	private String destinationValue;
	private Text destinationText;

    public ServiceExportWSWizardPage(IStructuredSelection selection) {
        super(org.talend.repository.services.Messages.ServiceExportWizard_Wizard_Title);
        @SuppressWarnings("unchecked")
		List<RepositoryNode> nodes = selection.toList();
        serviceName = "";
        serviceVersion = "";
		if (nodes.size() >= 1) {
            RepositoryNode node = nodes.get(0);
            if (node.getType() == ENodeType.REPOSITORY_ELEMENT) {
                IRepositoryViewObject repositoryObject = node.getObject();
                if (node.getProperties(EProperties.CONTENT_TYPE) == ESBRepositoryNodeType.SERVICES) {
                    serviceName = repositoryObject.getLabel();
                    serviceVersion = repositoryObject.getVersion();
                }
            }
        }

    }

    protected void handleDestinationBrowseButtonPressed() {
        FileDialog dialog = new FileDialog(getContainer().getShell(), SWT.SAVE);
        dialog.setFilterExtensions(new String[] { "*.kar", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
        File destination = new File(getDestinationValue());
        dialog.setFileName(destination.getName());
        dialog.setFilterPath(destination.getParent());
        String selectedFileName = dialog.open();
        if (selectedFileName == null) {
            return;
        }
        if (!selectedFileName.endsWith(getOutputSuffix()))
            selectedFileName += getOutputSuffix();
        checkDestination(selectedFileName);
        destinationValue = selectedFileName;
    }

	private void checkDestination(String fileName) {
		File destination;
		destination = new File(fileName);
        if (destination.exists()) {
        	setMessage("Destination file exists and will be overwrited!");
        } else {
        	setMessage(null);
        }
	}

    
	public String getDestinationValue() {
		if (null == destinationValue) {
	        String bundleName = serviceName + "-" + serviceVersion + getOutputSuffix();
	        String userDir = System.getProperty("user.dir"); //$NON-NLS-1$
	        IPath path = new Path(userDir).append(bundleName);
	        destinationValue = path.toOSString();
		}
		return destinationValue;
	}

	protected String getOutputSuffix() {
		return ".kar";
	}

	public void createControl(Composite parent) {
		setControl(parent);
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		container.setLayout(new GridLayout(1, false));

		Group destinationGroup = new Group(container, SWT.NONE);
		destinationGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		destinationGroup.setText("Destination");
		destinationGroup.setLayout(new GridLayout(2, false));

		destinationText = new Text(destinationGroup, SWT.SINGLE | SWT.BORDER);
		destinationText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		destinationText.setText(getDestinationValue());
		destinationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				destinationValue = destinationText.getText();
				checkDestination(destinationValue);
			}
		});
		Button browseButton = new Button(destinationGroup, SWT.PUSH);
		browseButton.setText("Browse");
		browseButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				handleDestinationBrowseButtonPressed();
				destinationText.setText(destinationValue);
			}
			public void widgetDefaultSelected(SelectionEvent e) {;}
		});

	}

}
