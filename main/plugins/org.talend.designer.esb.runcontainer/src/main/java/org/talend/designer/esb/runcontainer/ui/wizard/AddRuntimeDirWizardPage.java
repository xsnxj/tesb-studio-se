// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.esb.runcontainer.ui.wizard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.designer.esb.runcontainer.i18n.RunContainerMessages;
import org.talend.designer.esb.runcontainer.util.FileUtil;

public class AddRuntimeDirWizardPage extends WizardPage {

    //private final String target;

    //private String rtHome;

    //private Text rtDirText;
    
    private Text txtRuntimeHome;
    
    private Text txtRuntimeArchive;

    //private Label labelVersion;

    private List<String> rtFiles = new ArrayList<String>();

    //private Label labelSize;

    //private Button btnCopyToStudio;
    
    private Button btnUseExistingContainer;
    
    private Button btnInstallNewContainer;

    /**
     * Create the wizard.
     */
    public AddRuntimeDirWizardPage() {
        super(RunContainerMessages.getString("AddRuntimeDirWizardPage.Title")); //$NON-NLS-1$
        //this.target = target;
        setTitle(RunContainerMessages.getString("AddRuntimeDirWizardPage.Title")); //$NON-NLS-1$
        setDescription(RunContainerMessages.getString("AddRuntimeDirWizardPage.Desc")); //$NON-NLS-1$
        rtFiles.add("/bin/trun"); //$NON-NLS-1$
        rtFiles.add("/bin/setenv"); //$NON-NLS-1$
        rtFiles.add("/etc"); //$NON-NLS-1$
        rtFiles.add("/system/org/apache/karaf"); //$NON-NLS-1$
        rtFiles.add("/system/org/talend/esb"); //$NON-NLS-1$
        rtFiles.add("/lib/boot"); //$NON-NLS-1$
        // rtFiles.add("/version.txt");
    }

    /**
     * Create contents of the wizard.
     * 
     * @param parent
     */
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(1, false));

        Composite body = new Composite(container, SWT.NONE);
        body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        body.setLayout(new GridLayout(1, false));
        
        // Runtime Home Location
        
        {
            //Composite compCheck = new Composite(body, SWT.NONE);
            //compCheck.setLayout(new GridLayout(1, false));

            btnUseExistingContainer = new Button(body, SWT.RADIO);
            btnUseExistingContainer.setText("Use existing Local Talend Runtime (ESB OSGi Container)"); //$NON-NLS-1$
            btnUseExistingContainer.setSelection(true);
            btnUseExistingContainer.setEnabled(true);
            btnUseExistingContainer.addSelectionListener(new SelectionAdapter() {
                /* (non-Javadoc)
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
                    validateRuntimeHome();
                }
            });

            Composite compLocation = new Composite(body, SWT.NONE);
            compLocation.setLayout(new GridLayout(3, false));
            compLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            compLocation.setBounds(0, 0, 64, 64);

            Label lblHome = new Label(compLocation, SWT.NONE);
            GridData gd_lblHome = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
            gd_lblHome.widthHint = 116;
            lblHome.setLayoutData(gd_lblHome);
            lblHome.setText(RunContainerMessages.getString("AddRuntimeDirWizardPage.Home")); //$NON-NLS-1$

            txtRuntimeHome = new Text(compLocation, SWT.BORDER);
            txtRuntimeHome.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            txtRuntimeHome.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    // Handle event
                    btnUseExistingContainer.setSelection(true);
                    validateRuntimeHome();
                }
            });
            Button btnNewButton = new Button(compLocation, SWT.NONE);
            btnNewButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {

                    DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN | SWT.SHEET);
                    String rtHome = fileDialog.open();
                    if (rtHome != null) {
                        txtRuntimeHome.setText(rtHome);
                        btnUseExistingContainer.setSelection(true);
                    }
                }
            });
            btnNewButton.setText(RunContainerMessages.getString("AddRuntimeDirWizardPage.DirButton")); //$NON-NLS-1$
        }

        // Runtime Distribution

        {
            //Composite compCheck = new Composite(body, SWT.NONE);
            //compCheck.setLayout(new GridLayout(1, false));

            btnInstallNewContainer = new Button(body, SWT.RADIO);
            btnInstallNewContainer.setText("Install from Talend Runtime (ZIP) or Talend ESB (ZIP)"); //$NON-NLS-1$
            btnInstallNewContainer.setSelection(false);
            btnInstallNewContainer.setEnabled(true);
            btnInstallNewContainer.addSelectionListener(new SelectionAdapter() {
                /* (non-Javadoc)
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
                    validateRuntimeArchive();
                }
            });

            Composite compLocation = new Composite(body, SWT.NONE);
            compLocation.setLayout(new GridLayout(3, false));
            compLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            compLocation.setBounds(0, 0, 64, 64);

            Label lblHome = new Label(compLocation, SWT.NONE);
            GridData gd_lblHome = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
            gd_lblHome.widthHint = 116;
            lblHome.setLayoutData(gd_lblHome);
            lblHome.setText(RunContainerMessages.getString("AddRuntimeDirWizardPage.Install")); //$NON-NLS-1$

            txtRuntimeArchive = new Text(compLocation, SWT.BORDER);
            txtRuntimeArchive.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            txtRuntimeArchive.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    // Handle event
                    btnInstallNewContainer.setSelection(true);
                    validateRuntimeArchive();
                }
            });
            Button btnNewButton = new Button(compLocation, SWT.NONE);
            btnNewButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {

                    FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
                    fileDialog.setFilterExtensions(new String[] { "*.zip" });
                    String rtHome = fileDialog.open();
                    if (rtHome != null) {
                        txtRuntimeArchive.setText(rtHome);
                        btnInstallNewContainer.setSelection(true);
                    }
                }
            });
            btnNewButton.setText(RunContainerMessages.getString("AddRuntimeDirWizardPage.FileButton")); //$NON-NLS-1$
        }
        
        // Old stuff

        /*
        {
            Composite compLocation = new Composite(body, SWT.NONE);
            compLocation.setLayout(new GridLayout(3, false));
            compLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            compLocation.setBounds(0, 0, 64, 64);

            Label lblHome = new Label(compLocation, SWT.NONE);
            GridData gd_lblHome = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
            gd_lblHome.widthHint = 116;
            lblHome.setLayoutData(gd_lblHome);
            lblHome.setText(RunContainerMessages.getString("AddRuntimeDirWizardPage.Home")); //$NON-NLS-1$

            rtDirText = new Text(compLocation, SWT.BORDER);
            rtDirText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            rtDirText.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    // Handle event
                    validate();
                }
            });
            Button btnNewButton = new Button(compLocation, SWT.NONE);
            btnNewButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {

                    DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN | SWT.SHEET);
                    String rtHome = fileDialog.open();
                    if (rtHome != null) {
                        rtDirText.setText(rtHome);
                    }
                }
            });
            btnNewButton.setText(RunContainerMessages.getString("AddRuntimeDirWizardPage.DirButton")); //$NON-NLS-1$
        }
        
        // Copy check button
        
        {
            Composite compCheck = new Composite(body, SWT.NONE);
            compCheck.setLayout(new GridLayout(2, false));

            btnCopyToStudio = new Button(compCheck, SWT.CHECK);
            btnCopyToStudio.setText(RunContainerMessages.getString("AddRuntimeDirWizardPage.IfCopy")); //$NON-NLS-1$
            btnCopyToStudio.setSelection(true);
            btnCopyToStudio.setEnabled(true);

            Label blank = new Label(compCheck, SWT.NONE);
            blank.setText(""); //$NON-NLS-1$

            Label labelStudioPath = new Label(compCheck, SWT.NONE);
            labelStudioPath.setText(target);
        }
        
        // Info
        
        {
            Composite compInfo = new Composite(body, SWT.NONE);
            compInfo.setLayout(new GridLayout(2, false));
            compInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

            Label lblSpace = new Label(compInfo, SWT.NONE);
            lblSpace.setText(RunContainerMessages.getString("AddRuntimeDirWizardPage.Space")); //$NON-NLS-1$

            Label labelSpaceSize = new Label(compInfo, SWT.NONE);
            labelSpaceSize.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
            labelSpaceSize.setText(new File(System.getProperty("user.dir")).getFreeSpace() / 1024 / 1024 + " MB"); //$NON-NLS-1$ //$NON-NLS-2$

            Label lblRuntimeServerVersion = new Label(compInfo, SWT.NONE);
            lblRuntimeServerVersion.setText(RunContainerMessages.getString("AddRuntimeDirWizardPage.Version")); //$NON-NLS-1$

            labelVersion = new Label(compInfo, SWT.NONE);
            labelVersion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
            labelVersion.setText(""); //$NON-NLS-1$

            Label lblRtSize = new Label(compInfo, SWT.NONE);
            lblRtSize.setText(RunContainerMessages.getString("AddRuntimeDirWizardPage.Size")); //$NON-NLS-1$

            labelSize = new Label(compInfo, SWT.NONE);
            labelSize.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
            labelSize.setText(""); //$NON-NLS-1$
        }
        */

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
     */
    // @Override
    // public boolean isPageComplete() {
    // // TODO Auto-generated method stub
    // return super.isPageComplete();
    // }
/*
    private long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();

        int count = files.length;

        for (int i = 0; i < count; i++) {
            if (files[i].isFile()) {
                length += files[i].length();
            } else {
                length += getFolderSize(files[i]);
            }
        }
        return length;
    }*/
    
    private void validateRuntimeArchive() {
        boolean isValid = FileUtil.isContainerArchive(txtRuntimeArchive.getText());
        setPageComplete(isValid);
        setErrorMessage(isValid ? null : "Zip file is not valid Talend ESB or Runtime"); 
    }

    private void validateRuntimeHome() {
        //labelVersion.setText(""); //$NON-NLS-1$
        //labelSize.setText(""); //$NON-NLS-1$
        //labelVersion.getParent().layout();
        String rtHome = txtRuntimeHome.getText();
        // validate, 1st version, 2nd etc
        boolean validated = true;
        String errorMsg = RunContainerMessages.getString("AddRuntimeDirWizardPage.ErrorFind"); //$NON-NLS-1$
        if (rtHome != null) {
            rtHome = rtHome.trim();
            if (rtHome.length() > 0) {
                File rtDir = new File(rtHome);
                // find version.txt, 1st in root, second check in container folder
                if (rtDir.isDirectory()) {
                    File version = new File(rtHome + "/version.txt"); //$NON-NLS-1$
                    String ver = ""; //$NON-NLS-1$
                    try {
                        if (version.exists()) {
                            ver = Files.readAllLines(version.toPath()).get(0);
                        } else {
                            version = new File(rtHome + "/container/version.txt"); //$NON-NLS-1$
                            if (version.exists()) {
                                ver = Files.readAllLines(version.toPath()).get(0);
                                rtHome += "/container/"; //$NON-NLS-1$
                            }
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    if (ver.isEmpty()) {
                        validated = false;
                    } else {
                        //labelVersion.setText(ver);
                        //long size = getFolderSize(new File(rtHome)) / 1024 / 1024;
                        //labelSize.setText(size + " MB"); //$NON-NLS-1$
                        //labelVersion.getParent().layout();

                        for (String f : rtFiles) {
                            File resFile = new File(rtHome + f);
                            if (!resFile.exists()) {
                                validated = false;
                                errorMsg = RunContainerMessages.getString("AddRuntimeDirWizardPage.ErrorDir"); //$NON-NLS-1$
                                break;
                            }
                        }
                    }
                } else {
                    validated = false;
                }
            } else {
                validated = false;
            }

            if (validated) {
                setErrorMessage(null);
                setPageComplete(true);
            } else {
                setErrorMessage(errorMsg);
                setPageComplete(false);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
     */
    @Override
    public boolean canFlipToNextPage() {
        // TODO Auto-generated method stub
        return true;
    }

    public String getRuntimeHome() {
        return txtRuntimeHome.getText();
    }
    
    public String getRuntimeArchive() {
        return txtRuntimeArchive.getText();
    }
    
    public boolean shouldInstallNewContainer() {
        return btnInstallNewContainer.getSelection();
    }
}
