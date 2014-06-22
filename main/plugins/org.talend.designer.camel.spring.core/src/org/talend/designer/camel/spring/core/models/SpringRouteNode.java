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
package org.talend.designer.camel.spring.core.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class SpringRouteNode {

    private SpringRoute ownerRoute;

    private SpringRouteNode parent;

    private List<SpringRouteNode> children;

    private Map<String, String> parameter;

    private String uniqueName;

    private int type;

    private SpringRouteNode next;

    private SpringRouteNode firstChild;

    private boolean isParentChild;

    public SpringRouteNode(String uniqueName) {
        this.uniqueName = uniqueName;
        this.parameter = new HashMap<String, String>();
    }

    /**
     * Getter for ownerRoute.
     * 
     * @return the ownerRoute
     */
    public SpringRoute getOwnerRoute() {
        return ownerRoute;
    }

    /**
     * Sets the ownerRoute.
     * 
     * @param ownerRoute the ownerRoute to set
     */
    public void setOwnerRoute(SpringRoute ownerRoute) {
        this.ownerRoute = ownerRoute;
    }

    /**
     * Getter for parent.
     * 
     * @return the parent
     */
    public SpringRouteNode getParent() {
        return parent;
    }

    /**
     * Sets the parent.
     * 
     * @param parent the parent to set
     */
    private void setParent(SpringRouteNode parent) {
        this.parent = parent;
    }

    /**
     * Getter for children.
     * 
     * @return the children
     */
    public List<SpringRouteNode> getChildren() {
        return children;
    }

    /**
     * Sets the children.
     * 
     * @param children the children to set
     */
    public void setChildren(List<SpringRouteNode> children) {
        this.children = children;
    }

    /**
     * Getter for parameter.
     * 
     * @return the parameter
     */
    public Map<String, String> getParameter() {
        return parameter;
    }

    /**
     * Sets the parameter.
     * 
     * @param parameter the parameter to set
     */
    public void setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
    }

    /**
     * Getter for uniqueName.
     * 
     * @return the uniqueName
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * Sets the uniqueName.
     * 
     * @param uniqueName the uniqueName to set
     */
    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    /**
     * Sets the type.
     * 
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Getter for type.
     * 
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the next.
     * 
     * @param nextSibling the nex to set
     */
    public void setSibling(SpringRouteNode nextSibling) {
        this.next = nextSibling;
        nextSibling.setParent(this);
        setParentChild(false);
    }

    /**
     * Getter for next
     * 
     * @return the next
     */
    public SpringRouteNode getSibling() {
        return next;
    }

    /**
     * Sets the nextChild.
     * 
     * @param firstChild the nextChild to set
     */
    public void setFirstChild(SpringRouteNode firstChild) {
        this.firstChild = firstChild;
        firstChild.setParent(this);
        setParentChild(true);
    }

    /**
     * Getter for nextChild.
     * 
     * @return the nextChild
     */
    public SpringRouteNode getFirstChild() {
        return firstChild;
    }

    @Override
    public String toString() {
        String firstChildName = firstChild == null ? "" : firstChild.getUniqueName();
        String nextName = next == null || next.getUniqueName() == null ? "" : next.getUniqueName();
        String toString = "Component [" + (uniqueName == null ? "" : uniqueName) + "] FistChild [" + (firstChildName == null ? "" : firstChildName) + "] Sibling [" + (nextName == null ? "" : nextName) + "]";
        return toString;
    }

    /**
     * Sets the isParentChild.
     * 
     * @param isParentChild the isParentChild to set
     */
    public void setParentChild(boolean isParentChild) {
        this.isParentChild = isParentChild;
    }

    /**
     * Getter for isParentChild.
     * 
     * @return the isParentChild
     */
    public boolean isParentChild() {
        return isParentChild;
    }

}
