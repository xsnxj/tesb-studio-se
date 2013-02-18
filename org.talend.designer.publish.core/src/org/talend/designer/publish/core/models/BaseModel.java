// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.publish.core.models;


public class BaseModel {

	private final String groupId;
	private final String artifactId;
	private final String version;

	BaseModel(String groupId, String artifactId, String version) {
		if(groupId == null || artifactId == null || version == null) {
			throw new IllegalArgumentException();
		}
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
	public boolean equals(Object obj) {
		if(obj instanceof BaseModel) {
			BaseModel model = (BaseModel) obj;
			return groupId.equals(model.getGroupId()) && artifactId.equals(model.getArtifactId());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return (groupId + artifactId).hashCode();
	}

}
