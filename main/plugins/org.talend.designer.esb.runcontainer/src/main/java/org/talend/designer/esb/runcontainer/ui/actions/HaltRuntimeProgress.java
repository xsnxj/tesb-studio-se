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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.talend.designer.esb.runcontainer.i18n.RunContainerMessages;
import org.talend.designer.esb.runcontainer.server.RuntimeServerController;


public class HaltRuntimeProgress implements IRunnableWithProgress {

    /* (non-Javadoc)
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        monitor.beginTask(RunContainerMessages.getString("HaltRuntimeAction.Stoping"), 20); //$NON-NLS-1$
        try {
            RuntimeServerController.getInstance().stopRuntimeServer();
            int i = 0;
            String dot = "."; //$NON-NLS-1$
            // JMXUtil.connectToRuntime() != null
            while (RuntimeServerController.getInstance().isRunning() && i < 20 && !monitor.isCanceled()) {
                monitor.subTask(RunContainerMessages.getString("HaltRuntimeAction.Task") + dot); //$NON-NLS-1$
                dot += "."; //$NON-NLS-1$
                monitor.worked(1);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        monitor.done();

    }

}
