package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.SetHeaderDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class SetHeaderComponentParser extends AbstractComponentParser {

	public SetHeaderComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		SetHeaderDefinition shd = (SetHeaderDefinition) oid;
		String headerName = shd.getHeaderName();
		map.put(SH_HEADER_NAME, headerName);
		ExpressionDefinition expression = shd.getExpression();
		Map<String, String> expressionMap = ExpressionProcessor.getExpressionMap(expression);
		map.putAll(expressionMap);
	}

	@Override
	public int getType() {
		return SETHEADER;
	}

}
