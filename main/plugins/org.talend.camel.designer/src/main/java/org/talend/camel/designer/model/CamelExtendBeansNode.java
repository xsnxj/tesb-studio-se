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
package org.talend.camel.designer.model;

import java.util.ArrayList;
import java.util.List;

import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.commons.ui.runtime.repository.IExtendRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelExtendBeansNode implements IExtendRepositoryNode {

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.repository.IExtendRepositoryNode#getNodeImage()
     */
    public IImage getNodeImage() {
        // TODO Auto-generated method stub
        return ECamelCoreImage.BEAN_ICON;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.repository.IExtendRepositoryNode#getOrdinal()
     */
    public int getOrdinal() {
        // TODO Auto-generated method stub
        return 16;
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
