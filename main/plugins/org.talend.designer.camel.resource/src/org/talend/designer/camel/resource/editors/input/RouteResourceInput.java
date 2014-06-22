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
package org.talend.designer.camel.resource.editors.input;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeListener;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.core.model.properties.Item;
import org.talend.designer.camel.resource.core.util.RouteResourceUtil;
import org.talend.designer.camel.resource.ui.util.RouteResourceEditorUtil;
import org.talend.repository.editor.RepositoryEditorInput;
import org.talend.repository.model.IRepositoryNode;

/**
 * @author xpli
 * 
 */
public class RouteResourceInput extends RepositoryEditorInput {

	private IResourceChangeListener listener;

	protected RouteResourceInput(IFile file, Item item) {
		super(file, item);
	}

	/**
	 * Create instance
	 * 
	 * @param item
	 * @return
	 */
	public static RouteResourceInput createInput(IRepositoryNode node, RouteResourceItem item) {
		RouteResourceInput routeResourceInput = new RouteResourceInput(
				RouteResourceUtil.getSourceFile(item), item);
		routeResourceInput.setRepositoryNode(node);
		routeResourceInput.setReadOnly(RouteResourceEditorUtil.isReadOnly(node));
		return routeResourceInput;
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setListener(IResourceChangeListener listener) {
		this.listener = listener;
	}

	/**
	 * @return the listener
	 */
	public IResourceChangeListener getListener() {
		return listener;
	}

}
