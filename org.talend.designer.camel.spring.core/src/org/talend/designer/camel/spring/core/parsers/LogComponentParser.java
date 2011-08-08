package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.LogDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class LogComponentParser extends AbstractComponentParser {

	public LogComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
		// TODO Auto-generated constructor stub
	}

	protected void parse(OptionalIdentifiedDefinition d, Map<String, String> map) {
		LogDefinition ld = (LogDefinition) d;
		map.put(ENDPOINT_URI, "\"log://"+ld.getMessage()+"\"");
	}

	public int getType() {
		return MSGENDPOINT;
	}

}
