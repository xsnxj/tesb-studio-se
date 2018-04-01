// ============================================================================
//
// Copyright (C) 2018 Talend Inc. - www.talend.com
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

/**
 * This class is a temporary solution for information exchange
 * during route processing, e.g. microservice creation.
 */
public final class RouteProcessingExchange {

	public static final ThreadLocal<Boolean> isCreatingMicroService = new ThreadLocal<>();

	private RouteProcessingExchange() {
		super();
	}
}
