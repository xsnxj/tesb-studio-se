package org.talend.designer.camel.spring.core.parsers;

import java.util.List;
import java.util.Map;

import org.apache.camel.model.BeanDefinition;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class MulticastComponentParser extends AbstractComponentParser {

	public MulticastComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		MulticastDefinition md = (MulticastDefinition) oid;
		
		List<ProcessorDefinition> outputs = md.getOutputs();
		StringBuilder sb = new StringBuilder();
		for(ProcessorDefinition out:outputs){
			if(out instanceof ToDefinition){
				sb.append(((ToDefinition)out).getUri());
			}else if(out instanceof BeanDefinition){
				BeanDefinition bd = (BeanDefinition) out;
				sb.append(bd.getShortName());
				sb.append(":");
				sb.append(bd.getRef());
			}else{
				sb.append(out.toString());
			}
			sb.append(";");
		}
		map.put(ML_DESTINATIONS, sb.toString());
		
		boolean parallelProcessing = md.isParallelProcessing();
		map.put(ML_IS_PARALLEL, parallelProcessing+"");
		
		AggregationStrategy aggregationStrategy = md.getAggregationStrategy();
		if(aggregationStrategy!=null){
			map.put(ML_AGGREGATE_STRATEGY, aggregationStrategy.getClass().getName());
		}else{
			String strategyRef = md.getStrategyRef();
			if(strategyRef!=null){
				String registeredBeanClass = getRegisteredBeanClass(strategyRef);
				if(registeredBeanClass!=null){
					map.put(ML_AGGREGATE_STRATEGY, registeredBeanClass);
				}
			}
		}
		
		Long timeout = md.getTimeout();
		if(timeout!=null){
			map.put(ML_TIMEOUT, timeout.longValue()+"");
		}
	}

	@Override
	public int getType() {
		return MULTICAST;
	}

}
