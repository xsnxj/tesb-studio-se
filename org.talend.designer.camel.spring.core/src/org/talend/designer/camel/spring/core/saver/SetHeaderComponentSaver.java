package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class SetHeaderComponentSaver extends AbstractComponentSaver {

	public SetHeaderComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <setHeader headerName="headerName">
	 * 		<method beanType="beanClass"/>
	 * 		or
	 * 		<expressionType>expressionValue</expressionType>
	 * </setHeader>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(SETHEADER_ELE);
		parent.appendChild(element);
		
		Map<String, String> parameter = srn.getParameter();
		
		//set header attribute
		String headerName = parameter.get(SH_HEADER_NAME);
		if(headerName==null){
			headerName = "";
		}else{
			headerName = removeQuote(headerName);
		}
		element.setAttribute("headerName", headerName);
		
		//create expression type
		String type = parameter.get(EP_EXPRESSION_TYPE);
		if(type!=null){
			String text = parameter.get(EP_EXPRESSION_TEXT);
			if(text!=null){
				text = removeQuote(text);
			}else{
				text = "";
			}
			if("bean".equals(type)){
				Element methodElement = document.createElement("method");
				element.appendChild(methodElement);
				methodElement.setAttribute("beanType", text);
			}else{
				Element typeElement = document.createElement(type);
				element.appendChild(typeElement);
				Text createTextNode = document.createTextNode(text);
				typeElement.appendChild(createTextNode);
			}
		}
		
		return element;
	}

}
