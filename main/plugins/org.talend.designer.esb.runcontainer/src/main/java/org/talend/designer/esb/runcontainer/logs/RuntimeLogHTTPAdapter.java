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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.osgi.service.log.LogService;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class RuntimeLogHTTPAdapter implements IRuntimeLogListener {

    static String URL = "http://localhost:8040/system/console/logs?traces=true&minLevel=" + LogService.LOG_INFO;

    static String BASIC = "karaf:karaf";

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.esb.runcontainer.logs.IRuntimeLogListener#addListenerByBundleId(int)
     */
    @Override
    public void addListenerByBundleId(int bundleId) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.designer.esb.runcontainer.logs.IRuntimeLogListener#logReceived(org.talend.designer.esb.runcontainer
     * .logs.FelixLogsModel)
     */
    @Override
    public void logReceived(FelixLogsModel logsModel) {
        // TODO Auto-generated method stub
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        Date date = new Date(logsModel.getReceived());

        System.out.println(sdf.format(date) + " | " + logsModel.getLevel() + " | " + logsModel.getBundleId() + " - "
                + logsModel.getBundleName() + " | " + logsModel.getMessage());
    }

    // test
    public static void main(String[] args) throws Exception {

        URL url = new URL(URL);
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,sss");

            Date date = new Date(runtimeLogsModel.getReceived());

            System.out.println(sdf.format(date) + " | " + runtimeLogsModel.getLevel() + " | " + runtimeLogsModel.getBundleId()
                    + " - " + runtimeLogsModel.getBundleName() + " | " + runtimeLogsModel.getMessage());
        }
    }
}
