package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.ConvertBodyDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class ConvertBodyComponentParser extends AbstractComponentParser {

	public ConvertBodyComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		ConvertBodyDefinition cbd = (ConvertBodyDefinition) oid;
		String typeClass = cbd.getType();
		map.put(CV_TARGET_TYPE_CLASS, typeClass+".class");
	}

	@Override
	public int getType() {
		return CONVERT;
	}

}
