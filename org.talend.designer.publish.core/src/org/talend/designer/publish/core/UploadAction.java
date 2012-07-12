package org.talend.designer.publish.core;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.talend.designer.publish.core.models.BundleModel;
import org.talend.designer.publish.core.models.DependencyModel;
import org.talend.designer.publish.core.models.FeaturesModel;

public class UploadAction {

	private static String JOB_CONTROLLER_FEATURE = "talend-job-controller";

	private static String JOB_CONTROLLER_VERSION = "[5,6)";

	private String repositoryUrl = null;
	private String username = null;
	private String password = null;

	public UploadAction(String repositoryUrl, String username, String password) {
		this.repositoryUrl = repositoryUrl;
		this.username = username;
		this.password = password;
	}

	public boolean deployJob(String configName, Map<String, Map<String, String>> contexts,
			String jarFilePath, String groupId, String artifactId,
			String version, Set<DependencyModel> dependencies) throws Exception {
		return deployJob(configName, contexts, new File(jarFilePath),
				groupId, artifactId, version, dependencies);
	}

	/**
	 * http://jira.talendforge.org/browse/TESB-5426
	 * 
	 * @param configName
	 * @param contexts
	 * @param jarFilePath
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @param dependencies
	 * @param features
	 * @return
	 * @throws Exception
	 */
	public boolean deployRoute(String configName,
			Map<String, Map<String, String>> contexts, String jarFilePath,
			String groupId, String artifactId, String version,
			Set<DependencyModel> dependencies, FeaturesModel featuresModel)
			throws Exception {
		BundleModel bundleModel = new BundleModel(new File(jarFilePath),
				groupId, artifactId, version, repositoryUrl, username, password);
		deployBundle(bundleModel);

		featuresModel.setConfigName(configName);
		featuresModel.setContexts(contexts);
		featuresModel.addSubBundle(bundleModel);

		featuresModel.addSubFeature(JOB_CONTROLLER_FEATURE,
				JOB_CONTROLLER_VERSION);

		deployFeatures(featuresModel);
		return true;
	}

	private boolean deployJob(String configName, Map<String, Map<String, String>> contexts,
			File jarFile, String groupId, String artifactId, String version,
			Set<DependencyModel> dependencies) throws Exception {
		BundleModel bundleModel = new BundleModel(jarFile, groupId, artifactId,
				version, repositoryUrl, username, password);
		deployBundle(bundleModel);

		FeaturesModel featuresModel = new FeaturesModel(groupId, artifactId,
				version, repositoryUrl, username, password);
		featuresModel.setConfigName(configName);
		featuresModel.setContexts(contexts);
		featuresModel.addSubBundle(bundleModel);
		// http://jira.talendforge.org/browse/TESB-3698
		featuresModel.addSubFeature(JOB_CONTROLLER_FEATURE,
				JOB_CONTROLLER_VERSION);
		deployFeatures(featuresModel);
		return true;
	}

	public void deployFeatures(FeaturesModel featuresModel) throws Exception {
		if (featuresModel != null) {
			FeaturesModel model = new FeaturesModel(featuresModel,
					repositoryUrl, username, password);
			model.upload();
		}
	}

	public void deployBundle(BundleModel bundleModel) throws Exception {
		if (bundleModel != null) {
			BundleModel model = new BundleModel(bundleModel, repositoryUrl,
					username, password);
			model.upload();
		}
	}

	// for test
	public static void main(String[] args) throws Exception {
		UploadAction uploadAction = new UploadAction(
				"http://192.168.0.10:8080/archiva/repository/snapshots/",
				"talend", "talend123");

		// BundleModel b1 = new BundleModel(new File("userRoutines.jar"),
		// "org.talend.liugang", "userRoutines1", "1.0",
		// "http://localhost:8080/archiva/repository/snapshots/", "gliu",
		// "liugang123");
		// BundleModel b2 = new BundleModel(new File("userRoutines.jar"),
		// "trg.talend.liugang", "userRoutines2", "1.0",
		// "http://localhost:8080/archiva/repository/snapshots/", "gliu",
		// "liugang123");
		//
		// FeaturesModel f1 = new FeaturesModel("org.talend.liugang", "user1",
		// "1.0", "http://localhost:8080/archiva/repository/snapshots/",
		// "gliu", "liugang123");
		// FeaturesModel f2 = new FeaturesModel("trg.talend.liugang", "user2",
		// "1.0", "http://localhost:8080/archiva/repository/snapshots/",
		// "gliu", "liugang123");
		//
		// f2.addSubBundle(b1);
		// f2.addSubBundle(b2);
		// f1.addSubBundle(b1);
		// f1.addSubBundle(b2);
		// f1.addSubFeature(f2);
		//
		// uploadAction.deployBundle(b1);
		// uploadAction.deployBundle(b2);
		// uploadAction.deployFeatures(f1);
		// uploadAction.deployFeatures(f2);

		// uploadAction.deployRoute("TestEERoute_0.1.jar", "org.talend.liugang",
		// "TestEERoute2", "2.0.22-SNAPSHOT", null);
	}

}
