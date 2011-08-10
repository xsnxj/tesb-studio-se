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
import java.util.Properties;

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
        Properties params = getBasicParameters();

        for (Entry<String, String> param : parameters.entrySet()) {
            
            String key = param.getKey();
            String value = param.getValue();
            
            if (key.equals(ICamelSpringConstants.UNIQUE_NAME_ID)) {// Add UNIQUE_NAME parameter
                addParamType(elemParams, FIELD_TEXT, "UNIQUE_NAME", uniqueName);
                continue;
            }

            String field = params.getProperty(key + FIELD_POSTFIX);
            String name = params.getProperty(key + NAME_POSTFIX);
            String ref = params.getProperty(key + REF_POSTFIX);
            
            if(ref != null){ //Handle reference check
                addParamType(elemParams, FIELD_CHECK, ref, VALUE_TRUE);
            }
            
            if (field != null && name != null) { // Basic parameters
                
                if(name.equals("URI")){ //Remove possible file path prefix '/'
                    while(value.startsWith("/")){
                        value = value.substring(1);
                    }
                }
                addParamType(elemParams, field, name, value);
                continue;
            } else {
                handleAddtionalParam(nodeType, param);
            }
            
           
        }

        nodeType.getElementParameter().addAll(elemParams);
    }
}
