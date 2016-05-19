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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.designer.i18n.Messages;
import org.talend.camel.designer.ui.editor.CamelEditorUtil;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.designer.ui.editor.CamelProcessEditorInput;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.ui.action.AbstractProcessAction;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC smallet class global comment. Detailled comment <br/>
 * 
 * $Id: EditProcess.java 1495 2007-01-18 04:31:30Z nrousseau $
 * 
 */
public class ReadCamelProcess extends AbstractProcessAction {

    private static final String LABEL = Messages.getString("ReadProcess.label"); //$NON-NLS-1$

    public ReadCamelProcess() {
        super();
        this.setText(LABEL);
        this.setToolTipText(LABEL);
        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.READ_ICON));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    protected void doRun() {
        ISelection selection = getSelection();
        Object obj = ((IStructuredSelection) selection).getFirstElement();

        RepositoryNode node = (RepositoryNode) obj;
        CamelProcessItem processItem = (CamelProcessItem) node.getObject().getProperty().getItem();

        IWorkbenchPage page = getActivePage();

        try {
            CamelProcessEditorInput fileEditorInput = new CamelProcessEditorInput(processItem, true, null, true);
            checkUnLoadedNodeForProcess(fileEditorInput);
            IEditorPart editorPart = page.findEditor(fileEditorInput);

            if (editorPart == null) {
                fileEditorInput.setRepositoryNode(node);
                page.openEditor(fileEditorInput, CamelMultiPageTalendEditor.ID, true);
            } else {
                page.activate(editorPart);
            }
        } catch (PartInitException e) {
            MessageBoxExceptionHandler.process(e);
        } catch (PersistenceException e) {
            MessageBoxExceptionHandler.process(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse.jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(TreeViewer viewer, IStructuredSelection selection) {
        boolean canWork = !selection.isEmpty() && selection.size() == 1;
        if (canWork) {
            Object o = selection.getFirstElement();
            RepositoryNode node = (RepositoryNode) o;
            if (CamelEditorUtil.hasEditorOpened(node)) {
                canWork = false;
            } else {
                switch (node.getType()) {
                case REPOSITORY_ELEMENT:
                    if (node.getObjectType() != CamelRepositoryNodeType.repositoryRoutesType) {
                        canWork = false;
                    }
                    break;
                default:
                    canWork = false;
                }
            }
            if (canWork && node.getObject() != null
                    && ProxyRepositoryFactory.getInstance().getStatus(node.getObject()) == ERepositoryStatus.LOCK_BY_USER) {
                canWork = false;
            }
        }
        setEnabled(canWork);
    }
}
