// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.check;

import java.text.MessageFormat;
import java.util.List;

import org.talend.camel.designer.i18n.CamelDesignerMessages;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.process.Element;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.Problem.ProblemStatus;
import org.talend.designer.core.ICheckNodesService;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.views.problems.Problems;

/**
 * @author xpli
 * 
 */
public class CheckJMSNodesService implements ICheckNodesService {

	/**
	 * 
	 */
	public CheckJMSNodesService() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.talend.designer.core.ICheckNodesService#checkNode(org.talend.designer
	 * .core.ui.editor.nodes.Node)
	 */
	public void checkNode(Node node) {
		if (!node.getComponent().getName().equals("cJMS")) { //$NON-NLS-1$
			return;
		}
		List<? extends IElementParameter> parameters = node
				.getElementParameters();
		for (IElementParameter param : parameters) {
			if (param.getFieldType() == EParameterFieldType.ROUTE_COMPONENT_TYPE) {
				IElementParameter idParam = param.getChildParameters().get(
						EParameterName.ROUTE_COMPONENT_TYPE_ID.getName());
				if (idParam == null || idParam.getValue() == null
						|| idParam.getValue().toString().isEmpty()) {
					String errorMessage = MessageFormat.format(CamelDesignerMessages.getString("CheckJMSNodesService_emptyParaError") //$NON-NLS-1$
							, param.getDisplayName());
					Problems.add(ProblemStatus.ERROR, (Element) node,
							errorMessage);
				} else {
					List<? extends INode> graphicalNodes = node.getProcess()
							.getGraphicalNodes();
					boolean has = false;
					for (INode n : graphicalNodes) {
						if (n.getUniqueName().equals(idParam.getValue())) {
							has = true;
							break;
						}
					}
					if (!has) {
						String errorMessage = MessageFormat
								.format(CamelDesignerMessages
										.getString("CheckJMSNodesService_nonExistError"), param.getDisplayName()); //$NON-NLS-1$
						Problems.add(ProblemStatus.ERROR, (Element) node,
								errorMessage);
					}
				}
			}
		}
	}

}
