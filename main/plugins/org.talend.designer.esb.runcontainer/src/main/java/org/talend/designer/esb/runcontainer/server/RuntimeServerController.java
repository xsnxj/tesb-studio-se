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
package org.talend.designer.esb.runcontainer.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.talend.designer.esb.runcontainer.util.JMXUtil;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 *
 * To support local mode and remote mode
 *
 */
public class RuntimeServerController {

    private String karafHome;

    private String host;

    private static Process runtimeProcess;

    private static RuntimeServerController instance;

    private List<RuntimeStatusChangeListener> listeners;

    private RuntimeServerStatusMonitor monitor;

    private RuntimeServerController() {
        listeners = new ArrayList<RuntimeStatusChangeListener>();
        monitor = new RuntimeServerStatusMonitor();
        new Thread(monitor, "Runtime Server Heartbeat").start();
    }

    public static RuntimeServerController getInstance() {
        if (instance == null) {
            instance = new RuntimeServerController();
        }

        return instance;
    }

    public void addStatusChangeListener(RuntimeStatusChangeListener listener) {
        listeners.add(listener);
    }

    public void removeStatusChangeListener(RuntimeStatusChangeListener listener) {
        listeners.remove(listener);
    }

    public Process startLocalRuntimeServer(String karafHome) throws IOException {
        this.karafHome = karafHome;
        if (!isRunning()) {
            File launcher;
            String os = System.getProperty("os.name");
            if (os != null && os.toLowerCase().contains("windows")) {
                launcher = new File(karafHome + "/bin/trun.bat");
            } else {
                launcher = new File(karafHome + "/bin/trun");
            }

            if (launcher.exists()) {
                runtimeProcess = Runtime.getRuntime().exec(launcher.getAbsolutePath()); // rtPb.start();
            } else {
                throw new IOException("Cannot find launcher file in " + launcher.getPath());
            }
        } else {
            runtimeProcess = null;
        }

        return runtimeProcess;
    }

    public void stopRuntimeServer() throws Exception {
        if (isRunning()) {
            JMXUtil.halt();
            // monitor.stop();
        } else {
            throw new IOException("Runtime Server is not Running");
        }
    }

    public Process startClient(String karafHome, String host, String username, String password, String cmd) throws Exception {
        if (isRunning()) {
            File launcher;
            String os = System.getProperty("os.name");
            if (os != null && os.toLowerCase().contains("windows")) {
                launcher = new File(karafHome + "/bin/client.bat");
            } else {
                launcher = new File(karafHome + "/bin/client");
            }
            return Runtime.getRuntime().exec(
                    launcher.getAbsolutePath() + " -h " + host + " -u " + username + " -p " + password + " \"" + cmd + "\"");
        }
        return null;
    }

    public boolean isRunning() {
        if (runtimeProcess != null && runtimeProcess.isAlive()) {
            return true;
        }
        return JMXUtil.testConnection();
    }

    /**
     * Test rt server every 2 seconds <br/>
     *
     * $Id$
     *
     */
    class RuntimeServerStatusMonitor implements Runnable {

        private boolean stop = false;

        private MBeanServerConnection serverJMX;

        private BufferedReader outIs;

        private BufferedReader errIs;

        private boolean status = false;

        /**
         * DOC yyan RuntimeServerController.RuntimeServerStatusMonitor constructor comment.
         */
        public RuntimeServerStatusMonitor() {
            serverJMX = JMXUtil.connectToRuntime();
            if (serverJMX != null) {
                status = true;
                try {
                    String sys = "org.apache.karaf:type=feature,name=trun";
                    ObjectName objectSys = new ObjectName(sys);
                    serverJMX.addNotificationListener(objectSys, new NotificationListener() {

                        @Override
                        public void handleNotification(Notification notification, Object handback) {
                            // System.out.println("------>" + notification.getType());
                            // if ("org.apache.karaf.features.repositoryEvent".equals(notification.getType())) {
                            //
                            // System.out.println("------>"
                            // + ((javax.management.openmbean.CompositeDataSupport) notification.getUserData())
                            // .get("Type"));
                            // }
                            if ("org.apache.karaf.features.featureEvent".equals(notification.getType())) {
                                String region = String.valueOf(((javax.management.openmbean.CompositeDataSupport) notification
                                        .getUserData()).get("Region"));
                                if ("Installed".equals(region)) {
                                    for (RuntimeStatusChangeListener listener : listeners) {
                                        listener.featureInstalled(0);
                                    }
                                } else if ("Uninstalled".equals(region)) {
                                    for (RuntimeStatusChangeListener listener : listeners) {
                                        listener.featureUninstalled(0);
                                    }
                                }
                                // System.out.println("------>" + region);
                            }

                        }
                    }, null, null);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            //
            // this.outIs = new BufferedReader(new InputStreamReader(runtimeProcess.getInputStream()));
            // this.errIs = new BufferedReader(new InputStreamReader(runtimeProcess.getErrorStream()));
        }

        public void stop() {
            stop = true;
        }

        @Override
        public void run() {
            while (!stop) {
                // TODO if is local, should using process
                // System.out.println("isRunning:" + isRunning() + ",status:" + status);
                try {
                    if (isRunning() && status == false) {
                        status = true;
                        for (RuntimeStatusChangeListener listener : listeners) {
                            listener.startRunning();
                        }
                    }
                    if (!isRunning() && status == true) {
                        status = false;
                        for (RuntimeStatusChangeListener listener : listeners) {
                            listener.stopRunning();
                        }
                    }
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
