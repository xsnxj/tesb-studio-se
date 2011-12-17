package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class StopComponentParser extends AbstractComponentParser {

	public StopComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {

	}

	@Override
	public int getType() {
		return STOP;
	}

}
