package org.talend.camel.designer.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Button;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.properties.tab.IDynamicProperty;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.ui.editor.cmd.PropertyChangeCommand;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.properties.controllers.ProcessController;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.ProjectRepositoryNode;
import org.talend.repository.ui.dialog.RepositoryReviewDialog;
import org.talend.repository.ui.utils.RecombineRepositoryNodeUtil;

public class RouteInputProcessTypeController extends ProcessController {

	public RouteInputProcessTypeController(IDynamicProperty dp) {
		super(dp);
	}

	protected Command createButtonCommand(Button button) {
		String procssId = null;
		if (elem != null && elem instanceof Node) {
			Node runJobNode = (Node) elem;
			procssId = runJobNode.getProcess().getId();
		}
		RepositoryReviewDialog dialog = new RepositoryReviewDialog(
				(button).getShell(), ERepositoryObjectType.PROCESS, procssId,
				new ViewerFilter[] { new RouteInputContainedFilter() });
		if (dialog.open() == RepositoryReviewDialog.OK) {
			IRepositoryViewObject repositoryObject = dialog.getResult()
					.getObject();
			final Item item = repositoryObject.getProperty().getItem();
			String id = item.getProperty().getId();

			String paramName = (String) button.getData(PARAMETER_NAME);
			return new PropertyChangeCommand(elem, paramName, id);
		}
		return null;
	}

	private class RouteInputContainedFilter extends ViewerFilter {

		private List<IRepositoryNode> routeInputContainedJobs = new ArrayList<IRepositoryNode>();

		private RouteInputContainedFilter() {
			/*
			 * find all RouteInput contained Jobs first
			 */
			IRepositoryNode jobRoot = RecombineRepositoryNodeUtil
					.getFixingTypesInputRoot(
							ProjectRepositoryNode.getInstance(),
							Arrays.asList(ERepositoryObjectType.PROCESS));
			addAllRouteInputContainedJob(routeInputContainedJobs, jobRoot);
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (!(element instanceof IRepositoryNode)) {
				return false;
			}
			IRepositoryNode node = (IRepositoryNode) element;
			ENodeType type = node.getType();
			/*
			 * if it's an element and contains a tRouteInput then selected
			 */
			if (type == ENodeType.REPOSITORY_ELEMENT) {
				for (IRepositoryNode rn : routeInputContainedJobs) {
					if (rn == node) {
						return true;
					}
				}
				return false;
			}
			/*
			 * if it's a container node, and some child of it contains a
			 * tRouteInput then selected
			 */
			else {
				for (IRepositoryNode rn : routeInputContainedJobs) {
					if (isAncestor(rn, node)) {
						return true;
					}
				}
			}
			return false;
		}

		private boolean isAncestor(IRepositoryNode jobNode,
				IRepositoryNode ancestor) {
			if (jobNode == null || ancestor == null) {
				return false;
			}
			IRepositoryNode current = jobNode;
			while (current != ancestor) {
				if (current == null) {
					return false;
				}
				current = current.getParent();
			}
			return true;
		}

		/**
		 * find all Jobs which contains a tRouteInput component
		 * 
		 * @param routeInputContainedJobs
		 * @param jobNode
		 */
		private void addAllRouteInputContainedJob(
				List<IRepositoryNode> routeInputContainedJobs,
				IRepositoryNode jobNode) {
			if (jobNode == null) {
				return;
			}
			if (jobNode.getType() == ENodeType.REPOSITORY_ELEMENT) {
				try {
					Item item = jobNode.getObject().getProperty().getItem();
					if (!(item instanceof ProcessItem)) {
						return;
					}
					ProcessItem pi = (ProcessItem) item;
					EList<?> nodes = pi.getProcess().getNode();
					Iterator<?> iterator = nodes.iterator();
					while (iterator.hasNext()) {
						Object next = iterator.next();
						if (!(next instanceof NodeType)) {
							continue;
						}
						NodeType nt = (NodeType) next;
						String componentName = nt.getComponentName();
						if ("tRouteInput".equals(componentName)) {
							routeInputContainedJobs.add(jobNode);
							return;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			List<IRepositoryNode> children = jobNode.getChildren();
			for (IRepositoryNode child : children) {
				addAllRouteInputContainedJob(routeInputContainedJobs, child);
			}
		}
	}
}
