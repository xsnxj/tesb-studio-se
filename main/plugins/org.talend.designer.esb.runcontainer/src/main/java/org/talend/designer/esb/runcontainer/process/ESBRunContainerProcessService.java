package org.talend.designer.esb.runcontainer.process;

import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.properties.Property;
import org.talend.designer.runprocess.DefaultRunProcessService;
import org.talend.designer.runprocess.IProcessor;

public class ESBRunContainerProcessService extends DefaultRunProcessService {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.designer.runprocess.DefaultRunProcessService#createCodeProcessor(org.talend.core.model.process.IProcess
     * , org.talend.core.model.properties.Property, org.talend.core.language.ECodeLanguage, boolean)
     */
    @Override
    public IProcessor createCodeProcessor(IProcess process, Property property, ECodeLanguage language, boolean filenameFromLabel) {
        return new ESBRuntimeContainerProcessor(process);
    }
}
