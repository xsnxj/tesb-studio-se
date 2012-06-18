package org.talend.designer.camel.dependencies.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EMap;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.OsgiDependencies;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;

public class DependenciesCoreUtil {

	private static final String DELIMITER_REGEX = "\\|";
	private static final String DELIMITER = "|";
	private static final String ID = "org.talend.designer.camel.dependencies.core";
	private static final String REQUIRE_BUNDLE_ID = ID + ".requireBundle";
	private static final String IMPORT_PACKAGE_ID = ID + ".importPackage";
	private static final String BUNDLE_CLASSPATH_ID = ID + ".bundleClasspath";

	public static void saveToMap(EMap map,
			Collection<BundleClasspath> bundleClasspaths,
			Collection<ImportPackage> importPackages,
			Collection<RequireBundle> requiredBundles){
		StringBuilder sb = new StringBuilder();
		for (BundleClasspath b : bundleClasspaths) {
			if (b.isBuiltIn()) {
				continue;
			}
			if (sb.length() == 0) {
				sb.append(b);
			} else {
				sb.append(DELIMITER);
				sb.append(b);
			}
		}
		map.put(BUNDLE_CLASSPATH_ID, sb.toString());
		sb = null;
		sb = new StringBuilder();
		for (ImportPackage ip : importPackages) {
			if (ip.isBuiltIn()) {
				continue;
			}
			if (sb.length() == 0) {
				sb.append(ip);
			} else {
				sb.append(DELIMITER);
				sb.append(ip);
			}
		}
		map.put(IMPORT_PACKAGE_ID, sb.toString());

		sb = null;
		sb = new StringBuilder();
		for (RequireBundle rb : requiredBundles) {
			if (rb.isBuiltIn()) {
				continue;
			}
			if (sb.length() == 0) {
				sb.append(rb);
			} else {
				sb.append(DELIMITER);
				sb.append(rb);
			}
		}
		map.put(REQUIRE_BUNDLE_ID, sb.toString());
	}

	public static Set<ImportPackage> getStoredImportPackages(EMap map) {
		Object stored = map.get(IMPORT_PACKAGE_ID);
		if (stored != null) {
			return convertToImportPackages(stored.toString());
		}
		return Collections.EMPTY_SET;
	}

	public static Set<RequireBundle> getStoredRequireBundles(EMap map) {
		Object stored = map.get(REQUIRE_BUNDLE_ID);
		if (stored != null) {
			return convertToRequireBundles(stored.toString());
		}
		return Collections.EMPTY_SET;
	}

	/**
	 * 
	 * @param bundleString
	 * @return {@link Set<RequireBundle>}
	 */
	private static Set<RequireBundle> convertToRequireBundles(String bundleString) {
		if(bundleString==null||bundleString.trim().equals("")){
			return Collections.EMPTY_SET;
		}
		Set<RequireBundle> set = new HashSet<RequireBundle>();
		String[] split = bundleString.split(DELIMITER_REGEX);
		for (String s : split) {
			RequireBundle bundle = convertToRequireBundle(s);
			if (bundle != null) {
				set.add(bundle);
			}
		}
		return Collections.unmodifiableSet(set);
	}

	/**
	 * 
	 * @param bundleString
	 * @return {@link RequireBundle} or null if failed to convert
	 */
	private static RequireBundle convertToRequireBundle(String bundleString) {
		if(bundleString==null||bundleString.trim().equals("")){
			return null;
		}
		RequireBundle requireBundle = new RequireBundle();
		return convertToDependencies(bundleString, requireBundle);
	}

	/**
	 * 
	 * @param bundleString
	 * @return {@link Set<ImportPackage>}
	 */
	private static Set<ImportPackage> convertToImportPackages(String bundleString) {
		if(bundleString==null||bundleString.trim().equals("")){
			return Collections.EMPTY_SET;
		}
		Set<ImportPackage> set = new HashSet<ImportPackage>();
		String[] split = bundleString.split(DELIMITER_REGEX);
		for (String s : split) {
			ImportPackage importPackage = convertToImportPackage(s);
			if (importPackage != null) {
				set.add(importPackage);
			}
		}
		return Collections.unmodifiableSet(set);
	}

	/**
	 * 
	 * @param bundleString
	 * @return {@link ImportPackage} or null if failed to convert
	 */
	private static ImportPackage convertToImportPackage(String bundleString) {
		if(bundleString==null||bundleString.trim().equals("")){
			return null;
		}
		ImportPackage importPackage = new ImportPackage();
		return convertToDependencies(bundleString, importPackage);
	}

	private static <T extends OsgiDependencies> T convertToDependencies(
			String bundleString, T dependencies) {
		try {
			String[] split = bundleString.split(";");
			dependencies.setName(split[0]);
			if (split.length > 1) {
				int firstQuote = split[1].indexOf("\"");
				int lastQuote = split[1].lastIndexOf("\"");

				if (split[1].indexOf(",") != -1) {
					String s = split[1]
							.substring(firstQuote + 2, lastQuote - 1);
					String[] versions = s.split(",");
					dependencies.setMaxVersion(versions[1]);
					dependencies.setMinVersion(versions[0]);
				} else {
					dependencies.setMinVersion(split[1].substring(
							firstQuote + 1, lastQuote));
				}
			}

			if (split.length > 2) {
				dependencies.setOptional(true);
			}
			return dependencies;
		} catch (Exception e) {
			return null;
		}
	}

}
