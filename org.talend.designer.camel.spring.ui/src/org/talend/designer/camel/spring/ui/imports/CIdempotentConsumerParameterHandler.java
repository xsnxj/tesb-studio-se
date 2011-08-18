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
public class CIdempotentConsumerParameterHandler extends AbstractParameterHandler {

    public CIdempotentConsumerParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {
       
        super.handle(nodeType, uniqueName, parameters);
        
        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();
        
        String type = parameters.get(ICamelSpringConstants.EP_EXPRESSION_TYPE);
        String text = parameters.get(ICamelSpringConstants.EP_EXPRESSION_TEXT);
        
        if("".equals(type)){//Use bean?
            addParamType(elemParams, FIELD_CHECK, "USE_LANGUAGE", VALUE_FALSE);
            text = unquotes(text);
            addParamType(elemParams, FIELD_TEXT, "EXPRESSION", text);
        }else{
            addParamType(elemParams, FIELD_CHECK, "USE_LANGUAGE", VALUE_TRUE);
            addParamType(elemParams, FIELD_CLOSED_LIST, "LANGUAGES", type);
            addParamType(elemParams, FIELD_TEXT, "PREDICATE", text);
        }
        
        nodeType.getElementParameter().addAll(elemParams);
    }
}
