// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class CSetHeaderExParameterHandler extends AbstractExParameterHandler {

    public CSetHeaderExParameterHandler(String component) {
        super(component);
    }

    @Override
    public Map<String, String> getAvaiableParameters() {
        return Collections.emptyMap();
    }

    @Override
    public void handleParameters(EList<?> elementParameterTypes, Map<String, String> parameters) {
        
        String HEADER = computeTextElementValue("HEADER", elementParameterTypes);
        parameters.put(ICamelSpringConstants.SH_HEADER_NAME, HEADER);
        
        boolean useBean = computeCheckElementValue("USE_BEAN", elementParameterTypes);
        if(useBean){
            String bean = computeTextElementValue("BEAN", elementParameterTypes);
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TYPE, "bean");
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TEXT, bean);
        }else{
            String language = computeTextElementValue("LANGUAGES", elementParameterTypes);
            String text = computeTextElementValue("EXPRESSION", elementParameterTypes);
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TYPE, language);
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TEXT, text);
        }
        
    }
}
