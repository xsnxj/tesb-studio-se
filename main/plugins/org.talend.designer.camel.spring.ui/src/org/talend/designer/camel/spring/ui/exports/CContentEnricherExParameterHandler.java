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
package org.talend.designer.camel.spring.ui.exports;

import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class CContentEnricherExParameterHandler extends AbstractExParameterHandler {

    private Map<String, String> parameters;
    
    public CContentEnricherExParameterHandler(String component) {
        super(component);
    }

    @Override
    public Map<String, String> getAvaiableParameters() {
        parameters = super.getAvaiableParameters();
        parameters.put("RESOURCE_URI", "resource_uri");
        parameters.put("AGGREGATION_STRATEGY", "aggregate_strategy");
        return parameters;
    }
    
    @Override
    public void handleParameters(EList<?> elementParameterTypes, Map<String, String> parameters) {
        
        super.handleParameters(elementParameterTypes, parameters);
        
        boolean ENRICH = computeCheckElementValue("ENRICH", elementParameterTypes);
        boolean POLLENRICH = computeCheckElementValue("POLLENRICH", elementParameterTypes);
        if(ENRICH){
            parameters.put(ICamelSpringConstants.ER_MERGE_DATA, ICamelSpringConstants.ER_PRODUCER);
        }else if(POLLENRICH){
            parameters.put(ICamelSpringConstants.ER_MERGE_DATA, ICamelSpringConstants.ER_CONSUMER);
        }
        
        boolean isSpecifyTimeOut = computeCheckElementValue("SPECIFY_TIMEOUT", elementParameterTypes);
        if(isSpecifyTimeOut){
            boolean IMMEDIATE = computeCheckElementValue("IMMEDIATE", elementParameterTypes);
            boolean WAIT = computeCheckElementValue("WAIT", elementParameterTypes);
            boolean TRIGGER = computeCheckElementValue("TRIGGER", elementParameterTypes);
            
            if(IMMEDIATE){
                parameters.put(ICamelSpringConstants.ER_TIMEOUT_STYLE, ICamelSpringConstants.ER_POLL_IMMED);
            }else if(WAIT){
                parameters.put(ICamelSpringConstants.ER_TIMEOUT_STYLE, ICamelSpringConstants.ER_WAIT_UNTIL);
            }else if(TRIGGER){
                String tiggerTimeout = computeTextElementValue("TIMEOUT_TRIGGER", elementParameterTypes);
                parameters.put(ICamelSpringConstants.ER_TIMEOUT_STYLE, ICamelSpringConstants.ER_WAIT_TIMEOUT);
                parameters.put(ICamelSpringConstants.ER_WAIT_TIMEOUT, tiggerTimeout);
            }
        }
    }
  
}
