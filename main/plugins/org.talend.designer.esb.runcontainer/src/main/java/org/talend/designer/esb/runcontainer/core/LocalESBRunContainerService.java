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
 *
 * $Id$
 *
 */
public class LocalESBRunContainerService implements IESBRunContainerService {

    private static final String ESB_RUNTIME_ITEM = "ESB Runtime";

    private RunProcessContext esbProcessContext;

    private RunProcessContextManager defaultContextManager;

    private RunContainerProcessContextManager runtimeContextManager;

    private int index = 0;

    @Override
    public void addRuntimeServer(TargetExecComposite targetExecComposite, JobJvmComposite jobComposite) {
        Combo targetCombo = null;
        IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
        if (JobJvmComposite.class == jobComposite.getClass()) { // Update Tab SE
            try {
                Control control = ((Composite) jobComposite.getChildren()[0]).getChildren()[0];
                if (control instanceof StyledText) {
                    StyledText styled = (StyledText) control;
                    styled.setText("Please select target execution environment");
                    targetCombo = new Combo((Composite) jobComposite.getChildren()[0], SWT.BORDER | SWT.READ_ONLY);
                    GridData data = new GridData(GridData.FILL_BOTH);
                    data.horizontalIndent = 5;
                    targetCombo.setLayoutData(data);
                    targetCombo.add("Default", 0);
                    String url = store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_HOST);
                    String rt = ESB_RUNTIME_ITEM + ":" + url;
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
                    String url = store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_HOST);
                    String rt = ESB_RUNTIME_ITEM + ":" + url;
                    targetCombo.add(rt);
                    this.index = targetCombo.getSelectionIndex();
                }
            } catch (Exception ex) {
            }
        }

        if (targetCombo != null) {
            if (RunProcessPlugin.getDefault().getRunProcessContextManager() instanceof RunContainerProcessContextManager) {
                int i = targetCombo.indexOf(ESB_RUNTIME_ITEM);
                targetCombo.select(i);
            } else {
                targetCombo.select(index);
            }
            targetCombo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (0 == ((Combo) e.getSource()).getText().indexOf(ESB_RUNTIME_ITEM)) {
                        // check if server setting is validated.
                        IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
                        String host = store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_HOST);
                        File containerDir = new File(store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_LOCATION));
                        if (containerDir.exists() || !(host.equals("localhost") || host.equals("127.0.0.1"))) {
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
                            boolean openPrefs = MessageDialog.openConfirm(jobComposite.getShell(), "Runtime Server Setting",
                                    "Runtime Server setting is not complete, please update runtime server informations before running.");
                            if (openPrefs) {
                                PreferenceDialog d = new PreferenceDialog(jobComposite.getShell(), PlatformUI.getWorkbench()
                                        .getPreferenceManager());
                                d.setSelectedNode(RunContainerPreferencePage.ID);
                                d.open();
                            }
                            // ((Combo) e.getSource()).select(index);
                        }
                    } else if (defaultContextManager != null) {
                        RunProcessPlugin.getDefault().setRunProcessContextManager(defaultContextManager);
                    }
                }
            });
        }
    }

    // @Override
    // public boolean isESBProcessContextManager(RunProcessContextManager contextManager) {
    // return contextManager instanceof RunContainerProcessContextManager;
    // }
}
