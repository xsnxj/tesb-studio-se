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

import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class CFileParameterHandler extends AbstractParameterHandler {

    public CFileParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public Map<String, List<String>> getTableParameters() {
        
        Map<String, List<String>> tableParams = new HashMap<String, List<String>>();
        List<String> columns = new ArrayList<String>();
        columns.add("NAME");
        columns.add("VALUE");
        tableParams.put("ADVARGUMENTS", columns);
        
        return tableParams;
    }
    
    @Override
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {
        
        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();

        for (Entry<String, String> param : parameters.entrySet()) {

            ElementParameterType paramType;

            Map<String, String> params = getBasicParameters();

            String key = param.getKey();
            String value = param.getValue();
            
            if (key.equals(ICamelSpringConstants.UNIQUE_NAME_ID)) {// Add UNIQUE_NAME parameter
                paramType = fileFact.createElementParameterType();
                paramType.setField("TEXT");
                paramType.setName("UNIQUE_NAME");
                paramType.setValue(uniqueName);
                elemParams.add(paramType);
                continue;
            }

            String field = params.get(key + FIELD_POSTFIX);
            String name = params.get(key + NAME_POSTFIX);
            String ref = params.get(key + REF_POSTFIX);
            
            if(ref != null){ //Handle reference check
                paramType = fileFact.createElementParameterType();
                paramType.setField("CHECK");
                paramType.setName(ref);
                paramType.setValue("true");
                elemParams.add(paramType);
            }
            
            if (field != null && name != null) { // Basic parameters
                
                if(name.equals("URI")){ //Remove possible file path prefix '/'
                    while(value.startsWith("/")){
                        value = value.substring(1);
                    }
                }
                
                paramType = fileFact.createElementParameterType();
                paramType.setField(field);
                paramType.setName(name);
                paramType.setValue(value);
                elemParams.add(paramType);
                continue;
            } else {
                handleAddtionalParam(nodeType, param);
            }
            
           
        }

        nodeType.getElementParameter().addAll(elemParams);
    }
}
