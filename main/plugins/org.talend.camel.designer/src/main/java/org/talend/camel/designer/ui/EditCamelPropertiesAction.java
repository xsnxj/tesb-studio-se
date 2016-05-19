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
package org.talend.camel.designer.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.designer.ui.wizards.EditRoutePropertiesWizard;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.metadata.managment.ui.wizard.PropertiesWizard;
import org.talend.repository.ProjectManager;
import org.talend.repository.i18n.Messages;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.ui.actions.EditPropertiesAction;

/**
 * smallet class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40 +0000 (ven., 29 sept. 2006) nrousseau $
 * 
 */
public class EditCamelPropertiesAction extends EditPropertiesAction {

    public EditCamelPropertiesAction() {
        super();
        this.setText(Messages.getString("EditPropertiesAction.action.title")); //$NON-NLS-1$
        this.setToolTipText(Messages.getString("EditPropertiesAction.action.toolTipText")); //$NON-NLS-1$
        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.EDIT_ICON));
    }

    protected void doRun() {
        IWizard wizard = createWizard();
        if (wizard == null) {
            return;
        } else {
            WizardDialog dlg = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
            dlg.open();
        }
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
                    if (node.getObjectType() == CamelRepositoryNodeType.repositoryRoutesType) {
                        canWork = true;
                    } else if (node.getObjectType() == CamelRepositoryNodeType.repositoryBeansType) {
                        Item item = node.getObject().getProperty().getItem();
                        if (item instanceof BeanItem) {
                            canWork = true;
                        } else {
                            canWork = false;
                        }
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

    public IWizard createWizard() {
        ISelection selection = getSelection();
        Object obj = ((IStructuredSelection) selection).getFirstElement();
        RepositoryNode node = (RepositoryNode) obj;

        IRepositoryViewObject object = node.getObject();
        if (getNeededVersion() != null && !object.getVersion().equals(getNeededVersion())) {
            try {
                object = ProxyRepositoryFactory.getInstance().getSpecificVersion(
                        new Project(ProjectManager.getInstance().getProject(object.getProperty().getItem())),
                        object.getProperty().getId(), getNeededVersion(), false);
            } catch (PersistenceException e) {
                ExceptionHandler.process(e);
            }
        }
        IPath path = RepositoryNodeUtilities.getPath(node);
        String originalName = object.getLabel();
        PropertiesWizard wizard = new EditRoutePropertiesWizard(object, path, getNeededVersion() == null);
        // wizard.setProcessConverter(new MapRedProcessConverter(object));
        return wizard;
    }

}
