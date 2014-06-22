package org.talend.designer.camel.dependencies.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.talend.designer.camel.dependencies.core.messages"; //$NON-NLS-1$
	public static String DependenciesCoreUtil_userDefinedExportPackage;
	public static String DependenciesCoreUtil_userDefinedImportPackage;
	public static String DependenciesCoreUtil_userDefinedRequireBundle;
	public static String ExDependenciesResolver_commonImportPackage;
	public static String ExDependenciesResolver_commonRequireBundle;
	public static String ExDependenciesResolver_generatedPackage;
	public static String VersionValidateUtil_InvalidFormatInBundleVersion;
	public static String VersionValidateUtil_invalidVersionRangeFormat;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
