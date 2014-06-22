package org.talend.designer.camel.spring.core.saver;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ProcessorComponentSaver extends AbstractComponentSaver {

	public ProcessorComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <process ref="beanId"/>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(PROCESSOR_ELE);
		parent.appendChild(element);
		
		element.setAttribute("ref", "");
		return element;
	}

}
