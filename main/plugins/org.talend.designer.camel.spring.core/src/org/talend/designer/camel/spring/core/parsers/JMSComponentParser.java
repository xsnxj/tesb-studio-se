package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.ToDefinition;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.TypedStringValue;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;
public class JMSComponentParser extends AbstractComponentParser {

	public JMSComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		String uri = null;
		if(oid instanceof FromDefinition){
			uri = ((FromDefinition)oid).getUri();
		}else if(oid instanceof ToDefinition){
			uri = ((ToDefinition)oid).getUri();
		}
		int index = uri.indexOf(":");
		String schema = uri.substring(0, index);
		map.put(JMS_SCHEMA_NAME, schema);
		uri = uri.substring(index+1);
		index = uri.indexOf(":");
		String jmsType = "queue";
		if(index!=-1){
			jmsType = uri.substring(0,index);
			uri = uri.substring(index+1);
		}
		map.put(JMS_TYPE, jmsType);
		
		index = uri.indexOf("?");
		if(index!=-1){
			map.put(JMS_DESTINATION, uri.substring(0,index));
			uri = uri.substring(index+1);
			String[] parameters = uri.split("&");
			for(String s:parameters){
				String[] kv = s.split("=");
				if(!"".equals(kv[0])){
					map.put(kv[0], kv[1]);
				}
			}
		}else{
			map.put(JMS_DESTINATION, uri);
		}

		BeanDefinition beanDefinition = getBeanDefinition(schema);
		String beanClassName = appContext.getRegisterBeanClassName(schema);
		if (ActiveMQComponent.class.getName().equals(beanClassName)) {
			map.put(JMS_BROKER_TYPE, JMS_ACTIVEMQ_BROKER);
			if (beanDefinition != null) {
				MutablePropertyValues propertyValues = beanDefinition
						.getPropertyValues();
				if (propertyValues != null && !propertyValues.isEmpty()) {
					PropertyValue[] values = propertyValues.getPropertyValues();
					for (PropertyValue pv : values) {
						String name = pv.getName();
						Object value = pv.getValue();
						if (value != null) {
							if (value instanceof TypedStringValue) {
								map.put(name,
										((TypedStringValue) value).getValue());
							} else {
								map.put(name, value.toString());
							}
						} else {
							map.put(name, "");
						}
					}
				}
			}
		} else {
			if (beanDefinition != null) {
				map.put(JMS_BROKER_TYPE, JMS_CUSTOM_BROKER);
				MutablePropertyValues propertyValues = beanDefinition
						.getPropertyValues();
				if (propertyValues == null || propertyValues.isEmpty()) {
					return;
				}
				PropertyValue propertyValue = propertyValues
						.getPropertyValue("connectionFactory");
				Object value = propertyValue.getValue();
				if (!(value instanceof BeanDefinitionHolder)) {
					return;
				}
				BeanDefinitionHolder bdh = (BeanDefinitionHolder) value;
				BeanDefinition factoryBean = bdh.getBeanDefinition();
				if (factoryBean == null) {
					return;
				}
				String factoryBeanClassName = factoryBean.getBeanClassName();
				if (ActiveMQConnectionFactory.class.getName().equals(
						factoryBeanClassName)) {
					map.put(JMS_BROKER_TYPE, JMS_ACTIVEMQ_BROKER);
				} else if ("com.ibm.mq.jms.MQQueueConnectionFactory"
						.equals(factoryBeanClassName)) {
					map.put(JMS_BROKER_TYPE, JMS_WMQ_BROKER);
				}
				MutablePropertyValues factoryProperties = factoryBean
						.getPropertyValues();
				if (factoryProperties != null && !factoryProperties.isEmpty()) {
					PropertyValue[] values = factoryProperties
							.getPropertyValues();
					for (PropertyValue pv : values) {
						String name = pv.getName();
						value = pv.getValue();
						if (value != null) {
							if (value instanceof TypedStringValue) {
								map.put(name,
										((TypedStringValue) value).getValue());
							} else {
								map.put(name, value.toString());
							}
						} else {
							map.put(name, "");
						}
					}
				}
			}
		}
	}

	@Override
	public int getType() {
		return JMS;
	}

}
