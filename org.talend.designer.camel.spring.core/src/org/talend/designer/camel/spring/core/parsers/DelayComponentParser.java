package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.DelayDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;

public class DelayComponentParser extends AbstractComponentParser {

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		DelayDefinition dd = (DelayDefinition) oid;
		ExpressionDefinition expression = dd.getExpression();
		Map<String, String> expressionMap = ExpressionProcessor.getExpressionMap(expression);
		map.putAll(expressionMap);
	}

	@Override
	public int getType() {
		return DELAY;
	}

}
