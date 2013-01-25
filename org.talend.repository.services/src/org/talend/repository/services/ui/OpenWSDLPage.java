// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.commons.ui.swt.formtools.LabelledFileField;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.ResourceModelUtils;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.services.Activator;
import org.talend.repository.services.Messages;
import org.talend.repository.services.action.OpenWSDLEditorAction;
import org.talend.repository.services.action.PublishMetadataRunnable;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.model.services.ServicesFactory;
import org.talend.repository.services.utils.TemplateProcessor;
import org.talend.repository.services.utils.WSDLLoader;
import org.talend.repository.services.utils.WSDLUtils;
import org.talend.repository.ui.wizards.PropertiesWizardPage;

/**
 * hwang class global comment. Detailed comment
 */
public class OpenWSDLPage extends WizardPage {

    private static final String TEMPLATE_SERVICE_WSDL = "/resources/wsdl-template.wsdl"; //$NON-NLS-1$

    private RepositoryNode repositoryNode;

    private LabelledFileField wsdlText;

    private String path;

    private final ServiceItem item;

    private final boolean creation;

    private IPath pathToSave;

    private Button checkImport;

    private Button radioImportWsdl;

    private Button radioCreateWsdl;

    private Definition definition;

    protected OpenWSDLPage(RepositoryNode repositoryNode, IPath pathToSave, ServiceItem item, boolean creation) {
        super("OpenWSDLPage"); //$NON-NLS-1$
        this.creation = creation;
        this.pathToSave = pathToSave;
        this.item = item;
        this.repositoryNode = repositoryNode;

        this.setTitle(Messages.AssignWsdlDialog_Title);
        this.setMessage(Messages.AssignWsdlDialog_Description);
    }

    public void createControl(Composite parent) {
        Composite parentArea = new Composite(parent, SWT.NONE);
        parentArea.setLayout(new GridLayout());

        radioCreateWsdl = new Button(parentArea, SWT.RADIO);
        radioCreateWsdl.setText(Messages.AssignWsdlDialog_WsdlChoice_CreateNew);
        radioCreateWsdl.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                switchRadio();
            }
        });
        radioCreateWsdl.setSelection(true);

        radioImportWsdl = new Button(parentArea, SWT.RADIO);
        radioImportWsdl.setText(Messages.AssignWsdlDialog_WsdlChoice_ImportExistent);
        radioImportWsdl.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                switchRadio();
            }
        });

        Composite wsdlFileArea = new Composite(parentArea, SWT.NONE);
        wsdlFileArea.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        GridLayout layout = new GridLayout(3, false);
//        layout.marginLeft = 15;
//        layout.marginHeight = 0;
        wsdlFileArea.setLayout(layout);

        String[] xmlExtensions = { "*.xml;*.xsd;*.wsdl", "*.*", "*" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        wsdlText = new LabelledFileField(wsdlFileArea, Messages.AssignWsdlDialog_ExistentWsdlFilePath, xmlExtensions);
        String initialPath = (null == item || null == item.getConnection()) ? "" //$NON-NLS-1$
                : ((ServiceConnection) item.getConnection()).getWSDLPath();
        wsdlText.setText(initialPath);
        wsdlText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                path = wsdlText.getText().trim();
                setPageComplete(!path.isEmpty());
            }
        });
        new Label(wsdlFileArea, SWT.NONE);
        checkImport = new Button(wsdlFileArea, SWT.CHECK);
        checkImport.setText(Messages.AssignWsdlDialog_ImportWsdlSchemas);
        checkImport.setSelection(true);

        switchRadio();

        setControl(parentArea);
    }

    private void switchRadio() {
        if (radioCreateWsdl.getSelection()) {
            wsdlText.setVisible(false);
            checkImport.setVisible(false);
            path = null;
            setPageComplete(true);
        } else {
            wsdlText.setVisible(true);
            checkImport.setVisible(true);
            path = wsdlText.getText().trim();
            setPageComplete(!path.isEmpty());
        }
    }

    /**
     * Gets the path to save the Service node. Created by Marvin Wang on May 11, 2012.
     *
     * @return
     */
    protected IPath getDestinationPath() {
        IWizardPage previousPage = this.getPreviousPage();
        if (previousPage instanceof PropertiesWizardPage) {
            PropertiesWizardPage wizardPage = (PropertiesWizardPage) previousPage;
            pathToSave = wizardPage.getDestinationPath();
        }
        return pathToSave;
    }

    public boolean finish() {
        // changed by hqzhang for TDI-19527, label=displayName
        final String label = item.getProperty().getDisplayName();
        item.getProperty().setLabel(label);
        String version = item.getProperty().getVersion();
        final String wsdlFileName = label + "_" + version + ".wsdl"; //$NON-NLS-1$ //$NON-NLS-2$

        IWorkspaceRunnable runnable = new IWorkspaceRunnable() {

            public void run(final IProgressMonitor monitor) throws CoreException {
                ByteArrayOutputStream baos = null;
                try {
                    IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
                    item.setConnection(ServicesFactory.eINSTANCE.createServiceConnection());
                    if (creation) {
                        item.getProperty().setId(factory.getNextId());
                        factory.create(item, getDestinationPath());
                        repositoryNode = new RepositoryNode(new RepositoryViewObject(item.getProperty()), repositoryNode.getParent(),
                                ENodeType.REPOSITORY_ELEMENT);
                    }

                    ((ServiceConnection) item.getConnection()).setWSDLPath(path);
                    ((ServiceConnection) item.getConnection()).getServicePort().clear();

                    IProject currentProject = ResourceModelUtils.getProject(ProjectManager.getInstance().getCurrentProject());
                    String foldPath = item.getState().getPath();
                    String folder = !foldPath.equals("") ? "/" + foldPath : ""; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                    final IFile fileTemp = currentProject.getFolder("services" + folder).getFile(wsdlFileName);

                    if (null == path) {
                        baos = new ByteArrayOutputStream();
                        // create new WSDL file from template
                        TemplateProcessor.processTemplate(TEMPLATE_SERVICE_WSDL,
                                Collections.singletonMap("serviceName", (Object) label), new OutputStreamWriter(baos));
                        //path = fileTemp.getLocation().toPortableString();
                    } else {
//                            baos = new ByteArrayOutputStream();
//                            // copy WSDL file
//                            InputStream wsdlInputStream = null;
//                            if (path.startsWith("http://") || path.startsWith("https://")){
//                                wsdlInputStream = new URL(path).openStream();
//                            } else {
//                                wsdlInputStream = new FileInputStream(new File(path));
//                            }
//                            readWsdlFile(wsdlInputStream, baos);
                        baos = new WSDLLoader().load(path);
                    }

                    // store WSDL in service
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
                    if (!fileTemp.exists()) {
                        fileTemp.create(byteArrayInputStream, true, null);
                    } else {
                        fileTemp.setContents(byteArrayInputStream, 0, null);
                    }

                    // ??
                    ReferenceFileItem createReferenceFileItem = null;
                    if (item.getReferenceResources().isEmpty()) {
                        createReferenceFileItem = PropertiesFactory.eINSTANCE.createReferenceFileItem();
                        ByteArray byteArray = PropertiesFactory.eINSTANCE.createByteArray();
                        createReferenceFileItem.setContent(byteArray);
                        createReferenceFileItem.setExtension("wsdl");
                        item.getReferenceResources().add(createReferenceFileItem);
                    } else {
                        createReferenceFileItem = (ReferenceFileItem) item.getReferenceResources().get(0);
                    }
                    createReferenceFileItem.getContent().setInnerContent(baos.toByteArray());

                    //
                    populateModelFromWsdl(factory, fileTemp.getLocation().toPortableString()/*path*/, item, repositoryNode);

                    factory.save(item);
                    ProxyRepositoryFactory.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());

                } catch (CoreException e) {
                    throw e;
                } catch (InvocationTargetException e) {
                    throwCoreException("WDSL creation failed", e.getCause());
                } catch (Exception e) {
                    throwCoreException("WDSL creation failed", e);
                } finally {
                    if (null != baos) {
                        try {
                            baos.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                }
            }
        };
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        try {
            ISchedulingRule schedulingRule = workspace.getRoot();// we use the workspace scheduling rule to lock all
                                                                 // workspace modifications during the run.
            // the update of the project files need to be done in the workspace runnable to avoid all notification
            // of changes before the end of the modifications.
            workspace.run(runnable, schedulingRule, IWorkspace.AVOID_UPDATE, null);
            repositoryNode = RepositoryNodeUtilities.getRepositoryNode(new RepositoryViewObject(item.getProperty()));
            OpenWSDLEditorAction action = new OpenWSDLEditorAction();
            action.setRepositoryNode(repositoryNode);
            action.run();
        } catch (CoreException e) {
            MessageBoxExceptionHandler.process(e);
            return false;
        }
        if (checkImport.isVisible() && checkImport.getSelection() && null != definition) {
            PublishMetadataRunnable publishMetadataRunnable = new PublishMetadataRunnable(definition, getShell());
            try {
                getContainer().run(true, true, publishMetadataRunnable);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                String message = (null != cause.getMessage()) ? cause.getMessage() : cause.getClass().getName();
                setErrorMessage("Populate schema to repository: " + message);
                return false;
            } catch (InterruptedException e) {
            }
        }
        return true;
    }

//    private void readWsdlFile(InputStream is, OutputStream os) throws IOException {
//        BufferedInputStream bis = new BufferedInputStream(is);
//        try {
//            byte[] buf = new byte[1024];
//            int i = 0;
//            while ((i = bis.read(buf)) != -1) {
//                os.write(buf, 0, i);
//            }
//        } finally {
//            if (null != bis) {
//                try {
//                    bis.close();
//                } catch (Exception e) {
//                }
//            }
//        }
//    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void populateModelFromWsdl(IProxyRepositoryFactory factory, String wsdlPath, ServiceItem serviceItem,
            RepositoryNode serviceRepositoryNode) throws CoreException {
        definition = WSDLUtils.getDefinition(wsdlPath);
        Map portTypes = definition.getAllPortTypes();
        Iterator it = portTypes.keySet().iterator();
        serviceRepositoryNode.getChildren().clear();
        ((ServiceConnection) serviceItem.getConnection()).getServicePort().clear();
        while (it.hasNext()) {
            QName key = (QName) it.next();
            PortType portType = (PortType) portTypes.get(key);
            ServicePort port = ServicesFactory.eINSTANCE.createServicePort();
            port.setId(factory.getNextId());
            port.setName(portType.getQName().getLocalPart());
            List<Operation> list = portType.getOperations();
            for (Operation operation : list) {
                ServiceOperation serviceOperation = ServicesFactory.eINSTANCE.createServiceOperation();
                serviceOperation.setId(factory.getNextId());
                RepositoryNode operationNode = new RepositoryNode(new RepositoryViewObject(serviceItem.getProperty()),
                        serviceRepositoryNode, ENodeType.REPOSITORY_ELEMENT);
                operationNode.setProperties(EProperties.LABEL, serviceItem.getProperty().getLabel());
                operationNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.SERVICESOPERATION);
                serviceOperation.setName(operation.getName());
                if (operation.getDocumentationElement() != null) {
                    serviceOperation.setDocumentation(operation.getDocumentationElement().getTextContent());
                }
                serviceOperation.setLabel(operation.getName());
                port.getServiceOperation().add(serviceOperation);
            }
            ((ServiceConnection) serviceItem.getConnection()).getServicePort().add(port);
        }
    }

    private static void throwCoreException(String message, Throwable initialException) throws CoreException {
        throw new CoreException(new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
            message, initialException));
    }

}
