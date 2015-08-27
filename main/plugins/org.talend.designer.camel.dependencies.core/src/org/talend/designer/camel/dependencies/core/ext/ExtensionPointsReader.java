package org.talend.designer.camel.dependencies.core.ext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class ExtensionPointsReader {

	private static final String COMPONENT = "component"; //$NON-NLS-1$
	private static final String REGEX = "regex"; //$NON-NLS-1$
	private static final String ATTRIBUTE_VALUE = "attributeValue"; //$NON-NLS-1$
	private static final String PREDICATE = "predicate"; //$NON-NLS-1$
	private static final String BUNDLE_NAME = "bundleName"; //$NON-NLS-1$
	private static final String REQUIRE_BUNDLE = "requireBundle"; //$NON-NLS-1$
	private static final String ATTRIBUTE_NAME = "attributeName"; //$NON-NLS-1$
	private static final String IMPORT_PACKAGE = "importPackage"; //$NON-NLS-1$
	private static final String OPTIONAL = "optional"; //$NON-NLS-1$
	private static final String PACKAGE_NAME = "packageName"; //$NON-NLS-1$
	private static final String COMPONENT_NAME = "componentName"; //$NON-NLS-1$

	private static final String REQUIRE_BUNDLE_EXT = "org.talend.designer.camel.dependencies.requireBundle"; //$NON-NLS-1$
	private static final String IMPORT_PACKAGE_EXT = "org.talend.designer.camel.dependencies.importPackage"; //$NON-NLS-1$
	private static final String BUNDLE_CLASSPATH_EXT = "org.talend.designer.camel.dependencies.bundleClasspath"; //$NON-NLS-1$

	public static ExtensionPointsReader INSTANCE = new ExtensionPointsReader();

	private Map<String, Set<ExBundleClasspath>> componentBundleClasspaths = new HashMap<String, Set<ExBundleClasspath>>();
	private Map<String, Set<ExImportPackage>> componentImportPackages = new HashMap<String, Set<ExImportPackage>>();
	private Map<String, Set<ExRequireBundle>> componentRequireBundles = new HashMap<String, Set<ExRequireBundle>>();
	private Set<ExRequireBundle> requireBundlesForAll = new HashSet<ExRequireBundle>();
	private Set<ExImportPackage> importPackagesForAll = new HashSet<ExImportPackage>();
	
	//this one is special case for ROUTE_WHEN Connection Type
	private Map<String, Set<ExImportPackage>> languageImportPackages = new HashMap<String, Set<ExImportPackage>>();

	private ExtensionPointsReader() {
		initialization();
	}

	private void initialization() {
		readRegisteredBundleClasspaths();
		readRegisteredImportPackages();
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
        importPackage.setName("groovy.lang");
        groovySet.add(importPackage);
        
        importPackage = new ExImportPackage();
        importPackage.setName("org.codehaus.groovy.runtime");
        groovySet.add(importPackage);
        
        importPackage = new ExImportPackage();
        importPackage.setName("org.codehaus.groovy.runtime.callsite");
        groovySet.add(importPackage);
        
        importPackage = new ExImportPackage();
        importPackage.setName("org.codehaus.groovy.runtime.typehandling");
        groovySet.add(importPackage);
        
        importPackage = new ExImportPackage();
        importPackage.setName("org.codehaus.groovy.reflection");
        groovySet.add(importPackage);
       
        languageImportPackages.put("groovy", groovySet);
	}

	private void readRegisteredBundleClasspaths() {
		IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						BUNDLE_CLASSPATH_EXT);
		for (IConfigurationElement e : configurationElements) {
			final String cmpName = e.getAttribute(COMPONENT_NAME);
            final ExBundleClasspath bc = new ExBundleClasspath();
            bc.setName(e.getAttribute(ATTRIBUTE_NAME));
            bc.setOptional(Boolean.parseBoolean(e.getAttribute(OPTIONAL)));
            parsePredicates(bc, e);

            Set<ExBundleClasspath> attributeSet = componentBundleClasspaths.get(cmpName);
            if (attributeSet == null) {
                attributeSet = new HashSet<ExBundleClasspath>();
                componentBundleClasspaths.put(cmpName, attributeSet);
            }
			attributeSet.add(bc);
		}
	}

	private void readRegisteredImportPackages() {
		IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						IMPORT_PACKAGE_EXT);
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

		ExImportPackage importPackage = new ExImportPackage();
		importPackage.setName(packageName);
		importPackage.setOptional(Boolean.parseBoolean(p.getAttribute(OPTIONAL)));
        parsePredicates(importPackage, p);
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

		ExRequireBundle requireBundle = new ExRequireBundle();
		requireBundle.setName(bundleName);
        requireBundle.setOptional(Boolean.parseBoolean(b.getAttribute(OPTIONAL)));
        parsePredicates(requireBundle, b);
		return requireBundle;
	}

    private static void parsePredicates(final AbstractExPredicator<?> abstractExPredicator,
        final IConfigurationElement element) {
        for (final IConfigurationElement pe : element.getChildren(PREDICATE)) {
            final ExPredicate exPredicate = new ExPredicate();
            exPredicate.setAttributeName(pe.getAttribute(ATTRIBUTE_NAME));
            exPredicate.setAttributeValue(pe.getAttribute(ATTRIBUTE_VALUE));
            exPredicate.setRegex(Boolean.parseBoolean(pe.getAttribute(REGEX)));
            abstractExPredicator.addPredicate(exPredicate);
        }
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
	
	public Map<String, Set<ExImportPackage>> getLanguageImportPackages() {
		return languageImportPackages;
	}
}
