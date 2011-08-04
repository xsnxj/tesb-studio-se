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
public interface ILayoutManager {

    void startLayout();
    
    void stopLayout();
    
    Point getNextPosition(String componentId, String parentId);
}
