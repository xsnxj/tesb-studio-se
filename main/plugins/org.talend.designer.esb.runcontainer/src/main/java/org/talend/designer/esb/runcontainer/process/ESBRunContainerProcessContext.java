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
package org.talend.designer.esb.runcontainer.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.IProcess2;
import org.talend.core.model.properties.Property;
import org.talend.designer.runprocess.IProcessMessage;
import org.talend.designer.runprocess.IProcessMonitor;
import org.talend.designer.runprocess.IProcessor;
import org.talend.designer.runprocess.ProcessMessage;
import org.talend.designer.runprocess.RunProcessContext;
import org.talend.designer.runprocess.ProcessMessage.MsgType;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class ESBRunContainerProcessContext extends RunProcessContext {

    public ESBRunContainerProcessContext(IProcess2 process) {
        super(process);
        // processMessageManager = new ProcessMessageManager();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.RunProcessContext#getProcessor(org.talend.core.model.process.IProcess,
     * org.talend.core.model.properties.Property)
     */
    @Override
    protected IProcessor getProcessor(IProcess process, Property property) {
        // TODO Auto-generated method stub
        // new ESBRuntimeContainerProcessor();
        return new ESBRuntimeContainerProcessor(process);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.RunProcessContext#createProcessMonitor(java.lang.Process)
     */
    @Override
    protected IProcessMonitor createProcessMonitor(Process process) {
        // TODO Auto-generated method stub
        return new LocalRuntimeProcessMonitor(process);
    }

    /**
     * This monitor will stop with kill button only. <br/>
     *
     * $Id$
     *
     */
    protected class LocalRuntimeProcessMonitor implements IProcessMonitor {

        volatile boolean stopThread;

        /** The monitoring process. */
        private final Process process;

        /** Input stream for stdout of the process. */
        private final BufferedReader outIs;

        /** Input stream for stderr of the process. */
        private final BufferedReader errIs;

        private boolean hasCompilationError = false;

        /**
         * DOC yyi ESBRunContainerProcessContext.LocalRuntimeProcessMonitor constructor comment.
         */
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
                // processMessageManager.updateConsole();
                boolean ended;
                try {

                    if (!hasCompilationError) {
                        exitValue = process.exitValue();
                    }

                    // flush remaining messages
                    while (!stopThread) {
                        extractMessages(true);
                    }

                    // Read the end of the stream after the end of the process
                    ended = true;
                    stopThread = true;
                    try {
                        this.process.getInputStream().close();
                    } catch (IOException e) {
                        ExceptionHandler.process(e);
                    }

                    try {
                        this.process.getErrorStream().close();
                    } catch (IOException e) {
                        ExceptionHandler.process(e);
                    }

                } catch (IllegalThreadStateException itse) {
                    ended = false;
                } catch (Exception e) {
                    ended = false;
                }

                if (!dataPiped && !ended) {
                    synchronized (this) {
                        try {
                            final long waitTime = 5;
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
                    if (messageErr.getContent().contains("Unresolved compilation problem")) { //$NON-NLS-1$
                        hasCompilationError = true;
                    }
                    addMessage(messageErr);
                    // processMessageManager.addMessage(messageErr);
                }
                messageOut = extractMessage(outIs, MsgType.STD_OUT, flush);
                if (messageOut != null) {
                    addMessage(messageOut);
                    // processMessageManager.addMessage(messageOut);
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
                    sb.append(data).append("\n"); //$NON-NLS-1$
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
