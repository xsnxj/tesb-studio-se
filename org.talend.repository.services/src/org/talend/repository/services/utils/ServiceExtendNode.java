// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.services.utils;

import java.util.ArrayList;
import java.util.List;

import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.core.repository.IExtendRepositoryNode;
import org.talend.repository.model.RepositoryNode;

public class ServiceExtendNode implements IExtendRepositoryNode {

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.repository.IExtendRepositoryNode#getNodeImage()
     */
    public IImage getNodeImage() {
        return ECoreImage.METADATA_WSDL_SCHEMA_ICON;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.repository.IExtendRepositoryNode#getOrdinal()
     */
    public int getOrdinal() {
        return 5;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.repository.IExtendRepositoryNode#getChildren()
     */
    public Object[] getChildren() {
        List<RepositoryNode> children = new ArrayList<RepositoryNode>();
        return children.toArray(new RepositoryNode[0]);
    }

}
