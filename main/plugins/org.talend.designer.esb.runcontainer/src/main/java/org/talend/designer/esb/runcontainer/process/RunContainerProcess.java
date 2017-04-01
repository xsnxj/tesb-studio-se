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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.talend.designer.esb.runcontainer.logs.FelixLogsModel;
import org.talend.designer.esb.runcontainer.logs.RuntimeLogHTTPAdapter;
import org.talend.designer.esb.runcontainer.logs.RuntimeLogHTTPMonitor;

public class RunContainerProcess extends Process {

    private PipedOutputStream stdOutputStream;

    private PipedInputStream errInputStream;

    private PipedInputStream stdInputStream;

    private PipedOutputStream errOutputStream;

    private RuntimeLogHTTPAdapter logListener;

    private boolean startLogging;

    public RunContainerProcess() {
        stdInputStream = new PipedInputStream();
        errInputStream = new PipedInputStream();

        try {
            stdOutputStream = new PipedOutputStream(stdInputStream);
            errOutputStream = new PipedOutputStream(errInputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        logListener = new RuntimeLogHTTPAdapter() {

            private final String info = "INFO";

            @Override
            public synchronized void logReceived(FelixLogsModel logsModel) {
                if (startLogging) {
                    String msg = logsModel.toString();
                    try {
                        if (info.equals(logsModel.getLevel())) {
                            stdOutputStream.write(msg.getBytes());
                            stdOutputStream.write('\n');
                            // stdOutputStream.flush();
                        } else {
                            errOutputStream.write(msg.getBytes());
                            errOutputStream.write('\n');
                            // errOutputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    // start logging
    public void startLogging() {
        RuntimeLogHTTPMonitor logMonitor = RuntimeLogHTTPMonitor.createRuntimeLogHTTPMonitor();
        logMonitor.startLogging();
        logMonitor.addLogLictener(logListener);
        startLogging = true;
    }

    public void stopLogging() {
        RuntimeLogHTTPMonitor.createRuntimeLogHTTPMonitor().removeLogLictener(logListener);
        startLogging = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Process#getOutputStream()
     */
    @Override
    public OutputStream getOutputStream() {
        // TODO Auto-generated method stub
        return stdOutputStream;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Process#getInputStream()
     */
    @Override
    public InputStream getInputStream() {
        // TODO Auto-generated method stub
        return stdInputStream;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Process#getErrorStream()
     */
    @Override
    public InputStream getErrorStream() {
        // TODO Auto-generated method stub
        return errInputStream;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Process#waitFor()
     */
    @Override
    public int waitFor() throws InterruptedException {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Process#exitValue()
     */
    @Override
    public int exitValue() {
        // TODO Auto-generated method stub
        return 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Process#destroy()
     */
    @Override
    public void destroy() {
        stopLogging();
        try {
            stdInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            stdOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PipedOutputStream getErrOutputStream() {
        return errOutputStream;
    }

}
