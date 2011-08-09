package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.RoutingSlipDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class RoutingSlipComponentParser extends AbstractComponentParser {

	public RoutingSlipComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		RoutingSlipDefinition rsd = (RoutingSlipDefinition) oid;
		ExpressionDefinition expression = rsd.getExpression();
		String uriDelimiter = rsd.getUriDelimiter();
		if(uriDelimiter.equals("")){
			uriDelimiter = ",";
		}
		map.put(RS_URI_DELIMITER, "\""+uriDelimiter+"\"");
		Map<String, String> expressionMap = ExpressionProcessor.getExpressionMap(expression);
		map.putAll(expressionMap);
	}

	@Override
	public int getType() {
		return ROUTINGSLIP;
	}

}
