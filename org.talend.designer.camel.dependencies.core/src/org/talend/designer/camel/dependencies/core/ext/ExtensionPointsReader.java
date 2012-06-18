package org.talend.designer.camel.dependencies.core.ext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class ExtensionPointsReader {

	private static final String COMPONENT = "component";
	private static final String REGEX = "regex";
	private static final String ATTRIBUTE_VALUE = "attributeValue";
	private static final String PREDICATE = "predicate";
	private static final String BUNDLE_NAME = "bundleName";
	private static final String REQUIRE_BUNDLE = "requireBundle";
	private static final String ATTRIBUTE_NAME = "attributeName";
	private static final String IMPORT_PACKAGE = "importPackage";
	private static final String OPTIONAL = "optional";
	private static final String MIN_VERSION = "minVersion";
	private static final String MAX_VERSION = "maxVersion";
	private static final String PACKAGE_NAME = "packageName";
	private static final String COMPONENT_NAME = "componentName";

	private static final String REQUIRE_BUNDLE_EXT = "org.talend.designer.camel.dependencies.requireBundle";
	private static final String IMPORT_PACKAGE_EXT = "org.talend.designer.camel.dependencies.importPackage";
	private static final String BUNDLE_CLASSPATH_EXT = "org.talend.designer.camel.dependencies.bundleClasspath";

	public static ExtensionPointsReader INSTANCE = new ExtensionPointsReader();

	private Map<String, Set<ExBundleClasspath>> componentBundleClasspaths = new HashMap<String, Set<ExBundleClasspath>>();
	private Map<String, Set<ExImportPackage>> componentImportPackages = new HashMap<String, Set<ExImportPackage>>();
	private Map<String, Set<ExRequireBundle>> componentRequireBundles = new HashMap<String, Set<ExRequireBundle>>();
	private Set<ExRequireBundle> requireBundlesForAll = new HashSet<ExRequireBundle>();
	private Set<ExImportPackage> importPackagesForAll = new HashSet<ExImportPackage>();

	private ExtensionPointsReader() {
		initialization();
	}

	private void initialization() {
		readRegisteredBundleClasspaths();
		readRegisteredImportPackages();
		readRegisteredRequireBundles();
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
			Set<ExBundleClasspath> attributeSet = componentBundleClasspaths
					.get(cmpName);
			if (attributeSet == null) {
				attributeSet = new HashSet<ExBundleClasspath>();
				componentBundleClasspaths.put(cmpName, attributeSet);
			}

			ExBundleClasspath bc = new ExBundleClasspath();
			bc.setAttributeName(attrName);

			IConfigurationElement[] predicates = e.getChildren(PREDICATE);
			if (predicates != null) {
				for (IConfigurationElement pe : predicates) {
					String name = pe.getAttribute(ATTRIBUTE_NAME);
					String value = pe.getAttribute(ATTRIBUTE_VALUE);
					String isRegex = pe.getAttribute(REGEX);
					ExPredicate exPredicate = new ExPredicate();
					exPredicate.setAttributeName(name);
					exPredicate.setAttributeValue(value);
					if ("true".equals(isRegex)) {
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

	private ExImportPackage createImportPackageFrom(IConfigurationElement p) {
		String packageName = p.getAttribute(PACKAGE_NAME);
		String maxVersion = p.getAttribute(MAX_VERSION);
		String minVersion = p.getAttribute(MIN_VERSION);
		String optional = p.getAttribute(OPTIONAL);

		ExImportPackage importPackage = new ExImportPackage();
		importPackage.setPackageName(packageName);
		if (maxVersion != null && !"".equals(maxVersion)) {
			importPackage.setMaxVersion(maxVersion);
		}
		if (minVersion != null && !"".equals(minVersion)) {
			importPackage.setMinVersion(minVersion);
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
				if ("true".equals(isRegex)) {
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
		if (maxVersion != null && !"".equals(maxVersion)) {
			requireBundle.setMaxVersion(maxVersion);
		}
		if (minVersion != null && !"".equals(minVersion)) {
			requireBundle.setMinVersion(minVersion);
		}
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
				if ("true".equals(isRegex)) {
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
}
