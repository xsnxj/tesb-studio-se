package org.talend.designer.camel.spring.core;

import java.util.Map;

public interface ISpringParserListener {

	public void preProcess();
	
	public void process(int componentType, Map<String, String> parameters,
			int connectionType, String sourceId, Map<String, String> connectionParameters);

	public void postProcess();
}
