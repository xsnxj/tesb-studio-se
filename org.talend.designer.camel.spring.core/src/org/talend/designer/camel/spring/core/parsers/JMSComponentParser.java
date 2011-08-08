package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;
public class JMSComponentParser extends AbstractComponentParser {

	private String uri;
	private String schema;

	public JMSComponentParser(XmlFileApplicationContext appContext,String schema, String uri) {
		super(appContext);
		this.uri = uri;
		this.schema = schema;
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		map.put(JMS_SCHEMA_NAME, schema);
		int index = uri.indexOf(":");
		if(index!=-1){
			uri = uri.substring(index+1);
			index = uri.indexOf(":");
			if(index!=-1){
				map.put(JMS_TYPE, uri.substring(0,index));
				map.put(JMS_DESTINATION, uri.substring(index+1));
			}else{
				map.put(JMS_TYPE, "queue");
				map.put(JMS_DESTINATION, uri);
			}
		}
	}

	@Override
	public int getType() {
		return JMS;
	}

}
