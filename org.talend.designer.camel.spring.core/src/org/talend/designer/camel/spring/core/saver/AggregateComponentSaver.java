package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.apache.camel.component.hawtdb.HawtDBAggregationRepository;
import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class AggregateComponentSaver extends AbstractComponentSaver {

	private String ID = "aggregateStrategy";
	private int index = 0;
	
	private String REPOID = "repo";
	private int repoIndex = 0;
	
	public AggregateComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <bean id="beanId" class="aggregateClass"/>
	 * <bean id="repoId" class="repoClass" >
	 * 		<property name="recoveryInterval" value="interval"/>
	 * 		<property name="maximumRedeliveries" value="max"/>
	 * 		<property name="deadLetterUri" value="uri"/>
	 * 		<property name="persistentFileName" value="filepath"/>
	 * </bean>
	 * ...
	 * <aggregate aggregationRepositoryRef="repoId" completionSize="size" completionTimeout="timeout" completionInterval="interval"
	 * 			completionFromBatchConsumer="true/false" eagerCheckCompletion="tru/false"
	 * 			ignoreInvalidCorrelationKeys="true/false" groupExchanges="true/false"
	 * 			closeCorrelationKeyOnCompletion="number" strategyRef="beanId">
	 * 		<correlatiomExpression>
	 * 			<expressionType>value</expressionType>
	 * 		</correlationExpresstion>
	 * 		<completionPredicate>
	 * 			<expressionType>value</expressionType>
	 * 		</completionPredicate>
	 * </aggregate>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(AGGREGATE_ELE);
		parent.appendChild(element);
		
		Map<String, String> parameter = srn.getParameter();
		
		String type = parameter.get(EP_EXPRESSION_TYPE);
		String text = parameter.get(EP_EXPRESSION_TEXT);
		if(null!=type&&!"".equals(type)){
			Element expressionElement = document.createElement("correlationExpression");
			element.appendChild(expressionElement);
			Element typeElement = document.createElement(type);
			expressionElement.appendChild(typeElement);
			if(text==null){
				text = "";
			}else{
				text = removeQuote(text);
			}
			Text textNode = document.createTextNode(text);
			typeElement.appendChild(textNode);
		}
		
		String aggregateStrategy = parameter.get(AG_AGGREGATE_STRATEGY);
		if(aggregateStrategy!=null&&!"".equals(aggregateStrategy)){
			index++;
			addBeanElement(ID+index, aggregateStrategy);
			addAttribute("strategyRef", ID+index, element);
		}
		
		String completionSize = parameter.get(AG_COMPLETION_SIEZE);
		if(completionSize!=null&&!"".equals(completionSize)){
			element.setAttribute("completionSize", completionSize);
		}
		
		String interval = parameter.get(AG_COMPLETION_INTERVAL);
		if(interval!=null&&!"".equals(interval)){
			element.setAttribute("completionInterval", interval);
		}
		
		String timeout = parameter.get(AG_COMPLETION_TIMEOUT);
		if(timeout!=null&&!"".equals(timeout)){
			element.setAttribute("completionTimeout", timeout);
		}
		
		String fromBatch = parameter.get(AG_COMPLETION_FROM_BATCH);
		if(fromBatch!=null&&!"".equals(fromBatch)){
			element.setAttribute("completionFromBatchConsumer", fromBatch);
		}
		
		String groupExchange = parameter.get(AG_GROUP_EXCHANGES);
		if(groupExchange!=null&&!"".equals(groupExchange)){
			element.setAttribute("groupExchanges", groupExchange);
		}
		
		String ignoreInvalid = parameter.get(AG_IGNORE_INVALID);
		if(ignoreInvalid!=null&&!"".equals(ignoreInvalid)){
			element.setAttribute("ignoreInvalidCorrelationKeys", ignoreInvalid);
		}
		
		String checkCompletion = parameter.get(AG_CHECK_COMPLETION);
		if(checkCompletion!=null&&!"".equals(checkCompletion)){
			element.setAttribute("eagerCheckCompletion", checkCompletion);
		}
		
		String closeOnCompletion = parameter.get(AG_CLOSE_ON_COMPLETION);
		if(closeOnCompletion!=null&&!"".equals(closeOnCompletion)){
			element.setAttribute("closeCorrelationKeyOnCompletion", closeOnCompletion);
		}
		
		String completionPredicate = parameter.get(AG_COMPLETION_PREDICATE);
		if(completionPredicate!=null&&!"".endsWith(completionPredicate)){
			Element predicateElement = document.createElement("completionPredicate");
			element.appendChild(predicateElement);
			
			Element expressElement = document.createElement("expressionDefinition");
			predicateElement.appendChild(expressElement);
			
			Text textNode = document.createTextNode(removeQuote(completionPredicate));
			expressElement.appendChild(textNode);
			
		}
		
		String repositoryType = parameter.get(AG_REPOSITORY_TYPE);
		if(repositoryType!=null&&!"".equals(repositoryType)){
			String name = "";
			if(AG_AGGREGATION_REPO.equals(repositoryType)||AG_RECOVERABLE_REPO.equals(repositoryType)){
				name = parameter.get(AG_REPOSITORY_NAME);
			}else if(AG_HAWTDB_REPO.equals(repositoryType)){
				name = HawtDBAggregationRepository.class.getName();
			}
			if(name==null){
				name = "";
			}
			//create bean
			repoIndex++;
			Element beanElement = addBeanElement(REPOID+repoIndex,name);
			element.setAttribute("aggregationRepositoryRef", REPOID+repoIndex);
			
			//add properties
			String recoverInterval = parameter.get(AG_RECOVER_INTERVAL);
			if(recoverInterval!=null&&!"".equals(recoverInterval)){
				Element tmp = document.createElement("property");
				beanElement.appendChild(tmp);
				tmp.setAttribute("name"	, "recoveryInterval");
				tmp.setAttribute("value", recoverInterval);
			}
			String maxRedeliveries = parameter.get(AG_MAXIMUM_REDELIVERIES);
			if(maxRedeliveries!=null&&!"".equals(maxRedeliveries)){
				Element tmp = document.createElement("property");
				beanElement.appendChild(tmp);
				tmp.setAttribute("name"	, "maximumRedeliveries");
				tmp.setAttribute("value", maxRedeliveries);
			}
			String deadLetter = parameter.get(AG_DEAD_LETTER_CHANNEL);
			if(deadLetter!=null&&!"".equals(deadLetter)){
				Element tmp = document.createElement("property");
				beanElement.appendChild(tmp);
				tmp.setAttribute("name"	, "deadLetterUri");
				tmp.setAttribute("value", deadLetter);
			}
			String persistentFile = parameter.get(AG_HAWTDB_PERSISFILE);
			if(persistentFile!=null&&!"".equals(persistentFile)){
				Element tmp = document.createElement("property");
				beanElement.appendChild(tmp);
				tmp.setAttribute("name"	, "persistentFileName");
				tmp.setAttribute("value", persistentFile);
			}
		}
		return element;
	}

}
