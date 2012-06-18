package org.talend.designer.camel.dependencies.core.ext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExImportPackage extends AbstractExPredicator<ImportPackage> {

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

	protected Set<ImportPackage> to(NodeType t) {
		ImportPackage importPackage = toTargetIgnorePredicates();
		Set<ImportPackage> s = new HashSet<ImportPackage>();
		s.add(importPackage);
		return Collections.unmodifiableSet(s);
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
