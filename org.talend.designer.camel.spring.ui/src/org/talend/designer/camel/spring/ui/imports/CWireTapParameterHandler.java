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
package org.talend.designer.camel.spring.ui.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class CWireTapParameterHandler extends AbstractParameterHandler {

    public CWireTapParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {
        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();

        String uri = parameters.get(ICamelSpringConstants.ENDPOINT_URI);
        String populateType = parameters.get(ICamelSpringConstants.WT_POPULATE_TYPE);
        String copy = parameters.get(ICamelSpringConstants.WT_WIRETAP_COPY);
        String language = parameters.get(ICamelSpringConstants.EP_EXPRESSION_TYPE);
        String expression = parameters.get(ICamelSpringConstants.EP_EXPRESSION_TEXT);

        addParamType(elemParams, FIELD_TEXT, "UNIQUE_NAME", uniqueName);
        addParamType(elemParams, FIELD_TEXT, "URI", uri);

        if (populateType != null) {
            addParamType(elemParams, FIELD_CHECK, "NEW_EXCHANGE", VALUE_TRUE);
            addParamType(elemParams, FIELD_CHECK, "COPY_ORIGINAL_MESSAGE", unquotes(copy));

            populateType = unquotes(populateType);

            addParamType(elemParams, FIELD_RADIO, "EXPRESSION",
                    ICamelSpringConstants.WT_NEW_EXPRESSION_POP.equals(populateType) ? VALUE_TRUE : VALUE_FALSE);
            addParamType(elemParams, FIELD_RADIO, "PROCESSOR",
                    ICamelSpringConstants.WT_NEW_PROCESSOR_POP.equals(populateType) ? VALUE_TRUE : VALUE_FALSE);

            if (ICamelSpringConstants.WT_NEW_EXPRESSION_POP.equals(populateType)) {
                addParamType(elemParams, FIELD_CLOSED_LIST, "LANGUAGES", unquotes(language));
                addParamType(elemParams, FIELD_TEXT, "EXPRESSIONTXT", expression);
            }
        }

        nodeType.getElementParameter().addAll(elemParams);
    }
}
