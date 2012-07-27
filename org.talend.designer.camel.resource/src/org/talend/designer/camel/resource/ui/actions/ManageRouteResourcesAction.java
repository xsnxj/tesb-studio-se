// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.designer.camel.resource.i18n.Messages;
import org.talend.designer.camel.resource.ui.dialogs.ManageRouteResourceDialog;
import org.talend.repository.model.BinRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.AContextualAction;

/**
 * @author xpli
 * 
 */
public class ManageRouteResourcesAction extends AContextualAction {

	public ManageRouteResourcesAction() {
		super();
		this.setText(Messages.getString("ManageRouteResourcesAction.Title")); //$NON-NLS-1$
		this.setToolTipText(Messages
				.getString("ManageRouteResourcesAction.Tooltip")); //$NON-NLS-1$
		this.setImageDescriptor(ImageProvider
				.getImageDesc(ECamelCoreImage.ROUTE_RESOURCE_ICON));
	}

	protected void doRun() {
		ManageRouteResourceDialog dlg = new ManageRouteResourceDialog(
				getWorkbenchWindow().getShell(), this.getSelection());
		dlg.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse
	 * .jface.viewers.TreeViewer,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(TreeViewer viewer, IStructuredSelection selection) {
		boolean canWork = true;
		if (selection.isEmpty()) {
			setEnabled(false);
			return;
		}
		@SuppressWarnings("unchecked")
		List<RepositoryNode> nodes = (List<RepositoryNode>) selection.toList();
		for (RepositoryNode node : nodes) {
			if (node.getType() != ENodeType.REPOSITORY_ELEMENT
					|| node.getProperties(EProperties.CONTENT_TYPE) != CamelRepositoryNodeType.repositoryRoutesType) {
				canWork = false;
				break;
			}
			RepositoryNode parent = node.getParent();
			if (canWork && parent != null
					&& parent instanceof BinRepositoryNode) {
				canWork = false;
				break;
			}
		}
		setEnabled(canWork);
	}

	public boolean isVisible() {
		return isEnabled();
	}
}
