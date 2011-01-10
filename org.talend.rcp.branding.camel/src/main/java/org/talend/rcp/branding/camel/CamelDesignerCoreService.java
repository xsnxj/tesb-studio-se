// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.rcp.branding.camel;

import org.eclipse.jface.action.IAction;
import org.talend.designer.core.ICamelDesignerCoreService;
import org.talend.rcp.branding.camel.ui.CreateCamelProcess;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelDesignerCoreService implements ICamelDesignerCoreService {

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.core.ICamelDesignerCoreService#getCreateProcessAction(boolean)
     */
    public IAction getCreateProcessAction(boolean isToolbar) {
        return new CreateCamelProcess(isToolbar);
    }

}
