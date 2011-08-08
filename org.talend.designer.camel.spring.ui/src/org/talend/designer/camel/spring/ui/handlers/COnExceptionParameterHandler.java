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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.talend.core.model.components.ComponentUtilities;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class COnExceptionParameterHandler extends AbstractParameterHandler {

    public COnExceptionParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public Map<String, List<String>> getTableParameters() {

        Map<String, List<String>> tableParams = new HashMap<String, List<String>>();
        List<String> columns = new ArrayList<String>();
        columns.add("EXCEPTION");
        tableParams.put("EXCEPTIONS", columns);

        return tableParams;
    }

    @Override
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {
       
        ElementParameterType paramType;
        String exceptionBehavor = parameters.get(ICamelSpringConstants.OE_EXCEPTION_BEHAVIOR);
        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();
        
        if(ICamelSpringConstants.OE_CONTINUE_EXCEPTION.equals(exceptionBehavor)){
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("CONTINUE");
            paramType.setValue("true");
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("HANDLE");
            paramType.setValue("false");
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("NONE");
            paramType.setValue("false");
            elemParams.add(paramType);
        }else  if(ICamelSpringConstants.OE_HANDLE_EXCEPTION.equals(exceptionBehavor)){
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("CONTINUE");
            paramType.setValue("false");
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("HANDLE");
            paramType.setValue("true");
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("NONE");
            paramType.setValue("false");
            elemParams.add(paramType);
        }else{
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("CONTINUE");
            paramType.setValue("false");
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("HANDLE");
            paramType.setValue("false");
            elemParams.add(paramType);
            
            paramType = fileFact.createElementParameterType();
            paramType.setField("RADIO");
            paramType.setName("NONE");
            paramType.setValue("true");
            elemParams.add(paramType);
        }
        
        nodeType.getElementParameter().addAll(elemParams);
        
        parameters.remove(ICamelSpringConstants.OE_EXCEPTION_BEHAVIOR);
        
        super.handle(nodeType, uniqueName, parameters);
        
        
    }
    
    @Override
    public void handleAddtionalParam(NodeType nodeType, Entry<String, String> param) {

        Map<String, List<String>> tableParameters = getTableParameters();

        if (tableParameters.size() == 0) {
            return;
        }

        if (tableParameters.size() == 1) {

            for (Entry<String, List<String>> tableParam : tableParameters.entrySet()) {

                String key = param.getKey();
                String value = param.getValue();

                if (key.equals("exceptions")) {
                    String[] exceptions = value.split(";");
                    List<ElementValueType> valueTypes = new ArrayList<ElementValueType>();
                    for (String ex : exceptions) {
                        ElementValueType valueType = fileFact.createElementValueType();
                        valueType.setElementRef(tableParam.getValue().get(0));
                        valueType.setValue(ex);
                        valueTypes.add(valueType);
                    }
                    ElementParameterType nodeProperty = ComponentUtilities.getNodeProperty(nodeType, tableParam.getKey());
                    if(nodeProperty == null){
                        ComponentUtilities.addNodeProperty(nodeType, tableParam.getKey(), "TABLE");
                      ComponentUtilities.setNodeProperty(nodeType, tableParam.getKey(), valueTypes);
                    }else{
                        nodeProperty.getElementValue().addAll(valueTypes);
                    }
                }
            }

        } else {
            // /FIXME
        }

    }
}
