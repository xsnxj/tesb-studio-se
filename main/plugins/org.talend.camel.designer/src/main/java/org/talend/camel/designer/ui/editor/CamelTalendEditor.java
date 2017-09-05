// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.ui.editor;

import org.talend.camel.designer.ui.SaveAsRoutesAction;
import org.talend.core.model.components.IComponentsHandler;
import org.talend.designer.core.ui.editor.AbstractTalendEditor;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelTalendEditor extends AbstractTalendEditor {

    private static CamelComponentsHandler CAMEL_COMPONENTS_HANDLER;

    public CamelTalendEditor() {
        super();
    }

    public CamelTalendEditor(boolean readOnly) {
        super(readOnly);
    }

    @Override
    public void doSaveAs() {
        SaveAsRoutesAction saveAsAction = new SaveAsRoutesAction(this.getParent());
        saveAsAction.run();
    }

    protected IComponentsHandler initComponentsHandler() {
        if(CAMEL_COMPONENTS_HANDLER == null){
            synchronized (CamelTalendEditor.class) {
                CAMEL_COMPONENTS_HANDLER = new CamelComponentsHandler();
            }
        }
        return CAMEL_COMPONENTS_HANDLER;
    }

}
