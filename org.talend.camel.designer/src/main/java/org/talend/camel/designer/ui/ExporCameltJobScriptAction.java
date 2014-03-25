// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.talend.camel.designer.i18n.Messages;
import org.talend.camel.designer.ui.wizards.JobCamelScriptsExportWizard;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.repository.model.BinRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.AContextualAction;

/**
 * Action used to export job scripts. <br/>
 * 
 * $Id: ExportJobScriptAction.java 1 2006-12-13 下午03:12:05 bqian
 * 
 */
public class ExporCameltJobScriptAction extends AContextualAction {

    protected static final String EXPORTJOBSCRIPTS = Messages.getString("ExportJobScriptAction.buildRoute"); //$NON-NLS-1$

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse.jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(TreeViewer viewer, IStructuredSelection selection) {
        boolean canWork = true;
        if (selection.isEmpty()) {
            setEnabled(false);
            return;
        }
        List<RepositoryNode> nodes = (List<RepositoryNode>) selection.toList();
        for (RepositoryNode node : nodes) {
            if (node.getType() != ENodeType.REPOSITORY_ELEMENT
                    || node.getProperties(EProperties.CONTENT_TYPE) != CamelRepositoryNodeType.repositoryRoutesType) {
                canWork = false;
                break;
            }
            RepositoryNode parent = node.getParent();
            if (canWork && parent != null && parent instanceof BinRepositoryNode) {
                canWork = false;
                break;
            }
        }
        setEnabled(canWork);
    }

    public boolean isVisible() {
        return isEnabled();
    }

    public ExporCameltJobScriptAction() {
        super();
        this.setText(EXPORTJOBSCRIPTS);
        this.setToolTipText(EXPORTJOBSCRIPTS);
        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.EXPORT_JOB_ICON));
    }

    protected void doRun() {
        JobCamelScriptsExportWizard processWizard = new JobCamelScriptsExportWizard();
        IWorkbench workbench = getWorkbench();
        processWizard.setWindowTitle(EXPORTJOBSCRIPTS);
        processWizard.init(workbench, (IStructuredSelection) this.getSelection());

        Shell activeShell = Display.getCurrent().getActiveShell();
        WizardDialog dialog = new WizardDialog(activeShell, processWizard);
        workbench.saveAllEditors(true);
        dialog.setPageSize(830, 450);
        dialog.open();
    }
}
