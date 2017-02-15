package org.talend.designer.esb.runcontainer.process;

import org.talend.core.model.process.IProcess2;
import org.talend.designer.runprocess.RunProcessContext;
import org.talend.designer.runprocess.RunProcessContextManager;

public class ESBRunContainerProcessContextManager extends RunProcessContextManager {

    public ESBRunContainerProcessContextManager() {
        super();
    }

    @Override
    protected RunProcessContext getRunProcessContext(IProcess2 p2) {
        return new ESBRunContainerProcessContext(p2);
    }
}
