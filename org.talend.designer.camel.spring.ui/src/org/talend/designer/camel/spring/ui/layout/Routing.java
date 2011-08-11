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
package org.talend.designer.camel.spring.ui.layout;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class Routing {

    private int routingId;
    
    private int width;
    
    private int height;
    
    private RoutingNode fromNode;
    
    public Routing() {
    }

    
    /**
     * Getter for routingId.
     * @return the routingId
     */
    public int getRoutingId() {
        return routingId;
    }

    
    /**
     * Sets the routingId.
     * @param routingId the routingId to set
     */
    public void setRoutingId(int routingId) {
        this.routingId = routingId;
    }

    
    /**
     * Getter for width.
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    
    /**
     * Sets the width.
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    
    /**
     * Getter for height.
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    
    /**
     * Sets the height.
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    
    /**
     * Getter for fromNode.
     * @return the fromNode
     */
    public RoutingNode getFromNode() {
        return fromNode;
    }

    
    /**
     * Sets the fromNode.
     * @param fromNode the fromNode to set
     */
    public void setFromNode(RoutingNode fromNode) {
        this.fromNode = fromNode;
    }
    
}
