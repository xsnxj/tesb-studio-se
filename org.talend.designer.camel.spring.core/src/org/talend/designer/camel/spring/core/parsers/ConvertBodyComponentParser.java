package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.ConvertBodyDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;

public class ConvertBodyComponentParser extends AbstractComponentParser {

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		ConvertBodyDefinition cbd = (ConvertBodyDefinition) oid;
		String typeClass = cbd.getType();
		map.put(TARGET_TYPE_CLASS, typeClass+".class");
	}

	@Override
	public int getType() {
		return CONVERT;
	}

}
