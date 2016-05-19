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
package org.talend.camel.designer.ui.wizards.actions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.talend.camel.designer.util.CamelFeatureUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.relationship.RelationshipItemBuilder;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.publish.core.models.BundleModel;
import org.talend.designer.publish.core.models.FeaturesModel;
import org.talend.designer.publish.core.utils.ZipModel;
import org.talend.designer.runprocess.IProcessor;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.wizards.exportjob.action.JobExportAction;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager.ExportChoice;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JobJavaScriptOSGIForESBManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.RouteUsedJobManager;
import org.talend.repository.utils.EmfModelUtils;
import org.talend.repository.utils.JobContextUtils;

public class JavaCamelJobScriptsExportWSAction implements IRunnableWithProgress {

    private IProgressMonitor monitor;

    protected final IRepositoryNode routeNode;

    protected final String version;

    protected String bundleVersion;

    protected String destinationKar;

    private final JobScriptsManager manager;

    private FeaturesModel featuresModel;

    private final boolean addStatisticsCode;

    public JavaCamelJobScriptsExportWSAction(IRepositoryNode routeNode, String version, String destinationKar,
            boolean addStatisticsCode) {
        this.routeNode = routeNode;
        this.version = version;
        this.bundleVersion = version;
        this.destinationKar = destinationKar;
        this.addStatisticsCode = addStatisticsCode;

        manager = new JobJavaScriptOSGIForESBManager(getExportChoice(), null, null, IProcessor.NO_STATISTICS,
                IProcessor.NO_TRACES);
        manager.setBundleVersion(version);
    }

    private Map<ExportChoice, Object> getExportChoice() {
        Map<ExportChoice, Object> exportChoiceMap = new EnumMap<ExportChoice, Object>(ExportChoice.class);
        exportChoiceMap.put(ExportChoice.needJobItem, false);
        exportChoiceMap.put(ExportChoice.needSourceCode, false);
        exportChoiceMap.put(ExportChoice.needMetaInfo, true);
        exportChoiceMap.put(ExportChoice.needContext, true);
        exportChoiceMap.put(ExportChoice.needJobItem, false);
        exportChoiceMap.put(ExportChoice.needSourceCode, false);
        exportChoiceMap.put(ExportChoice.addStatistics, addStatisticsCode);
        return exportChoiceMap;
    }

    public JavaCamelJobScriptsExportWSAction(IRepositoryNode routeNode, String version, String bundleVersion) {
        this(routeNode, version, null, false);
        this.bundleVersion = bundleVersion;
    }

    public JobScriptsManager getManager() {
        return manager;
    }

    protected String getGroupId() {
        return CamelFeatureUtil.getMavenGroupId(routeNode.getObject().getProperty().getItem());
    }

    protected String getArtifactId() {
        return routeNode.getObject().getProperty().getDisplayName();
    }

    protected String getArtifactVersion() {
        return version;
    }

    public final void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        this.monitor = monitor;

        featuresModel = new FeaturesModel(getGroupId(), getArtifactId(), getArtifactVersion());
        try {
            // generated bundle jar first
            String routeName = routeNode.getObject().getProperty().getDisplayName();
            File routeFile;
            try {
                routeFile = File.createTempFile("route", FileConstants.JAR_FILE_SUFFIX, new File(getTempDir())); //$NON-NLS-1$
            } catch (IOException e) {
                throw new InvocationTargetException(e);
            }

            BundleModel routeModel = new BundleModel(getGroupId(), routeName, getArtifactVersion(), routeFile);
            if (featuresModel.addBundle(routeModel)) {
                exportRouteBundle(routeNode, routeFile, version, bundleVersion);

                final ProcessItem routeProcess = (ProcessItem) routeNode.getObject().getProperty().getItem();
                CamelFeatureUtil.addFeatureAndBundles(routeProcess, featuresModel);
                featuresModel.setConfigName(routeNode.getObject().getLabel());
                featuresModel.setContexts(JobContextUtils.getContextsMap(routeProcess));

                exportAllReferenceJobs(routeProcess);
            }

            processResults(featuresModel, monitor);

        } finally {
            // remove generated files
            FilesUtils.removeFolder(getTempDir(), true);
        }
    }

    protected void processResults(FeaturesModel featuresModel, IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        // create kar file
        try {
            new ZipModel(featuresModel, new File(destinationKar));
        } catch (IOException e) {
            throw new InvocationTargetException(e);
        }
    }

    private void exportAllReferenceJobs(ProcessItem routeProcess) throws InvocationTargetException, InterruptedException {
        for (NodeType cTalendJob : EmfModelUtils.getComponentsByName(routeProcess, "cTalendJob")) {
            String jobId = null;
            String jobVersion = null;
            for (Object o : cTalendJob.getElementParameter()) {
                if (!(o instanceof ElementParameterType)) {
                    continue;
                }
                ElementParameterType ept = (ElementParameterType) o;
                String eptName = ept.getName();
                if ("FROM_EXTERNAL_JAR".equals(eptName) && "true".equals(ept.getValue())) {
                    break;
                }
                if (jobId == null && "SELECTED_JOB_NAME:PROCESS_TYPE_PROCESS".equals(eptName)) {
                    jobId = ept.getValue();
                }
                if (jobVersion == null && "SELECTED_JOB_NAME:PROCESS_TYPE_VERSION".equals(eptName)) {
                    jobVersion = ept.getValue();
                }
            }

            if (jobId == null || jobVersion == null) {
                continue;
            }
            IRepositoryNode referencedJobNode;
            try {
                referencedJobNode = getJobRepositoryNode(jobId);
            } catch (PersistenceException e) {
                throw new InvocationTargetException(e);
            }
            if (RelationshipItemBuilder.LATEST_VERSION.equals(jobVersion)) {
                jobVersion = referencedJobNode.getObject().getVersion();
            }

            String jobName = referencedJobNode.getObject().getProperty().getDisplayName();
            File jobFile;
            try {
                jobFile = File.createTempFile("job", FileConstants.JAR_FILE_SUFFIX, new File(getTempDir())); //$NON-NLS-1$
            } catch (IOException e) {
                throw new InvocationTargetException(e);
            }
            String jobArtifactVersion = jobVersion;
            if(getArtifactVersion().endsWith("-SNAPSHOT")) {
            	jobArtifactVersion += "-SNAPSHOT";
            }
            String routeName = routeNode.getObject().getProperty().getDisplayName();
            BundleModel jobModel = new BundleModel(getGroupId() + '.' + routeName, jobName, getArtifactVersion(), jobFile);
            if (featuresModel.addBundle(jobModel)) {
                exportRouteUsedJobBundle(referencedJobNode, jobFile, jobVersion, jobName, jobVersion, routeNode
                    .getObject().getProperty().getDisplayName(), version);
            }
        }
    }

    private static IRepositoryNode getJobRepositoryNode(String jobId) throws PersistenceException {
        for (IRepositoryViewObject job : ProxyRepositoryFactory.getInstance().getAll(ERepositoryObjectType.PROCESS)) {
            if (job.getId().equals(jobId)) {
                return new RepositoryNode(job, null, ENodeType.REPOSITORY_ELEMENT);
            }
        }
        return null;
    }

    private void exportRouteBundle(IRepositoryNode node, File filePath, String version, String bundleVersion)
            throws InvocationTargetException, InterruptedException {
        JobJavaScriptOSGIForESBManager talendJobManager = new JobJavaScriptOSGIForESBManager(getExportChoice(), null, null,
                IProcessor.NO_STATISTICS, IProcessor.NO_TRACES);
        talendJobManager.setBundleVersion(bundleVersion);
        talendJobManager.setMultiNodes(false);
        talendJobManager.setDestinationPath(filePath.getAbsolutePath());
        JobExportAction action = new JobExportAction(Collections.singletonList(node), version, bundleVersion, talendJobManager,
                getTempDir(), "Route");
        action.run(monitor);
    }

    protected void exportRouteUsedJobBundle(IRepositoryNode node, File filePath, String jobVersion,
    		String bundleName, String bundleVersion,
    		String routeName, String routeVersion) throws InvocationTargetException, InterruptedException {
    	RouteUsedJobManager talendJobManager = new RouteUsedJobManager(getExportChoice());
        talendJobManager.setJobVersion(jobVersion);
        talendJobManager.setBundleName(bundleName);
        talendJobManager.setBundleVersion(bundleVersion);
        talendJobManager.setDestinationPath(filePath.getAbsolutePath());
    	talendJobManager.setRouteName(routeName);
    	talendJobManager.setRouteVersion(routeVersion);
    	talendJobManager.setGroupId(getGroupId());
    	talendJobManager.setArtifactId(getArtifactId());
    	talendJobManager.setArtifactVersion(getArtifactVersion());
        JobExportAction action = new JobExportAction(Collections.singletonList(node), jobVersion, bundleVersion, talendJobManager,
                getTempDir(), "Job");
        action.run(monitor);
	}

    protected static String getTempDir() {
        String path = System.getProperty("java.io.tmpdir") + File.separatorChar + "route" + File.separatorChar; //$NON-NLS-1$
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }

        return path;
    }
    // END of TESB-5328

}
