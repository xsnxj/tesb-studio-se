// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorPart;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.repository.i18n.Messages;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.ui.actions.EditPropertiesAction;

/**
 * hwang class global comment. Detailled comment
 */
public class ServiceEditPropertiesAction extends EditPropertiesAction {

    public ServiceEditPropertiesAction() {
        super();
        this.setText(Messages.getString("EditPropertiesAction.action.title")); //$NON-NLS-1$
        this.setToolTipText(Messages.getString("EditPropertiesAction.action.toolTipText")); //$NON-NLS-1$
        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.EDIT_ICON));
    }

    protected void doRun() {
        super.doRun();
    }

    /**
     * delete the used routine java file if the routine is renamed. This method is added for solving bug 1321, only
     * supply to talend java version.
     * 
     * @param path
     * @param node
     * @param originalName
     */
    protected void processRoutineRenameOperation(String originalName, RepositoryNode node, IPath path) {
        super.processRoutineRenameOperation(originalName, node, path);
    }

    /**
     * Find the editor that is related to the node.
     * 
     * @param node
     * @return
     */
    protected IEditorPart getCorrespondingEditor(RepositoryNode node) {

        return super.getCorrespondingEditor(node);
    }

    public void init(TreeViewer viewer, IStructuredSelection selection) {
        boolean canWork = selection.size() == 1;
        if (canWork) {
            Object o = ((IStructuredSelection) selection).getFirstElement();
            if (o instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) o;
                switch (node.getType()) {
                case REPOSITORY_ELEMENT:
                    if (node.getObjectType() == ESBRepositoryNodeType.SERVICES) {
                        canWork = true;
                    } else {
                        canWork = false;
                    }
                    break;
                default:
                    canWork = false;
                    break;
                }
                if (canWork) {
                    canWork = (node.getObject().getRepositoryStatus() != ERepositoryStatus.DELETED);
                }
                if (canWork) {
                    canWork = isLastVersion(node);
                }
            }
        }
        setEnabled(canWork);
    }

}
