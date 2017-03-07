// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2013 Talend – www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 *
 * Support user to choice ESB server or runtime server
 *
 */
public class ESBRuntimeDirWizardPage extends WizardPage {

    private Text rtDirText;

    private Label labelVersion;

    private List<String> rtFiles = new ArrayList<String>();

    private Label labelSize;

    /**
     * Create the wizard.
     */
    public ESBRuntimeDirWizardPage() {
        super("Add ESB Runtime Server");
        setTitle("Add ESB Runtime Server");
        setDescription("Please select local runtime installation directory");
        rtFiles.add("/bin/trun");
        rtFiles.add("/bin/setenv");
        rtFiles.add("/etc");
        rtFiles.add("/system/org/apache/karaf");
        rtFiles.add("/system/org/talend/esb");
        rtFiles.add("/lib/boot");
        // rtFiles.add("/version.txt");
    }

    /**
     * Create contents of the wizard.
     * 
     * @param parent
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(1, false));

        Composite body = new Composite(container, SWT.NONE);
        body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        body.setLayout(new GridLayout(1, false));

        Composite compLocation = new Composite(body, SWT.NONE);
        compLocation.setLayout(new GridLayout(3, false));
        compLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compLocation.setBounds(0, 0, 64, 64);

        Label lblHome = new Label(compLocation, SWT.NONE);
        GridData gd_lblHome = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gd_lblHome.widthHint = 116;
        lblHome.setLayoutData(gd_lblHome);
        lblHome.setText("Runtime home:");

        rtDirText = new Text(compLocation, SWT.BORDER);
        rtDirText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        rtDirText.addModifyListener(new ModifyListener() {

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
                rtDirText.setText(rtHome);
            }
        });
        btnNewButton.setText("&Directory...");

        Composite compCheck = new Composite(body, SWT.NONE);
        compCheck.setLayout(new GridLayout(2, false));

        Button btnCopyToStudio = new Button(compCheck, SWT.CHECK);
        btnCopyToStudio.setText("Copy entire runtime server into studio directory and use it");
        btnCopyToStudio.setSelection(true);

        Label blank = new Label(compCheck, SWT.NONE);
        blank.setText("");

        Label labelStudioPath = new Label(compCheck, SWT.NONE);
        labelStudioPath.setText(System.getProperty("user.dir") + File.separator + "esb" + File.separator + "container");
        Composite compInfo = new Composite(body, SWT.NONE);
        compInfo.setLayout(new GridLayout(2, false));
        compInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Label lblSpace = new Label(compInfo, SWT.NONE);
        lblSpace.setText("Available Spaces:");

        Label labelSpaceSize = new Label(compInfo, SWT.NONE);
        labelSpaceSize.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        labelSpaceSize.setText(new File(System.getProperty("user.dir")).getFreeSpace() / 1024 / 1024 + " MB");

        Label lblRuntimeServerVersion = new Label(compInfo, SWT.NONE);
        lblRuntimeServerVersion.setText("Runtime Server Version:");

        labelVersion = new Label(compInfo, SWT.NONE);
        labelVersion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        labelVersion.setText("");

        Label lblRtSize = new Label(compInfo, SWT.NONE);
        lblRtSize.setText("Size:");

        labelSize = new Label(compInfo, SWT.NONE);
        labelSize.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        labelSize.setText("");

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
    }

    private void validate() {
        labelVersion.setText("");
        labelSize.setText("");
        labelVersion.getParent().layout();
        String rtHome = rtDirText.getText();
        // validate, 1st version, 2nd etc
        boolean validated = true;
        String errorMsg = "Cannot find ESB Runtime server in the directory";
        if (rtHome != null) {
            rtHome = rtHome.trim();
            if (rtHome.length() > 0) {
                File rtDir = new File(rtHome);
                // find version.txt, 1st in root, second check in container folder
                if (rtDir.isDirectory()) {
                    File version = new File(rtHome + "/version.txt");
                    String ver = "";
                    try {
                        if (version.exists()) {
                            ver = Files.readAllLines(version.toPath()).get(0);
                        } else {
                            version = new File(rtHome + "/container/version.txt");
                            if (version.exists()) {
                                ver = Files.readAllLines(version.toPath()).get(0);
                                rtHome += "/container/";
                            }
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    if (ver.isEmpty()) {
                        validated = false;
                    } else {
                        labelVersion.setText(ver);
                        long size = getFolderSize(new File(rtHome)) / 1024 / 1024;
                        labelSize.setText(size + " MB");
                        labelVersion.getParent().layout();

                        for (String f : rtFiles) {
                            File resFile = new File(rtHome + f);
                            if (!resFile.exists()) {
                                validated = false;
                                errorMsg = "This is not an complete ESB Runtime server installation";
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
}
