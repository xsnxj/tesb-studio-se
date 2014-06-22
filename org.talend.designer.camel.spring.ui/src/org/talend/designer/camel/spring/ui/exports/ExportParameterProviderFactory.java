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
package org.talend.designer.camel.spring.ui.exports;

import java.util.HashMap;
import java.util.Map;

import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.camel.spring.ui.RouteMapping;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class ExportParameterProviderFactory {

	static class CommonParameterProvider extends AbstractExParameterHandler {
		public CommonParameterProvider(String component) {
			super(component);
		}
	}

	public static ExportParameterProviderFactory ISNTANCE = new ExportParameterProviderFactory();

	private Map<String, IExportParameterHandler> exportHandlers;

	private ExportParameterProviderFactory() {
		init();
	}

	private void init() {
		exportHandlers = new HashMap<String, IExportParameterHandler>();

		String componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.FILE];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		// http://jira.talendforge.org/browse/TESB-3817
		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.RECIPIENT];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.AGGREGATE];
		exportHandlers.put(componentName, new CAggregateParameterExHandler(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.BALANCE];
		exportHandlers.put(componentName, new CLoadBalancerExParameterHandler(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.BEAN];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.CONVERT];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.CXF];
		exportHandlers.put(componentName, new CCXFExParameterHandler(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.DELAY];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.DYNAMIC];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.ENRICH];
		exportHandlers.put(componentName,
				new CContentEnricherExParameterHandler(componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.EXCEPTION];
		exportHandlers.put(componentName, new COnExceptionExParameterHandler(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.FILTER];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.FTP];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.IDEM];
		exportHandlers.put(componentName,
				new CIdempotentConsumerExParameterHandler(componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.INTERCEPT];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.JMS];
		exportHandlers.put(componentName, new CJMSParameterExHandler(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.LOOP];
		exportHandlers.put(componentName, new CLoopExParameterHandler(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.MSGENDPOINT];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.MULTICAST];
		exportHandlers.put(componentName, new CMutilcastExParameterHandler(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.MSGROUTER];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.PATTERN];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.PF];
		exportHandlers.put(componentName,
				new CPipesAndFiltersExParameterHandler(componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.PROCESSOR];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.ROUTINGSLIP];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.SETBODY];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.SETHEADER];
		exportHandlers.put(componentName, new CSetHeaderExParameterHandler(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.SPLIT];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.STOP];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.THROTTLER];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.TRY];
		exportHandlers.put(componentName, new CommonParameterProvider(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.WIRETAP];
		exportHandlers.put(componentName, new CWiredTapExParameterHandler(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.WHEN];
		exportHandlers.put(componentName, new CWhenExParameterHandler(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.OTHER];
		exportHandlers.put(componentName, new COtherExParameterHandler(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.CATCH];
		exportHandlers.put(componentName, new CCatchExParameterHandler(
				componentName));

		componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.FINALLY];
		exportHandlers.put(componentName, new CFinallyParameterExHandler(
				componentName));
	}

	/**
	 * 
	 * DOC LiXP Comment method "getExParameterHandlers".
	 * 
	 * @return
	 */
	public Map<String, IExportParameterHandler> getExParameterHandlers() {
		return exportHandlers;
	}
}
