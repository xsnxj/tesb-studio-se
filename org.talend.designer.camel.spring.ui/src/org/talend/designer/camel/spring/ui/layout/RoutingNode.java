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

import java.util.ArrayList;
import java.util.List;

import org.talend.designer.core.model.utils.emf.talendfile.NodeType;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class RoutingNode {

    private String name;
    
    private int positionX;
    
    private int positionY;
    
    private List<RoutingNode> children = new ArrayList<RoutingNode>();
    
    private Routing routing;
    
    private NodeType nodeType;

    /**
     * DOC LiXP RoutingNode constructor comment.
     * @param name
     */
    public RoutingNode(String name) {
        this.name = name;
    }

    
    /**
     * Getter for name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    
    /**
     * Sets the name.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    
    /**
     * Getter for positionX.
     * @return the positionX
     */
    public int getPositionX() {
        return positionX;
    }

    
    /**
     * Sets the positionX.
     * @param positionX the positionX to set
     */
    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    
    /**
     * Getter for positionY.
     * @return the positionY
     */
    public int getPositionY() {
        return positionY;
    }

    
    /**
     * Sets the positionY.
     * @param positionY the positionY to set
     */
    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    
    /**
     * Getter for children.
     * @return the children
     */
    public List<RoutingNode> getChildren() {
        return children;
    }

    
    /**
     * Sets the children.
     * @param children the children to set
     */
    public void setChildren(List<RoutingNode> children) {
        this.children = children;
    }

    
    /**
     * Getter for routing.
     * @return the routing
     */
    public Routing getRouting() {
        return routing;
    }

    
    /**
     * Sets the routing.
     * @param routing the routing to set
     */
    public void setRouting(Routing routing) {
        this.routing = routing;
    }


    /**
     * Sets the nodeType.
     * @param nodeType the nodeType to set
     */
    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }


    /**
     * Getter for nodeType.
     * @return the nodeType
     */
    public NodeType getNodeType() {
        return nodeType;
    }
    
}
