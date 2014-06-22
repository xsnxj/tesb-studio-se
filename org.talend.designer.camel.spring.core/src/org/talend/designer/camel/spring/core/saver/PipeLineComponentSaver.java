package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PipeLineComponentSaver extends AbstractComponentSaver {

	public PipeLineComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <pipeline>
	 * 		<to uri="uri"/>
	 * 		....
	 * 		or
	 * 		<bean ref="beanId"/>
	 * </pipeline>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(PIPES_ELE);
		parent.appendChild(element);
		
		Map<String, String> parameter = srn.getParameter();
		String destinations = parameter.get(PF_DESTINATIONS);
		if(null!=destinations&&!"".equals(destinations)){
			String[] splits = destinations.split(";");
			for(String s:splits){
				s = removeQuote(s);
				if(s.startsWith("bean:")){
					Element beanElement = document.createElement(BEAN_ELE);
					beanElement.setAttribute("ref", s.substring("bean:".length()));
					element.appendChild(beanElement);
				}else{
					Element toElement = document.createElement(TO_ELE);
					toElement.setAttribute("uri", s);
					element.appendChild(toElement);
				}
			}
		}
		
		return element;
	}

}
