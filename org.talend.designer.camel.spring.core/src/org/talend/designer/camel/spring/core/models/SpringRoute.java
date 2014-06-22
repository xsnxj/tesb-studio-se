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
package org.talend.designer.camel.spring.core.models;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class SpringRoute {

    private int routeId;
    
    private SpringRouteNode from;

    
    /**
     * Getter for routeId.
     * @return the routeId
     */
    public int getRouteId() {
        return routeId;
    }

    
    /**
     * Sets the routeId.
     * @param routeId the routeId to set
     */
    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    
    /**
     * Getter for from.
     * @return the from
     */
    public SpringRouteNode getFrom() {
        return from;
    }

    
    /**
     * Sets the from.
     * @param from the from to set
     */
    public void setFrom(SpringRouteNode from) {
        this.from = from;
    }
    
}
