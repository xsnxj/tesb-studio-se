package org.talend.designer.camel.dependencies.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.talend.designer.camel.dependencies.core.Messages;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ExportPackage;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;

public class DependenciesCoreUtil {

	private static final String DELIMITER_REGEX = "\\|"; //$NON-NLS-1$
	private static final String DELIMITER = "|"; //$NON-NLS-1$
	private static final String ID = "org.talend.designer.camel.dependencies.core"; //$NON-NLS-1$
    private static final String BUNDLE_CLASSPATH_ID = ID + ".bundleClasspath"; //$NON-NLS-1$
    private static final String IMPORT_PACKAGE_ID = ID + ".importPackage"; //$NON-NLS-1$
	private static final String REQUIRE_BUNDLE_ID = ID + ".requireBundle"; //$NON-NLS-1$
	private static final String EXPORT_PACKAGE_ID = ID + ".exportPackage"; //$NON-NLS-1$

	public static void saveToMap(Map<Object, Object> map,
			Collection<BundleClasspath> bundleClasspaths,
			Collection<ImportPackage> importPackages,
			Collection<RequireBundle> requiredBundles,
			Collection<ExportPackage> exportPackages){
		//save classpaths
		StringBuilder sb = new StringBuilder();
		for (BundleClasspath b : bundleClasspaths) {
		    if (b.isChecked()) {
	            if (sb.length() != 0) {
	                sb.append(DELIMITER);
	            }
                sb.append(b);
	            sb.append(";"); //$NON-NLS-1$
	            sb.append(b.isChecked());
		    }
		}
		map.put(BUNDLE_CLASSPATH_ID, sb.toString());
		
		//save import-packages
		sb.setLength(0);
		for (ImportPackage ip : importPackages) {
			if (ip.isBuiltIn()) {
				continue;
			}
			if (sb.length() != 0) {
                sb.append(DELIMITER);
			}
            sb.append(ip);
		}
		map.put(IMPORT_PACKAGE_ID, sb.toString());

		//save require-bundles
		sb.setLength(0);
		for (RequireBundle rb : requiredBundles) {
			if (rb.isBuiltIn()) {
				continue;
			}
			if (sb.length() != 0) {
				sb.append(DELIMITER);
			}
            sb.append(rb);
		}
		map.put(REQUIRE_BUNDLE_ID, sb.toString());
		
		//save export-packages
		sb.setLength(0);
		for (ExportPackage ep : exportPackages) {
			if(ep.isBuiltIn()){
				continue;
			}
			if (sb.length() != 0) {
				sb.append(DELIMITER);
			}
            sb.append(ep);
		}
		map.put(EXPORT_PACKAGE_ID, sb.toString());
	}

    public static Collection<BundleClasspath> getStoredBundleClasspaths(Map<?, ?> map) {
        final Collection<BundleClasspath> list = new ArrayList<BundleClasspath>();
        for (String s : getDependencies(map, BUNDLE_CLASSPATH_ID)) {
            if (!s.isEmpty()) {
                BundleClasspath bcp = new BundleClasspath(s);
                // support old routes with null name for unchecked items
                if (null != bcp.getName() && !"null".equals(bcp.getName()) && bcp.isChecked()) {
                    list.add(bcp);
                }
            }
        }
        return list;
    }

    public static Collection<ImportPackage> getStoredImportPackages(Map<?, ?> map) {
        final Collection<ImportPackage> list = new ArrayList<ImportPackage>();
        for (String s : getDependencies(map, IMPORT_PACKAGE_ID)) {
            if (!s.isEmpty()) {
                ImportPackage importPackage = new ImportPackage(s);
                importPackage.setDescription(Messages.DependenciesCoreUtil_userDefinedImportPackage);
                list.add(importPackage);
            }
        }
        return list;
    }

    public static Collection<RequireBundle> getStoredRequireBundles(Map<?, ?> map) {
        final Collection<RequireBundle> list = new ArrayList<RequireBundle>();
        for (String s : getDependencies(map, REQUIRE_BUNDLE_ID)) {
            if (!s.isEmpty()) {
                RequireBundle requireBundle = new RequireBundle(s);
                requireBundle.setDescription(Messages.DependenciesCoreUtil_userDefinedRequireBundle);
                list.add(requireBundle);
            }
        }
        return list;
    }

    public static Collection<ExportPackage> getStoredExportPackages(Map<?, ?> map) {
        final Collection<ExportPackage> list = new ArrayList<ExportPackage>();
        for (String s : getDependencies(map, EXPORT_PACKAGE_ID)) {
            if (!s.isEmpty()) {
                ExportPackage exportPackage = new ExportPackage(s);
                exportPackage.setDescription(Messages.DependenciesCoreUtil_userDefinedExportPackage);
                list.add(exportPackage);
            }
        }
        return list;
	}

    private static String[] getDependencies(Map<?, ?> map, String key) {
        Object stored = map.get(key);
        if (stored != null) {
            return stored.toString().split(DELIMITER_REGEX);
        }
        return new String[] {};
    }

}
