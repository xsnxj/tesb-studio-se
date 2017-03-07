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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.TabularDataSupport;
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

    /**
     * for Job installation
     * 
     * @param bundle
     * @return
     * @throws ReflectionException
     */
    public static long[] installBundle(File bundle) throws Exception {

        try {

            MBeanServerConnection mbsc = connectToRuntime();
            // String KARAF_BUNDLE_MBEAN = "org.apache.karaf:type=feature,name=trun";
            String KARAF_BUNDLE_MBEAN = "org.apache.karaf:type=bundle,name=trun";
            ObjectName objectBundle = new ObjectName(KARAF_BUNDLE_MBEAN);

            Set<Object> existsBundles = ((TabularDataSupport) mbsc.getAttribute(objectBundle, "Bundles")).keySet();

            // mbsc.invoke(objectName, "addRepository", new Object[] { "file:E:/tmp/alltest/" + artifactId
            // + "-feature/repository/local_project/" + artifactId + "/" + artifactId + "-bundle/0.1/" + artifactId
            // + "-bundle-0.1.jar" }, new String[] { String.class.getName() });
            // Object info = mbsc.invoke(objectName, "infoFeature", new Object[] { artifactId + "-feature" },
            // new String[] { String.class.getName() });

            Object bundleId = mbsc.invoke(objectBundle, "install", new Object[] { "file:" + bundle.getAbsolutePath() },
                    new String[] { String.class.getName() });

            Set<Object> newBundles = ((TabularDataSupport) mbsc.getAttribute(objectBundle, "Bundles")).keySet();
            newBundles.removeAll(existsBundles);
            Object[] bundleIds = newBundles.toArray();
            long[] newIds = new long[bundleIds.length];
            for (int i = 0; i < bundleIds.length; i++) {
                String id = bundleIds[i].toString();
                newIds[i] = Long.parseLong(id.substring(1, id.length() - 1));
                mbsc.invoke(objectBundle, "start", new Object[] { String.valueOf(newIds[i]) },
                        new String[] { String.class.getName() });
            }

            return newIds; // bundleId instanceof Long ? (long) bundleId : 0;
        } finally {
            closeConnection();
        }
    }

    public static void uninstallBundle(long bundleID) throws Exception {

        try {

            MBeanServerConnection mbsc = connectToRuntime();
            // String KARAF_BUNDLE_MBEAN = "org.apache.karaf:type=feature,name=trun";
            String KARAF_BUNDLE_MBEAN = "org.apache.karaf:type=bundle,name=trun";
            ObjectName objectBundle = new ObjectName(KARAF_BUNDLE_MBEAN);

            Object bundleId = mbsc.invoke(objectBundle, "uninstall", new Object[] { String.valueOf(bundleID) },
                    new String[] { String.class.getName() });
        } finally {
            closeConnection();
        }
    }

    /**
     * for Route installation
     * 
     * @param kar
     * @throws ReflectionException
     */
    public static String[] installKar(File kar) throws Exception {

        MBeanServerConnection mbsc = connectToRuntime();
        String KARAF_KAR_MBEAN = "org.apache.karaf:type=kar,name=trun";

        ObjectName objectKar = new ObjectName(KARAF_KAR_MBEAN);

        ArrayList existKars = (ArrayList) mbsc.getAttribute(objectKar, "Kars");

        mbsc.invoke(objectKar, "install", new Object[] { "file:" + kar.getAbsolutePath().replaceAll("\\\\", "/") },
                new String[] { String.class.getName() });

        ArrayList kars = (ArrayList) mbsc.getAttribute(objectKar, "Kars");
        kars.removeAll(existKars);

        String[] addedKars = new String[kars.size()];
        for (int i = 0; i < kars.size(); i++) {
            addedKars[i] = String.valueOf(kars.get(i));
        }
        return addedKars;
    }

    public static void uninstallKar(String karID) throws Exception {

        MBeanServerConnection mbsc = connectToRuntime();
        String KARAF_KAR_MBEAN = "org.apache.karaf:type=kar,name=trun";

        ObjectName objectKar = new ObjectName(KARAF_KAR_MBEAN);

        mbsc.invoke(objectKar, "uninstall", new Object[] { karID }, new String[] { String.class.getName() });

    }

    public static long findBundleIDWithKarName(String karName) {

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
            // try {
            // jmxc.close();
            // } catch (IOException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
        }
        connectNumber--;
    }
}
