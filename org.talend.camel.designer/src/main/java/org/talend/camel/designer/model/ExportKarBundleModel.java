package org.talend.camel.designer.model;

import org.talend.repository.model.RepositoryNode;

public class ExportKarBundleModel {

	private String bundleFilePath;
	private RepositoryNode originRepositoryNode;
	private String repositoryVersion;

	public ExportKarBundleModel(String bundleFilePath,
			RepositoryNode originRepositoryNode, String repositoryVersion) {
		super();
		this.bundleFilePath = bundleFilePath;
		this.originRepositoryNode = originRepositoryNode;
		this.repositoryVersion = repositoryVersion;
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

}
