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

import org.talend.core.model.process.EConnectionType;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.core.model.utils.emf.talendfile.ConnectionType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class ConnectionParameterHandler {

    /**
     * Connection "route when" parameter mapping
     */
    private static final Map<String, String> WHEN_CONN_PARAMS = new HashMap<String, String>();

    /**
     * Connection "route catch" parameter mapping
     */
    private static final Map<String, String> CATCH_CONN_PARAMS = new HashMap<String, String>();

    static {
        WHEN_CONN_PARAMS.put(ICamelSpringConstants.EP_EXPRESSION_TYPE + IParameterHandler.FIELD_POSTFIX, "CLOSED_LIST");
        WHEN_CONN_PARAMS.put(ICamelSpringConstants.EP_EXPRESSION_TYPE + IParameterHandler.NAME_POSTFIX, "ROUTETYPE");
        WHEN_CONN_PARAMS.put(ICamelSpringConstants.EP_EXPRESSION_TEXT + IParameterHandler.FIELD_POSTFIX, "MEMO_JAVA");
        WHEN_CONN_PARAMS.put(ICamelSpringConstants.EP_EXPRESSION_TEXT + IParameterHandler.NAME_POSTFIX, "CONDITION");

        CATCH_CONN_PARAMS.put(ICamelSpringConstants.LB_EXCEPTIONS + IParameterHandler.FIELD_POSTFIX, "TEXT");
        CATCH_CONN_PARAMS.put(ICamelSpringConstants.LB_EXCEPTIONS + IParameterHandler.NAME_POSTFIX, "EXCEPTIONLIST");

    }

    /**
     * 
     * DOC LiXP Comment method "addConnectionParameters".
     * 
     * @param connectionType
     * @param connParameters
     */
    @SuppressWarnings("unchecked")
    public static void addConnectionParameters(ConnectionType connectionType, Map<String, String> connParameters) {

        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();
        int lineStyle = connectionType.getLineStyle();
        Map<String, String> params = null;

        if (lineStyle == EConnectionType.ROUTE_WHEN.getId()) {
            params = WHEN_CONN_PARAMS;
        } else if (lineStyle == EConnectionType.ROUTE_CATCH.getId()) {
            params = CATCH_CONN_PARAMS;
        }

        TalendFileFactory fileFact = TalendFileFactory.eINSTANCE;

        for (Entry<String, String> param : connParameters.entrySet()) {

            ElementParameterType paramType = fileFact.createElementParameterType();
            String key = param.getKey();
            String value = param.getValue();

            String field = params.get(key + IParameterHandler.FIELD_POSTFIX);
            String name = params.get(key + IParameterHandler.NAME_POSTFIX);

            if (field != null && name != null) { // Basic parameters
                paramType.setField(field);
                paramType.setName(name);
                paramType.setValue(value);
                elemParams.add(paramType);
            }
        }

        connectionType.getElementParameter().addAll(elemParams);
    }

}
