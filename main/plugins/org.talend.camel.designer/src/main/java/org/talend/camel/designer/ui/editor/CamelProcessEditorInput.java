// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.ui.editor.JobEditorInput;
import org.talend.designer.core.ui.editor.process.Process;
import org.talend.designer.core.ui.projectsetting.ProjectSettingManager;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelProcessEditorInput extends JobEditorInput {

    /**
     * DOC guanglong.du CamelProcessEditorInput constructor comment.
     * 
     * @param item
     * @param load
     * @param lastVersion
     * @param readonly
     * @throws PersistenceException
     */
    public CamelProcessEditorInput(CamelProcessItem item, boolean load, Boolean lastVersion, Boolean readonly)
            throws PersistenceException {
        super(item, load, lastVersion, readonly);
    }

    public CamelProcessEditorInput(CamelProcessItem processItem, boolean load) throws PersistenceException {
        this(processItem, load, null, null);
    }

    public CamelProcessEditorInput(CamelProcessItem processItem, boolean load, Boolean lastVersion) throws PersistenceException {
        this(processItem, load, lastVersion, null);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.repository.editor.JobEditorInput#createProcess()
     */
    @Override
    protected Process createProcess() {
        return new RouteProcess(getItem().getProperty());
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.repository.editor.JobEditorInput#saveProcessBefore()
     */
    @Override
    protected void saveProcessBefore() {
        ProjectSettingManager.defaultUseProjectSetting(getLoadedProcess());
    }

    @Override
    public Process getLoadedProcess() {
        return (Process) loadedProcess;
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == RepositoryNode.class) {
            return getRepositoryNode();
        }
        return super.getAdapter(adapter);
    }
}
