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
public class CDelayerParameterHandler extends AbstractParameterHandler {

    public CDelayerParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {
        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();

        String text = parameters.get(ICamelSpringConstants.EP_EXPRESSION_TEXT);

        addParamType(elemParams, FIELD_TEXT, "UNIQUE_NAME", uniqueName);
        text = unquotes(text);

        try {
            Integer.decode(text);
        } catch (NumberFormatException e) {
            text = "2000";
        }

        addParamType(elemParams, FIELD_TEXT, "WAIT", quotes(text));

        nodeType.getElementParameter().addAll(elemParams);
    }
}
