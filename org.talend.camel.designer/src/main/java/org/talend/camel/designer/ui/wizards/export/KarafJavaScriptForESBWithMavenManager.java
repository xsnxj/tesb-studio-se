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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.camel.designer.CamelDesignerPlugin;
import org.talend.camel.designer.prefs.ICamelPrefConstants;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.properties.ProcessItem;
import org.talend.designer.runprocess.LastGenerationInfo;
import org.talend.designer.runprocess.ProcessorException;
import org.talend.repository.constants.ExportJobConstants;
import org.talend.repository.documentation.ExportFileResource;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JobJavaScriptOSGIForESBManager;
import org.talend.resource.IResourceService;
import org.talend.resources.util.EMavenBuildScriptProperties;

/**
 * DOC ycbai class global comment. Detailled comment
 */
public class KarafJavaScriptForESBWithMavenManager extends JobJavaScriptOSGIForESBManager {

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

        String privatePackage = "", exportService = ""; //$NON-NLS-1$ //$NON-NLS-2$

        Iterator<ExportFileResource> it = list.iterator();
        while (it.hasNext()) {
            ExportFileResource resource = it.next();
            if ("".equals(resource.getDirectoryName())) { //$NON-NLS-1$
                Map<String, Set<URL>> newResourcesMap = new HashMap<String, Set<URL>>();
                for (String path : resource.getRelativePathList()) {
                    if (path.startsWith("OSGI-INF")) { //$NON-NLS-1$
                        Set<URL> urls = resource.getResourcesByRelativePath(path);
                        // put OSGI_INF to /src/main/resources/
                        newResourcesMap.put("src/main/resources/" + path, urls); //$NON-NLS-1$
                    }
                }
                resource.removeAllMap();
                for (String path : newResourcesMap.keySet()) {
                    resource.addResources(path, new ArrayList<URL>(newResourcesMap.get(path)));
                }
            } else if (LIBRARY_FOLDER_NAME.equals(resource.getDirectoryName())) {
                resource.setDirectoryName("src/main/resources/lib/"); //$NON-NLS-1$
            } else if (META_INF.equals(resource.getDirectoryName())) {
                it.remove();
                if (!resource.getAllResources().isEmpty()) {
                    Set<URL> urls = resource.getAllResources().iterator().next();
                    if (!urls.isEmpty()) {
                        URL manifestURL = urls.iterator().next();
                        try {
                            File manifestFile = new File(manifestURL.toURI());
                            FileInputStream fis = new FileInputStream(manifestFile);
                            Manifest manifest = new Manifest(fis);
                            exportService = manifest.getMainAttributes().getValue("Export-Service"); //$NON-NLS-1$
                            privatePackage = manifest.getMainAttributes().getValue("Private-Package"); //$NON-NLS-1$
                            fis.close();
                        } catch (URISyntaxException e) {
                            ExceptionHandler.process(e);
                        } catch (FileNotFoundException e) {
                            ExceptionHandler.process(e);
                        } catch (IOException e) {
                            ExceptionHandler.process(e);
                        }
                    }
                }
            }
        }

        addSourceCodeToExport(list, processes, codeOptions);

        addMavenScriptToExport(list, processes, privatePackage, exportService);

        addKarFileToExport(list);

        addFeatureFileToExport(list, processes);

        return list;
    }

    private void addSourceCodeToExport(List<ExportFileResource> list, ExportFileResource[] processes, String... codeOptions) {
        List<URL> noUseJustForSimplifyChange = new ArrayList<URL>();
        exportChoice.put(ExportChoice.needSourceCode, Boolean.TRUE);
        exportChoice.put(ExportChoice.needContext, Boolean.TRUE);
        exportChoice.put(ExportChoice.needMavenScript, Boolean.FALSE);
        processes[0].removeAllMap();
        posExportResource(processes, exportChoice, contextName, launcher, statisticPort, tracePort, 0, (IProcess) null,
                (ProcessItem) processes[0].getItem(), processes[0].getItem().getProperty().getVersion(),
                noUseJustForSimplifyChange, codeOptions);
        Map<String, Set<URL>> newResourcesMap = new HashMap<String, Set<URL>>();
        for (String path : processes[0].getRelativePathList()) {
            Set<URL> urls = processes[0].getResourcesByRelativePath(path);
            if (path.startsWith("src/")) { //$NON-NLS-1$
                newResourcesMap.put(path.replace("src/", "src/main/java/"), urls); //$NON-NLS-1$//$NON-NLS-2$
            } else {
                newResourcesMap.put("src/main/resources/" + path, urls); //$NON-NLS-1$
            }
        }
        ExportFileResource sourceCodeResource = new ExportFileResource(null, ""); //$NON-NLS-1$
        for (String path : newResourcesMap.keySet()) {
            sourceCodeResource.addResources(path, new ArrayList<URL>(newResourcesMap.get(path)));
        }
        list.add(sourceCodeResource);
    }

    private void addMavenScriptToExport(List<ExportFileResource> list, ExportFileResource[] processes, String privatePackage,
            String exportService) {
        List<URL> scriptsUrls = new ArrayList<URL>();
        addMavenBuildScripts(scriptsUrls, (ProcessItem) processes[0].getItem(),
                processes[0].getItem().getProperty().getVersion(), privatePackage, exportService);
        ExportFileResource mavenResource = new ExportFileResource(null, ""); //$NON-NLS-1$
        mavenResource.addResources("", scriptsUrls); //$NON-NLS-1$
        list.add(mavenResource);
    }

    private void addMavenBuildScripts(List<URL> scriptsUrls, ProcessItem processItem, String selectedJobVersion,
            String privatePackage, String exportService) {
        IResourceService resourceService = (IResourceService) GlobalServiceRegister.getDefault().getService(
                IResourceService.class);
        if (resourceService == null) {
            return;
        }

        String projectName = getCorrespondingProjectName(processItem);
        String jobName = processItem.getProperty().getLabel();
        String jobVersion = processItem.getProperty().getVersion();

        // set the maven properties
        final Map<String, String> mavenPropertiesMap = new HashMap<String, String>();
        mavenPropertiesMap.put(EMavenBuildScriptProperties.ItemProjectName.getVarScript(), projectName);
        mavenPropertiesMap.put(EMavenBuildScriptProperties.ItemName.getVarScript(), jobName);
        mavenPropertiesMap.put(EMavenBuildScriptProperties.ItemVersion.getVarScript(), jobVersion);
        mavenPropertiesMap.put(EMavenBuildScriptProperties.BundleConfigPrivatePackage.getVarScript(), privatePackage);
        mavenPropertiesMap.put(EMavenBuildScriptProperties.BundleConfigExportService.getVarScript(), exportService);

        Set<ModuleNeeded> neededModules = LastGenerationInfo.getInstance().getModulesNeededWithSubjobPerJob(
                processItem.getProperty().getId(), selectedJobVersion);

        File mavenBuildFile = new File(getTmpFolder() + PATH_SEPARATOR + ExportJobConstants.MAVEN_BUILD_FILE_NAME);
        File mavenBuildBundleFile = new File(getTmpFolder() + PATH_SEPARATOR
                + ExportJobConstants.MAVEN_KARAF_BUILD_BUNDLE_FILE_NAME);
        File mavenBuildFeatureFile = new File(getTmpFolder() + PATH_SEPARATOR
                + ExportJobConstants.MAVEN_KARAF_BUILD_FEATURE_FILE_NAME);
        File mavenBuildParentFile = new File(getTmpFolder() + PATH_SEPARATOR
                + ExportJobConstants.MAVEN_KARAF_BUILD_PARENT_FILE_NAME);

        try {

            IPreferenceStore mavenPreferenceStore = CamelDesignerPlugin.getDefault().getPreferenceStore();
            String mavenScript = mavenPreferenceStore.getString(ICamelPrefConstants.MAVEN_KARAF_SCRIPT_TEMPLATE);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildFile, mavenPropertiesMap);
                scriptsUrls.add(mavenBuildFile.toURL());
            }

            mavenScript = mavenPreferenceStore.getString(ICamelPrefConstants.MAVEN_KARAF_SCRIPT_TEMPLATE_BUNDLE);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildBundleFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildBundleFile, mavenPropertiesMap, neededModules, MAVEN_PROP_LIB_PATH);
                scriptsUrls.add(mavenBuildBundleFile.toURL());
            }

            mavenScript = mavenPreferenceStore.getString(ICamelPrefConstants.MAVEN_KARAF_SCRIPT_TEMPLATE_FEATURE);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildFeatureFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildFeatureFile, mavenPropertiesMap);
                scriptsUrls.add(mavenBuildFeatureFile.toURL());
            }

            mavenScript = mavenPreferenceStore.getString(ICamelPrefConstants.MAVEN_KARAF_SCRIPT_TEMPLATE_PARENT);
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
                        .append(jobName).append("-feature-").append(jobVersion).append("-feature.xml").toString(); //$NON-NLS-1$
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
                            featureFileResource.addResource("src/main/resources/feature", featureFile.toURL()); //$NON-NLS-1$
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
        return ".zip"; //$NON-NLS-1$
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
