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
       
        ElementParameterType paramType;
        paramType = fileFact.createElementParameterType();
        paramType.setField("TEXT");
        paramType.setName("UNIQUE_NAME");
        paramType.setValue(uniqueName);
        elemParams.add(paramType);
        
        paramType = fileFact.createElementParameterType();
        paramType.setField("CLOSED_LIST");
        paramType.setName("STRATEGY");
        paramType.setValue(strategy);
        elemParams.add(paramType);
        
        if(ICamelSpringConstants.LB_FAILOVER_STRATEGY.equals(strategy)){
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("BASIC_MODE");
            paramType.setValue(ICamelSpringConstants.LB_BASIC_TYPE.equals(failoverType)?"true":"false");
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("EXCEPTION_MODE");
            paramType.setValue(ICamelSpringConstants.LB_EXCEPTION_TYPE.equals(failoverType)?"true":"false");
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("ROUND_ROBIN_MODE");
            paramType.setValue(ICamelSpringConstants.LB_ROUND_ROBIN_TYPE.equals(failoverType)?"true":"false");
            elemParams.add(paramType);
            
            if(ICamelSpringConstants.LB_EXCEPTION_TYPE.equals(failoverType) && exception != null){
                String[] exceptions = exception.split(";");
                List<ElementValueType> valueTypes = new ArrayList<ElementValueType>();
                for (String ex : exceptions) {
                    ElementValueType valueType = fileFact.createElementValueType();
                    valueType.setElementRef("EXCEPTION");
                    valueType.setValue(ex);
                    valueTypes.add(valueType);
                }
                
                ElementParameterType nodeProperty = ComponentUtilities.getNodeProperty(nodeType, "EXCEPTIONS");
                if(nodeProperty == null){
                    ComponentUtilities.addNodeProperty(nodeType, "EXCEPTIONS", "TABLE");
                  ComponentUtilities.setNodeProperty(nodeType, "EXCEPTIONS", valueTypes);
                }else{
                    nodeProperty.getElementValue().addAll(valueTypes);
                }
            }
            
            if(ICamelSpringConstants.LB_ROUND_ROBIN_TYPE.equals(failoverType)){
               
                paramType = fileFact.createElementParameterType();
                paramType.setField("CLOSED_LIST");
                paramType.setName("INHERIT_ERROR_HANDLER");
                paramType.setValue(parameters.get(ICamelSpringConstants.LB_INHERIT_HANDLE));
                elemParams.add(paramType);
                
                paramType = fileFact.createElementParameterType();
                paramType.setField("CLOSED_LIST");
                paramType.setName("MAXFAILATTEMPT");
                paramType.setValue(parameters.get(ICamelSpringConstants.LB_ATTAMPT_TYPE));
                elemParams.add(paramType);
                
                paramType = fileFact.createElementParameterType();
                paramType.setField("TEXT");
                paramType.setName("ATTEMPT_NUMBER");
                paramType.setValue(maxAttempt);
                elemParams.add(paramType);
                
                paramType = fileFact.createElementParameterType();
                paramType.setField("CHECK");
                paramType.setName("USE_ROUND_ROBIN");
                paramType.setValue(isRoundRobin);
                elemParams.add(paramType);
            }
        }else if(ICamelSpringConstants.LB_CUSTOM_STRATEGY.equals(strategy)){
            paramType = fileFact.createElementParameterType();
            paramType.setField("TEXT");
            paramType.setName("CUSTOM_LOAD_BALANCER");
            paramType.setValue(custom);
            elemParams.add(paramType);
        }else if(ICamelSpringConstants.LB_STICKY_STRATEGY.equals(strategy)){
            paramType = fileFact.createElementParameterType();
            paramType.setField("TEXT");
            paramType.setName("EXPRESSION");
            paramType.setValue(parameters.get(ICamelSpringConstants.EP_EXPRESSION_TEXT));
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("CLOSED_LIST");
            paramType.setName("LANGUAGES");
            paramType.setValue(parameters.get(ICamelSpringConstants.EP_EXPRESSION_TYPE));
            elemParams.add(paramType);
        }
        
        nodeType.getElementParameter().addAll(elemParams);
    }
    
}
