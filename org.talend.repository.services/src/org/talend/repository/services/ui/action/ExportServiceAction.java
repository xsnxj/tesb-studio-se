package org.talend.repository.services.ui.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.progress.IProgressService;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.designer.publish.core.SaveAction;
import org.talend.designer.publish.core.models.BundleModel;
import org.talend.designer.publish.core.models.FeaturesModel;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.ui.scriptmanager.ServiceExportManager;
import org.talend.repository.ui.utils.ZipToFile;
import org.talend.repository.ui.wizards.exportjob.action.JobExportAction;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;

public class ExportServiceAction extends WorkspaceJob {

    private List<RepositoryNode> nodes;

    private String serviceVersion;

    private ServiceExportManager serviceManager;

    private final IFile serviceWsdl;

    private final Map<String, String> operations;

    public ExportServiceAction(String name, List<RepositoryNode> nodes, String jobVersion, ServiceExportManager jobManager,
            IFile serviceWsdl, Map<String, String> operations) {
        super(name);
        this.nodes = nodes;
        this.serviceVersion = jobVersion;
        this.serviceManager = jobManager;
        this.serviceWsdl = serviceWsdl;
        this.operations = operations;
    }

    @Override
    public IStatus runInWorkspace(IProgressMonitor arg0) throws CoreException {
        final String serviceNS = serviceManager.getDefinition(serviceWsdl.getLocation().toOSString()).getTargetNamespace();
        final String serviceName = serviceManager.getServiceName();
        // TODO

        String groupId = getGroupId(serviceNS, serviceName);
        IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
        String directoryName = serviceManager.getRootFolderName(serviceManager.getDestinationPath());
        Map<String, String> bundles = new HashMap<String, String>();
        for (RepositoryNode node : nodes) {
            JobScriptsManager manager = serviceManager.getJobManager(node, groupId, serviceVersion);
            JobExportAction job = new JobExportAction(Arrays.asList(new RepositoryNode[] { node }), serviceVersion, manager,
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
            final String artefactName = "control-bundle";
            bundles.put(artefactName, generateControlBundle(groupId, artefactName));
			String feature = generateFeature(serviceName, groupId, bundles);
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
        serviceManager.getManifest(artefactName).write(out);
        out.close();
        // wsdl
        File wsdl = new File(temp, serviceWsdl.getName());
        FilesUtils.copyFile(serviceWsdl.getLocation().toFile(), wsdl);
        // spring
        File spring = new File(temp, "spring");
        spring.mkdirs();
        serviceManager.createSpringBeans(new File(temp, "beans.xml").getAbsolutePath(), operations, wsdl);
        String fileName = artefactName + "-" + serviceVersion + ".jar";
        File file = new File(serviceManager.getFilePath(groupId, artefactName, serviceVersion), fileName);
        try {
            ZipToFile.zipFile(temp.getAbsolutePath(), file.getAbsolutePath());
            FilesUtils.removeFolder(temp, true);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return file.getAbsolutePath();
    }

	private String generateFeature(final String serviceName, String groupId,
			Map<String, String> bundles) throws IOException {
		FeaturesModel feature = new FeaturesModel(groupId, serviceName,
				serviceVersion);
		for (Map.Entry<String, String> entry : bundles.entrySet()) {
			File jarFile = new File(entry.getValue());
			BundleModel model = new BundleModel(jarFile, groupId,
					entry.getKey(), serviceVersion);
			feature.addSubBundle(model);
		}
		String artefactName = serviceName + "-feature";
		File filePath = serviceManager.getFilePath(groupId, artefactName,
				serviceVersion);
		String fileName = artefactName + "-" + serviceVersion + "-feature.xml";
		String featureFilePath = new File(filePath, fileName).getAbsolutePath();
		SaveAction.saveFeature(featureFilePath, feature);
		return featureFilePath;
	}

    private String getGroupId(String serviceNS, String serviceName) {
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
