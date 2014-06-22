package org.talend.designer.camel.spring.core.exprs;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.model.language.TokenizerExpression;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;

public class ExpressionProcessor implements ICamelSpringConstants {

	/**
	 * this method used to process the expression
	 * There are many expression types there.
	 * this method may be improved in future
	 * @param expression
	 * @return
	 */
	public static Map<String, String> getExpressionMap(
			ExpressionDefinition expression) {
		Map<String, String> map = new HashMap<String, String>();
		if(expression==null){
			return map;
		}
//		if (expression instanceof TokenizerExpression) {
//			TokenizerExpression te = (TokenizerExpression) expression;
//			String headerName = te.getHeaderName();
//			String token = te.getToken();
//			String ex = "";
//			if (headerName != null) {
//				ex = "header(\""+headerName+"\").tokenize" + "(\"" + token + "\")";
//			} else {
//				ex = "body().tokenize(\"" + token + "\")";
//			}
//			map.put(EP_EXPRESSION_TYPE, EP_TOKENIZER_EXPRESSION);
//			map.put(EP_EXPRESSION_TEXT, ex);
//		}else{
			map.put(EP_EXPRESSION_TYPE, expression.getLanguage());
			map.put(EP_EXPRESSION_TEXT, "\""+expression.getExpression()+"\"");
//		}
//		else if (expression instanceof XPathExpression) {
//			XPathExpression xp = (XPathExpression) expression;
//			String ex = xp.getExpression();
//			map.put(EXCEPTION_TYPE, XPATH_EXPRESSION);
//			map.put(EXPRESSION_TEXT, ex);
//		} else if (expression instanceof ELExpression) {
//			ELExpression el = (ELExpression) expression;
//			String ex = el.getExpression();
//			map.put(EXCEPTION_TYPE, EL_EXPRESSION);
//			map.put(EXPRESSION_TEXT, ex);
//		} else if (expression instanceof HeaderExpression) {
//			HeaderExpression he = (HeaderExpression) expression;
//			String ex = he.getExpression();
//			map.put(EXCEPTION_TYPE, HEADER_EXPRESSION);
//			map.put(EXPRESSION_TEXT, ex);
//		} else if (expression instanceof ConstantExpression) {
//			ConstantExpression ce = (ConstantExpression) expression;
//			String ex = ce.getExpression();
//			map.put(EXCEPTION_TYPE, CONSTANT_EXPRESSION);
//			map.put(EXPRESSION_TEXT, ex);
//		}
		return map;
	}

}
