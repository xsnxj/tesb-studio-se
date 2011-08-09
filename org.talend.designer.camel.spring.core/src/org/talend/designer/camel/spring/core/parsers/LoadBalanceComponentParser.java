package org.talend.designer.camel.spring.core.parsers;

import java.util.List;
import java.util.Map;

import org.apache.camel.model.ExpressionSubElementDefinition;
import org.apache.camel.model.LoadBalanceDefinition;
import org.apache.camel.model.LoadBalancerDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.loadbalancer.FailoverLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.RandomLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.RoundRobinLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.StickyLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.TopicLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.WeightedLoadBalancerDefinition;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class LoadBalanceComponentParser extends AbstractComponentParser {

	public LoadBalanceComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		LoadBalanceDefinition lbd = (LoadBalanceDefinition) oid;
		LoadBalancerDefinition balancerType = lbd.getLoadBalancerType();
		if (balancerType instanceof FailoverLoadBalancerDefinition) {
			FailoverLoadBalancerDefinition fld = (FailoverLoadBalancerDefinition) balancerType;
			List<String> exceptions = fld.getExceptions();
			Boolean roundRobin = fld.getRoundRobin();
			Integer maximumFailoverAttempts = fld.getMaximumFailoverAttempts();
			map.put(LB_BALANCE_STRATEGY, LB_FAILOVER_STRATEGY);
			map.put(LB_FAILOVER_TYPE, LB_BASIC_TYPE);
			if (exceptions != null && exceptions.size() > 0) {
				map.put(LB_FAILOVER_TYPE, LB_EXCEPTION_TYPE);
				StringBuilder sb = new StringBuilder();
				for (String s : exceptions) {
					sb.append(s);
					sb.append(";");
				}
				map.put(LB_EXCEPTIONS, sb.toString());
			} else if (roundRobin) {
				map.put(LB_FAILOVER_TYPE, LB_ROUND_ROBIN_TYPE);
				map.put(LB_IS_ROUND_ROBIN, fld.isRoundRobin() + "");
				if(maximumFailoverAttempts!=null){
					int intValue = maximumFailoverAttempts.intValue();
					if(intValue==-1){
						map.put(LB_ATTAMPT_TYPE, LB_ATTAMPT_FOREVER);
					}else if(intValue==0){
						map.put(LB_ATTAMPT_TYPE, LB_ATTAMPT_NEVER);
					}else{
						map.put(LB_ATTAMPT_TYPE, LB_ATTAMPT_NUMBERS);
						map.put(LB_MAXIMUM_ATTAMPTS, maximumFailoverAttempts.intValue()
								+ "");
					}
				}else{
					map.put(LB_ATTAMPT_TYPE, LB_ATTAMPT_NEVER);
				}
				Boolean inheritErrorHandler = lbd.isInheritErrorHandler();
				if(inheritErrorHandler!=null){
					map.put(LB_INHERIT_HANDLE, inheritErrorHandler.toString());
				}
			}
		} else if (balancerType instanceof RandomLoadBalancerDefinition) {
			map.put(LB_BALANCE_STRATEGY, LB_RANDOM_STRATEGY);
		} else if (balancerType instanceof RoundRobinLoadBalancerDefinition) {
			map.put(LB_BALANCE_STRATEGY, LB_ROUND_STRATEGY);
		} else if (balancerType instanceof StickyLoadBalancerDefinition) {
			map.put(LB_BALANCE_STRATEGY, LB_STICKY_STRATEGY);
			StickyLoadBalancerDefinition slbd = (StickyLoadBalancerDefinition) balancerType;
			ExpressionSubElementDefinition expression = slbd
					.getCorrelationExpression();
			Map<String, String> expressionMap = ExpressionProcessor
					.getExpressionMap(expression.getExpressionType());
			map.put(LB_STICKY_EXPRESSION, expressionMap.get(EP_EXPRESSION_TEXT));
		} else if (balancerType instanceof TopicLoadBalancerDefinition) {
			map.put(LB_BALANCE_STRATEGY, LB_TOPIC_STRATEGY);
		}
		// else if(balancerType instanceof WeightedLoadBalancerDefinition){
		// }
		else {
			map.put(LB_BALANCE_STRATEGY, LB_CUSTOM_STRATEGY);
		}
	}

	@Override
	public int getType() {
		return BALANCE;
	}

}
