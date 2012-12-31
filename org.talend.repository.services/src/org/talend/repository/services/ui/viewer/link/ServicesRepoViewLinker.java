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
package org.talend.repository.services.ui.viewer.link;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.link.IRepoViewLinker;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.utils.LocalWSDLEditor;

/**
 * DOC ggu class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public class ServicesRepoViewLinker implements IRepoViewLinker {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.repository.link.IRepoViewLinker#getRelationNode(org.eclipse.ui.IEditorInput)
     */
    public RepositoryNode getRelationNode(IEditorInput editorInput) {
        if (editorInput != null && editorInput.getClass().equals(FileEditorInput.class)) {
            FileEditorInput fileEditorInput = (FileEditorInput) editorInput;
            final IFile file = fileEditorInput.getFile();
            if (file.getName().endsWith(FileConstants.WSDL_FILE_SUFFIX)) {
                IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                if (activeWorkbenchWindow != null) {
                    IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
                    if (activePage != null) {
                        IEditorPart wsdlEditor = null;
                        /*
                         * There is a warning
                         * 
                         * !MESSAGE Warning: Detected recursive attempt by part
                         * org.talend.repository.services.utils.LocalWSDLEditor to create itself (this is probably, but
                         * not necessarily, a bug)
                         */
                        // wsdlEditor = activePage.findEditor(editorInput);

                        // iterator
                        IEditorReference[] editorReferences = activePage.getEditorReferences();
                        if (editorReferences != null) {
                            for (IEditorReference er : editorReferences) {
                                try {
                                    if (editorInput.equals(er.getEditorInput())) {
                                        wsdlEditor = er.getEditor(false);
                                        break;
                                    }
                                } catch (PartInitException e) {
                                    ExceptionHandler.process(e);
                                }
                            }
                        }

                        if (wsdlEditor != null && wsdlEditor instanceof LocalWSDLEditor) {
                            return ((LocalWSDLEditor) wsdlEditor).getRepositoryNode();
                        }
                    }
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.repository.link.IRepoViewLinker#isRelation(org.eclipse.ui.IEditorInput, java.lang.String)
     */
    public boolean isRelation(IEditorInput editorInput, String repoNodeId) {
        RepositoryNode relationNode = getRelationNode(editorInput);
        if (relationNode != null && repoNodeId != null && repoNodeId.equals(relationNode.getId())) {
            return true;
        }
        return false;
    }

}
