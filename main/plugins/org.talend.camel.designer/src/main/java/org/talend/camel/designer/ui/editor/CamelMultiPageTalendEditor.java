// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.talend.camel.designer.i18n.Messages;
import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.core.CorePlugin;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.PluginChecker;
import org.talend.core.model.process.IProcess2;
import org.talend.core.model.properties.Item;
import org.talend.core.services.ISVNProviderService;
import org.talend.designer.core.ICamelDesignerCoreService;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.ui.AbstractMultiPageTalendEditor;
import org.talend.designer.core.ui.editor.AbstractTalendEditor;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryService;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelMultiPageTalendEditor extends AbstractMultiPageTalendEditor {

    public static final String ID = "org.talend.camel.designer.core.ui.CamelMultiPageTalendEditor";

    public CamelMultiPageTalendEditor() {
        super();
        designerEditor = new CamelTalendEditor();
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

    /**
     * Creates the pages of the multi-page editor.
     */
    @Override
    protected void createPages() {
        createPage0();
        createPage1();
        // super.createPages();
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.ui.AbstractMultiPageTalendEditor#getEditorId()
     */
    @Override
    public String getEditorId() {
        return ID;
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        Item curItem = getProcess().getProperty().getItem();
        IRepositoryService service = CorePlugin.getDefault().getRepositoryService();
        IProxyRepositoryFactory repFactory = service.getProxyRepositoryFactory();
        try {
            repFactory.updateLockStatus();
            // For TDI-23825, if not lock by user try to lock again.
            boolean locked = repFactory.getStatus(curItem) == ERepositoryStatus.LOCK_BY_USER;
            if (!locked && !getProcess().isReadOnly()) {
                repFactory.lock(curItem);
            }
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
        ERepositoryStatus status = repFactory.getStatus(curItem);
        if (!status.equals(ERepositoryStatus.LOCK_BY_USER) && !repFactory.getRepositoryContext().isEditableAsReadOnly()) {
            MessageDialog.openWarning(getEditor(0).getEditorSite().getShell(),
                    Messages.getString("MultiPageTalendEditor.canNotSaveTitle"),
                    Messages.getString("MultiPageTalendEditor.canNotSaveMessage"));
            return;
        }
        if (!isDirty()) {
            return;
        }
        updateRunJobContext();
        designerEditor.getProcess().getProperty().eAdapters().remove(dirtyListener);
        repFactory.addRepositoryWorkUnitListener(repositoryWorkListener);

        if (getActivePage() == 0) {
            getEditor(0).doSave(monitor);
            Item item = getDesignerEditor().getProcess().getProperty().getItem();
            ProcessType processType = null;
            if (GlobalServiceRegister.getDefault().isServiceRegistered(ICamelDesignerCoreService.class)) {
                ICamelDesignerCoreService camelService = (ICamelDesignerCoreService) GlobalServiceRegister.getDefault()
                        .getService(ICamelDesignerCoreService.class);
                processType = camelService.getCamelProcessType(item);
            }

        }

        /*
         * refresh should be executed before add the listener,or it will has eProxy on the property,it will cause a
         * editor dirty problem. hywang commet bug 17357
         */
        if (processEditorInput != null) {
            propertyInformation = new ArrayList(processEditorInput.getItem().getProperty().getInformations());
            propertyIsDirty = false;
            firePropertyChange(IEditorPart.PROP_DIRTY);

        }
        if (designerEditor != null && dirtyListener != null) {
            designerEditor.getProcess().getProperty().eAdapters().add(dirtyListener);
        }

        this.setName();
    }

    /**
     * Keep lock if there's any other editor still openning
     */
    @Override
    public boolean isKeepPropertyLocked() {
        IEditorInput input = getEditorInput();
        return super.isKeepPropertyLocked()
                || CamelEditorUtil.hasMoreEditorOpenedExcept((RepositoryNode) input.getAdapter(RepositoryNode.class), input);
    }
}
