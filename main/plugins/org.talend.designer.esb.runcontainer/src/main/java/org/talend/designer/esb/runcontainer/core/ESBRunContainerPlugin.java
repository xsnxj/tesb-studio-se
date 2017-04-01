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
import org.talend.designer.esb.runcontainer.process.RunContainerProcessContextManager;
import org.talend.designer.esb.runcontainer.server.RuntimeServerController;
import org.talend.designer.runprocess.RunProcessContextManager;
import org.talend.designer.runprocess.RunProcessPlugin;

public class ESBRunContainerPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.talend.designer.esb.runcontainer"; //$NON-NLS-1$

    // The shared instance
    private static ESBRunContainerPlugin plugin;

    private RunProcessContextManager defaultManager;

    private RunProcessContextManager osgiManager;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        // boolean inOSGi = getPreferenceStore().getBoolean(RunContainerPreferenceInitializer.P_ESB_RUNTIME_IN_OSGI);
        // if (inOSGi) {
        // BundleListener bundleListener = new BundleListener() {
        //
        // @Override
        // public void bundleChanged(BundleEvent event) {
        // if (event.getBundle().getSymbolicName().equals(RunProcessPlugin.PLUGIN_ID)) {
        // useOsgiManager(true);
        // context.removeBundleListener(this);
        // }
        // }
        // };
        // context.addBundleListener(bundleListener);
        // }
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

    public void useOsgiManager(boolean isOsgiManager) {
        if (isOsgiManager) {
            RunProcessContextManager manager = RunProcessPlugin.getDefault().getRunProcessContextManager();
            if (manager != null && manager.getClass() != RunContainerProcessContextManager.class) {
                defaultManager = manager;
                osgiManager = new RunContainerProcessContextManager();
                RunProcessPlugin.getDefault().setRunProcessContextManager(osgiManager);
            }
        } else {
            RunProcessPlugin.getDefault().setRunProcessContextManager(defaultManager);
        }
    }
}
