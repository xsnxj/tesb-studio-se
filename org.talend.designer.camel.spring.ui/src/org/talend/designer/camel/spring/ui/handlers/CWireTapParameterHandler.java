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
      
        ElementParameterType paramType = fileFact.createElementParameterType();
        paramType.setField("TEXT");
        paramType.setName("UNIQUE_NAME");
        paramType.setValue(uniqueName);
        elemParams.add(paramType);
        
        paramType = fileFact.createElementParameterType();
        paramType.setField("TEXT");
        paramType.setName("URI");
        
        if(!uri.startsWith("\"")){
            uri = "\"" + uri + "\"";
        }
        
        paramType.setValue(uri);
        elemParams.add(paramType);
        
        if(populateType != null){
            paramType = fileFact.createElementParameterType();
            paramType.setField("CHECK");
            paramType.setName("NEW_EXCHANGE");
            paramType.setValue("true");
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("CHECK");
            paramType.setName("COPY_ORIGINAL_MESSAGE");
            paramType.setValue(copy);
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("EXPRESSION");
            paramType.setValue(ICamelSpringConstants.WT_NEW_EXPRESSION_POP.equals(populateType)?"true":"false");
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("PROCESSOR");
            paramType.setValue(ICamelSpringConstants.WT_NEW_PROCESSOR_POP.equals(populateType)?"true":"false");
            elemParams.add(paramType);
            
            if(ICamelSpringConstants.WT_NEW_EXPRESSION_POP.equals(populateType)){
                paramType = fileFact.createElementParameterType();
                paramType.setField("CLOSED_LIST");
                paramType.setName("LANGUAGES");
                paramType.setValue(language);
                elemParams.add(paramType);
                
                paramType = fileFact.createElementParameterType();
                paramType.setField("TEXT");
                paramType.setName("EXPRESSIONTXT");
                paramType.setValue(expression);
                elemParams.add(paramType);
            }
        }
        
        nodeType.getElementParameter().addAll(elemParams);
    }
}
