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
import org.apache.camel.model.OptionalIdentifiedDefinition;
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
import org.talend.designer.camel.spring.core.intl.SpringNodeIdFactory;
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

	private AbstractComponentParser[] parsers = new AbstractComponentParser[LENGTH];

	private XmlFileApplicationContext appContext;

	/*
	 * this used to store all component classes who has end_block connection
	 * type
	 */
	private List<Class<?>> endBlockComponentTypes = new ArrayList<Class<?>>();

	public void startParse(String filePath) throws Exception {
		try {
			// read file
			String path = filePath;

			// Spring Context loader bug, cannot find file in Linux and Mac.
			// Must add a "file:" prefix. Added By LiXP.
			if (filePath != null && !filePath.contains(":")) {
				path = "file:" + filePath;
			}

			appContext = new XmlFileApplicationContext(path);
			SpringCamelContext camelContext = SpringCamelContext
					.springCamelContext(appContext);

			// initial parsers and endBlock types
			initialize();

			// fire ready to parse
			beforeProcessEvent();

			// parse
			List<RouteDefinition> routeDefinitions = camelContext
					.getRouteDefinitions();
			NodeIdFactory nodeIdFactory = new SpringNodeIdFactory();
			for (RouteDefinition rd : routeDefinitions) {
				parseRouteDefinitions(rd, camelContext, nodeIdFactory);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			// end of parse
			endProcessEvent();
			if (appContext != null) {
				appContext.destroy();
				appContext = null;
			}
		}
	}

	private void initialize() {
		// initial parser
		parsers[FILE] = new FileComponentParser(appContext);
		parsers[FTP] = new FTPComponentParser(appContext);
		parsers[ACTIVEMQ] = new ActiveMQComponentParser(appContext);
		parsers[JMS] = new JMSComponentParser(appContext);
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

		// initial end_block
		endBlockComponentTypes.add(SplitDefinition.class);
		endBlockComponentTypes.add(LoadBalanceDefinition.class);
		endBlockComponentTypes.add(ChoiceDefinition.class);
		endBlockComponentTypes.add(AggregateDefinition.class);
		endBlockComponentTypes.add(FilterDefinition.class);
		endBlockComponentTypes.add(TryDefinition.class);
		// endBlockComponentTypes.add(DynamicRouterDefinition.class);
		// endBlockComponentTypes.add(RoutingSlipDefinition.class);
		endBlockComponentTypes.add(IdempotentConsumerDefinition.class);
		endBlockComponentTypes.add(LoopDefinition.class);
		endBlockComponentTypes.add(ThrottleDefinition.class);
	}

	/**
	 * parse a route
	 * 
	 * @param rd
	 * @param camelContext
	 * @throws UnsupportedElementException
	 */
	private void parseRouteDefinitions(RouteDefinition rd,
			SpringCamelContext camelContext, NodeIdFactory nodeIdFactory)
			throws UnsupportedElementException {
		List<FromDefinition> inputs = rd.getInputs();
		if (inputs.size() < 1) {
			throw new UnsupportedElementException(rd);
		}

		// process from
		String id = parseFromDefinition(nodeIdFactory, inputs);

		// process end
		parseProcessorDefinition(rd.getOutputs(), nodeIdFactory, id, false,
				ROUTE);
	}

	private String parseFromDefinition(NodeIdFactory nodeIdFactory,
			List<FromDefinition> inputs) {
		FromDefinition fd = inputs.get(0);
		String uri = fd.getUri();
		String id = processIsolateDefinition(getDefinitionParser(uri),
				nodeIdFactory, null, fd, NULL_ROUTE, null);
		return id;
	}

	/**
	 * parser a route but from node
	 * 
	 * @param outputs
	 * @param nodeIdFactory
	 * @param fromId
	 * @param keepFrom
	 */
	private void parseProcessorDefinition(List<ProcessorDefinition> outputs,
			NodeIdFactory nodeIdFactory, String fromId, boolean keepFrom,
			int connectionType) {
		parseProcessorDefinition(outputs, nodeIdFactory, fromId, keepFrom,
				connectionType, null);
	}

	private void parseProcessorDefinition(List<ProcessorDefinition> outputs,
			NodeIdFactory nodeIdFactory, String fromId, boolean keepFrom,
			int connectionType, Map<String, String> connectionMap) {
		for (ProcessorDefinition pd : outputs) {
			/*
			 * the intercept component and onException component should be
			 * processed seperately
			 */
			if (pd instanceof InterceptDefinition) {
				parseInterceptDefinition(nodeIdFactory,
						(InterceptDefinition) pd);
			} else if (pd instanceof OnExceptionDefinition) {
				parseOnExceptionDefinition(nodeIdFactory,
						(OnExceptionDefinition) pd);
			}
			// process other components
			else {
				fromId = parseProcessorDefinition(nodeIdFactory, fromId,
						keepFrom, pd, connectionType, connectionMap);
				if (!keepFrom) {
					connectionType = getFollowedConnectionType(pd.getClass());
				}
			}
		}
	}

	private int getFollowedConnectionType(Class componentClass) {
		int connectionType = ROUTE;
		for (Class c : endBlockComponentTypes) {
			if (c == componentClass) {
				connectionType = ROUTE_ENDBLOCK;
				break;
			}
		}
		return connectionType;
	}

	private void parseOnExceptionDefinition(NodeIdFactory nodeIdFactory,
			OnExceptionDefinition pd) {
		// skipped if it has been parsed
		AbstractComponentParser parser = parsers[EXCEPTION];
		if (((OnExceptionComponentParser) parser).hasProcessed(pd)) {
			return;
		}
		processCascadeDefinition(EXCEPTION, nodeIdFactory, null, pd,
				NULL_ROUTE, null, false);
	}

	private void parseInterceptDefinition(NodeIdFactory nodeIdFactory,
			InterceptDefinition rd) {
		// skipped if it has been parsed
		AbstractComponentParser parser = parsers[INTERCEPT];
		if (((InterceptComponentParser) parser).hasProcessed(rd)) {
			return;
		}
		String id = processIsolateDefinition(INTERCEPT, nodeIdFactory, null,
				rd, NULL_ROUTE, null);
		List<ProcessorDefinition> outputs = rd.getOutputs();
		if (outputs.size() > 0) {
			ProcessorDefinition firstOutput = outputs.get(0);
			if (firstOutput == null) {
				return;
			}
			if (!(firstOutput instanceof WhenDefinition)) {
				parseProcessorDefinition(outputs, nodeIdFactory, id, false,
						ROUTE);
			} else {
				WhenDefinition wd = (WhenDefinition) firstOutput;
				ExpressionDefinition expression = wd.getExpression();
				Map<String, String> connectionMap = ExpressionProcessor
						.getExpressionMap(expression);
				parseProcessorDefinition(wd.getOutputs(), nodeIdFactory, id,
						false, ROUTE_WHEN, connectionMap);
			}
		}
	}

	private String parseProcessorDefinition(NodeIdFactory nodeIdFactory,
			String fromId, boolean keepFrom, ProcessorDefinition pd,
			int connectionType, Map<String, String> connectionMap) {
		String id = null;
		if (pd instanceof ToDefinition) {
			id = parseToDefinition(nodeIdFactory, fromId, (ToDefinition) pd,
					connectionType, connectionMap);
		} else if (pd instanceof LogDefinition) {
			id = processIsolateDefinition(LOG, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else if (pd instanceof SplitDefinition) {
			id = processCascadeDefinition(SPLIT, nodeIdFactory, fromId, pd,
					connectionType, connectionMap, false);
		} else if (pd instanceof BeanDefinition) {
			id = processIsolateDefinition(BEAN, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else if (pd instanceof LoadBalanceDefinition) {
			id = processCascadeDefinition(BALANCE, nodeIdFactory, fromId, pd,
					connectionType, connectionMap, true);
		} else if (pd instanceof FilterDefinition) {
			id = parseFilterDefinition(nodeIdFactory, fromId,
					(FilterDefinition) pd, connectionType, connectionMap);
		} else if (pd instanceof ChoiceDefinition) {
			id = parseMsgRouterDefinition(nodeIdFactory, fromId,
					(ChoiceDefinition) pd, connectionType, connectionMap);
		} else if (pd instanceof SetHeaderDefinition) {
			id = processIsolateDefinition(SETHEADER, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else if (pd instanceof RoutingSlipDefinition) {
			id = processCascadeDefinition(ROUTINGSLIP, nodeIdFactory, fromId,
					pd, connectionType, connectionMap, false);
		} else if (pd instanceof SetBodyDefinition) {
			id = processIsolateDefinition(SETBODY, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else if (pd instanceof EnrichDefinition) {
			id = processIsolateDefinition(ENRICH, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else if (pd instanceof PollEnrichDefinition) {
			id = processIsolateDefinition(ENRICH, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else if (pd instanceof ConvertBodyDefinition) {
			id = processIsolateDefinition(CONVERT, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else if (pd instanceof DelayDefinition) {
			id = processIsolateDefinition(DELAY, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else if (pd instanceof AggregateDefinition) {
			id = processCascadeDefinition(AGGREGATE, nodeIdFactory, fromId, pd,
					connectionType, connectionMap, false);
		} else if (pd instanceof ProcessDefinition) {
			id = processIsolateDefinition(PROCESSOR, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else if (pd instanceof MulticastDefinition) {
			id = processIsolateDefinition(MULTICAST, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else if (pd instanceof WireTapDefinition) {
			id = processIsolateDefinition(WIRETAP, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else if (pd instanceof DynamicRouterDefinition) {
			id = processCascadeDefinition(DYNAMIC, nodeIdFactory, fromId, pd,
					connectionType, connectionMap, false);
		} else if (pd instanceof IdempotentConsumerDefinition) {
			id = processCascadeDefinition(IDEM, nodeIdFactory, fromId, pd,
					connectionType, connectionMap, false);
		} else if (pd instanceof SetExchangePatternDefinition) {
			id = processIsolateDefinition(PATTERN, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else if (pd instanceof ThrottleDefinition) {
			id = processCascadeDefinition(THROTTLER, nodeIdFactory, fromId, pd,
					connectionType, connectionMap, false);
		} else if (pd instanceof LoopDefinition) {
			id = processCascadeDefinition(LOOP, nodeIdFactory, fromId, pd,
					connectionType, connectionMap, false);
		} else if (pd instanceof StopDefinition) {
			id = processIsolateDefinition(STOP, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else if (pd instanceof TryDefinition) {
			id = parseTryDefinition(nodeIdFactory, fromId, (TryDefinition) pd,
					connectionType, connectionMap);
		} else if (pd instanceof PipelineDefinition) {
			id = processIsolateDefinition(PF, nodeIdFactory, fromId, pd,
					connectionType, connectionMap);
		} else {
			id = processIsolateDefinition(MSGENDPOINT, nodeIdFactory, fromId,
					pd, connectionType, connectionMap);
		}
		if (!keepFrom) {
			fromId = id;
		}
		return fromId;
	}

	/**
	 * common parse method, used to process isolate node which has only one
	 * output
	 * 
	 * @param componentType
	 * @param nodeIdFactory
	 * @param fromId
	 * @param pd
	 * @param connectionType
	 * @param connectionMap
	 * @return
	 */
	private String processIsolateDefinition(int componentType,
			NodeIdFactory nodeIdFactory, String fromId,
			OptionalIdentifiedDefinition pd, int connectionType,
			Map<String, String> connectionMap) {
		AbstractComponentParser parser = parsers[componentType];
		Map<String, String> map = parser.parse(nodeIdFactory, pd);
		String id = map.get(UNIQUE_NAME_ID);
		fireProcessEvent(parser.getType(), map, connectionType, fromId,
				connectionMap);
		return id;
	}

	/**
	 * common parse method, used to process cascade node which may have multi
	 * output
	 * 
	 * @param componentType
	 * @param nodeIdFactory
	 * @param fromId
	 * @param pd
	 * @param connectionType
	 * @param connectionMap
	 * @param keepFrom
	 * @return
	 */
	private String processCascadeDefinition(int componentType,
			NodeIdFactory nodeIdFactory, String fromId, ProcessorDefinition pd,
			int connectionType, Map<String, String> connectionMap,
			boolean keepFrom) {
		String id = processIsolateDefinition(componentType, nodeIdFactory,
				fromId, pd, connectionType, connectionMap);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		parseProcessorDefinition(outputs, nodeIdFactory, id, keepFrom, ROUTE);
		return id;
	}

	private String parseTryDefinition(NodeIdFactory nodeIdFactory,
			String fromId, TryDefinition pd, int connectionType,
			Map<String, String> connectionMap) {
		String id = processIsolateDefinition(TRY, nodeIdFactory, fromId, pd,
				connectionType, connectionMap);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		List<CatchDefinition> catchClauses = pd.getCatchClauses();
		FinallyDefinition finallyClause = pd.getFinallyClause();
		fromId = id;
		outputs.removeAll(catchClauses);
		outputs.remove(finallyClause);
		parseProcessorDefinition(outputs, nodeIdFactory, fromId, false,
				ROUTE_TRY);
		if (catchClauses != null) {
			for (CatchDefinition cd : catchClauses) {
				parseCatchDefinition(nodeIdFactory, id, cd, null);
			}
		}
		if (finallyClause != null) {
			parseFinallyDefinition(nodeIdFactory, id, finallyClause, null);
		}
		return id;
	}

	private void parseFinallyDefinition(NodeIdFactory nodeIdFactory, String id,
			FinallyDefinition finallyClause, Map<String, String> connectionMap) {
		List<ProcessorDefinition> outputs = finallyClause.getOutputs();
		int size = outputs.size();

		String fromId = id;
		int connectionType = ROUTE;
		for (int i = 0; i < size; i++) {
			ProcessorDefinition pd = outputs.get(i);
			if (i == 0) {
				fromId = parseProcessorDefinition(nodeIdFactory, fromId, false,
						pd, ROUTE_FINALLY, connectionMap);
			} else {
				fromId = parseProcessorDefinition(nodeIdFactory, fromId, false,
						pd, connectionType, null);
			}
			connectionType = getFollowedConnectionType(pd.getClass());
		}
	}

	private void parseCatchDefinition(NodeIdFactory nodeIdFactory, String id,
			CatchDefinition cd, Map<String, String> connectionMap) {
		List<String> exceptions = cd.getExceptions();
		StringBuilder sb = new StringBuilder();
		if (exceptions != null && exceptions.size() > 0) {
			for (String e : exceptions) {
				sb.append(e);
				sb.append(".class");
				sb.append(",");
			}
			if (sb.length() > 1) {
				sb.deleteCharAt(sb.length() - 1);
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put(LB_EXCEPTIONS, sb.toString());
		List<ProcessorDefinition> outputs = cd.getOutputs();
		String fromId = id;
		int connectionType = ROUTE;
		for (ProcessorDefinition pd : outputs) {
			if (fromId == id) {
				fromId = parseProcessorDefinition(nodeIdFactory, fromId, false,
						pd, ROUTE_CATCH, map);
			} else {
				fromId = parseProcessorDefinition(nodeIdFactory, fromId, false,
						pd, connectionType, null);
			}
			connectionType = getFollowedConnectionType(pd.getClass());
		}
	}

	private String parseMsgRouterDefinition(NodeIdFactory nodeIdFactory,
			String fromId, ChoiceDefinition pd, int connectionType,
			Map<String, String> connectionMap) {
		String id = processIsolateDefinition(MSGROUTER, nodeIdFactory, fromId,
				pd, connectionType, connectionMap);
		List<WhenDefinition> whenClauses = pd.getWhenClauses();
		if (whenClauses != null)
			for (WhenDefinition wd : whenClauses) {
				parseWhenDefinition(nodeIdFactory, id, wd);
			}
		OtherwiseDefinition otherwise = pd.getOtherwise();
		if (otherwise != null)
			parseOtherwiseDefinition(nodeIdFactory, id, otherwise);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		outputs.removeAll(whenClauses);
		outputs.removeAll(outputs);
		parseProcessorDefinition(outputs, nodeIdFactory, fromId, false,
				ROUTE_ENDBLOCK);
		return id;
	}

	private void parseOtherwiseDefinition(NodeIdFactory nodeIdFactory,
			String fromId, OtherwiseDefinition otherwise) {
		List<ProcessorDefinition> outputs = otherwise.getOutputs();
		parseProcessorDefinition(outputs, nodeIdFactory, fromId, false,
				ROUTE_OTHER);
	}

	private void parseWhenDefinition(NodeIdFactory nodeIdFactory,
			String fromId, WhenDefinition wd) {
		ExpressionDefinition expression = wd.getExpression();
		Map<String, String> connectionMap = ExpressionProcessor
				.getExpressionMap(expression);
		List<ProcessorDefinition> outputs = wd.getOutputs();
		parseProcessorDefinition(outputs, nodeIdFactory, fromId, false,
				ROUTE_WHEN, connectionMap);
	}

	private String parseFilterDefinition(NodeIdFactory nodeIdFactory,
			String fromId, FilterDefinition pd, int connectionType,
			Map<String, String> connectionMap) {
		String id = processIsolateDefinition(FILTER, nodeIdFactory, fromId, pd,
				connectionType, connectionMap);
		List<ProcessorDefinition> outputs = pd.getOutputs();
		parseProcessorDefinition(outputs, nodeIdFactory, id, false, ROUTE);
		return id;
	}

	private String parseToDefinition(NodeIdFactory nodeIdFactory,
			String fromId, ToDefinition pd, int connectionType,
			Map<String, String> connectionMap) {
		String uri = pd.getUri();
		String id = processIsolateDefinition(getDefinitionParser(uri),
				nodeIdFactory, fromId, pd, connectionType, connectionMap);
		return id;
	}

	/**
	 * For ToDefinition or FromDifinition return different component type
	 * according to the schema
	 * 
	 * @param uri
	 * @return componentType
	 */
	private int getDefinitionParser(String uri) {
		if (uri == null) {
			return MSGENDPOINT;
		}
		if (uri.startsWith("file:")) {
			return FILE;
		} else if (uri.startsWith("cxf")) {
			return CXF;
		} else if (uri.startsWith("ftp") || uri.startsWith("ftps")
				|| uri.startsWith("sftp")) {
			return FTP;
		} else {
			int index = uri.indexOf(":");
			if (index != -1) {
				String schema = uri.substring(0, index);
				String beanClassName = appContext
						.getRegisterBeanClassName(schema);
				if (ActiveMQComponent.class.getName().equals(beanClassName)) {
					return ACTIVEMQ;
				} else if (JmsComponent.class.getName().equals(beanClassName)) {
					return JMS;
				}
			}
			return MSGENDPOINT;
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

	/**
	 * invoke before the first parser
	 */
	protected void beforeProcessEvent() {
		for (AbstractComponentParser p : parsers) {
			if (p != null) {
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

	/**
	 * invoke at the end of last parser
	 */
	protected void endProcessEvent() {
		for (ISpringParserListener l : listeners) {
			l.postProcess();
		}
		for (AbstractComponentParser p : parsers) {
			if (p != null) {
				p.clear();
				p = null;
			}
		}
	}
}
