// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.esb.runcontainer.core;

import org.talend.designer.esb.runcontainer.process.RunContainerProcessContextManager;
import org.talend.designer.runprocess.IESBRunContainerService;
import org.talend.designer.runprocess.RunProcessContext;
import org.talend.designer.runprocess.RunProcessContextManager;
import org.talend.designer.runprocess.ui.JobJvmComposite;
import org.talend.designer.runprocess.ui.TargetExecComposite;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 * TESB-18750, Locally ESB runtime server service
 */
public class LocalESBRunContainerService implements IESBRunContainerService {

    private static final String ESB_RUNTIME_ITEM = "ESB Runtime"; //$NON-NLS-1$

    private RunProcessContext esbProcessContext;

    private RunProcessContextManager defaultContextManager;

    private RunContainerProcessContextManager runtimeContextManager;

    private int index = 0;

    /*
     * This method is to add a combo box(if do not have) in the <b>target exec</b> run tab view for ESB runtime server
     * 
     * @see org.talend.designer.runprocess.IESBRunContainerService#addRuntimeServer(org.talend.designer.runprocess.ui.
     * TargetExecComposite, org.talend.designer.runprocess.ui.JobJvmComposite)
     */
    @Override
    public void addRuntimeServer(TargetExecComposite targetExecComposite, JobJvmComposite jobComposite) {
    }

    @Override
    public boolean isESBProcessContextManager(RunProcessContextManager contextManager) {
        return contextManager instanceof RunContainerProcessContextManager;
    }
}
