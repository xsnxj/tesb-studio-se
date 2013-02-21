package org.talend.designer.camel.dependencies.core.model;

public class ImportPackage extends OsgiDependencies<ImportPackage> {

	public ImportPackage() {
		super();
	}

	public ImportPackage(String inputString) {
		super(inputString);
	}

	public ImportPackage(ImportPackage copied) {
		super(copied);
	}

	@Override
	public int getType() {
		return IMPORT_PACKAGE;
	}

	@Override
	protected String getVersionPrefix() {
		return "version";
	}
}
