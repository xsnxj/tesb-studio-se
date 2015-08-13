package org.talend.designer.camel.dependencies.core;

import java.util.HashMap;
import java.util.Map;

import org.talend.core.IOsgiDependenciesService;
import org.talend.core.model.properties.ProcessItem;

public class OsgiDependenciesService implements IOsgiDependenciesService {

    @Override
    public Map<String, String> getBundleDependences(ProcessItem pi) {
        final DependenciesResolver resolver = new DependenciesResolver(pi);
        Map<String, String> map = new HashMap<String, String>();
        map.put(IMPORT_PACKAGE, resolver.getManifestImportPackage(ITEM_SEPARATOR));
        map.put(REQUIRE_BUNDLE, resolver.getManifestRequireBundle(ITEM_SEPARATOR));
        map.put(BUNDLE_CLASSPATH, resolver.getManifestBundleClasspath(ITEM_SEPARATOR));
        map.put(EXPORT_PACKAGE, resolver.getManifestExportPackage(ITEM_SEPARATOR));
        return map;
    }

}
