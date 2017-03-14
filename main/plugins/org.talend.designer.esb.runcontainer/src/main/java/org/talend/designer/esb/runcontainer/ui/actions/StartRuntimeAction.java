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
package org.talend.designer.esb.runcontainer.ui.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.designer.esb.runcontainer.core.ESBRunContainerPlugin;
import org.talend.designer.esb.runcontainer.preferences.RunContainerPreferenceInitializer;
import org.talend.designer.esb.runcontainer.server.RuntimeServerController;
import org.talend.designer.esb.runcontainer.util.JMXUtil;
import org.talend.designer.esb.runcontainer.util.RuntimeConsoleUtil;
import org.talend.designer.runprocess.ui.ERunprocessImages;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class StartRuntimeAction extends Action {

    public StartRuntimeAction() {
        setToolTipText("Start Server");
        setImageDescriptor(ImageProvider.getImageDesc(ERunprocessImages.RUN_PROCESS_ACTION));
        setEnabled(!RuntimeServerController.getInstance().isRunning());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        if (JMXUtil.testConnection()) {
            RuntimeConsoleUtil.loadConsole();
        } else {
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
            try {
                dialog.run(true, true, new IRunnableWithProgress() {

                    @Override
                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        monitor.beginTask("Starting Runtime Server...", 20);
                        try {
                            IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
                            Process ps = RuntimeServerController.getInstance().startLocalRuntimeServer(
                                    store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_LOCATION));
                            int i = 0;
                            String dot = ".";
                            while (JMXUtil.connectToRuntime() == null && ++i < 20 && !monitor.isCanceled()) {
                                monitor.subTask("Try to connect to runtime server" + dot);
                                dot += ".";
                                monitor.worked(1);
                                Thread.sleep(1000);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        monitor.done();
                        if (JMXUtil.connectToRuntime() != null) {
                            RuntimeConsoleUtil.loadConsole();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MessageDialog.openError(Display.getCurrent().getActiveShell(), "Start server failed", e.getMessage());
            }
        }
    }

}
