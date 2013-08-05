// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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
import org.talend.designer.core.ui.editor.ITalendJobEditor;
import org.talend.designer.core.ui.editor.process.Process;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelTalendEditor extends AbstractTalendEditor implements ITalendJobEditor {

    private static CamelComponentsHandler CAMEL_COMPONENTS_HANDLER;

	public CamelTalendEditor() {
        this(false);

    }

    public CamelTalendEditor(boolean readOnly) {
        super(readOnly);
    }

    @Override
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

    @Override
    public CamelMultiPageTalendEditor getParent() {
        return (CamelMultiPageTalendEditor) super.getParent();
    }

    @Override
    public void doSaveAs() {
        SaveAsRoutesAction saveAsAction = new SaveAsRoutesAction(this);
        saveAsAction.run();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.ui.editor.AbstractTalendEditor#initComponentsHandler()
     */
    @Override
    protected IComponentsHandler initComponentsHandler() {
    	if(CAMEL_COMPONENTS_HANDLER == null){
	    	synchronized (CamelTalendEditor.class) {
	    		CAMEL_COMPONENTS_HANDLER = new CamelComponentsHandler();
			}
    	}
        return CAMEL_COMPONENTS_HANDLER;
    }
}
