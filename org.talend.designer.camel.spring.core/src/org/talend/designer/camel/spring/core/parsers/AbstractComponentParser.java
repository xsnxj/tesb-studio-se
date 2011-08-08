package org.talend.designer.camel.spring.core.parsers;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.spi.NodeIdFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public abstract class AbstractComponentParser implements ICamelSpringConstants{

	protected XmlFileApplicationContext appContext;

	public AbstractComponentParser(XmlFileApplicationContext appContext){
		this.appContext = appContext;
	}
	
	public final Map<String, String> parse(NodeIdFactory factory, OptionalIdentifiedDefinition oid){
		Map<String,String> map = new HashMap<String, String>();
		String id = factory.createId(oid);
		map.put(UNIQUE_NAME_ID, id);
		parse(oid, map);
		return map;
	}
	
	public void initial(){
		
	}
	
	public BeanDefinition getBeanDefinition(String ref){
		return appContext.getBeanFactory().getBeanDefinition(ref);
	}
	
	public String getRegisteredBeanClass(String ref){
		return appContext.getRegisterBeanClassName(ref);
	}
	
	protected abstract void parse(OptionalIdentifiedDefinition oid, Map<String, String> map);

	public abstract int getType();
	
	public void clear(){
		
	}
}
