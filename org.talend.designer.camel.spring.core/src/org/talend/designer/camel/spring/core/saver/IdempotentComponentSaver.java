package org.talend.designer.camel.spring.core.saver;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class IdempotentComponentSaver extends AbstractComponentSaver {

	public IdempotentComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(IDEMPOTENT_ELE);
		parent.appendChild(element);
		return element;
	}

}
