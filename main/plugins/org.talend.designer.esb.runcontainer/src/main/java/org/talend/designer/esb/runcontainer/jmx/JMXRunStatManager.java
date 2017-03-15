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
package org.talend.designer.esb.runcontainer.jmx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.talend.core.model.process.INode;
import org.talend.designer.esb.runcontainer.process.RunContainerProcessContext;
import org.talend.designer.esb.runcontainer.util.JMXUtil;
import org.talend.repository.ProjectManager;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class JMXRunStatManager {

    private static JMXRunStatManager manager;

    private static int limitSize = 5;

    private List<RunContainerProcessContext> contextList;

    private Map<String, String> targetNodeToConnectionMap = new HashMap(); // processorId, conn

    private JMXRunStat jmxRunStat;

    private List<JMXPerformanceChangeListener> listeners;

    private String projectLabel;

    public JMXRunStatManager() {
        contextList = new ArrayList();
        listeners = new ArrayList();
        // local_project.let1-local_project
        projectLabel = ProjectManager.getInstance().getCurrentProject().getLabel().toLowerCase();
    }

    private void updateConnectionMap(RunContainerProcessContext esbProcessContext, boolean isAdd) {

        if (esbProcessContext.getProcess() != null) {
            for (INode node : esbProcessContext.getProcess().getGraphicalNodes()) {
                if (node.isActivate()) {
                    for (org.talend.core.model.process.IConnection conn : node.getIncomingConnections()) {
                        if (isAdd) {

                            targetNodeToConnectionMap.put(esbProcessContext.getProcess().getLabel() + "_" + node.getUniqueName(),
                                    conn.getUniqueName());
                        } else {
                            targetNodeToConnectionMap.remove(
                                    esbProcessContext.getProcess().getLabel() + "_" + node.getUniqueName(), conn.getUniqueName());
                        }
                    }
                }
            }
        }
    }

    public static JMXRunStatManager getInstance() {
        if (manager == null) {
            manager = new JMXRunStatManager();
        }
        return manager;
    }

    public void addProcessContext(RunContainerProcessContext context) {
        contextList.add(context);
        updateConnectionMap(context, true);
    }

    public void removeProcessContext(RunContainerProcessContext context) {
        contextList.remove(context);
        updateConnectionMap(context, false);
    }

    public void addTracing(RunContainerProcessContext context) {
        if (jmxRunStat == null || jmxRunStat.stopTracing) {
            jmxRunStat = new JMXRunStat();
            Thread thread = new Thread(jmxRunStat, "JMX_Run_Stat");
            thread.start();
        }
        addProcessContext(context);
    }

    public void stopTracing(RunContainerProcessContext context) {
        removeProcessContext(context);
        if (jmxRunStat != null && contextList.size() == 0) {
            jmxRunStat.stopTracing();
        }
    }

    public void addPerformancesChangeListener(JMXPerformanceChangeListener performanceChangeListener) {
        listeners.add(performanceChangeListener);
    }

    public void removePerformancesChangeListener(JMXPerformanceChangeListener performanceChangeListener) {
        listeners.remove(performanceChangeListener);
    }

    class JMXRunStat implements Runnable {

        private MBeanServerConnection server;

        private boolean stopTracing = false;

        public JMXRunStat() {
            this.server = JMXUtil.connectToRuntime();
        }

        @Override
        public void run() {
            stopTracing = false;
            do {
                if (listeners.size() > 0) {
                    try {
                        // for camel route
                        Set<ObjectName> components = server.queryNames(new ObjectName("org.apache.camel:context=" + projectLabel
                                + ".*,type=processors,name=*"), null);
                        // org.apache.camel:context=local_project.let1-local_project.let1_0_1.let1,type=processors,name="let1_cLog_1"
                        for (ObjectName component : components) {
                            int completed = Integer
                                    .parseInt(String.valueOf(server.getAttribute(component, "ExchangesCompleted")));

                            String camelID = String.valueOf(server.getAttribute(component, "CamelId"));
                            String processorId = String.valueOf(server.getAttribute(component, "ProcessorId"));
                            for (JMXPerformanceChangeListener listener : listeners) {
                                if (listener.getProcessName().equals(camelID.substring(camelID.lastIndexOf('.') + 1))) {
                                    listener.performancesChanged(targetNodeToConnectionMap.get(processorId), completed);
                                }
                            }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!stopTracing);
        }

        /**
         * JMX run stat only stop with stop tracing
         */
        public void stopTracing() {
            stopTracing = true;
        }
    }
}
