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



/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class EnhanceRelativeLayoutNode extends RelativeLayoutNode{

    private int column;

    /**
     * DOC LiXP LayoutNode constructor comment.
     * @param id
     */
    public EnhanceRelativeLayoutNode(String id) {
       super(id);
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
