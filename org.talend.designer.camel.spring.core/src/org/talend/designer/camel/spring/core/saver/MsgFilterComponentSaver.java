package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class MsgFilterComponentSaver extends AbstractComponentSaver {

	public MsgFilterComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(MSGFILTER_ELE);
		parent.appendChild(element);
		
		Map<String, String> parameter = srn.getParameter();
		String type = parameter.get(EP_EXPRESSION_TYPE);
		if(type!=null){
			String text = parameter.get(EP_EXPRESSION_TEXT);
			Element sub = document.createElement(type);
			element.appendChild(sub);
			Text textNode = document.createTextNode(removeQuote(text));
			sub.appendChild(textNode);
		}
		return element;
	}

}
