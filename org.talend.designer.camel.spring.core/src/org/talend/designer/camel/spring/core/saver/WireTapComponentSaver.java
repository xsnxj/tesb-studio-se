package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class WireTapComponentSaver extends AbstractComponentSaver {

	public WireTapComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	/**
	 * the generated xml format looks like:
	 * <wireTap processorRef="beanId" uri="uri" copy="true/false">
	 * 		<body>
	 * 			<expressionDefinition></expressionDefinition>
	 * 		</body>
	 * </wireTap>
	 */
	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(WIRETAP_ELE);
		parent.appendChild(element);
		
		//set uri attribute
		Map<String, String> parameter = srn.getParameter();
		String uri = parameter.get(ENDPOINT_URI);
		if(uri==null){
			uri = "";
		}else{
			uri = removeQuote(uri);
		}
		element.setAttribute("uri", uri);
		
		//set copy attribute
		String copy = parameter.get(WT_WIRETAP_COPY);
		if(copy!=null){
			element.setAttribute("copy", copy);
		}
		
		//create expression element or processorRef attribute
		String populateType = parameter.get(WT_POPULATE_TYPE);
		if(WT_NEW_EXPRESSION_POP.equals(populateType)){
			String type = parameter.get(EP_EXPRESSION_TYPE);
			String text = parameter.get(EP_EXPRESSION_TEXT);
			if(type!=null){
				Element bodyElement = document.createElement("body");
				element.appendChild(bodyElement);
				Element sub = document.createElement(type);
				bodyElement.appendChild(sub);
				if(text==null){
					text = "";
				}else{
					text = removeQuote(text);
				}
				Text textNode = document.createTextNode(text);
				sub.appendChild(textNode);
			}
		}else if(WT_NEW_PROCESSOR_POP.equals(populateType)){
			element.setAttribute("processorRef", "");
		}
		
		
		return element;
	}

}
