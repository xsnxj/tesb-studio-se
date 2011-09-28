package org.talend.repository.services.ui.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.progress.IProgressService;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.designer.publish.core.SaveAction;
import org.talend.designer.publish.core.models.BundleModel;
import org.talend.designer.publish.core.models.FeaturesModel;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.services.action.OpenWSDLEditorAction;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.ui.scriptmanager.ServiceExportManager;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.ui.utils.ZipToFile;
import org.talend.repository.ui.wizards.exportjob.action.JobExportAction;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;

public class ExportServiceAction extends WorkspaceJob {

    private List<RepositoryNode> nodes = new ArrayList<RepositoryNode>();

    private String serviceVersion;

	protected ServiceExportManager serviceManager;

    private IFile serviceWsdl;

    private final Map<String, String> operations = new HashMap<String, String>();

	private String serviceName;

	private String groupId;

    public ExportServiceAction(RepositoryNode node) throws CoreException {
    	this(node, null);
    }

    	
	public ExportServiceAction(RepositoryNode node, String targetPath) throws CoreException {
    	super("Exporting service");
        if ((node.getType() == ENodeType.REPOSITORY_ELEMENT) &&
        		(node.getProperties(EProperties.CONTENT_TYPE) == ESBRepositoryNodeType.SERVICES)) 
        {
            IRepositoryViewObject repositoryObject = node.getObject();
            if (node.getProperties(EProperties.CONTENT_TYPE) == ESBRepositoryNodeType.SERVICES) {
            	serviceName = repositoryObject.getLabel();
                serviceVersion = repositoryObject.getVersion();
                serviceWsdl  = OpenWSDLEditorAction.getWsdlFile(node);
            } 
            ServiceItem serviceItem = (ServiceItem) node.getObject().getProperty().getItem();
            ServiceConnection serviceConnection = serviceItem.getServiceConnection();
			EList<ServicePort> listPort = serviceConnection.getServicePort();
            for (ServicePort port : listPort) {
                List<ServiceOperation> listOperation = port.getServiceOperation();
                for (ServiceOperation operation : listOperation) {
                    if (operation.getReferenceJobId() != null && !operation.getReferenceJobId().equals("")) {
                        String[] label = operation.getLabel().split("-"); //TODO: do it correct way!!!
						operations.put(label[0], label[1]);
                        nodes.add(RepositoryNodeUtilities.getRepositoryNode(operation.getReferenceJobId(), false));
                    }
                }
            }
        }
        if (targetPath == null) {
            String bundleName = serviceName + "-" + serviceVersion + "/"; //TODO: change / to .kar
            String userDir = System.getProperty("user.dir"); //$NON-NLS-1$
            IPath path = new Path(userDir).append(bundleName);
            targetPath = path.toOSString();
        }
		this.serviceManager = new ServiceExportManager();
        serviceManager.setDestinationPath(targetPath);
        final String serviceNS = ServiceExportManager.getDefinition(serviceWsdl.getLocation().toOSString()).getTargetNamespace();
        groupId = getGroupId(serviceNS, getServiceName());
    }
    
    @Override
    public IStatus runInWorkspace(IProgressMonitor arg0) throws CoreException {
        IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
        String directoryName = serviceManager.getRootFolderName(serviceManager.getDestinationPath());
        Map<String, String> bundles = new HashMap<String, String>();
        for (RepositoryNode node : nodes) {
            JobScriptsManager manager = serviceManager.getJobManager(node, getGroupId(), getServiceVersion());
            JobExportAction job = new JobExportAction(Arrays.asList(new RepositoryNode[] { node }), node.getObject().getVersion(), manager,
                    directoryName);
            try {
                progressService.run(false, true, job);
            } catch (InvocationTargetException e) {
                ExceptionHandler.process(e);
            } catch (InterruptedException e) {
                ExceptionHandler.process(e);
            }
            bundles.put(serviceManager.getNodeLabel(node), manager.getDestinationPath());
        }
        try {
            final String artefactName = getServiceName()+"-control-bundle";
            bundles.put(artefactName, generateControlBundle(getGroupId(), artefactName));
			processFeature(generateFeature(bundles));
		    String destinationPath = serviceManager.getDestinationPath();
		    processFinalResult(destinationPath);

        } catch (IOException e) {
            return StatusUtil.newStatus(IStatus.ERROR, e.getLocalizedMessage(), e);
        }

        return StatusUtil.newStatus(IStatus.OK, "Done", null);
    }

    private String generateControlBundle(String groupId, String artefactName) throws IOException, CoreException {
        File temp = new File(serviceManager.getDestinationPath(), "temp");
        File metaInf = new File(temp, "META-INF");
        metaInf.mkdirs();
        // manifest
        FileOutputStream out = new FileOutputStream(new File(metaInf, "MANIFEST.MF"));
        serviceManager.getManifest(artefactName, getServiceVersion()).write(out);
        out.close();
        // wsdl
        File wsdl = new File(temp, serviceWsdl.getName());
        FilesUtils.copyFile(serviceWsdl.getLocation().toFile(), wsdl);
        // spring
        File spring = new File(metaInf, "spring");
        spring.mkdirs();
        serviceManager.createSpringBeans(new File(spring, "beans.xml").getAbsolutePath(), operations, wsdl, getServiceName());
        String fileName = artefactName + "-" + getServiceVersion() + ".jar";
        File file = new File(serviceManager.getFilePath(groupId, artefactName, getServiceVersion()), fileName);
        try {
            ZipToFile.zipFile(temp.getAbsolutePath(), file.getAbsolutePath());
            FilesUtils.removeFolder(temp, true);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return file.getAbsolutePath();
    }

	private FeaturesModel generateFeature(Map<String, String> bundles) throws IOException {
		FeaturesModel feature = new FeaturesModel(getGroupId(), getServiceName(),
				getServiceVersion());
		for (Map.Entry<String, String> entry : bundles.entrySet()) {
			File jarFile = new File(entry.getValue());
			BundleModel model = new BundleModel(jarFile, getGroupId(),
					entry.getKey(), getServiceVersion());
			feature.addSubBundle(model);
		}
		return feature;
	}
	
	protected void processFeature(FeaturesModel feature) throws IOException {
		String artefactName = getServiceName() + "-feature";
		File filePath = serviceManager.getFilePath(getGroupId(), artefactName,
				getServiceVersion());
		String fileName = artefactName + "-" + getServiceVersion() + "-feature.xml";
		String featureFilePath = new File(filePath, fileName).getAbsolutePath();
		SaveAction.saveFeature(featureFilePath, feature);
	}
	
	protected void processFinalResult(String destinationPath) throws IOException {
		try {
		    File destination = new File(destinationPath);
		    String s = destination.getAbsolutePath()+".kar";
			ZipToFile.zipFile(destinationPath, s);
		    FilesUtils.removeFolder(destinationPath, true);
		} catch (Exception e) {
		    throw new IOException(e.getMessage());
		}
	}

	public String getServiceVersion() {
		return serviceVersion;
	}


	public String getServiceName() {
		return serviceName;
	}


	public String getGroupId() {
		return groupId;
	}


	//DO NOT OVERRIDE!! CALLED FROM CONSTRUCTOR
    private final String getGroupId(String serviceNS, String serviceName) {  
        String schemeId;
        try {
            schemeId = new URI(serviceNS).getScheme() + "://";
        } catch (URISyntaxException e1) {
            schemeId = "http://";
        }
        String servNS = serviceNS.replace(schemeId, "");
        if (!servNS.endsWith("/")) {
            servNS += "/";
        }
        return (servNS + serviceName).replace('/', '.');
    }

}
