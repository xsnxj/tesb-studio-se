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
	/**
	 * generated xml format:
	 * <routingSlip uriDelimiter="delimiter">
	 * 		<header>headerName</header>
	 * </routingSlip>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Map<String, String> parameter = srn.getParameter();
		String delimiter = parameter.get(RS_URI_DELIMITER);
		String express = parameter.get(EP_EXPRESSION_TEXT);
		
		Element element = document.createElement(ROUTINGSLIP_ELE);
		parent.appendChild(element);
		if(delimiter!=null){
			element.setAttribute("uriDelimiter", removeQuote(delimiter));
		}
		
		//create header element
		Element header = document.createElement("header");
		element.appendChild(header);
		if(express==null){
			express = "";
		}
		Text expressNode = document.createTextNode(removeQuote(express));
		header.appendChild(expressNode);
		return element;
	}

}
