package org.talend.designer.camel.dependencies.core.model;

public interface IDependencyItem {

	/**
	 * types
	 */
	public static final int IMPORT_PACKAGE = 0x1;
	public static final int REQUIRE_BUNDLE = 0x2;
	public static final int CLASS_PATH = 0x4;
	public static final int EXPORT_PACKAGE = 0x8;

	public String getLabel();

	public boolean isBuiltIn();

	/**
	 * {@link #IMPORT_PACKAGE} {@link #REQUIRE_BUNDLE} {@link #CLASS_PATH}
	 * 
	 * @return
	 */
	public int getType();
	
	public boolean strictEqual(Object obj);
	
	public boolean isChecked();
	
	public void setChecked(boolean isChecked);
	
	public String getDescription();

	public String toManifestString();
}
