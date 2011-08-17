package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BeanComponentSaver extends AbstractComponentSaver {

	public BeanComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		Map<String, String> parameter = srn.getParameter();
		String beanClass = parameter.get(BN_BEAN_CLASS);
		String beanMethod = parameter.get(BN_BEAN_METHOD);
		
		if(beanClass!=null&&beanClass.endsWith(".class")){
			beanClass = beanClass.substring(0,beanClass.length()-".class".length());
		}
		if(beanMethod!=null){
			beanMethod = removeQuote(beanMethod);
		}
		
		Element element = document.createElement(BEAN_ELE);
		element.setAttribute("beanType", beanClass);
		element.setAttribute("method", beanMethod);
		parent.appendChild(element);
		return element;
	}

}
