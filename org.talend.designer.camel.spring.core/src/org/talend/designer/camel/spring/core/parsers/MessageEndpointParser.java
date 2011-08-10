package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.ToDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class MessageEndpointParser extends AbstractComponentParser {

	public MessageEndpointParser(XmlFileApplicationContext appContext) {
		super(appContext);
		// TODO Auto-generated constructor stub
	}

	protected void parse(OptionalIdentifiedDefinition fd, Map<String, String> map) {
		String uri = null;
		if (fd instanceof FromDefinition) {
			uri = ((FromDefinition) fd).getUri();
		} else if (fd instanceof ToDefinition) {
			uri = ((ToDefinition) fd).getUri();
		}
//		assert uri != null;
		if(uri!=null){
			map.put(ENDPOINT_URI, "\""+uri+"\"");
		}else{
			map.put(ENDPOINT_URI, "\""+fd.toString()+"\"");
		}
	}

	public int getType() {
		return MSGENDPOINT;
	}

}
