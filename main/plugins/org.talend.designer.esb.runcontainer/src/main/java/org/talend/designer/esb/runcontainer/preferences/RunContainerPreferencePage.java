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
package org.talend.designer.esb.runcontainer.preferences;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.window.Window;
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
import org.talend.designer.esb.runcontainer.i18n.RunContainerMessages;
import org.talend.designer.esb.runcontainer.server.RuntimeServerController;
import org.talend.designer.esb.runcontainer.ui.actions.StartRuntimeAction;
import org.talend.designer.esb.runcontainer.ui.wizard.AddRuntimeWizard;
import org.talend.designer.esb.runcontainer.util.FileUtil;

/**
 * ESB Runtime pref page
 *
 */
public class RunContainerPreferencePage extends FieldLayoutPreferencePage implements IWorkbenchPreferencePage {

    public static final String ID = "org.talend.designer.esb.runcontainer.preferences.ESBRunContainerPreferencePage"; //$NON-NLS-1$

    /**
     * Create the preference page.
     */
    public RunContainerPreferencePage() {
        setTitle(RunContainerMessages.getString("RunContainerPreferencePage.Title")); //$NON-NLS-1$
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
        groupServer.setText(RunContainerMessages.getString("RunContainerPreferencePage.Group1")); //$NON-NLS-1$
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
        lbLocation.setText(RunContainerMessages.getString("RunContainerPreferencePage.Location")); //$NON-NLS-1$

        Composite compServer = new Composite(compSvrBody, SWT.NONE);
        compServer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        StringFieldEditor locationEditor = new StringFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_LOCATION,
                "", compServer); //$NON-NLS-1$
        locationEditor.setEnabled(true, compServer);
        addField(locationEditor);
        compServer.setSize(230, 25);

        Label lblHost = new Label(compSvrBody, SWT.NONE);
        lblHost.setText(RunContainerMessages.getString("RunContainerPreferencePage.Host")); //$NON-NLS-1$

        Composite compHost = new Composite(compSvrBody, SWT.NONE);
        compHost.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        StringFieldEditor hostFieldEditor = new StringFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_HOST, "", //$NON-NLS-1$
                -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, compHost);
        addField(hostFieldEditor);

        Label lblPost = new Label(compSvrBody, SWT.NONE);
        lblPost.setText(RunContainerMessages.getString("RunContainerPreferencePage.Post")); //$NON-NLS-1$

        Composite compPort = new Composite(compSvrBody, SWT.NONE);
        compPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        IntegerFieldEditor portFieldEditor = new IntegerFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_PORT,
                "", compPort, -1); //$NON-NLS-1$
        addField(portFieldEditor);

        Label lblUsername = new Label(compSvrBody, SWT.NONE);
        lblUsername.setText(RunContainerMessages.getString("RunContainerPreferencePage.Username")); //$NON-NLS-1$

        Composite compUser = new Composite(compSvrBody, SWT.NONE);
        compUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        StringFieldEditor userFieldEditor = new StringFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_USERNAME,
                "", -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, compUser); //$NON-NLS-1$
        addField(userFieldEditor);

        Label lblPassword = new Label(compSvrBody, SWT.NONE);
        lblPassword.setText(RunContainerMessages.getString("RunContainerPreferencePage.Password")); //$NON-NLS-1$

        Composite compPassword = new Composite(compSvrBody, SWT.NONE);
        compPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        StringFieldEditor passwordFieldEditor = new StringFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_PASSWORD,
                "", -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, compPassword); //$NON-NLS-1$
        addField(passwordFieldEditor);

        Label lblInstance = new Label(compSvrBody, SWT.NONE);
        lblInstance.setText(RunContainerMessages.getString("RunContainerPreferencePage.Instance")); //$NON-NLS-1$

        Composite compInstance = new Composite(compSvrBody, SWT.NONE);
        compInstance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        StringFieldEditor instanceFieldEditor = new StringFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_INSTANCE,
                "", -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, compInstance); //$NON-NLS-1$
        addField(instanceFieldEditor);

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
                AddRuntimeWizard dirWizard = new AddRuntimeWizard(locationEditor.getStringValue());
                dirWizard.setNeedsProgressMonitor(true);
                WizardDialog wizardDialog = new WizardDialog(getShell(), dirWizard);
                if (wizardDialog.open() == Window.OK) {
                    locationEditor.setStringValue(dirWizard.getTarget());
                }
            }
        });
        btnAddSvr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnAddSvr.setSize(78, 25);
        btnAddSvr.setText(RunContainerMessages.getString("RunContainerPreferencePage.ServerButton")); //$NON-NLS-1$

        // Button btnTestConnection = new Button(compBtn, SWT.NONE);
        // btnTestConnection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        // btnTestConnection.setText("Server Info...");

        Button btnInitalize = new Button(compBtn, SWT.NONE);
        btnInitalize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnInitalize.setText(RunContainerMessages.getString("RunContainerPreferencePage.InitalizeButton")); //$NON-NLS-1$
        btnInitalize.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    new StartRuntimeAction().run();
                    if (RuntimeServerController.getInstance().isRunning()) {
                        Process startAll = RuntimeServerController.getInstance().startClient(locationEditor.getStringValue(),
                                hostFieldEditor.getStringValue(), userFieldEditor.getStringValue(),
                                passwordFieldEditor.getStringValue(), "tesb:start-all"); //$NON-NLS-1$
                        startAll.waitFor();
                        Process amq = RuntimeServerController.getInstance().startClient(locationEditor.getStringValue(),
                                hostFieldEditor.getStringValue(), userFieldEditor.getStringValue(),
                                passwordFieldEditor.getStringValue(), "feature:install activemq-broker"); //$NON-NLS-1$
                        amq.waitFor();
                        MessageDialog.openInformation(getShell(),
                                RunContainerMessages.getString("RunContainerPreferencePage.InitalizeMessage"), //$NON-NLS-1$
                                RunContainerMessages.getString("RunContainerPreferencePage.InitailzeDialog1")); //$NON-NLS-1$
                    } else {
                        MessageDialog.openError(
                                getShell(),
                                RunContainerMessages.getString("RunContainerPreferencePage.InitailzeDialog2"), RunContainerMessages.getString("RunContainerPreferencePage.InitailzeDialog3")); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                } catch (Exception e1) {
                    MessageDialog.openError(
                            getShell(),
                            RunContainerMessages.getString("RunContainerPreferencePage.InitailzeDialog4"), RunContainerMessages.getString("RunContainerPreferencePage.InitailzeDialog5") + e1); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        });

        Group groupEnd = new Group(body, SWT.NONE);
        groupEnd.setText(RunContainerMessages.getString("RunContainerPreferencePage.Group2")); //$NON-NLS-1$
        groupEnd.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        groupEnd.setBounds(0, 0, 70, 82);
        groupEnd.setLayout(new GridLayout(1, false));

        //Composite compScript = new Composite(groupEnd, SWT.NONE);
        //addField(new BooleanFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_MAVEN_SCRIPT,
          //      RunContainerMessages.getString("RunContainerPreferencePage.MavenScript"), //$NON-NLS-1$
            //    BooleanFieldEditor.DEFAULT, compScript));

        Composite compCache = new Composite(groupEnd, SWT.NONE);
        GridLayout gl_compCache = new GridLayout(2, false);
        gl_compCache.marginWidth = 0;
        gl_compCache.marginHeight = 0;
        gl_compCache.verticalSpacing = 0;
        compCache.setLayout(gl_compCache);

        Composite compClean = new Composite(compCache, SWT.NONE);
        addField(new BooleanFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_CLEAN_CACHE,
                RunContainerMessages.getString("RunContainerPreferencePage.Clean"), BooleanFieldEditor.DEFAULT, compClean)); //$NON-NLS-1$

        Button btnCleanButton = new Button(compCache, SWT.NONE);
        btnCleanButton.setText(RunContainerMessages.getString("RunContainerPreferencePage.CleanButton")); //$NON-NLS-1$
        btnCleanButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (MessageDialog.openConfirm(
                        getShell(),
                        RunContainerMessages.getString("RunContainerPreferencePage.CleanCheckbox"), RunContainerMessages.getString("RunContainerPreferencePage.CleanConfirm"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    if (new File(locationEditor.getStringValue() + "/data").exists()) { //$NON-NLS-1$

                        try {
                            FileUtil.deleteFolder(locationEditor.getStringValue() + "/data"); //$NON-NLS-1$
                        } catch (Exception x) {
                            MessageDialog.openError(getShell(),
                                    RunContainerMessages.getString("RunContainerPreferencePage.CleanFailed1"), //$NON-NLS-1$
                                    RunContainerMessages.getString("RunContainerPreferencePage.CleanFailed2") + x); //$NON-NLS-1$
                        }
                    } else {
                        MessageDialog.openError(getShell(),
                                RunContainerMessages.getString("RunContainerPreferencePage.CleanFailed3"), //$NON-NLS-1$
                                RunContainerMessages.getString("RunContainerPreferencePage.CleanFailed4")); //$NON-NLS-1$
                    }
                }
            }
        });

        Composite compJmx = new Composite(groupEnd, SWT.NONE);
        addField(new BooleanFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_JMX,
                RunContainerMessages.getString("RunContainerPreferencePage.UseJMX"), //$NON-NLS-1$
                BooleanFieldEditor.DEFAULT, compJmx));

        Composite compJmxPort = new Composite(groupEnd, SWT.NONE);
        addField(new IntegerFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_JMX_PORT,
                RunContainerMessages.getString("RunContainerPreferencePage.Port"), compJmxPort)); //$NON-NLS-1$

        Composite compFilter = new Composite(groupEnd, SWT.NONE);
        addField(new BooleanFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_SYS_LOG,
                RunContainerMessages.getString("RunContainerPreferencePage.FilterLogs"), //$NON-NLS-1$
                BooleanFieldEditor.DEFAULT, compFilter));
        return container;
    }

    /**
     * Initialize the preference page.
     */
    @Override
    public void init(IWorkbench workbench) {
        // Initialize the preference page
        setPreferenceStore(ESBRunContainerPlugin.getDefault().getPreferenceStore());
    }
}