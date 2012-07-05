package org.talend.designer.camel.dependencies.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class UIActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.talend.designer.camel.dependencies.ui"; //$NON-NLS-1$

	// The shared instance
	private static UIActivator plugin;
	
	public static final String DEPEN_ICON = "icons/dependencies.gif"; //$NON-NLS-1$
	public static final String IMPORT_PKG_ICON = "icons/importPackage.gif"; //$NON-NLS-1$
	public static final String REQUIRE_BD_ICON = "icons/requireBundle.gif"; //$NON-NLS-1$
	public static final String BUNDLE_CP_ICON = "icons/bundleClass.gif"; //$NON-NLS-1$
	public static final String REFRESH_ICON = "icons/refresh.gif"; //$NON-NLS-1$
	public static final String GRAY_REM_ICON = "icons/gray_rem.gif"; //$NON-NLS-1$
	public static final String HIGHLIGHT_REM_ICON = "icons/highlight_rem.gif"; //$NON-NLS-1$
	/**
	 * The constructor
	 */
	public UIActivator() {
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
	public static UIActivator getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(DEPEN_ICON, getImageDescriptor(DEPEN_ICON).createImage());
		reg.put(BUNDLE_CP_ICON, getImageDescriptor(BUNDLE_CP_ICON)
				.createImage());
		reg.put(REQUIRE_BD_ICON, getImageDescriptor(REQUIRE_BD_ICON)
				.createImage());
		reg.put(IMPORT_PKG_ICON, getImageDescriptor(IMPORT_PKG_ICON)
				.createImage());
		reg.put(REFRESH_ICON, getImageDescriptor(REFRESH_ICON)
				.createImage());
		reg.put(GRAY_REM_ICON, getImageDescriptor(GRAY_REM_ICON)
				.createImage());
		reg.put(HIGHLIGHT_REM_ICON, getImageDescriptor(HIGHLIGHT_REM_ICON)
				.createImage());
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return imageDescriptorFromPlugin(PLUGIN_ID, key);
	}

	public static Image getImage(String path) {
		return getDefault().getImageRegistry().get(path);
	}
}
