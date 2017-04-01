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
package org.talend.designer.esb.runcontainer.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.components.ComponentCategory;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.IProcess2;
import org.talend.core.model.properties.Property;
import org.talend.core.model.runprocess.data.PerformanceData;
import org.talend.designer.esb.runcontainer.core.ESBRunContainerPlugin;
import org.talend.designer.esb.runcontainer.jmx.JMXPerformanceChangeListener;
import org.talend.designer.esb.runcontainer.jmx.JMXRunStatManager;
import org.talend.designer.esb.runcontainer.preferences.RunContainerPreferenceInitializer;
import org.talend.designer.runprocess.IProcessMessage;
import org.talend.designer.runprocess.IProcessMonitor;
import org.talend.designer.runprocess.IProcessor;
import org.talend.designer.runprocess.ProcessMessage;
import org.talend.designer.runprocess.ProcessMessage.MsgType;
import org.talend.designer.runprocess.RunProcessContext;
import org.talend.designer.runprocess.trace.TraceConnectionsManager;

public class RunContainerProcessContext extends RunProcessContext {

    private JMXPerformanceMonitor jmxPerformanceMonitor;

    private JMXConnectionsManager jmxConnectionsManager;

    private LocalRuntimeProcessMonitor localRuntimeProcessMonitor;

    private IProcess2 process;

    private IProcessor processor;

    public RunContainerProcessContext(IProcess2 process) {
        super(process);
        this.process = process;
    }

    @Override
    protected IProcessor getProcessor(IProcess process, Property property) {
        if (null == processor) {
            processor = new RunContainerProcessor(process);
        }
        return processor;
    }

    @Override
    protected IProcessMonitor createProcessMonitor(Process process) {
        localRuntimeProcessMonitor = new LocalRuntimeProcessMonitor(process);
        return localRuntimeProcessMonitor;
    }

    @Override
    public synchronized int kill(Integer returnExitValue) {
        if (getProcess() instanceof RunContainerProcess) {
            ((RunContainerProcess) getProcess()).stopLogging();
        }
        if (processor instanceof RunContainerProcessor) {
            ((RunContainerProcessor) processor).stop();
        }
        processor.setCodeGenerated(false);
        return super.kill(returnExitValue);
    }

    @Override
    protected PerformanceMonitor getPerformanceMonitor() {
        IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
        if (ComponentCategory.CATEGORY_4_CAMEL.getName().equals(process.getComponentsType())
                && store.getBoolean(RunContainerPreferenceInitializer.P_ESB_RUNTIME_JMX)) {
            jmxPerformanceMonitor = new JMXPerformanceMonitor();
            return jmxPerformanceMonitor;
        }
        return super.getPerformanceMonitor();
    }

    @Override
    protected TraceConnectionsManager getTraceConnectionsManager(IProcess2 process) {
        IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
        if (ComponentCategory.CATEGORY_4_CAMEL.getName().equals(process.getComponentsType())
                && store.getBoolean(RunContainerPreferenceInitializer.P_ESB_RUNTIME_JMX)) {
            jmxConnectionsManager = new JMXConnectionsManager(process);
            return jmxConnectionsManager;
        }
        return super.getTraceConnectionsManager(process);
    }

    class JMXPerformanceMonitor extends PerformanceMonitor {

        private JMXPerformanceChangeListener jmxPerformanceChangeListener;

        private JMXRunStatManager jmxManager;

        /**
         * DOC yyan ESBRunContainerProcessContext.JMXPerformanceMonitor constructor comment.
         */
        public JMXPerformanceMonitor() {
            jmxManager = JMXRunStatManager.getInstance();
            jmxPerformanceChangeListener = new JMXPerformanceChangeListener() {

                long startTime = System.currentTimeMillis();

                @Override
                public String getProcessName() {
                    return getProcess().getLabel();
                }

                @Override
                public void performancesChanged(String connId, int exchangesCompleted) {
                    long duration = 1000;// (System.currentTimeMillis() - startTime);
                    final IConnection conn = jmxConnectionsManager.finConnectionByUniqueName(connId);
                    final PerformanceData perfData = new PerformanceData(connId + "|" + exchangesCompleted + "|" + duration);
                    processPerformances(connId + "|" + exchangesCompleted + "|" + duration, perfData, conn);
                    startTime = System.currentTimeMillis();
                }
            };
        }

        @Override
        public void run() {
            // jmxManager.startTracing();
            jmxManager.addTracing(RunContainerProcessContext.this);
            jmxManager.addPerformancesChangeListener(jmxPerformanceChangeListener);
        }

        @Override
        public void stopThread() {
            jmxManager.stopTracing(RunContainerProcessContext.this);
            super.stopThread();
        }
    }

    class JMXConnectionsManager extends TraceConnectionsManager {

        public JMXConnectionsManager(IProcess process) {
            super(process);
        }

    }

    /**
     * This monitor will stop with kill button only. <br/>
     *
     * $Id$
     *
     */
    class LocalRuntimeProcessMonitor implements IProcessMonitor {

        volatile boolean stopThread;

        /** The monitoring process. */
        private final Process process;

        /** Input stream for stdout of the process. */
        private final BufferedReader outIs;

        /** Input stream for stderr of the process. */
        private final BufferedReader errIs;

        private boolean hasCompilationError = false;

        public LocalRuntimeProcessMonitor(Process ps) {
            super();
            this.process = ps;
            this.outIs = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            this.errIs = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
        }

        @Override
        public void run() {

            int exitValue = 0;
            while (!stopThread) {
                boolean dataPiped = extractMessages(false);
                processMessageManager.updateConsole();
                boolean ended;
                try {

                    exitValue = process.exitValue();
                    // flush remaining messages
                    while (!stopThread) {
                        extractMessages(true);
                        processMessageManager.updateConsole();
                        synchronized (this) {
                            try {
                                wait(200);
                            } catch (InterruptedException e) {
                                // Do nothing
                            }
                        }
                    }

                    // Read the end of the stream after the end of the process
                    ended = true;
                    stopThread = true;
                    try {
                        this.process.getInputStream().close();
                    } catch (IOException e) {
                        // Do nothing
                    }

                    try {
                        this.process.getErrorStream().close();
                    } catch (IOException e) {
                        // Do nothing
                    }

                } catch (IllegalThreadStateException itse) {
                    ended = false;
                } catch (Exception e) {
                    ended = false;
                }

                if (!dataPiped && !ended) {
                    synchronized (this) {
                        try {
                            final long waitTime = 100;
                            wait(waitTime);
                        } catch (InterruptedException e) {
                            // Do nothing
                        }
                    }
                }
            }
            kill(exitValue);
        }

        @Override
        public void stopThread() {
            stopThread = true;
            synchronized (this) {
                notify();
            }
        }

        private boolean extractMessages(boolean flush) {
            IProcessMessage messageOut = null;
            IProcessMessage messageErr = null;
            try {
                messageErr = extractMessage(errIs, MsgType.STD_ERR, flush);
                if (messageErr != null) {
                    processMessageManager.addMessage(messageErr);
                }
                messageOut = extractMessage(outIs, MsgType.STD_OUT, flush);
                if (messageOut != null) {
                    processMessageManager.addMessage(messageOut);
                    // processMessageManager.updateConsole();
                }
            } catch (IOException ioe) {
                addErrorMessage(ioe);
                ExceptionHandler.process(ioe);
            }
            return messageOut != null || messageErr != null;
        }

        /**
         * Extract a message from a stream.
         * 
         * @param is Input stream to be read.
         * @param type Type of message read.
         * @param flush
         * @return the message extracted or null if no message was present.
         * @throws IOException Extraction failure.
         */
        private IProcessMessage extractMessage(final BufferedReader is, MsgType type, boolean flush) throws IOException {

            IProcessMessage msg;
            if (is.ready()) {

                StringBuilder sb = new StringBuilder();

                String data = null;
                long timeStart = System.currentTimeMillis();
                while (is.ready()) {
                    data = is.readLine();
                    if (data == null) {
                        break;
                    }
                    // TODO check if it's not end with '\n', add it.
                    if (!data.endsWith("\n")) {
                        sb.append(data).append('\n');
                    } else {
                        sb.append(data);
                    }
                    if (sb.length() > 1024 || System.currentTimeMillis() - timeStart > 100) {
                        break;
                    }
                }
                msg = new ProcessMessage(type, sb.toString());
            } else {
                msg = null;
            }
            return msg;
        }
    }

}
