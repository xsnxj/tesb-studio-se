package org.talend.designer.camel.spring.core.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;

public class OnExceptionComponentParser extends AbstractComponentParser {

	private List<OnExceptionDefinition> ids = new ArrayList<OnExceptionDefinition>();

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		ids.add((OnExceptionDefinition) oid);
	}

	public boolean hasProcessed(OnExceptionDefinition id) {
		return ids.contains(id);
	}

	@Override
	public int getType() {
		return INTERCEPT;
	}

	public void clear() {
		ids.clear();
	}

}
