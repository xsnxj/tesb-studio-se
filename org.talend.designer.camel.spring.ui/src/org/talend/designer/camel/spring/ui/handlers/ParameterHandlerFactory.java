// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.spring.ui.handlers;

import java.util.HashMap;
import java.util.Map;

import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.camel.spring.ui.utils.RouteMapping;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public final class ParameterHandlerFactory {

    /**
     * 
     * DOC LiXP ParameterHandlerFactory class global comment. Detailled comment
     */
    static class CommonParameterHandler extends AbstractParameterHandler {

        public CommonParameterHandler(String componentName) {
            super(componentName);
        }
    }

    public static final ParameterHandlerFactory INSTANCE = new ParameterHandlerFactory();

    private ParameterHandlerFactory() {
        registerParamHandlers();
    }

    private Map<String, IParameterHandler> handlers;

    private void registerParamHandlers() {
        if (handlers == null) {
            handlers = new HashMap<String, IParameterHandler>();
        }

        String componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.FILE];
        handlers.put(componentName, new CFileParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.ACTIVEMQ];
        handlers.put(componentName, new CActiveMQParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.AGGREGATE];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.BALANCE];
        handlers.put(componentName, new CLoadBalancerParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.BEAN];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.CONVERT];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.CXF];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.DELAY];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.DYNAMIC];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.ENRICH];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.EXCEPTION];
        handlers.put(componentName, new COnExceptionrParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.FILTER];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.FTP];
        handlers.put(componentName, new CFtpParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.IDEM];
        handlers.put(componentName, new CIdempotentConsumerParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.INTERCEPT];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.JMS];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.LOOP];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.MSGENDPOINT];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.MULTICAST];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.MSGROUTER];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.PATTERN];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.PF];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.PROCESSOR];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.ROUTINGSLIP];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.SETBODY];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.SETHEADER];
        handlers.put(componentName, new CSetHeaderParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.SPLIT];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.STOP];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.THROTTLER];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.TRY];
        handlers.put(componentName, new CommonParameterHandler(componentName));

        componentName = RouteMapping.COMPOMENT_NAMES[ICamelSpringConstants.WIRETAP];
        handlers.put(componentName, new CommonParameterHandler(componentName));

    }

    public Map<String, IParameterHandler> getHandlers() {
        return handlers;
    }
}
