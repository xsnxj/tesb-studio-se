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
public class RuntimeLogHTTPListener {

    static String URL = "http://192.168.32.98:8040/system/console/logs?traces=true&minLevel=" + LogService.LOG_INFO;

    static String BASIC = "karaf:karaf";

    public void addListenerByBundleId(int bundleId){
        
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
            System.out.println(runtimeLogsModel.getMessage());
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ssss");

            Date date = new Date(runtimeLogsModel.getReceived());

            System.out.println(sdf.format(date));
        }
    }

}
