package org.talend.repository.services.action;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.ResourceModelUtils;
import org.talend.core.repository.ui.actions.metadata.AbstractCreateAction;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.utils.ESBRepositoryNodeType;

public class OpenWSDLEditorAction extends AbstractCreateAction {

    private ERepositoryObjectType currentNodeType;

    private final static String ID = "org.eclipse.wst.wsdl.ui.internal.WSDLEditor";

    public OpenWSDLEditorAction() {
        currentNodeType = ESBRepositoryNodeType.SERVICES;
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
        String wsdlFile = serviceItem.getServiceConnection().getWSDLContent().toString();
        try {
            IProject currentProject = ResourceModelUtils.getProject(ProjectManager.getInstance().getCurrentProject());
            IFile file = currentProject.getFolder("temp").getFile(repositoryNode.getObject().getProperty().getLabel());
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(wsdlFile.getBytes());
            if (!file.exists()) {
                file.create(byteArrayInputStream, true, null);
                file.setContents(byteArrayInputStream, 0, null);
            } else {
                file.delete(true, null);
                file.create(byteArrayInputStream, true, null);
                file.setContents(byteArrayInputStream, 0, null);
            }
            IEditorInput editorInput = new FileEditorInput(file);
            WorkbenchPage page = (WorkbenchPage) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            page.openEditor(editorInput, ID, true);
        } catch (PersistenceException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

}
