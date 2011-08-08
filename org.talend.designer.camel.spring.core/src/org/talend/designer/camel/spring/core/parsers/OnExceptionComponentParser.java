package org.talend.designer.camel.spring.core.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.model.ExpressionSubElementDefinition;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class OnExceptionComponentParser extends AbstractComponentParser {

	public OnExceptionComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
		// TODO Auto-generated constructor stub
	}

	private List<OnExceptionDefinition> ids = new ArrayList<OnExceptionDefinition>();

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		ids.add((OnExceptionDefinition) oid);
		
		OnExceptionDefinition oed = (OnExceptionDefinition) oid;
		List<String> exceptions = oed.getExceptions();
		StringBuilder sb = new StringBuilder();
		for(String s:exceptions){
			sb.append(s);
			sb.append(";");
		}
		map.put(LB_EXCEPTIONS, sb.toString());
		
		RedeliveryPolicyDefinition redeliveryPolicy = oed.getRedeliveryPolicy();
		String maximumRedeliveries = redeliveryPolicy.getMaximumRedeliveries();
		String maximumRedeliveryDelay = redeliveryPolicy.getMaximumRedeliveryDelay();
		
		map.put(OE_MAX_REDELIVER_DELAY, maximumRedeliveryDelay);
		map.put(OE_MAX_REDELIVER_TIMES, maximumRedeliveries);
		
		boolean useOriginalMessage = oed.isUseOriginalMessage();
		map.put(OE_USE_ORIGINAL_MSG, useOriginalMessage+"");
		
		ExpressionSubElementDefinition continued = oed.getContinued();
		ExpressionSubElementDefinition handled = oed.getHandled();
		if(handled!=null&&"true".equals(handled.toString())){
			map.put(OE_EXCEPTION_BEHAVIOR, OE_HANDLE_EXCEPTION);
		}else if(continued!=null&&"true".equals(continued.toString())){
			map.put(OE_EXCEPTION_BEHAVIOR, OE_CONTINUE_EXCEPTION);
		}
		
		String asyncDelayedRedelivery = redeliveryPolicy.getAsyncDelayedRedelivery();
		map.put(OE_ASYNC_DELAY_REDELIVER, asyncDelayedRedelivery);
		
	}

	public boolean hasProcessed(OnExceptionDefinition id) {
		return ids.contains(id);
	}

	@Override
	public int getType() {
		return INTERCEPT;
	}

	@Override
	public void clear() {
		ids.clear();
	}

}
