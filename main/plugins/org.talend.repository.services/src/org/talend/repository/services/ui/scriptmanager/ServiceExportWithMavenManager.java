// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.services.ui.scriptmanager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.PluginChecker;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.ItemResourceUtil;
import org.talend.core.runtime.projectsetting.IProjectSettingPreferenceConstants;
import org.talend.core.runtime.projectsetting.IProjectSettingTemplateConstants;
import org.talend.designer.maven.model.TalendMavenConstants;
import org.talend.designer.maven.template.MavenTemplateManager;
import org.talend.designer.maven.utils.PomUtil;
import org.talend.designer.runprocess.ProcessorException;
import org.talend.repository.ProjectManager;
import org.talend.repository.documentation.ExportFileResource;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JavaScriptForESBWithMavenManager;
import org.talend.resources.util.EMavenBuildScriptProperties;
import org.talend.utils.io.FilesUtils;

/**
 * DOC ycbai class global comment. Detailled comment
 */
public class ServiceExportWithMavenManager extends JavaScriptForESBWithMavenManager {

    public static final String OPERATIONS_PATH = "operations/"; //$NON-NLS-1$

    private String mavenGroupId;

    public ServiceExportWithMavenManager(Map<ExportChoice, Object> exportChoiceMap, String contextName, String launcher,
            int statisticPort, int tracePort) {
        super(exportChoiceMap, contextName, launcher, statisticPort, tracePort);
    }

    /**
     * Getter for mavenGroupId.
     * 
     * @return the mavenGroupId
     */
    public String getMavenGroupId() {
        return this.mavenGroupId;
    }

    /**
     * Sets the mavenGroupId.
     * 
     * @param mavenGroupId the mavenGroupId to set
     */
    public void setMavenGroupId(String mavenGroupId) {
        this.mavenGroupId = mavenGroupId;
    }

    @Override
    public List<ExportFileResource> getExportResources(ExportFileResource[] processes, String... codeOptions)
            throws ProcessorException {
        List<ExportFileResource> list = new ArrayList<ExportFileResource>();

        analysisMavenModule(processes[0].getItem());

        Map<String, String> mavenPropertiesMap = getMainMavenProperties(processes[0].getItem());
        addMavenScriptToExport(list, processes, mavenPropertiesMap);

        return list;
    }

    @Override
    protected void addMavenBuildScripts(ExportFileResource[] processes, List<URL> scriptsUrls,
            Map<String, String> mavenPropertiesMap) {
        if (!PluginChecker.isPluginLoaded(PluginChecker.EXPORT_ROUTE_PLUGIN_ID)) {
            return;
        }

        Item item = processes[0].getItem();
        File templatePomFile = null, templateBundleFile = null, templateFeatureFile = null, templateParentFile = null;

        if (item != null) {
            IPath itemLocationPath = ItemResourceUtil.getItemLocationPath(item.getProperty());
            IFolder objectTypeFolder = ItemResourceUtil.getObjectTypeFolder(item.getProperty());
            if (itemLocationPath != null && objectTypeFolder != null) {
                IPath itemRelativePath = itemLocationPath.removeLastSegments(1).makeRelativeTo(objectTypeFolder.getLocation());
                templatePomFile = PomUtil.getTemplateFile(objectTypeFolder, itemRelativePath, TalendMavenConstants.POM_FILE_NAME);

                if (FilesUtils.allInSameFolder(templatePomFile,
                        IProjectSettingTemplateConstants.MAVEN_CONTROL_BUILD_BUNDLE_FILE_NAME,
                        IProjectSettingTemplateConstants.MAVEN_KARAF_BUILD_FEATURE_FILE_NAME,
                        IProjectSettingTemplateConstants.MAVEN_KARAF_BUILD_PARENT_FILE_NAME)) {

                    templateBundleFile = new File(templatePomFile.getParentFile(),
                            IProjectSettingTemplateConstants.MAVEN_CONTROL_BUILD_BUNDLE_FILE_NAME);
                    templateFeatureFile = new File(templatePomFile.getParentFile(),
                            IProjectSettingTemplateConstants.MAVEN_KARAF_BUILD_FEATURE_FILE_NAME);
                    templateParentFile = new File(templatePomFile.getParentFile(),
                            IProjectSettingTemplateConstants.MAVEN_KARAF_BUILD_PARENT_FILE_NAME);

                } else { // don't use template file from repository.
                    // other files is not init, so no need set null at all.
                    templatePomFile = null;
                }
            }
        }

        File mavenBuildFile = new File(getTmpFolder() + PATH_SEPARATOR + IProjectSettingTemplateConstants.POM_FILE_NAME);
        File mavenBuildBundleFile = new File(getTmpFolder() + PATH_SEPARATOR
                + IProjectSettingTemplateConstants.MAVEN_CONTROL_BUILD_BUNDLE_FILE_NAME);
        File mavenBuildFeatureFile = new File(getTmpFolder() + PATH_SEPARATOR
                + IProjectSettingTemplateConstants.MAVEN_KARAF_BUILD_FEATURE_FILE_NAME);
        File mavenBuildParentFile = new File(getTmpFolder() + PATH_SEPARATOR
                + IProjectSettingTemplateConstants.MAVEN_KARAF_BUILD_PARENT_FILE_NAME);

        try {
            final Map<String, Object> templateParameters = getTemplateParameters(item.getProperty());
            
            String mavenScript = getTemplateContent(templatePomFile,
                    IProjectSettingPreferenceConstants.TEMPLATE_SERVICES_KARAF_POM, PluginChecker.EXPORT_ROUTE_PLUGIN_ID,
                    IProjectSettingTemplateConstants.PATH_SERVICES + '/' + TalendMavenConstants.POM_FILE_NAME,templateParameters);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildFile, mavenPropertiesMap, false, true);
                scriptsUrls.add(mavenBuildFile.toURI().toURL());
            }

            mavenScript = getTemplateContent(templateBundleFile,
                    IProjectSettingPreferenceConstants.TEMPLATE_SERVICES_KARAF_BUNDLE, PluginChecker.EXPORT_ROUTE_PLUGIN_ID,
                    IProjectSettingTemplateConstants.PATH_SERVICES + '/'
                            + IProjectSettingTemplateConstants.MAVEN_CONTROL_BUILD_BUNDLE_FILE_NAME,templateParameters);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildBundleFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildBundleFile, mavenPropertiesMap, true, false);
                scriptsUrls.add(mavenBuildBundleFile.toURI().toURL());
            }

            mavenScript = getTemplateContent(templateFeatureFile,
                    IProjectSettingPreferenceConstants.TEMPLATE_SERVICES_KARAF_FEATURE, PluginChecker.EXPORT_ROUTE_PLUGIN_ID,
                    IProjectSettingTemplateConstants.PATH_SERVICES + '/'
                            + IProjectSettingTemplateConstants.MAVEN_KARAF_BUILD_FEATURE_FILE_NAME,templateParameters);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildFeatureFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildFeatureFile, mavenPropertiesMap);
                scriptsUrls.add(mavenBuildFeatureFile.toURI().toURL());
            }

            mavenScript = getTemplateContent(templateParentFile,
                    IProjectSettingPreferenceConstants.TEMPLATE_SERVICES_KARAF_PARENT, PluginChecker.EXPORT_ROUTE_PLUGIN_ID,
                    IProjectSettingTemplateConstants.PATH_SERVICES + '/'
                            + IProjectSettingTemplateConstants.MAVEN_KARAF_BUILD_PARENT_FILE_NAME,templateParameters);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildParentFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildParentFile, mavenPropertiesMap);
                scriptsUrls.add(mavenBuildParentFile.toURI().toURL());
            }
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
    }

    @Override
    public String getOutputSuffix() {
        return FileConstants.ZIP_FILE_SUFFIX;
    }

    @Override
    protected void analysisMavenModule(Item item) {
        if (item != null && item instanceof ServiceItem) {
            try {
                ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
                List<String> mavenModules = getMavenModules();
                ServiceItem serviceItem = (ServiceItem) item;
                ServiceConnection connection = (ServiceConnection) serviceItem.getConnection();
                EList<ServicePort> listPort = connection.getServicePort();
                for (ServicePort port : listPort) {
                    List<ServiceOperation> listOperation = port.getServiceOperation();
                    for (ServiceOperation operation : listOperation) {
                        if (StringUtils.isNotEmpty(operation.getReferenceJobId())) {
                            IRepositoryViewObject repObj = factory.getLastVersion(operation.getReferenceJobId());
                            if (repObj != null) {
                                String jobName = repObj.getLabel();
                                if (jobName != null && !mavenModules.contains(jobName)) {
                                    mavenModules.add(OPERATIONS_PATH + jobName);
                                }
                            }
                        }
                    }
                }
            } catch (PersistenceException e) {
                ExceptionHandler.process(e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobJavaScriptsManager#getMainMavenProperties(org.talend
     * .core.model.properties.Item)
     */
    @Override
    protected Map<String, String> getMainMavenProperties(Item item) {
        Map<String, String> mavenPropertiesMap = super.getMainMavenProperties(item);
        if (getMavenGroupId() != null) {
            mavenPropertiesMap.put(EMavenBuildScriptProperties.ItemGroupName.getVarScript(), getMavenGroupId());
        }
        return mavenPropertiesMap;
    }

    private static Map<String, Object> getTemplateParameters(Property property) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (property != null && property.eResource() != null) {
            final org.talend.core.model.properties.Project project = ProjectManager.getInstance().getProject(property);
            if (project != null // from reference projects
                    && !ProjectManager.getInstance().getCurrentProject().getTechnicalLabel().equals(project.getTechnicalLabel())) {
                parameters.put("ProjectName", project.getTechnicalLabel());
            }
        }
        return parameters;
    }

    private static String getTemplateContent(File templateFile, String projectSettingKey, String bundleName,
            String bundleTemplatePath, Map<String, Object> parameters) throws Exception {
        return MavenTemplateManager.getContentFromInputStream(getTemplateStream(
                templateFile, projectSettingKey, bundleName, bundleTemplatePath, parameters));
    }

    private static InputStream getTemplateStream(File templateFile, String projectSettingKey, String bundleName,
            String bundleTemplatePath, Map<String, Object> parameters) throws Exception {
        InputStream stream = null;
        // 1. from file template directly.
        if (templateFile != null && templateFile.exists()) {
            // will close it later.
            stream = new BufferedInputStream(new FileInputStream(templateFile));
        }
        // 2. from project setting
        if (stream == null && projectSettingKey != null) {
            stream = getProjectSettingStream(projectSettingKey, parameters);
        }
        // 3. from bundle template.
        if (stream == null && bundleName != null && bundleTemplatePath != null) {
            stream = MavenTemplateManager.getBundleTemplateStream(bundleName, bundleTemplatePath);
        }
        return stream;
    }

    private static Method mGetProjectSettingStream = null;
    private static boolean newMethod = false;

    private static InputStream getProjectSettingStream(String key, Map<String, Object> parameters) {
        if (mGetProjectSettingStream == null) {
            try {
                try {
                    mGetProjectSettingStream = MavenTemplateManager.class.getMethod(
                            "getProjectSettingStream", String.class, Map.class);
                    newMethod = true;
                } catch (NoSuchMethodException ne) {
                    mGetProjectSettingStream = MavenTemplateManager.class.getMethod(
                            "getProjectSettingStream", String.class);
                    newMethod = false;
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalStateException("Unexpected method resolution failure. ", e);
            }
        }
        try {
            if (newMethod) {
                return (InputStream) mGetProjectSettingStream.invoke(null, key, parameters);
            } else {
                return (InputStream) mGetProjectSettingStream.invoke(null, key);
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
}
