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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

    private static final String ESB_RUNTIME_ITEM = "ESB Runtime ";

    private RunProcessContext esbProcessContext;

    private RunProcessContextManager remoteContextManager;

    private RunContainerProcessContextManager runtimeContextManager;

    private int index;

    @Override
    public void addRuntimeServer(TargetExecComposite targetExecComposite, JobJvmComposite jobComposite) {
        // targetExecComposite.get
        Combo tragetCombo = null;
        IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
        if ("org.talend.designer.runprocess.remote.ui.RemoteProcessComposite".equals(jobComposite.getClass().getName())) {
            // Update EE tab
            try {
                Control control = ((Composite) jobComposite.getChildren()[0]).getChildren()[0];
                tragetCombo = (Combo) control;
                String url = store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_HOST);
                String rt = ESB_RUNTIME_ITEM + url;
                tragetCombo.add(rt);
                this.index = tragetCombo.getSelectionIndex();
            } catch (Exception ex) {
            }

        } else {
            // Update Tab SE

        }
        if (tragetCombo != null) {
            if (RunProcessPlugin.getDefault().getRunProcessContextManager() instanceof RunContainerProcessContextManager) {
                int i = tragetCombo.indexOf(ESB_RUNTIME_ITEM);
                tragetCombo.select(i);
            } else {
                tragetCombo.select(index);
            }
            tragetCombo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (0 == ((Combo) e.getSource()).getText().indexOf(ESB_RUNTIME_ITEM)) {
                        // check if server setting is validated.
                        IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
                        File containerDir = new File(store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_LOCATION));
                        if (containerDir.exists()) {
                            remoteContextManager = RunProcessPlugin.getDefault().getRunProcessContextManager();
                            esbProcessContext = remoteContextManager.getActiveContext();
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
                                    "Runtime Server setting is not complete, please add it before running.");
                            if (openPrefs) {
                                PreferenceDialog d = new PreferenceDialog(jobComposite.getShell(), PlatformUI.getWorkbench()
                                        .getPreferenceManager());
                                d.setSelectedNode(RunContainerPreferencePage.ID);
                                d.open();
                            }
                            // ((Combo) e.getSource()).select(index);
                        }
                    } else if (remoteContextManager != null) {
                        RunProcessPlugin.getDefault().setRunProcessContextManager(remoteContextManager);
                    }
                }
            });
        }
    }
}
