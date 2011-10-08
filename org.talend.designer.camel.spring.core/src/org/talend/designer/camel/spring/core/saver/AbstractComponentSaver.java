package org.talend.designer.camel.spring.core.saver;

import java.util.Iterator;
import java.util.Map;

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
	
	public Element saveToElement(SpringRouteNode srn, Element parent) {
		Map<String, String> parameter = srn.getParameter();
		Iterator<String> iterator = parameter.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = parameter.get(key);
			value = removeQuote(value);
			parameter.put(key, value);
		}
		return save(srn, parent);
	};

	protected abstract Element save(SpringRouteNode srn, Element parent);

	protected Element addElement(String elementName, Element parentElement) {
		Element element = document.createElement(elementName);
		parentElement.appendChild(element);
		return element;
	}

	protected Attr addAttribute(String name, String value, Element element) {
		Attr attribute = document.createAttribute(name);
		attribute.setValue(value);
		element.setAttributeNode(attribute);
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
	
	protected Element addBeanElement(String id, String className){
		Element beanElement = document.createElement(BEAN_ELE);
		root.insertBefore(beanElement, context);
		beanElement.setAttribute("id", id);
		beanElement.setAttribute("class", className);
		return beanElement;
	}
	
	public void afterSaved(){
		
	}
}
