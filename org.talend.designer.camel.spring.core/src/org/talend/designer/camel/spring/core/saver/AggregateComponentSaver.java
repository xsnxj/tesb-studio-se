package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class AggregateComponentSaver extends AbstractComponentSaver {

	private String ID = "aggregateStrategy";
	private int index = 0;
	
	public AggregateComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(AGGREGATE_ELE);
		parent.appendChild(element);
		
		Map<String, String> parameter = srn.getParameter();
		
		String type = parameter.get(EP_EXPRESSION_TYPE);
		String text = parameter.get(EP_EXPRESSION_TEXT);
		if(null!=type){
			Element expressionElement = document.createElement("correlationExpression");
			element.appendChild(expressionElement);
			Element typeElement = document.createElement(type);
			expressionElement.appendChild(typeElement);
			if(text==null){
				text = "";
			}else{
				text = removeQuote(text);
			}
			Text textNode = document.createTextNode(text);
			typeElement.appendChild(textNode);
		}
		
		String aggregateStrategy = parameter.get(AG_AGGREGATE_STRATEGY);
		if(aggregateStrategy!=null){
			index++;
			Element beanElement = document.createElement(BEAN_ELE);
			root.insertBefore(beanElement, context);
			beanElement.setAttribute("id", ID+index);
			beanElement.setAttribute("class", aggregateStrategy);
			element.setAttribute("strategyRef", ID+index);
		}
		
		String completionSize = parameter.get(AG_COMPLETION_SIEZE);
		if(completionSize!=null){
			element.setAttribute("completionSize", completionSize);
		}
		
		String interval = parameter.get(AG_COMPLETION_INTERVAL);
		if(interval!=null){
			element.setAttribute("completionInterval", interval);
		}
		
		String timeout = parameter.get(AG_COMPLETION_TIMEOUT);
		if(timeout!=null){
			element.setAttribute("completionTimeout", timeout);
		}
		
		String fromBatch = parameter.get(AG_COMPLETION_FROM_BATCH);
		if(fromBatch!=null){
			element.setAttribute("completionFromBatchConsumer", fromBatch);
		}
		
		String groupExchange = parameter.get(AG_GROUP_EXCHANGES);
		if(groupExchange!=null){
			element.setAttribute("groupExchanges", groupExchange);
		}
		
		String ignoreInvalid = parameter.get(AG_IGNORE_INVALID);
		if(ignoreInvalid!=null){
			element.setAttribute("ignoreInvalidCorrelationKeys", ignoreInvalid);
		}
		
		String checkCompletion = parameter.get(AG_CHECK_COMPLETION);
		if(checkCompletion!=null){
			element.setAttribute("eagerCheckCompletion", checkCompletion);
		}
		
		String closeOnCompletion = parameter.get(AG_CLOSE_ON_COMPLETION);
		if(closeOnCompletion!=null){
			element.setAttribute("closeCorrelationKeyOnCompletion", closeOnCompletion);
		}
		
		String completionPredicate = parameter.get(AG_COMPLETION_PREDICATE);
		if(completionPredicate!=null){
			Element predicateElement = document.createElement("completionPredicate");
			element.appendChild(predicateElement);
			
			Element expressElement = document.createElement("expressionDefinition");
			predicateElement.appendChild(expressElement);
			
			Text textNode = document.createTextNode(removeQuote(completionPredicate));
			expressElement.appendChild(textNode);
			
		}
		return element;
	}

}
