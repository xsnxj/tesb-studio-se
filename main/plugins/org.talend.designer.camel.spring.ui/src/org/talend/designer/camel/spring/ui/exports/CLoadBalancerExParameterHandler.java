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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class CLoadBalancerExParameterHandler extends AbstractExParameterHandler {

    private Map<String, String> parameters;

    public CLoadBalancerExParameterHandler(String component) {
        super(component);
    }
    
    @Override
    public Map<String, String> getAvaiableParameters() {
        
        if(parameters != null){
            return parameters;
        }
        
        parameters = new HashMap<String, String>();
        parameters.put("EXCEPTION", "exceptions");
        return parameters;
    }

    @Override
    public void handleParameters(EList<?> elementParameterTypes, Map<String, String> parameters) {
        
        String strategy = computeTextElementValue("STRATEGY", elementParameterTypes);
        parameters.put(ICamelSpringConstants.LB_BALANCE_STRATEGY, strategy);

        if (ICamelSpringConstants.LB_FAILOVER_STRATEGY.equals(strategy)) {
            boolean basicMode = computeCheckElementValue("BASIC_MODE", elementParameterTypes);
            boolean exception_mode = computeCheckElementValue("EXCEPTION_MODE", elementParameterTypes);
            boolean round_robin_mode = computeCheckElementValue("ROUND_ROBIN_MODE", elementParameterTypes);
            
            if(basicMode){
                parameters.put(ICamelSpringConstants.LB_FAILOVER_TYPE, ICamelSpringConstants.LB_BASIC_TYPE);
            }else if(exception_mode){
                parameters.put(ICamelSpringConstants.LB_FAILOVER_TYPE, ICamelSpringConstants.LB_EXCEPTION_TYPE);
                super.handleParameters(elementParameterTypes, parameters);
            }else if(round_robin_mode){
                parameters.put(ICamelSpringConstants.LB_FAILOVER_TYPE, ICamelSpringConstants.LB_ROUND_ROBIN_TYPE);
                
                String inherit_error_handler = computeTextElementValue("INHERIT_ERROR_HANDLER", elementParameterTypes);
                String maxfailattempt = computeTextElementValue("MAXFAILATTEMPT", elementParameterTypes);
                String attempt_number = computeTextElementValue("ATTEMPT_NUMBER", elementParameterTypes);
                String use_round_robin = computeTextElementValue("USE_ROUND_ROBIN", elementParameterTypes);
                parameters.put(ICamelSpringConstants.LB_INHERIT_HANDLE, inherit_error_handler);
                parameters.put(ICamelSpringConstants.LB_ATTAMPT_TYPE, maxfailattempt);
                parameters.put(ICamelSpringConstants.LB_MAXIMUM_ATTAMPTS, attempt_number);
                parameters.put(ICamelSpringConstants.LB_IS_ROUND_ROBIN, use_round_robin);
            }
            
        } else if (ICamelSpringConstants.LB_CUSTOM_STRATEGY.equals(strategy)) {
            String load = computeTextElementValue("CUSTOM_LOAD_BALANCER", elementParameterTypes);
            parameters.put(ICamelSpringConstants.LB_CUSTOM_STRATEGY, load);
        } else if (ICamelSpringConstants.LB_STICKY_STRATEGY.equals(strategy)) {
            String language = computeTextElementValue("LANGUAGES", elementParameterTypes);
            String text = computeTextElementValue("EXPRESSION", elementParameterTypes);
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TYPE, language);
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TEXT, text);
        }
        
    }
}
