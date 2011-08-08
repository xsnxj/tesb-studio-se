package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.ThrottleDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class ThrottlerComponentParser extends AbstractComponentParser {

	public ThrottlerComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		ThrottleDefinition td = (ThrottleDefinition) oid;
		Long timePeriodMillis = td.getTimePeriodMillis();
		Long maximumRequestsPerPeriod = td.getMaximumRequestsPerPeriod();
		Boolean asyncDelayed = td.getAsyncDelayed();
		
		map.put(TH_TIME_PERIOD_MILL, timePeriodMillis+"");
		map.put(TH_MAX_REQUEST_PER_PERIOD, maximumRequestsPerPeriod+"");
		map.put(TH_ASYNC_DELAY, asyncDelayed==null?null:asyncDelayed.toString());
	}

	@Override
	public int getType() {
		return THROTTLER;
	}

}
