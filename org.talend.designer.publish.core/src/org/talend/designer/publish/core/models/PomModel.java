package org.talend.designer.publish.core.models;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class PomModel extends UploadableModel{

	private String packaging;

	private Set<DependencyModel> dependencies = new HashSet<DependencyModel>();

	public PomModel(String groupId, String artifactId, String version,
			String packaging, String repositoryURL, String userName, String password) {
		super(groupId, artifactId, version, repositoryURL, userName, password);
		this.packaging = packaging;
	}

	@Override
	public void upload() throws IOException {
		
		String artifactPath = getArtifactDestination();
		String versionPath = artifactPath + version + "/";
		
		String fileName = getFileName();
		String filePath = versionPath + fileName;
		URL pomUrl = new URL(filePath);
		String pomContent = getPomContent();
		uploadContent(pomUrl, pomContent);

		// upload md5 and sha1
		uploadMd5AndSha1(filePath, fileName, pomContent);
	}
	
	public String getFileName() {
		return artifactId + "-" + version + ".pom";
	}

	public void addDenpendency(DependencyModel dependency) {
		if (!dependencies.contains(dependency)) {
			dependencies.add(dependency);
		}
	}

	@Override
	public int hashCode() {
		return groupId.hashCode() * 31 + artifactId.hashCode() * 7
				+ version.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		PomModel dm = (PomModel) obj;
		return groupId.equals(dm.groupId) && artifactId.equals(dm.artifactId)
				&& version.equals(dm.version);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		sb.append("<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"\n");
		sb.append("\txmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");

		sb.append("\t<modelVersion>4.0.0</modelVersion>\n");

		sb.append("\t<groupId>");
		sb.append(groupId);
		sb.append("</groupId>\n");

		sb.append("\t<artifactId>");
		sb.append(artifactId);
		sb.append("</artifactId>\n");

		sb.append("\t<version>");
		sb.append(version);
		sb.append("</version>\n");

		sb.append("\t<packaging>");
		sb.append(packaging);
		sb.append("</packaging>\n");

		if (dependencies.size() > 0) {
			sb.append(generateDependenciesContent());
		}

		sb.append("</project>");
		return sb.toString();
	}

	public String getPomContent() {
		return toString();
	}

	private String generateDependenciesContent() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t<dependencies>\n");
		for (DependencyModel dm : dependencies) {
			sb.append(dm);
		}
		sb.append("\t</dependencies>\n");
		return sb.toString();
	}

	//for test
//	public static void main(String[] args) {
//		PomModel pomModel = new PomModel("org.talend.liugang", "TestEERoute",
//				"1.0.28", "jar");
//		System.out.println(pomModel);
//		DependencyModel dependencyModel = new DependencyModel("com.liugang.aa",
//				"bbb", "1.0.2");
//		pomModel.addDenpendency(dependencyModel);
//		System.out.println(pomModel);
//		System.out.println(pomModel.getFileName());
//	}
}
