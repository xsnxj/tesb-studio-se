package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class RoutingSlipComponentSaver extends AbstractComponentSaver {

	public RoutingSlipComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		Map<String, String> parameter = srn.getParameter();
		String delimiter = parameter.get(RS_URI_DELIMITER);
		String express = parameter.get(EP_EXPRESSION_TEXT);
		
		Element element = document.createElement(ROUTINGSLIP_ELE);
		if(delimiter!=null){
			element.setAttribute("uriDelimiter", removeQuote(delimiter));
		}
		Element header = document.createElement("header");
		element.appendChild(header);
		if(express!=null){
			Text expressNode = document.createTextNode(removeQuote(express));
			header.appendChild(expressNode);
		}
		parent.appendChild(element);
		return element;
	}

}
