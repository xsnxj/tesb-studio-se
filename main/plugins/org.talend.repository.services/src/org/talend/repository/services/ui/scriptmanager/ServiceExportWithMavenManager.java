// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.runprocess.ProcessorException;
import org.talend.repository.constants.IExportJobConstants;
import org.talend.repository.documentation.ExportFileResource;
import org.talend.repository.preference.constants.IExportServicePrefConstants;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JavaScriptForESBWithMavenManager;
import org.talend.resource.IExportRouteResourcesService;
import org.talend.resources.util.EMavenBuildScriptProperties;

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
                + IExportJobConstants.MAVEN_CONTROL_BUILD_BUNDLE_FILE_NAME);
        File mavenBuildFeatureFile = new File(getTmpFolder() + PATH_SEPARATOR
                + IExportJobConstants.MAVEN_KARAF_BUILD_FEATURE_FILE_NAME);
        File mavenBuildParentFile = new File(getTmpFolder() + PATH_SEPARATOR
                + IExportJobConstants.MAVEN_KARAF_BUILD_PARENT_FILE_NAME);

        try {
            String mavenScript = resourcesService
                    .getScriptFromPreferenceStore(IExportServicePrefConstants.MAVEN_SERVICES_SCRIPT_TEMPLATE);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildFile, mavenPropertiesMap, false, true);
                scriptsUrls.add(mavenBuildFile.toURL());
            }

            mavenScript = resourcesService
                    .getScriptFromPreferenceStore(IExportServicePrefConstants.MAVEN_SERVICES_SCRIPT_TEMPLATE_BUNDLE);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildBundleFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildBundleFile, mavenPropertiesMap, true, false);
                scriptsUrls.add(mavenBuildBundleFile.toURL());
            }

            mavenScript = resourcesService
                    .getScriptFromPreferenceStore(IExportServicePrefConstants.MAVEN_SERVICES_SCRIPT_TEMPLATE_FEATURE);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildFeatureFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildFeatureFile, mavenPropertiesMap);
                scriptsUrls.add(mavenBuildFeatureFile.toURL());
            }

            mavenScript = resourcesService
                    .getScriptFromPreferenceStore(IExportServicePrefConstants.MAVEN_SERVICES_SCRIPT_TEMPLATE_PARENT);
            if (mavenScript != null) {
                createMavenBuildFileFromTemplate(mavenBuildParentFile, mavenScript);
                updateMavenBuildFileContent(mavenBuildParentFile, mavenPropertiesMap);
                scriptsUrls.add(mavenBuildParentFile.toURL());
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

}
