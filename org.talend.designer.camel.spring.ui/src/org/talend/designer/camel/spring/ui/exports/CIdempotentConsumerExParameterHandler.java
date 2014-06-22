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
public class CIdempotentConsumerExParameterHandler extends AbstractExParameterHandler {

    public CIdempotentConsumerExParameterHandler(String component) {
        super(component);
    }

    @Override
    public void handleParameters(EList<?> elementParameterTypes, Map<String, String> parameters) {

        super.handleParameters(elementParameterTypes, parameters);
        
        boolean userLanguage = computeCheckElementValue("USE_LANGUAGE", elementParameterTypes);
        String language = computeTextElementValue("LANGUAGES", elementParameterTypes);
        if(userLanguage){
            String predicate = computeTextElementValue("PREDICATE", elementParameterTypes);
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TYPE, language);
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TEXT, predicate);
        }else{
            String text = computeTextElementValue("EXPRESSION", elementParameterTypes);
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TYPE, "");
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TEXT, text);
        }
    }
}
