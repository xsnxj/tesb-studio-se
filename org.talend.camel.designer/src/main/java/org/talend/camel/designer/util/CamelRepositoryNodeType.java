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
package org.talend.camel.designer.util;

import org.talend.core.model.repository.ERepositoryObjectType;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public interface CamelRepositoryNodeType {

    String ROUTES = "ROUTES";

    String BEANS = "BEANS";

	String ROUTE_RESOURCES = "ROUTE_RESOURCES";

    ERepositoryObjectType repositoryRoutesType = (ERepositoryObjectType) ERepositoryObjectType.valueOf(
            ERepositoryObjectType.class, ROUTES);

    ERepositoryObjectType repositoryBeansType = (ERepositoryObjectType) ERepositoryObjectType.valueOf(
            ERepositoryObjectType.class, BEANS);

	ERepositoryObjectType repositoryRouteResourceType = (ERepositoryObjectType) ERepositoryObjectType
			.valueOf(ERepositoryObjectType.class, ROUTE_RESOURCES);
}
