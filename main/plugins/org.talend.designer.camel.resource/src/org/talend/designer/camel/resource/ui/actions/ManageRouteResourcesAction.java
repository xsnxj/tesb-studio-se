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
package org.talend.designer.camel.resource.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.designer.ui.editor.CamelEditorUtil;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.designer.ui.editor.CamelProcessEditorInput;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.core.model.properties.Item;
import org.talend.core.model.relationship.RelationshipItemBuilder;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.resource.RouteResourceActivator;
import org.talend.designer.camel.resource.core.util.RouteResourceUtil;
import org.talend.designer.camel.resource.i18n.Messages;
import org.talend.designer.camel.resource.ui.dialogs.ManageRouteResourceDialog;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.ENodeType;
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
        this.setToolTipText(Messages.getString("ManageRouteResourcesAction.Tooltip")); //$NON-NLS-1$
        this.setImageDescriptor(RouteResourceActivator.createImageDesc("icons/manage-route-resource.png"));
    }

    /**
     * if readonly, then it's uneditable else lock it first, and after that unlock if no any other editor is openning.
     */
    protected void doRun() {
        RepositoryNode node = getSelectedRepositoryNode();
        boolean routeEditorIsOpenning = routeEditorIsOpenning(node);

        Item selectedRouteItem = node.getObject().getProperty().getItem();
        ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        ERepositoryStatus status = factory.getStatus(selectedRouteItem);

        boolean readOnly = false;

        /*
         * if it's locked by others, then its readonly else lock it first
         */
        if (ERepositoryStatus.LOCK_BY_OTHER.equals(status)) {
            readOnly = true;
        } else if (!routeEditorIsOpenning) {
            try {
                factory.lock(selectedRouteItem);
            } catch (PersistenceException e) {
                e.printStackTrace();
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }

        // open it
        ManageRouteResourceDialog dlg = new ManageRouteResourceDialog(getWorkbenchWindow().getShell(), selectedRouteItem,
                readOnly);
        dlg.open();
        RelationshipItemBuilder.getInstance().addOrUpdateItem(selectedRouteItem);

        // TESB-15201: Find and update additional properties in open editor
        IWorkbenchWindow workBench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (workBench != null) {
            IWorkbenchPage page = workBench.getActivePage();
            try {
                CamelProcessEditorInput editorInput = new CamelProcessEditorInput((CamelProcessItem) selectedRouteItem, true,
                        true);

                CamelMultiPageTalendEditor editor = (CamelMultiPageTalendEditor) page.findEditor((IEditorInput) editorInput);
                if (editor != null && editor.getProcess().getAdditionalProperties() != null) {
                    String resourcesProp = selectedRouteItem.getProperty().getAdditionalProperties()
                            .get(RouteResourceUtil.ROUTE_RESOURCES_PROP).toString();
                    editor.getProcess().getAdditionalProperties().put(RouteResourceUtil.ROUTE_RESOURCES_PROP, resourcesProp);
                }
            } catch (PersistenceException e) {
                e.printStackTrace();
            }
        }
        /*
         * if it's locked, then release this lock
         */
        if (!readOnly && !routeEditorIsOpenning) {
            try {
                factory.unlock(selectedRouteItem);
            } catch (PersistenceException e) {
                e.printStackTrace();
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse .jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(TreeViewer viewer, IStructuredSelection selection) {
        setEnabled(false);
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        /*
         * if user is readonly , then set enable as false
         */
        if (factory.isUserReadOnlyOnCurrentProject()) {
            return;
        }
        // size should be one
        if (selection.size() != 1) {
            return;
        }
        Object firstElement = ((IStructuredSelection) selection).getFirstElement();
        // should be repository node
        if (!(firstElement instanceof RepositoryNode)) {
            return;
        }
        // should be simple element
        RepositoryNode node = (RepositoryNode) firstElement;
        if (ENodeType.REPOSITORY_ELEMENT != node.getType()) {
            return;
        }

        // should be route process
        ERepositoryObjectType objectType = node.getObjectType();
        if (objectType != CamelRepositoryNodeType.repositoryRoutesType) {
            return;
        }

        // if it's not in current project, then it's disable
        if (!ProjectManager.getInstance().isInCurrentMainProject(node)) {
            return;
        }

        // if it's locked by others, then it's disable
        // if(ERepositoryStatus.LOCK_BY_OTHER.equals(factory.getStatus(node.getObject().getProperty().getItem()))){
        // return;
        // }

        // should be not deleted
        if (node.getObject().getRepositoryStatus() == ERepositoryStatus.DELETED) {
            return;
        }
        // should be last version
        if (!isLastVersion(node)) {
            return;
        }
        setEnabled(true);
    }

    public boolean isVisible() {
        return isEnabled();
    }

    private boolean routeEditorIsOpenning(RepositoryNode node) {
        return CamelEditorUtil.hasEditorOpened(node);
    }

    // private Item getSelectedRouteItem() {
    // RepositoryNode node = getSelectedRepositoryNode();
    // Property property = (Property) node.getObject().getProperty();
    // Item item = property.getItem();
    // return item;
    // }

    private RepositoryNode getSelectedRepositoryNode() {
        IStructuredSelection sselection = (IStructuredSelection) getSelection();
        Object element = sselection.getFirstElement();
        RepositoryNode node = (RepositoryNode) element;
        return node;
    }
}
