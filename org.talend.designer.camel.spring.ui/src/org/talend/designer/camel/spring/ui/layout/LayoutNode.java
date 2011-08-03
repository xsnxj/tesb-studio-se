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

import org.eclipse.draw2d.geometry.Point;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class LayoutNode {

    private String id;
    
    private Point position;
    
    private Point nextChildPosition;

    private Point delta;
    /**
     * DOC LiXP LayoutNode constructor comment.
     * @param id
     */
    public LayoutNode(String id) {
        this.id = id;
    }
    
    private void calculateNextChildPosition(int maxPositionY){
        int x, y;
        if(nextChildPosition == null){//first time get child position
            x = position.x + delta.x;
            y = position.y;
        }else{//already has children, so it's multi-cast
            x = nextChildPosition.x;
            y = maxPositionY + delta.y;
        }
        nextChildPosition = new Point(x, y);
    }

    
    /**
     * Getter for id.
     * @return the id
     */
    public String getId() {
        return id;
    }

    
    /**
     * Sets the id.
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    
    /**
     * Getter for position.
     * @return the position
     */
    public Point getPosition() {
        return position;
    }

    
    /**
     * Sets the position.
     * @param position the position to set
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    
    /**
     * Getter for nextChildPosition.
     * @return the nextChildPosition
     */
    public Point getNextChildPosition(int maxPositionY) {
        calculateNextChildPosition(maxPositionY);
        return nextChildPosition;
    }

    
    /**
     * Sets the nextChildPosition.
     * @param nextChildPosition the nextChildPosition to set
     */
    public void setNextChildPosition(Point nextChildPosition) {
        this.nextChildPosition = nextChildPosition;
    }

    /**
     * Sets the delta.
     * @param delta the delta to set
     */
    public void setDelta(Point delta) {
        this.delta = delta;
    }

    /**
     * Getter for delta.
     * @return the delta
     */
    public Point getDelta() {
        return delta;
    }
    
}
