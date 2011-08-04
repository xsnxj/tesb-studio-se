package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.SplitDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;

public class SplitComponentParser extends AbstractComponentParser {

	protected void parse(OptionalIdentifiedDefinition d, Map<String, String> map) {
		SplitDefinition sd = (SplitDefinition) d;
		ExpressionDefinition expression = sd.getExpression();
		Map<String, String> expressionMap = ExpressionProcessor.getExpressionMap(expression);
		String ex = expressionMap.get(EXPRESSION_TEXT);
		map.put(SPLIT_EXPRESS, ex);
	}

	public int getType() {
		return SPLIT;
	}

}
