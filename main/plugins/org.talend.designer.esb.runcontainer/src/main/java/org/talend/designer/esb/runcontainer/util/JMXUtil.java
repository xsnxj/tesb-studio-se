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
package org.talend.designer.esb.runcontainer.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 */
public class JMXUtil {

    /**
     * 
     */
    private static final String CREDENTIALS = "jmx.remote.credentials";

    public static final String username = "karaf";

    public static final String password = "karaf";

    public static final String host = "localhost";

    public static final String jmxPort = "44444";

    public static final String karafPort = "1099";

    public static final String instanceName = "trun";

    public static final String serviceUrl = "service:jmx:rmi://" + host + ":" + jmxPort + "/jndi/rmi://" + host + ":" + karafPort
            + "/karaf-" + instanceName;

    private static MBeanServerConnection mbsc;

    private static JMXConnector jmxc;

    private static int connectNumber = 0;

    public static long installBundle(File bundleJar) {

        try {

            MBeanServerConnection mbsc = connectToRuntime();
            // String KARAF_BUNDLE_MBEAN = "org.apache.karaf:type=feature,name=trun";
            String KARAF_BUNDLE_MBEAN = "org.apache.karaf:type=bundle,name=trun";
            ObjectName objectName = new ObjectName(KARAF_BUNDLE_MBEAN);

            // mbsc.invoke(objectName, "addRepository", new Object[] { "file:E:/tmp/alltest/" + artifactId
            // + "-feature/repository/local_project/" + artifactId + "/" + artifactId + "-bundle/0.1/" + artifactId
            // + "-bundle-0.1.jar" }, new String[] { String.class.getName() });
            // Object info = mbsc.invoke(objectName, "infoFeature", new Object[] { artifactId + "-feature" },
            // new String[] { String.class.getName() });

            Object bundleId = mbsc.invoke(objectName, "install", new Object[] { "file:" + bundleJar.toURI().getPath() },
                    new String[] { String.class.getName() });
            mbsc.invoke(objectName, "start", new Object[] { bundleId.toString() }, new String[] { String.class.getName() });

            return bundleId instanceof Long ? (long) bundleId : 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return 0;
    }

    /**
     * if use catched connection
     * 
     * @return
     */
    public static MBeanServerConnection connectToRuntime() {
        if (mbsc == null) {
            HashMap<String, String[]> env = new HashMap<String, String[]>();
            String[] credentials = new String[] { username, password };
            env.put(CREDENTIALS, credentials);
            try {
                JMXServiceURL url = new JMXServiceURL(serviceUrl);
                jmxc = JMXConnectorFactory.connect(url, env);
                mbsc = jmxc.getMBeanServerConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        connectNumber++;
        return mbsc;
    }

    public static void closeConnection() {
        if (jmxc != null && connectNumber < 1) {
//            try {
//                jmxc.close();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
        }
        connectNumber--;
    }
}
