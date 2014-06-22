package org.talend.esb.tooling.component.provider;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator extends Plugin implements BundleActivator {

	public static final String PLUGIN_ID = "org.talend.esb.tooling.component.provider";
	private static Activator plugin;

	public void start(BundleContext paramBundleContext)	throws Exception {
		super.start(paramBundleContext);
		plugin = this;
	}

	public void stop(BundleContext paramBundleContext) throws Exception {
		plugin = null;
		super.stop(paramBundleContext);
	}

	public static Activator getDefault() {
		return plugin;
	}

}
