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
package org.talend.designer.esb.runcontainer.core;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.talend.designer.esb.runcontainer.i18n.RunContainerMessages;
import org.talend.designer.esb.runcontainer.preferences.RunContainerPreferenceInitializer;
import org.talend.designer.esb.runcontainer.preferences.RunContainerPreferencePage;
import org.talend.designer.esb.runcontainer.process.RunContainerProcessContextManager;
import org.talend.designer.runprocess.IESBRunContainerService;
import org.talend.designer.runprocess.RunProcessContext;
import org.talend.designer.runprocess.RunProcessContextManager;
import org.talend.designer.runprocess.RunProcessPlugin;
import org.talend.designer.runprocess.ui.JobJvmComposite;
import org.talend.designer.runprocess.ui.ProcessManager;
import org.talend.designer.runprocess.ui.TargetExecComposite;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 * TESB-18750, Locally ESB runtime server service
 */
public class LocalESBRunContainerService implements IESBRunContainerService {

    private static final String ESB_RUNTIME_ITEM = "ESB Runtime"; //$NON-NLS-1$

    private RunProcessContext esbProcessContext;

    private RunProcessContextManager defaultContextManager;

    private RunContainerProcessContextManager runtimeContextManager;

    private int index = 0;

    /*
     * This method is to add a combo box(if do not have) in the <b>target exec</b> run tab view for ESB runtime server
     * 
     * @see org.talend.designer.runprocess.IESBRunContainerService#addRuntimeServer(org.talend.designer.runprocess.ui.
     * TargetExecComposite, org.talend.designer.runprocess.ui.JobJvmComposite)
     */
    @Override
    public void addRuntimeServer(TargetExecComposite targetExecComposite, JobJvmComposite jobComposite) {
        Combo targetCombo = null;
        IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
        String url = store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_HOST);
        String port = store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_PORT);
        String rt = ESB_RUNTIME_ITEM + " (" + url + ":" + port + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (JobJvmComposite.class == jobComposite.getClass()) { // Update Tab SE
            try {
                Control control = ((Composite) jobComposite.getChildren()[0]).getChildren()[0];
                if (control instanceof StyledText) {
                    StyledText styled = (StyledText) control;
                    styled.setText(RunContainerMessages.getString("LocalESBRunContainerService.Tip")); //$NON-NLS-1$
                    targetCombo = new Combo((Composite) jobComposite.getChildren()[0], SWT.BORDER | SWT.READ_ONLY);
                    GridData data = new GridData(GridData.FILL_BOTH);
                    data.horizontalIndent = 5;
                    targetCombo.setLayoutData(data);
                    targetCombo.add("Default", 0); //$NON-NLS-1$
                    targetCombo.add(rt, 1);
                    this.index = targetCombo.getSelectionIndex();
                    targetCombo.select(index == -1 ? 0 : index);
                }
            } catch (Exception ex) {
            }
        } else { // Update EE tab
            try {
                Control control = ((Composite) jobComposite.getChildren()[0]).getChildren()[0];
                if (control instanceof Combo) {
                    targetCombo = (Combo) control;
                    targetCombo.add(rt);
                    this.index = targetCombo.getSelectionIndex();
                }
            } catch (Exception ex) {
            }
        }

        if (targetCombo != null) {
            if (RunProcessPlugin.getDefault().getRunProcessContextManager() instanceof RunContainerProcessContextManager) {
                targetCombo.select(targetCombo.indexOf(rt));
            } else {
                targetCombo.select(index);
            }
            targetCombo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (((Combo) e.getSource()).getText().startsWith(ESB_RUNTIME_ITEM)) {
                        // check if server setting is validated.
                        IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
                        String host = store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_HOST);
                        File containerDir = new File(store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_LOCATION));
                        if (containerDir.exists() || !(host.equals("localhost") || host.equals("127.0.0.1"))) { //$NON-NLS-1$ //$NON-NLS-2$
                            defaultContextManager = RunProcessPlugin.getDefault().getRunProcessContextManager();
                            esbProcessContext = defaultContextManager.getActiveContext();
                            if (runtimeContextManager == null) {
                                runtimeContextManager = new RunContainerProcessContextManager();
                            }
                            // reset context manager and active process
                            RunProcessPlugin.getDefault().setRunProcessContextManager(runtimeContextManager);
                            RunProcessPlugin.getDefault().getRunProcessContextManager()
                                    .setActiveProcess(esbProcessContext.getProcess());
                            ProcessManager.getInstance().setProcessContext(runtimeContextManager.getActiveContext());
                        } else {
                            boolean openPrefs = MessageDialog.openConfirm(jobComposite.getShell(),
                                    RunContainerMessages.getString("LocalESBRunContainerService.Dialog1"), //$NON-NLS-1$
                                    RunContainerMessages.getString("LocalESBRunContainerService.Dialog2")); //$NON-NLS-1$
                            if (openPrefs) {
                                PreferenceDialog dlg = new PreferenceDialog(jobComposite.getShell(), PlatformUI.getWorkbench()
                                        .getPreferenceManager());
                                dlg.setSelectedNode(RunContainerPreferencePage.ID);
                                dlg.open();
                            }
                            // move to previous selection
                            ((Combo) e.getSource()).select(index);
                        }
                    } else if (defaultContextManager != null) {
                        RunProcessPlugin.getDefault().setRunProcessContextManager(defaultContextManager);
                    }
                }
            });
        }
    }

    @Override
    public boolean isESBProcessContextManager(RunProcessContextManager contextManager) {
        return contextManager instanceof RunContainerProcessContextManager;
    }
}
