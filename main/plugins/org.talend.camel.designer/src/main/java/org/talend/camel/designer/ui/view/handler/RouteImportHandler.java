// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.ui.view.handler;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.runtime.model.emf.TalendXMIResource;
import org.talend.repository.items.importexport.handlers.HandlerUtil;
import org.talend.repository.items.importexport.handlers.imports.ImportRepTypeHandler;
import org.talend.repository.items.importexport.handlers.model.ImportItem;
import org.talend.repository.items.importexport.manager.ResourcesManager;

/**
 * DOC ggu class global comment. Detailled comment
 */
public class RouteImportHandler extends ImportRepTypeHandler {

    /**
     * DOC ggu RouteImportHandler constructor comment.
     */
    public RouteImportHandler() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.repository.items.importexport.handlers.imports.ImportRepTypeHandler#createItemResource(org.eclipse
     * .emf.common.util.URI)
     */
    @Override
    protected Resource createItemResource(URI pathUri) {
        // maybe, because it's same job,joblet
        return new TalendXMIResource(pathUri);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.repository.items.importexport.handlers.imports.ImportRepTypeHandler#copyReferenceFiles(org.talend
     * .repository.items.importexport.ui.wizard.imports.managers.ResourcesManager,
     * org.talend.repository.items.importexport.ui.wizard.imports.models.ItemRecord)
     */
    @Override
    protected boolean copyReferenceFiles(ResourcesManager resManager, ImportItem selectedItemRecord) throws IOException {
        HandlerUtil.copyScreenshotFile(resManager, selectedItemRecord);
        return super.copyReferenceFiles(resManager, selectedItemRecord);
    }

}
