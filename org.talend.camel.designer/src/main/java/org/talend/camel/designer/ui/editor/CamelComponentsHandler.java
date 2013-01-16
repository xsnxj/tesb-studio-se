// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.ui.editor;

import java.util.Iterator;
import java.util.List;

import org.talend.core.model.components.ComponentCategory;
import org.talend.core.model.components.IComponent;
import org.talend.core.model.components.IComponentsHandler;

/**
 * created by nrousseau on Jan 16, 2013 Detailled comment
 * 
 */
public class CamelComponentsHandler implements IComponentsHandler {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponentsHandler#filterComponents(java.util.List)
     */
    public List<IComponent> filterComponents(List<IComponent> allComponents) {
        if (allComponents != null && allComponents.size() > 0) {
            Iterator<IComponent> componentsIterator = allComponents.iterator();
            while (componentsIterator.hasNext()) {
                IComponent component = componentsIterator.next();
                String compType = component.getPaletteType();
                if (compType != null && !ComponentCategory.CATEGORY_4_CAMEL.getName().equals(compType)) {
                    componentsIterator.remove();
                }
            }
        }
        return allComponents;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponentsHandler#sortComponents(java.util.List)
     */
    public List<IComponent> sortComponents(List<IComponent> filteredComponents) {
        return filteredComponents;
    }

}
