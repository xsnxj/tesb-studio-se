package org.talend.designer.camel.dependencies.core.ext;

import org.talend.designer.camel.dependencies.core.model.ExportPackage;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExExportPackage extends AbstractExPredicator<ExportPackage, ExportPackage> {

	private String packageName;
	private String version;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}


	ExExportPackage() {
	}

	void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	protected ExportPackage to(NodeType t) {
		ExportPackage exportPackage = toTargetIgnorePredicates();
		return exportPackage;
	}

	@Override
	public ExportPackage toTargetIgnorePredicates() {
		ExportPackage exportPackage = new ExportPackage();
		exportPackage.setBuiltIn(true);
		exportPackage.setVersion(version);
		exportPackage.setName(packageName);
		return exportPackage;
	}
}
