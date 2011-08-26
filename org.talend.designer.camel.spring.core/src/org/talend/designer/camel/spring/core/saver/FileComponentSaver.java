package org.talend.designer.camel.spring.core.saver;

import java.util.Map;
import java.util.Set;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FileComponentSaver extends AbstractComponentSaver {

	public FileComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <from uri="file:directoryName[?options]" />
	 * or 
	 * <to uri="file:directoryName[?options]" />
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		SpringRouteNode preNode = srn.getParent();
		Element element = null;
		if (preNode == null) {
			element = document.createElement(FROM_ELE);
		} else {
			element = document.createElement(TO_ELE);
		}
		StringBuilder sb = new StringBuilder();
		Map<String, String> parameter = srn.getParameter();
		Set<String> keySet = parameter.keySet();
		for(String key:keySet){
			if(FILE_PATH.equals(key)){
				continue;
			}
			String value = parameter.get(key);
			value = removeQuote(value);
			if(null==value||"".equals(value)){
				continue;
			}
			sb.append(key);
			sb.append("=");
			sb.append(value);
			sb.append("&");
		}
		String filePath = "file:"+removeQuote(parameter.get(FILE_PATH));
		if(sb.length()>1){
			sb.deleteCharAt(sb.length()-1);
			filePath += "?"+sb.toString();
		}
		
		element.setAttribute(URI_ATT, filePath);
		parent.appendChild(element);
		return element;
	}

}
