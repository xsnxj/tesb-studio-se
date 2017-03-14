// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2013 Talend – www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.designer.esb.runcontainer.export;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.talend.core.CorePlugin;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.runtime.process.LastGenerationInfo;
import org.talend.designer.core.IDesignerCoreService;
import org.talend.designer.esb.runcontainer.util.StatPortChecker;
import org.talend.designer.runprocess.IProcessor;
import org.talend.designer.runprocess.ProcessorException;
import org.talend.designer.runprocess.ProcessorUtilities;
import org.talend.designer.runprocess.maven.MavenJavaProcessor;
import org.talend.repository.documentation.ExportFileResource;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JobJavaScriptOSGIForESBManager;

/**
 * DOC yyan class global comment. Detailled comment
 * 
 * For esb run container to replace stat port
 */
public class JobJavaScriptOSGIForESBRuntimeManager extends JobJavaScriptOSGIForESBManager {

    public JobJavaScriptOSGIForESBRuntimeManager(Map<ExportChoice, Object> exportChoiceMap, String contextName, String launcher,
            int statisticPort, int tracePort) {
        super(exportChoiceMap, contextName, launcher, statisticPort, tracePort);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JobJavaScriptOSGIForESBManager#getExportResources
     * (org.talend.repository.documentation.ExportFileResource[], java.lang.String[])
     */
    @Override
    public List<ExportFileResource> getExportResources(ExportFileResource[] processes, String... codeOptions)
            throws ProcessorException {
        // TODO Auto-generated method stub
        List<ExportFileResource> exportResources = super.getExportResources(processes, codeOptions);
        return exportResources;
    }

    @Override
    protected IProcess generateJobFiles(ProcessItem process, String contextName, String version, boolean statistics,
            boolean trace, boolean applyContextToChildren, IProgressMonitor monitor) throws ProcessorException {
        LastGenerationInfo.getInstance().getUseDynamicMap().clear();

        // TODO stat port tracer, to replace the port, need to improve efficiency
        IDesignerCoreService service = CorePlugin.getDefault().getDesignerCoreService();
        IProcess currentProcess = service.getProcessFromProcessItem(process);
        IProcessor processor = ProcessorUtilities.getProcessor(currentProcess, null);
        StatPortChecker checker = null;
        if (processor instanceof MavenJavaProcessor) {
            MavenJavaProcessor mvnProcessor = (MavenJavaProcessor) processor;
            checker = new StatPortChecker(processor.getCodeProject().getFile(mvnProcessor.getSrcCodePath()).getLocation(),
                    statisticPort, tracePort);
            new Thread(checker, "StatPort Worker").start();
            mvnProcessor = null;
        }

        IProcessor processor2 = ProcessorUtilities.generateCode(process, contextName, version, statistics, trace,
                applyContextToChildren, isOptionChoosed(ExportChoice.needContext), monitor);

        if (checker != null) {
            checker.stop();
        }
        return processor2.getProcess();
    }
}
