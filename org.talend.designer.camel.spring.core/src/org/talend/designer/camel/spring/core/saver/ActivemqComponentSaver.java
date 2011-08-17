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
	public Element save(SpringRouteNode srn, Element parent) {
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
			Element beanElement = document.createElement(BEAN_ELE);
			root.insertBefore(beanElement, context);
			beanElement.setAttribute("id", id);
			beanElement.setAttribute("class", "org.apache.activemq.camel.component.ActiveMQComponent");
			
			Element brokerProperty = document.createElement("property");
			brokerProperty.setAttribute("name", "brokerURL");
			brokerProperty.setAttribute("value", brokerUrl);
			beanElement.appendChild(brokerProperty);
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
			sb.append("&");
			sb.append(s);
			sb.append("=");
			sb.append(parameter.get(s));
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
