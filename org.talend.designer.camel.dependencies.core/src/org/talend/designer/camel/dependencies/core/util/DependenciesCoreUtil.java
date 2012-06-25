package org.talend.designer.camel.dependencies.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;

@SuppressWarnings({"rawtypes","unchecked"})
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

	public static List<ImportPackage> getStoredImportPackages(EMap map) {
		Object stored = map.get(IMPORT_PACKAGE_ID);
		if (stored != null) {
			return convertToImportPackages(stored.toString());
		}
		return Collections.EMPTY_LIST;
	}

	public static List<RequireBundle> getStoredRequireBundles(EMap map) {
		Object stored = map.get(REQUIRE_BUNDLE_ID);
		if (stored != null) {
			return convertToRequireBundles(stored.toString());
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * 
	 * @param input
	 * @return {@link List<RequireBundle>}
	 */
	private static List<RequireBundle> convertToRequireBundles(String input) {
		if(input==null||input.trim().equals("")){
			return Collections.EMPTY_LIST;
		}
		List<RequireBundle> list = new ArrayList<RequireBundle>();
		String[] split = input.split(DELIMITER_REGEX);
		for (String s : split) {
			RequireBundle bundle = convertToRequireBundle(s);
			if (bundle != null) {
				list.add(bundle);
			}
		}
		return Collections.unmodifiableList(list);
	}

	/**
	 * 
	 * @param input
	 * @return {@link RequireBundle} or null if failed to convert
	 */
	private static RequireBundle convertToRequireBundle(String input) {
		if(input==null||input.trim().equals("")){
			return null;
		}
		RequireBundle requireBundle = new RequireBundle(input);
		return requireBundle;
	}

	/**
	 * 
	 * @param input
	 * @return {@link List<ImportPackage>}
	 */
	private static List<ImportPackage> convertToImportPackages(String input) {
		if(input==null||input.trim().equals("")){
			return Collections.EMPTY_LIST;
		}
		List<ImportPackage> list = new ArrayList<ImportPackage>();
		String[] split = input.split(DELIMITER_REGEX);
		for (String s : split) {
			ImportPackage importPackage = convertToImportPackage(s);
			if (importPackage != null) {
				list.add(importPackage);
			}
		}
		return Collections.unmodifiableList(list);
	}

	/**
	 * 
	 * @param input
	 * @return {@link ImportPackage} or null if failed to convert
	 */
	private static ImportPackage convertToImportPackage(String input) {
		if(input==null||input.trim().equals("")){
			return null;
		}
		ImportPackage importPackage = new ImportPackage(input);
		return importPackage;
	}

}
