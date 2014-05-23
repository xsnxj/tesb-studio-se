package org.talend.designer.publish.core.models.meta;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Metadata {
	@XmlAttribute(name = "meta-version")
	private String metaVersion = "1.0";
	
	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private String version;
	
	@XmlAttribute
	private String type;

	@XmlElement
	private String repository;

	@XmlElementWrapper(name = "config")
	@XmlElement(name = "item")
	private List<ContextItem> configItems = new ArrayList<ContextItem>();

	@XmlElementWrapper(name = "input")
	@XmlElement(name = "field")
	private List<DataField> inputFields = new ArrayList<DataField>();

	@XmlElementWrapper(name = "output")
	@XmlElement(name = "field")
	private List<DataField> outputFields = new ArrayList<DataField>();

	Metadata(){
	}
	
	public Metadata(String name, String version, String type) {
		super();
		this.name = name;
		this.version = version;
		this.type = type;
	}
	
	public void setRepository(String repository) {
		this.repository = repository;
	}

	public void addConfigItem(ContextItem ci) {
		configItems.add(ci);
	}

	public void addInputField(DataField f) {
		inputFields.add(f);
	}

	public void addOutputField(DataField f) {
		outputFields.add(f);
	}
}