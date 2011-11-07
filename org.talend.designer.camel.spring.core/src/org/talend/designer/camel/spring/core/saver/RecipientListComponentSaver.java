package org.talend.designer.camel.spring.core.saver;

import java.util.HashMap;
import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RecipientListComponentSaver extends AbstractComponentSaver {

	private int index = 1;
	
	private Map<String, String> brokerMap = new HashMap<String, String>();
	
	public RecipientListComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <bean id="activemqId" class="org.apache.activemq.camel.component.ActiveMQComponent">
	 * 		<property name="brokerURL" value="brokerUrl" />
	 * </bean>
	 * ...
	 * <from uri="activemqId:(queue|topic):destination?options" />
	 * or
	 * <to uri="activemqId:(queue|topic):destination?options" />
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

		String ignoreInvalid = parameter.get(RL_IGNORE_INVALID);
		if ("true".equals(ignoreInvalid)) {
			element.setAttribute("ignoreInvalidEndpoints", "true");
		}
		String parellelProcess = parameter.get(RL_PARELLEL_PROCESS);
		if ("true".equals(parellelProcess)) {
			element.setAttribute("parallelProcessing", "true");
		}
		String stopOnException = parameter.get(RL_STOP_ON_EXCEPTION);
		if ("true".equals(parellelProcess)) {
			element.setAttribute("stopOnException", "true");
		}
		return element;
	}
	
	@Override
	public void afterSaved() {
		super.afterSaved();
		brokerMap.clear();
		brokerMap = null;
	}

}
