package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.BeanDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class BeanComponentParser extends AbstractComponentParser {

	public BeanComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		BeanDefinition bd = (BeanDefinition) oid;
		String registeredBeanClass = getRegisteredBeanClass(bd.getRef());
		if(registeredBeanClass==null){
			registeredBeanClass = bd.getLabel();
		}
		map.put(BN_BEAN_CLASS, registeredBeanClass+".class");
		String method = bd.getMethod();
		if(method!=null){
			map.put(BN_BEAN_METHOD, "\"" + method + "\"");
		}
	}

	@Override
	public int getType() {
		return BEAN;
	}

}
