package org.talend.designer.camel.spring.core.saver;

import java.util.Map;
import java.util.Set;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JmsComponentSaver extends AbstractComponentSaver {

	public JmsComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <bean class="org.apache.camel.component.jms.JmsComponent" id="jmsId">
	 * 		<!-- Auto generated property -->
	 * 		<property name="connectionFactory" 
	 * 				value="vm://localhost?broker.persistent=false&amp;amp;broker.useJmx=false"/>
	 * </bean>
	 * ...
	 * <to uri="jmsId:(queue|topic):destination?k=v&k=v&...&k=v"/>
	 * or
	 * <from uri="jmsId:(queue|topic):destination?k=v&k=v&...&k=v"/>
	 */
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
		Element beanElement = addBeanElement(schema, "org.apache.camel.component.jms.JmsComponent");

		//Comment on element
		Comment brokerPropertyComment = document.createComment("Auto generated property.");
        beanElement.appendChild(brokerPropertyComment);
        
		Element brokerProperty = document.createElement("property");
		brokerProperty.setAttribute("name", "connectionFactory");
		//Add a url attribute
		brokerProperty.setAttribute("value", "vm://localhost?broker.persistent=false&amp;broker.useJmx=false");
		beanElement.appendChild(brokerProperty);
		
		
		return element;
	}

}
