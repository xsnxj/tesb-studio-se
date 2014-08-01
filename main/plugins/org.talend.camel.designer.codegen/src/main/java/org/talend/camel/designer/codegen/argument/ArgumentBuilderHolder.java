package org.talend.camel.designer.codegen.argument;

import java.util.List;

import org.talend.camel.designer.ui.editor.RouteProcess;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.NodesTree;

public abstract class ArgumentBuilderHolder {

	protected final CodeGeneratorArgumentBuilder argumentBuilder;
	protected final RouteProcess process;
	protected final NodesTree processTree;
	protected final List<INode> rootNodes;

	public ArgumentBuilderHolder(CodeGeneratorArgumentBuilder argumentBuilder) {
		this.argumentBuilder = argumentBuilder;
		process = argumentBuilder.getProcess();
		processTree = argumentBuilder.getProcessTree();
		rootNodes = processTree.getRootNodes();
	}

	public CodeGeneratorArgumentBuilder getArgumentBuilder() {
		return argumentBuilder;
	}
}
