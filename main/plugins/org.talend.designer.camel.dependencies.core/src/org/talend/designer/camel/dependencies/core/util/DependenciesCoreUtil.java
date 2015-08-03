package org.talend.designer.camel.dependencies.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.talend.designer.camel.dependencies.core.Messages;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ExportPackage;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;

@SuppressWarnings({"rawtypes","unchecked"})
public class DependenciesCoreUtil {

	private static final String DELIMITER_REGEX = "\\|"; //$NON-NLS-1$
	private static final String DELIMITER = "|"; //$NON-NLS-1$
	private static final String ID = "org.talend.designer.camel.dependencies.core"; //$NON-NLS-1$
	private static final String REQUIRE_BUNDLE_ID = ID + ".requireBundle"; //$NON-NLS-1$
	private static final String IMPORT_PACKAGE_ID = ID + ".importPackage"; //$NON-NLS-1$
	private static final String BUNDLE_CLASSPATH_ID = ID + ".bundleClasspath"; //$NON-NLS-1$
	private static final String EXPORT_PACKAGE_ID = ID + ".exportPackage"; //$NON-NLS-1$

	public static void saveToMap(Map map,
			Collection<BundleClasspath> bundleClasspaths,
			Collection<ImportPackage> importPackages,
			Collection<RequireBundle> requiredBundles,
			Collection<ExportPackage> exportPackages){
		//save classpaths
		StringBuilder sb = new StringBuilder();
		for (BundleClasspath b : bundleClasspaths) {
			if (sb.length() == 0) {
				sb.append(b);
			} else {
				sb.append(DELIMITER);
				sb.append(b);
			}
			sb.append(";"); //$NON-NLS-1$
			sb.append(b.isChecked());
		}
		map.put(BUNDLE_CLASSPATH_ID, sb.toString());
		
		//save import-packages
		sb.setLength(0);
		for (ImportPackage ip : importPackages) {
			if (ip.isBuiltIn()) {
				continue;
			}
			if (sb.length() == 0) {
				sb.append(ip.toManifestString());
			} else {
				sb.append(DELIMITER);
				sb.append(ip.toManifestString());
			}
		}
		map.put(IMPORT_PACKAGE_ID, sb.toString());

		//save require-bundles
		sb.setLength(0);
		for (RequireBundle rb : requiredBundles) {
			if (rb.isBuiltIn()) {
				continue;
			}
			if (sb.length() == 0) {
				sb.append(rb.toManifestString());
			} else {
				sb.append(DELIMITER);
				sb.append(rb.toManifestString());
			}
		}
		map.put(REQUIRE_BUNDLE_ID, sb.toString());
		
		//save export-packages
		sb.setLength(0);
		for (ExportPackage ep : exportPackages) {
			if(ep.isBuiltIn()){
				continue;
			}
			if (sb.length() == 0) {
				sb.append(ep);
			} else {
				sb.append(DELIMITER);
				sb.append(ep);
			}
		}
		map.put(EXPORT_PACKAGE_ID, sb.toString());
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
	
	public static List<BundleClasspath> getStoredBundleClasspaths(EMap map) {
		Object stored = map.get(BUNDLE_CLASSPATH_ID);
		if (stored != null) {
			return convertToBundleClasspaths(stored.toString());
		}
		return Collections.EMPTY_LIST;
	}
	
	public static List<ExportPackage> getStoredExportPackages(
			EMap map) {
		Object stored = map.get(EXPORT_PACKAGE_ID);
		if (stored != null) {
			return convertToExportPackages(stored.toString());
		}
		return Collections.EMPTY_LIST;
	}

	private static List<ExportPackage> convertToExportPackages(String input) {
		if(input==null||input.trim().equals("")){ //$NON-NLS-1$
			return Collections.EMPTY_LIST;
		}
		List<ExportPackage> list = new ArrayList<ExportPackage>();
		String[] split = input.split(DELIMITER_REGEX);
		for (String s : split) {
			ExportPackage exportPackage = convertToExportPackage(s);
			if (exportPackage != null) {
				list.add(exportPackage);
			}
		}
		return Collections.unmodifiableList(list);
	}

	private static ExportPackage convertToExportPackage(String input) {
		if(input==null||input.trim().equals("")){ //$NON-NLS-1$
			return null;
		}
		ExportPackage exportPackage = new ExportPackage(input);
		exportPackage.setDescription(Messages.DependenciesCoreUtil_userDefinedExportPackage);
		return exportPackage;
	}

	private static List<BundleClasspath> convertToBundleClasspaths(String input) {
		if(input==null||input.trim().equals("")){ //$NON-NLS-1$
			return Collections.EMPTY_LIST;
		}
		List<BundleClasspath> list = new ArrayList<BundleClasspath>();
		String[] split = input.split(DELIMITER_REGEX);
		for (String s : split) {
			BundleClasspath bundleClasspath = convertToBundleClasspath(s);
			if (bundleClasspath != null) {
				list.add(bundleClasspath);
			}
		}
		return Collections.unmodifiableList(list);
	}

	private static BundleClasspath convertToBundleClasspath(String input) {
		if(input==null||input.trim().equals("")){ //$NON-NLS-1$
			return null;
		}
		BundleClasspath bundleClasspath = new BundleClasspath(input);
		return bundleClasspath;
	}

	/**
	 * 
	 * @param input
	 * @return {@link List<RequireBundle>}
	 */
	private static List<RequireBundle> convertToRequireBundles(String input) {
		if(input==null||input.trim().equals("")){ //$NON-NLS-1$
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
		if(input==null||input.trim().equals("")){ //$NON-NLS-1$
			return null;
		}
		RequireBundle requireBundle = new RequireBundle(input);
		requireBundle.setDescription(Messages.DependenciesCoreUtil_userDefinedRequireBundle);
		return requireBundle;
	}

	/**
	 * 
	 * @param input
	 * @return {@link List<ImportPackage>}
	 */
	private static List<ImportPackage> convertToImportPackages(String input) {
		if(input==null||input.trim().equals("")){ //$NON-NLS-1$
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
		if(input==null||input.trim().equals("")){ //$NON-NLS-1$
			return null;
		}
		ImportPackage importPackage = new ImportPackage(input);
		importPackage.setDescription(Messages.DependenciesCoreUtil_userDefinedImportPackage);
		return importPackage;
	}

}
