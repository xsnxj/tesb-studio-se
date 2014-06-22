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
package org.talend.repository.services.ui;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
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
import org.talend.core.model.repository.RepositoryManager;
import org.talend.designer.runprocess.ProcessorUtilities;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.ui.action.ExportServiceAction;

/**
 * Job scripts export wizard. <br/>
 * 
 * $Id: ServiceExportWizard.java 1 2006-12-13 PM 03:13:18 bqian
 * 
 */
public class ServiceExportWizard extends Wizard implements IExportWizard {

    protected IStructuredSelection selection;

    protected String exportType;

	private ServiceExportWSWizardPage mainPage;

	private static Logger log = Logger.getLogger(ServiceExportWizard.class);

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
		}
        setDialogSettings(section);
    }

    /*
     * (non-Javadoc) Method declared on IWizard.
     */
    public void addPages() {
        super.addPages();
        mainPage = new ServiceExportWSWizardPage(selection);
        addPage((IWizardPage) mainPage);
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
        try {
        	@SuppressWarnings("unchecked")
			List<RepositoryNode> nodes = selection.toList();
        	for (RepositoryNode node : nodes) {
        		new ExportServiceAction(node, mainPage.getDestinationValue()).runInWorkspace(null);
        	}
			mainPage.finish();
		} catch (CoreException e) {
			log.error(e);
			return false;
		}
        return true;
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
