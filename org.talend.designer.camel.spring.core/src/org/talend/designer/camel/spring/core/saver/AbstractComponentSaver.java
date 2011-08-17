package org.talend.designer.camel.spring.core.saver;

import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractComponentSaver implements ICamelSpringConstants{

	protected Document document;
	protected Element root;
	protected Element context;

	public AbstractComponentSaver(Document document, Element rootElement, Element contextElement) {
		this.document = document;
		this.root = rootElement;
		this.context = contextElement;
	}

	public void beforeSave(){
		
	}
	
	public abstract Element save(SpringRouteNode srn, Element parent);

	protected Element addElement(String elementName, Element parentElement) {
		Element element = document.createElement(elementName);
		parentElement.appendChild(element);
		return element;
	}

	protected Attr addAttribute(String name, String value, Element element) {
		Attr attribute = document.createAttribute(name);
		attribute.setValue(value);
		element.appendChild(attribute);
		return attribute;
	}
	
	protected String removeQuote(String s){
		if(s==null){
			return null;
		}
		if(s.startsWith("\"")){
			s = s.substring(1);
		}
		if(s.endsWith("\"")){
			s = s.substring(0,s.length()-1);
		}
		return s;
	}
	
	public void afterSaved(){
		
	}
}
