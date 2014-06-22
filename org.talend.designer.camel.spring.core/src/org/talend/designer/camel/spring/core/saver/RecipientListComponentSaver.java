package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RecipientListComponentSaver extends AbstractComponentSaver {

	public RecipientListComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <recipientList parallelProcessing="true" ignoreInvalidEndpoints="true" stopOnException="true" delimiter=",">
	 *       <xpath>$foo</xpath>
	 *  </recipientList>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(RECIPIENT_LIST_ELE);
		parent.appendChild(element);
		
		Map<String, String> parameter = srn.getParameter();
		
		// set expression
		String type = parameter.get(EP_EXPRESSION_TYPE);
		String text = parameter.get(EP_EXPRESSION_TEXT);
		Element typeElement = document.createElement(type);
		typeElement.setTextContent(text);
		element.appendChild(typeElement);

		String ignoreInvalid = parameter.get(RL_IGNORE_INVALID);
		if ("true".equals(ignoreInvalid)) {
			element.setAttribute("ignoreInvalidEndpoints", "true");
		}
		String parellelProcess = parameter.get(RL_PARELLEL_PROCESS);
		if ("true".equals(parellelProcess)) {
			element.setAttribute("parallelProcessing", "true");
		}
		String stopOnException = parameter.get(RL_STOP_ON_EXCEPTION);
		if ("true".equals(stopOnException)) {
			element.setAttribute("stopOnException", "true");
		}
		String delimiter = parameter.get(RL_DELIMITER);
		if (delimiter != null && !"".equals(delimiter)) {
			element.setAttribute("delimiter", delimiter);
		}

		return element;
	}
	
}
