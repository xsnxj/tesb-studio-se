package org.talend.designer.camel.spring.core.intl;

import org.springframework.beans.factory.FactoryBean;

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
