package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DynamicComponentSaver extends AbstractComponentSaver {

	private String dynamicId = "dynamic";
	private int index = 1;
	
	public DynamicComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(DYNAMIC_ELE);
		parent.appendChild(element);
		
		//create bean element
		Element beanElement = document.createElement(BEAN_ELE);
		root.insertBefore(beanElement, context);
		beanElement.setAttribute("id", dynamicId+index);
		
		Map<String, String> parameter = srn.getParameter();
		String beanClass = parameter.get(BN_BEAN_CLASS);
		if(beanClass!=null&&beanClass.endsWith(".class")){
			beanClass = beanClass.substring(0,beanClass.length()-".class".length());
		}
		beanElement.setAttribute("class", beanClass);
		
		//create sub method element
		Element methodEle = document.createElement("method");
		element.appendChild(methodEle);
		
		methodEle.setAttribute("ref", dynamicId+index);
		methodEle.setAttribute("method", removeQuote(parameter.get(BN_BEAN_METHOD)));
		index++;
		return element;
	}

}
