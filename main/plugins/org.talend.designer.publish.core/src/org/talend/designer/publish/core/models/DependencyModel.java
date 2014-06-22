package org.talend.designer.publish.core.models;

public class DependencyModel {

	private String groupId;
	private String artifactId;
	private String version;
	
	private String type;

	public DependencyModel(String groupId, String artifactId, String version, String type) {
		this(groupId,artifactId,version);
		this.type = type;
	}
	
	public DependencyModel(String groupId, String artifactId, String version) {
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
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
		DependencyModel dm = (DependencyModel) obj;
		return groupId.equals(dm.groupId) && artifactId.equals(dm.artifactId)
				&& version.equals(dm.version);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t\t<dependency>\n");
		sb.append("\t\t\t<groupId>");
		sb.append(groupId);
		sb.append("</groupId>\n");

		sb.append("\t\t\t<artifactId>");
		sb.append(artifactId);
		sb.append("</artifactId>\n");

		sb.append("\t\t\t<version>");
		sb.append(version);
		sb.append("</version>\n");
		
		if(type!=null){
			sb.append("\t\t\t<type>");
			sb.append(type);
			sb.append("</type>\n");
		}
		sb.append("\t\t</dependency>\n");
		return sb.toString();
	}
}
