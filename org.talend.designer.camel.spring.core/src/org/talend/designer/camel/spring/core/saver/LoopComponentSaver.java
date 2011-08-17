package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class LoopComponentSaver extends AbstractComponentSaver {

	public LoopComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(LOOP_ELE);
		parent.appendChild(element);
		
		Map<String, String> parameter = srn.getParameter();
		String constant = parameter.get("constant");
		if(constant!=null&&!"".equals(constant)){
			Element constantElement = document.createElement("constant");
			element.appendChild(constantElement);
			Text textNode = document.createTextNode(constant);
			constantElement.appendChild(textNode);
			return element;
		}
		String header = parameter.get("header");
		if(header!=null&&!"".equals(header)){
			int leftB = header.indexOf("\"");
			int rightB = header.indexOf("\"",leftB+1);
			if(rightB!=-1){
				String headerEx = header.substring(leftB+1, rightB);
				Element headerElement = document.createElement("header");
				element.appendChild(headerElement);
				Text textNode = document.createTextNode(headerEx);
				headerElement.appendChild(textNode);
			}
			return element;
		}
		String type = parameter.get(EP_EXPRESSION_TYPE);
		String text = parameter.get(EP_EXPRESSION_TEXT);
		if(type!=null){
			Element typeElement = document.createElement(type);
			element.appendChild(typeElement);
			Text textNode = document.createTextNode(removeQuote(text));
			typeElement.appendChild(textNode);
		}
		
		return element;
	}

}
