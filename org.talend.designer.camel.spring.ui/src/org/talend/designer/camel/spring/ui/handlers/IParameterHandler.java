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

import org.talend.designer.core.model.utils.emf.talendfile.NodeType;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public interface IParameterHandler {

    /**
     * represent parameter name in Component_java.XML
     */
    String NAME_POSTFIX = ".NAME";

    /**
     * represent parameter field in Component_java.XML
     */
    String FIELD_POSTFIX = ".FIELD";
    
    /**
     * represent related check field, such as cAggregate's AGGREGATION_STRATEGY and USE_AGGREGATION_STRATEGY
     */
    String REF_POSTFIX = ".REF_CHECK";
    
    /**
     * 
     * Handle details parameters of node
     * @param nodeType
     * @param uniqueName
     * @param parameters
     */
    void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters);
    
}
