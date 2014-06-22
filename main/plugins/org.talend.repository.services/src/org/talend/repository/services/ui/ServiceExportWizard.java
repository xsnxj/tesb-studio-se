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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.model.process.IContext;
import org.talend.designer.runprocess.IProcessor;
import org.talend.designer.runprocess.ProcessorUtilities;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.Messages;
import org.talend.repository.services.ui.action.ExportServiceAction;
import org.talend.repository.services.ui.action.ExportServiceWithMavenAction;
import org.talend.repository.services.ui.scriptmanager.ServiceExportWithMavenManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager.ExportChoice;

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
            IRunnableWithProgress action = null;
            Map<ExportChoice, Object> exportChoiceMap = mainPage.getExportChoiceMap();
            String destinationValue = mainPage.getDestinationValue();
            
            //TESB-7319: add confirm dialog
            if(new File(destinationValue).exists()){
            	boolean openQuestion = MessageDialog.openQuestion(getShell(), Messages.ServiceExportWizard_destinationExistTitle, Messages.ServiceExportWizard_destinationExistMessage);
            	if(!openQuestion){
            		return false;
            	}
            }
            //END TESB-7319
            
            Iterator<?> iterator = selection.iterator();
            while (iterator.hasNext()) {
                RepositoryNode node = (RepositoryNode) iterator.next();
                if (mainPage.isAddMavenScript()) {
                    ServiceExportWithMavenManager mavenManager = new ServiceExportWithMavenManager(exportChoiceMap,
                            IContext.DEFAULT, JobScriptsManager.LAUNCHER_ALL, IProcessor.NO_STATISTICS, IProcessor.NO_TRACES);
                    action = new ExportServiceWithMavenAction(mavenManager, exportChoiceMap, node, destinationValue);
                } else {
                    action = new ExportServiceAction(exportChoiceMap, node, destinationValue);
                }
                getContainer().run(false, true, action);
            }
            mainPage.finish();
        } catch (InvocationTargetException e) {
            MessageBoxExceptionHandler.process(e.getCause(), getShell());
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
