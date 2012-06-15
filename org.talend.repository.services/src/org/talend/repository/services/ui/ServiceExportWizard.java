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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
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
    @Override
    public void addPages() {
        super.addPages();
        mainPage = new ServiceExportWSWizardPage(selection);
        addPage(mainPage);
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
    @Override
    public boolean performFinish() {
        try {
            @SuppressWarnings("unchecked")
            List<RepositoryNode> nodes = selection.toList();
            for (RepositoryNode node : nodes) {
            	getContainer().run(false, true, new ExportServiceAction(node, mainPage.getDestinationValue()));
            }
            mainPage.finish();
        } catch (CoreException e) {
        	MessageBoxExceptionHandler.process(e, getShell());
            return false;
        } catch (InvocationTargetException e) {
        	MessageBoxExceptionHandler.process(e, getShell());
            return false;
		} catch (InterruptedException e) {
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
        selection = null;
        mainPage = null;
        return true;
    }
}
