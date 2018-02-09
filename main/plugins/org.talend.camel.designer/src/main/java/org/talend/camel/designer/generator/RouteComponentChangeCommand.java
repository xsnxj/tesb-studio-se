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
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.ui.editor.cmd.PropertyChangeCommand;

/**
 * @author xpli
 * 
 */
public class RouteComponentChangeCommand extends PropertyChangeCommand {

	private IElement element;

	private String propertyName;

	public RouteComponentChangeCommand(IElement elem, String propName,
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
				EParameterName.ROUTE_COMPONENT_TYPE_ID.getName())) {
			setUpdate(true);
		}
		super.execute();
	}
}
