package org.talend.designer.camel.dependencies.core.model;


public class RequireBundle extends OsgiDependencies<RequireBundle> {

	public RequireBundle() {
		super();
	}

	public RequireBundle(String inputString) {
		super(inputString);
	}

	public RequireBundle(RequireBundle copied) {
		super(copied);
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
