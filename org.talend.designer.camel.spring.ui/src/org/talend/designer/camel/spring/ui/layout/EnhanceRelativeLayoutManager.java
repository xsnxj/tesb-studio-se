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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class EnhanceRelativeLayoutManager implements ILayoutManager {

    private List<Integer> columnHeights;

    private Map<String, EnhanceRelativeLayoutNode> caches;

    private Point delta;

    private Point startPotision;

//    private int column;

    public EnhanceRelativeLayoutManager() {
        columnHeights = new ArrayList<Integer>();
        caches = new HashMap<String, EnhanceRelativeLayoutNode>();
        delta = new Point(LayoutConstants.DEFAULT_DELTA_X, LayoutConstants.DEFAULT_DELTA_Y);
        startPotision = new Point(LayoutConstants.DEFAULT_START_POSITION_X, LayoutConstants.DEFAULT_START_POSITION_Y);
    }

    public void startLayout() {

    }

    public void stopLayout() {
        caches.clear();
        columnHeights.clear();
    }

    /**
     * 
     * DOC LiXP Comment method "getNewLinePotsition".
     * 
     * @return
     */
    private Point getNewLinePotsition(String componentId) {

        int column = 0;
        int maxPositionY = safeGetMaxColumnHeight(column);
        Point position = new Point();
        position.x = startPotision.x;
        position.y = maxPositionY + delta.y;
        columnHeights.set(column, position.y);

        EnhanceRelativeLayoutNode node = new EnhanceRelativeLayoutNode(componentId);
        node.setPosition(position);
        node.setColumn(column);
        caches.put(componentId, node);

        return position;
    }

    private int safeGetMaxColumnHeight(int column) {
        if (columnHeights.size() < column + 1) {
            columnHeights.add(column, 0);
        }
        return columnHeights.get(column);
    }

    /**
     * 
     */
    public Point getNextPosition(String componentId, String parentId) {

        int column = 0;
        
        if (parentId == null) {
            return getNewLinePotsition(componentId);
        }

        EnhanceRelativeLayoutNode parent = caches.get(parentId);
        if (parent == null) {
            // ????? wrong
            return getNewLinePotsition(componentId);
        }

        EnhanceRelativeLayoutNode node = new EnhanceRelativeLayoutNode(componentId);
        Point parentPosition = parent.getPosition();
        Point nextPosition = parent.getNextChildPosition();
        if (nextPosition != null) {// If the parent node already has outgoing connections, just move down a grid to the
                                   // position, and reset the new column height
            column = parent.getColumn() + 1;
            nextPosition.y += delta.y;
            columnHeights.set(column, nextPosition.y);
        } else {// If the parent node doesn't have a outgoing connection
            
            column = parent.getColumn() + 1;
            int maxPositionY = safeGetMaxColumnHeight(column);// Get possible height, if it's a new column, reset the height
                                                        // to started value.
            nextPosition = new Point();// Initial the next position
            nextPosition.x = parentPosition.x + delta.x;// Always remember to move x one step
            if (maxPositionY < parentPosition.y) {// If the column height is less then the parent position at y, set the
                                                  // column height to parent position at y.
                nextPosition.y = parentPosition.y;
                columnHeights.set(column, nextPosition.y);
            } else {// If the column height is more then the parent position y, set the next position y to column height
                    // plus delta y and also reset the column height
                nextPosition.y = maxPositionY + delta.y;
                columnHeights.set(column, nextPosition.y);
            }
        }

        parent.setNextChildPosition(nextPosition);
        node.setPosition(nextPosition);
        node.setColumn(column);
        caches.put(componentId, node);

        return node.getPosition();
    }

}
