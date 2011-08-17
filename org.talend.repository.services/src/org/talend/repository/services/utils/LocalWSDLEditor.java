package org.talend.repository.services.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.wsdl.ui.internal.InternalWSDLMultiPageEditor;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.ResourceModelUtils;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicesFactory;

public class LocalWSDLEditor extends InternalWSDLMultiPageEditor {

    private ServiceItem serviceItem;

    private RepositoryNode repositoryNode;

    public LocalWSDLEditor() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        if (serviceItem != null) {
            IProject currentProject;
            try {
                currentProject = ResourceModelUtils.getProject(ProjectManager.getInstance().getCurrentProject());
                String foldPath = serviceItem.getState().getPath();
                String folder = "";
                if (!foldPath.equals("")) {
                    folder = "/" + foldPath;
                }
                IFile fileTemp = currentProject.getFolder("services" + folder).getFile(
                        repositoryNode.getObject().getProperty().getLabel() + "_"
                                + repositoryNode.getObject().getProperty().getVersion() + ".wsdl");
                if (fileTemp.exists()) {
                    saveModel(fileTemp.getRawLocation().toOSString());
                }
                IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
                factory.save(serviceItem);
                RepositoryManager.refreshSavedNode(repositoryNode);
            } catch (PersistenceException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveModel(String path) {
        WSDLFactory wsdlFactory;
        try {
            wsdlFactory = WSDLFactory.newInstance();
            WSDLReader newWSDLReader = wsdlFactory.newWSDLReader();
            newWSDLReader.setExtensionRegistry(wsdlFactory.newPopulatedExtensionRegistry());
            newWSDLReader.setFeature(com.ibm.wsdl.Constants.FEATURE_VERBOSE, false);
            Definition definition = newWSDLReader.readWSDL(path);
            Map portTypes = definition.getAllPortTypes();
            Iterator it = portTypes.keySet().iterator();
            serviceItem.getServiceConnection().getServiceOperation().clear();
            while (it.hasNext()) {
                QName key = (QName) it.next();
                PortType portType = (PortType) portTypes.get(key);
                List<Operation> list = portType.getOperations();
                for (Operation operation : list) {
                    ServiceOperation serviceOperation = ServicesFactory.eINSTANCE.createServiceOperation();
                    serviceOperation.setOperationName(operation.getName());
                    if (operation.getDocumentationElement() != null) {
                        serviceOperation.setDocumentation(operation.getDocumentationElement().getTextContent());
                    }
                    serviceOperation.setLabel(operation.getName());
                    serviceItem.getServiceConnection().getServiceOperation().add(serviceOperation);
                }
            }
        } catch (WSDLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doSaveAs() {
        super.doSaveAs();
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);

    }

    @Override
    public boolean isDirty() {
        return super.isDirty();
    }

    @Override
    public boolean isSaveAsAllowed() {
        return super.isSaveAsAllowed();
    }

    @Override
    public void setFocus() {
        super.setFocus();
    }

    public RepositoryNode getRepositoryNode() {
        return this.repositoryNode;
    }

    public void setRepositoryNode(RepositoryNode repositoryNode) {
        this.repositoryNode = repositoryNode;
    }

    public ServiceItem getServiceItem() {
        return this.serviceItem;
    }

    public void setServiceItem(ServiceItem serviceItem) {
        this.serviceItem = serviceItem;
    }

}
