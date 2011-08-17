package org.talend.designer.camel.spring.core.saver;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MsgEndpointComponentSaver extends AbstractComponentSaver {

	public MsgEndpointComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		SpringRouteNode preNode = srn.getParent();
		Element element = null;
		if (preNode == null) {
			element = document.createElement(FROM_ELE);
		} else {
			element = document.createElement(TO_ELE);
		}
		String uri = srn.getParameter().get(ENDPOINT_URI);
		element.setAttribute(URI_ATT, removeQuote(uri));
		parent.appendChild(element);
		return element;
	}

}
