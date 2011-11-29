package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MulticastComponentSaver extends AbstractComponentSaver {

	private String ID = "multicast";
	private int index = 0;
	
	public MulticastComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <bean id="beanId" class="AggregateStrategyClass/>
	 * ...
	 * <multicast parallelProcessing="true/false" timeout="count" strategyRef="beanId">
	 * 		<to uri="uri"/>
	 * 		...
	 * 		<to uri="uri"/>
	 * </multicast>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(MULTICAST_ELE);
		parent.appendChild(element);
		
		Map<String, String> parameter = srn.getParameter();
		//set timeout attribute
		String timeout = parameter.get(ML_TIMEOUT);
		if(timeout!=null){
			element.setAttribute("timeout", timeout);
		}
		
		//set parallel attribute
		String parallel = parameter.get(ML_IS_PARALLEL);
		if(parallel!=null){
			element.setAttribute("parallelProcessing", parallel);
		}
		
		//create strategy bean
		String strategy = parameter.get(ML_AGGREGATE_STRATEGY);
		if(strategy!=null){
			index++;
			addBeanElement(ID+index, strategy);
			element.setAttribute("strategyRef", ID+index);
		}
		
		//create to elements
		String destinations = parameter.get(ML_DESTINATIONS);
		if(destinations!=null&&!"".equals(destinations)){
			String[] splits = destinations.split(",");
			for(String s:splits){
				Element toElement = document.createElement(TO_ELE);
				toElement.setAttribute("uri", removeQuote(s));
				element.appendChild(toElement);
			}
		}
		
		return element;
	}

}
