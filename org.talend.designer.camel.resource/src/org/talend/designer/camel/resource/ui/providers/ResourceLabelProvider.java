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
package org.talend.designer.camel.resource.ui.providers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.talend.designer.camel.resource.core.model.ResourceDependencyModel;

/**
 * @author xpli
 * 
 */
public class ResourceLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	private static final int COL_NAME = 0;
	private static final int COL_VERSION = 1;

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		if (element instanceof ResourceDependencyModel) {
			ResourceDependencyModel item = (ResourceDependencyModel) element;
			switch (columnIndex) {
			case COL_NAME:
				return item.getItem().getProperty().getDisplayName();
			case COL_VERSION:
				return item.getSelectedVersion();

			default:
				break;
			}
		}
		return element.toString();
	}
}
