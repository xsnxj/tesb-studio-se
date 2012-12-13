package org.talend.designer.esb.webservice;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class WebServiceComponentPlugin extends AbstractUIPlugin {

	public boolean hasRepositoryServices = false;

	// The shared instance
	private static WebServiceComponentPlugin plugin;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		Bundle repositoryServices = Platform
				.getBundle("org.talend.repository.services");
		if (repositoryServices != null) {
			hasRepositoryServices = true;
		}
	}

	// http://jira.talendforge.org/browse/TESB-3602 LiXiaopeng 2011-10-24
	// /**
	// * Set WS_HTTP_PORT_PREFERENCE into java system property. DOC LiXP Comment
	// method "loadCustomProperty".
	// */
	// public void loadCustomProperty() {
	// String defaultPort =
	// getPreferenceStore().getString(WS_HTTP_PORT_PREFERENCE);
	// System.getProperties().put(WS_HTTP_PORT_PREFERENCE, defaultPort);
	// }
	// protected void initializeDefaultPreferences(IPreferenceStore store) {
	// store.setDefault(WS_HTTP_PORT_PREFERENCE, 8088);
	// }
	// End

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
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

	public static boolean hasRepositoryServices() {
		return getDefault().hasRepositoryServices;
	}

    public static IStatus getStatus(final String message, final Throwable e) {
        String msg = (message != null) ? message : ((e.getMessage() != null) ? e.getMessage() : e.getClass().getName());
        final String pluginId = getDefault().getBundle().getSymbolicName();

        List<IStatus> exStatus = new ArrayList<IStatus>();

        exStatus.add(new Status(IStatus.ERROR, pluginId, e.getClass().getName(), e));
        for (StackTraceElement el : e.getStackTrace()) {
            exStatus.add(new Status(IStatus.ERROR, pluginId, el.toString(), null));
        }
        return new MultiStatus(pluginId, 0, exStatus.toArray(new IStatus[exStatus.size()]), msg, null);
    }

}
