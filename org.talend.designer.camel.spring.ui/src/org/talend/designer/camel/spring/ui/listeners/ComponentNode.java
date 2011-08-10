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
package org.talend.designer.camel.spring.ui.listeners;

import java.util.ArrayList;
import java.util.List;

import org.talend.designer.core.model.utils.emf.talendfile.NodeType;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class ComponentNode {

    private int routingId;
    
    private String componentId;
    
    private ComponentNode parent;
    
    private List<ComponentNode> children = new ArrayList<ComponentNode>();
    
    private NodeType nodeType;
    
    private int column;

    /**
     * DOC LiXP ComponentNode constructor comment.
     * @param componentId
     */
    public ComponentNode(String componentId) {
        this.componentId = componentId;
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
     * Getter for componentId.
     * @return the componentId
     */
    public String getComponentId() {
        return componentId;
    }

    
    /**
     * Sets the componentId.
     * @param componentId the componentId to set
     */
    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    
    /**
     * Getter for parent.
     * @return the parent
     */
    public ComponentNode getParent() {
        return parent;
    }

    
    /**
     * Sets the parent.
     * @param parent the parent to set
     */
    public void setParent(ComponentNode parent) {
        this.parent = parent;
    }

    
    /**
     * Getter for children.
     * @return the children
     */
    public List<ComponentNode> getChildren() {
        return children;
    }

    
    /**
     * Sets the children.
     * @param children the children to set
     */
    public void setChildren(List<ComponentNode> children) {
        this.children = children;
    }

    
    /**
     * Getter for nodeType.
     * @return the nodeType
     */
    public NodeType getNodeType() {
        return nodeType;
    }

    
    /**
     * Sets the nodeType.
     * @param nodeType the nodeType to set
     */
    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }


    /**
     * Sets the column.
     * @param column the column to set
     */
    public void setColumn(int column) {
        this.column = column;
    }


    /**
     * Getter for column.
     * @return the column
     */
    public int getColumn() {
        return column;
    }
    
    
}
