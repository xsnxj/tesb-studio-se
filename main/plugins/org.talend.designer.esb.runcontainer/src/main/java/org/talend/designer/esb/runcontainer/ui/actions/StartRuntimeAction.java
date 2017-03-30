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

import java.io.File;
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

/**
 * Start runtime server steps:
 * 
 * 1. check is there a local runtime server exists(start in studio or is an external runtime server)
 * 
 * 2.
 */
public class StartRuntimeAction extends Action {

    private boolean needConsole;

    public StartRuntimeAction() {
        setToolTipText(RunContainerMessages.getString("StartRuntimeAction.Start")); //$NON-NLS-1$
        setImageDescriptor(ImageProvider.getImageDesc(ERunprocessImages.RUN_PROCESS_ACTION));
        setEnabled(!RuntimeServerController.getInstance().isRunning());
    }

    public StartRuntimeAction(boolean needConsole) {
        this.needConsole = needConsole;
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
        if (JMXUtil.isConnected()) {
            // do nothing, just load console if needed
            if (needConsole) {
                loadConsole();
            }
            return;
        }

        if (JMXUtil.createJMXconnection() != null) {
            try {
                File karafHome = new File(JMXUtil.getSystemPropertie("karaf.home").replaceFirst("\\\\:", ":")); //$NON-NLS-1$ //$NON-NLS-2$
                IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
                File runtimeLocation = new File(store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_LOCATION));
                // is the same runtime, but it already running
                if (runtimeLocation.getAbsolutePath().equals(karafHome.getAbsolutePath())) {
                    if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Connect to Runtime Server",
                            "A local runtime server has been running, do you want to connect to it directly?")) {
                        if (needConsole) {
                            loadConsole();
                        }
                    } else {
                        JMXUtil.closeJMXConnection();
                    }
                } else {
                    // different runtime is running
                    JMXUtil.closeJMXConnection();
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), "Start Runtime Server Failed",
                            "Another runtime server is running, please stop it first.");
                }
            } catch (Exception e) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), "Start Runtime Server Failed", e.getMessage());
            }
        } else {
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
            try {
                dialog.run(true, true, new IRunnableWithProgress() {

                    Process ps = null;

                    String errorMessage = null;

                    @Override
                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        monitor.beginTask(RunContainerMessages.getString("StartRuntimeAction.Starting"), 10); //$NON-NLS-1$
                        try {
                            IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
                            ps = RuntimeServerController.getInstance().startLocalRuntimeServer(
                                    store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_LOCATION));
                            int i = 0;
                            String dot = "."; //$NON-NLS-1$
                            while (JMXUtil.createJMXconnection() == null && ++i < 10 && !monitor.isCanceled()) {
                                monitor.subTask(RunContainerMessages.getString("StartRuntimeAction.Try") + dot); //$NON-NLS-1$
                                dot += "."; //$NON-NLS-1$
                                monitor.worked(1);
                                Thread.sleep(1000);
                            }
                        } catch (IOException e) {
                            errorMessage = e.getMessage();
                            e.printStackTrace();
                        } finally {
                            monitor.done();
                            if (JMXUtil.isConnected()) {
                                if (needConsole) {
                                    loadConsole();
                                }
                            } else {
                                if (ps != null) {
                                    ps.destroy();
                                }
                                MessageDialog
                                        .openError(
                                                Display.getCurrent().getActiveShell(),
                                                "Start Runtime Server Failed",
                                                "Runtime Server cannot be start, please check your settings.\n" + errorMessage == null ? ""
                                                        : errorMessage);
                            }
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

    public void loadConsole() {
        RuntimeConsoleUtil.loadConsole();
    }
}
