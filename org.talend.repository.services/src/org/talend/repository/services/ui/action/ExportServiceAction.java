package org.talend.repository.services.ui.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.publish.core.models.BundleModel;
import org.talend.designer.publish.core.models.FeaturesModel;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.ui.ServiceMetadataDialog;
import org.talend.repository.services.ui.scriptmanager.ServiceExportManager;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.services.utils.WSDLUtils;
import org.talend.repository.ui.wizards.exportjob.action.JobExportAction;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager.ExportChoice;
import org.talend.utils.io.FilesUtils;

public class ExportServiceAction implements IRunnableWithProgress {

    protected final static String PATH_SEPERATOR = "/"; //$NON-NLS-1$

    protected Map<ExportChoice, Object> exportChoiceMap;

    protected List<RepositoryNode> nodes = new ArrayList<RepositoryNode>();

    private String serviceVersion;

    protected ServiceExportManager serviceManager;

    protected String tempFolder;

    private IFile serviceWsdl;

    private final Map<ServicePort, Map<String, String>> ports = new HashMap<ServicePort, Map<String, String>>();

    private String serviceName;

    private String groupId;

    private final ServiceItem serviceItem;

    private ServiceConnection serviceConnection;

    public ExportServiceAction(Map<ExportChoice, Object> exportChoiceMap, RepositoryNode node, String targetPath)
            throws InvocationTargetException {
        if (node.getType() == ENodeType.REPOSITORY_ELEMENT
                && node.getProperties(EProperties.CONTENT_TYPE) == ESBRepositoryNodeType.SERVICES) {
            serviceItem = (ServiceItem) node.getObject().getProperty().getItem();
            this.exportChoiceMap = exportChoiceMap;
            init(targetPath);
        } else {
            IllegalArgumentException e = new IllegalArgumentException("provided node is not service node");
            throw new InvocationTargetException(e);
        }
    }

    public ExportServiceAction(IRepositoryViewObject viewObject, String targetPath) throws InvocationTargetException {
        serviceItem = (ServiceItem) viewObject.getProperty().getItem();
        init(targetPath);
    }

    private void init(String targetPath) throws InvocationTargetException {
        serviceName = serviceItem.getProperty().getLabel();
        serviceVersion = serviceItem.getProperty().getVersion();

        serviceWsdl = WSDLUtils.getWsdlFile(serviceItem);
        serviceConnection = (ServiceConnection) serviceItem.getConnection();
        EList<ServicePort> listPort = serviceConnection.getServicePort();
        try {
            for (ServicePort port : listPort) {
                List<ServiceOperation> listOperation = port.getServiceOperation();
                Map<String, String> operations = new HashMap<String, String>(listOperation.size());
                for (ServiceOperation operation : listOperation) {
                    if (operation.getReferenceJobId() != null && !operation.getReferenceJobId().equals("")) {
                        String operationName = operation.getName();
                        RepositoryNode jobNode = getJobRepositoryNode(operation.getReferenceJobId());
                        if(jobNode==null) {
                        	continue;
                        }
                        String jobName = jobNode.getObject().getLabel();
                        operations.put(operationName, jobName);
                        nodes.add(jobNode);
                    }
                }
                ports.put(port, operations);
            }
        } catch (PersistenceException e) {
            throw new InvocationTargetException(e);
        }

        this.serviceManager = new ServiceExportManager(exportChoiceMap);
        serviceManager.setDestinationPath(targetPath);
        tempFolder = getTmpFolderPath();
    }

    private RepositoryNode getJobRepositoryNode(String jobId) throws PersistenceException {
        List<IRepositoryViewObject> jobs = ProxyRepositoryFactory.getInstance().getAll(ERepositoryObjectType.PROCESS);
        for (IRepositoryViewObject job : jobs) {
            if (job.getId().equals(jobId)) {
                return new RepositoryNode(job, null, ENodeType.REPOSITORY_ELEMENT);
            }
        }
        return null;
    }

    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        String destinationPath = serviceManager.getDestinationPath();
        if (!destinationPath.endsWith(FileConstants.KAR_FILE_SUFFIX)) {
            destinationPath = destinationPath.replace("\\", PATH_SEPERATOR); //$NON-NLS-1$
            if (destinationPath.indexOf(PATH_SEPERATOR) != -1) {
                String filePath = destinationPath.substring(0, destinationPath.lastIndexOf(PATH_SEPERATOR) + 1);
                String fileName = destinationPath.substring(destinationPath.lastIndexOf(PATH_SEPERATOR) + 1);
                if (fileName.indexOf('.') != -1) {
                    fileName = fileName.substring(0, fileName.lastIndexOf('.'));
                }
                destinationPath = filePath + fileName + FileConstants.KAR_FILE_SUFFIX;
            }
        }

        FeaturesModel feature = new FeaturesModel(getGroupId(), getServiceName(), getServiceVersion());
        feature.setConfigName(getServiceName());
        feature.setContexts(ContextNodeRetriever.getContextsMap(serviceItem));

        ServiceConnection connection = (ServiceConnection) serviceItem.getConnection();
        String useCorrelation=connection.getAdditionalInfo().get(ServiceMetadataDialog.USE_BUSINESS_CORRELATION);
        if("true".equals(useCorrelation)) {
        	feature.addPolicyCorrelationIdFeature();
        }

        try {
            String directoryName = serviceManager.getRootFolderName(tempFolder);
            for (RepositoryNode node : nodes) {
                JobScriptsManager manager = serviceManager.getJobManager(exportChoiceMap, tempFolder, node, getGroupId(),
                        getServiceVersion());
                JobExportAction job = new JobExportAction(Collections.singletonList(node), node.getObject().getVersion(),
                        getBundleVersion(), manager, directoryName, "Service"); //$NON-NLS-1$
                job.run(monitor);
                feature.addBundle(new BundleModel(getGroupId(), serviceManager.getNodeLabel(node), getServiceVersion(), new File(manager.getDestinationPath())));
            }
            try {
                // control bundle
                final String artefactName = getServiceName() + "-control-bundle"; //$NON-NLS-1$
                File contrilBundle = generateControlBundle(getGroupId(), artefactName);
                feature.addBundle(new BundleModel(getGroupId(), artefactName, getServiceVersion(), contrilBundle));

                processFeature(feature);

                processFinalResult(destinationPath);
            } catch (Exception e) {
                throw new InvocationTargetException(e);
            }
        } finally {
            clean();
        }
    }

    private File generateControlBundle(String groupId, String artefactName) throws IOException, CoreException {
        File temp = new File(tempFolder, "control-bundle"); //$NON-NLS-1$
        // wsdl
        File wsdl = new File(temp, serviceWsdl.getName());
        FilesUtils.copyFile(serviceWsdl.getContents(), wsdl);
        // wsdl:import
        String serviceWsdlPrefix = serviceName + '_' + serviceVersion + '_';
        for (IResource resource : serviceWsdl.getParent().members()) {
            if (IResource.FILE == resource.getType()
                && resource.getName().startsWith(serviceWsdlPrefix)) {
                FilesUtils.copyFile(((IFile) resource).getContents(), new File(temp, resource.getName()));
            }
        }
        // blueprint
        File blueprint = new File(temp, FileConstants.BLUEPRINT_FOLDER_NAME);
        blueprint.mkdirs();
        serviceManager.createBlueprint(new File(blueprint, "blueprint.xml"), ports, serviceConnection, serviceWsdl,
                getServiceName());
        String jarName = artefactName + '-' + getServiceVersion() + FileConstants.JAR_FILE_SUFFIX;
        File jar = new File(serviceManager.getFilePath(tempFolder, groupId, artefactName, getServiceVersion()), jarName);
        FilesUtils.jar(serviceManager.getManifest(artefactName, getBundleVersion()), temp, jar);
        FilesUtils.removeFolder(temp, true);
        return jar;
    }

    protected void processFeature(FeaturesModel feature) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                getFeatureFilePath())));
        try {
            bw.write(feature.getContent());
            bw.flush();
        } finally {
            bw.close();
        }
    }

    protected void processFinalResult(String destinationPath) throws IOException {
        FilesUtils.zip(tempFolder, destinationPath);
    }

    protected void clean() {
        FilesUtils.removeFolder(tempFolder, true);
    }

    protected String getControlBundleFilePath() {
        String artefactName = getServiceName() + "-control-bundle"; //$NON-NLS-1$
        String fileName = artefactName + '-' + getServiceVersion() + FileConstants.JAR_FILE_SUFFIX;
        String filePath = new File(serviceManager.getFilePath(tempFolder, getGroupId(), artefactName, getServiceVersion()),
                fileName).getAbsolutePath();

        return filePath;
    }

    protected String getFeatureFilePath() {
        String artefactName = getServiceName() + FeaturesModel.NAME_SUFFIX;
        return new File(serviceManager.getFilePath(tempFolder, getGroupId(), artefactName, getServiceVersion()),
                getServiceName() + '-' + getServiceVersion() + FeaturesModel.NAME_SUFFIX + ".xml").getAbsolutePath(); //$NON-NLS-1$
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getGroupId() {
        if (null == groupId) {
            try {
                groupId = getGroupId(WSDLUtils.getDefinition(serviceWsdl).getTargetNamespace(),
                        getServiceName());
            } catch (CoreException e) {
                throw new RuntimeException(e);
            }
        }
        return groupId;
    }

    public String getBundleVersion() {
        return serviceVersion;
    }

    private final static String getGroupId(String serviceNS, String serviceName) {
        // TESB-7782: Can't Export service if the targetNamespace of it contains a illegal character
        String servNS = URI.create(serviceNS).getSchemeSpecificPart().substring(2).replace(':', '.');
        if (!servNS.endsWith("/")) {
            servNS += '/';
        }
        return (servNS + serviceName).replace('/', '.');
    }

    public String getTmpFolderPath() {
        try {
        	File tmpExportFolder = File.createTempFile("service", null); //$NON-NLS-1$
            if (tmpExportFolder.exists() && tmpExportFolder.isFile()) {
                tmpExportFolder.delete();
                tmpExportFolder.mkdirs();
            }
            tmpExportFolder.deleteOnExit();
    		return tmpExportFolder.getAbsolutePath();
        } catch (IOException e) {
        	ExceptionHandler.process(e);
        	return null;
        }
    }

}
