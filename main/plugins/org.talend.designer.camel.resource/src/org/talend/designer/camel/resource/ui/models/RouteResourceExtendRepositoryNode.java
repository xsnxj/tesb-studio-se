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
package org.talend.designer.camel.resource.ui.models;

import java.util.ArrayList;
import java.util.List;

import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.commons.ui.runtime.repository.IExtendRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * @author xpli
 * 
 */
public class RouteResourceExtendRepositoryNode implements IExtendRepositoryNode {

	/**
	 * 
	 */
	public RouteResourceExtendRepositoryNode() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.repository.IExtendRepositoryNode#getChildren()
	 */
	public Object[] getChildren() {
		List<RepositoryNode> children = new ArrayList<RepositoryNode>();
		return children.toArray(new RepositoryNode[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.repository.IExtendRepositoryNode#getNodeImage()
	 */
	public IImage getNodeImage() {
		return ECamelCoreImage.ROUTE_RESOURCE_ICON;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.core.repository.IExtendRepositoryNode#getOrdinal()
	 */
	public int getOrdinal() {
		return 2;
	}

}
