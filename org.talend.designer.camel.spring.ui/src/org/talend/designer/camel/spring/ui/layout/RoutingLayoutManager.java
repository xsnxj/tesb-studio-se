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
package org.talend.designer.camel.spring.ui.layout;

import java.util.List;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class RoutingLayoutManager {

    /**
     * Current position of y
     */
    private int currentY;

    /**
     * 
     * Compute the height of a Node, recursion computing children node.
     * 
     * @param node
     * @return
     */
    private int computeHeight(RoutingNode node) {
        if (node.getChildren().isEmpty()) {
            node.setHeight(1);
            return 1;
        }
        int childrenHeights = 0;
        for (RoutingNode child : node.getChildren()) {
            // All children heights
            childrenHeights += computeHeight(child);
        }
        node.setHeight(childrenHeights);
        return childrenHeights;
    }

    /**
     * Compute the x,y position in pix
     * 
     * @param node
     */
    private void computePosition(RoutingNode node) {
        node.getNodeType().setPosX(LayoutConstants.DEFAULT_DELTA_X * node.getDeltaX());
        node.getNodeType().setPosY((int) (LayoutConstants.DEFAULT_DELTA_Y * node.getDeltaY()));
    }

    /**
     * 
     * Get previous siblings heights
     * 
     * @param node
     * @return
     */
    private double getPreviousSiblingHeights(RoutingNode node) {
        int index = node.getParant().getChildren().indexOf(node);
        double heights = 0;
        for (int i = 0; i < index; i++) {
            heights += node.getParant().getChildren().get(i).getHeight();
        }
        return heights;
    }

    /**
     * Layout routing.
     * 
     * @param routings
     */
    public void layout(List<Routing> routings) {
        startLayout();
        for (Routing routing : routings) {
            layout(routing);
        }
        stopLayout();
    }

    /**
     * 
     * DOC LiXP Comment method "layout".
     * 
     * @param routing
     */
    private void layout(Routing routing) {
        RoutingNode from = routing.getFromNode();
        computeHeight(from);
        locateNode(from);
        currentY += from.getHeight();
    }

    /**
     * Compute the position of Node
     * 
     * @param node
     */
    private void locateNode(RoutingNode node) {
        if (node.getParant() == null) {
            node.setDeltaX(1);
            node.setDeltaY(currentY + node.getHeight() / 2.0);
        } else {
            // for a child, position x is the parent x adding 1, y should be computing using parent height and sibling
            // heights divided.
            int parentX = node.getParant().getDeltaX();
            double parentY = node.getParant().getDeltaY();
            double previosSiblingHeights = getPreviousSiblingHeights(node);
            double y = parentY - node.getParant().getHeight() / 2.0 + previosSiblingHeights + node.getHeight() / 2.0;
            node.setDeltaX(parentX + 1);
            node.setDeltaY(y);
        }
        computePosition(node);

        for (RoutingNode child : node.getChildren()) {
            locateNode(child);
        }
    }

    private void startLayout() {
        currentY = 1;
    }

    private void stopLayout() {
    }

}
