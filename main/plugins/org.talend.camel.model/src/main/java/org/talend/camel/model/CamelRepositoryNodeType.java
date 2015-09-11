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
package org.talend.camel.model;

import java.util.HashMap;
import java.util.Map;

import org.talend.core.model.repository.ERepositoryObjectType;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public interface CamelRepositoryNodeType {

    String ROUTES = "ROUTES";

    String BEANS = "BEANS";

    String ROUTE_RESOURCES = "ROUTE_RESOURCES";

    String ROUTE_DOCUMENTATIONS = "ROUTE_DOCS";

    String ROUTE_DOCUMENTATION = "ROUTE_DOC";

    ERepositoryObjectType repositoryRoutesType = ERepositoryObjectType.valueOf(ERepositoryObjectType.class, ROUTES);

    ERepositoryObjectType repositoryBeansType = ERepositoryObjectType.valueOf(ERepositoryObjectType.class, BEANS);

    ERepositoryObjectType repositoryRouteResourceType = ERepositoryObjectType.valueOf(ERepositoryObjectType.class,
        ROUTE_RESOURCES);

    ERepositoryObjectType repositoryDocumentationsType = ERepositoryObjectType.valueOf(ERepositoryObjectType.class,
        ROUTE_DOCUMENTATIONS);

    ERepositoryObjectType repositoryDocumentationType = ERepositoryObjectType.valueOf(ERepositoryObjectType.class,
        ROUTE_DOCUMENTATION);

    // repository type and folder name Map
    Map<ERepositoryObjectType, String> AllRouteRespositoryTypes = new HashMap<ERepositoryObjectType, String>() {
        {
            put(repositoryBeansType, "Bean");
            put(repositoryRouteResourceType, "Resource");
            put(repositoryRoutesType, "Route");
            put(repositoryDocumentationsType, "Route_Docs");
            put(repositoryDocumentationType, "Route_Doc");
        }
    };

}
