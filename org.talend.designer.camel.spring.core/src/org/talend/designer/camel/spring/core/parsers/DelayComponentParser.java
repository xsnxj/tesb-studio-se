package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.DelayDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class DelayComponentParser extends AbstractComponentParser {

	public DelayComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
		// TODO Auto-generated constructor stub
	}

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
