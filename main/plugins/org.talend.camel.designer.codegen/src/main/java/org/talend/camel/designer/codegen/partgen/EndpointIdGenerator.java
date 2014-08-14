package org.talend.camel.designer.codegen.partgen;

import org.talend.camel.designer.codegen.util.NodeUtil;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.exception.CodeGeneratorException;

public class EndpointIdGenerator implements PartGenerator<INode> {

	@Override
	public CharSequence generatePart(INode node, Object... ignoredParams) throws CodeGeneratorException {
		return generateEndpointId(node);
	}

	private final String generateEndpointId(INode node) {
		if (NodeUtil.isStartNode(node)) {
			// http://jira.talendforge.org/browse/TESB-4086
			// XiaopengLi
			String label = null;
			IElementParameter parameter = node.getElementParameter("LABEL");
			if (parameter != null && !"__UNIQUE_NAME__".equals(parameter.getValue())) {
				label = (String) parameter.getValue();
			}

			/*
			 * Fix https://jira.talendforge.org/browse/TESB-6685 label +
			 * uniqueName to make it unique
			 */
			if (label == null) {
				label = node.getUniqueName();
			} else {
				label += "_" + node.getUniqueName();
			}
			if (!"cErrorHandler".equals(node.getComponent().getName())) {
				return ".routeId(\"" + label + "\")";
			}
		} else {
			return ".id(\"" + node.getUniqueName() + "\")";
		}
		return "";
	}
}
