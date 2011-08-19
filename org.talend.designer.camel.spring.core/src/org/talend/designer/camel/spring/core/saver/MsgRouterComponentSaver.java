package org.talend.designer.camel.spring.core.saver;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MsgRouterComponentSaver extends AbstractComponentSaver {

	public MsgRouterComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <choice>
	 * 		...
	 * </choice>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(MSGROUTER_ELE);
		parent.appendChild(element);
		return element;
	}

}
