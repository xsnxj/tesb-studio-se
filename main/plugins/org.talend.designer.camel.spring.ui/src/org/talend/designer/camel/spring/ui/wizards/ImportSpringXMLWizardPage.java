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
package org.talend.designer.camel.spring.ui.wizards;

import java.io.File;

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
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.designer.camel.spring.ui.i18n.Messages;
import org.talend.repository.ui.wizards.PropertiesWizardPage;

/**
 * Page for new project details. <br/>
 * 
 * $Id: ImportSpringXMLWizardPage.java 52559 2010-12-13 04:14:06Z nrousseau $
 * 
 */
public class ImportSpringXMLWizardPage extends PropertiesWizardPage {

    private static final String DESC = Messages.getString("ImportSpringXMLWizardPage_desc"); //$NON-NLS-1$

    private static final String DEFAULT_NEW_NAME = Messages.getString("ImportSpringXMLWizardPage_defaultName"); //$NON-NLS-1$

    private Text xmlPathText;
    
    /**
     * Constructs a new NewProjectWizardPage.
     * 
     */
    public ImportSpringXMLWizardPage(Property property, IPath destinationPath) {
        super("WizardPage", property, destinationPath); //$NON-NLS-1$

        setTitle(Messages.getString("ImportSpringXMLWizardPage_title"));  //$NON-NLS-1$
        setDescription(DESC);
        
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        createFileBrowser(container);
        super.createControl(container);

        setControl(container);
        updateContent();
        addListeners();
        setPageComplete(false);
    }

    /**
     * 
     * DOC LiXP Comment method "createFileBrowser".
     * 
     * @param container
     */
    private void createFileBrowser(Composite container) {
        Label label = new Label(container, SWT.NONE);
        label.setText(Messages.getString("ImportSpringXMLWizardPage_filePathLabel")); //$NON-NLS-1$
        Composite composite = new Composite(container, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        xmlPathText = new Text(composite, SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        xmlPathText.setLayoutData(gd);

        Button button = new Button(composite, SWT.PUSH);
        gd = new GridData();
        button.setLayoutData(gd);
        button.setText(Messages.getString("ImportSpringXMLWizardPage_browseBtnLabel")); //$NON-NLS-1$
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                openFileBrowseDialog();
            }
        });
    }

    protected void openFileBrowseDialog() {
        FileDialog dialog = new FileDialog(getControl().getShell(), SWT.OPEN);
        dialog.setFilterPath(xmlPathText.getText());
        String path = dialog.open();
        if (path != null) {
            xmlPathText.setText(path);
           
        }
    }
    
    /**
     * 
     * DOC LiXP Comment method "getDefaultRouteName".
     * @param filePath
     * @return
     */
    private String getDefaultRouteName(String filePath) {
        
        String name = DEFAULT_NEW_NAME;
        int newNamePostfix = 1;
        while(!isValid(name)){
            name = DEFAULT_NEW_NAME + "_" + newNamePostfix; //$NON-NLS-1$
            newNamePostfix++;
        }
        return name;
       
    }

    public ERepositoryObjectType getRepositoryObjectType() {
        //Currently to prevent name validation crashing
        return ERepositoryObjectType.PROCESS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.wizards.PropertiesWizardPage#evaluateTextField()
     */
    protected void evaluateTextField() {

        //Validate XML path first, if fails, show the error message. LiXP
        evaluateXMLPath();
        if (nameStatus.getSeverity() != IStatus.OK) {
            return;
        }
        super.evaluateTextField();

    }

    /**
     * DOC LiXP Comment method "evaluateXMLPath".
     */
    private void evaluateXMLPath() {
        String xmlPath = xmlPathText.getText().trim();
        boolean isValid = xmlPath != null && new File(xmlPath).exists();
        if (!isValid) {
            nameStatus = createStatus(IStatus.ERROR, Messages.getString("ImportSpringXMLWizardPage.errorPathMsg")); //$NON-NLS-1$
            updatePageStatus();
        }else{
            nameStatus = createOkStatus();
        }
        
    }

    @Override
    protected void addListeners() {
        super.addListeners();
        xmlPathText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                evaluateTextField();
                nameText.setText(getDefaultRouteName(xmlPathText.getText()));
            }
        });
    }
    
    /**
     * 
     * DOC LiXP Comment method "getXMLPath".
     * @return
     */
    public String getXMLPath(){
        return xmlPathText.getText();
    }
}
