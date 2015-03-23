package org.talend.designer.camel.dependencies.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.talend.core.IOsgiDependenciesService;
import org.talend.core.model.properties.ProcessItem;
import org.talend.designer.camel.dependencies.core.ext.ExDependenciesResolver;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ExportPackage;
import org.talend.designer.camel.dependencies.core.model.IDependencyItem;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;

public class OsgiDependenciesService implements IOsgiDependenciesService {

    private List<ImportPackage> importPackages;
    private List<RequireBundle> requireBundles;
    private List<BundleClasspath> bundleClasspaths;
    private List<ExportPackage> exportPackages;

    public static OsgiDependenciesService fromProcessItem(ProcessItem pi) {
        OsgiDependenciesService s = new OsgiDependenciesService();
        s.init(pi);
        return s;
    }

    @Override
    public Map<String, String> getBundleDependences(ProcessItem pi) {
        init(pi);

        Map<String, String> map = new HashMap<String, String>();
        map.put(IMPORT_PACKAGE, toManifestString(getImportPackages()));
        map.put(REQUIRE_BUNDLE, toManifestString(getRequireBundles()));
        map.put(BUNDLE_CLASSPATH, toManifestString(getBundleClasspaths()));
        map.put(EXPORT_PACKAGE, toManifestString(getExportPackages()));
        return map;
    }

    private void init(ProcessItem pi) {
        ExDependenciesResolver resolver = new ExDependenciesResolver(pi);
        EMap<?, ?> additionProperties = pi.getProperty().getAdditionalProperties();
        importPackages = new ArrayList<ImportPackage>(resolver.getImportPackages());
        requireBundles = new ArrayList<RequireBundle>(resolver.getRequireBundles());
        bundleClasspaths = new ArrayList<BundleClasspath>(resolver.getBundleClasspaths());

        for (ImportPackage ip : DependenciesCoreUtil.getStoredImportPackages(additionProperties)) {
            if (importPackages.contains(ip)) {
                continue;
            }
            importPackages.add(ip);
        }

        for (RequireBundle rb : DependenciesCoreUtil.getStoredRequireBundles(additionProperties)) {
            if (requireBundles.contains(rb)) {
                continue;
            }
            requireBundles.add(rb);
        }

        List<BundleClasspath> additionClasspaths = DependenciesCoreUtil.getStoredBundleClasspaths(additionProperties);
        for (BundleClasspath bcp : bundleClasspaths) {
            int index = additionClasspaths.indexOf(bcp);
            if(index != -1){
                bcp.setChecked(additionClasspaths.get(index).isChecked());
            }
        }

        //retrieve all export-packages
        exportPackages = new ArrayList<ExportPackage>(resolver.getExportPackages());
        for (ExportPackage rb : DependenciesCoreUtil.getStoredExportPackages(additionProperties)) {
            if (exportPackages.contains(rb)) {
                continue;
            }
            exportPackages.add(rb);
        }

        DependenciesListSorter sorter = new DependenciesListSorter();
        Collections.sort(importPackages, sorter);
        Collections.sort(requireBundles, sorter);
        Collections.sort(bundleClasspaths, sorter);
    }

    public Collection<BundleClasspath> getBundleClasspaths() {
        return bundleClasspaths;
    }

    public Collection<RequireBundle> getRequireBundles() {
        return requireBundles;
    }

    public Collection<ImportPackage> getImportPackages() {
        return importPackages;
    }

    public Collection<ExportPackage> getExportPackages() {
        return exportPackages;
    }

    private static String toManifestString(Collection<? extends IDependencyItem> dependencyItems) {
        StringBuilder sb = new StringBuilder();
        for (IDependencyItem item : dependencyItems) {
            String text = item.toManifestString();
            if (null == text) {
                continue;
            }
            if (sb.length() != 0) {
                sb.append(OsgiDependenciesService.ITEM_SEPARATOR);
            }
            sb.append(item.toManifestString());
        }
        return sb.toString();
    }

}
