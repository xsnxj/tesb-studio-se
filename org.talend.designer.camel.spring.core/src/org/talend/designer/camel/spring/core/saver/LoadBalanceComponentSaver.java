package org.talend.designer.camel.spring.core.saver;

import java.util.Map;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class LoadBalanceComponentSaver extends AbstractComponentSaver {

	private String ID = "loadBalance";
	private int index = 0;

	public LoadBalanceComponentSaver(Document document, Element rootElement,
			Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <loadBalance ref="customStrategy">
	 * 		<random/>
	 * 		or
	 * 		<topic/>
	 * 		or
	 * 		<roundRobin/>
	 * 		or
	 * 		<sticky>
	 * 			<correlationExpression>
	 * 				<expressionType>value</expressionType
	 * 			</correlationExpression>
	 * 		</sticky>
	 * 		or
	 * 		<failover roundRobin="true/false" maximumFailoverAttempts="count">
	 * 			<exception>exceptionClass</exception>
	 * 			...
	 * 			<exception>exceptionClass</exception>
	 * 		</failover>
	 * </loadBalance>
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(LOADBALANCE_ELE);
		parent.appendChild(element);

		Map<String, String> parameter = srn.getParameter();
		String strategy = parameter.get(LB_BALANCE_STRATEGY);
		if (LB_RANDOM_STRATEGY.equals(strategy)
				|| LB_ROUND_STRATEGY.equals(strategy)
				|| LB_TOPIC_STRATEGY.equals(strategy)) {
			Element strategyElement = document.createElement(strategy);
			element.appendChild(strategyElement);
		} else if (LB_STICKY_STRATEGY.equals(strategy)) {
			Element strategyElement = document
					.createElement(LB_STICKY_STRATEGY);
			element.appendChild(strategyElement);
			Element expressionElement = document
					.createElement("correlationExpression");
			strategyElement.appendChild(expressionElement);
			String type = parameter.get(EP_EXPRESSION_TYPE);
			String text = parameter.get(EP_EXPRESSION_TEXT);
			if (text == null) {
				text = "";
			}
			if (type != null) {
				Element typeElement = document.createElement(type);
				expressionElement.appendChild(typeElement);
				Text textNode = document.createTextNode(removeQuote(text));
				typeElement.appendChild(textNode);
			}
		} else if (LB_FAILOVER_STRATEGY.equals(strategy)) {
			Element failoverElement = document.createElement("failover");
			element.appendChild(failoverElement);

			String failoverType = parameter.get(LB_FAILOVER_TYPE);
			if (LB_EXCEPTION_TYPE.equals(failoverType)) {
				String exceptions = parameter.get(LB_EXCEPTIONS);
				if (exceptions != null) {
					String[] splits = exceptions.split(";");
					for (String s : splits) {
						Element exceptionElement = document
								.createElement("exception");
						failoverElement.appendChild(exceptionElement);
						Text textNode = document.createTextNode(s);
						exceptionElement.appendChild(textNode);
					}
				}
			} else if (LB_ROUND_ROBIN_TYPE.equals(failoverType)) {
				String inheritHandler = parameter.get(LB_INHERIT_HANDLE);
				if (null != inheritHandler) {
					element.setAttribute("inheritErrorHandler", inheritHandler);
				}
				String maximumAttempts = parameter.get(LB_MAXIMUM_ATTAMPTS);
				String attamptType = parameter.get(LB_ATTAMPT_TYPE);
				if (LB_ATTAMPT_FOREVER.equals(attamptType)) {
					maximumAttempts = "-1";
				} else if (LB_ATTAMPT_NEVER.equals(attamptType)) {
					maximumAttempts = "0";
				}
				if (null != maximumAttempts) {
					failoverElement.setAttribute("maximumFailoverAttempts",
							maximumAttempts);
				}
				String isRoundRobin = parameter.get(LB_IS_ROUND_ROBIN);
				if (null != isRoundRobin) {
					failoverElement.setAttribute("roundRobin", isRoundRobin);
				}
			}
		} else if (LB_CUSTOM_STRATEGY.equals(strategy)) {
			String customStrategy = parameter.get(LB_CUSTOM_STRATEGY);
			if (customStrategy != null) {
				index++;
				addBeanElement(ID+index, customStrategy);
				element.setAttribute("ref", ID + index);
			}
		}
		return element;
	}

}
