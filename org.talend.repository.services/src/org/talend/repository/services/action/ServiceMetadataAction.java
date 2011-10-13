// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.services.action;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.ui.ServiceMetadataDialog;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.ui.actions.AContextualAction;

/**
 * Action used to export job scripts. <br/>
 * 
 * $Id: ExportJobScriptAction.java 1 2006-12-13 下午03:12:05 bqian
 * 
 */
public class ServiceMetadataAction extends AContextualAction {

    protected static final String ACTION_LABEL = "Edit Service Metadata";

    private IStructuredSelection selection;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse.jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(TreeViewer viewer, IStructuredSelection selection) {
        boolean canWork = true;
        if (selection.isEmpty() || (selection.size() > 1)) {
            setEnabled(false);
            return;
        }

        @SuppressWarnings("unchecked")
        List<RepositoryNode> nodes = (List<RepositoryNode>) selection.toList();
        for (RepositoryNode node : nodes) {
			if (node.getProperties(EProperties.CONTENT_TYPE) != ESBRepositoryNodeType.SERVICEPORT) {
                canWork = false;
                break;
            } else if (canWork && node.getObject() != null
                    && ProxyRepositoryFactory.getInstance().getStatus(node.getObject()) == ERepositoryStatus.DELETED) {
                canWork = false;
                break;
            } else {
                this.selection = selection;
            }
        }
        setEnabled(canWork);
    }

    public boolean isVisible() {
        return isEnabled();
    }

    public ServiceMetadataAction() {
        super();
        this.setText(ACTION_LABEL);
        this.setToolTipText(ACTION_LABEL);
        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.EDIT_ICON));
    }

    protected void doRun() {
        IWorkbenchWindow window = this.getViewPart().getViewSite().getWorkbenchWindow();
        ServicePort port = null;
        ServiceItem serviceItem = null;
        List<RepositoryNode> nodes = (List<RepositoryNode>) selection.toList();
        for (RepositoryNode node : nodes) {
            serviceItem = (ServiceItem) node.getParent().getObject().getProperty().getItem();
            ServiceConnection serviceConnection = (ServiceConnection) serviceItem.getConnection();
            EList<ServicePort> listPort = serviceConnection.getServicePort();
            for (ServicePort cport : listPort) {
				if (cport.getName().equals(node.getObject().getLabel())) {
                    port = cport;
                }
            }
        }

        Dialog dialog = new ServiceMetadataDialog(window, serviceItem, port);
        dialog.open();
    }
}
