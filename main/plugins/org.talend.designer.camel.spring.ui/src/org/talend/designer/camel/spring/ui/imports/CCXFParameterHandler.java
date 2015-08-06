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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class CCXFParameterHandler extends AbstractParameterHandler {

    public CCXFParameterHandler(String componentName) {
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
        
        String wsdlURL = parameters.get("wsdlURL");
        String serviceClass = parameters.get("serviceClass");
        if(wsdlURL != null){
            addParamType(nodeType.getElementParameter(), FIELD_CLOSED_LIST, "SERVICE_TYPE", "wsdlURL");
            addParamType(nodeType.getElementParameter(), FIELD_TEXT, "WSDL_FILE", wsdlURL);
            parameters.remove("wsdlURL");
        }else{
            addParamType(nodeType.getElementParameter(), FIELD_CLOSED_LIST, "SERVICE_TYPE", "serviceClass");
            addParamType(nodeType.getElementParameter(), FIELD_TEXT, "SERVICE_CLASS", serviceClass);
            parameters.remove("wsdlURL");
            parameters.remove("serviceClass");
        }
        
        super.handle(nodeType, uniqueName, parameters);
    }
}
