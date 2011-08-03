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

public class LoadBalanceComponentParser extends AbstractComponentParser {

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
			map.put(BALANCE_STRATEGY, FAILOVER_STRATEGY);
			map.put(FAILOVER_TYPE, BASIC_TYPE);
			if (exceptions != null && exceptions.size() > 0) {
				map.put(FAILOVER_TYPE, EXCEPTION_TYPE);
				StringBuilder sb = new StringBuilder();
				for (String s : exceptions) {
					sb.append(s);
					sb.append(";");
				}
				map.put(EXCEPTIONS, sb.toString());
			} else if (roundRobin) {
				map.put(FAILOVER_TYPE, ROUND_ROBIN_TYPE);
				map.put(IS_ROUND_ROBIN, fld.isRoundRobin() + "");
				map.put(MAXIMUM_ATTAMPTS, maximumFailoverAttempts.intValue()
						+ "");
			}
		} else if (balancerType instanceof RandomLoadBalancerDefinition) {
			map.put(BALANCE_STRATEGY, RANDOM_STRATEGY);
		} else if (balancerType instanceof RoundRobinLoadBalancerDefinition) {
			map.put(BALANCE_STRATEGY, ROUND_ROBIN_TYPE);
		} else if (balancerType instanceof StickyLoadBalancerDefinition) {
			map.put(BALANCE_STRATEGY, STICKY_STRATEGY);
			StickyLoadBalancerDefinition slbd = (StickyLoadBalancerDefinition) balancerType;
			ExpressionSubElementDefinition expression = slbd
					.getCorrelationExpression();
			map.put(STICKY_EXPRESSION, ExpressionProcessor
					.getExpresstionText(expression.getExpressionType()));
		} else if (balancerType instanceof TopicLoadBalancerDefinition) {
			map.put(BALANCE_STRATEGY, TOPIC_STRATEGY);
		}
		// else if(balancerType instanceof WeightedLoadBalancerDefinition){
		// }
		else {
			map.put(BALANCE_STRATEGY, CUSTOM_STRATEGY);
		}
	}

	@Override
	public int getType() {
		return BALANCE;
	}

}
