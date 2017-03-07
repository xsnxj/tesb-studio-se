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
package org.talend.designer.esb.runcontainer.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.talend.designer.esb.runcontainer.core.ESBRunContainerPlugin;
import org.talend.designer.esb.runcontainer.ui.wizard.AddRuntimeWizard;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 *
 */
public class RunContainerPreferencePage extends FieldLayoutPreferencePage implements IWorkbenchPreferencePage {

    public static final String ID = "org.talend.designer.esb.runcontainer.preferences.ESBRunContainerPreferencePage"; //$NON-NLS-1$

    /**
     * Create the preference page.
     */
    public RunContainerPreferencePage() {
        setTitle("ESB Runtime Server");
    }

    /**
     * Create contents of the preference page.
     * 
     * @param parent
     */
    @Override
    public Control createPageContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout());

        Composite body = new Composite(container, SWT.NONE);
        body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        body.setLayout(new GridLayout(1, false));

        Group groupServer = new Group(body, SWT.NONE);
        groupServer.setText("Connection Information");
        groupServer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        groupServer.setBounds(0, 0, 70, 82);
        groupServer.setLayout(new GridLayout(2, false));

        Composite compSvrBody = new Composite(groupServer, SWT.BORDER);
        GridLayout gl_compSvrBody = new GridLayout(2, false);
        gl_compSvrBody.horizontalSpacing = 0;
        compSvrBody.setLayout(gl_compSvrBody);
        compSvrBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        // Composite compRemote = new Composite(compSvrBody, SWT.NONE);
        // addField(new BooleanFieldEditor("id", "Remote server", BooleanFieldEditor.DEFAULT, compRemote));
        // new Label(compSvrBody, SWT.NONE);

        Label lbLocation = new Label(compSvrBody, SWT.NONE);
        lbLocation.setText("Location:");

        Composite compServer = new Composite(compSvrBody, SWT.NONE);
        compServer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compServer.setSize(230, 25);
        {

            DirectoryFieldEditor directoryFieldEditor = new DirectoryFieldEditor(
                    RunContainerPreferenceInitializer.P_ESB_RUNTIME_LOCATION, "Location:", compServer);
            directoryFieldEditor.getLabelControl(compServer).setText("");
            addField(directoryFieldEditor);
        }

        Label lblHost = new Label(compSvrBody, SWT.NONE);
        lblHost.setText("Host:");

        Composite compHost = new Composite(compSvrBody, SWT.NONE);
        compHost.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        {
            StringFieldEditor stringFieldEditor = new StringFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_HOST,
                    "Host:", -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, compHost);
            stringFieldEditor.getLabelControl(compHost).setText("");
            addField(stringFieldEditor);
        }

        Label lblPost = new Label(compSvrBody, SWT.NONE);
        lblPost.setText("Post:");

        Composite compPort = new Composite(compSvrBody, SWT.NONE);
        compPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        {
            IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(
                    RunContainerPreferenceInitializer.P_ESB_RUNTIME_PORT, "Port:", compPort, -1);
            integerFieldEditor.getLabelControl(compPort).setText("");
            addField(integerFieldEditor);
        }

        Label lblUsername = new Label(compSvrBody, SWT.NONE);
        lblUsername.setText("Username:");

        Composite compUser = new Composite(compSvrBody, SWT.NONE);
        compUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        {
            StringFieldEditor stringFieldEditor = new StringFieldEditor(
                    RunContainerPreferenceInitializer.P_ESB_RUNTIME_USERNAME, "User:", -1,
                    StringFieldEditor.VALIDATE_ON_KEY_STROKE, compUser);
            stringFieldEditor.getLabelControl(compUser).setText("");
            addField(stringFieldEditor);
        }

        Label lblPassword = new Label(compSvrBody, SWT.NONE);
        lblPassword.setText("Password:");

        Composite compPassword = new Composite(compSvrBody, SWT.NONE);
        compPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        {
            StringFieldEditor stringFieldEditor = new StringFieldEditor(
                    RunContainerPreferenceInitializer.P_ESB_RUNTIME_PASSWORD, "Password:", -1,
                    StringFieldEditor.VALIDATE_ON_KEY_STROKE, compPassword);
            stringFieldEditor.getLabelControl(compPassword).setText("");
            addField(stringFieldEditor);
        }

        Composite compBtn = new Composite(groupServer, SWT.NONE);
        compBtn.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
        GridLayout gl_compBtn = new GridLayout(1, false);
        gl_compBtn.marginWidth = 0;
        gl_compBtn.marginHeight = 0;
        gl_compBtn.horizontalSpacing = 0;
        compBtn.setLayout(gl_compBtn);
        Button btnAddSvr = new Button(compBtn, SWT.NONE);
        btnAddSvr.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                AddRuntimeWizard dirWizard = new AddRuntimeWizard();
                WizardDialog wizardDialog = new WizardDialog(getShell(), dirWizard);
                wizardDialog.open();
            }
        });
        btnAddSvr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnAddSvr.setSize(78, 25);
        btnAddSvr.setText("Add Server...");

        Button btnTestConnection = new Button(compBtn, SWT.NONE);
        btnTestConnection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnTestConnection.setText("Server Info...");

        Button btnInitalize = new Button(compBtn, SWT.NONE);
        btnInitalize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnInitalize.setText("Initalize...");

        Group groupEnd = new Group(body, SWT.NONE);
        groupEnd.setText("Running Options");
        groupEnd.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        groupEnd.setBounds(0, 0, 70, 82);
        groupEnd.setLayout(new GridLayout(1, false));

        Composite compScript = new Composite(groupEnd, SWT.NONE);
        addField(new BooleanFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_MAVEN_SCRIPT, "Add Maven Script",
                BooleanFieldEditor.DEFAULT, compScript));

        Composite compCache = new Composite(groupEnd, SWT.NONE);
        GridLayout gl_compCache = new GridLayout(2, false);
        gl_compCache.marginWidth = 0;
        gl_compCache.marginHeight = 0;
        gl_compCache.verticalSpacing = 0;
        compCache.setLayout(gl_compCache);

        Composite compClean = new Composite(compCache, SWT.NONE);
        addField(new BooleanFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_CLEAN_CACHE,
                "Clean cache before running", BooleanFieldEditor.DEFAULT, compClean));

        Button btnNewButton = new Button(compCache, SWT.NONE);
        btnNewButton.setText("Clean now");

        Composite compJmx = new Composite(groupEnd, SWT.NONE);
        addField(new BooleanFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_JMX, "Use JMX for Route Monitoring",
                BooleanFieldEditor.DEFAULT, compJmx));

        Composite compJmxPort = new Composite(groupEnd, SWT.NONE);
        addField(new IntegerFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_JMX_PORT, "Port:", compJmxPort));

        Composite compFilter = new Composite(groupEnd, SWT.NONE);
        addField(new BooleanFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_SYS_LOG, "Filter system logs",
                BooleanFieldEditor.DEFAULT, compFilter));
        return container;
    }

    /**
     * Initialize the preference page.
     */
    public void init(IWorkbench workbench) {
        // Initialize the preference page
        setPreferenceStore(ESBRunContainerPlugin.getDefault().getPreferenceStore());
    }

}