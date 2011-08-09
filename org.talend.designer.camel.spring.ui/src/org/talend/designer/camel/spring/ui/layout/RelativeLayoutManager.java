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
public class RelativeLayoutManager implements ILayoutManager {

    private Map<String, RelativeLayoutNode> caches;

    private Point delta;

    private Point startPotision;

    private int max = 0;

    public RelativeLayoutManager() {
        caches = new HashMap<String, RelativeLayoutNode>();
        delta = new Point(LayoutConstants.DEFAULT_DELTA_X, LayoutConstants.DEFAULT_DELTA_Y);
        startPotision = new Point(LayoutConstants.DEFAULT_START_POSITION_X, LayoutConstants.DEFAULT_START_POSITION_Y);
    }

    public void startLayout() {
    }

    public void stopLayout() {
        caches.clear();
    }

    /**
     * 
     * DOC LiXP Comment method "getNewLinePotsition".
     * 
     * @return
     */
    private Point getNewLinePotsition(String componentId) {
        Point position = new Point();

        int maxPositionY = max;
        position.x = startPotision.x;
        position.y = maxPositionY + delta.y;
        max = position.y;

        RelativeLayoutNode node = new RelativeLayoutNode(componentId);
        node.setPosition(position);
        caches.put(componentId, node);
        return position;
    }

    public Point getNextPosition(String componentId, String parentId) {

        if (parentId == null) {
            return getNewLinePotsition(componentId);
        }

        RelativeLayoutNode parent = caches.get(parentId);
        if (parent == null) {
            // ????? wrong
            return startPotision;
        }

        RelativeLayoutNode node = new RelativeLayoutNode(componentId);
        Point nextChildPosition = calculateNextChildPosition(parent, max);
        parent.setNextChildPosition(nextChildPosition.getCopy());
        node.setPosition(nextChildPosition.getCopy());
        caches.put(componentId, node);

        return node.getPosition();
    }

    private Point calculateNextChildPosition(RelativeLayoutNode parent, int maxPositionY) {
        int x, y;
        Point nextChildPosition = parent.getNextChildPosition();
        Point position = parent.getPosition();
        if (nextChildPosition == null) {// first time get child position
            x = position.x + delta.x;
            y = position.y;
        } else {// already has children, so it's multi-cast
            x = nextChildPosition.x;
            y = maxPositionY + delta.y;
            max = y;
        }
       return new Point(x, y);
    }

//    private int calculateMaxY() {
//        for (RelativeLayoutNode node : caches.values()) {
//            if (node.getPosition().y > max) {
//                max = node.getPosition().y;
//            }
//        }
//        return max;
//    }

}
