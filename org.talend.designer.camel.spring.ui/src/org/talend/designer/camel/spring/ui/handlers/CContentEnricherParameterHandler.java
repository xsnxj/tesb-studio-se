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

        ElementParameterType paramType = fileFact.createElementParameterType();
        paramType.setField("TEXT");
        paramType.setName("UNIQUE_NAME");
        paramType.setValue(uniqueName);
        elemParams.add(paramType);

        paramType = fileFact.createElementParameterType();
        paramType.setField("TEXT");
        paramType.setName("RESOURCE_URI");
        paramType.setValue(uri == null ? "" : uri);
        elemParams.add(paramType);

        paramType = fileFact.createElementParameterType();
        paramType.setField("RADIO");
        paramType.setName("ENRICH");
        paramType.setValue(ICamelSpringConstants.ER_PRODUCER.equals(mergeData) ? "true" : "false");
        elemParams.add(paramType);

        paramType = fileFact.createElementParameterType();
        paramType.setField("RADIO");
        paramType.setName("POLLENRICH");
        paramType.setValue(ICamelSpringConstants.ER_CONSUMER.equals(mergeData) ? "true" : "false");
        elemParams.add(paramType);

        if (ICamelSpringConstants.ER_CONSUMER.equals(mergeData)) {
            // Consumer
            if (timeoutStyle != null) {
                paramType = fileFact.createElementParameterType();
                paramType.setField("CHECK");
                paramType.setName("SPECIFY_TIMEOUT");
                paramType.setValue("true");
                elemParams.add(paramType);

                paramType = fileFact.createElementParameterType();
                paramType.setField("RADIO");
                paramType.setName("IMMEDIATE");
                paramType.setValue(ICamelSpringConstants.ER_POLL_IMMED.equals(timeoutStyle)?"true":"false");
                elemParams.add(paramType);

                paramType = fileFact.createElementParameterType();
                paramType.setField("RADIO");
                paramType.setName("WAIT");
                paramType.setValue(ICamelSpringConstants.ER_WAIT_UNTIL.equals(timeoutStyle)?"true":"false");
                elemParams.add(paramType);
                
                paramType = fileFact.createElementParameterType();
                paramType.setField("RADIO");
                paramType.setName("TRIGGER");
                paramType.setValue(ICamelSpringConstants.ER_WAIT_TIMEOUT.equals(timeoutStyle)?"true":"false");
                elemParams.add(paramType);
                
                if (ICamelSpringConstants.ER_WAIT_TIMEOUT.equals(timeoutStyle)) {
                    paramType = fileFact.createElementParameterType();
                    paramType.setField("TEXT");
                    paramType.setName("TIMEOUT_TRIGGER");
                    paramType.setValue(waitTimeout);
                    elemParams.add(paramType);
                }
            }
        }

        if (strategy != null) {
            paramType = fileFact.createElementParameterType();
            paramType.setField("CHECK");
            paramType.setName("USE_AGG_STRATEGY");
            paramType.setValue("true");
            elemParams.add(paramType);

            paramType = fileFact.createElementParameterType();
            paramType.setField("TEXT");
            paramType.setName("AGGREGATION_STRATEGY");
            paramType.setValue(strategy);
            elemParams.add(paramType);
        }

        nodeType.getElementParameter().addAll(elemParams);
    }
}
