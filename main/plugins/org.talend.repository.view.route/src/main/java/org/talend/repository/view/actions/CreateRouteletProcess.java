// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.view.actions;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.talend.camel.designer.ui.CreateCamelProcess;
import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.camel.model.CamelRepositoryNodeType;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.designer.core.DesignerPlugin;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryService;
import org.talend.repository.model.RepositoryConstants;
import org.talend.repository.view.actions.wizards.NewRouteletWizard;

public class CreateRouteletProcess extends CreateCamelProcess {

    private static final String CREATE_LABEL = "Create Routelet";

    public CreateRouteletProcess() {
        super();
        this.setText(CREATE_LABEL);
        this.setToolTipText(CREATE_LABEL);
        this.setImageDescriptor(ImageProvider.getImageDesc(ECamelCoreImage.ROUTELET_ICON));
    }

    public CreateRouteletProcess(boolean isToolbar) {
        this();
        setToolbar(isToolbar);
    }

    @Override
    public ERepositoryObjectType getProcessType() {
        return CamelRepositoryNodeType.repositoryRouteletType;
    }

    @Override
    protected void doRun() {
        final NewRouteletWizard processWizard;
        if (isToolbar()) {
            processWizard = new NewRouteletWizard(null);
        } else {
            ISelection selection = getSelection();
            if (selection == null) {
                return;
            }
            Object obj = ((IStructuredSelection) selection).getFirstElement();

            IRepositoryService service = DesignerPlugin.getDefault().getRepositoryService();
            IPath path = service.getRepositoryPath((IRepositoryNode) obj);
            if (RepositoryConstants.isSystemFolder(path.toString())) {
                // Not allowed to create in system folder.
                return;
            }

            processWizard = new NewRouteletWizard(path);
        }

        WizardDialog dlg = new WizardDialog(Display.getCurrent().getActiveShell(), processWizard);
        if (dlg.open() == Window.OK) {
            if (processWizard.getProcess() == null) {
                return;
            }
            try {
                openEditor(processWizard.getProcess());
            } catch (PartInitException e) {
                ExceptionHandler.process(e);
            } catch (PersistenceException e) {
                MessageBoxExceptionHandler.process(e);
            }
        }
    }

}
