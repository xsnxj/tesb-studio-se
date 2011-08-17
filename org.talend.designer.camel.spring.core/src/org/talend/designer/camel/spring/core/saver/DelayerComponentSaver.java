package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class DelayerComponentSaver extends AbstractComponentSaver {

	public DelayerComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(DELAYER_ELE);
		parent.appendChild(element);
		
		Element sub = document.createElement("constant");
		element.appendChild(sub);
		Map<String, String> parameter = srn.getParameter();
		String text = parameter.get(EP_EXPRESSION_TEXT);
		if(text!=null){
			Text textNode = document.createTextNode(removeQuote(text));
			sub.appendChild(textNode);
		}
		return element;
	}

}
