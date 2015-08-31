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
package org.talend.designer.camel.resource.handler;

import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.repository.items.importexport.handlers.imports.ImportRepTypeHandler;
import org.talend.repository.items.importexport.handlers.model.ImportItem;

/**
 * DOC ggu class global comment. Detailled comment
 */
public class RouteResourceImportHandler extends ImportRepTypeHandler {

    /**
     * DOC ggu RouteResourceImportHandler constructor comment.
     */
    public RouteResourceImportHandler() {
        super();
    }

    /*
     * TESB-16305: Set null for reference item name on importing
     */
    @Override
    protected void beforeCreatingItem(ImportItem importItem) {
        Property property = importItem.getProperty();
        if (property != null) {
            for (Object itemRefObj : property.getItem().getReferenceResources()) {
                ReferenceFileItem refItem = (ReferenceFileItem) itemRefObj;
                if (refItem.getName() != null) {
                    refItem.setName(null);
                }
            }
        }
    }

}
