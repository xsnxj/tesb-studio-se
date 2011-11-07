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
package org.talend.designer.camel.spring.ui.exports;

import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class CJMSParameterExHandler extends AbstractExParameterHandler {

	public CJMSParameterExHandler(String component) {
		super(component);
	}

	@Override
	public void handleParameters(EList<?> elementParameterTypes,
			Map<String, String> parameters) {

		String NAME = computeTextElementValue("NAME", elementParameterTypes);
		parameters.put("jms_schema", NAME);
		String TYPE = computeTextElementValue("TYPE", elementParameterTypes);
		parameters.put("jms_type", TYPE);
		String DESTINATION = computeTextElementValue("DESTINATION",
				elementParameterTypes);
		parameters.put("jms_destination", DESTINATION);

		String mqType = computeTextElementValue("MQ_TYPE",
				elementParameterTypes);
		parameters.put(ICamelSpringConstants.JMS_BROKER_TYPE, mqType);

		if (ICamelSpringConstants.JMS_ACTIVEMQ_BROKER.equals(mqType)) {
			String brokerURI = computeTextElementValue("AMQ_BROKER_URI",
					elementParameterTypes);
			parameters.put("brokerURL", unquotes(brokerURI));
		} else if (ICamelSpringConstants.JMS_WMQ_BROKER.equals(mqType)) {
			String WQM_SEVER = computeTextElementValue("WQM_SEVER",
					elementParameterTypes);
			parameters.put("hostname", unquotes(WQM_SEVER));
			String WMQ_PORT = computeTextElementValue("WMQ_PORT",
					elementParameterTypes);
			parameters.put("port", unquotes(WMQ_PORT));
			String WMQ_TRANSPORT_TYPE = computeTextElementValue(
					"WMQ_TRANSPORT_TYPE", elementParameterTypes);
			parameters.put("transportType", unquotes(WMQ_TRANSPORT_TYPE));
			String WMQ_QUEUE_MANAGER = computeTextElementValue(
					"WMQ_QUEUE_MANAGER", elementParameterTypes);
			parameters.put("queueManager", unquotes(WMQ_QUEUE_MANAGER));
		}

	}

}
