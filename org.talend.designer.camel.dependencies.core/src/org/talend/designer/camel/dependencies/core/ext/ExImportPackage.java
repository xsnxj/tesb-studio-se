package org.talend.designer.camel.dependencies.core.ext;

import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExImportPackage extends AbstractExPredicator<ImportPackage, ImportPackage> {

	private String packageName;
	private String versionRange;

	public String getVersionRange() {
		return versionRange;
	}

	public void setVersionRange(String versionRange) {
		this.versionRange = versionRange;
	}

	private boolean isOptional;

	ExImportPackage() {
	}

	void setPackageName(String packageName) {
		this.packageName = packageName;
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
		importPackage.setVersionRange(versionRange);
		importPackage.setName(packageName);
		importPackage.setOptional(isOptional);
		return importPackage;
	}
}
