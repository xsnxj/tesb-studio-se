package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class SplitterComponentSaver extends AbstractComponentSaver {

	public SplitterComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}
	
	@Override
	/**
	 * generated xml format:
	 * <split>
	 * 		<expressionType>expressionValue</expressionType>
	 * 		...
	 * </split>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(SPLIT_ELE);
		parent.appendChild(element);
		Map<String, String> parameter = srn.getParameter();
		String express = parameter.get(SP_SPLIT_EXPRESS);
		if(express!=null&&!"".equals(express)){
			int index = express.indexOf(".");
			int leftB = express.indexOf("(", index);
			if(leftB!=-1){
				String type = express.substring(index+1,leftB).trim();
				String value = "";
				int firstQuote = express.indexOf("\"");
				int nextQuote = express.indexOf("\"",firstQuote+1);
				if(firstQuote!=-1&&nextQuote!=-1){
					value = express.substring(firstQuote+1,nextQuote);
				}
				if("tokenize".equals(type)){
					Element typeElement = document.createElement(type);
					typeElement.setAttribute("token", value);
					element.appendChild(typeElement);
				}else{
					Element typeElement = document.createElement(type);
					element.appendChild(typeElement);
					Text textNode = document.createTextNode(value);
					typeElement.appendChild(textNode);
				}
			}
		}
		return element;
	}

}
