package org.talend.repository.services.ui.action;

import java.util.ArrayList;
import java.util.List;

import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.ProjectRepositoryNode;
import org.talend.repository.model.RepositoryNode;

public class ContextNodeRetriever {

	public static String[] getAllContext(RepositoryNode node) {
		ProjectRepositoryNode projectNode = (ProjectRepositoryNode) node
				.getRoot();
		RepositoryNode contextNode = projectNode.getContextNode();
		List<String> contexts = new ArrayList<String>();
		contexts.add("Default");
		retrieveAllContexts(contextNode, contexts);
		return contexts.toArray(new String[0]);
	}

	private static void retrieveAllContexts(RepositoryNode contextNode,
			List<String> contexts) {
		if (contextNode.getType() == IRepositoryNode.ENodeType.REPOSITORY_ELEMENT) {
			contexts.add(contextNode.getObject().getLabel());
		} else if (contextNode.hasChildren()) {
			List<IRepositoryNode> children = contextNode.getChildren();
			for (IRepositoryNode n : children) {
				retrieveAllContexts((RepositoryNode) n, contexts);
			}
		}
	}
}
