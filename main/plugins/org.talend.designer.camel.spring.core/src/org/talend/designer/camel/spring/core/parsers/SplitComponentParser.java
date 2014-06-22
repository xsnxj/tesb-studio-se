package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.SplitDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.model.language.TokenizerExpression;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class SplitComponentParser extends AbstractComponentParser {

	public SplitComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
	}

	protected void parse(OptionalIdentifiedDefinition d, Map<String, String> map) {
		SplitDefinition sd = (SplitDefinition) d;
		ExpressionDefinition expression = sd.getExpression();
		
		if (expression instanceof TokenizerExpression) {
			TokenizerExpression te = (TokenizerExpression) expression;
			String headerName = te.getHeaderName();
			String token = te.getToken();
			String ex = "";
			if (headerName != null) {
				ex = "header(\""+headerName+"\").tokenize" + "(\"" + token + "\")";
			} else {
				ex = "body().tokenize(\"" + token + "\")";
			}
//			map.put(EP_EXPRESSION_TYPE, EP_TOKENIZER_EXPRESSION);
//			map.put(EP_EXPRESSION_TEXT, ex);
			map.put(SP_SPLIT_EXPRESS, ex);
		}else{
			Map<String, String> expressionMap = ExpressionProcessor.getExpressionMap(expression);
			String type = expressionMap.get(EP_EXPRESSION_TYPE);
			String ex = expressionMap.get(EP_EXPRESSION_TEXT);
			map.put(SP_SPLIT_EXPRESS, type+"("+ex+")");
		}
	}

	public int getType() {
		return SPLIT;
	}

}
