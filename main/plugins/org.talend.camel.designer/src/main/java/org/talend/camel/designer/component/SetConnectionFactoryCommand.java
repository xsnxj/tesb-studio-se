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
package org.talend.camel.designer.component;

import org.eclipse.gef.commands.Command;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;

/**
 * @author liXiaopeng
 * 
 */
public class SetConnectionFactoryCommand extends Command {

	private JSMExternalComponentMain main;

	private INode selectedNode;

	/**
	 * @param main
	 * @param selectedNode
	 */
	public SetConnectionFactoryCommand(JSMExternalComponentMain main,
			INode selectedNode) {
		this.main = main;
		this.selectedNode = selectedNode;
	}

	@Override
	public void execute() {
		JMSExternalComponent jmsExternalComponent = main.getExternalComponent();
		INode originalNode = jmsExternalComponent.getOriginalNode();
		IElementParameter elementParameter = originalNode
				.getElementParameter("CONNECTION_FACOTRY_LABEL");
		if (selectedNode != null) {
			if (elementParameter != null) {
				elementParameter.setValue(getLabel(selectedNode));
			}
			elementParameter = originalNode
					.getElementParameter("CONNECTION_FACOTRY");
			if (elementParameter != null) {
				elementParameter.setValue(selectedNode.getUniqueName().replace(
						"_", ""));
			}
		}
	}


	public static String getLabel(INode element) {
		IElementParameter param = element.getElementParameter("LABEL");
		String label = "";
		if (param != null && !"__UNIQUE_NAME__".equals(param.getValue())) {
			label = (String) param.getValue();
		} else {
			label = ((INode) element).getUniqueName();
		}
		return label;
	}
}
