package org.talend.repository.services.ui.action;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.designer.runprocess.IProcessor;
import org.talend.repository.documentation.ArchiveFileExportOperationFullPath;
import org.talend.repository.documentation.ExportFileResource;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.ui.scriptmanager.ServiceExportWithMavenManager;
import org.talend.repository.ui.utils.ZipToFile;
import org.talend.repository.ui.wizards.exportjob.action.JobExportAction;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager.ExportChoice;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.OSGIJavaScriptForESBWithMavenManager;
import org.talend.resources.util.EMavenBuildScriptProperties;

/**
 * DOC ycbai class global comment. Detailled comment
 */
public class ExportServiceWithMavenAction extends ExportServiceAction {

    private ServiceExportWithMavenManager manager;

    /**
     * DOC ycbai ExportServiceWithMavenAction constructor comment.
     * 
     * @param exportChoiceMap
     * @param node
     * @param targetPath
     * @throws InvocationTargetException
     */
    public ExportServiceWithMavenAction(ServiceExportWithMavenManager manager, Map<ExportChoice, Object> exportChoiceMap,
            RepositoryNode node, String targetPath) throws InvocationTargetException {
        super(exportChoiceMap, node, targetPath);
        this.manager = manager;
        this.manager.setMavenGroupId(this.getGroupId());
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        super.run(monitor);
        try {
            addServiceFilesToExport();
            addJobFilesToExport(monitor);
            addMavenFilesToExport(monitor);
            super.processFinalResult(serviceManager.getDestinationPath());
        } catch (Exception e) {
            ExceptionHandler.process(e);
        } finally {
            super.clean();
        }
    }

    private void addMavenFilesToExport(IProgressMonitor monitor) throws Exception {
        List<ExportFileResource> list = new ArrayList<ExportFileResource>();
        IRepositoryViewObject repositoryObject = serviceNode.getObject();
        Item item = repositoryObject.getProperty().getItem();
        ExportFileResource resource = new ExportFileResource(item, ""); //$NON-NLS-1$
        resource.setNode(serviceNode);
        list.add(resource);
        manager.setDestinationPath(serviceManager.getDestinationPath());
        String tempDestinationPath = getTempDestinationValue();
        List<ExportFileResource> resourcesToExport = manager.getExportResources(list.toArray(new ExportFileResource[0]));
        ArchiveFileExportOperationFullPath exporterOperation = new ArchiveFileExportOperationFullPath(resourcesToExport,
                tempDestinationPath);
        manager.setTopFolder(resourcesToExport);
        executeExportOperation(exporterOperation, monitor);
        ZipToFile.unZipFile(tempDestinationPath, tempFolder);
        FilesUtils.removeFile(new File(tempDestinationPath));
    }

    private void addServiceFilesToExport() throws Exception {
        String resourcesPath = "src/main/resources"; //$NON-NLS-1$
        String repositoryPath = "repository"; //$NON-NLS-1$
        // control bundle file
        ZipToFile.unZipFile(getControlBundleFilePath(), tempFolder + PATH_SEPERATOR + resourcesPath);
        // feature file
        FilesUtils.copyFile(new File(getFeatureFilePath()), new File(tempFolder + PATH_SEPERATOR + resourcesPath
                + "/feature/feature.xml")); //$NON-NLS-1$
        FilesUtils.removeFolder(tempFolder + PATH_SEPERATOR + repositoryPath, true);
    }

    private void addJobFilesToExport(IProgressMonitor monitor) throws Exception {
        String directoryName = serviceManager.getRootFolderName(tempFolder);
        exportChoiceMap.put(ExportChoice.needJobItem, false);
        for (RepositoryNode node : nodes) {
            JobScriptsManager osgiManager = new OSGIJavaScriptForESBWithMavenManager(exportChoiceMap, "Default", "all", //$NON-NLS-1$  //$NON-NLS-2$
                    IProcessor.NO_STATISTICS, IProcessor.NO_TRACES) {

                @Override
                protected Map<String, String> getMainMavenProperties(Item item) {
                    Map<String, String> mavenPropertiesMap = super.getMainMavenProperties(item);
                    mavenPropertiesMap.put(EMavenBuildScriptProperties.ItemGroupName.getVarScript(), getGroupId());
                    return mavenPropertiesMap;
                }

                @Override
                protected void setMavenBuildScriptProperties(Document pomDocument, Map<String, String> mavenPropertiesMap) {
                    super.setMavenBuildScriptProperties(pomDocument, mavenPropertiesMap);
                    String itemName = mavenPropertiesMap.get(EMavenBuildScriptProperties.ItemName.getVarScript());
                    if (itemName != null && pomDocument != null) {
                        Element rootElement = pomDocument.getRootElement();
                        // Because re-use the osgi bundle for service, but for artifactId, there is no "-bundle"
                        // suffix. TDI-23491
                        Element artifactIdEle = rootElement.element("artifactId"); //$NON-NLS-1$
                        if (artifactIdEle != null) {
                            artifactIdEle.setText(itemName);
                        }
                    }
                }

            };
            String artefactName = serviceManager.getNodeLabel(node);
            String version = node.getObject().getVersion();
            // String fileName = artefactName + "-" + version;
            File destFile = new File(tempFolder + PATH_SEPERATOR + artefactName + osgiManager.getOutputSuffix());
            String destinationPath = destFile.getAbsolutePath();
            osgiManager.setDestinationPath(destinationPath);
            JobExportAction job = new JobExportAction(Collections.singletonList(node), version, osgiManager, directoryName);
            job.run(monitor);
            ZipToFile.unZipFile(destinationPath, tempFolder + PATH_SEPERATOR + ServiceExportWithMavenManager.OPERATIONS_PATH
                    + artefactName);
            FilesUtils.removeFile(destFile);
        }
    }

    /**
     * Export the passed resource and recursively export all of its child resources (iff it's a container). Answer a
     * boolean indicating success.
     */
    private boolean executeExportOperation(ArchiveFileExportOperationFullPath op, IProgressMonitor monitor) {
        op.setCreateLeadupStructure(true);
        op.setUseCompression(true);

        try {
            op.run(monitor);
        } catch (InvocationTargetException e) {
            ExceptionHandler.process(e);
        } catch (InterruptedException e) {
            return false;
        }

        IStatus status = op.getStatus();
        if (!status.isOK()) {
            ErrorDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "", null, //$NON-NLS-1$
                    status);
            return false;
        }

        return true;
    }

    protected String getTempDestinationValue() {
        String idealSuffix = manager.getOutputSuffix();
        String destinationText = manager.getDestinationPath();
        String tempDestination = getTmpFolderPath();
        destinationText = destinationText.replace("\\", PATH_SEPERATOR); //$NON-NLS-1$
        if (destinationText.indexOf(PATH_SEPERATOR) != -1) {
            String fileName = destinationText.substring(destinationText.lastIndexOf(PATH_SEPERATOR) + 1);
            tempDestination = tempDestination + PATH_SEPERATOR + fileName;
        }
        if (tempDestination.length() != 0 && !tempDestination.endsWith(File.separator)) {
            int dotIndex = tempDestination.lastIndexOf('.');
            if (dotIndex != -1) {
                int pathSepIndex = tempDestination.lastIndexOf(PATH_SEPERATOR);
                if (pathSepIndex != -1 && dotIndex < pathSepIndex) {
                    tempDestination += idealSuffix;
                }
            } else {
                tempDestination += idealSuffix;
            }
        }

        return tempDestination;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.services.ui.action.ExportServiceAction#processFinalResult(java.lang.String)
     */
    @Override
    protected void processFinalResult(String destinationPath) throws IOException {
        destinationPath = destinationPath.replace("\\", PATH_SEPERATOR); //$NON-NLS-1$
        if (destinationPath.indexOf(PATH_SEPERATOR) != -1) {
            destinationPath = destinationPath.substring(destinationPath.lastIndexOf(PATH_SEPERATOR));
        }
        destinationPath = tempFolder + destinationPath;
        super.processFinalResult(destinationPath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.services.ui.action.ExportServiceAction#clean()
     */
    @Override
    protected void clean() {
    }

}
