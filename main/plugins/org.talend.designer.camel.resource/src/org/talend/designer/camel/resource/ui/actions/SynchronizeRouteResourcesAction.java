// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.resource.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.swt.actions.ITreeContextualAction;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.resource.core.util.RouteResourceUtil;
import org.talend.designer.camel.resource.i18n.Messages;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.AContextualAction;

/**
 * @author xpli
 * 
 */
public class SynchronizeRouteResourcesAction extends AContextualAction
		implements ITreeContextualAction {

	public SynchronizeRouteResourcesAction() {
		setText(Messages.getString("SynchronizeRouteResourcesAction.Title")); //$NON-NLS-1$
	}

	private void copyChildResources(IRepositoryNode n) {
		for (IRepositoryNode node : n.getChildren()) {
			copyResources(node);
		}

	}

	private void copyResources(IRepositoryNode n) {
		// Copy itself
		IRepositoryViewObject object = n.getObject();
		if (object != null) {
			Property property = (Property) object.getProperty();

			Item item = property.getItem();
			if (item instanceof RouteResourceItem) {
				RouteResourceItem rrItem = (RouteResourceItem) item;
				try {
					RouteResourceUtil.copyResources(rrItem);
				} catch (CoreException e) {
					ExceptionHandler.process(e);
				}
			}

		}

		if (n.hasChildren()) {
			copyChildResources(n);
		}

	}

	@Override
	protected void doRun() {
		IRepositoryNode node = null;
		ISelection selection = getSelection();
		if (selection == null) {
			return;
		}
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		node = (IRepositoryNode) obj;
		copyResources(node);
	}

	public void init(TreeViewer viewer, IStructuredSelection selection) {
		boolean canWork = !selection.isEmpty() && selection.size() == 1;
		IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
		if (factory.isUserReadOnlyOnCurrentProject()) {
			canWork = false;
		}
		if (canWork) {
			Object o = selection.getFirstElement();
			RepositoryNode node = (RepositoryNode) o;
			switch (node.getType()) {
			case SIMPLE_FOLDER:
			case SYSTEM_FOLDER:
				ERepositoryObjectType nodeType = (ERepositoryObjectType) node
						.getProperties(EProperties.CONTENT_TYPE);
				if (nodeType != CamelRepositoryNodeType.repositoryRouteResourceType) {
					canWork = false;
				}
				if (node.getObject() != null
						&& node.getObject().getProperty().getItem().getState()
								.isDeleted()) {
					canWork = false;
				}
				break;
			default:
				canWork = false;
			}
			if (canWork
					&& !ProjectManager.getInstance().isInCurrentMainProject(
							node)) {
				canWork = false;
			}
		}
		setEnabled(canWork);
	}
}
