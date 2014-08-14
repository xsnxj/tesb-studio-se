package org.talend.camel.designer.codegen.argument;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;

import org.talend.camel.designer.codegen.util.ProcessUtil;
import org.talend.camel.designer.ui.editor.RouteProcess;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.core.CorePlugin;
import org.talend.core.model.process.IContext;
import org.talend.core.model.process.INode;
import org.talend.core.model.temp.ETypeGen;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.codegen.config.NodesTree;

public class CodeGeneratorArgumentBuilder {

	private CodeGeneratorArgument template;
	private RouteProcess process;
	private NodesTree processTree;

	private BeanInfo beanInfo;

	public CodeGeneratorArgumentBuilder(RouteProcess process, boolean statistics, boolean trace, String[] options) {
		template = new CodeGeneratorArgument();
		setProcess(process);
		template.setStatistics(statistics);
		template.setTrace(trace);
		setOptions(options);

		template.setCheckingSyntax(false);
		template.setPauseTime(CorePlugin.getDefault().getRunProcessService().getPauseTime());

		try {
			beanInfo = Introspector.getBeanInfo(CodeGeneratorArgument.class);
		} catch (IntrospectionException e) {
			ExceptionHandler.process(e);
		}
	}

	public CodeGeneratorArgument build() {
		CodeGeneratorArgument argument = cloneTemplate();
		return argument;
	}

	private CodeGeneratorArgument cloneTemplate() {
		CodeGeneratorArgument argument = new CodeGeneratorArgument();
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			try {
				Object property = propertyDescriptor.getReadMethod().invoke(template);
				propertyDescriptor.getWriteMethod().invoke(argument, property);
			} catch (Exception e) {
				// ExceptionHandler.process(e); ignore.
			}
		}
		return argument;
	}

	public void setOptions(String[] options) {
		if (options == null || options.length < 4) {
			options = new String[] { "", "", "", "" };
		}
		template.setInterpreterPath(options[0]);
		template.setLibPath(options[1]);
		template.setRuntimeFilePath(options[2]);
		template.setCurrentProjectName(options[3]);
	}

	public void setProcess(RouteProcess process) {
		this.process = process;
		template.setJobName(process.getName());
		if (process.getVersion() != null) {
			template.setJobVersion(process.getVersion().replace(".", "_"));
		}
		template.setContextName(process.getContextManager().getDefaultContext().getName());

		template.setIsRunInMultiThread(ProcessUtil.getRunInMultiThread(process));
	}

	public void setContextName(String contextName) {
		template.setContextName(contextName);
	}

	public void setCheckingSyntax(boolean b) {
		template.setCheckingSyntax(b);
	}

	public RouteProcess getProcess() {
		return process;
	}

	public IContext getDefaultContext() {
		return process.getContextManager().getDefaultContext();
	}

	public NodesTree getProcessTree() {
		if (processTree == null) {
			processTree = new NodesTree(process, getGeneratingNodes(), true, ETypeGen.CAMEL);
		}
		return processTree;
	}

	public List<? extends INode> getGraphicalNodes() {
		return process.getGraphicalNodes();
	}

	public List<? extends INode> getGeneratingNodes() {
		return process.getGeneratingNodes();
	}

}
