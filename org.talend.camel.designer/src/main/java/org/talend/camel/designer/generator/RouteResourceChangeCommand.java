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
package org.talend.camel.designer.generator;

import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.core.model.process.IElement;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.ui.editor.cmd.PropertyChangeCommand;

/**
 * @author xpli
 * 
 */
public class RouteResourceChangeCommand extends PropertyChangeCommand {

	private IElement element;

	private String propertyName;

	public RouteResourceChangeCommand(IElement elem, String propName,
			Object propValue) {
		super(elem, propName, propValue);

		this.element = elem;
		this.propertyName = propName;
	}

	@Override
	public void execute() {

		IElementParameter currentParam = element
				.getElementParameter(propertyName);
		// TESB 6226
		if (currentParam.getName().equals(
				EParameterName.ROUTE_RESOURCE_TYPE_ID.getName())) {
			setUpdate(true);
		}
		super.execute();

		IElementParameter uriParam = element
				.getElementParameter(EParameterName.ROUTE_RESOURCE_TYPE_RES_URI
						.getName());
		String value = (String) currentParam.getValue();
		IRepositoryViewObject lastVersion;
		try {
			lastVersion = ProxyRepositoryFactory.getInstance().getLastVersion(
					value);
			Item item = lastVersion.getProperty().getItem();
			String label = item.getProperty().getDisplayName();
			String parentPaths = item.getState().getPath();
			if (parentPaths != null && !parentPaths.isEmpty()) {
				label = parentPaths + "/" + label;
			}
			label = "classpath:" + RouteResourceItem.ROUTE_RESOURCES_FOLDER
					+ "/" + label;
			uriParam.setValue(label);
		} catch (Exception e) {
		}

	}
}
