package org.talend.designer.camel.spring.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CamelSpringUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.talend.designer.camel.spring.ui"; //$NON-NLS-1$

	// The shared instance
	private static CamelSpringUIPlugin plugin;
	
	/**
	 * The constructor
	 */
	public CamelSpringUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
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
	public static CamelSpringUIPlugin getDefault() {
		return plugin;
	}

	public ImageDescriptor getImageDescriptor(String imageFilePath){
        return CamelSpringUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, imageFilePath);
    }
}
