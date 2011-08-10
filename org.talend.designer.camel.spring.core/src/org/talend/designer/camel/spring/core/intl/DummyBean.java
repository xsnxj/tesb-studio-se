package org.talend.designer.camel.spring.core.intl;

import org.springframework.beans.factory.FactoryBean;

/**
 * 
 * @author liugang
 * This bean class used to create a dummy bean object
 * if the expected bean can't be resolved or not exist. 
 *
 */
public class DummyBean implements FactoryBean{

	private String camelId = "camel_Spring_id";
	
	public DummyBean(){
	}
	
	public DummyBean(Object... args){
	}
	
	public void setCamelId(String camelId) {
		this.camelId = camelId;
	}
	
	public String getCamelId() {
		return camelId;
	}
	
	public void dummy(String m){
	}

	public Object getObject() throws Exception {
		return null;
	}

	public Class getObjectType() {
		return null;
	}

	public boolean isSingleton() {
		return false;
	}
	
}
