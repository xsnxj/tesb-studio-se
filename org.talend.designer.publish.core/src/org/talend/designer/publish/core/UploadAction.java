package org.talend.designer.publish.core;

import java.io.File;
import java.util.Set;

import org.talend.designer.publish.core.models.BundleModel;
import org.talend.designer.publish.core.models.DependencyModel;
import org.talend.designer.publish.core.models.FeaturesModel;

public class UploadAction {

	private String repositoryUrl = null;
	private String username = null;
	private String password = null;

	public UploadAction(String repositoryUrl, String username, String password) {
		this.repositoryUrl = repositoryUrl;
		this.username = username;
		this.password = password;
	}

	public boolean deployRoute(String jarFilePath, String groupId,
			String artifactId, String version,
			Set<DependencyModel> dependencies) throws Exception {
		return deployRoute(new File(jarFilePath), groupId, artifactId, version,
				dependencies);
	}

	private boolean deployRoute(File jarFile, String groupId,
			String artifactId, String version,
			Set<DependencyModel> dependencies) throws Exception {
		BundleModel bundleModel = new BundleModel(jarFile, groupId, artifactId,
				version, repositoryUrl, username, password);
		deployBundle(bundleModel);

		FeaturesModel featuresModel = new FeaturesModel(groupId, artifactId,
				version, repositoryUrl, username, password);
		featuresModel.addSubBundle(bundleModel);
		deployFeatures(featuresModel);
		return true;
	}

	public void deployFeatures(FeaturesModel featuresModel) throws Exception {
		if (featuresModel != null) {
			FeaturesModel model = new FeaturesModel(featuresModel, repositoryUrl, username, password);
			model.upload();
		}
	}

	public void deployBundle(BundleModel bundleModel) throws Exception {
		if (bundleModel != null) {
			bundleModel.upload();
		}
	}

	// for test
	public static void main(String[] args) throws Exception {
		UploadAction uploadAction = new UploadAction("http://192.168.0.10:8080/archiva/repository/snapshots/",
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

		uploadAction.deployRoute("TestEERoute_0.1.jar", "org.talend.liugang",
				"TestEERoute2", "2.0.22-SNAPSHOT", null);
	}
}
