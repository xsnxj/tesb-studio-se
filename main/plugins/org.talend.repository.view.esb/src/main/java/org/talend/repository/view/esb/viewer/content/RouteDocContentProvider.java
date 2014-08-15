package org.talend.repository.view.esb.viewer.content;

import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.repository.model.ProjectRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.viewer.content.SubEmptyTopNodeContentProvider;

public class RouteDocContentProvider extends SubEmptyTopNodeContentProvider {

	@Override
	protected RepositoryNode getTopLevelNodeFromProjectRepositoryNode(
			ProjectRepositoryNode projectNode) {
		return projectNode
				.getRootRepositoryNode(CamelRepositoryNodeType.repositoryDocumentationsType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.repository.viewer.content.SingleTopLevelContentProvider#
	 * isRootNodeType(java.lang.Object)
	 */
	@Override
	protected boolean isRootNodeType(Object element) {
		if (element instanceof RepositoryNode) {
			return ERepositoryObjectType.GENERATED
					.equals(((RepositoryNode) element).getContentType());
		} else {
			return false;
		}

	}
}
