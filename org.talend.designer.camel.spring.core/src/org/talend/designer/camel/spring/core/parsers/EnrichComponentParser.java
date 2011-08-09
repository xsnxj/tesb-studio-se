package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.EnrichDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.PollEnrichDefinition;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class EnrichComponentParser extends AbstractComponentParser {

	public EnrichComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		if (oid instanceof EnrichDefinition) {
			map.put(ER_MERGE_DATA, ER_PRODUCER);
			
			EnrichDefinition ed = (EnrichDefinition) oid;
			AggregationStrategy aggregationStrategy = ed.getAggregationStrategy();
			if(aggregationStrategy!=null){
				map.put(ER_AGGREGATE_STRATEGY, aggregationStrategy.getClass().getName());
			}else{
				String aggregationStrategyRef = ed.getAggregationStrategyRef();
				if(aggregationStrategyRef!=null){
					String registeredBeanClass = getRegisteredBeanClass(aggregationStrategyRef);
					registeredBeanClass = registeredBeanClass==null?aggregationStrategyRef:registeredBeanClass;
					map.put(ER_AGGREGATE_STRATEGY, registeredBeanClass);
				}
			}
			String resourceUri = ed.getResourceUri();
			map.put(ER_RESOUCE_URI, "\""+resourceUri+"\"");
		} else if (oid instanceof PollEnrichDefinition) {
			map.put(ER_MERGE_DATA, ER_CONSUMER);
			
			PollEnrichDefinition ped = (PollEnrichDefinition) oid;
			
			AggregationStrategy aggregationStrategy = ped.getAggregationStrategy();
			if(aggregationStrategy!=null){
				map.put(ER_AGGREGATE_STRATEGY, aggregationStrategy.getClass().getName());
			}else{
				String aggregationStrategyRef = ped.getAggregationStrategyRef();
				if(aggregationStrategyRef!=null){
					String registeredBeanClass = getRegisteredBeanClass(aggregationStrategyRef);
					registeredBeanClass = registeredBeanClass==null?aggregationStrategyRef:registeredBeanClass;
					map.put(ER_AGGREGATE_STRATEGY, registeredBeanClass);
				}
			}
			
			Long timeout = ped.getTimeout();
			if(timeout!=null){
				long longValue = timeout.longValue();
				if(-1==longValue){
					map.put(ER_TIMEOUT_STYLE, ER_WAIT_UNTIL);
				}else if(0==longValue){
					map.put(ER_TIMEOUT_STYLE, ER_POLL_IMMED);
				}else{
					map.put(ER_TIMEOUT_STYLE, ER_WAIT_TIMEOUT);
					map.put(ER_WAIT_TIMEOUT, longValue+"");
				}
			}
			String resourceUri = ped.getResourceUri();
			map.put(ER_RESOUCE_URI,  "\""+resourceUri+"\"");
		}
	}

	@Override
	public int getType() {
		return ENRICH;
	}

}
