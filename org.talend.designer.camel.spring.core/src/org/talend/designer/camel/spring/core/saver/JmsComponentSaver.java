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
	/**
	 * generated xml format:
	 * <bean class="org.apache.camel.component.jms.JmsComponent" id="jmsId">
	 * 		<!-- Auto generated property -->
	 * 		<property name="connectionFactory" 
	 * 				value="vm://localhost?broker.persistent=false&ampbroker.useJmx=false"/>
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
		
		//create bean
		Element beanElement = addBeanElement(schema, "org.apache.camel.component.jms.JmsComponent");

		Element connectionFactory = document.createElement("property");
		connectionFactory.setAttribute("name", "connectionFactory");
		beanElement.appendChild(connectionFactory);
		
		Element factoryBean = document.createElement(BEAN_ELE);
		connectionFactory.appendChild(factoryBean);
		String brokerType = parameter.get(JMS_BROKER_TYPE);
		parameter.remove(JMS_BROKER_TYPE);
		if (JMS_ACTIVEMQ_BROKER.equals(brokerType)) {
			factoryBean.setAttribute("class",
					"org.apache.activemq.ActiveMQConnectionFactory");
			Element urlProperty = document.createElement("property");
			factoryBean.appendChild(urlProperty);
			String brokerUrl = parameter.get("brokerURL");
			parameter.remove("brokerURL");
			urlProperty.setAttribute("brokerURL", brokerUrl);

		} else if (JMS_WMQ_BROKER.equals(brokerType)) {
			factoryBean.setAttribute("class",
					"com.ibm.mq.jms.MQQueueConnectionFactory");
			Element queueManagerProperty = document.createElement("property");
			factoryBean.appendChild(queueManagerProperty);
			queueManagerProperty.setAttribute("queueManager",
					parameter.get("queueManager"));
			parameter.remove("queueManager");

			Element tranportTypeProperty = document.createElement("property");
			factoryBean.appendChild(tranportTypeProperty);
			tranportTypeProperty.setAttribute("transportType",
					parameter.get("transportType"));
			parameter.remove("transportType");

			Element hostNameProperty = document.createElement("property");
			factoryBean.appendChild(hostNameProperty);
			hostNameProperty
					.setAttribute("hostName",
					parameter.get("hostName"));
			parameter.remove("hostName");

			Element portProperty = document.createElement("property");
			factoryBean.appendChild(portProperty);
			portProperty.setAttribute("port", parameter.get("port"));
			parameter.remove("port");

		}
		
		String type = parameter.get(JMS_TYPE);
		parameter.remove(JMS_TYPE);
		if (type != null) {
			sb.append(":");
			sb.append(type);
		}

		String destination = parameter.get(JMS_DESTINATION);
		parameter.remove(JMS_DESTINATION);
		sb.append(":");
		sb.append(destination);

		Set<String> keySet = parameter.keySet();
		if (keySet.size() > 0) {
			sb.append("?");
		}
		for (String s : keySet) {
			String value = parameter.get(s);
			sb.append(removeQuote(s));
			sb.append("=");
			sb.append(removeQuote(value));
			sb.append("&");
		}
		if (keySet.size() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		element.setAttribute(URI_ATT, sb.toString());

		return element;
	}

}
