package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.component.hawtdb.HawtDBAggregationRepository;
import org.apache.camel.model.AggregateDefinition;
import org.apache.camel.model.ExpressionSubElementDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.spi.AggregationRepository;
import org.apache.camel.spi.RecoverableAggregationRepository;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class AggregateComponentParser extends AbstractComponentParser {

	public AggregateComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
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
				map.put(AG_AGGREGATE_STRATEGY, getRegisteredBeanClass(aggregationStrategyRef));
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
			if(expressionType!=null){
				String language = expressionType.getLanguage();
				String ex = expressionType.getExpression();
				map.put(AG_COMPLETION_PREDICATE, language+"(\""+ex+"\")");
			}
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
			if(aggregationRepository instanceof HawtDBAggregationRepository){
				map.put(AG_REPOSITORY_TYPE, AG_HAWTDB_REPO);
				HawtDBAggregationRepository har = (HawtDBAggregationRepository) aggregationRepository; 
				String persistentFileName = har.getPersistentFileName();
				if(persistentFileName!=null){
					map.put(AG_HAWTDB_PERSISFILE, persistentFileName);
				}
			}else if(aggregationRepository instanceof RecoverableAggregationRepository){
				map.put(AG_REPOSITORY_TYPE, AG_RECOVERABLE_REPO);
				map.put(AG_REPOSITORY_NAME, aggregationRepository.getClass().getName());
			}else{
				map.put(AG_REPOSITORY_TYPE, AG_AGGREGATION_REPO);
				map.put(AG_REPOSITORY_NAME, aggregationRepository.getClass().getName());
			}
			if(aggregationRepository instanceof RecoverableAggregationRepository
					||aggregationRepository instanceof HawtDBAggregationRepository){
				RecoverableAggregationRepository rar = (RecoverableAggregationRepository) aggregationRepository;
				long intervalInMillis = rar.getRecoveryIntervalInMillis();
				String deadLetterUri = rar.getDeadLetterUri();
				int maximumRedeliveries = rar.getMaximumRedeliveries();
				map.put(AG_RECOVER_INTERVAL, intervalInMillis+"");
				map.put(AG_DEAD_LETTER_CHANNEL, deadLetterUri);
				map.put(AG_MAXIMUM_REDELIVERIES, maximumRedeliveries+"");
			}
		}else{
			String ref = ad.getAggregationRepositoryRef();
			if(ref!=null){
				map.put(AG_USE_PERSISTENCE, "true");
				BeanDefinition beanDefinition = getBeanDefinition(ref);
				MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
				String beanClassName = getRegisteredBeanClass(ref);
				if(beanClassName.equals("org.apache.camel.component.hawtdb.HawtDBAggregationRepository")){
					map.put(AG_REPOSITORY_TYPE, AG_HAWTDB_REPO);
					PropertyValue hawtDBFile = propertyValues.getPropertyValue("persistentFileName");
					if(hawtDBFile!=null){
						TypedStringValue value = (TypedStringValue) hawtDBFile.getValue();
						map.put(AG_HAWTDB_PERSISFILE, value.getValue());
					}
				}else if(propertyValues.size()>0){
					map.put(AG_REPOSITORY_TYPE, AG_RECOVERABLE_REPO);
					map.put(AG_REPOSITORY_NAME, beanClassName);
				}else{
					map.put(AG_REPOSITORY_TYPE, AG_AGGREGATION_REPO);
					map.put(AG_REPOSITORY_NAME, beanClassName);
				}
				if(propertyValues.size()>0){
					PropertyValue intervalValue = propertyValues.getPropertyValue("recoveryInterval");
					if(intervalValue!=null){
						TypedStringValue value = (TypedStringValue) intervalValue.getValue();
						map.put(AG_RECOVER_INTERVAL, value.getValue());
					}
					PropertyValue deadProperty = propertyValues.getPropertyValue("deadLetterUri");
					if(deadProperty!=null){
						TypedStringValue value = (TypedStringValue) deadProperty.getValue();
						map.put(AG_DEAD_LETTER_CHANNEL, "\""+value.getValue()+"\"");
					}
					PropertyValue maxRedeliveryProperty = propertyValues.getPropertyValue("maximumRedeliveries");
					if(maxRedeliveryProperty!=null){
						TypedStringValue value = (TypedStringValue) maxRedeliveryProperty.getValue();
						map.put(AG_MAXIMUM_REDELIVERIES, value.getValue());
					}
				}
			}
			
		}
	}

	@Override
	public int getType() {
		return AGGREGATE;
	}

}
