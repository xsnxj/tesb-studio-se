package org.talend.designer.camel.spring.core.parsers;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.spi.NodeIdFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

/**
 * 
 * @author liugang
 * all concrete parser class will extend this class
 * only one instance exist during parsing.
 */
public abstract class AbstractComponentParser implements ICamelSpringConstants{

	protected XmlFileApplicationContext appContext;

	public AbstractComponentParser(XmlFileApplicationContext appContext){
		this.appContext = appContext;
	}
	
	/**
	 * before parse, give a unique name for the component
	 * @param factory
	 * @param oid
	 * @return
	 */
	public final Map<String, String> parse(NodeIdFactory factory, OptionalIdentifiedDefinition oid){
		Map<String,String> map = new HashMap<String, String>();
		String id = factory.createId(oid);
		map.put(UNIQUE_NAME_ID, id);
		parse(oid, map);
		return map;
	}
	
	/**
	 * this method will be invoke once before parse
	 */
	public void initial(){
		
	}
	
	public BeanDefinition getBeanDefinition(String ref){
		return appContext.getBeanFactory().getBeanDefinition(ref);
	}
	
	public String getRegisteredBeanClass(String ref){
		return appContext.getRegisterBeanClassName(ref);
	}
	
	/**
	 * this method will be invoked
	 * when encounter a corresponding component type
	 * @param oid
	 * @param map
	 */
	protected abstract void parse(OptionalIdentifiedDefinition oid, Map<String, String> map);

	/**
	 * return the component type
	 * @see ICamelSpringConstants
	 * @return
	 */
	public abstract int getType();
	
	/**
	 * this method will be invoke once after parse
	 */
	public void clear(){
		
	}
}
