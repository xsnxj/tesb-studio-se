package org.talend.designer.publish.core.models.meta;

import javax.xml.bind.annotation.XmlAttribute;

public class DataField {

	@XmlAttribute
	private String name;

	@XmlAttribute
	private String type;

	DataField(){
	}
	
	public DataField(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

}