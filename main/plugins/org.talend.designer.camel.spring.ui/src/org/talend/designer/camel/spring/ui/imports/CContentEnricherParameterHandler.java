// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.spring.ui.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class CContentEnricherParameterHandler extends AbstractParameterHandler {

    public CContentEnricherParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {
        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();

        String uri = parameters.get(ICamelSpringConstants.ER_RESOUCE_URI);
        String strategy = parameters.get(ICamelSpringConstants.ER_AGGREGATE_STRATEGY);
        String mergeData = parameters.get(ICamelSpringConstants.ER_MERGE_DATA);
        String timeoutStyle = parameters.get(ICamelSpringConstants.ER_TIMEOUT_STYLE);
        String waitTimeout = parameters.get(ICamelSpringConstants.ER_WAIT_TIMEOUT);

        mergeData = unquotes(mergeData);

        addParamType(elemParams, FIELD_TEXT, "UNIQUE_NAME", uniqueName);
        addParamType(elemParams, FIELD_TEXT, "RESOURCE_URI", uri == null ? "" : uri);
        addParamType(elemParams, FIELD_RADIO, "ENRICH", ICamelSpringConstants.ER_PRODUCER.equals(mergeData) ? VALUE_TRUE
                : VALUE_FALSE);
        addParamType(elemParams, FIELD_RADIO, "POLLENRICH", ICamelSpringConstants.ER_CONSUMER.equals(mergeData) ? VALUE_TRUE
                : VALUE_FALSE);

        if (ICamelSpringConstants.ER_CONSUMER.equals(mergeData)) {
            // Consumer
            if (timeoutStyle != null) {
                timeoutStyle = unquotes(timeoutStyle);
                addParamType(elemParams, FIELD_CHECK, "SPECIFY_TIMEOUT", VALUE_TRUE);
                addParamType(elemParams, FIELD_RADIO, "IMMEDIATE",
                        ICamelSpringConstants.ER_POLL_IMMED.equals(timeoutStyle) ? VALUE_TRUE : VALUE_FALSE);
                addParamType(elemParams, FIELD_RADIO, "WAIT",
                        ICamelSpringConstants.ER_WAIT_UNTIL.equals(timeoutStyle) ? VALUE_TRUE : VALUE_FALSE);
                addParamType(elemParams, FIELD_RADIO, "TRIGGER",
                        ICamelSpringConstants.ER_WAIT_TIMEOUT.equals(timeoutStyle) ? VALUE_TRUE : VALUE_FALSE);

                if (ICamelSpringConstants.ER_WAIT_TIMEOUT.equals(timeoutStyle)) {
                    addParamType(elemParams, FIELD_TEXT, "TIMEOUT_TRIGGER", waitTimeout);
                }
            }
        }

        if (strategy != null) {
            addParamType(elemParams, FIELD_CHECK, "USE_AGG_STRATEGY", VALUE_TRUE);
            addParamType(elemParams, FIELD_TEXT, "AGGREGATION_STRATEGY", strategy);
        }

        nodeType.getElementParameter().addAll(elemParams);
    }
}
