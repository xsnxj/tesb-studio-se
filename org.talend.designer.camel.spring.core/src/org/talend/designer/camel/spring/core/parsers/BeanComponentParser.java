package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.BeanDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;

public class BeanComponentParser extends AbstractComponentParser {

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		BeanDefinition bd = (BeanDefinition) oid;
		map.put(BEAN_CLASS, bd.getLabel());
		String method = bd.getMethod();
		map.put(BEAN_METHOD, method);
	}

	@Override
	public int getType() {
		return BEAN;
	}

}
