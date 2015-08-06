package org.talend.designer.camel.dependencies.core.model;

public interface IDependencyItem {

	/**
	 * types
	 */
	int IMPORT_PACKAGE = 0x1;
	int REQUIRE_BUNDLE = 0x2;
	int CLASS_PATH = 0x4;
	int EXPORT_PACKAGE = 0x8;

	String getName();

	String getLabel();

	boolean isBuiltIn();

	/**
	 * {@link #IMPORT_PACKAGE} {@link #REQUIRE_BUNDLE} {@link #CLASS_PATH}
	 * 
	 * @return
	 */
	int getType();

	public String getDescription();

}
