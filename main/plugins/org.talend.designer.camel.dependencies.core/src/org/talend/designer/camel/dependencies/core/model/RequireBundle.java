package org.talend.designer.camel.dependencies.core.model;


public class RequireBundle extends OsgiDependencies {

	public RequireBundle() {
		super();
	}

	public RequireBundle(String inputString) {
		super(inputString);
	}

	@Override
	protected String getVersionPrefix() {
		return "bundle-version";
	}

	@Override
	public int getType() {
		return REQUIRE_BUNDLE;
	}
}
