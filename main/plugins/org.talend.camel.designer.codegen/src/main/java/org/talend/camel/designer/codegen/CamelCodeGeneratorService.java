package org.talend.camel.designer.codegen;

import org.eclipse.core.runtime.jobs.Job;
import org.talend.camel.designer.codegen.i18n.Messages;
import org.talend.camel.designer.ui.editor.RouteProcess;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.language.LanguageManager;
import org.talend.core.model.process.IProcess;
import org.talend.designer.codegen.ICamelCodeGeneratorService;
import org.talend.designer.codegen.ICodeGenerator;
import org.talend.designer.codegen.ISQLPatternSynchronizer;
import org.talend.designer.codegen.ITalendSynchronizer;
import org.talend.designer.codegen.JavaRoutineSynchronizer;
import org.talend.designer.codegen.PerlRoutineSynchronizer;
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
	public ITalendSynchronizer createPerlRoutineSynchronizer() {
		return new PerlRoutineSynchronizer();
	}

	@Override
	public ITalendSynchronizer createJavaRoutineSynchronizer() {
		return new JavaRoutineSynchronizer();
	}

	@Override
	public ITalendSynchronizer createRoutineSynchronizer() {
		ECodeLanguage lan = LanguageManager.getCurrentLanguage();
		if (lan.equals(ECodeLanguage.PERL)) {
			return createPerlRoutineSynchronizer();
		} else if (lan.equals(ECodeLanguage.JAVA)) {
			return createJavaRoutineSynchronizer();
		}
		throw new AssertionError(Messages.getString("CamelCodeGeneratorService.invalidLanguage1")); //$NON-NLS-1$;
	}

	@Override
	public ISQLPatternSynchronizer getSQLPatternSynchronizer() {
		return null;
	}

	@Override
	public ITalendSynchronizer createCamelBeanSynchronizer() {
		return null;
	}

	@Override
	public boolean isInitializingJet() {
		return !CodeGeneratorEmittersPoolFactory.isInitialized()
				&& CodeGeneratorEmittersPoolFactory.isInitializeStart();
	}

	@Override
	public boolean validProcess(IProcess process) {
		return (process != null) && (process instanceof RouteProcess);
	}

}
