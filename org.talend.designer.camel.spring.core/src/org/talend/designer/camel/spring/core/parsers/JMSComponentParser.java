package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.ToDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;
public class JMSComponentParser extends AbstractComponentParser {

	public JMSComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		String uri = null;
		if(oid instanceof FromDefinition){
			uri = ((FromDefinition)oid).getUri();
		}else if(oid instanceof ToDefinition){
			uri = ((ToDefinition)oid).getUri();
		}
		assert uri != null;
		int index = uri.indexOf(":");
		String schema = uri.substring(0, index);
		map.put(JMS_SCHEMA_NAME, schema);
		index = uri.indexOf(":");
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
