package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.ThrottleDefinition;

public class ThrottlerComponentParser extends AbstractComponentParser {

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		ThrottleDefinition td = (ThrottleDefinition) oid;
		Long timePeriodMillis = td.getTimePeriodMillis();
		Long maximumRequestsPerPeriod = td.getMaximumRequestsPerPeriod();
		Boolean asyncDelayed = td.getAsyncDelayed();
		
		map.put(TIME_PERIOD_MILL, timePeriodMillis+"");
		map.put(MAX_REQUEST_PER_PERIOD, maximumRequestsPerPeriod+"");
		map.put(ASYNC_DELAY, asyncDelayed==null?null:asyncDelayed.toString());
	}

	@Override
	public int getType() {
		return THROTTLER;
	}

}
