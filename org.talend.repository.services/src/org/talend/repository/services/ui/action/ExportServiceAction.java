package org.talend.repository.services.ui.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.general.Project;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.publish.core.SaveAction;
import org.talend.designer.publish.core.models.BundleModel;
import org.talend.designer.publish.core.models.FeaturesModel;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.ResourceModelUtils;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.ui.scriptmanager.ServiceExportManager;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.services.utils.WSDLUtils;
import org.talend.repository.ui.utils.ZipToFile;
import org.talend.repository.ui.wizards.exportjob.action.JobExportAction;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager.ExportChoice;

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

    private static String JOB_CONTROLLER_FEATURE = "talend-job-controller";

    private static String JOB_CONTROLLER_VERSION = "[5,6)";

    private final ServiceItem serviceItem;

    protected ServiceConnection serviceConnection;

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
                if (fileName.indexOf(".") != -1) { //$NON-NLS-1$
                    fileName = fileName.substring(0, fileName.lastIndexOf(".")); //$NON-NLS-1$
                }
                destinationPath = filePath + fileName + FileConstants.KAR_FILE_SUFFIX;
            }
        }

        try {
            String directoryName = serviceManager.getRootFolderName(tempFolder);
            Map<String, String> bundles = new HashMap<String, String>();
            for (RepositoryNode node : nodes) {
                JobScriptsManager manager = serviceManager.getJobManager(exportChoiceMap, tempFolder, node, getGroupId(),
                        getServiceVersion());
                JobExportAction job = new JobExportAction(Collections.singletonList(node), node.getObject().getVersion(),
                        getBundleVersion(), manager, directoryName, "Service"); //$NON-NLS-1$
                job.run(monitor);
                bundles.put(serviceManager.getNodeLabel(node), manager.getDestinationPath());
            }
            try {
                final String artefactName = getServiceName() + "-control-bundle"; //$NON-NLS-1$
                bundles.put(artefactName, generateControlBundle(getGroupId(), artefactName));
                processFeature(generateFeature(bundles));
                processFinalResult(destinationPath);
            } catch (Exception e) {
                throw new InvocationTargetException(e);
            }
        } finally {
            clean();
        }
    }

    private String generateControlBundle(String groupId, String artefactName) throws IOException, CoreException {
        File temp = new File(tempFolder, "temp");
        File metaInf = new File(temp, FileConstants.META_INF_FOLDER_NAME);
        metaInf.mkdirs();
        // manifest
        FileOutputStream out = new FileOutputStream(new File(metaInf, FileConstants.MANIFEST_MF_FILE_NAME));
        serviceManager.getManifest(artefactName, getBundleVersion()).write(out);
        out.close();
        // wsdl
        File wsdl = new File(temp, serviceWsdl.getName());
        FilesUtils.copyFile(serviceWsdl.getLocation().toFile(), wsdl);
        // spring
        File spring = new File(metaInf, "spring");
        spring.mkdirs();
        serviceManager.createSpringBeans(new File(spring, "beans.xml").getAbsolutePath(), ports, serviceConnection, serviceWsdl,
                getServiceName());
        String fileName = artefactName + "-" + getServiceVersion() + FileConstants.JAR_FILE_SUFFIX;
        File file = new File(serviceManager.getFilePath(tempFolder, groupId, artefactName, getServiceVersion()), fileName);
        try {
            ZipToFile.zipFile(temp.getAbsolutePath(), file.getAbsolutePath());
            FilesUtils.removeFolder(temp, true);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return file.getAbsolutePath();
    }

    private FeaturesModel generateFeature(Map<String, String> bundles) throws IOException {
        FeaturesModel feature = new FeaturesModel(getGroupId(), getServiceName(), getServiceVersion());
        for (Map.Entry<String, String> entry : bundles.entrySet()) {
            File jarFile = new File(entry.getValue());
            BundleModel model = new BundleModel(jarFile, getGroupId(), entry.getKey(), getServiceVersion());
            feature.addSubBundle(model);
        }
        // <feature version='[5,6)'>talend-job-controller</feature>
        feature.addSubFeature(JOB_CONTROLLER_FEATURE, JOB_CONTROLLER_VERSION);
        feature.setConfigName(getServiceName());
        feature.setContexts(ContextNodeRetriever.getContextsMap(serviceItem));
        return feature;
    }

    protected void processFeature(FeaturesModel feature) throws IOException {
        String artefactName = getServiceName() + "-feature";
        File filePath = serviceManager.getFilePath(tempFolder, getGroupId(), artefactName, getServiceVersion());
        String fileName = artefactName + "-" + getServiceVersion() + "-feature.xml";
        String featureFilePath = new File(filePath, fileName).getAbsolutePath();
        SaveAction.saveFeature(featureFilePath, feature);
    }

    protected void processFinalResult(String destinationPath) throws IOException {
        try {
            ZipToFile.zipFile(tempFolder, destinationPath);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    protected void clean() {
        FilesUtils.removeFolder(tempFolder, true);
    }

    protected String getControlBundleFilePath() {
        String artefactName = getServiceName() + "-control-bundle"; //$NON-NLS-1$
        String fileName = artefactName + "-" + getServiceVersion() + FileConstants.JAR_FILE_SUFFIX; //$NON-NLS-1$ 
        String filePath = new File(serviceManager.getFilePath(tempFolder, getGroupId(), artefactName, getServiceVersion()),
                fileName).getAbsolutePath();

        return filePath;
    }

    protected String getFeatureFilePath() {
        String artefactName = getServiceName() + "-feature"; //$NON-NLS-1$
        String fileName = artefactName + "-" + getServiceVersion() + "-feature.xml"; //$NON-NLS-1$ //$NON-NLS-2$
        String filePath = new File(serviceManager.getFilePath(tempFolder, getGroupId(), artefactName, getServiceVersion()),
                fileName).getAbsolutePath();

        return filePath;
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
        if (tempFolder != null) {
            return tempFolder;
        }
        Project project = ProjectManager.getInstance().getCurrentProject();
        String tmpFolderPath;
        try {
            IProject physProject = ResourceModelUtils.getProject(project);
            tmpFolderPath = physProject.getFolder("temp").getLocation().toPortableString(); //$NON-NLS-1$
        } catch (Exception e) {
            tmpFolderPath = System.getProperty("user.dir"); //$NON-NLS-1$
        }
        tmpFolderPath = tmpFolderPath + "/serviceExporter"; //$NON-NLS-1$
        File tmpFolder = new File(tmpFolderPath);
        if (!tmpFolder.exists()) {
            tmpFolder.mkdirs();
        }

        File tmpExportFolder = null;
        try {
            tmpExportFolder = File.createTempFile("service", null, tmpFolder); //$NON-NLS-1$
            if (tmpExportFolder.exists() && tmpExportFolder.isFile()) {
                tmpExportFolder.delete();
                tmpExportFolder.mkdirs();
            }
        } catch (IOException e) {
        } finally {
            if (tmpExportFolder != null) {
                tmpExportFolder.deleteOnExit();
            }
        }
        if (tmpExportFolder != null) {
            tmpFolder = tmpExportFolder;
        }

        return tmpFolder.getAbsolutePath();
    }

}
