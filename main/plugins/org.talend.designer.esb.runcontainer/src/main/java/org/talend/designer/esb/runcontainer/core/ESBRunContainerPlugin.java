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

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.talend.designer.esb.runcontainer.server.RuntimeServerController;

public class ESBRunContainerPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.talend.designer.esb.runcontainer"; //$NON-NLS-1$

    // The shared instance
    private static ESBRunContainerPlugin plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        RuntimeServerController osgiController = RuntimeServerController.getInstance();
        if (osgiController != null && osgiController.isRunning()) {
            osgiController.stopRuntimeServer();
        }
        plugin = null;
        super.stop(context);
    }

    public static ESBRunContainerPlugin getDefault() {
        return plugin;
    }
}
