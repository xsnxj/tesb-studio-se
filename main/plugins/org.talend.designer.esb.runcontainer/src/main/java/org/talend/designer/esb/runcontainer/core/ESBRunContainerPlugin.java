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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.talend.core.GlobalServiceRegister;
import org.talend.designer.esb.runcontainer.preferences.RunContainerPreferenceInitializer;
import org.talend.designer.esb.runcontainer.server.RuntimeServerController;
import org.talend.designer.runprocess.IESBRunContainerService;

public class ESBRunContainerPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.talend.designer.esb.runcontainer"; //$NON-NLS-1$

    // The shared instance
    private static ESBRunContainerPlugin plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        initRuntimePreference();
    }

    private void initRuntimePreference() {

        IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
        boolean runtimeEnable = store.getBoolean(RunContainerPreferenceInitializer.P_ESB_IN_OSGI);

        IESBRunContainerService esbContainerService = (IESBRunContainerService) GlobalServiceRegister.getDefault().getService(
                IESBRunContainerService.class);
        esbContainerService.enableRuntime(runtimeEnable);
        IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent event) {
                if (RunContainerPreferenceInitializer.P_ESB_IN_OSGI.equals(event.getProperty())) {
                    esbContainerService.enableRuntime(Boolean.valueOf(event.getNewValue().toString()));
                }
            }
        };
        store.addPropertyChangeListener(propertyChangeListener);
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
