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
package org.talend.designer.camel.spring.ui.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class CLoopParameterHandler extends AbstractParameterHandler {

    public CLoopParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {
        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();
        
        String type = parameters.get(ICamelSpringConstants.EP_EXPRESSION_TYPE);
        String text = parameters.get(ICamelSpringConstants.EP_EXPRESSION_TEXT);
        
        ElementParameterType paramType = fileFact.createElementParameterType();
        paramType.setField("TEXT");
        paramType.setName("UNIQUE_NAME");
        paramType.setValue(uniqueName);
        elemParams.add(paramType);
        
        if("constant".equals(type)){
            paramType = fileFact.createElementParameterType();
            paramType.setField("CLOSED_LIST");
            paramType.setName("LOOP_TYPE");
            paramType.setValue("VALUE_TYPE");
            elemParams.add(paramType);
            
            text = removeQuotes(text);
            paramType = fileFact.createElementParameterType();
            paramType.setField("TEXT");
            paramType.setName("VALUE");
            paramType.setValue(text);
            elemParams.add(paramType);
        }else if("header".equals(type)){
            paramType = fileFact.createElementParameterType();
            paramType.setField("CLOSED_LIST");
            paramType.setName("LOOP_TYPE");
            paramType.setValue("HEADER_TYPE");
            elemParams.add(paramType);
            
            text = removeQuotes(text);
            paramType = fileFact.createElementParameterType();
            paramType.setField("TEXT");
            paramType.setName("HEADER");
            paramType.setValue(text);
            elemParams.add(paramType);
        }else{
            paramType = fileFact.createElementParameterType();
            paramType.setField("CLOSED_LIST");
            paramType.setName("LOOP_TYPE");
            paramType.setValue("EXPRESSION_TYPE");
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("TEXT");
            paramType.setName("LANGUAGES");
            paramType.setValue(type);
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("TEXT");
            paramType.setName("EXPRESSION");
            paramType.setValue(text);
            elemParams.add(paramType);
        }
        
        nodeType.getElementParameter().addAll(elemParams);
    }

    /**
     * 
     * DOC LiXP Comment method "removeQuotes".
     * @param text
     * @return
     */
    private String removeQuotes(String text) {
        String result = "";
        if(text.startsWith("\"")){
            result = text.substring(1);
        }
        
        if(text.endsWith("\"")){
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
