package org.talend.camel.designer.check;

import java.text.MessageFormat;
import java.util.List;

import org.talend.camel.designer.i18n.CamelDesignerMessages;
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
		checkIncomingConnections(node);
		checkErroHandler(node);
		checkIntercept(node);
		return;
	}

	private void checkIntercept(Node node) {
		/*
		 * for intercept, the ROUTE and ROUTE_WHEN connections
		 * can't be exist at the same time.
		 */
		String componentName = node.getComponent().getName();
		if(!"cIntercept".equals(componentName)){
			return;
		}
		List<? extends IConnection> outgoingConnections = node.getOutgoingConnections();
		if(outgoingConnections!=null && outgoingConnections.size()>1){
			Problems.add(ProblemStatus.ERROR, (Element) node,
					CamelDesignerMessages.getString("CheckRouteBuilderNodeService_interceptOutgoingError")); //$NON-NLS-1$
		}
	}

	private void checkIncomingConnections(Node node) {
		//all route component can have only one incoming connection
		List<? extends IConnection> incomingConnections = node.getIncomingConnections();
		if(incomingConnections.size()>1){
			Problems.add(ProblemStatus.ERROR, (Element) node,
					CamelDesignerMessages.getString("CheckRouteBuilderNodeService_incomingConnectionsError")); //$NON-NLS-1$
		}
	}

	private void checkErroHandler(Node node) {
		String componentName = node.getComponent().getName();
		if (!componentName.equals("cErrorHandler")) { //$NON-NLS-1$
			return;
		}
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
					MessageFormat.format(CamelDesignerMessages.getString("CheckRouteBuilderNodeService_errorHandlerCantBeStart"),componentName)); //$NON-NLS-1$
		}
	}

}
