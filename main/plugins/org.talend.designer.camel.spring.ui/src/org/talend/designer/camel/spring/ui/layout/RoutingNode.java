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
package org.talend.designer.camel.spring.ui.layout;

import java.util.ArrayList;
import java.util.List;

import org.talend.designer.core.model.utils.emf.talendfile.NodeType;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class RoutingNode {

    private String name;
    
    /**
     * X delta grid.
     */
    private int deltaX;
    
    /**
     * Y delta grid.
     */
    private double deltaY;
    
    private List<RoutingNode> children = new ArrayList<RoutingNode>();
    
    private Routing routing;
    
    private NodeType nodeType;
    
    private RoutingNode parant;
    
    /**
     * Routing node height, size of direct children minus 1 and add all heights of children
     */
    private int height;

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
     * Getter for deltaX.
     * @return the deltaX
     */
    public int getDeltaX() {
        return deltaX;
    }

    
    /**
     * Sets the deltaX.
     * @param deltaX the deltaX to set
     */
    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }

    
    /**
     * Getter for positionY.
     * @return the positionY
     */
    public double getDeltaY() {
        return deltaY;
    }

    
    /**
     * Sets the deltaY.
     * @param deltaY the deltaY to set
     */
    public void setDeltaY(double deltaY) {
        this.deltaY = deltaY;
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


    /**
     * Sets the height.
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }


    /**
     * Getter for height.
     * @return the height
     */
    public int getHeight() {
        return height;
    }


    /**
     * Sets the parant.
     * @param parant the parant to set
     */
    public void setParant(RoutingNode parant) {
        this.parant = parant;
    }


    /**
     * Getter for parant.
     * @return the parant
     */
    public RoutingNode getParant() {
        return parant;
    }
    
}
