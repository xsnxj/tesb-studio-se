package org.talend.designer.camel.spring.core.saver;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InterceptComponentSaver extends AbstractComponentSaver {

	public InterceptComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <intercept>
	 * 		...
	 * </intercept>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(INTERCEPT_ELE);
		
		/**
		 * according to the schema
		 * the intercept elements should behind the onException elements
		 */
		NodeList exceptionEles = parent.getElementsByTagName(ONEXCEPTION_ELE);
		if(exceptionEles!=null&&exceptionEles.getLength()>0){
			Node lastException = exceptionEles.item(exceptionEles.getLength()-1);
			Node nextSibling = lastException.getNextSibling();
			parent.insertBefore(element,nextSibling);
		}else{
			parent.insertBefore(element, parent.getFirstChild());
		}
		
		return element;
	}

}
