package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class OnExceptionComponentSaver extends AbstractComponentSaver {

	private String ASYNC_DELAY = "asyncDelay";
	private int index = 0;
	
	public OnExceptionComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		index++;
		Element element = document.createElement(ONEXCEPTION_ELE);
		parent.insertBefore(element, parent.getFirstChild());
		
		Map<String, String> parameter = srn.getParameter();
		
		String exceptions = parameter.get(OE_EXCEPTIONS);
		if(exceptions!=null&&!"".equals(exceptions)){
			String[] splits = exceptions.split(";");
			for(String s:splits){
				Element exceptionElement = document.createElement("exception");
				element.appendChild(exceptionElement);
				Text textNode = document.createTextNode(s);
				exceptionElement.appendChild(textNode);
			}
		}
		String useOriginal = parameter.get(OE_USE_ORIGINAL_MSG);
		if(useOriginal!=null&&!"".equals(useOriginal)){
			element.setAttribute("useOriginalMessage", useOriginal);
		}
		String redeliveryTimes = parameter.get(OE_MAX_REDELIVER_TIMES);
		if(redeliveryTimes!=null&&!"".equals(redeliveryTimes)){
			Element redeliveryPolicy = document.createElement("redeliveryPolicy");
			redeliveryPolicy.setAttribute("maximumRedeliveries", redeliveryTimes);
			element.appendChild(redeliveryPolicy);
		}
		String asyncDelay = parameter.get(OE_ASYNC_DELAY_REDELIVER);
		if(asyncDelay!=null&&!"".equals(asyncDelay)){
			element.setAttribute("redeliveryPolicyRef", ASYNC_DELAY+index);
			Element profile = document.createElement("redeliveryPolicyProfile");
			profile.setAttribute("id", ASYNC_DELAY+index);
			profile.setAttribute("asyncDelayedRedelivery", asyncDelay);
			parent.insertBefore(profile, element);
		}
		String exceptionBehavior = parameter.get(OE_EXCEPTION_BEHAVIOR);
		if(exceptionBehavior!=null&&!"".equals(exceptionBehavior)){
			String elementName = null;
			if(OE_HANDLE_EXCEPTION.equals(exceptionBehavior)){
				elementName = "handled";
			}else if(OE_CONTINUE_EXCEPTION.equals(exceptionBehavior)){
				elementName = "continued";
			}
			if(elementName!=null){
				Element behaviorElement = document.createElement(elementName);
				element.appendChild(behaviorElement);
				Element constantElement = document.createElement("constant");
				behaviorElement.appendChild(constantElement);
				Text textNode = document.createTextNode("true");
				constantElement.appendChild(textNode);
			}
		}
		return element;
	}

}
