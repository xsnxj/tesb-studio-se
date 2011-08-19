package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class WhenComponentSaver extends AbstractComponentSaver {

	public WhenComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * The generated xml format looks like:
	 * <when>
	 * 		<expressionType>expressionValue</expressionType>
	 * 		...
	 * </when>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(WHEN_ELE);
		parent.appendChild(element);
		
		Map<String, String> parameter = srn.getParameter();
		String type = parameter.get(EP_EXPRESSION_TYPE);
		String text = parameter.get(EP_EXPRESSION_TEXT);
		if(type!=null){
			Element typeElement = document.createElement(type);
			element.appendChild(typeElement);
			if(text!=null){
				Text textNode = document.createTextNode(removeQuote(text));
				typeElement.appendChild(textNode);
			}
		}
		return element;
	}

}
