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
import org.talend.designer.esb.runcontainer.i18n.RunContainerMessages;
import org.talend.designer.esb.runcontainer.preferences.RunContainerPreferenceInitializer;
import org.talend.designer.esb.runcontainer.server.RuntimeServerController;
import org.talend.designer.esb.runcontainer.util.JMXUtil;
import org.talend.designer.esb.runcontainer.util.RuntimeConsoleUtil;
import org.talend.designer.runprocess.ui.ERunprocessImages;

public class StartRuntimeAction extends Action {

    public StartRuntimeAction() {
        setToolTipText(RunContainerMessages.getString("StartRuntimeAction.Start")); //$NON-NLS-1$
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
                        monitor.beginTask(RunContainerMessages.getString("StartRuntimeAction.Starting"), 20); //$NON-NLS-1$
                        try {
                            IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
                            Process ps = RuntimeServerController.getInstance().startLocalRuntimeServer(
                                    store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_LOCATION));
                            int i = 0;
                            String dot = "."; //$NON-NLS-1$
                            while (JMXUtil.connectToRuntime() == null && ++i < 20 && !monitor.isCanceled()) {
                                monitor.subTask(RunContainerMessages.getString("StartRuntimeAction.Try") + dot); //$NON-NLS-1$
                                dot += "."; //$NON-NLS-1$
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
                MessageDialog.openError(Display.getCurrent().getActiveShell(),
                        RunContainerMessages.getString("StartRuntimeAction.ErrorStart"), e.getMessage()); //$NON-NLS-1$
            }
        }
    }

}
