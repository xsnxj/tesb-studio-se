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
package org.talend.repository.services.ui;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.designer.runprocess.ProcessorUtilities;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.wizards.exportjob.JavaJobScriptsExportWizardPage;

/**
 * Job scripts export wizard. <br/>
 * 
 * $Id: ServiceExportWizard.java 1 2006-12-13 下午03:13:18 bqian
 * 
 */
public class ServiceExportWizard extends Wizard implements IExportWizard {

    protected IStructuredSelection selection;

    protected String exportType;

	private ServiceExportWSWizardPage mainPage;

    /**
     * Creates a wizard for exporting workspace resources to a zip file.
     */
    public ServiceExportWizard() {
        @SuppressWarnings("deprecation")
		AbstractUIPlugin plugin = (AbstractUIPlugin) Platform.getPlugin(PlatformUI.PLUGIN_ID);
        IDialogSettings workbenchSettings = plugin.getDialogSettings();
        IDialogSettings section = workbenchSettings.getSection("ServiceExportWizard"); //$NON-NLS-1$
        if (section == null) {
            section = workbenchSettings.addNewSection("ServiceExportWizard"); //$NON-NLS-1$

            section.put(JavaJobScriptsExportWizardPage.STORE_SHELL_LAUNCHER_ID, true);
            section.put(JavaJobScriptsExportWizardPage.STORE_SYSTEM_ROUTINE_ID, true);
            section.put(JavaJobScriptsExportWizardPage.STORE_USER_ROUTINE_ID, true);
            section.put(JavaJobScriptsExportWizardPage.STORE_MODEL_ID, true);
            section.put(JavaJobScriptsExportWizardPage.STORE_JOB_ID, true);
            section.put(JavaJobScriptsExportWizardPage.STORE_DEPENDENCIES_ID, false);
            section.put(JavaJobScriptsExportWizardPage.STORE_CONTEXT_ID, true);
            section.put(JavaJobScriptsExportWizardPage.APPLY_TO_CHILDREN_ID, false);
            // this is done in the wizard page
            // section.put(ServiceExportWSWizardPage.STORE_EXPORTTYPE_ID, JobExportType.POJO.toString());

            section.put(ServiceExportWSWizardPage.STORE_WEBXML_ID, true);
            section.put(ServiceExportWSWizardPage.STORE_CONFIGFILE_ID, true);
            section.put(ServiceExportWSWizardPage.STORE_AXISLIB_ID, true);
            section.put(ServiceExportWSWizardPage.STORE_WSDD_ID, true);
            section.put(ServiceExportWSWizardPage.STORE_WSDL_ID, true);
            section.put(ServiceExportWSWizardPage.STORE_SOURCE_ID, true);

            // section.put(JobScriptsExportWizardPage.STORE_GENERATECODE_ID, true);
        }
        setDialogSettings(section);
    }

    /*
     * (non-Javadoc) Method declared on IWizard.
     */
    public void addPages() {
        super.addPages();
		@SuppressWarnings("unchecked")
		List<RepositoryNode> nodes = selection.toList();
		if (nodes.size() >= 1) {
            RepositoryNode node = nodes.get(0);
            if (node.getType() == ENodeType.REPOSITORY_ELEMENT) {
                IRepositoryViewObject repositoryObject = node.getObject();
                mainPage = new ServiceExportWSWizardPage(selection, repositoryObject.getLabel());
                addPage((IWizardPage) mainPage);
            }
		}
    }

    /*
     * (non-Javadoc) Method declared on IWorkbenchWizard.
     */
    public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
        this.selection = currentSelection;
        @SuppressWarnings("rawtypes")
		List selectedResources = IDE.computeSelectedResources(currentSelection);
        if (!selectedResources.isEmpty()) {
            this.selection = new StructuredSelection(selectedResources);
        }

        setWindowTitle(org.talend.repository.services.Messages.ServiceExportWizard_Wizard_Title);
        setDefaultPageImageDescriptor(IDEWorkbenchPlugin.getIDEImageDescriptor("wizban/exportzip_wiz.png"));//$NON-NLS-1$
        setNeedsProgressMonitor(true);

    }

    /*
     * (non-Javadoc) Method declared on IWizard.
     */
    public boolean performFinish() {
        boolean finish = mainPage.finish();
        if (!finish && !getShell().isDisposed()) {
            getShell().close();
        } else {
            selection = null;
            mainPage = null;
        }
        return finish;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performCancel()
     */
    @Override
    public boolean performCancel() {
        ProcessorUtilities.resetExportConfig();
        RepositoryManager.refreshCreatedNode(ERepositoryObjectType.PROCESS);
        selection = null;
        mainPage = null;
        return true;
    }
}
