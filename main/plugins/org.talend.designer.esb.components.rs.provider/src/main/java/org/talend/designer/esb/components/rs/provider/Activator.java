package org.talend.designer.esb.components.rs.provider;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.talend.designer.esb.components.rs.provider"; //$NON-NLS-1$

	public static final String REST_URI_PREFERENCE = "restServiceDefaultUri";

	public static final String REST_URI_DEFAULT = "http://127.0.0.1:8090/";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public void loadCustomProperty() {
		String defaultRestUri = getPreferenceStore().getString(REST_URI_PREFERENCE);
		System.getProperties().put(REST_URI_PREFERENCE, defaultRestUri);
	}

	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(REST_URI_PREFERENCE, REST_URI_DEFAULT);
	}

}
