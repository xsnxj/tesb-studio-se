package org.talend.camel.designer.model;

import org.talend.repository.model.RepositoryNode;

public class ExportKarBundleModel {

	public static final int ROUTE_TYPE = 1;
	public static final int JOB_TYPE = 2;
	
	
	private String bundleFilePath;
	private RepositoryNode originRepositoryNode;
	private String repositoryVersion;
	private int type = ROUTE_TYPE;

	public ExportKarBundleModel(String bundleFilePath,
			RepositoryNode originRepositoryNode, String repositoryVersion, int type) {
		super();
		this.bundleFilePath = bundleFilePath;
		this.originRepositoryNode = originRepositoryNode;
		this.repositoryVersion = repositoryVersion;
		this.type = type;
	}

	public String getBundleFilePath() {
		return bundleFilePath;
	}

	public RepositoryNode getRepositoryNode() {
		return originRepositoryNode;
	}

	public String getRepositoryVersion() {
		return repositoryVersion;
	}

	@Override
	public int hashCode() {
		return getBundleFilePath() == null ?super.hashCode():getBundleFilePath().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(getBundleFilePath() == null){
			return false;
		}
		if( obj == null){
			return false;
		}
		if(this == obj){
			return true;
		}
		if(!(obj instanceof ExportKarBundleModel)){
			return false;
		}
		ExportKarBundleModel tmp = (ExportKarBundleModel) obj;
		return bundleFilePath.equals(tmp.getBundleFilePath());
	}
	
	public int getType() {
		return type;
	}
}
