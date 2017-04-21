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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.designer.esb.runcontainer.core.ESBRunContainerPlugin;
import org.talend.designer.esb.runcontainer.preferences.RunContainerPreferenceInitializer;
import org.talend.designer.esb.runcontainer.util.JMXUtil;

public class RuntimeClientProgress extends RuntimeProgress {

    private String command;

    private List<String> log;

    public RuntimeClientProgress(String command) {
        this.command = command;
        this.log = new ArrayList();
    }

    @Override
    public void run(IProgressMonitor parentMonitor) throws InvocationTargetException, InterruptedException {
        SubMonitor subMonitor = SubMonitor.convert(parentMonitor, 10);
        subMonitor.setTaskName("Running script (" + command + ")"); //$NON-NLS-1$
        if (checkRunning()) {
            subMonitor.subTask("Checking runtime bundles...");
            waitForActive();
            subMonitor.worked(2);

            IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
            File containerDir = new File(store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_LOCATION));
            try {
                subMonitor.subTask("Script is running to end...");
                Process process = Runtime.getRuntime().exec(command, null, containerDir);
                new Thread(new ProcessOutput(process.getInputStream(), false)).start();
                new Thread(new ProcessOutput(process.getErrorStream(), true)).start();
                process.waitFor();
                subMonitor.worked(8);

                int size = log.size();
                if (!log.get(size - 1).equals("EOF")) {

                    StackTraceElement[] stackTrace = new StackTraceElement[size];
                    for (int i = 0; i < size; i++) {
                        stackTrace[i] = new StackTraceElement("RuntimeClientProgress", log.get(i), "Unknown source", i);
                    }
                    InterruptedException e = new InterruptedException("Script initlocal.sh run failed");
                    e.setStackTrace(stackTrace);
                    throw e;
                }
            } catch (IOException e) {
                throw new InvocationTargetException(e);
            }
        }
    }

    private void waitForActive() {
        try {
            int deactiveCount = 0;
            do {
                long[] bundleList = JMXUtil.getBundlesList();
                deactiveCount = bundleList.length;
                for (long id : bundleList) {
                    if ("Active".equals(JMXUtil.getBundleStatus(id))) {
                        deactiveCount--;
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (deactiveCount < 5);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for log.
     * 
     * @return the log
     */
    public String getLog() {
        return this.log.toString();
    }

    final class ProcessOutput implements Runnable {

        private static final char LINE = '\n';

        private InputStream input;

        ProcessOutput(InputStream input, boolean isError) {
            this.input = input;
        }

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(input));
                String input = null;
                while ((input = inReader.readLine()) != null) {
                    if (!input.isEmpty()) {
                        log.add(input);
                    }
                }
            } catch (IOException e) {
            }
        }
    }

}
