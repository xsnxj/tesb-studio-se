package org.talend.designer.esb.runcontainer.core;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

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
        plugin = null;
        super.stop(context);
    }

    public static ESBRunContainerPlugin getDefault() {
        return plugin;
    }
}
