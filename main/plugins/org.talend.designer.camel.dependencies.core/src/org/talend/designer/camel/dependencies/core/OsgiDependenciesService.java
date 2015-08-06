package org.talend.designer.camel.dependencies.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.talend.core.IOsgiDependenciesService;
import org.talend.core.model.properties.ProcessItem;
import org.talend.designer.camel.dependencies.core.model.IDependencyItem;

public class OsgiDependenciesService implements IOsgiDependenciesService {

    @Override
    public Map<String, String> getBundleDependences(ProcessItem pi) {
        final DependenciesResolver resolver = new DependenciesResolver(pi);
        Map<String, String> map = new HashMap<String, String>();
        map.put(IMPORT_PACKAGE, toManifestString(resolver.getImportPackages()));
        map.put(REQUIRE_BUNDLE, toManifestString(resolver.getRequireBundles()));
        map.put(BUNDLE_CLASSPATH, toManifestString(resolver.getBundleClasspaths()));
        map.put(EXPORT_PACKAGE, toManifestString(resolver.getExportPackages()));
        return map;
    }

    private static String toManifestString(Collection<? extends IDependencyItem> dependencyItems) {
        StringBuilder sb = new StringBuilder();
        for (IDependencyItem item : dependencyItems) {
            if (sb.length() != 0) {
                sb.append(OsgiDependenciesService.ITEM_SEPARATOR);
            }
            sb.append(item);
        }
        return sb.toString();
    }

}
