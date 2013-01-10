// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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
public interface IExportParameterHandler {

    /**
     * 
     * DOC LiXP Comment method "getComponentName".
     * @return
     */
    String getComponentName();
    
    /**
     * 
     * DOC LiXP Comment method "loadParameters".
     * @param elementParameterTypes
     * @param parameters
     */
    void handleParameters(EList<?> elementParameterTypes, Map<String, String> parameters);

}
