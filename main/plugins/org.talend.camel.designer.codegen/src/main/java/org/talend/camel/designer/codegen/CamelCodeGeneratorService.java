package org.talend.camel.designer.codegen;

import org.eclipse.core.runtime.jobs.Job;
import org.talend.camel.designer.ui.editor.RouteProcess;
import org.talend.core.model.process.IProcess;
import org.talend.designer.codegen.ICamelCodeGeneratorService;
import org.talend.designer.codegen.ICodeGenerator;
import org.talend.designer.codegen.model.CodeGeneratorEmittersPoolFactory;

public class CamelCodeGeneratorService implements ICamelCodeGeneratorService {

    @Override
    public Job initializeTemplates() {
        return null;
    }

    @Override
    public Job refreshTemplates() {
        return null;
    }

    @Override
    public ICodeGenerator createCodeGenerator() {
        throw new AssertionError("This code generator only use to generate process specific generator");
    }

    @Override
    public ICodeGenerator createCodeGenerator(IProcess process, boolean statistics, boolean trace, String... options) {
        if (!validProcess(process)) {
            return null;
        }
        return new CamelCodeGenerator((RouteProcess) process, statistics, trace, options);
    }

    @Override
    public boolean isInitializingJet() {
        return !CodeGeneratorEmittersPoolFactory.isInitialized() && CodeGeneratorEmittersPoolFactory.isInitializeStart();
    }

    @Override
    public boolean validProcess(IProcess process) {
        return process != null && process instanceof RouteProcess;
    }

}
