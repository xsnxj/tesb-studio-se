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
package org.talend.designer.esb.runcontainer.process;

import org.talend.core.model.process.IProcess2;
import org.talend.designer.runprocess.RunProcessContext;
import org.talend.designer.runprocess.RunProcessContextManager;

public class RunContainerProcessContextManager extends RunProcessContextManager {

    public RunContainerProcessContextManager() {
        super();
    }

    @Override
    protected RunProcessContext getRunProcessContext(IProcess2 p2) {
        return new RunContainerProcessContext(p2);
    }
}
