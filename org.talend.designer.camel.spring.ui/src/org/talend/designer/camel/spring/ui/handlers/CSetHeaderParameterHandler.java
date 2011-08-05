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

import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class CSetHeaderParameterHandler extends AbstractParameterHandler {

    public CSetHeaderParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {
        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();
        
        String name = parameters.get("header_name");
        String type = parameters.get("EXPRESSION_TYPE");
        String text = parameters.get("EXPRESSION_TEXT");
        
        ElementParameterType paramType = fileFact.createElementParameterType();
        paramType.setField("TEXT");
        paramType.setName("UNIQUE_NAME");
        paramType.setValue(uniqueName);
        elemParams.add(paramType);
        
        if(!name.startsWith("\"")){
            name = "\"" + name;
        }
        
        if(!name.endsWith("\"")){
            name = name + "\"";
        }
        
        paramType = fileFact.createElementParameterType();
        paramType.setField("TEXT");
        paramType.setName("HEADER");
        paramType.setValue(name);
        elemParams.add(paramType);
        
        if("bean".equals(type)){//Use bean?
            paramType = fileFact.createElementParameterType();
            paramType.setField("CHECK");
            paramType.setName("USE_BEAN");
            paramType.setValue("true");
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("TEXT");
            paramType.setName("BEAN");
            paramType.setValue(text);
            elemParams.add(paramType);
        }else{
            paramType = fileFact.createElementParameterType();
            paramType.setField("COLSED_LIST");
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
}
