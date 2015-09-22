// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.ui.routelet;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.talend.camel.designer.CamelDesignerPlugin;
import org.talend.commons.ui.runtime.image.IImage;

public enum ERouteletImages implements IImage {
    ROUTELET_COMPONENT_32("/icons/routelet/joblet_palette.png"), //$NON-NLS-1$
    ROUTELET_COMPONENT_16("/icons/routelet/joblet_icon.png"), //$NON-NLS-1$
    ;

    private String path;

    ERouteletImages(String path) {
        this.path = path;
    }

    /**
     * Getter for path.
     * 
     * @return the path
     */
    @Override
    public String getPath() {
        return this.path;
    }

    /**
     * Getter for clazz.
     * 
     * @return the clazz
     */
    @Override
    public Class<?> getLocation() {
        return ERouteletImages.class;
    }

    /**
     * DOC qzhang Comment method "getURLImageDescriptor".
     * 
     * @param images
     * @return
     */
    public static ImageDescriptor getURLImageDescriptor(ERouteletImages images) {
        Bundle b = CamelDesignerPlugin.getDefault().getBundle();
        URL url = null;
        try {
            url = FileLocator.toFileURL(FileLocator.find(b, new Path(images.getPath()), null));
            return ImageDescriptor.createFromURL(url);
        } catch (IOException e) {
            throw new RuntimeException("Image Not Exist: " + images.getPath());
        }
    }
}
