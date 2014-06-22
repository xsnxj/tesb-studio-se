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
package org.talend.designer.camel.spring.ui.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.core.model.components.ComponentUtilities;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class CLoadBalancerParameterHandler extends AbstractParameterHandler {

    public CLoadBalancerParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {
        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();

        String strategy = parameters.get(ICamelSpringConstants.LB_BALANCE_STRATEGY);
        String failoverType = parameters.get(ICamelSpringConstants.LB_FAILOVER_TYPE);
        String exception = parameters.get(ICamelSpringConstants.LB_EXCEPTIONS);
        String maxAttempt = parameters.get(ICamelSpringConstants.LB_MAXIMUM_ATTAMPTS);
        String isRoundRobin = parameters.get(ICamelSpringConstants.LB_IS_ROUND_ROBIN);
        String custom = parameters.get(ICamelSpringConstants.LB_CUSTOM_STRATEGY);

        strategy = unquotes(strategy);
        failoverType = unquotes(failoverType);

        addParamType(elemParams, FIELD_TEXT, "UNIQUE_NAME", uniqueName);
        addParamType(elemParams, FIELD_CLOSED_LIST, "STRATEGY", strategy);

        if (ICamelSpringConstants.LB_FAILOVER_STRATEGY.equals(strategy)) {

            addParamType(elemParams, FIELD_RADIO, "BASIC_MODE",
                    ICamelSpringConstants.LB_BASIC_TYPE.equals(failoverType) ? VALUE_TRUE : VALUE_FALSE);
            addParamType(elemParams, FIELD_RADIO, "EXCEPTION_MODE",
                    ICamelSpringConstants.LB_EXCEPTION_TYPE.equals(failoverType) ? VALUE_TRUE : VALUE_FALSE);
            addParamType(elemParams, FIELD_RADIO, "ROUND_ROBIN_MODE",
                    ICamelSpringConstants.LB_ROUND_ROBIN_TYPE.equals(failoverType) ? VALUE_TRUE : VALUE_FALSE);

            if (ICamelSpringConstants.LB_EXCEPTION_TYPE.equals(failoverType) && exception != null) {
                exception = unquotes(exception);
                String[] exceptions = exception.split(";");
                List<ElementValueType> valueTypes = new ArrayList<ElementValueType>();
                for (String ex : exceptions) {
                    ElementValueType valueType = fileFact.createElementValueType();
                    valueType.setElementRef("EXCEPTION");
                    valueType.setValue(ex);
                    valueTypes.add(valueType);
                }

                ElementParameterType nodeProperty = ComponentUtilities.getNodeProperty(nodeType, "EXCEPTIONS");
                if (nodeProperty == null) {
                    ComponentUtilities.addNodeProperty(nodeType, "EXCEPTIONS", "TABLE");
                    ComponentUtilities.setNodeProperty(nodeType, "EXCEPTIONS", valueTypes);
                } else {
                    nodeProperty.getElementValue().addAll(valueTypes);
                }
            }

            if (ICamelSpringConstants.LB_ROUND_ROBIN_TYPE.equals(failoverType)) {
                addParamType(elemParams, FIELD_CLOSED_LIST, "INHERIT_ERROR_HANDLER",
                        unquotes(parameters.get(ICamelSpringConstants.LB_INHERIT_HANDLE)));
                addParamType(elemParams, FIELD_CLOSED_LIST, "MAXFAILATTEMPT",
                        unquotes(parameters.get(ICamelSpringConstants.LB_ATTAMPT_TYPE)));
                addParamType(elemParams, FIELD_TEXT, "ATTEMPT_NUMBER", maxAttempt);
                addParamType(elemParams, FIELD_CHECK, "USE_ROUND_ROBIN", unquotes(isRoundRobin));
            }
        } else if (ICamelSpringConstants.LB_CUSTOM_STRATEGY.equals(strategy)) {
            addParamType(elemParams, FIELD_TEXT, "CUSTOM_LOAD_BALANCER", custom);
        } else if (ICamelSpringConstants.LB_STICKY_STRATEGY.equals(strategy)) {
            addParamType(elemParams, FIELD_TEXT, "EXPRESSION", parameters.get(ICamelSpringConstants.EP_EXPRESSION_TEXT));
            addParamType(elemParams, FIELD_CLOSED_LIST, "LANGUAGES",
                    unquotes(parameters.get(ICamelSpringConstants.EP_EXPRESSION_TYPE)));
        }

        nodeType.getElementParameter().addAll(elemParams);
    }

}
