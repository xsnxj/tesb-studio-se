package org.talend.designer.camel.spring.core.saver;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConvertBodyComponentSaver extends AbstractComponentSaver {

	public ConvertBodyComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(CONVERT_ELE);
		parent.appendChild(element);
		
		String type = srn.getParameter().get(CV_TARGET_TYPE_CLASS);
		if(type!=null&&!"".equals(type)){
			if(type.endsWith(".class")){
				type = type.substring(0,type.length()-".class".length());
			}
			element.setAttribute("type", type);
		}
		return element;
	}

}
