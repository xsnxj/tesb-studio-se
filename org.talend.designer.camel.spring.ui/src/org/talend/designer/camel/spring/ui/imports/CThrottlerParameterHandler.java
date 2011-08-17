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
package org.talend.designer.camel.spring.ui.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class CThrottlerParameterHandler extends AbstractParameterHandler {

    public CThrottlerParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {
        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();
        
        String timePreiodMill = parameters.get(ICamelSpringConstants.TH_TIME_PERIOD_MILL);
        String maxRequestPerPeriod = parameters.get(ICamelSpringConstants.TH_MAX_REQUEST_PER_PERIOD);
        String delay = parameters.get(ICamelSpringConstants.TH_ASYNC_DELAY);
        
        addParamType(elemParams, FIELD_TEXT, "UNIQUE_NAME", uniqueName);
        addParamType(elemParams, FIELD_TEXT, "MESSAGE_COUNT", timePreiodMill);
        addParamType(elemParams, FIELD_TEXT, "TIME_PERIOD", maxRequestPerPeriod);
        addParamType(elemParams, FIELD_CHECK, "USE_ASYNC_DELAYING", delay);
        
        if(maxRequestPerPeriod != null && !maxRequestPerPeriod.isEmpty()){
            addParamType(elemParams, FIELD_CHECK, "SET_TIME_PERIOD", delay);
        }
        
        nodeType.getElementParameter().addAll(elemParams);
    }
}
