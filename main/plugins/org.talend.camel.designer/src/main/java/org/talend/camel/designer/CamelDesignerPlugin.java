package org.talend.camel.designer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CamelDesignerPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.talend.camel.designer"; //$NON-NLS-1$

    public static final String DEPEN_ICON = "icons/dependencies/dependencies.gif"; //$NON-NLS-1$
    public static final String IMPORT_PKG_ICON = "icons/dependencies/importPackage.gif"; //$NON-NLS-1$
    public static final String REQUIRE_BD_ICON = "icons/dependencies/requireBundle.gif"; //$NON-NLS-1$
    public static final String BUNDLE_CP_ICON = "icons/dependencies/bundleClass.gif"; //$NON-NLS-1$
    public static final String REFRESH_ICON = "icons/dependencies/refresh.gif"; //$NON-NLS-1$
    public static final String GRAY_REM_ICON = "icons/dependencies/gray_rem.gif"; //$NON-NLS-1$
    public static final String HIGHLIGHT_REM_ICON = "icons/dependencies/highlight_rem.gif"; //$NON-NLS-1$
    public static final String OPTIONAL_OVERLAY_ICON = "icons/dependencies/optional.gif"; //$NON-NLS-1$
    public static final String IMPORT_PACKAGE_OVERLAY_ICON = "IMPORT_PACKAGE_OVERLAY_ICON"; //$NON-NLS-1$
    public static final String REQUIRE_BUNDLE_OVERLAY_ICON = "REQUIRE_BUNDLE_OVERLAY_ICON"; //$NON-NLS-1$

    // The shared instance
    private static CamelDesignerPlugin plugin;

    /**
     * The constructor
     */
    public CamelDesignerPlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
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
    public static CamelDesignerPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
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
        reg.put(OPTIONAL_OVERLAY_ICON, getImageDescriptor(OPTIONAL_OVERLAY_ICON)
                .createImage());
        reg.put(IMPORT_PACKAGE_OVERLAY_ICON, getOptionalOverlayIcon(getImage(IMPORT_PKG_ICON)));
        reg.put(REQUIRE_BUNDLE_OVERLAY_ICON, getOptionalOverlayIcon(getImage(REQUIRE_BD_ICON)));
    }

    private static Image getOptionalOverlayIcon(Image base) {
        DecorationOverlayIcon decorationOverlayIcon = new DecorationOverlayIcon(
                base, getImageDescriptor(OPTIONAL_OVERLAY_ICON),
                IDecoration.TOP_LEFT);
        return decorationOverlayIcon.createImage();
    }

    public static Image getImage(String path) {
        return getDefault().getImageRegistry().get(path);
    }
}
