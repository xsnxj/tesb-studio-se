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
package org.talend.camel.designer.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.designer.ui.editor.CamelProcessEditorInput;
import org.talend.camel.designer.ui.wizards.SaveAsRoutesWizard;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.designer.core.ui.editor.AbstractTalendEditor;
import org.talend.repository.editor.JobEditorInput;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;

/**
 * DOC xtan class global comment. <br/>
 */
public class SaveAsRoutesAction extends Action {

    private final EditorPart editorPart;

    public SaveAsRoutesAction(EditorPart editorPart) {
        this.editorPart = editorPart;
    }

    @Override
    public void run() {
        SaveAsRoutesWizard processWizard = new SaveAsRoutesWizard(editorPart);

        WizardDialog dlg = new WizardDialog(Display.getCurrent().getActiveShell(), processWizard);
        if (dlg.open() == Window.OK) {

            try {

                // Set readonly to false since created routes will always be editable.
                JobEditorInput newRoutesEditorInput = new CamelProcessEditorInput(processWizard.getProcess(), true, true, false);

                IWorkbenchPage page = getActivePage();

                IRepositoryNode repositoryNode = RepositoryNodeUtilities.getRepositoryNode(newRoutesEditorInput.getItem()
                        .getProperty().getId(), false);
                newRoutesEditorInput.setRepositoryNode(repositoryNode);

                // close the old editor
                page.closeEditor(((AbstractTalendEditor) this.editorPart).getParent(), false);

                // open the new editor, because at the same time, there will update the routes view
                page.openEditor(newRoutesEditorInput, CamelMultiPageTalendEditor.ID, true);

            } catch (Exception e) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
                        "Routes could not be saved" + " : " + e.getMessage());
                ExceptionHandler.process(e);
            }
        }
    }

    private IWorkbenchPage getActivePage() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    }

}
