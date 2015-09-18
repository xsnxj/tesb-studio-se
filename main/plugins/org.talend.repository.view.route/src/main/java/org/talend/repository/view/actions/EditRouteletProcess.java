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
package org.talend.repository.view.actions;

import org.talend.camel.core.model.camelProperties.RouteletProcessItem;
import org.talend.camel.designer.ui.EditCamelProcess;
import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.camel.model.CamelRepositoryNodeType;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.repository.ERepositoryObjectType;

/**
 * DOC smallet class global comment. Detailled comment <br/>
 * 
 * $Id: EditProcess.java 52559 2010-12-13 04:14:06Z nrousseau $
 * 
 */
public class EditRouteletProcess extends EditCamelProcess {

    private final String EDIT_LABEL = "Edit Routelet";
    private final String OPEN_LABEL = "Open Routelet";

    public EditRouteletProcess() {
        super();
        this.setImageDescriptor(ImageProvider.getImageDesc(ECamelCoreImage.ROUTELET_ICON));
    }

    @Override
    protected ERepositoryObjectType getProcessType() {
        return CamelRepositoryNodeType.repositoryRouteletType;
    }

    @Override
    protected String getLabel(boolean editable) {
        return editable ? EDIT_LABEL : OPEN_LABEL;
    }

    @Override
    public Class<?> getClassForDoubleClick() {
        return RouteletProcessItem.class;
    }

}
