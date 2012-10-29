// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.ui.wizards.export;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.repository.constants.FileConstants;
import org.talend.designer.runprocess.ProcessorException;
import org.talend.repository.constants.IExportJobConstants;
import org.talend.repository.documentation.ExportFileResource;
import org.talend.repository.preference.constants.IExportRoutePrefConstants;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JavaScriptForESBWithMavenManager;
import org.talend.resource.IExportRouteResourcesService;

/**
 * DOC ycbai class global comment. Detailled comment
 */
public class KarafJavaScriptForESBWithMavenManager extends JavaScriptForESBWithMavenManager {

    private final static String PATH_SEPERATOR = "/"; //$NON-NLS-1$

    private String destinationKar;

    public KarafJavaScriptForESBWithMavenManager(Map<ExportChoice, Object> exportChoiceMap, String destinationKar,
            String contextName, String launcher, int statisticPort, int tracePort) {
        super(exportChoiceMap, contextName, launcher, statisticPort, tracePort);
        this.destinationKar = destinationKar;
    }

    @Override
    public List<ExportFileResource> getExportResources(ExportFileResource[] processes, String... codeOptions)
            throws ProcessorException {
        List<ExportFileResource> list = super.getExportResources(processes, codeOptions);

        addKarFileToExport(list);

        addFeatureFileToExport(list, processes);

        return list;
    }

    @Override
    protected void addMavenBuildScripts(List<URL> scriptsUrls, Map<String, String> mavenPropertiesMap) {
        IExportRouteResourcesService resourcesService = null;
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IExportRouteResourcesService.class)) {
            resourcesService = (IExportRouteResourcesService) GlobalServiceRegister.getDefault().getService(
                    IExportRouteResourcesService.class);
        }
        if (resourcesService == null) {
            return;
        }

        File mavenBuildFile = new File(getTmpFolder() + PATH_SEPARATOR + IExportJobConstants.MAVEN_BUILD_FILE_NAME);
        File mavenBuildBundleFile = new File(getTmpFolder() + PATH_SEPARATOR
                + IExportJobConstants.MAVEN_KARAF_BUILD_BUNDLE_FILE_NAME);
        File mavenBuildFeatureFile = new File(getTmpFolder() + PATH_SEPARATOR
                + IExportJobConstants.MAVEN_KARAF_BUILD_FEATURE_FILE_NAME);
        File mavenBuildParentFile = new File(getTmpFolder() + PATH_SEPARATOR
                + IExportJobConstants.MAVEN_KARAF_BUILD_PARENT_FILE_NAME);

        try {
            String mavenScript = resourcesService
                    .getScriptFromPreferenceStore(IExportRoutePrefConstants.MAVEN_KARAF_SCRIPT_TEMPLATE);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildFile, mavenPropertiesMap, false, true);
                scriptsUrls.add(mavenBuildFile.toURL());
            }

            mavenScript = resourcesService
                    .getScriptFromPreferenceStore(IExportRoutePrefConstants.MAVEN_KARAF_SCRIPT_TEMPLATE_BUNDLE);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildBundleFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildBundleFile, mavenPropertiesMap, true, false);
                scriptsUrls.add(mavenBuildBundleFile.toURL());
            }

            mavenScript = resourcesService
                    .getScriptFromPreferenceStore(IExportRoutePrefConstants.MAVEN_KARAF_SCRIPT_TEMPLATE_FEATURE);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildFeatureFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildFeatureFile, mavenPropertiesMap);
                scriptsUrls.add(mavenBuildFeatureFile.toURL());
            }

            mavenScript = resourcesService
                    .getScriptFromPreferenceStore(IExportRoutePrefConstants.MAVEN_KARAF_SCRIPT_TEMPLATE_PARENT);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildParentFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildParentFile, mavenPropertiesMap);
                scriptsUrls.add(mavenBuildParentFile.toURL());
            }
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
    }

    private void addKarFileToExport(List<ExportFileResource> list) {
        if (destinationKar != null) {
            File karFile = new File(destinationKar);
            if (karFile.exists()) {
                ExportFileResource karFileResource = new ExportFileResource(null, ""); //$NON-NLS-1$
                try {
                    karFileResource.addResource("", karFile.toURL()); //$NON-NLS-1$
                } catch (MalformedURLException e) {
                    ExceptionHandler.process(e);
                }
                list.add(karFileResource);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void addFeatureFileToExport(List<ExportFileResource> list, ExportFileResource[] processes) {
        if (destinationKar != null) {
            File karFile = new File(destinationKar);
            if (karFile.exists()) {
                ProcessItem processItem = (ProcessItem) processes[0].getItem();
                String projectName = getCorrespondingProjectName(processItem);
                String jobName = processItem.getProperty().getLabel();
                String jobVersion = processItem.getProperty().getVersion();
                StringBuilder sb = new StringBuilder();
                sb.append("repository/").append(projectName).append(PATH_SEPERATOR).append(jobName).append(PATH_SEPERATOR); //$NON-NLS-1$
                String featurePath = sb.append(jobName).append("-feature/").append(jobVersion).append(PATH_SEPERATOR) //$NON-NLS-1$
                        .append(jobName).append("-feature-").append(jobVersion).append("-feature.xml").toString(); //$NON-NLS-1$ //$NON-NLS-2$
                ExportFileResource featureFileResource = new ExportFileResource(null, ""); //$NON-NLS-1$
                try {
                    ZipFile zipFile = new ZipFile(karFile);
                    ZipEntry zipEntry = zipFile.getEntry(featurePath);
                    if (zipEntry != null) {
                        InputStream in = null;
                        try {
                            in = zipFile.getInputStream(zipEntry);
                            File featureFile = new File(getTmpFolder() + "feature/feature.xml"); //$NON-NLS-1$
                            FilesUtils.copyFile(in, featureFile);
                            featureFileResource.addResource(MAVEN_RESOURCES_PATH + "feature", featureFile.toURL()); //$NON-NLS-1$
                        } finally {
                            zipFile.close();
                        }
                    }
                } catch (Exception e) {
                    ExceptionHandler.process(e);
                }
                list.add(featureFileResource);
            }
        }
    }

    @Override
    public String getOutputSuffix() {
        return FileConstants.ZIP_FILE_SUFFIX;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobJavaScriptsManager#addRoutinesSourceCodes(org.talend
     * .repository.documentation.ExportFileResource[], org.talend.repository.documentation.ExportFileResource,
     * org.eclipse.core.resources.IProject, boolean)
     */
    @Override
    protected void addRoutinesSourceCodes(ExportFileResource[] process, ExportFileResource resource, IProject javaProject,
            boolean useBeans) throws Exception {
        if (useBeans) {
            super.addRoutinesSourceCodes(process, resource, javaProject, true);
            // FIXME, need add routines also, else the maven should be error to execute
            super.addRoutinesSourceCodes(process, resource, javaProject, false);
        }
    }
}
