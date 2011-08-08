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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.talend.designer.camel.spring.ui.listeners.ComponentNode;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public final class LayoutUtils {

    private LayoutUtils() {

    }

    /**
     * 
     * DOC LiXP Comment method "simpleLayout".
     * 
     * @param nodes
     */
    public static void simpleLayout(Collection<ComponentNode> nodes) {

        List<ComponentNode> routings = new ArrayList<ComponentNode>();
        buildRouings(routings, nodes);
        layoutRoutings(routings);
        
    }

    /**
     * 
     * DOC LiXP Comment method "internalLayout".
     * @param routings
     */
    private static void layoutRoutings(List<ComponentNode> routings) {
        int x = LayoutConstants.DEFAULT_START_POSITION_X;
        int y = LayoutConstants.DEFAULT_START_POSITION_Y;
        int deltaY = LayoutConstants.DEFAULT_DELTA_Y;
        
        for(int index = 0; index < routings.size(); index ++){
            ComponentNode node = routings.get(index);
            node.getNodeType().setPosX(x);
            node.getNodeType().setPosY(y + deltaY * (index + 1));
            layoutNode(node);
        }
    }

    private static void layoutNode(ComponentNode node) {

        ComponentNode parent = node.getParent();
        if(parent != null){
            int x = parent.getNodeType().getPosX();
            int y = parent.getNodeType().getPosY();
            
            int childIndex = parent.getChildren().indexOf(node);
            if(childIndex == 0){
                node.getNodeType().setPosX(x + LayoutConstants.DEFAULT_DELTA_X);
                node.getNodeType().setPosY(y);
            }else{
                ComponentNode previous = parent.getChildren().get(childIndex - 1);
                node.getNodeType().setPosX(previous.getNodeType().getPosX());
                node.getNodeType().setPosY(previous.getNodeType().getPosY() + LayoutConstants.DEFAULT_DELTA_Y);
            }
        }
        
        for(ComponentNode children: node.getChildren()){
            layoutNode(children);
        }
        
        
    }

    /**
     * 
     * Build routings. All routing are in a ASC order.
     * 
     * @param routings
     * @param nodes
     */
    private static void buildRouings(List<ComponentNode> routings, Collection<ComponentNode> nodes) {
        for (ComponentNode node : nodes) {
            if (node.getParent() == null) {
                routings.add(node);
            }
        }

        Collections.sort(routings, new Comparator<ComponentNode>() {
            public int compare(ComponentNode o1, ComponentNode o2) {
                return o1.getRoutingId() - o2.getRoutingId();
            }
        });
    }
}
