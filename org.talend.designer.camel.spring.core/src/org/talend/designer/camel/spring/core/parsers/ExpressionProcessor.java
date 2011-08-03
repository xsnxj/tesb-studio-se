package org.talend.designer.camel.spring.core.parsers;

import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.model.language.TokenizerExpression;
import org.apache.camel.model.language.XPathExpression;

public class ExpressionProcessor {

	public static String getExpresstionText(ExpressionDefinition expression) {
		if (expression instanceof TokenizerExpression) {
			TokenizerExpression te = (TokenizerExpression) expression;
			String headerName = te.getHeaderName();
			String token = te.getToken();
			String ex = "";
			if (headerName != null) {
				ex = headerName + "(\"" + token + "\")";
			} else {
				ex = "body(\"" + token + "\")";
			}
			return ex;
		}
		if(expression instanceof XPathExpression){
		}
		return null;
	}
	
}
