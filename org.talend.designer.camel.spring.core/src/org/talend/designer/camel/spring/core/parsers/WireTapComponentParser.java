package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.Processor;
import org.apache.camel.model.ExpressionSubElementDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.WireTapDefinition;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;

public class WireTapComponentParser extends AbstractComponentParser {

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		WireTapDefinition wtd = (WireTapDefinition) oid;
		String uri = wtd.getUri();
		map.put(ENDPOINT_URI, uri);
		
		Boolean copy = wtd.getCopy();
		if(copy!=null){
			map.put(WIRETAP_COPY, copy.toString());
		}
		
		ExpressionSubElementDefinition newExchangeExpression = wtd.getNewExchangeExpression();
		if(newExchangeExpression!=null){
			map.put(POPULATE_TYPE, NEW_EXPRESSION_POP);
			Map<String, String> expressionMap = ExpressionProcessor.getExpressionMap(newExchangeExpression.getExpressionType());
			map.putAll(expressionMap);
			return;
		}
		Processor newExchangeProcessor = wtd.getNewExchangeProcessor();
		if(newExchangeProcessor != null){
			map.put(POPULATE_TYPE, NEW_PROCESSOR_POP);
			return;
		}
		String newExchangeProcessorRef = wtd.getNewExchangeProcessorRef();
		if(newExchangeProcessorRef!=null){
			map.put(POPULATE_TYPE, NEW_PROCESSOR_POP);
			return;
		}
	}

	@Override
	public int getType() {
		return WIRETAP;
	}

}
