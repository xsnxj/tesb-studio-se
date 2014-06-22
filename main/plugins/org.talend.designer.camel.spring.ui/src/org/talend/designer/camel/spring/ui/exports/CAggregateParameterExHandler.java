// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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
import org.talend.designer.camel.spring.core.ICamelSpringConstants;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class CAggregateParameterExHandler extends AbstractExParameterHandler {

    public CAggregateParameterExHandler(String component) {
        super(component);
    }

    @Override
    public void handleParameters(EList<?> elementParameterTypes, Map<String, String> parameters) {
        super.handleParameters(elementParameterTypes, parameters);
        
        boolean USE_PERSISTENCE = computeCheckElementValue("USE_PERSISTENCE", elementParameterTypes);
        if(!USE_PERSISTENCE){
            return;
        }
        
        String REPOSITORY = computeTextElementValue("REPOSITORY", elementParameterTypes);
        parameters.put(ICamelSpringConstants.AG_REPOSITORY_TYPE, REPOSITORY);
        if(ICamelSpringConstants.AG_AGGREGATION_REPO.equals(REPOSITORY) || ICamelSpringConstants.AG_RECOVERABLE_REPO.equals(REPOSITORY)){
            String CUSTOM_REPOSITORY = computeTextElementValue("CUSTOM_REPOSITORY", elementParameterTypes);
            parameters.put(ICamelSpringConstants.AG_REPOSITORY_NAME, unquotes(CUSTOM_REPOSITORY));
        }
        
        if(ICamelSpringConstants.AG_RECOVERABLE_REPO.equals(REPOSITORY) || ICamelSpringConstants.AG_HAWTDB_REPO.equals(REPOSITORY)){
            boolean USE_RECOVERY = computeCheckElementValue("USE_RECOVERY", elementParameterTypes);
            if(USE_RECOVERY){
                String RECOVERY_INTERVAL = computeTextElementValue("RECOVERY_INTERVAL", elementParameterTypes);
                String DEAD_LETTER_URI = computeTextElementValue("DEAD_LETTER_URI", elementParameterTypes);
                String MAXIMUM_REDELIVERIES = computeTextElementValue("MAXIMUM_REDELIVERIES", elementParameterTypes);

                parameters.put(ICamelSpringConstants.AG_RECOVER_INTERVAL, unquotes(RECOVERY_INTERVAL));
                parameters.put(ICamelSpringConstants.AG_DEAD_LETTER_CHANNEL, unquotes(DEAD_LETTER_URI));
                parameters.put(ICamelSpringConstants.AG_MAXIMUM_REDELIVERIES, MAXIMUM_REDELIVERIES);
            }
        }
        
        if(ICamelSpringConstants.AG_HAWTDB_REPO.equals(REPOSITORY)){
            boolean USE_PERSISTENT_FILE = computeCheckElementValue("USE_PERSISTENT_FILE", elementParameterTypes);
            if(USE_PERSISTENT_FILE){
                String PERSISTENT_FILENAME = computeTextElementValue("PERSISTENT_FILENAME", elementParameterTypes);
                parameters.put(ICamelSpringConstants.AG_HAWTDB_PERSISFILE, unquotes(PERSISTENT_FILENAME));
            }
        }
    }
    
}
