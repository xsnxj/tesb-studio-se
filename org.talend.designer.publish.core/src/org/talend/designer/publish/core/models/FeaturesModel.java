package org.talend.designer.publish.core.models;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class FeaturesModel extends UploadableModel {

	private Set<String> subFeatures = new HashSet<String>();

	private Set<BundleModel> subBundles = new HashSet<BundleModel>();

	private static String nameSuffix = "-feature";

	private String configName = "";
	private String[] contextList = new String[] { "Default" };

	public FeaturesModel(String groupId, String namePrefix, String version) {
		this(groupId, namePrefix, version, null, null, null);
	}			
	
	public FeaturesModel(String groupId, String namePrefix, String version,
			String repositoryURL, String userName, String password) {
		super(groupId, namePrefix + nameSuffix, version, repositoryURL,
				userName, password);
	}

	public FeaturesModel(FeaturesModel featuresModel, String repositoryUrl,
			String userName, String password) {
		super(featuresModel.groupId, featuresModel.artifactId, featuresModel.version, repositoryUrl,
				userName, password);
		subFeatures.addAll(featuresModel.subFeatures);
		subBundles.addAll(featuresModel.subBundles);
		this.configName = featuresModel.configName;
		this.contextList = featuresModel.contextList;
	}

	@Override
	public void upload() throws Exception {
		uploadFeature();
		uploadPom();
		uploadMetadata();
	}

	private void uploadMetadata() throws Exception {
		MetadataModel metadataModel = new MetadataModel(groupId, artifactId,
				version, repositoryURL, userName, password);
		metadataModel.addVersion(version);
		metadataModel.upload();
	}

	private void uploadPom() throws IOException {
		// upload pom part
		PomModel pomModel = new PomModel(groupId, artifactId, version, "pom",
				repositoryURL, userName, password);
		for (BundleModel bm : subBundles) {
			DependencyModel dm = new DependencyModel(bm.getGroupId(),
					bm.getArtifactId(), bm.getVersion());
			pomModel.addDenpendency(dm);
		}
		// for (FeaturesModel fm : subFeatures) {
		// DependencyModel dm = new DependencyModel(fm.getGroupId(),
		// fm.getArtifactId(), fm.getVersion(), "pom");
		// pomModel.addDenpendency(dm);
		// }
		pomModel.upload();
	}

	private void uploadFeature() throws MalformedURLException, IOException {
		// upload feature part
		String artifactPath = getArtifactDestination();
		String versionPath = artifactPath + version + "/";
		String fileName = artifactId + "-" + version + ".xml";
		String filePath = versionPath + fileName;
		URL featureURL = new URL(filePath);
		String featureContent = getFeatureContent();
		uploadContent(featureURL, featureContent);

		// upload md5 and sha1
		uploadMd5AndSha1(filePath, fileName, featureContent);
	}

	public void addSubFeature(FeaturesModel subFeature) {
		String s = toFeatureString(subFeature.getVersion(),
				subFeature.getArtifactId());
		if (!subFeatures.contains(s)) {
			subFeatures.add(s);
		}
	}

	public void addSubFeature(String featureName, String featureVersion) {
		String s = toFeatureString(featureVersion, featureName);
		if (!subFeatures.contains(s)) {
			subFeatures.add(s);
		}
	}

	public void addSubBundle(BundleModel model) {
		if (!subBundles.contains(model)) {
			subBundles.add(model);
		}
	}
	
	/**
	 * @return the subBundles
	 */
	public BundleModel[] getSubBundles() {
		return subBundles.toArray(new BundleModel[0]);
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public void setContextList(String[] contextList) {
		if (contextList != null) {
			this.contextList = contextList;
		}
	}

	private String toFeatureString(String featureVersion, String featureName) {
		StringBuilder sb = new StringBuilder();
		sb.append("<feature version='");
		sb.append(featureVersion);
		sb.append("\'>");
		sb.append(featureName);
		sb.append("</feature>");
		return sb.toString();
	}

	private String toBundleString(BundleModel model) {
		StringBuilder sb = new StringBuilder();
		sb.append("<bundle>mvn:");
		sb.append(model.getGroupId());
		sb.append("/");
		sb.append(model.getArtifactId());
		sb.append("/");
		sb.append(model.getVersion());
		sb.append("</bundle>");
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		// add headers
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<features>\n");
		sb.append("\t<feature name=\"");
		sb.append(artifactId);
		sb.append("\" version=\"");
		sb.append(version);
		sb.append("\">\n");
		// add sub features
		for (String s : subFeatures) {
			sb.append("\t\t");
			sb.append(s);
			sb.append("\n");
		}

		// add sub bundles
		for (BundleModel s : subBundles) {
			sb.append("\t\t");
			sb.append(toBundleString(s));
			sb.append("\n");
		}

		// add config
		sb.append("\t\t<config name=\"");
		sb.append(configName);
		sb.append("\">\n");
		sb.append("\t\t\ttalendcontext=\"");
		for (int i = 0; i < contextList.length; i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(contextList[i]);
		}
		sb.append("\"\n");
		sb.append("\t\t</config>\n");

		sb.append("\t</feature>\n");
		sb.append("</features>");

		return sb.toString();
	}

	public String getFeatureContent() {
		return toString();
	}

	public static void main(String[] args) {
		FeaturesModel featuresModel = new FeaturesModel("aaa",
				"CustomService-feature", "1.0.0");
		// featuresModel.addSubBundle("talend", "job-control-bundle", "1.0");
		// featuresModel.addSubBundle("talend", "ProviderJob", "1.0");
		// featuresModel.addSubBundle("talend", "ESBProvider2", "1.0");
		// featuresModel.addSubFeature("5.0-SNAPSHOT", "talend-job-controller");
		featuresModel.setConfigName("aa.bb");
		featuresModel
				.setContextList(new String[] { "Default", "Product", "Dev" });
		System.out.println(featuresModel);
	}

}
