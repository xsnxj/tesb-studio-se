package org.talend.designer.publish.core.models.meta;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class ContextItem {
	
	@XmlAttribute
	private String name;
	
	@XmlValue
	private String value;

	ContextItem(){
	}
	
	public ContextItem(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
}