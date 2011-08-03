package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.FilterDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.language.ExpressionDefinition;

public class FilterComponentParser extends AbstractComponentParser {

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		FilterDefinition fd = (FilterDefinition) oid;
		ExpressionDefinition expression = fd.getExpression();
	}

	@Override
	public int getType() {
		return FILTER;
	}

}
