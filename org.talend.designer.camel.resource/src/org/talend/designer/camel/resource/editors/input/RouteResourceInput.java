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
import org.talend.core.model.properties.Item;
import org.talend.designer.camel.resource.core.util.RouteResourceUtil;
import org.talend.repository.editor.RepositoryEditorInput;

/**
 * @author xpli
 * 
 */
public class RouteResourceInput extends RepositoryEditorInput {

	protected RouteResourceInput(IFile file, Item item) {
		super(file, item);
	}

	/**
	 * Create instance
	 * 
	 * @param item
	 * @return
	 */
	public static RouteResourceInput createInput(Item item) {
		return new RouteResourceInput(RouteResourceUtil.getSourceFile(item),
				item);
	}

}
