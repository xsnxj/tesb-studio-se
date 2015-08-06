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
package org.talend.designer.camel.spring.ui.exports;

import java.util.Map;

import org.eclipse.emf.common.util.EList;



/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class CCXFExParameterHandler extends AbstractExParameterHandler {
   
   
    public CCXFExParameterHandler(String component) {
        super(component);
    }
    
    @Override
    public void handleParameters(EList<?> elementParameterTypes, Map<String, String> parameters) {
        
//        #SERVICE_TYPE
//        #type.FIELD=CLOSED_LIST
//        #type.NAME=SERVICE_TYPE
//
//        #WSDL_FILE
//        #wsdlURL.FIELD=TEXT
//        #wsdlURL.NAME=WSDL_FILE
//
//        #SERVICE_CLASS
//        #serviceClass.FILED=TEXT
//        #serviceClass.NAME=SERVICE_CLASS
        String SERVICE_TYPE = computeTextElementValue("SERVICE_TYPE", elementParameterTypes);
        String WSDL_FILE = computeTextElementValue("WSDL_FILE", elementParameterTypes);
        String SERVICE_CLASS = computeTextElementValue("SERVICE_CLASS", elementParameterTypes);
        
        if(SERVICE_TYPE.equals("wsdlURL")){
            parameters.put("wsdlURL", WSDL_FILE);
        }else if(SERVICE_TYPE.equals("serviceClass")){
            parameters.put("serviceClass", SERVICE_CLASS);
        }
        parameters.put("type", SERVICE_TYPE);
      
        super.handleParameters(elementParameterTypes, parameters);
        
        boolean SPECIFY_SERVICE = computeCheckElementValue("SPECIFY_SERVICE", elementParameterTypes);
        if(!SPECIFY_SERVICE){
            parameters.remove("serviceName");
            parameters.remove("endpointName");
        }
    }
}
