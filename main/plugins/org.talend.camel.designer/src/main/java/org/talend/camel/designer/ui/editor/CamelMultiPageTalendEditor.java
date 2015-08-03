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
package org.talend.camel.designer.ui.editor;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.talend.camel.designer.CamelDesignerPlugin;
import org.talend.camel.designer.i18n.Messages;
import org.talend.camel.designer.ui.editor.dependencies.CamelDependenciesEditor;
import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.PluginChecker;
import org.talend.core.model.process.IProcess2;
import org.talend.core.services.ISVNProviderService;
import org.talend.designer.core.ui.AbstractMultiPageTalendEditor;
import org.talend.designer.core.ui.editor.AbstractTalendEditor;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelMultiPageTalendEditor extends AbstractMultiPageTalendEditor {

    public static final String ID = "org.talend.camel.designer.core.ui.CamelMultiPageTalendEditor";

    private CamelDependenciesEditor dependenciesEditor;

    public CamelMultiPageTalendEditor() {
        super();
        designerEditor = new CamelTalendEditor();
    }

    @Override
    public String getEditorId() {
        return ID;
    }

    /**
     * Getter for designerEditor.
     * 
     * @return the designerEditor
     */
    @Override
    public AbstractTalendEditor getDesignerEditor() {
        return this.designerEditor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.ui.AbstractMultiPageTalendEditor#getEditorTitleImage()
     */
    @Override
    protected IImage getEditorTitleImage() {
        return ECamelCoreImage.ROUTES_ICON_EDITOR;
    }

    /**
     * The <code>MultiPageEditorExample</code> implementation of this method checks that the input is an instance of
     * <code>IFileEditorInput</code>.
     */
    @Override
    public void init(final IEditorSite site, final IEditorInput editorInput) throws PartInitException {
        if (!(editorInput instanceof IFileEditorInput) && !(editorInput instanceof CamelProcessEditorInput)) {
            throw new PartInitException(Messages.getString("MultiPageTalendEditor.InvalidInput")); //$NON-NLS-1$
        }
        super.init(site, editorInput);
    }

    /**
     * DOC smallet Comment method "setName".
     * 
     * @param label
     */
    @Override
    public void setName() {
        if (getEditorInput() == null) {
            return;
        }
        super.setName();
        String label = getEditorInput().getName();
        IProcess2 process2 = this.getProcess();
        String jobVersion = "0.1";
        if (process2 != null) {
            jobVersion = process2.getVersion();
        }
        // if (getActivePage() == 1) {
        ISVNProviderService service = null;
        if (PluginChecker.isSVNProviderPluginLoaded()) {
            service = (ISVNProviderService) GlobalServiceRegister.getDefault().getService(ISVNProviderService.class);
            if (revisionChanged && service.isProjectInSvnMode()) {
                revisionNumStr = service.getCurrentSVNRevision(process2);
                revisionChanged = false;
                if (revisionNumStr != null) {
                    revisionNumStr = ".r" + revisionNumStr;
                }
            }
        }
        String title = "MultiPageTalendEditor.Route";

        if (revisionNumStr != null) {
            setPartName(Messages.getString(title, label, jobVersion) + revisionNumStr);
        } else {
            setPartName(Messages.getString(title, label, jobVersion));
        }
    }

    public void setName(String RevisionNumStr) {
        super.setName();
        String label = getEditorInput().getName();
        String jobVersion = this.getProcess().getVersion();
        setPartName(Messages.getString("MultiPageTalendEditor.Route", label, jobVersion) + RevisionNumStr); //$NON-NLS-1$
        revisionNumStr = RevisionNumStr;
    }

    @Override
    protected void createPage2() {
        dependenciesEditor = new CamelDependenciesEditor(this, designerEditor.isReadOnly());
        try {
            int index = addPage(dependenciesEditor, getEditorInput());
            setPageText(index, org.talend.camel.designer.ui.editor.dependencies.Messages.EditDependenciesContextualAction_ActionName);
            setPageImage(index, CamelDesignerPlugin.getImage(CamelDesignerPlugin.DEPEN_ICON));
        } catch (PartInitException e) {
            ExceptionHandler.process(e);
        }
    }
}
