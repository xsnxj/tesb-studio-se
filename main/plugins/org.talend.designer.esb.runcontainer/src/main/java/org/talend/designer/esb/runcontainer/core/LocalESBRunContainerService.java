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
package org.talend.designer.esb.runcontainer.core;

import javax.management.MBeanServerConnection;

import org.talend.core.model.components.ComponentCategory;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.Property;
import org.talend.designer.esb.runcontainer.process.RunContainerProcessor;
import org.talend.designer.esb.runcontainer.util.JMXUtil;
import org.talend.designer.runprocess.IESBRunContainerService;
import org.talend.designer.runprocess.java.JavaProcessor;
import org.talend.repository.utils.EmfModelUtils;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 * TESB-18750, Locally ESB runtime server service
 */
public class LocalESBRunContainerService implements IESBRunContainerService {

    private boolean enableRuntime;

    @Override
    public void enableRuntime(boolean inRuntime) {
        this.enableRuntime = inRuntime;
    }

    @Override
    public boolean isRuntimeEnable() {
        return enableRuntime;
    }

    /**
     * DOC The local Runtime for ESB will be only taken into account if the user runs an ESB Artifact:
     * 
     * A route (Any Route)
     * 
     * A DataService (SOAP) A DataService (REST)
     * 
     * For tRESTClient or tESBConsumer we use the ESB Runtime
     */
    @Override
    public JavaProcessor createJavaProcessor(IProcess process, Property property, boolean filenameFromLabel) {
        if (enableRuntime) {
            if (ComponentCategory.CATEGORY_4_CAMEL.getName().equals(process.getComponentsType())) {
                return new RunContainerProcessor(process, property, filenameFromLabel);
            } else if (ComponentCategory.CATEGORY_4_DI.getName().equals(process.getComponentsType())) {
                if (EmfModelUtils.getComponentByName((ProcessItem) property.getItem(), "tESBProviderRequest", "tESBConsumer",
                        "tRESTClient") != null) {
                    return new RunContainerProcessor(process, property, filenameFromLabel);
                }
            }
        }
        return null;
    }

    @Override
    public MBeanServerConnection getJMXServerConnection() {
        return JMXUtil.createJMXconnection();
    }
}
