package org.talend.designer.esb.components.rs.provider.validation;

import java.util.List;

import org.talend.core.model.process.Element;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.Problem.ProblemStatus;
import org.talend.designer.core.ICheckNodesService;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.views.problems.Problems;

public class CheckMultiRestRequestNodes implements ICheckNodesService {

	private static String COMPONENT_NAME = "tRESTRequest";
	
	public CheckMultiRestRequestNodes() {
	}

	@Override
	public void checkNode(Node node) {
		String name = node.getComponent().getName();
		if(!COMPONENT_NAME.equals(name)){
			return;
		}
		IProcess process = node.getProcess();
		List<? extends INode> nodesOfType = process.getNodesOfType(COMPONENT_NAME);
		if(nodesOfType.size()>1){
			Problems.add(ProblemStatus.ERROR, (Element) node,
					"Only one "+COMPONENT_NAME+" component is allowed at one Job!");
		}
	}

}
