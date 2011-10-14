package org.talend.designer.esb.webservice;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class WebServiceComponentPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.talend.designer.esb.webservice";

    public static final String WS_HTTP_PORT_PREFERENCE = "wsHttpPort";

    // The shared instance
    private static WebServiceComponentPlugin plugin;

    /**
     * The constructor
     */
    public WebServiceComponentPlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        loadCustomProperty();
    }

    /**
     * Set WS_HTTP_PORT_PREFERENCE into java system property. DOC LiXP Comment method "loadCustomProperty".
     */
    public void loadCustomProperty() {
        String defaultPort = getPreferenceStore().getString(WS_HTTP_PORT_PREFERENCE);
        System.getProperties().put(WS_HTTP_PORT_PREFERENCE, defaultPort);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static WebServiceComponentPlugin getDefault() {
        return plugin;
    }

    protected void initializeDefaultPreferences(IPreferenceStore store) {
        store.setDefault(WS_HTTP_PORT_PREFERENCE, 8088);
    }
}
