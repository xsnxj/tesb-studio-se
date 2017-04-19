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
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
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
import org.talend.designer.esb.runcontainer.ui.actions.HaltRuntimeAction;
import org.talend.designer.esb.runcontainer.ui.actions.StartRuntimeAction;
import org.talend.designer.esb.runcontainer.ui.dialog.RunClientDialog;
import org.talend.designer.esb.runcontainer.ui.wizard.AddRuntimeWizard;
import org.talend.designer.esb.runcontainer.util.FileUtil;

/**
 * ESB Runtime pref page
 *
 */
public class RunContainerPreferencePage extends FieldLayoutPreferencePage implements IWorkbenchPreferencePage {

    public static final String ID = "org.talend.designer.esb.runcontainer.preferences.ESBRunContainerPreferencePage"; //$NON-NLS-1$

    private Composite compositeServerBody;

    private Composite compositeOptionBody;

    private List<FieldEditor> serverFieldEditors;

    private List<FieldEditor> optionFieldEditors;

    private Button buttonAddServer;

    private Button buttonInitalizeServer;

    private BooleanFieldEditor useOSGiEditor;

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
        serverFieldEditors = new ArrayList<FieldEditor>();
        optionFieldEditors = new ArrayList<FieldEditor>();
        GridLayout gridLayoutDefault = new GridLayout(1, false);

        Composite body = new Composite(parent, SWT.NONE);
        body.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        body.setLayout(gridLayoutDefault);
        getPreferenceStore().getBoolean(RunContainerPreferenceInitializer.P_ESB_IN_OSGI);
        useOSGiEditor = new BooleanFieldEditor(RunContainerPreferenceInitializer.P_ESB_IN_OSGI,
                "ESB Studio Runtime - Use Local Talend Runtime (OSGi Container)", body);
        addField(useOSGiEditor);
        Label lblNote = new Label(body, SWT.WRAP);
        lblNote.setText("Note: It will be only taken into account for an ESB Artifact:\n" + "  · A Route (Any Route)\n"
                + "  · A DataService (SOAP/REST)\n" + "  · A Job contains tRESTClient or tESBConsumer component");

        Group groupServer = new Group(body, SWT.NONE);
        groupServer.setText(RunContainerMessages.getString("RunContainerPreferencePage.Group1")); //$NON-NLS-1$
        groupServer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        groupServer.setLayout(new GridLayout(2, false));

        compositeServerBody = new Composite(groupServer, SWT.BORDER);
        compositeServerBody.setLayout(gridLayoutDefault);
        compositeServerBody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        StringFieldEditor locationEditor = new StringFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_LOCATION,
                RunContainerMessages.getString("RunContainerPreferencePage.Location"), compositeServerBody); //$NON-NLS-1$
        addField(locationEditor);
        serverFieldEditors.add(locationEditor);
        StringFieldEditor hostFieldEditor = new StringFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_HOST,
                RunContainerMessages.getString("RunContainerPreferencePage.Host"), compositeServerBody);
        addField(hostFieldEditor);
        // svrFields.add(hostFieldEditor);
        // only support local runtime server, if need support remote server ,enable this editor
        hostFieldEditor.setEnabled(false, compositeServerBody);

        StringFieldEditor userFieldEditor = new StringFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_USERNAME,
                RunContainerMessages.getString("RunContainerPreferencePage.Username"), compositeServerBody); //$NON-NLS-1$
        addField(userFieldEditor);
        serverFieldEditors.add(userFieldEditor);
        StringFieldEditor passwordFieldEditor = new StringFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_PASSWORD,
                RunContainerMessages.getString("RunContainerPreferencePage.Password"), compositeServerBody); //$NON-NLS-1$
        addField(passwordFieldEditor);
        serverFieldEditors.add(passwordFieldEditor);
        StringFieldEditor instanceFieldEditor = new StringFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_INSTANCE,
                RunContainerMessages.getString("RunContainerPreferencePage.Instance"), compositeServerBody); //$NON-NLS-1$
        addField(instanceFieldEditor);
        serverFieldEditors.add(instanceFieldEditor);

        IntegerFieldEditor portFieldEditor = new IntegerFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_PORT,
                RunContainerMessages.getString("RunContainerPreferencePage.Port"), compositeServerBody); //$NON-NLS-1$
        addField(portFieldEditor);
        serverFieldEditors.add(portFieldEditor);

        StringFieldEditor jmxPortFieldEditor = new StringFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_JMX_PORT,
                RunContainerMessages.getString("RunContainerPreferencePage.JMXPort"), compositeServerBody); //$NON-NLS-1$
        addField(jmxPortFieldEditor);
        serverFieldEditors.add(jmxPortFieldEditor);

        Composite compBtn = new Composite(groupServer, SWT.NONE);
        GridData gridDataBtn = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
        gridDataBtn.widthHint = 100;
        compBtn.setLayoutData(gridDataBtn);
        GridLayout layoutCompBtn = new GridLayout(1, false);
        layoutCompBtn.marginWidth = 0;
        layoutCompBtn.marginHeight = 0;

        compBtn.setLayout(layoutCompBtn);
        buttonAddServer = new Button(compBtn, SWT.NONE);
        buttonAddServer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        buttonAddServer.addSelectionListener(new SelectionAdapter() {

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
        buttonAddServer.setText(RunContainerMessages.getString("RunContainerPreferencePage.ServerButton")); //$NON-NLS-1$

        // Button btnTestConnection = new Button(compBtn, SWT.NONE);
        // btnTestConnection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        // btnTestConnection.setText("Server Info...");

        buttonInitalizeServer = new Button(compBtn, SWT.NONE);
        buttonInitalizeServer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        buttonInitalizeServer.setText(RunContainerMessages.getString("RunContainerPreferencePage.InitalizeButton")); //$NON-NLS-1$
        buttonInitalizeServer.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                initalizeRuntime(locationEditor.getStringValue(), hostFieldEditor.getStringValue());
            }
        });

        Group groupOption = new Group(body, SWT.NONE);
        groupOption.setLayout(gridLayoutDefault);
        groupOption.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        groupOption.setText(RunContainerMessages.getString("RunContainerPreferencePage.Group2")); //$NON-NLS-1$

        compositeOptionBody = new Composite(groupOption, SWT.NONE);
        compositeOptionBody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        BooleanFieldEditor useJmxEditor = new BooleanFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_JMX,
                RunContainerMessages.getString("RunContainerPreferencePage.UseJMX"), compositeOptionBody);//$NON-NLS-1$
        addField(useJmxEditor);
        optionFieldEditors.add(useJmxEditor);

        BooleanFieldEditor filterLogEditor = new BooleanFieldEditor(RunContainerPreferenceInitializer.P_ESB_RUNTIME_SYS_LOG,
                RunContainerMessages.getString("RunContainerPreferencePage.FilterLogs"), compositeOptionBody); //$NON-NLS-1$
        addField(filterLogEditor);
        optionFieldEditors.add(filterLogEditor);

        return body;
    }

    protected void updateFieldEditors(boolean enable) {
        // compOption
        // compSvrBody
        for (FieldEditor editor : serverFieldEditors) {
            editor.setEnabled(enable, compositeServerBody);
        }
        for (FieldEditor editor : optionFieldEditors) {
            editor.setEnabled(enable, compositeOptionBody);
        }
    }

    /**
     * Initialize the preference page.
     */
    @Override
    public void init(IWorkbench workbench) {
        // Initialize the preference page
        setPreferenceStore(ESBRunContainerPlugin.getDefault().getPreferenceStore());
    }

    private void initalizeRuntime(String location, String host) {
        try {
            // 1. try to stop first
            if (RuntimeServerController.getInstance().isRunning()) {
                HaltRuntimeAction halt = new HaltRuntimeAction();
                halt.run();
                if (halt.getErrorMessage() != null) {
                    MessageDialog
                            .openError(
                                    getShell(),
                                    RunContainerMessages.getString("RunContainerPreferencePage.InitailzeDialog2"), RunContainerMessages.getString("RunContainerPreferencePage.InitailzeDialog6") + "\n" + halt.getErrorMessage()); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }
            }
            // 2. delete data(cannot use JMX to rebootCleanAll as a DLL delete failed)
            FileUtil.deleteFolder(location + "/data");
            if (new File(location + "/data").exists()) {
                MessageDialog
                        .openError(
                                getShell(),
                                RunContainerMessages.getString("RunContainerPreferencePage.InitailzeDialog2"), RunContainerMessages.getString("RunContainerPreferencePage.InitailzeDialog7")); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }
            // 3. start (again)
            StartRuntimeAction start = new StartRuntimeAction(false);
            start.run();

            if (null == start.getErrorMessage() && RuntimeServerController.getInstance().isRunning()) {
                File launcher;
                String os = System.getProperty("os.name");
                if (os != null && os.toLowerCase().contains("windows")) {
                    launcher = new File(location + "/bin/client.bat");
                } else {
                    launcher = new File(location + "/bin/client");
                }
                InputStream stream = RunContainerPreferencePage.class.getResourceAsStream("/resources/commands");
                File initFile = new File(location + "/scripts/initlocal.sh");
                if (!initFile.exists()) {
                    Files.copy(stream, initFile.toPath());
                }
                // without username and password is ok
                String command = launcher.getAbsolutePath() + " -h " + host + " -l 1 \"source scripts/initlocal.sh\"";
                RunClientDialog.runClientWithCommandConsole(getShell(), command);
            } else {
                MessageDialog
                        .openError(
                                getShell(),
                                RunContainerMessages.getString("RunContainerPreferencePage.InitailzeDialog2"), RunContainerMessages.getString("RunContainerPreferencePage.InitailzeDialog3") + "\n" + start.getErrorMessage()); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            MessageDialog
                    .openError(
                            getShell(),
                            RunContainerMessages.getString("RunContainerPreferencePage.InitailzeDialog4"), RunContainerMessages.getString("RunContainerPreferencePage.InitailzeDialog5") + e1); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}