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


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class CCatchExParameterHandler extends AbstractExParameterHandler {
   
    private Map<String, String> parameters;
   
    public CCatchExParameterHandler(String component) {
        super(component);
    }
    
    @Override
    public Map<String, String> getAvaiableParameters() {

        if(parameters != null){
            return parameters;
        }
        
        parameters = new HashMap<String, String>();
        parameters.put("EXCEPTIONLIST", ICamelSpringConstants.LB_EXCEPTIONS);
        return parameters;
    }
}
