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
public class CDelayerParameterHandler extends AbstractParameterHandler {

    public CDelayerParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {
        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();
        
        String text = parameters.get(ICamelSpringConstants.EP_EXPRESSION_TEXT);
        
        ElementParameterType paramType = fileFact.createElementParameterType();
        paramType.setField("TEXT");
        paramType.setName("UNIQUE_NAME");
        paramType.setValue(uniqueName);
        elemParams.add(paramType);
        
        if(text.startsWith("\"")){
            text = text.substring(1);
        }
        
        if(text.endsWith("\"")){
            text = text.substring(0, text.length() -1);
        }
        
        try{
            Integer.decode(text);
        }catch(NumberFormatException e){
            text = "2000";
        }
        
        paramType = fileFact.createElementParameterType();
        paramType.setField("TEXT");
        paramType.setName("WAIT");
        paramType.setValue(text);
        elemParams.add(paramType);
      
        nodeType.getElementParameter().addAll(elemParams);
    }
}
