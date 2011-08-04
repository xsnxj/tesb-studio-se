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
public class EnhanceRelativeLayoutManager implements ILayoutManager{

    private List<Integer> columnMaxHeights;

    private Map<String, RelativeLayoutNode> caches;

    private Point delta;

    private Point startPotision;

    private int column;

    public EnhanceRelativeLayoutManager() {
        columnMaxHeights = new ArrayList<Integer>();
        caches = new HashMap<String, RelativeLayoutNode>();
        delta = new Point(LayoutConstants.DEFAULT_DELTA_X, LayoutConstants.DEFAULT_DELTA_Y);
        startPotision = new Point(LayoutConstants.DEFAULT_START_POSITION_X, LayoutConstants.DEFAULT_START_POSITION_Y);
    }

    public void startLayout() {
       
    }

    public void stopLayout() {
        caches.clear();
        columnMaxHeights.clear();
    }

    /**
     * 
     * DOC LiXP Comment method "getNewLinePotsition".
     * 
     * @return
     */
    private Point getNewLinePotsition(String componentId) {

        column = 0;

        int maxPositionY = safeGetMaxColumnHeight();
        Point position = new Point();
        position.x = startPotision.x;
        position.y = maxPositionY + delta.y;
        columnMaxHeights.set(column, position.y);

        RelativeLayoutNode node = new RelativeLayoutNode(componentId);
        node.setPosition(position);
        caches.put(componentId, node);

        return position;
    }

    private int safeGetMaxColumnHeight() {
        if (columnMaxHeights.size() < column + 1) {
            columnMaxHeights.add(column, 0);
        }
        return columnMaxHeights.get(column);
    }

    public Point getNextPosition(String componentId, String parentId) {

        if (parentId == null) {
            return getNewLinePotsition(componentId);
        }

        RelativeLayoutNode parent = caches.get(parentId);
        if (parent == null) {
            // ????? wrong
            return getNewLinePotsition(componentId);
        }

        RelativeLayoutNode node = new RelativeLayoutNode(componentId);
        Point parentPosition = parent.getPosition();
        Point nextPosition = parent.getNextChildPosition();
        if(nextPosition != null){//Multi-cast
            nextPosition.y += delta.y;
            columnMaxHeights.add(column, nextPosition.y);
        }else{
            column ++;
            int maxPositionY = safeGetMaxColumnHeight();
            nextPosition = new Point();
            if (maxPositionY <= parentPosition.y) {
                nextPosition.x = parentPosition.x + delta.x;
                nextPosition.y = parentPosition.y;
                columnMaxHeights.set(column, nextPosition.y);
            } else {
                nextPosition.x = parentPosition.x + delta.x;
                nextPosition.y = maxPositionY + delta.y;
                columnMaxHeights.set(column, nextPosition.y);
            }
        }
        
        parent.setNextChildPosition(nextPosition);
        node.setPosition(nextPosition);
        caches.put(componentId, node);

        return node.getPosition();
    }
    
}
