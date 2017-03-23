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
package org.talend.designer.esb.runcontainer.logs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.log.LogService;
import org.talend.designer.esb.runcontainer.core.ESBRunContainerPlugin;
import org.talend.designer.esb.runcontainer.preferences.RunContainerPreferenceInitializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Retrieve runtime by rest request
 */
public class RuntimeLogHTTPMonitor {

    private static RuntimeLogHTTPMonitor instance;

    private List<RuntimeLogHTTPAdapter> listeners;

    private Timer httpLogTimer;

    private HttpLoggingTask httpLoggingTask;

    private long tailMilliSecond = 0;

    private Map<RuntimeLogHTTPAdapter, Long> listenerMap;

    // private boolean scheduled = false;

    RuntimeLogHTTPMonitor() {
        // init
        listeners = new ArrayList<RuntimeLogHTTPAdapter>();
        httpLoggingTask = new HttpLoggingTask();
        listenerMap = new HashMap<RuntimeLogHTTPAdapter, Long>();
    }

    public static RuntimeLogHTTPMonitor createRuntimeLogHTTPMonitor() {
        if (instance == null) {
            instance = new RuntimeLogHTTPMonitor();
        }
        return instance;
    }

    public void addLogLictener(RuntimeLogHTTPAdapter listener) {
        listenerMap.put(listener, System.currentTimeMillis());
        listeners.add(listener);
    }

    public boolean startLogging() {
        // httpLogTimer.cancel();
        if (!httpLoggingTask.isRunning()) {
            new Thread(httpLoggingTask, "ESB Runtime Logging Monitor").start();
        }
        // if (httpLoggingTask.getStatus() == 0) {
        // httpLogTimer.schedule(httpLoggingTask, 0);
        // }
        return true;
    }

    public boolean stopLogging() {
        httpLoggingTask.cancel();
        return true;
    }

    public static void main(String[] args) throws Exception {
        RuntimeLogHTTPMonitor monitor = RuntimeLogHTTPMonitor.createRuntimeLogHTTPMonitor();
        monitor.startLogging();
        monitor.addLogLictener(new RuntimeLogHTTPAdapter() {

            @Override
            public void logReceived(FelixLogsModel logsModel) {
                super.logReceived(logsModel);
            }
        });
    }

    class HttpLoggingTask implements Runnable {

        boolean running = false;

        public boolean isRunning() {
            return running;
        }

        public void cancel() {
            if (listeners.size() == 0) {
                running = false;
            }
        }

        @Override
        public void run() {
            running = true;
            long latestTime = 0;
            long current = System.currentTimeMillis();
            URL url;

            IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
            boolean sysLog = store.getBoolean(RunContainerPreferenceInitializer.P_ESB_RUNTIME_SYS_LOG);

            store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
            String host = store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_HOST);
            String URL = "http://" + host + ":8040/system/console/logs?traces=true&minLevel=" + LogService.LOG_INFO;

            String username = store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_USERNAME);
            String password = store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_PASSWORD);
            String BASIC = username + ":" + password;

            do {
                try {
                    url = new URL(URL);

                    String encoding = Base64.getEncoder().encodeToString(BASIC.getBytes("UTF-8"));

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Authorization", "Basic " + encoding);

                    InputStream content = connection.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(content));
                    String line = in.readLine();

                    ObjectMapper mapper = new ObjectMapper();

                    int dataPos = line.indexOf("\"data\":") + 7;
                    line = line.substring(dataPos, line.length());
                    FelixLogsModel[] logs = mapper.readValue(line, FelixLogsModel[].class);
                    if (logs[0].getReceived() >= current) {
                        for (int i = logs.length - 1; i >= 0; i--) {
                            if (latestTime >= logs[i].getReceived()) {
                                continue;
                            }
                            // ignore system logs
                            if (sysLog && logs[i].getBundleName().startsWith("Apache Karaf")) {
                                continue;
                            }
                            if (logs[i].getMessage().indexOf("Authentication") == 0) {
                                continue;
                            }
                            for (IRuntimeLogListener listener : listeners) {
                                if (listenerMap.get(listener) < logs[i].getReceived()) {
                                    listener.logReceived(logs[i]);
                                }
                            }
                        }
                        latestTime = logs[0].getReceived();
                    }

                    synchronized (this) {
                        try {
                            final long waitTime = 500;
                            wait(waitTime);
                        } catch (InterruptedException e) {
                            // Do nothing
                        }
                    }
                } catch (IOException e) {
                    // e.printStackTrace();
                }
            } while (running);
        }
    }

    public void removeLogLictener(RuntimeLogHTTPAdapter listener) {
        listeners.remove(listener);
        listenerMap.remove(listener);
        if (listeners.size() == 0) {
            stopLogging();
        }
    }
}
