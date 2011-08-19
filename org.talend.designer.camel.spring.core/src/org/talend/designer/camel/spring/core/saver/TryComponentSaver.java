package org.talend.designer.camel.spring.core.saver;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TryComponentSaver extends AbstractComponentSaver {

	public TryComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <doTry>
	 * 	...
	 * </doTry>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(TRY_ELE);
		parent.appendChild(element);
		return element;
	}

}
