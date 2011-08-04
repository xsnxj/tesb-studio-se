package org.talend.designer.camel.spring.core.parsers;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.spi.NodeIdFactory;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;

public abstract class AbstractComponentParser implements ICamelSpringConstants{

	public final Map<String, String> parse(NodeIdFactory factory, OptionalIdentifiedDefinition oid){
		Map<String,String> map = new HashMap<String, String>();
		String id = factory.createId(oid);
		map.put(UNIQUE_NAME_ID, id);
		parse(oid, map);
		return map;
	}
	
	public void initial(){
		
	}
	
	protected abstract void parse(OptionalIdentifiedDefinition oid, Map<String, String> map);

	public abstract int getType();
	
	public void clear(){
		
	}
}
