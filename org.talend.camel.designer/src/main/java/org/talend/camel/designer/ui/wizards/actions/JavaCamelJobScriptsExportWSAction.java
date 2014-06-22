package org.talend.camel.designer.ui.wizards.actions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.talend.camel.designer.model.ExportKarBundleModel;
import org.talend.camel.designer.util.KarFileGenerator;
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
import org.talend.designer.runprocess.IProcessor;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.wizards.exportjob.action.JobExportAction;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager.ExportChoice;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JobJavaScriptOSGIForESBManager;
import org.talend.repository.utils.EmfModelUtils;

public class JavaCamelJobScriptsExportWSAction implements IRunnableWithProgress {

    protected IProgressMonitor monitor;

    protected final RepositoryNode routeNode;

    protected final String version;

    protected String bundleVersion;

    protected String destinationKar;

    private final JobScriptsManager manager;

    protected Set<ExportKarBundleModel> exportedBundleModels = new HashSet<ExportKarBundleModel>();

    private final boolean addStatisticsCode;

    public JavaCamelJobScriptsExportWSAction(RepositoryNode routeNode, String version, String destinationKar,
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

    public JavaCamelJobScriptsExportWSAction(RepositoryNode routeNode, String version, String bundleVersion) {
        this(routeNode, version, null, false);
        this.bundleVersion = bundleVersion;
    }

    public JobScriptsManager getManager() {
        return manager;
    }

    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        this.monitor = monitor;

        try {
            // generated bundle jar first
            exportKarOsgiBundles();

            processResults();

        } finally {
            // remove generated files
            FilesUtils.removeFolder(getTempDir(), true);
        }
    }

    protected boolean processRoute(String routeBundlePath, RepositoryNode routeNode, String version)
            throws InvocationTargetException {
        return exportedBundleModels.add(new ExportKarBundleModel(routeBundlePath, routeNode, version,
                ExportKarBundleModel.ROUTE_TYPE));
    }

    protected boolean processReferencedJob(String filePath, RepositoryNode referencedJobNode, String jobVersion)
            throws InvocationTargetException {
        return exportedBundleModels.add(new ExportKarBundleModel(filePath, referencedJobNode, jobVersion,
                ExportKarBundleModel.JOB_TYPE));
    }

    protected void processResults() throws InvocationTargetException {
        // create kar file
        try {
            KarFileGenerator.generateKarFile(exportedBundleModels, routeNode, version, destinationKar);
        } catch (IOException e) {
            throw new InvocationTargetException(e);
        }
    }

    protected void exportKarOsgiBundles() throws InvocationTargetException, InterruptedException {
        String routerBundlePath = getTempDir() + getNodeBundleName(routeNode, version) + FileConstants.JAR_FILE_SUFFIX;

        if (processRoute(routerBundlePath, routeNode, version)) {
            exportOsgiBundle(routeNode, routerBundlePath, version, bundleVersion, "Route"); //$NON-NLS-1$
            exportAllReferenceJobs(routeNode);
        }
    }

    protected String getNodeBundleName(RepositoryNode node, String v) {
        String displayName = node.getObject().getProperty().getDisplayName();
        return displayName + "-bundle-" + v; //$NON-NLS-1$ 

    }

    private void exportAllReferenceJobs(RepositoryNode n) throws InvocationTargetException, InterruptedException {
        for (NodeType node : EmfModelUtils.getComponentsByName((ProcessItem) n.getObject().getProperty().getItem(), "cTalendJob")) {
            exportReferencedJob(node);
        }
    }

    protected void exportReferencedJob(NodeType cTalendJob) throws InvocationTargetException, InterruptedException {

        String jobId = null;
        String jobVersion = null;
        for (Object o : cTalendJob.getElementParameter()) {
            if (!(o instanceof ElementParameterType)) {
                continue;
            }
            ElementParameterType ept = (ElementParameterType) o;
            String eptName = ept.getName();
            if ("FROM_EXTERNAL_JAR".equals(eptName) && "true".equals(ept.getValue())) {
                return;
            }
            if (jobId == null && "SELECTED_JOB_NAME:PROCESS_TYPE_PROCESS".equals(eptName)) {
                jobId = ept.getValue();
            }
            if (jobVersion == null && "SELECTED_JOB_NAME:PROCESS_TYPE_VERSION".equals(eptName)) {
                jobVersion = ept.getValue();
            }
        }

        if (jobId == null || jobVersion == null) {
            return;
        }
        RepositoryNode referencedJobNode;
        try {
            referencedJobNode = getJobRepositoryNode(jobId);
        } catch (PersistenceException e) {
            throw new InvocationTargetException(e);
        }
        if (RelationshipItemBuilder.LATEST_VERSION.equals(jobVersion)) {
            jobVersion = referencedJobNode.getObject().getVersion();
        }

        String displayName = referencedJobNode.getObject().getProperty().getDisplayName();
        String filePath = getTempDir() + displayName + '-' + jobVersion + FileConstants.JAR_FILE_SUFFIX;
        if (processReferencedJob(filePath, referencedJobNode, jobVersion)) {
            exportOsgiBundle(referencedJobNode, filePath, jobVersion, jobVersion, "Job", true);
        }

    }

    protected RepositoryNode getJobRepositoryNode(String jobId) throws PersistenceException {
        List<IRepositoryViewObject> jobs = ProxyRepositoryFactory.getInstance().getAll(ERepositoryObjectType.PROCESS);
        for (IRepositoryViewObject job : jobs) {
            if (job.getId().equals(jobId)) {
                return new RepositoryNode(job, null, ENodeType.REPOSITORY_ELEMENT);
            }
        }
        return null;
    }

     protected void exportOsgiBundle(RepositoryNode node, String filePath, String version, String bundleVersion, String itemType)
             throws InvocationTargetException, InterruptedException {
	exportOsgiBundle(node, filePath, version, bundleVersion, itemType, false);
     }

     //patch for TESB-12909
     protected void exportOsgiBundle(RepositoryNode node, String filePath, String version, String bundleVersion, String itemType, boolean isRefJobBycTalendJob)
             throws InvocationTargetException, InterruptedException {
        JobJavaScriptOSGIForESBManager talendJobManager = new JobJavaScriptOSGIForESBManager(getExportChoice(), null, null,
                IProcessor.NO_STATISTICS, IProcessor.NO_TRACES);
        talendJobManager.setBundleVersion(bundleVersion);
        talendJobManager.setMultiNodes(false);
        talendJobManager.setDestinationPath(filePath);
        talendJobManager.setIsRefJobByCTalendJob(isRefJobBycTalendJob);
        JobExportAction action = new JobExportAction(Collections.singletonList(node), version, bundleVersion, talendJobManager,
                getTempDir(), itemType);
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
