package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class IdempotentComponentSaver extends AbstractComponentSaver {

	private String ID = "idemRepo";
	private int index = 0;
	
	public IdempotentComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <bean id="idemRepoId" class="RepositoryClass">
	 * 		<constructor-arg type="argType" value="argValue"/>
	 * 		...
	 * 		<constructor-arg type="argType" value="argValue"/>
	 * </bean>
	 * ...
	 * <idempotentConsumer messageIdRepositoryRef="idemRepoId">
	 * 		<expressionType>value</expressionType>
	 * </idempotentConsumer>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(IDEMPOTENT_ELE);
		parent.appendChild(element);
		
		Map<String, String> parameter = srn.getParameter();
		String repositoryType = parameter.get(ID_REPOSITORY_TYPE);
		index++;
		String repoClass = "org.apache.camel.processor.idempotent.FileIdempotentRepository";
		if(ID_MEMORY_REPOSITORY.equals(repositoryType)){
			repoClass = "org.apache.camel.processor.idempotent.MemoryIdempotentRepository";
		}
		
		//create bean
		Element beanElement = document.createElement(BEAN_ELE);
		root.insertBefore(beanElement, context);
		beanElement.setAttribute("id", ID+index);
		beanElement.setAttribute("class", repoClass);
		element.setAttribute("messageIdRepositoryRef", ID+index);
		
		if(ID_FILE_REPOSITORY.equals(repositoryType)){
			Element constructorElement = document.createElement("constructor-arg");
			constructorElement.setAttribute("type", "java.io.File");
			constructorElement.setAttribute("value", removeQuote(parameter.get(ID_FILE_STORE)));
			beanElement.appendChild(constructorElement);
			constructorElement = document.createElement("constructor-arg");
			constructorElement.setAttribute("type", "int");
			constructorElement.setAttribute("value", parameter.get(ID_CACHE_SIZE));
			beanElement.appendChild(constructorElement);
		}else if(ID_MEMORY_REPOSITORY.equals(repositoryType)){
			Element constructorElement = document.createElement("constructor-arg");
			constructorElement.setAttribute("type", "int");
			constructorElement.setAttribute("value", parameter.get(ID_CACHE_SIZE));
			beanElement.appendChild(constructorElement);
		}
		
		//create expression
		String type = parameter.get(EP_EXPRESSION_TYPE);
		String text = parameter.get(EP_EXPRESSION_TEXT);
		if(text==null){
			text = "";
		}
		if(type!=null&&!"".equals(type)){
			Element typeElement = document.createElement(type);
			element.appendChild(typeElement);
			Text textNode = document.createTextNode(removeQuote(text));
			typeElement.appendChild(textNode);
		}else{
			Element textElement = document.createElement("expressionDefinition");
			element.appendChild(textElement);
			Text textNode = document.createTextNode(removeQuote(text));
			textElement.appendChild(textNode);
		}
		
		return element;
	}

}
