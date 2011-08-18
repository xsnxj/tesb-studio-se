package org.talend.designer.camel.spring.core.saver;

import java.util.Map;
import java.util.Set;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CxfComponentSaver extends AbstractComponentSaver {

	private String ID = "cxfEndpoint";
	private int index = 0;

	public CxfComponentSaver(Document document, Element rootElement,
			Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		SpringRouteNode preNode = srn.getParent();

		// create cxf Endpoint
		index++;
		Element endpointElement = document.createElement("cxf:cxfEndpoint");
		endpointElement.setAttribute("id", ID + index);
		root.insertBefore(endpointElement, context);

		Map<String, String> parameter = srn.getParameter();
		String address = parameter.get("address");
		if (address == null) {
			address = "";
		}
		endpointElement.setAttribute("address", address);
		parameter.remove("address");

		String type = parameter.get("type");
		if (type != null) {
			if ("wsdlURL".equals(type)) {
				String wsdlURL = parameter.get("wsdlURL");
				endpointElement.setAttribute("wsdlURL", removeQuote(wsdlURL));
			} else if ("serviceClass".equals(type)) {
				String serviceClass = parameter.get("serviceClass");
				endpointElement.setAttribute("serviceClass", serviceClass);
			}

		}
		parameter.remove("serviceClass");
		parameter.remove("wsdlURL");
		parameter.remove("type");

		String portName = parameter.get("endpointName");
		parameter.remove("endpointName");
		String serviceName = parameter.get("serviceName");
		parameter.remove("serviceName");
		String ns = null;
		if (portName != null) {
			int firstCurve = portName.indexOf("{");
			int lastCurve = portName.indexOf("}");
			if (firstCurve == 0 && lastCurve > firstCurve) {
				ns = portName.substring(firstCurve + 1, lastCurve);
			}
			portName = portName.substring(lastCurve + 1);
		}
		if (serviceName != null) {
			int firstCurve = serviceName.indexOf("{");
			int lastCurve = serviceName.indexOf("}");
			if (ns == null) {
				if (firstCurve == 0 && lastCurve > firstCurve) {
					ns = serviceName.substring(firstCurve + 1, lastCurve);
				}
			}
			serviceName = serviceName.substring(lastCurve + 1);
		}

		if (ns != null) {
			endpointElement.setAttribute("xmlns:ns", ns);
		}

		if (ns != null) {
			ns = "ns:";
		} else {
			ns = "";
		}

		if (portName != null) {
			endpointElement.setAttribute("endpointName", ns + portName);
		}
		if (serviceName != null) {
			endpointElement.setAttribute("serviceName", ns + serviceName);
		}
		
		String dataFormat = parameter.get("dataFormat");
		if(dataFormat!=null){
			Element propertiesElement = document.createElement("cxf:properties");
			endpointElement.appendChild(propertiesElement);
			
			Element entryElement = document.createElement("entry");
			propertiesElement.appendChild(entryElement);
			
			entryElement.setAttribute("key", "dataFormat");
			entryElement.setAttribute("value", dataFormat);
		}
		parameter.remove("dataFormat");
		
		StringBuilder sb = new StringBuilder();
		sb.append("cxf:bean:");
		sb.append(ID);
		sb.append(index);
		
		Set<String> keySet = parameter.keySet();
		if(keySet.size()>0){
			sb.append("?");
		}
		for(String s:keySet){
			String value = parameter.get(s);
			sb.append(removeQuote(s));
			sb.append("=");
			sb.append(removeQuote(value));
			sb.append("&");
		}
		
		if(keySet.size()>0){
			sb.deleteCharAt(sb.length()-1);
		}
		
		Element element = null;
		if (preNode == null) {
			element = document.createElement(FROM_ELE);
		} else {
			element = document.createElement(TO_ELE);
		}
		element.setAttribute(URI_ATT, sb.toString());
		parent.appendChild(element);

		return element;
	}

}
