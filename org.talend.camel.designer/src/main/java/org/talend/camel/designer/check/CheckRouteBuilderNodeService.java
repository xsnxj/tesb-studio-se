package org.talend.camel.designer.check;

import java.util.List;

import org.talend.core.model.process.Element;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.Problem.ProblemStatus;
import org.talend.designer.core.ICheckNodesService;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.views.problems.Problems;

public class CheckRouteBuilderNodeService implements ICheckNodesService {

	public CheckRouteBuilderNodeService() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.talend.designer.core.ICheckNodesService#checkNode(org.talend.designer
	 * .core.ui.editor.nodes.Node)
	 */
	public void checkNode(Node node) {
		String componentName = node.getComponent().getName();
		if (componentName.equals("cErrorHandler")) {
			checkErroHandler(node);
		}
		return;
	}

	private void checkErroHandler(Node node) {
		List<? extends IConnection> outgoingConnections = node
				.getOutgoingConnections();
		// if no output connection, then it's ok
		if (outgoingConnections == null || outgoingConnections.size() == 0) {
			return;
		}
		// else if no input connection, it's error
		List<? extends IConnection> incomingConnections = node
				.getIncomingConnections();
		if (incomingConnections == null || incomingConnections.size() == 0) {
			Problems.add(ProblemStatus.ERROR, (Element) node,
					"The cErrorHandler can't be a start Node");
		}
	}

}
