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
import org.talend.designer.maven.template.ETalendMavenVariables;
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
        Properties properties = model.getProperties();
        if (properties == null) {
            properties = new Properties();
            model.setProperties(properties);
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

        // required by ci-builder
        checkPomProperty(properties, "talend.project.name", ETalendMavenVariables.ProjectName, project.getTechnicalLabel());
        checkPomProperty(properties, "talend.job.version", ETalendMavenVariables.TalendJobVersion, property.getVersion());
        checkPomProperty(properties, "talend.job.id", ETalendMavenVariables.JobId, property.getId());
    }

    @Override
    public void create(IProgressMonitor monitor) throws Exception {

        IFile pom = getPomFile();

        if (pom == null) {
            return;
        }

        this.model = new Model(); // createModel();
        configModel(model); // config model
        Model pomModel = model; // new Model();
        pomModel.setModelVersion(MAVEN_VERSION);
        // pom.setParent(model.getParent());
        pomModel.setGroupId(PomIdsHelper.getJobGroupId(getJobProcessor().getProperty()));
        pomModel.setArtifactId(PomIdsHelper.getJobArtifactId(getJobProcessor().getProperty()));
        pomModel.setVersion(PomIdsHelper.getJobVersion(getJobProcessor().getProperty()));
        pomModel.setPackaging("pom");
        // pomModel.setName(PomIdsHelper.getp); //<name>@ProjectName@ @JobName@-@JobVersion@
        // (@TalendJobVersion@,@JobType@)</name>

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

    }

    /*
     * feature.xml and copy wsdl, mainfest
     * 
     * @see org.talend.designer.maven.tools.creator.CreateMavenJobPom#generateTemplates(boolean)
     */
    @Override
    public void generateTemplates(boolean overwrite) throws Exception {

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
        featuresFile.setValue("${basedir}/src/main/resources/feature/feature.xml");

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
