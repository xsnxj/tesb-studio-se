package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.LogDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;

public class LogComponentParser extends AbstractComponentParser {

	protected void parse(OptionalIdentifiedDefinition d, Map<String, String> map) {
		LogDefinition ld = (LogDefinition) d;
		map.put(ENDPOINT_URI, "\"log://"+ld.getMessage()+"\"");
	}

	public int getType() {
		return MSGENDPOINT;
	}

}
