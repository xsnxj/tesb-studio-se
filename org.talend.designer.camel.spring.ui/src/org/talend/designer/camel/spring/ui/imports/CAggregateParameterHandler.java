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
package org.talend.designer.camel.spring.ui.imports;

import java.util.Map;

import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class CAggregateParameterHandler extends AbstractParameterHandler {

    public CAggregateParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {
        super.handle(nodeType, uniqueName, parameters);

        String USE_PERSISTENCE = parameters.get(ICamelSpringConstants.AG_USE_PERSISTENCE);
        String REPOSITORY_TYPE = parameters.get(ICamelSpringConstants.AG_REPOSITORY_TYPE);
        String HAWTDB_PERSISFILE = parameters.get(ICamelSpringConstants.AG_HAWTDB_PERSISFILE);
        String REPOSITORY_NAME = parameters.get(ICamelSpringConstants.AG_REPOSITORY_NAME);
        String MAXIMUM_REDELIVERIES = parameters.get(ICamelSpringConstants.AG_MAXIMUM_REDELIVERIES);
        String RECOVER_INTERVAL = parameters.get(ICamelSpringConstants.AG_RECOVER_INTERVAL);
        String DEAD_LETTER_CHANNEL = parameters.get(ICamelSpringConstants.AG_DEAD_LETTER_CHANNEL);

        addParamType(nodeType.getElementParameter(), FIELD_CHECK, "USE_PERSISTENCE", unquotes(USE_PERSISTENCE));
        addParamType(nodeType.getElementParameter(), FIELD_CLOSED_LIST, "REPOSITORY", unquotes(REPOSITORY_TYPE));
        addParamType(nodeType.getElementParameter(), FIELD_TEXT, "CUSTOM_REPOSITORY", REPOSITORY_NAME);
        if (HAWTDB_PERSISFILE != null) {
            addParamType(nodeType.getElementParameter(), FIELD_CHECK, "USE_PERSISTENT_FILE", VALUE_TRUE);
            addParamType(nodeType.getElementParameter(), FIELD_TEXT, "PERSISTENT_FILENAME", HAWTDB_PERSISFILE);
        }
        if (RECOVER_INTERVAL != null) {
            addParamType(nodeType.getElementParameter(), FIELD_CHECK, "USE_RECOVERY", VALUE_TRUE);
        }
        addParamType(nodeType.getElementParameter(), FIELD_TEXT, "MAXIMUM_REDELIVERIES", MAXIMUM_REDELIVERIES);
        addParamType(nodeType.getElementParameter(), FIELD_CHECK, "DEAD_LETTER_URI", unquotes(DEAD_LETTER_CHANNEL));
        addParamType(nodeType.getElementParameter(), FIELD_CHECK, "RECOVERY_INTERVAL", unquotes(RECOVER_INTERVAL));

    }
}
