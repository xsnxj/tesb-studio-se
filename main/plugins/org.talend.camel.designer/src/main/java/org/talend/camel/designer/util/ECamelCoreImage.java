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
package org.talend.camel.designer.util;

import org.talend.commons.ui.runtime.image.IImage;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public enum ECamelCoreImage implements IImage {
    ROUTE_RESOURCE_ICON("/icons/route-resource.png"), //$NON-NLS-1$
    BEAN_ICON("/icons/bean.gif"), //$NON-NLS-1$
    ROUTES_ICON("/icons/routes_icon.png"), //$NON-NLS-1$
    ROUTES_ICON_EDITOR("/icons/routes_icon_editor.png"), //$NON-NLS-1$
    BEAN_WIZ("/icons/bean_wiz.png");

    private String path;

    ECamelCoreImage() {
        this.path = "/icons/unknown.gif"; //$NON-NLS-1$
    }

    ECamelCoreImage(String path) {
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
    public Class getLocation() {
        return ECamelCoreImage.class;
    }
}
