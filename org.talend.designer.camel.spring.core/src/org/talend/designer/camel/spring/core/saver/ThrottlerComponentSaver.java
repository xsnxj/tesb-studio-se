package org.talend.designer.camel.spring.core.saver;

import java.util.Map;
import java.util.Set;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ThrottlerComponentSaver extends AbstractComponentSaver {

	public ThrottlerComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	public Element save(SpringRouteNode srn, Element parent) {
		Element element = document.createElement(THROTTLER_ELE);
		parent.appendChild(element);
		Map<String, String> parameter = srn.getParameter();
		Set<String> keySet = parameter.keySet();
		for (String s : keySet) {
			String value = parameter.get(s);
			if (s.equals(TH_ASYNC_DELAY)) {
				s = "asyncDelayed";
			} else if (s.equals(TH_MAX_REQUEST_PER_PERIOD)) {
				s = "maximumRequestsPerPeriod";
			} else if (s.equals(TH_TIME_PERIOD_MILL)) {
				s = "timePeriodMillis";
			}
			value = removeQuote(value);
			if (null != value && !"".equals(value))
				element.setAttribute(s, removeQuote(value));
		}
		return element;
	}

}
