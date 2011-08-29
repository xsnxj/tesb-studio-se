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
		int index = uri.indexOf(":");
		String schema = uri.substring(0, index);
		map.put(JMS_SCHEMA_NAME, schema);
		uri = uri.substring(index+1);
		index = uri.indexOf(":");
		String jmsType = "queue";
		if(index!=-1){
			jmsType = uri.substring(0,index);
			uri = uri.substring(index+1);
		}
		map.put(JMS_TYPE, jmsType);
		
		index = uri.indexOf("?");
		if(index!=-1){
			map.put(JMS_DESTINATION, uri.substring(0,index));
			uri = uri.substring(index+1);
			String[] parameters = uri.split("&");
			for(String s:parameters){
				String[] kv = s.split("=");
				if(!"".equals(kv[0])){
					map.put(kv[0], kv[1]);
				}
			}
		}else{
			map.put(JMS_DESTINATION, uri);
		}
	}

	@Override
	public int getType() {
		return JMS;
	}

}
