package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class CatchComponentSaver extends AbstractComponentSaver {

	public CatchComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <doCatch>
	 * 		<exception>exceptionClass</exception>
	 * 		....
	 * 		<exception>exceptionClass</exception>
	 * </doCatch>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(CATCH_ELE);
		parent.appendChild(element);
		
		Map<String, String> parameter = srn.getParameter();
		String exceptions = parameter.get(OE_EXCEPTIONS);
		if(null!=exceptions&&!"".equals(exceptions)){
			String[] splits = exceptions.split(",");
			for(String s:splits){
				if(s.endsWith(".class")){
					s = s.substring(0,s.length()-".class".length());
				}
				Element sub = document.createElement("exception");
				element.appendChild(sub);
				Text textNode = document.createTextNode(s);
				sub.appendChild(textNode);
			}
		}
		return element;
	}

}
