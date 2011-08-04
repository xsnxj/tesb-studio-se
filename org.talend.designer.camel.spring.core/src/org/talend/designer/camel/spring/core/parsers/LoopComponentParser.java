package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.LoopDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;

public class LoopComponentParser extends AbstractComponentParser {

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		LoopDefinition ld = (LoopDefinition) oid;
		ExpressionDefinition expression = ld.getExpression();
		Map<String, String> expressionMap = ExpressionProcessor.getExpressionMap(expression);
		map.putAll(expressionMap);
	}

	@Override
	public int getType() {
		return LOOP;
	}

}
