package org.talend.designer.camel.dependencies.core.ext;

import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExImportPackage extends AbstractExPredicator<ImportPackage, ImportPackage> {

	private String packageName;
	private String minVersion;
	private String maxVersion;
	private boolean isOptional;

	ExImportPackage() {
	}

	void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	void setMaxVersion(String maxVersion) {
		this.maxVersion = maxVersion;
	}

	void setMinVersion(String minVersion) {
		this.minVersion = minVersion;
	}

	void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	protected ImportPackage to(NodeType t) {
		ImportPackage importPackage = toTargetIgnorePredicates();
		return importPackage;
	}

	@Override
	public ImportPackage toTargetIgnorePredicates() {
		ImportPackage importPackage = new ImportPackage();
		importPackage.setBuiltIn(true);
		importPackage.setMaxVersion(maxVersion);
		importPackage.setMinVersion(minVersion);
		importPackage.setName(packageName);
		importPackage.setOptional(isOptional);
		return importPackage;
	}
}
