package org.talend.designer.camel.spring.core.saver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ActivemqComponentSaver extends AbstractComponentSaver {

	private int index = 1;
	
	private Map<String, String> brokerMap = new HashMap<String, String>();
	
	public ActivemqComponentSaver(Document document, Element rootElement, Element contextElement) {
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
		//check attribute of activemq
		String attribute = root.getAttribute(XMLNS_AMQ);
		if(attribute==null||!AMQ_NS.equals(attribute)){
			//add namespace definition
			root.setAttribute(XMLNS_AMQ, AMQ_NS);
			
			//add schema location definition
			StringBuilder sb = new StringBuilder();
			String nsLocations = root.getAttribute(NS_LOCATION);
			if(nsLocations!=null&&!"".equals(nsLocations)){
				sb.append(nsLocations);
				sb.append(" ");
			}
			sb.append(AMQ_NS);
			sb.append(" ");
			sb.append(AMQ_XSD);
			root.setAttribute(NS_LOCATION, sb.toString());
		}
		
		//create element
		SpringRouteNode preNode = srn.getParent();
		
		Map<String, String> parameter = srn.getParameter();
		String brokerUrl = parameter.get("brokerURL");
		String id = null;
		if(brokerUrl!=null){
			id = brokerMap.get(brokerUrl);
		}
		if(id==null){
			//create bean
			id = "activemq"+index;
			index ++;
			Element beanElement = addBeanElement(id,
					"org.apache.activemq.camel.component.ActiveMQComponent");
			Element brokerProperty = addElement("property", beanElement);
			addAttribute("name", "brokerURL", brokerProperty);
			addAttribute("value", brokerUrl, brokerProperty);
			if(brokerUrl!=null){
				brokerMap.put(brokerUrl, id);
			}
		}
		
		//create element
		String destination = parameter.get(AMQ_MSG_DESTINATION);
		String type = parameter.get(AMQ_MESSAGE_TYPE);
		
		String url = id+":"+type+":"+destination;
		StringBuilder sb = new StringBuilder();
		Set<String> keySet = parameter.keySet();
		for(String s:keySet){
			if(AMQ_MESSAGE_TYPE.equals(s)||AMQ_MSG_DESTINATION.equals(s)||"brokerURL".equals(s)){
				continue;
			}
			String value = parameter.get(s);
			value = removeQuote(value);
			if(value==null||"".equals(value)){
				continue;
			}
			sb.append("&");
			sb.append(s);
			sb.append("=");
			sb.append(value);
		}
		if(sb.length()>0){
			sb.deleteCharAt(0);
			url += "?"+sb.toString();
		}
		Element element = null;
		if (preNode == null) {
			element = document.createElement(FROM_ELE);
		} else {
			element = document.createElement(TO_ELE);
		}
		
		element.setAttribute(URI_ATT, url);
		parent.appendChild(element);
		return element;
	}
	
	@Override
	public void afterSaved() {
		super.afterSaved();
		brokerMap.clear();
		brokerMap = null;
	}

}
