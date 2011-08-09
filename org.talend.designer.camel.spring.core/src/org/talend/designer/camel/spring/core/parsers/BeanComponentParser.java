package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.BeanDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class BeanComponentParser extends AbstractComponentParser {

	public BeanComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		BeanDefinition bd = (BeanDefinition) oid;
		map.put(BN_BEAN_CLASS, bd.getLabel() + ".class");
		String method = bd.getMethod();
		map.put(BN_BEAN_METHOD, "\"" + method + "\"");
	}

	@Override
	public int getType() {
		return BEAN;
	}

}
