package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.Predicate;
import org.apache.camel.model.AggregateDefinition;
import org.apache.camel.model.ExpressionSubElementDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.spi.AggregationRepository;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class AggregateComponentParser extends AbstractComponentParser {

	public AggregateComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		AggregateDefinition ad = (AggregateDefinition) oid;
		ExpressionDefinition expression = ad.getExpression();
		map.putAll(ExpressionProcessor.getExpressionMap(expression));
		
		AggregationStrategy aggregationStrategy = ad.getAggregationStrategy();
		if(aggregationStrategy!=null){
			map.put(AG_AGGREGATE_STRATEGY, aggregationStrategy.getClass().getName());
		}else{
			String aggregationStrategyRef = ad.getAggregationStrategyRef();
			if(aggregationStrategyRef!=null){
				map.put(AG_AGGREGATE_STRATEGY, aggregationStrategyRef);
			}
		}
		
		Integer completionSize = ad.getCompletionSize();
		if(completionSize!=null){
			map.put(AG_COMPLETION_SIEZE, completionSize.intValue()+"");
		}
		
		Long completionTimeout = ad.getCompletionTimeout();
		if(completionTimeout!=null){
			map.put(AG_COMPLETION_TIMEOUT, completionTimeout.longValue()+"");
		}
		
		Long completionInterval = ad.getCompletionInterval();
		if(completionInterval!=null){
			map.put(AG_COMPLETION_INTERVAL, completionInterval.longValue()+"");
		}
		
		ExpressionSubElementDefinition completionPredicate = ad.getCompletionPredicate();
		if(completionPredicate!=null){
			ExpressionDefinition expressionType = completionPredicate.getExpressionType();
			map.putAll(ExpressionProcessor.getExpressionMap(expressionType));
		}
		
		Boolean completionFromBatchConsumer = ad.getCompletionFromBatchConsumer();
		if(completionFromBatchConsumer!=null){
			map.put(AG_COMPLETION_FROM_BATCH, completionFromBatchConsumer.toString());
		}
		
		boolean eagerCheckCompletion = ad.isEagerCheckCompletion();
		map.put(AG_CHECK_COMPLETION, eagerCheckCompletion+"");
		boolean ignoreInvalidCorrelationKeys = ad.isIgnoreInvalidCorrelationKeys();
		map.put(AG_IGNORE_INVALID, ignoreInvalidCorrelationKeys+"");
		boolean groupExchanges = ad.isGroupExchanges();
		map.put(AG_GROUP_EXCHANGES, groupExchanges+"");
		Integer closeCorrelationKeyOnCompletion = ad.getCloseCorrelationKeyOnCompletion();
		if(closeCorrelationKeyOnCompletion!=null){
			map.put(AG_CLOSE_ON_COMPLETION, closeCorrelationKeyOnCompletion.toString());
		}
		
		AggregationRepository aggregationRepository = ad.getAggregationRepository();
		if(aggregationRepository!=null){
			map.put(AG_USE_PERSISTENCE, "true");
		}
	}

	@Override
	public int getType() {
		return AGGREGATE;
	}

}
