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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class RelativeLayoutManager {

    private static final RelativeLayoutManager instance = new RelativeLayoutManager();

    private Map<String, LayoutNode> caches;

    private Point delta;

    private Point startPotision;

    public RelativeLayoutManager() {
        caches = new HashMap<String, LayoutNode>();
        delta = new Point(LayoutConstants.DEFAULT_DELTA_X, LayoutConstants.DEFAULT_DELTA_Y);
        startPotision = new Point(LayoutConstants.DEFAULT_START_POSITION_X, LayoutConstants.DEFAULT_START_POSITION_Y);
    }

    public void startLayout() {
        caches.clear();
    }

    public void stopLayout() {
    }

    /**
     * 
     * DOC LiXP Comment method "getNewLinePotsition".
     * 
     * @return
     */
    private Point getNewLinePotsition(String componentId) {
        Point position = new Point();

        int maxPositionY = calculateMaxY();
        position.x = startPotision.x;
        position.y = maxPositionY + delta.y;

        LayoutNode node = new LayoutNode(componentId);
        node.setDelta(delta);
        node.setPosition(position);
        caches.put(componentId, node);
        return position;
    }

    public Point getNextPosition(String componentId, String parentId) {

        if (parentId == null) {
            return getNewLinePotsition(componentId);
        }

        LayoutNode parent = caches.get(parentId);
        if (parent == null) {
            // ????? wrong
            return startPotision;
        }

        LayoutNode node = new LayoutNode(componentId);
        node.setDelta(delta);
        node.setPosition(parent.getNextChildPosition(calculateMaxY()).getCopy());
        caches.put(componentId, node);
        
        return node.getPosition();
    }

    private int calculateMaxY() {
        int max = 0;
        for (LayoutNode node : caches.values()) {
            if (node.getPosition().y > max) {
                max = node.getPosition().y;
            }
        }
        return max;
    }

    /**
     * Getter for instance.
     * 
     * @return the instance
     */
    public static RelativeLayoutManager getInstance() {
        return instance;
    }
}
