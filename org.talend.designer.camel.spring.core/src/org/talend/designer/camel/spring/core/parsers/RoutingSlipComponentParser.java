package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.RoutingSlipDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;

public class RoutingSlipComponentParser extends AbstractComponentParser {

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		RoutingSlipDefinition rsd = (RoutingSlipDefinition) oid;
		ExpressionDefinition expression = rsd.getExpression();
		String uriDelimiter = rsd.getUriDelimiter();
		Map<String, String> expressionMap = ExpressionProcessor.getExpressionMap(expression);
		map.put(URI_DELIMITER, uriDelimiter);
		map.putAll(expressionMap);
	}

	@Override
	public int getType() {
		return ROUTINGSLIP;
	}

}
