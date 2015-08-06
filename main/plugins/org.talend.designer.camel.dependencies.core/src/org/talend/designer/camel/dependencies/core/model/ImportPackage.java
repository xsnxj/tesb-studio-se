package org.talend.designer.camel.dependencies.core.model;

public class ImportPackage extends OsgiDependencies {

	public ImportPackage() {
		super();
	}

	public ImportPackage(String inputString) {
		super(inputString);
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
