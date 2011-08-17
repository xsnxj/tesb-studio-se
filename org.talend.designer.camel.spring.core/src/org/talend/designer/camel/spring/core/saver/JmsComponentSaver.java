package org.talend.designer.camel.spring.core.saver;

import java.util.Map;
import java.util.Set;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JmsComponentSaver extends AbstractComponentSaver {

	public JmsComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		SpringRouteNode preNode = srn.getParent();
		//create element
		Element element = null;
		if (preNode == null) {
			element = document.createElement(FROM_ELE);
		} else {
			element = document.createElement(TO_ELE);
		}
		parent.appendChild(element);
		
		Map<String, String> parameter = srn.getParameter();
		StringBuilder sb = new StringBuilder();
		
		String schema = parameter.get(JMS_SCHEMA_NAME);
		parameter.remove(JMS_SCHEMA_NAME);
		sb.append(schema);
		
		String type = parameter.get(JMS_TYPE);
		parameter.remove(JMS_TYPE);
		if(type!=null){
			sb.append(":");
			sb.append(type);
		}
		
		String destination = parameter.get(JMS_DESTINATION);
		parameter.remove(JMS_DESTINATION);
		sb.append(":");
		sb.append(destination);
		
		Set<String> keySet = parameter.keySet();
		if(keySet.size()>0){
			sb.append("?");
		}
		for(String s:keySet){
			String value = parameter.get(s);
			sb.append(s);
			sb.append("=");
			sb.append(removeQuote(value));
			sb.append("&");
		}
		if(keySet.size()>0){
			sb.deleteCharAt(sb.length()-1);
		}
		element.setAttribute(URI_ATT, sb.toString());
		
		//create bean
		Element beanElement = document.createElement(BEAN_ELE);
		root.insertBefore(beanElement, context);
		
		beanElement.setAttribute("id", schema);
		beanElement.setAttribute("class", "org.apache.camel.component.jms.JmsComponent");
		
		Element brokerProperty = document.createElement("property");
		brokerProperty.setAttribute("name", "connectionFactory");
		beanElement.appendChild(brokerProperty);
		return element;
	}

}
