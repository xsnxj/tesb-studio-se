package org.talend.repository.services.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.wsdl.ui.internal.InternalWSDLMultiPageEditor;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.ui.actions.metadata.AbstractCreateAction;
import org.talend.designer.core.DesignerPlugin;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.services.utils.LocalWSDLEditor;
import org.talend.repository.services.utils.WSDLUtils;

public class OpenWSDLEditorAction extends AbstractCreateAction {

    private ERepositoryObjectType currentNodeType;

    private final static String ID = "org.talend.repository.services.utils.LocalWSDLEditor";

    private List<ServiceItem> scriptList = new ArrayList<ServiceItem>();

    public OpenWSDLEditorAction() {
        this.setText("Open WSDL Editor");
        this.setToolTipText("Open WSDL Editor");

        this.setImageDescriptor(ImageProvider.getImageDesc(ECoreImage.METADATA_WSDL_SCHEMA_ICON));
        currentNodeType = ESBRepositoryNodeType.SERVICES;
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(new IPartListener() {

            public void partActivated(IWorkbenchPart part) {

            }

            public void partBroughtToTop(IWorkbenchPart part) {

            }

            public void partClosed(IWorkbenchPart part) {
                if (part instanceof InternalWSDLMultiPageEditor) {
                    InternalWSDLMultiPageEditor editor = (InternalWSDLMultiPageEditor) part;
                    if (editor instanceof LocalWSDLEditor) {
                        LocalWSDLEditor localWSDLEditor = (LocalWSDLEditor) editor;
                        localWSDLEditor.removeListener();
                    }
                    String editorName = editor.getEditorInput().getName();
                    IProxyRepositoryFactory repFactory = DesignerPlugin.getDefault().getProxyRepositoryFactory();
                    Iterator it = scriptList.iterator();
                    try {
                        while (it.hasNext()) {
                            ServiceItem serviceItem = (ServiceItem) it.next();
                            String name = editorName.substring(0, editorName.lastIndexOf("_"));
                            if (name.equals(serviceItem.getProperty().getLabel())) {
                                repFactory.unlock(serviceItem);
                                it.remove();
                            }
                        }
                    } catch (Exception e) {
                        ExceptionHandler.process(e);
                    }

                    RepositoryManager.getRepositoryView().refreshView();
                }
            }

            public void partDeactivated(IWorkbenchPart part) {

            }

            public void partOpened(IWorkbenchPart part) {

            }

        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.core.repository.ui.actions.metadata.AbstractCreateAction#init(org.talend.repository.model.RepositoryNode
     * )
     */
    @Override
    protected void init(RepositoryNode node) {
        ERepositoryObjectType nodeType = (ERepositoryObjectType) node.getProperties(EProperties.CONTENT_TYPE);
        if (!currentNodeType.equals(nodeType)) {
            return;
        }
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        switch (node.getType()) {
        case REPOSITORY_ELEMENT:
            break;
        default:
            return;
        }
        boolean flag = true;
        if (node.getObject() == null) {
            flag = false;
        }
        setEnabled(flag);
    }

    public Class getClassForDoubleClick() {
        return ServiceItem.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.AContextualAction#doRun()
     */
    @Override
    protected void doRun() {
        if (repositoryNode == null) {
            repositoryNode = getCurrentRepositoryNode();
        }
        if (isToolbar()) {
            if (repositoryNode != null && repositoryNode.getContentType() != currentNodeType) {
                repositoryNode = null;
            }
            if (repositoryNode == null) {
                repositoryNode = getRepositoryNodeForDefault(currentNodeType);
            }
        }
        if (repositoryNode.getObject() == null) {
            return;
        }
        ServiceItem serviceItem = (ServiceItem) repositoryNode.getObject().getProperty().getItem();
        LocalWSDLEditor wsdlEditor = null;
        IFile file = WSDLUtils.getWsdlFile(repositoryNode);
        IEditorInput editorInput = new FileEditorInput(file);
        WorkbenchPage page = (WorkbenchPage) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try {
            IEditorPart editor = page.openEditor(editorInput, ID, true);
            if (editor instanceof LocalWSDLEditor) {
                wsdlEditor = (LocalWSDLEditor) editor;
                wsdlEditor.setServiceItem(serviceItem);
                wsdlEditor.setRepositoryNode(repositoryNode);
                wsdlEditor.addListener();
            }
            // lock
            if (DesignerPlugin.getDefault().getProxyRepositoryFactory().isEditableAndLockIfPossible(serviceItem)) {
                DesignerPlugin.getDefault().getProxyRepositoryFactory().lock(serviceItem);
                scriptList.add(serviceItem);
            } else {
                wsdlEditor.setReadOnly(true);
            }
            RepositoryManager.refresh(ESBRepositoryNodeType.SERVICES);
        } catch (CoreException e) {
            ExceptionHandler.process(e);
        } catch (LoginException e) {
            ExceptionHandler.process(e);
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
    }
}
