package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class LoadBalanceComponentSaver extends AbstractComponentSaver {

	public LoadBalanceComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(LOADBALANCE_ELE);
		parent.appendChild(element);
		
		Map<String, String> parameter = srn.getParameter();
		String strategy = parameter.get(LB_BALANCE_STRATEGY);
		if (LB_RANDOM_STRATEGY.equals(strategy)
				|| LB_ROUND_ROBIN_TYPE.equals(strategy)
				|| LB_TOPIC_STRATEGY.equals(strategy)) {
			Element strategyElement = document.createElement(strategy);
			element.appendChild(strategyElement);
		}else if(LB_STICKY_STRATEGY.equals(strategy)){
			Element strategyElement = document.createElement(LB_STICKY_STRATEGY);
			element.appendChild(strategyElement);
			Element expressionElement = document.createElement("correlationExpression");
			strategyElement.appendChild(expressionElement);
			String type = parameter.get(EP_EXPRESSION_TYPE);
			String text = parameter.get(EP_EXPRESSION_TEXT);
			if(text==null){
				text = "";
			}
			if(type!=null){
				Element typeElement = document.createElement(type);
				expressionElement.appendChild(typeElement);
				Text textNode = document.createTextNode(removeQuote(text));
				typeElement.appendChild(textNode);
			}
		}
		return element;
	}

}
