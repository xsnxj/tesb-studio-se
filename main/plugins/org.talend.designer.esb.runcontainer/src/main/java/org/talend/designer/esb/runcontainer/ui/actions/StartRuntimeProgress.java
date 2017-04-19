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
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.talend.designer.esb.runcontainer.i18n.RunContainerMessages;
import org.talend.designer.esb.runcontainer.server.RuntimeServerController;
import org.talend.designer.esb.runcontainer.util.JMXUtil;

public class StartRuntimeProgress implements IRunnableWithProgress {

    private String karafHome;

    public StartRuntimeProgress(String karafHome) {
        this.karafHome = karafHome;
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        monitor.beginTask(RunContainerMessages.getString("StartRuntimeAction.Starting"), 10); //$NON-NLS-1$
        try {
            RuntimeServerController.getInstance().startLocalRuntimeServer(karafHome);
            int i = 0;
            String dot = "."; //$NON-NLS-1$
            while (JMXUtil.createJMXconnection() == null && ++i < 10 && !monitor.isCanceled()) {
                monitor.subTask(RunContainerMessages.getString("StartRuntimeAction.Try") + dot); //$NON-NLS-1$
                dot += "."; //$NON-NLS-1$
                monitor.worked(1);
                Thread.sleep(2000);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new InterruptedException(e.getMessage());
        } finally {
            monitor.done();
        }
    }

}
