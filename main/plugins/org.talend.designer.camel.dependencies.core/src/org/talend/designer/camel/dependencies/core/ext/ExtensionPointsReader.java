package org.talend.designer.camel.dependencies.core.ext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.talend.designer.camel.dependencies.core.util.VersionValidateUtil;

public class ExtensionPointsReader {

	private static final String CHECKED = "checked"; //$NON-NLS-1$
	private static final String COMPONENT = "component"; //$NON-NLS-1$
	private static final String REGEX = "regex"; //$NON-NLS-1$
	private static final String ATTRIBUTE_VALUE = "attributeValue"; //$NON-NLS-1$
	private static final String PREDICATE = "predicate"; //$NON-NLS-1$
	private static final String BUNDLE_NAME = "bundleName"; //$NON-NLS-1$
	private static final String REQUIRE_BUNDLE = "requireBundle"; //$NON-NLS-1$
	private static final String ATTRIBUTE_NAME = "attributeName"; //$NON-NLS-1$
	private static final String IMPORT_PACKAGE = "importPackage"; //$NON-NLS-1$
	private static final String EXPORT_PACKAGE = "exportPackage"; //$NON-NLS-1$
	private static final String OPTIONAL = "optional"; //$NON-NLS-1$
	private static final String VERSION = "version";
	private static final String MIN_VERSION = "minVersion"; //$NON-NLS-1$
	private static final String MAX_VERSION = "maxVersion"; //$NON-NLS-1$
	private static final String PACKAGE_NAME = "packageName"; //$NON-NLS-1$
	private static final String COMPONENT_NAME = "componentName"; //$NON-NLS-1$

	private static final String REQUIRE_BUNDLE_EXT = "org.talend.designer.camel.dependencies.requireBundle"; //$NON-NLS-1$
	private static final String IMPORT_PACKAGE_EXT = "org.talend.designer.camel.dependencies.importPackage"; //$NON-NLS-1$
	private static final String BUNDLE_CLASSPATH_EXT = "org.talend.designer.camel.dependencies.bundleClasspath"; //$NON-NLS-1$
	private static final String EXPORT_PACKAGE_EXT = "org.talend.designer.camel.dependencies.exportPackage"; //$NON-NLS-1$

	public static ExtensionPointsReader INSTANCE = new ExtensionPointsReader();

	private Map<String, Set<ExBundleClasspath>> componentBundleClasspaths = new HashMap<String, Set<ExBundleClasspath>>();
	private Map<String, Set<ExImportPackage>> componentImportPackages = new HashMap<String, Set<ExImportPackage>>();
	private Map<String, Set<ExExportPackage>> componentExportPackages = new HashMap<String, Set<ExExportPackage>>();
	private Map<String, Set<ExRequireBundle>> componentRequireBundles = new HashMap<String, Set<ExRequireBundle>>();
	private Set<ExRequireBundle> requireBundlesForAll = new HashSet<ExRequireBundle>();
	private Set<ExImportPackage> importPackagesForAll = new HashSet<ExImportPackage>();
	private Set<ExExportPackage> exportPackagesForAll = new HashSet<ExExportPackage>();

	//this one is special case for ROUTE_WHEN Connection Type
	private Map<String, Set<ExImportPackage>> languageImportPackages = new HashMap<String, Set<ExImportPackage>>();

	private ExtensionPointsReader() {
		initialization();
	}

	private void initialization() {
		readRegisteredBundleClasspaths();
		readRegisteredImportPackages();
		readRegisteredExportPackages();
		readRegisteredRequireBundles();
		initLanguageDependenciesMap();
	}


	private void initLanguageDependenciesMap() {
		/*
		 * for languages, please check init(INode, INode, EConnectionType, String, String, String, boolean)
		 * of org.talend.designer.core.ui.editor.connections.Connection, and see the EConnectionType.ROUTE_WHEN case
		 */
//        String[] languages = { "constant", "el", "groovy", "header", "javaScript", "jxpath", "mvel", "ognl", "php", "property",
//                "python", "ruby", "simple", "spel", "sql", "xpath", "xquery" };
//
        Set<ExImportPackage> groovySet = new HashSet<ExImportPackage>();
        ExImportPackage importPackage = new ExImportPackage();
        importPackage.setPackageName("groovy.lang");
        groovySet.add(importPackage);

        importPackage = new ExImportPackage();
        importPackage.setPackageName("org.codehaus.groovy.runtime");
        groovySet.add(importPackage);

        importPackage = new ExImportPackage();
        importPackage.setPackageName("org.codehaus.groovy.runtime.callsite");
        groovySet.add(importPackage);

        importPackage = new ExImportPackage();
        importPackage.setPackageName("org.codehaus.groovy.runtime.typehandling");
        groovySet.add(importPackage);

        importPackage = new ExImportPackage();
        importPackage.setPackageName("org.codehaus.groovy.reflection");
        groovySet.add(importPackage);

        languageImportPackages.put("groovy", groovySet);
	}

	private void readRegisteredBundleClasspaths() {
		IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						BUNDLE_CLASSPATH_EXT);
		if (configurationElements == null || configurationElements.length == 0) {
			return;
		}
		for (IConfigurationElement e : configurationElements) {
			String attrName = e.getAttribute(ATTRIBUTE_NAME);
			String cmpName = e.getAttribute(COMPONENT_NAME);
			String isChecked = e.getAttribute(CHECKED);
			Set<ExBundleClasspath> attributeSet = componentBundleClasspaths
					.get(cmpName);
			if (attributeSet == null) {
				attributeSet = new HashSet<ExBundleClasspath>();
				componentBundleClasspaths.put(cmpName, attributeSet);
			}

			ExBundleClasspath bc = new ExBundleClasspath();
			bc.setAttributeName(attrName);

			if("true".equals(isChecked)){ //$NON-NLS-1$
				bc.setChecked(true);
			}else if("false".equals(isChecked)){ //$NON-NLS-1$
				bc.setChecked(false);
			}

			IConfigurationElement[] predicates = e.getChildren(PREDICATE);
			if (predicates != null) {
				for (IConfigurationElement pe : predicates) {
					String name = pe.getAttribute(ATTRIBUTE_NAME);
					String value = pe.getAttribute(ATTRIBUTE_VALUE);
					String isRegex = pe.getAttribute(REGEX);
					ExPredicate exPredicate = new ExPredicate();
					exPredicate.setAttributeName(name);
					exPredicate.setAttributeValue(value);
					if ("true".equals(isRegex)) { //$NON-NLS-1$
						exPredicate.setRegex(true);
					}
					bc.addPredicate(exPredicate);
				}
			}

			attributeSet.add(bc);
		}
	}

	private void readRegisteredImportPackages() {
		IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						IMPORT_PACKAGE_EXT);
		if (configurationElements == null || configurationElements.length == 0) {
			return;
		}
		for (IConfigurationElement e : configurationElements) {
			String name = e.getName();
			if (name.equals(COMPONENT)) {
				String cmpName = e.getAttribute(COMPONENT_NAME);
				Set<ExImportPackage> packageSet = componentImportPackages
						.get(cmpName);
				if (packageSet == null) {
					packageSet = new HashSet<ExImportPackage>();
					componentImportPackages.put(cmpName, packageSet);
				}
				IConfigurationElement[] packages = e
						.getChildren(IMPORT_PACKAGE);
				for (IConfigurationElement p : packages) {
					ExImportPackage importPackage = createImportPackageFrom(p);
					packageSet.add(importPackage);
				}
			} else {
				ExImportPackage importPackage = createImportPackageFrom(e);
				importPackagesForAll.add(importPackage);
			}
		}
	}

	private void readRegisteredExportPackages() {
		IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						EXPORT_PACKAGE_EXT);
		if (configurationElements == null || configurationElements.length == 0) {
			return;
		}
		for (IConfigurationElement e : configurationElements) {
			String name = e.getName();
			if (name.equals(COMPONENT)) {
				String cmpName = e.getAttribute(COMPONENT_NAME);
				Set<ExExportPackage> packageSet = componentExportPackages
						.get(cmpName);
				if (packageSet == null) {
					packageSet = new HashSet<ExExportPackage>();
					componentExportPackages.put(cmpName, packageSet);
				}
				IConfigurationElement[] packages = e
						.getChildren(EXPORT_PACKAGE);
				for (IConfigurationElement p : packages) {
					ExExportPackage exportPackage = createExportPackageFrom(p);
					packageSet.add(exportPackage);
				}
			} else {
				ExExportPackage exportPackage = createExportPackageFrom(e);
				exportPackagesForAll.add(exportPackage);
			}
		}
	}

	private ExExportPackage createExportPackageFrom(IConfigurationElement p) {
		String packageName = p.getAttribute(PACKAGE_NAME);
		String version = p.getAttribute(VERSION);

		ExExportPackage exportPackage = new ExExportPackage();
		exportPackage.setPackageName(packageName);
		if (version != null && !"".equals(version)) { //$NON-NLS-1$
			exportPackage.setVersion(version);
		}

		IConfigurationElement[] predicates = p.getChildren(PREDICATE);
		if (predicates != null) {
			for (IConfigurationElement pe : predicates) {
				String name = pe.getAttribute(ATTRIBUTE_NAME);
				String value = pe.getAttribute(ATTRIBUTE_VALUE);
				String isRegex = pe.getAttribute(REGEX);
				ExPredicate exPredicate = new ExPredicate();
				exPredicate.setAttributeName(name);
				exPredicate.setAttributeValue(value);
				if ("true".equals(isRegex)) { //$NON-NLS-1$
					exPredicate.setRegex(true);
				}
				exportPackage.addPredicate(exPredicate);
			}
		}
		return exportPackage;
	}

	private ExImportPackage createImportPackageFrom(IConfigurationElement p) {
		String packageName = p.getAttribute(PACKAGE_NAME);
		String maxVersion = p.getAttribute(MAX_VERSION);
		String minVersion = p.getAttribute(MIN_VERSION);
		String optional = p.getAttribute(OPTIONAL);

		ExImportPackage importPackage = new ExImportPackage();
		importPackage.setPackageName(packageName);
		if (maxVersion != null && !"".equals(maxVersion)) { //$NON-NLS-1$
//			importPackage.setMaxVersion(maxVersion);
		}
		if (minVersion != null && !"".equals(minVersion)) { //$NON-NLS-1$
//			importPackage.setMinVersion(minVersion);
		}
		if (optional != null) {
			importPackage.setOptional(Boolean.getBoolean(optional));
		}

		IConfigurationElement[] predicates = p.getChildren(PREDICATE);
		if (predicates != null) {
			for (IConfigurationElement pe : predicates) {
				String name = pe.getAttribute(ATTRIBUTE_NAME);
				String value = pe.getAttribute(ATTRIBUTE_VALUE);
				String isRegex = pe.getAttribute(REGEX);
				ExPredicate exPredicate = new ExPredicate();
				exPredicate.setAttributeName(name);
				exPredicate.setAttributeValue(value);
				if ("true".equals(isRegex)) { //$NON-NLS-1$
					exPredicate.setRegex(true);
				}
				importPackage.addPredicate(exPredicate);
			}
		}
		return importPackage;
	}

	private void readRegisteredRequireBundles() {
		IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						REQUIRE_BUNDLE_EXT);
		if (configurationElements == null || configurationElements.length == 0) {
			return;
		}
		for (IConfigurationElement e : configurationElements) {
			String name = e.getName();
			if (name.equals(COMPONENT)) {
				String cmpName = e.getAttribute(COMPONENT_NAME);
				Set<ExRequireBundle> bundleSet = componentRequireBundles
						.get(cmpName);
				if (bundleSet == null) {
					bundleSet = new HashSet<ExRequireBundle>();
					componentRequireBundles.put(cmpName, bundleSet);
				}
				IConfigurationElement[] bundles = e.getChildren(REQUIRE_BUNDLE);
				for (IConfigurationElement b : bundles) {
					ExRequireBundle requireBundle = createRequireBundleFrom(b);
					bundleSet.add(requireBundle);
				}
			} else {
				ExRequireBundle requireBundle = createRequireBundleFrom(e);
				requireBundlesForAll.add(requireBundle);
			}
		}
	}

	private ExRequireBundle createRequireBundleFrom(IConfigurationElement b) {
		String bundleName = b.getAttribute(BUNDLE_NAME);
		String maxVersion = b.getAttribute(MAX_VERSION);
		String minVersion = b.getAttribute(MIN_VERSION);
		String optional = b.getAttribute(OPTIONAL);

		ExRequireBundle requireBundle = new ExRequireBundle();
		requireBundle.setBundleName(bundleName);
		requireBundle.setVersionRange(VersionValidateUtil.tryToGetValidVersionRange(minVersion, maxVersion));


		if (optional != null) {
			requireBundle.setOptional(Boolean.getBoolean(optional));
		}

		IConfigurationElement[] predicates = b.getChildren(PREDICATE);
		if (predicates != null) {
			for (IConfigurationElement pe : predicates) {
				String name = pe.getAttribute(ATTRIBUTE_NAME);
				String value = pe.getAttribute(ATTRIBUTE_VALUE);
				String isRegex = pe.getAttribute(REGEX);
				ExPredicate exPredicate = new ExPredicate();
				exPredicate.setAttributeName(name);
				exPredicate.setAttributeValue(value);
				if ("true".equals(isRegex)) { //$NON-NLS-1$
					exPredicate.setRegex(true);
				}
				requireBundle.addPredicate(exPredicate);
			}
		}
		return requireBundle;
	}

	public Set<ExImportPackage> getImportPackagesForAll() {
		return importPackagesForAll;
	}

	public Set<ExExportPackage> getExportPackagesForAll() {
		return exportPackagesForAll;
	}

	public Set<ExRequireBundle> getRequireBundlesForAll() {
		return requireBundlesForAll;
	}

	public Map<String, Set<ExBundleClasspath>> getBundleClasspaths() {
		return componentBundleClasspaths;
	}

	public Map<String, Set<ExImportPackage>> getComponentImportPackages() {
		return componentImportPackages;
	}

	public Map<String, Set<ExRequireBundle>> getComponentRequireBundles() {
		return componentRequireBundles;
	}

	public Map<String, Set<ExImportPackage>> getLanguageImportPackages() {
		return languageImportPackages;
	}

	public Map<String, Set<ExExportPackage>> getComponentExportPackages() {
		return componentExportPackages;
	}

}
