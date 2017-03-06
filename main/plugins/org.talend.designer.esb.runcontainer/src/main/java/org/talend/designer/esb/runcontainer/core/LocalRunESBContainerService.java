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

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.talend.designer.esb.runcontainer.process.ESBRunContainerProcessContextManager;
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
public class LocalRunESBContainerService implements IESBRunContainerService {

    private RunProcessContext esbProcessContext;

    private RunProcessContextManager remoteContextManager;

    private ESBRunContainerProcessContextManager runtimeContextManager;

    private int index;

    @Override
    public void addRuntimeServer(TargetExecComposite targetExecComposite, JobJvmComposite jobComposite) {
        // targetExecComposite.get
        Combo tragetCombo = null;
        if ("org.talend.designer.runprocess.remote.ui.RemoteProcessComposite".equals(jobComposite.getClass().getName())) {
            // Update EE tab
            Control control = ((Composite) jobComposite.getChildren()[0]).getChildren()[0];
            tragetCombo = (Combo) control;
            tragetCombo.add("ESB Runtime (localhost)");
            this.index = tragetCombo.getSelectionIndex();

        } else {
            // Update Tab SE

        }
        if (tragetCombo != null) {
            if (RunProcessPlugin.getDefault().getRunProcessContextManager() instanceof ESBRunContainerProcessContextManager) {
                int i = tragetCombo.indexOf("ESB Runtime (localhost)");
                tragetCombo.select(i);
            } else {
                tragetCombo.select(index);
            }
            tragetCombo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (((Combo) e.getSource()).getText().equals("ESB Runtime (localhost)")) {
                        remoteContextManager = RunProcessPlugin.getDefault().getRunProcessContextManager();
                        esbProcessContext = remoteContextManager.getActiveContext();
                        if (runtimeContextManager == null) {
                            runtimeContextManager = new ESBRunContainerProcessContextManager();
                        }
                        // reset context manager and active process
                        RunProcessPlugin.getDefault().setRunProcessContextManager(runtimeContextManager);
                        RunProcessPlugin.getDefault().getRunProcessContextManager()
                                .setActiveProcess(esbProcessContext.getProcess());
                        ProcessManager.getInstance().setProcessContext(runtimeContextManager.getActiveContext());
                    } else if (remoteContextManager != null) {
                        RunProcessPlugin.getDefault().setRunProcessContextManager(remoteContextManager);
                    }
                }
            });
        }
    }
}
