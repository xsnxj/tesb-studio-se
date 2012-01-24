// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import org.talend.designer.core.ui.action.SaveAsProcessAction;
import org.talend.designer.core.ui.editor.AbstractTalendEditor;
import org.talend.designer.core.ui.editor.ITalendJobEditor;
import org.talend.designer.core.ui.editor.process.Process;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelTalendEditor extends AbstractTalendEditor implements ITalendJobEditor {

    public CamelTalendEditor() {
        this(false);

    }

    public CamelTalendEditor(boolean readOnly) {
        super(readOnly);
    }

    public Process getProcess() {
        return (Process) super.getProcess();
    }

    @Override
    public Object getAdapter(final Class type) {
        return super.getAdapter(type);
    }

    public void setParent(CamelMultiPageTalendEditor multiPageTalendEditor) {
        super.setParent(multiPageTalendEditor);
    }

    public CamelMultiPageTalendEditor getParent() {
        return (CamelMultiPageTalendEditor) super.getParent();
    }

    @Override
    public void doSaveAs() {
        SaveAsProcessAction saveAsAction = new SaveAsProcessAction(this);
        saveAsAction.run();
    }
}
