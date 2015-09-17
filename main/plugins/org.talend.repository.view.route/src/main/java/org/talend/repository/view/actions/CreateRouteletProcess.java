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

import org.talend.camel.designer.ui.CreateCamelProcess;
import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.camel.model.CamelRepositoryNodeType;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.repository.ERepositoryObjectType;

public class CreateRouteletProcess extends CreateCamelProcess {

    private static final String CREATE_LABEL = "Create Routelet";

    public CreateRouteletProcess() {
        super();
        this.setText(CREATE_LABEL);
        this.setToolTipText(CREATE_LABEL);
        this.setImageDescriptor(ImageProvider.getImageDesc(ECamelCoreImage.ROUTELET_ICON));
    }

    public CreateRouteletProcess(boolean isToolbar) {
        this();
        setToolbar(isToolbar);
    }

    @Override
    public ERepositoryObjectType getProcessType() {
        return CamelRepositoryNodeType.repositoryRouteletType;
    }

}
