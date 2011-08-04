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

import java.util.Map;
import java.util.Map.Entry;

import org.talend.designer.core.model.utils.emf.talendfile.NodeType;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public interface IParameterHandler {

    String NAME_POSTFIX = ".NAME";

    String FIELD_POSTFIX = ".FIELD";
    
    /**
     * 
     * DOC LiXP Comment method "handle".
     * @param nodeType
     * @param uniqueName
     * @param parameters
     */
    void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters);
    
    /**
     * 
     * DOC LiXP Comment method "handleAddtionalParam".
     * @param nodeType
     * @param param
     */
    void handleAddtionalParam(NodeType nodeType, Entry<String, String> param);
}
