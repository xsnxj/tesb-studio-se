// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import org.talend.core.model.process.IElement;
import org.talend.core.model.process.IElementParameter;
import org.talend.designer.camel.resource.core.model.ResourceDependencyModel;
import org.talend.designer.camel.resource.core.util.RouteResourceUtil;
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
		IElementParameter versionParam = element
				.getElementParameter(EParameterName.ROUTE_RESOURCE_TYPE_VERSION
						.getName());
		IElementParameter idParam = element
				.getElementParameter(EParameterName.ROUTE_RESOURCE_TYPE_ID
						.getName());
		String value = (String) idParam.getValue();
		String version = (String) versionParam.getValue();

		ResourceDependencyModel model = RouteResourceUtil.createDependency(
				value, version);
		if (model != null) {
			uriParam.setValue(model.getClassPathUrl());
		}

	}
}
