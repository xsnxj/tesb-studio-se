package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.ToDefinition;

public class MessageEndpointParser extends AbstractComponentParser {

	protected void parse(OptionalIdentifiedDefinition fd, Map<String, String> map) {
		String uri = null;
		if (fd instanceof FromDefinition) {
			uri = ((FromDefinition) fd).getUri();
		} else if (fd instanceof ToDefinition) {
			uri = ((ToDefinition) fd).getUri();
		}
		assert uri != null;
		map.put(ENDPOINT_URI, uri);
	}

	public int getType() {
		return MSGENDPOINT;
	}

}
