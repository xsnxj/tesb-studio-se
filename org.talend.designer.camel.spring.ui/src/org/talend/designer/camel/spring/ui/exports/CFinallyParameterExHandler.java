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
package org.talend.designer.camel.spring.ui.exports;

import java.util.Collections;
import java.util.Map;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class CFinallyParameterExHandler extends AbstractExParameterHandler {
   
    public CFinallyParameterExHandler(String component) {
        super(component);
    }

    @Override
    public Map<String, String> getAvaiableParameters() {
        return Collections.emptyMap();
    }
}
