package org.talend.designer.camel.spring.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.model.AggregateDefinition;
import org.apache.camel.model.BeanDefinition;
import org.apache.camel.model.CatchDefinition;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.ConvertBodyDefinition;
import org.apache.camel.model.DelayDefinition;
import org.apache.camel.model.DynamicRouterDefinition;
import org.apache.camel.model.EnrichDefinition;
import org.apache.camel.model.FilterDefinition;
import org.apache.camel.model.FinallyDefinition;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.IdempotentConsumerDefinition;
import org.apache.camel.model.InterceptDefinition;
import org.apache.camel.model.LoadBalanceDefinition;
import org.apache.camel.model.LogDefinition;
import org.apache.camel.model.LoopDefinition;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.OtherwiseDefinition;
import org.apache.camel.model.PipelineDefinition;
import org.apache.camel.model.PollEnrichDefinition;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutingSlipDefinition;
import org.apache.camel.model.SetBodyDefinition;
import org.apache.camel.model.SetExchangePatternDefinition;
import org.apache.camel.model.SetHeaderDefinition;
import org.apache.camel.model.SplitDefinition;
import org.apache.camel.model.StopDefinition;
import org.apache.camel.model.ThrottleDefinition;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.model.TryDefinition;
import org.apache.camel.model.WhenDefinition;
import org.apache.camel.model.WireTapDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.spi.NodeIdFactory;
import org.apache.camel.spring.SpringCamelContext;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;
import org.talend.designer.camel.spring.core.parsers.AbstractComponentParser;
import org.talend.designer.camel.spring.core.parsers.ActiveMQComponentParser;
import org.talend.designer.camel.spring.core.parsers.AggregateComponentParser;
import org.talend.designer.camel.spring.core.parsers.BeanComponentParser;
import org.talend.designer.camel.spring.core.parsers.CXFComponentParser;
import org.talend.designer.camel.spring.core.parsers.ConvertBodyComponentParser;
import org.talend.designer.camel.spring.core.parsers.DelayComponentParser;
import org.talend.designer.camel.spring.core.parsers.DynamicComponentParser;
import org.talend.designer.camel.spring.core.parsers.EnrichComponentParser;
import org.talend.designer.camel.spring.core.parsers.ExchangePatternComponentParser;
import org.talend.designer.camel.spring.core.parsers.FTPComponentParser;
import org.talend.designer.camel.spring.core.parsers.FileComponentParser;
import org.talend.designer.camel.spring.core.parsers.FilterComponentParser;
import org.talend.designer.camel.spring.core.parsers.IdempoComponentParser;
import org.talend.designer.camel.spring.core.parsers.InterceptComponentParser;
import org.talend.designer.camel.spring.core.parsers.JMSComponentParser;
import org.talend.designer.camel.spring.core.parsers.LoadBalanceComponentParser;
import org.talend.designer.camel.spring.core.parsers.LogComponentParser;
import org.talend.designer.camel.spring.core.parsers.LoopComponentParser;
import org.talend.designer.camel.spring.core.parsers.MessageEndpointParser;
import org.talend.designer.camel.spring.core.parsers.MessageRouterComponentParser;
import org.talend.designer.camel.spring.core.parsers.MulticastComponentParser;
import org.talend.designer.camel.spring.core.parsers.OnExceptionComponentParser;
import org.talend.designer.camel.spring.core.parsers.PipeLineComponentParser;
import org.talend.designer.camel.spring.core.parsers.ProcessorComponentParser;
import org.talend.designer.camel.spring.core.parsers.RoutingSlipComponentParser;
import org.talend.designer.camel.spring.core.parsers.SetBodyComponentParser;
import org.talend.designer.camel.spring.core.parsers.SetHeaderComponentParser;
import org.talend.designer.camel.spring.core.parsers.SplitComponentParser;
import org.talend.designer.camel.spring.core.parsers.StopComponentParser;
import org.talend.designer.camel.spring.core.parsers.ThrottlerComponentParser;
import org.talend.designer.camel.spring.core.parsers.TryComponentParser;
import org.talend.designer.camel.spring.core.parsers.WireTapComponentParser;

public class CamelSpringParser implements ICamelSpringConstants {

	private List<ISpringParserListener> listeners = new ArrayList<ISpringParserListener>();

	private AbstractComponentParser[] parsers = new AbstractComponentParser[33];

	private XmlFileApplicationContext appContext;

	public void startParse(String filePath) throws Exception {
		try {
			appContext = new XmlFileApplicationContext(filePath);
			SpringCamelContext camelContext = SpringCamelContext
					.springCamelContext(appContext);

			initialize();
			beforeProcessEvent();

			List<RouteDefinition> routeDefinitions = camelContext
					.getRouteDefinitions();
			for (RouteDefinition rd : routeDefinitions) {
				System.out.println("--------------------------");
				parseRouteDefinitions(rd, camelContext);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			endProcessEvent();
			if(appContext!=null){
				appContext.destroy();
				appContext = null;
			}
		}
	}

	private void initialize() {
		parsers[FILE] = new FileComponentParser(appContext);
		parsers[FTP] = new FTPComponentParser(appContext);
		parsers[CXF] = new CXFComponentParser(appContext);
		parsers[MSGENDPOINT] = new MessageEndpointParser(appContext);
		parsers[SPLIT] = new SplitComponentParser(appContext);
		parsers[BEAN] = new BeanComponentParser(appContext);
		parsers[BALANCE] = new LoadBalanceComponentParser(appContext);
		parsers[FILTER] = new FilterComponentParser(appContext);
		parsers[MSGROUTER] = new MessageRouterComponentParser(appContext);
		parsers[ROUTINGSLIP] = new RoutingSlipComponentParser(appContext);
		parsers[SETHEADER] = new SetHeaderComponentParser(appContext);
		parsers[ENRICH] = new EnrichComponentParser(appContext);
		parsers[CONVERT] = new ConvertBodyComponentParser(appContext);
		parsers[DELAY] = new DelayComponentParser(appContext);
		parsers[PROCESSOR] = new ProcessorComponentParser(appContext);
		parsers[AGGREGATE] = new AggregateComponentParser(appContext);
		parsers[MULTICAST] = new MulticastComponentParser(appContext);
		parsers[SETBODY] = new SetBodyComponentParser(appContext);
		parsers[DYNAMIC] = new DynamicComponentParser(appContext);
		parsers[IDEM] = new IdempoComponentParser(appContext);
		parsers[PATTERN] = new ExchangePatternComponentParser(appContext);
		parsers[THROTTLER] = new ThrottlerComponentParser(appContext);
		parsers[LOOP] = new LoopComponentParser(appContext);
		parsers[STOP] = new StopComponentParser(appContext);
		parsers[INTERCEPT] = new InterceptComponentParser(appContext);
		parsers[EXCEPTION] = new OnExceptionComponentParser(appContext);
		parsers[TRY] = new TryComponentParser(appContext);
		parsers[PF] = new PipeLineComponentParser(appContext);
		parsers[WIRETAP] = new WireTapComponentParser(appContext);
		parsers[LOG] = new LogComponentParser(appContext);
	}
	
	private void parseRouteDefinitions(RouteDefinition rd,
			SpringCamelContext camelContext) throws UnsupportedElementException {
		NodeIdFactory nodeIdFactory = camelContext.getNodeIdFactory();
		List<FromDefinition> inputs = rd.getInputs();
		if (inputs.size() < 1) {
			throw new UnsupportedElementException(rd);
		}

		// process from
		FromDefinition fd = inputs.get(0);
		String uri = fd.getUri();
		assert uri != null;
		AbstractComponentParser fromParser = getDefinitionParser(uri);
		Map<String, String> map = fromParser.parse(nodeIdFactory, fd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(fromParser.getType(), map, NULL, null, null);

		// process end
		parseProcessorDefinition(rd.getOutputs(), nodeIdFactory, id, false);
	}

	private void parseProcessorDefinition(List<ProcessorDefinition> outputs,
			NodeIdFactory nodeIdFactory, String fromId, boolean keepFrom) {
		int connectionType = ROUTE;
		for (ProcessorDefinition pd : outputs) {
			if (pd instanceof InterceptDefinition) {
				parseInterceptDefinition(nodeIdFactory,
						(InterceptDefinition) pd);
			} else if (pd instanceof OnExceptionDefinition) {
				parseOnExceptionDefinition(nodeIdFactory,
						(OnExceptionDefinition) pd);
			} else {
				fromId = parseProcessorDefinition(nodeIdFactory, fromId,
						keepFrom, pd, connectionType, null);
				if (pd instanceof SplitDefinition
						|| pd instanceof LoadBalanceDefinition
						|| pd instanceof ChoiceDefinition
						|| pd instanceof AggregateDefinition
						|| pd instanceof FilterDefinition
						|| pd instanceof DynamicRouterDefinition
						|| pd instanceof RoutingSlipDefinition
						|| pd instanceof IdempotentConsumerDefinition
						|| pd instanceof LoopDefinition
						|| pd instanceof ThrottleDefinition) {
					connectionType = ROUTE_ENDBLOCK;
				}
			}
		}
	}

	private void parseOnExceptionDefinition(NodeIdFactory nodeIdFactory,
			OnExceptionDefinition pd) {
		// process from
		AbstractComponentParser parser = parsers[EXCEPTION];
		if (((OnExceptionComponentParser) parser).hasProcessed(pd)) {
			return;
		}
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(EXCEPTION, map, NULL, null, null);

		// process end
		parseProcessorDefinition(pd.getOutputs(), nodeIdFactory, id, false);
	}

	private void parseInterceptDefinition(NodeIdFactory nodeIdFactory,
			InterceptDefinition rd) {

		// process from
		AbstractComponentParser parser = parsers[INTERCEPT];
		if (((InterceptComponentParser) parser).hasProcessed(rd)) {
			return;
		}
		Map<String, String> map = parser.parse(nodeIdFactory, rd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(INTERCEPT, map, NULL, null, null);

		// process end
		parseProcessorDefinition(rd.getOutputs(), nodeIdFactory, id, false);
	}

	private String parseProcessorDefinition(NodeIdFactory nodeIdFactory,
			String fromId, boolean keepFrom, ProcessorDefinition pd,
			int connectionType,Map<String, String> connectionMap) {
		System.out.println(pd.getClass().getName());
		String id = null;
		if (pd instanceof ToDefinition) {
			id = parseToDefinition(nodeIdFactory, fromId, (ToDefinition) pd,
					connectionType,connectionMap);
		} else if (pd instanceof LogDefinition) {
			id = parseLogDefinition(nodeIdFactory, fromId, (LogDefinition) pd,
					connectionType,connectionMap);
		} else if (pd instanceof SplitDefinition) {
			id = parseSplitDefinition(nodeIdFactory, fromId,
					(SplitDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof BeanDefinition) {
			id = parseBeanDefinition(nodeIdFactory, fromId,
					(BeanDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof LoadBalanceDefinition) {
			id = parseLoadBalanceDefinition(nodeIdFactory, fromId,
					(LoadBalanceDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof FilterDefinition) {
			id = parseFilterDefinition(nodeIdFactory, fromId,
					(FilterDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof ChoiceDefinition) {
			id = parseMsgRouterDefinition(nodeIdFactory, fromId,
					(ChoiceDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof SetHeaderDefinition) {
			id = parseSetHeaderDefinition(nodeIdFactory, fromId,
					(SetHeaderDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof RoutingSlipDefinition) {
			id = parseRoutingSlipDefinition(nodeIdFactory, fromId,
					(RoutingSlipDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof SetBodyDefinition) {
			id = parseSetBodyDefinition(nodeIdFactory, fromId,
					(SetBodyDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof EnrichDefinition) {
			id = parseEnrichDefinition(nodeIdFactory, fromId,
					(EnrichDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof PollEnrichDefinition) {
			id = parsePollEnrichDefinition(nodeIdFactory, fromId,
					(PollEnrichDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof ConvertBodyDefinition) {
			id = parseConvertBodyDefinition(nodeIdFactory, fromId,
					(ConvertBodyDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof DelayDefinition) {
			id = parseDelayDefinition(nodeIdFactory, fromId,
					(DelayDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof AggregateDefinition) {
			id = parseAggregateDefinition(nodeIdFactory, fromId,
					(AggregateDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof ProcessDefinition) {
			id = parseProcessDefinition(nodeIdFactory, fromId,
					(ProcessDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof MulticastDefinition) {
			id = parseMulticastDefinition(nodeIdFactory, fromId,
					(MulticastDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof WireTapDefinition) {
			id = parseWireTapDefinition(nodeIdFactory, fromId,
					(WireTapDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof DynamicRouterDefinition) {
			id = parseDynamicDefinition(nodeIdFactory, fromId,
					(DynamicRouterDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof IdempotentConsumerDefinition) {
			id = parseIdempoDefinition(nodeIdFactory, fromId,
					(IdempotentConsumerDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof SetExchangePatternDefinition) {
			id = parseExchangePatternDefinition(nodeIdFactory, fromId,
					(SetExchangePatternDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof ThrottleDefinition) {
			id = parseThrottleDefinition(nodeIdFactory, fromId,
					(ThrottleDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof LoopDefinition) {
			id = parseLoopDefinition(nodeIdFactory, fromId,
					(LoopDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof StopDefinition) {
			id = parseStopDefinition(nodeIdFactory, fromId,
					(StopDefinition) pd, connectionType,connectionMap);
		} else if (pd instanceof TryDefinition) {
			id = parseTryDefinition(nodeIdFactory, fromId, (TryDefinition) pd,
					connectionType,connectionMap);
		} else if (pd instanceof PipelineDefinition){
			id = parsePipeDefinition(nodeIdFactory, fromId, (PipelineDefinition) pd,
					connectionType,connectionMap);
		}
		if (!keepFrom) {
			fromId = id;
		}
		return fromId;
	}

	private String parsePipeDefinition(NodeIdFactory nodeIdFactory,
			String fromId, PipelineDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[PF];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(PF, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parseTryDefinition(NodeIdFactory nodeIdFactory,
			String fromId, TryDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[TRY];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(TRY, map, connectionType, fromId, connectionMap);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		fromId = id;
		for (ProcessorDefinition out : outputs) {
			if (out instanceof CatchDefinition
					|| out instanceof FinallyDefinition) {
				continue;
			}
			if (fromId == id) {
				fromId = parseProcessorDefinition(nodeIdFactory, fromId, false,
						out, ROUTE_TRY, null);
			} else {
				fromId = parseProcessorDefinition(nodeIdFactory, fromId, false,
						out, ROUTE, null);
			}
		}
		List<CatchDefinition> catchClauses = pd.getCatchClauses();
		if (catchClauses != null) {
			for (CatchDefinition cd : catchClauses) {
				parseCatchDefinition(nodeIdFactory, id, cd,null);
			}
		}
		FinallyDefinition finallyClause = pd.getFinallyClause();
		if (finallyClause != null) {
			parseFinallyDefinition(nodeIdFactory, id, finallyClause,null);
		}
		return id;
	}

	private void parseFinallyDefinition(NodeIdFactory nodeIdFactory, String id,
			FinallyDefinition finallyClause,Map<String, String> connectionMap) {
		List<ProcessorDefinition> outputs = finallyClause.getOutputs();
		for (ProcessorDefinition pd : outputs) {
			parseProcessorDefinition(nodeIdFactory, id, true, pd, ROUTE_FINALLY,connectionMap);
		}
	}

	private void parseCatchDefinition(NodeIdFactory nodeIdFactory, String id,
			CatchDefinition cd,Map<String, String> connectionMap) {
		List<String> exceptions = cd.getExceptions();
		StringBuilder sb = new StringBuilder();
		if(exceptions!=null&&exceptions.size()>0){
			for(String e:exceptions){
				sb.append(e);
				sb.append(".class");
				sb.append(",");
			}
			if(sb.length()>1){
				sb.deleteCharAt(sb.length()-1);
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put(LB_EXCEPTIONS, sb.toString());
		List<ProcessorDefinition> outputs = cd.getOutputs();
		for (ProcessorDefinition pd : outputs) {
			parseProcessorDefinition(nodeIdFactory, id, true, pd, ROUTE_CATCH,map);
		}
	}

	private String parseStopDefinition(NodeIdFactory nodeIdFactory,
			String fromId, StopDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[STOP];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(STOP, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parseLoopDefinition(NodeIdFactory nodeIdFactory,
			String fromId, LoopDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[LOOP];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(LOOP, map, connectionType, fromId, connectionMap);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		parseProcessorDefinition(outputs, nodeIdFactory, id, true);
		return id;
	}

	private String parseThrottleDefinition(NodeIdFactory nodeIdFactory,
			String fromId, ThrottleDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[THROTTLER];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(THROTTLER, map, connectionType, fromId, connectionMap);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		parseProcessorDefinition(outputs, nodeIdFactory, id, true);
		return id;
	}

	private String parseExchangePatternDefinition(NodeIdFactory nodeIdFactory,
			String fromId, SetExchangePatternDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[PATTERN];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(PATTERN, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parseIdempoDefinition(NodeIdFactory nodeIdFactory,
			String fromId, IdempotentConsumerDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[IDEM];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(IDEM, map, connectionType, fromId, connectionMap);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		parseProcessorDefinition(outputs, nodeIdFactory, id, true);
		return id;
	}

	private String parseDynamicDefinition(NodeIdFactory nodeIdFactory,
			String fromId, DynamicRouterDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[DYNAMIC];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(DYNAMIC, map, connectionType, fromId, connectionMap);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		parseProcessorDefinition(outputs, nodeIdFactory, id, true);
		return id;
	}

	private String parseWireTapDefinition(NodeIdFactory nodeIdFactory,
			String fromId, WireTapDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[WIRETAP];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(WIRETAP, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parseMulticastDefinition(NodeIdFactory nodeIdFactory,
			String fromId, MulticastDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[MULTICAST];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(MULTICAST, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parseProcessDefinition(NodeIdFactory nodeIdFactory,
			String fromId, ProcessDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[PROCESSOR];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(PROCESSOR, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parseAggregateDefinition(NodeIdFactory nodeIdFactory,
			String fromId, AggregateDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[AGGREGATE];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(AGGREGATE, map, connectionType, fromId, connectionMap);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		parseProcessorDefinition(outputs, nodeIdFactory, id, true);
		return id;
	}

	private String parseDelayDefinition(NodeIdFactory nodeIdFactory,
			String fromId, DelayDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[DELAY];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(DELAY, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parseConvertBodyDefinition(NodeIdFactory nodeIdFactory,
			String fromId, ConvertBodyDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[CONVERT];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(CONVERT, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parsePollEnrichDefinition(NodeIdFactory nodeIdFactory,
			String fromId, PollEnrichDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[ENRICH];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(ENRICH, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parseEnrichDefinition(NodeIdFactory nodeIdFactory,
			String fromId, EnrichDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[ENRICH];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(ENRICH, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parseSetBodyDefinition(NodeIdFactory nodeIdFactory,
			String fromId, SetBodyDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[SETBODY];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(SETBODY, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parseRoutingSlipDefinition(NodeIdFactory nodeIdFactory,
			String fromId, RoutingSlipDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[ROUTINGSLIP];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(ROUTINGSLIP, map, connectionType, fromId, connectionMap);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		parseProcessorDefinition(outputs, nodeIdFactory, id, true);
		return id;
	}

	private String parseSetHeaderDefinition(NodeIdFactory nodeIdFactory,
			String fromId, SetHeaderDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[SETHEADER];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(SETHEADER, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parseMsgRouterDefinition(NodeIdFactory nodeIdFactory,
			String fromId, ChoiceDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[MSGROUTER];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(MSGROUTER, map, connectionType, fromId, connectionMap);
		List<WhenDefinition> whenClauses = pd.getWhenClauses();
		if (whenClauses != null)
			for (WhenDefinition wd : whenClauses) {
				parseWhenDefinition(nodeIdFactory, id, wd);
			}
		OtherwiseDefinition otherwise = pd.getOtherwise();
		if (otherwise != null)
			parseOtherwiseDefinition(nodeIdFactory, id, otherwise);
		return id;
	}

	private void parseOtherwiseDefinition(NodeIdFactory nodeIdFactory,
			String fromId, OtherwiseDefinition otherwise) {
		List<ProcessorDefinition> outputs = otherwise.getOutputs();
		for (ProcessorDefinition pd : outputs) {
			parseProcessorDefinition(nodeIdFactory, fromId, true, pd,
					ROUTE_OTHER,null);
		}
	}

	private void parseWhenDefinition(NodeIdFactory nodeIdFactory,
			String fromId, WhenDefinition wd) {
		ExpressionDefinition expression = wd.getExpression();
		Map<String, String> connectionMap = ExpressionProcessor.getExpressionMap(expression);
		List<ProcessorDefinition> outputs = wd.getOutputs();
		for (ProcessorDefinition pd : outputs) {
			parseProcessorDefinition(nodeIdFactory, fromId, true, pd,
					ROUTE_WHEN, connectionMap);
		}
	}

	private String parseFilterDefinition(NodeIdFactory nodeIdFactory,
			String fromId, FilterDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[FILTER];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(FILTER, map, connectionType, fromId, connectionMap);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		fromId = id;
		for (ProcessorDefinition out : outputs) {
			if(fromId==id){
				fromId = parseProcessorDefinition(nodeIdFactory, fromId, false,
						out, ROUTE, null);
			}else{
				fromId = parseProcessorDefinition(nodeIdFactory, fromId, false,
						out, ROUTE_ENDBLOCK, null);
			}
		}
		return id;
	}

	private String parseLoadBalanceDefinition(NodeIdFactory nodeIdFactory,
			String fromId, LoadBalanceDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[BALANCE];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(BALANCE, map, connectionType, fromId, connectionMap);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		parseProcessorDefinition(outputs, nodeIdFactory, id, true);
		return id;
	}

	private String parseSplitDefinition(NodeIdFactory nodeIdFactory,
			String fromId, SplitDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[SPLIT];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(SPLIT, map, connectionType, fromId, connectionMap);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		parseProcessorDefinition(outputs, nodeIdFactory, id, false);
		return id;
	}

	private String parseLogDefinition(NodeIdFactory nodeIdFactory,
			String fromId, LogDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[LOG];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(MSGENDPOINT, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parseBeanDefinition(NodeIdFactory nodeIdFactory,
			String fromId, BeanDefinition pd, int connectionType,Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[BEAN];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(BEAN, map, connectionType, fromId, connectionMap);
		return id;
	}

	private String parseToDefinition(NodeIdFactory nodeIdFactory,
			String fromId, ToDefinition pd, int connectionType,Map<String, String> connectionMap) {
		String uri = pd.getUri();
		assert uri != null;
		AbstractComponentParser componentParser = getDefinitionParser(uri);
		Map<String, String> map = componentParser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(componentParser.getType(), map, connectionType,
				fromId, connectionMap);
		return id;
	}

	private AbstractComponentParser getDefinitionParser(String uri) {
		if (uri.startsWith("file:")) {
			return parsers[FILE];
		} else if (uri.startsWith("cxf")) {
			return parsers[CXF];
		} else if (uri.startsWith("ftp") || uri.startsWith("ftps")
				|| uri.startsWith("sftp")) {
			return parsers[FTP];
		} else {
			int index = uri.indexOf(":");
			if (index != -1) {
				String schema = uri.substring(0, index);
				String beanClassName = appContext
						.getRegisterBeanClassName(schema);
				if (ActiveMQComponent.class.getName().equals(beanClassName)) {
					return new ActiveMQComponentParser(appContext, schema, uri);
				} else if (JmsComponent.class.getName().equals(beanClassName)) {
					return new JMSComponentParser(appContext, schema, uri);
				}
			}
			return parsers[MSGENDPOINT];
		}
	}

	public void addListener(ISpringParserListener l) {
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	public void removeListener(ISpringParserListener l) {
		if (listeners.contains(l)) {
			listeners.remove(l);
		}
	}

	protected void beforeProcessEvent() {
		for(AbstractComponentParser p:parsers){
			if(p!=null){
				p.initial();
			}
		}
		for (ISpringParserListener l : listeners) {
			l.preProcess();
		}
	}

	protected void fireProcessEvent(int componentType,
			Map<String, String> parameters, int connectionType,
			String sourceId, Map<String, String> connParameters) {
		for (ISpringParserListener l : listeners) {
			l.process(componentType, parameters, connectionType, sourceId,
					connParameters);
		}
	}

	protected void endProcessEvent() {
		for (ISpringParserListener l : listeners) {
			l.postProcess();
		}
		for(AbstractComponentParser p:parsers){
			if(p!=null){
				p.clear();
			}
		}
	}
}
