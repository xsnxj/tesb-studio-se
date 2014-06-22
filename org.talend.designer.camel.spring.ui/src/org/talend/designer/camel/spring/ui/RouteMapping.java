// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.spring.ui;

import java.util.HashMap;
import java.util.Map;

import org.talend.core.model.process.EConnectionType;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public final class RouteMapping {

	public static final String[] COMPOMENT_NAMES = new String[ICamelSpringConstants.LENGTH];

	/**
	 * Connection style mapping
	 * 
	 * key - {@code ICamelSpringConstants#ROUTE}
	 * 
	 * value - {@code EConnectionType#ROUTE}
	 */
	private static Map<Integer, EConnectionType> connectionStyleMap;

	/**
	 * Component name mapping
	 * 
	 * key - {@code ICamelSpringConstants#ACTIVEMQ}
	 * 
	 * value - "cActiveMQ"
	 */
	private static Map<Integer, String> componentNameMap;

	private RouteMapping() {
	}

	static {
		connectionStyleMap = new HashMap<Integer, EConnectionType>();
		componentNameMap = new HashMap<Integer, String>();

		connectionStyleMap.put(ICamelSpringConstants.ROUTE,
				EConnectionType.ROUTE);
		connectionStyleMap.put(ICamelSpringConstants.ROUTE_CATCH,
				EConnectionType.ROUTE_CATCH);
		connectionStyleMap.put(ICamelSpringConstants.ROUTE_ENDBLOCK,
				EConnectionType.ROUTE_ENDBLOCK);
		connectionStyleMap.put(ICamelSpringConstants.ROUTE_FINALLY,
				EConnectionType.ROUTE_FINALLY);
		connectionStyleMap.put(ICamelSpringConstants.ROUTE_OTHER,
				EConnectionType.ROUTE_OTHER);
		connectionStyleMap.put(ICamelSpringConstants.ROUTE_WHEN,
				EConnectionType.ROUTE_WHEN);
		connectionStyleMap.put(ICamelSpringConstants.ROUTE_TRY,
				EConnectionType.ROUTE_TRY);

		componentNameMap.put(ICamelSpringConstants.RECIPIENT, "cRecipientList");
		componentNameMap.put(ICamelSpringConstants.AGGREGATE, "cAggregate");
		componentNameMap.put(ICamelSpringConstants.BALANCE, "cLoadBalancer");
		componentNameMap.put(ICamelSpringConstants.BEAN, "cBean");
		componentNameMap.put(ICamelSpringConstants.CONVERT, "cConvertBodyTo");
		componentNameMap.put(ICamelSpringConstants.CXF, "cCXF");
		componentNameMap.put(ICamelSpringConstants.DELAY, "cDelayer");
		componentNameMap.put(ICamelSpringConstants.DYNAMIC, "cDynamicRouter");
		componentNameMap.put(ICamelSpringConstants.ENRICH, "cContentEnricher");
		componentNameMap.put(ICamelSpringConstants.EXCEPTION, "cOnException");
		componentNameMap.put(ICamelSpringConstants.FILE, "cFile");
		componentNameMap.put(ICamelSpringConstants.FILTER, "cMessageFilter");
		componentNameMap.put(ICamelSpringConstants.FTP, "cFtp");
		componentNameMap.put(ICamelSpringConstants.JMS, "cJMS");
		componentNameMap.put(ICamelSpringConstants.IDEM, "cIdempotentConsumer");
		componentNameMap.put(ICamelSpringConstants.INTERCEPT, "cIntercept");
		componentNameMap.put(ICamelSpringConstants.LOOP, "cLoop");
		componentNameMap.put(ICamelSpringConstants.MSGENDPOINT,
				"cMessagingEndpoint");
		componentNameMap.put(ICamelSpringConstants.MSGROUTER, "cMessageRouter");
		componentNameMap.put(ICamelSpringConstants.MULTICAST, "cMulticast");
		componentNameMap.put(ICamelSpringConstants.ROUTINGSLIP, "cRoutingSlip");
		componentNameMap.put(ICamelSpringConstants.PATTERN, "cExchangePattern");
		componentNameMap.put(ICamelSpringConstants.PF, "cPipesAndFilters");
		componentNameMap.put(ICamelSpringConstants.PROCESSOR, "cProcessor");
		componentNameMap.put(ICamelSpringConstants.SETBODY, "cSetBody");
		componentNameMap.put(ICamelSpringConstants.SETHEADER, "cSetHeader");
		componentNameMap.put(ICamelSpringConstants.SPLIT, "cSplitter");
		componentNameMap.put(ICamelSpringConstants.STOP, "cStop");
		componentNameMap.put(ICamelSpringConstants.THROTTLER, "cThrottler");
		componentNameMap.put(ICamelSpringConstants.TRY, "cTry");
		componentNameMap.put(ICamelSpringConstants.WIRETAP, "cWireTap");
		componentNameMap.put(ICamelSpringConstants.UNKNOWN,
				"cMessagingEndpoint");

		// http://jira.talendforge.org/browse/TESB-3817
		COMPOMENT_NAMES[ICamelSpringConstants.RECIPIENT] = "cRecipientList";
		COMPOMENT_NAMES[ICamelSpringConstants.AGGREGATE] = "cAggregate";
		COMPOMENT_NAMES[ICamelSpringConstants.BALANCE] = "cLoadBalancer";
		COMPOMENT_NAMES[ICamelSpringConstants.BEAN] = "cBean";
		COMPOMENT_NAMES[ICamelSpringConstants.CONVERT] = "cConvertBodyTo";
		COMPOMENT_NAMES[ICamelSpringConstants.CXF] = "cCXF";
		COMPOMENT_NAMES[ICamelSpringConstants.DELAY] = "cDelayer";
		COMPOMENT_NAMES[ICamelSpringConstants.DYNAMIC] = "cDynamicRouter";
		COMPOMENT_NAMES[ICamelSpringConstants.ENRICH] = "cContentEnricher";
		COMPOMENT_NAMES[ICamelSpringConstants.EXCEPTION] = "cOnException";
		COMPOMENT_NAMES[ICamelSpringConstants.FILE] = "cFile";
		COMPOMENT_NAMES[ICamelSpringConstants.FILTER] = "cMessageFilter";
		COMPOMENT_NAMES[ICamelSpringConstants.FTP] = "cFtp";
		COMPOMENT_NAMES[ICamelSpringConstants.JMS] = "cJMS";
		COMPOMENT_NAMES[ICamelSpringConstants.IDEM] = "cIdempotentConsumer";
		COMPOMENT_NAMES[ICamelSpringConstants.INTERCEPT] = "cIntercept";
		COMPOMENT_NAMES[ICamelSpringConstants.LOOP] = "cLoop";
		COMPOMENT_NAMES[ICamelSpringConstants.MSGENDPOINT] = "cMessagingEndpoint";
		COMPOMENT_NAMES[ICamelSpringConstants.MSGROUTER] = "cMessageRouter";
		COMPOMENT_NAMES[ICamelSpringConstants.MULTICAST] = "cMulticast";
		COMPOMENT_NAMES[ICamelSpringConstants.ROUTINGSLIP] = "cRoutingSlip";
		COMPOMENT_NAMES[ICamelSpringConstants.PATTERN] = "cExchangePattern";
		COMPOMENT_NAMES[ICamelSpringConstants.PF] = "cPipesAndFilters";
		COMPOMENT_NAMES[ICamelSpringConstants.PROCESSOR] = "cProcessor";
		COMPOMENT_NAMES[ICamelSpringConstants.SETBODY] = "cSetBody";
		COMPOMENT_NAMES[ICamelSpringConstants.SETHEADER] = "cSetHeader";
		COMPOMENT_NAMES[ICamelSpringConstants.SPLIT] = "cSplitter";
		COMPOMENT_NAMES[ICamelSpringConstants.STOP] = "cStop";
		COMPOMENT_NAMES[ICamelSpringConstants.THROTTLER] = "cThrottler";
		COMPOMENT_NAMES[ICamelSpringConstants.TRY] = "cTry";
		COMPOMENT_NAMES[ICamelSpringConstants.WIRETAP] = "cWireTap";
		COMPOMENT_NAMES[ICamelSpringConstants.LOG] = "cMessagingEndpoint";
		COMPOMENT_NAMES[ICamelSpringConstants.UNKNOWN] = "cMessagingEndpoint";

		COMPOMENT_NAMES[ICamelSpringConstants.WHEN] = "when";
		COMPOMENT_NAMES[ICamelSpringConstants.CATCH] = "catch";
		COMPOMENT_NAMES[ICamelSpringConstants.OTHER] = "other";
		COMPOMENT_NAMES[ICamelSpringConstants.FINALLY] = "finally";
	}

	/**
	 * 
	 * DOC LiXP Comment method "getConnectionMapping".
	 * 
	 * @return
	 */
	public static Map<Integer, EConnectionType> getConnectionMapping() {
		return connectionStyleMap;
	}

	/**
	 * 
	 * DOC LiXP Comment method "getComponentMapping".
	 * 
	 * @return
	 */
	public static Map<Integer, String> getComponentMapping() {
		return componentNameMap;
	}
}
