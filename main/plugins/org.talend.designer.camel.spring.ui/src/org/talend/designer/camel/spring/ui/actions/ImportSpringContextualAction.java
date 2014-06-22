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
package org.talend.designer.camel.spring.ui.actions;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.ui.swt.actions.ITreeContextualAction;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.spring.ui.CamelSpringUIPlugin;
import org.talend.designer.camel.spring.ui.i18n.Messages;
import org.talend.designer.camel.spring.ui.wizards.ImportSpringXMLWizard;
import org.talend.designer.core.DesignerPlugin;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.IRepositoryService;
import org.talend.repository.model.RepositoryConstants;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.AContextualAction;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class ImportSpringContextualAction extends AContextualAction implements ITreeContextualAction {

    public ImportSpringContextualAction() {
        super();
        this.setText(Messages.getString("ImportSpringContextualAction_actionText")); //$NON-NLS-1$
        this.setToolTipText(Messages.getString("ImportSpringContextualAction_actionTooltip")); //$NON-NLS-1$
        this.setImageDescriptor(CamelSpringUIPlugin.getDefault().getImageDescriptor("icons/import.gif")); //$NON-NLS-1$
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
                ERepositoryObjectType nodeType = (ERepositoryObjectType) node.getProperties(EProperties.CONTENT_TYPE);
                if (nodeType != CamelRepositoryNodeType.repositoryRoutesType) {
                    canWork = false;
                }
                if (node.getObject() != null && node.getObject().getProperty().getItem().getState().isDeleted()) {
                    canWork = false;
                }
                break;
            default:
                canWork = false;
            }
            if (canWork && !ProjectManager.getInstance().isInCurrentMainProject(node)) {
                canWork = false;
            }
        }
        setEnabled(canWork);
    }

    @Override
    protected void doRun() {

        IRepositoryNode node = null;
        ImportSpringXMLWizard processWizard = null;
        if (isToolbar()) {
            processWizard = new ImportSpringXMLWizard(null);
        } else {
            ISelection selection = getSelection();
            if (selection == null) {
                return;
            }
            Object obj = ((IStructuredSelection) selection).getFirstElement();
            node = (IRepositoryNode) obj;
            // ItemCacheManager.clearCache();

            IRepositoryService service = DesignerPlugin.getDefault().getRepositoryService();
            IPath path = service.getRepositoryPath((RepositoryNode) node);
            if (RepositoryConstants.isSystemFolder(path.toString())) {
                // Not allowed to create in system folder.
                return;
            }

            processWizard = new ImportSpringXMLWizard(path);
        }

        WizardDialog dlg = new WizardDialog(Display.getCurrent().getActiveShell(), processWizard);
        dlg.open();

    }

}
