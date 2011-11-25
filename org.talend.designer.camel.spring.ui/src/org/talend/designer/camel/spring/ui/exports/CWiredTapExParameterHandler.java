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
package org.talend.designer.camel.spring.ui.exports;

import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class CWiredTapExParameterHandler extends AbstractExParameterHandler {

    public CWiredTapExParameterHandler(String component) {
        super(component);
    }

    @Override
    public void handleParameters(EList<?> elementParameterTypes, Map<String, String> parameters) {
        
        String uri = computeTextElementValue("URI", elementParameterTypes);
        parameters.put(ICamelSpringConstants.ENDPOINT_URI, uri);
        
        boolean expression = computeCheckElementValue("EXPRESSION", elementParameterTypes);
        boolean processor = computeCheckElementValue("PROCESSOR", elementParameterTypes);
        if(expression){
            parameters.put(ICamelSpringConstants.WT_POPULATE_TYPE, ICamelSpringConstants.WT_NEW_EXPRESSION_POP);
            String language = computeTextElementValue("LANGUAGES", elementParameterTypes);
            String text = computeTextElementValue("EXPRESSIONTXT", elementParameterTypes);
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TYPE, language);
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TEXT, text);
        }else if(processor){
            parameters.put(ICamelSpringConstants.WT_POPULATE_TYPE, ICamelSpringConstants.WT_NEW_PROCESSOR_POP);
        }
        
        boolean copy = computeCheckElementValue("COPY_ORIGINAL_MESSAGE", elementParameterTypes);
        parameters.put(ICamelSpringConstants.WT_WIRETAP_COPY, String.valueOf(copy));
        
    }
}
