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
package org.talend.repository.services.maven;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Resource;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.process.JobInfo;
import org.talend.core.model.properties.Project;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.SVNConstant;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.ItemResourceUtil;
import org.talend.designer.maven.model.TalendJavaProjectConstants;
import org.talend.designer.maven.model.TalendMavenConstants;
import org.talend.designer.maven.tools.AggregatorPomsHelper;
import org.talend.designer.maven.tools.creator.CreateMavenJobPom;
import org.talend.designer.maven.utils.PomIdsHelper;
import org.talend.designer.maven.utils.PomUtil;
import org.talend.designer.runprocess.IProcessor;
import org.talend.repository.ProjectManager;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;

/**
 * DOC yyan for Service pom generation
 */
public class CreateMavenDataServicePom extends CreateMavenJobPom {

    /**
     * 
     */
    private static final String MAVEN_VERSION = "4.0.0";

    private static final String POM_FEATURE_XML = "pom-feature.xml";

    private static final String POM_CONTROL_BUNDLE_XML = "pom-control-bundle.xml";

    private Model model;

    private ServiceItem serviceItem;

    public CreateMavenDataServicePom(IProcessor jobProcessor, IFile pomFile) {
        super(jobProcessor, pomFile);
        this.serviceItem = (ServiceItem) getJobProcessor().getProperty().getItem();
    }

    /*
     * @see org.talend.designer.maven.tools.creator.CreateMavenJobPom#addProperties(org.apache.maven.model.Model)
     */
    @Override
    protected void addProperties(Model model) {
        Properties p = model.getProperties();
        if (p == null) {
            p = new Properties();
            model.setProperties(p);
        }
        Property property = getJobProcessor().getProperty();
        Project project = ProjectManager.getInstance().getProject(property);
        if (project == null) { // current project
            project = ProjectManager.getInstance().getCurrentProject().getEmfProject();
        }
        String mainProjectBranch = ProjectManager.getInstance().getMainProjectBranch(project);
        if (mainProjectBranch == null) {
            mainProjectBranch = SVNConstant.NAME_TRUNK;
        }
    }

    @Override
    public void create(IProgressMonitor monitor) throws Exception {

        IFile pom = getPomFile();

        if (pom == null) {
            return;
        }

        this.model = new Model(); // createModel();
        Model pomModel = model; // new Model();
        pomModel.setModelVersion(MAVEN_VERSION);
        // pom.setParent(model.getParent());
        pomModel.setGroupId(PomIdsHelper.getJobGroupId(getJobProcessor().getProperty()));
        pomModel.setArtifactId(PomIdsHelper.getJobArtifactId(getJobProcessor().getProperty()));
        pomModel.setVersion(PomIdsHelper.getJobVersion(getJobProcessor().getProperty()));
        pomModel.setPackaging("pom");

        // add dynamic ds job modules
        String upperPath = "../";
        ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        ServiceConnection connection = (ServiceConnection) serviceItem.getConnection();
        EList<ServicePort> listPort = connection.getServicePort();
        // In case the service in under sub folder
        int depth = ItemResourceUtil.getItemRelativePath(serviceItem.getProperty()).segmentCount();
        String relativePath = upperPath.concat(upperPath);
        for (int level = 0; level < depth; level++) {
            relativePath += upperPath;
        }
        for (ServicePort port : listPort) {
            List<ServiceOperation> listOperation = port.getServiceOperation();
            for (ServiceOperation operation : listOperation) {
                if (StringUtils.isNotEmpty(operation.getReferenceJobId())) {
                    IRepositoryViewObject node = factory.getLastVersion(operation.getReferenceJobId());
                    if (node != null) {
                        String jobName = node.getLabel();
                        if (jobName != null && !pomModel.getModules().contains(jobName)) {
                            String module = relativePath + TalendJavaProjectConstants.DIR_PROCESS + "/" + node.getPath() + "/"
                                    + AggregatorPomsHelper.getJobProjectFolderName(node.getProperty());
                            pomModel.addModule(module);
                            // check if need to remove from parent pom
                            IFile jobPom = AggregatorPomsHelper.getItemPomFolder(node.getProperty())
                                    .getFile(TalendMavenConstants.POM_FILE_NAME);
                            AggregatorPomsHelper.removeFromParentModules(jobPom);
                        }
                    }
                }
            }
        }

        // add control bundle module
        pomModel.addModule(POM_CONTROL_BUNDLE_XML);
        // add feature module
        pomModel.addModule(POM_FEATURE_XML);
        PomUtil.savePom(monitor, pomModel, pom);

        IFile feature = pom.getParent().getFile(new Path(POM_FEATURE_XML));
        Model featureModel = new Model();
        featureModel.setModelVersion(MAVEN_VERSION);
        featureModel.setGroupId(PomIdsHelper.getJobGroupId(getJobProcessor().getProperty()));
        featureModel.setArtifactId(PomIdsHelper.getJobArtifactId(getJobProcessor().getProperty()) + "-feature");
        featureModel.setVersion(PomIdsHelper.getJobVersion(getJobProcessor().getProperty()));
        featureModel.setPackaging("pom");
        Build featureModelBuild = new Build();
        featureModelBuild.addPlugin(addFeaturesMavenPlugin());
        featureModel.setBuild(featureModelBuild);

        PomUtil.savePom(monitor, featureModel, feature);

        IFile controlBundle = pom.getParent().getFile(new Path(POM_CONTROL_BUNDLE_XML));
        Model controlBundleModel = new Model();
        controlBundleModel.setParent(model.getParent());
        controlBundleModel.setModelVersion(MAVEN_VERSION);
        controlBundleModel.setGroupId(PomIdsHelper.getJobGroupId(getJobProcessor().getProperty()));
        controlBundleModel.setArtifactId(PomIdsHelper.getJobArtifactId(getJobProcessor().getProperty()) + "-control-bundle");
        controlBundleModel.setVersion(PomIdsHelper.getJobVersion(getJobProcessor().getProperty()));
        controlBundleModel.setPackaging("jar");
        Build controlBundleModelBuild = new Build();
        controlBundleModelBuild.addPlugin(addControlBundleMavenPlugin());
        controlBundleModelBuild.addResource(addControlBundleMavenResource());
        controlBundleModel.setBuild(controlBundleModelBuild);
        PomUtil.savePom(monitor, controlBundleModel, controlBundle);

        afterCreate(monitor);
    }

    private Resource addControlBundleMavenResource() {
        Resource resource = new Resource();
        resource.addExclude("**/feature.xml");
        resource.setDirectory("${basedir}/src/main/resources");
        return resource;
    }

    protected void generateAssemblyFile(IProgressMonitor monitor, final Set<JobInfo> clonedChildrenJobInfors) throws Exception {
        // IFile assemblyFile = this.getAssemblyFile();
        // if (assemblyFile != null) {
        // // read template from project setting
        // try {
        // File templateFile = PomUtil.getTemplateFile(getObjectTypeFolder(), getItemRelativePath(),
        // TalendMavenConstants.ASSEMBLY_FILE_NAME);
        // if (!FilesUtils.allInSameFolder(templateFile, TalendMavenConstants.POM_FILE_NAME)) {
        // templateFile = null; // force to set null, in order to use the template from other places.
        // }
        //
        // final Map<String, Object> templateParameters = PomUtil.getTemplateParameters(getJobProcessor());
        // String content = MavenTemplateManager.getTemplateContent(templateFile,
        // IProjectSettingPreferenceConstants.TEMPLATE_ROUTE_ASSEMBLY, JOB_TEMPLATE_BUNDLE,
        // IProjectSettingTemplateConstants.PATH_OSGI_BUNDLE + '/'
        // + IProjectSettingTemplateConstants.ASSEMBLY_ROUTE_TEMPLATE_FILE_NAME,
        // templateParameters);
        // if (content != null) {
        // ByteArrayInputStream source = new ByteArrayInputStream(content.getBytes());
        // if (assemblyFile.exists()) {
        // assemblyFile.setContents(source, true, false, monitor);
        // } else {
        // assemblyFile.create(source, true, monitor);
        // }
        // updateDependencySet(assemblyFile);
        // }
        // } catch (Exception e) {
        // ExceptionHandler.process(e);
        // }
        // }
    }

    /*
     * feature.xml and copy wsdl, mainfest
     * 
     * @see org.talend.designer.maven.tools.creator.CreateMavenJobPom#generateTemplates(boolean)
     */
    @Override
    public void generateTemplates(boolean overwrite) throws Exception {
        // Map<ExportChoice, Object> exportChoiceMap = new EnumMap<ExportChoice, Object>(ExportChoice.class);
        // ServiceExportManager serviceExportManager = new ServiceExportManager(exportChoiceMap);
        //
        // String serviceName = serviceItem.getProperty().getLabel();
        // String serviceVersion = serviceItem.getProperty().getVersion();
        // List<IRepositoryViewObject> nodes = new ArrayList<IRepositoryViewObject>();
        // Map<String, Map<String, String>> contextValues = new HashMap<String, Map<String, String>>();
        // Map<ServicePort, Map<String, String>> ports = new HashMap<ServicePort, Map<String, String>>();
        //
        // ServiceConnection serviceConnection = (ServiceConnection) serviceItem.getConnection();
        // EList<ServicePort> listPort = serviceConnection.getServicePort();
        // try {
        // List<IRepositoryViewObject> jobs =
        // ProxyRepositoryFactory.getInstance().getAll(ERepositoryObjectType.PROCESS);
        // for (ServicePort port : listPort) {
        // List<ServiceOperation> listOperation = port.getServiceOperation();
        // Map<String, String> operations = new HashMap<String, String>(listOperation.size());
        // for (ServiceOperation operation : listOperation) {
        // String jobId = operation.getReferenceJobId();
        // if (jobId != null && !jobId.equals("")) {
        // String operationName = operation.getName();
        // IRepositoryViewObject jobNode = null;
        // for (IRepositoryViewObject job : jobs) {
        // if (job.getId().equals(jobId)) {
        // jobNode = job;
        // break;
        // }
        // }
        // if (jobNode == null) {
        // continue;
        // }
        // String jobName = jobNode.getLabel();
        // operations.put(operationName, jobName);
        // nodes.add(jobNode);
        // contextValues.putAll(JobContextUtils.getContextsMap((ProcessItem) jobNode.getProperty().getItem()));
        // }
        // }
        // ports.put(port, operations);
        // }
        // } catch (PersistenceException e) {
        // ExceptionHandler.process(e);
        // }
        //
        // // src\main\resources\feature\feature.xml
        // FeaturesModel features = new FeaturesModel(getGroupId(), serviceName, serviceVersion);
        // features.setConfigName(serviceName);
        // features.setContexts(contextValues);
        // ServiceConnection connection = (ServiceConnection) serviceItem.getConnection();
        // String useRegistry = connection.getAdditionalInfo().get(ServiceMetadataDialog.USE_SERVICE_REGISTRY);
        // if (!"true".equals(useRegistry)) {
        // String useCorrelation = connection.getAdditionalInfo().get(ServiceMetadataDialog.USE_BUSINESS_CORRELATION);
        // if ("true".equals(useCorrelation)) {
        // features.addFeature(new FeatureModel(FeaturesModel.CORRELATION_FEATURE_NAME));
        // }
        // }
        // // add talend-data-mapper feature
        // for (IRepositoryViewObject node : nodes) {
        // ProcessItem processItem = (ProcessItem) node.getProperty().getItem();
        // if (null != EmfModelUtils.getComponentByName(processItem, "tHMap")) {
        // features.addFeature(new FeatureModel(FeaturesModel.TALEND_DATA_MAPPER_FEATURE_NAME));
        // break;
        // }
        // }
        //
        // for (IRepositoryViewObject node : nodes) {
        // features.addBundle(new BundleModel(PomIdsHelper.getJobGroupId(node.getProperty()),
        // serviceExportManager.getNodeLabel(node) + "-bundle", PomIdsHelper.getJobVersion(node.getProperty())));
        // }
        // final String artifactName = serviceName + "-control-bundle"; //$NON-NLS-1$
        // features.addBundle(new BundleModel(PomIdsHelper.getJobGroupId(serviceItem.getProperty()), artifactName,
        // serviceVersion));
        //
        // IFile feature = talendProcessJavaProject
        // .createSubFolder(monitor, talendProcessJavaProject.getResourcesFolder(), "feature").getFile("feature.xml");
        // setFileContent(features.getContent(), feature, monitor);
        //
        // // resources\META-INF\MANIFEST.MF
        // Manifest manifest = serviceExportManager.getManifest(serviceName, serviceVersion, additionalInfo);
        // IFile mf = talendProcessJavaProject.createSubFolder(monitor, talendProcessJavaProject.getResourcesFolder(),
        // "META-INF")
        // .getFile("MANIFEST.MF");
        // // talendProcessJavaProject.getResourceSubFolder(monitor, "META-INF").getFile("MANIFEST.MF");
        // FileOutputStream outputStream = new FileOutputStream(mf.getLocation().toFile());
        // manifest.write(outputStream);
        // outputStream.flush();
        // outputStream.close();
        //
        // // resources\**.wsdl
        // IFile wsdl = talendProcessJavaProject.getResourcesFolder().getFile(serviceWsdl.getName());
        // setFileContent(serviceWsdl.getContents(), wsdl, monitor);
        //
        // // resources\OSGI-INF\blueprint\blueprint.xml
        // IFile blueprint = talendProcessJavaProject
        // .createSubFolder(monitor, talendProcessJavaProject.getResourcesFolder(), "OSGI-INF/blueprint")
        // .getFile("blueprint.xml");
        // // talendProcessJavaProject.getResourceSubFolder(monitor, "OSGI-INF/blueprint").getFile("blueprint.xml");
        // serviceExportManager.createBlueprint(blueprint.getLocation().toFile(), ports, additionalInfo, wsdl,
        // serviceName);

    }

    private Plugin addControlBundleMavenPlugin() {

        Plugin plugin = new Plugin();

        plugin.setGroupId("org.apache.maven.plugins");
        plugin.setArtifactId("maven-jar-plugin");
        plugin.setVersion("3.0.2");

        plugin.setExtensions(true);

        Xpp3Dom configuration = new Xpp3Dom("configuration");
        Xpp3Dom archive = new Xpp3Dom("archive");
        Xpp3Dom manifest = new Xpp3Dom("manifestFile");
        manifest.setValue("${project.build.outputDirectory}/META-INF/MANIFEST.MF");
        archive.addChild(manifest);
        configuration.addChild(archive);
        plugin.setConfiguration(configuration);

        return plugin;
    }

    private Plugin addFeaturesMavenPlugin() {
        Plugin plugin = new Plugin();

        plugin.setGroupId("org.apache.karaf.tooling");
        plugin.setArtifactId("features-maven-plugin");
        plugin.setVersion("2.2.9");

        Xpp3Dom configuration = new Xpp3Dom("configuration");

        Xpp3Dom resourcesDir = new Xpp3Dom("resourcesDir");
        resourcesDir.setValue("${project.build.directory}/bin");

        Xpp3Dom featuresFile = new Xpp3Dom("featuresFile");
        featuresFile.setValue("${basedir}/src/main/resources/feature.xml");

        configuration.addChild(resourcesDir);
        configuration.addChild(featuresFile);

        List<PluginExecution> pluginExecutions = new ArrayList<PluginExecution>();
        PluginExecution pluginExecution = new PluginExecution();
        pluginExecution.setId("create-kar");
        pluginExecution.addGoal("create-kar");
        pluginExecution.setConfiguration(configuration);

        pluginExecutions.add(pluginExecution);
        plugin.setExecutions(pluginExecutions);

        return plugin;
    }
}
