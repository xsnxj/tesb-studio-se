package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DynamicComponentSaver extends AbstractComponentSaver {

	private String dynamicId = "dynamic";
	private int index = 0;
	
	public DynamicComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <bean id="beanId" class="beanclass" />
	 * ...
	 * <dynamicRouter>
	 * 		<method ref="beanId" [method="beanmethod"] />
	 * </dynamicRouter>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(DYNAMIC_ELE);
		parent.appendChild(element);
		
		//create bean element
		Map<String, String> parameter = srn.getParameter();
		String beanClass = parameter.get(BN_BEAN_CLASS);
		if(beanClass!=null&&beanClass.endsWith(".class")){
			beanClass = beanClass.substring(0,beanClass.length()-".class".length());
		}
		index++;
		addBeanElement(dynamicId+index,beanClass);
		
		//create sub method element
		Element methodEle = document.createElement("method");
		element.appendChild(methodEle);
		
		methodEle.setAttribute("ref", dynamicId+index);
		
		String method = parameter.get(BN_BEAN_METHOD);
		method = removeQuote(method);
		if(method!=null&&!"".equals(method)){
			methodEle.setAttribute("method", method);
		}
		return element;
	}

}
