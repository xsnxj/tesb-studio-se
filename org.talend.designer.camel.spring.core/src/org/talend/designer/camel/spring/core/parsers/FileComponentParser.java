package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.ToDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class FileComponentParser extends AbstractComponentParser {

	public FileComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
		// TODO Auto-generated constructor stub
	}

	protected void parse(OptionalIdentifiedDefinition fd, Map<String,String> map) {
		String uri = null;
		if(fd instanceof FromDefinition){
			uri = ((FromDefinition)fd).getUri();
		}else if(fd instanceof ToDefinition){
			uri = ((ToDefinition)fd).getUri();
		}
		assert uri != null;
		int colonIndex = uri.indexOf(":");
		if (colonIndex == -1) {
			return;
		}
		int quesMark = uri.indexOf("?");
		if (quesMark == -1) {
			map.put(FILE_PATH, uri.substring(colonIndex + 1));
		} else {
			map.put(FILE_PATH, uri.substring(colonIndex + 1, quesMark));
			String options = uri.substring(quesMark + 1);
			String[] parameters = options.split("&");
			for (String p : parameters) {
				String[] kv = p.split("=");
				map.put(kv[0], kv[1]);
			}
		}
	}
	
	public int getType() {
		return FILE;
	}

}
