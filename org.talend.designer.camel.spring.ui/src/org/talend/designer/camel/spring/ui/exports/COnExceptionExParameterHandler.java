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

import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class COnExceptionExParameterHandler extends AbstractExParameterHandler {

    private Map<String, String> parameters;
    
   private static final String P_NONE = "NONE";
   
   private static final String P_HANDLE = "HANDLE";
   
   private static final String P_CONTINUE = "CONTINUE";
    
    public COnExceptionExParameterHandler(String component) {
        super(component);
    }

    @Override
    public Map<String, String> getAvaiableParameters() {
        parameters = super.getAvaiableParameters();
        parameters.put("EXCEPTION", "exceptions");
        return parameters;
    }
    
    @Override
    public void handleParameters(EList<?> elementParameterTypes, Map<String, String> parameters) {
        super.handleParameters(elementParameterTypes, parameters);
        
        boolean isNone = computeCheckElementValue(P_NONE, elementParameterTypes);
        boolean isHandler = computeCheckElementValue(P_HANDLE, elementParameterTypes);
        boolean isContinue = computeCheckElementValue(P_CONTINUE, elementParameterTypes);
        
        if(isContinue){
            parameters.put(ICamelSpringConstants.OE_EXCEPTION_BEHAVIOR, ICamelSpringConstants.OE_CONTINUE_EXCEPTION);
        }else if(isHandler){
            parameters.put(ICamelSpringConstants.OE_EXCEPTION_BEHAVIOR, ICamelSpringConstants.OE_HANDLE_EXCEPTION);
        }else if(isNone){
            parameters.put(ICamelSpringConstants.OE_EXCEPTION_BEHAVIOR, "");
        }
    }

}
