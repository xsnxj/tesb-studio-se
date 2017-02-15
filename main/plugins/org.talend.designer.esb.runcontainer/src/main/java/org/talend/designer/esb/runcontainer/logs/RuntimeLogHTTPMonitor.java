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
package org.talend.designer.esb.runcontainer.logs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.log.LogService;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class RuntimeLogHTTPMonitor {

    static String URL = "http://localhost:8040/system/console/logs?traces=true&minLevel=" + LogService.LOG_INFO;

    static String BASIC = "karaf:karaf";

    private static RuntimeLogHTTPMonitor instance;

    private List<RuntimeLogHTTPAdapter> listeners;

    private Timer httpLogTimer;

    private HttpLoggingTask httpLoggingTask;

    private long tailMilliSecond = 0;

    // private boolean scheduled = false;

    RuntimeLogHTTPMonitor() {
        // init
        listeners = new ArrayList<RuntimeLogHTTPAdapter>();
        httpLogTimer = new Timer();
        httpLoggingTask = new HttpLoggingTask();
    }

    public static RuntimeLogHTTPMonitor createRuntimeLogHTTPMonitor() {
        if (instance == null) {
            instance = new RuntimeLogHTTPMonitor();

        }
        return instance;
    }

    public void addLogLictener(RuntimeLogHTTPAdapter listener) {
        listeners.add(listener);
    }

    public boolean startLogging() throws IOException {
        // httpLogTimer.cancel();
        if (httpLoggingTask.getStatus() == 0) {
            httpLogTimer.schedule(httpLoggingTask, 0, 600);
        }
        return true;
    }

    public boolean stopLogging() {
        httpLogTimer.cancel();
        return true;
    }

    public static void main(String[] args) throws Exception {
        RuntimeLogHTTPMonitor monitor = RuntimeLogHTTPMonitor.createRuntimeLogHTTPMonitor();
        monitor.startLogging();
    }

    class HttpLoggingTask extends TimerTask {

        int status = 0;

        public int getStatus() {
            return status;
        }

        @Override
        public boolean cancel() {
            status = 0;
            return super.cancel();
        }

        @Override
        public void run() {
            status = 1;
            URL url;
            try {
                url = new URL(URL);

                String encoding = Base64.getEncoder().encodeToString(BASIC.getBytes("UTF-8"));

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Authorization", "Basic " + encoding);

                InputStream content = (InputStream) connection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(content));
                String line = in.readLine();

                ObjectMapper mapper = new ObjectMapper();

                // Map<String, RuntimeLogsModel[]> maps = mapper.readValue(json, Map.class);
                // maps.get("data");
                int dataPos = line.indexOf("\"data\":") + 7;
                line = line.substring(dataPos, line.length());
                FelixLogsModel[] logs = mapper.readValue(line, FelixLogsModel[].class);

                for (FelixLogsModel runtimeLogsModel : logs) {
                    if (tailMilliSecond < runtimeLogsModel.getReceived()) {
                        tailMilliSecond = runtimeLogsModel.getReceived();
                        for (IRuntimeLogListener listener : listeners) {
                            listener.logReceived(runtimeLogsModel);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
