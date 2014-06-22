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

import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class CLoopExParameterHandler extends AbstractExParameterHandler {

    public CLoopExParameterHandler(String component) {
        super(component);
    }

    @Override
    public Map<String, String> getAvaiableParameters() {

        return Collections.emptyMap();
    }

    @Override
    public void handleParameters(EList<?> elementParameterTypes, Map<String, String> parameters) {

        String loopType = computeTextElementValue("LOOP_TYPE", elementParameterTypes);
        if ("VALUE_TYPE".equals(loopType)) {
            String text = computeTextElementValue("VALUE", elementParameterTypes);
            parameters.put("constant", text);
        } else if ("HEADER_TYPE".equals(loopType)) {
            String text = computeTextElementValue("HEADER", elementParameterTypes);
            parameters.put("header", text);
        } else if ("EXPRESSION_TYPE".equals(loopType)) {
            String language = computeTextElementValue("LANGUAGES", elementParameterTypes);
            String text = computeTextElementValue("EXPRESSION", elementParameterTypes);
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TYPE, language);
            parameters.put(ICamelSpringConstants.EP_EXPRESSION_TEXT, text);
        }
    }
}
