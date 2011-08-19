package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.DynamicRouterDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.model.language.MethodCallExpression;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class DynamicComponentParser extends AbstractComponentParser {

	public DynamicComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		DynamicRouterDefinition drd = (DynamicRouterDefinition) oid;
		ExpressionDefinition expression = drd.getExpression();
		if(expression==null){
			return;
		}
		if(expression instanceof MethodCallExpression){
			MethodCallExpression mce = (MethodCallExpression) expression;
			String b = mce.getBean();
			if(b==null){
				String ref = mce.getRef();
				b = getRegisteredBeanClass(ref);
			}
			map.put(DY_BEAN_CLASS, b+".class");
			String method = mce.getMethod();
			map.put(DY_BEAN_METHOD, "\""+method+"\"");
		}
	}

	@Override
	public int getType() {
		return DYNAMIC;
	}

}
