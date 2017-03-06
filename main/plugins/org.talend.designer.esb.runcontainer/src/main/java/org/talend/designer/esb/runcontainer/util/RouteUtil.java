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

import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

public class RouteUtil {

    public static void main(String[] args) throws Exception {

        MBeanServerConnection connection = JMXUtil.connectToRuntime();

        class ClientListener implements NotificationListener {

            public void handleNotification(Notification notification, Object handback) {
                System.out.println("------>" + notification);
            }
        }
        // ClientListener clientListener = new ClientListener();
        // org.apache.camel:context=local_project.let1-local_project.let1_0_1.let1,type=processors,name="let1_cLog_1"
        Set<ObjectName> set = connection.queryNames(new ObjectName(
                "org.apache.camel:context=local_project.let1-local_project.let1_0_1.let1,type=processors,name=*"),
                null);
        for (int i = 1; i < 20; i++) {

            for (ObjectName on : set) {
                System.out.println(on.getCanonicalName() + "------>" + connection.getAttribute(on, "ExchangesCompleted"));
//                try {
//                    java.util.concurrent.TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }

        // set = connection.queryNames(new ObjectName("*:type=routes,*"), null);
        // System.out.println("------>" + set.size());
        // for (ObjectName on : set) {
        // // mbsc.addNotificationListener(on, clientListener, null, null);
        // MBeanInfo mBeanInfo = connection.getMBeanInfo(on);
        // MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
        // for (MBeanAttributeInfo info : attributes) {
        // System.out.println("------>" + info.getName());
        // connection.getAttribute(on, info.getName()).toString();
        // }
        // }

        JMXUtil.closeConnection();

        // try {
        // java.util.concurrent.TimeUnit.SECONDS.sleep(20);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

}
