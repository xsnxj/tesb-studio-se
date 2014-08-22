package org.talend.designer.camel.dependencies.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

	private static class DependencyItems<E extends IDependencyItem> {
		private final Set<E> dependencies = new TreeSet<E>(new DependenciesListSorter());
		private final String name;

		DependencyItems(String name) {
			this.name = name;
		}

		void addAll(E[] dependencies) {
			addAll(Arrays.asList(dependencies));
		}

		void addAll(Collection<E> dependencies) {
			this.dependencies.addAll(dependencies);
		}

		String toManifestString() {
			StringBuilder sb = new StringBuilder();
			for (E item : dependencies) {
				String manifestString = buildManifestString(item);
				if(manifestString == null) {
					continue;
				}
				if (sb.length() != 0) {
					sb.append(ITEM_SEPARATOR);
				}
				sb.append(manifestString);
			}
			return sb.toString();
		}

		String buildManifestString(E item) {
			return item.toManifestString();
		};
	}

	@Override
	public Map<String, String> getBundleDependences(ProcessItem pi, String targetBundleVersion) {
		Map<String, String> map = new HashMap<String, String>();
		for (DependencyItems<?> items : getBundleDependencyItems(pi, targetBundleVersion)) {
			map.put(items.name, items.toManifestString());
		}
		return map;
	}

	private static DependencyItems<?>[] getBundleDependencyItems(final ProcessItem pi, final String targetBundleVersion) {
		EMap<?, ?> additionProperties = pi.getProperty().getAdditionalProperties();

		DependencyItems<ImportPackage> importPackages = new DependencyItems<ImportPackage>(IMPORT_PACKAGE);
		DependencyItems<RequireBundle> requireBundles = new DependencyItems<RequireBundle>(REQUIRE_BUNDLE);
		DependencyItems<ExportPackage> exportPackages = new DependencyItems<ExportPackage>(EXPORT_PACKAGE);
		DependencyItems<BundleClasspath> bundleClasspaths = new DependencyItems<BundleClasspath>(BUNDLE_CLASSPATH) {
			@Override
			String buildManifestString(BundleClasspath item) {
				return item.isChecked() ? super.buildManifestString(item) : null;
			}
		};

		ExDependenciesResolver resolver = new ExDependenciesResolver(pi, targetBundleVersion);

		// retrieve all import-packages
		importPackages.addAll(resolver.getImportPackages());
		importPackages.addAll(DependenciesCoreUtil.getStoredImportPackages(additionProperties));

		// retrieve all require-bundles
		requireBundles.addAll(resolver.getRequireBundles());
		requireBundles.addAll(DependenciesCoreUtil.getStoredRequireBundles(additionProperties));

		// retrieve all bundle-classpaths
		bundleClasspaths.addAll(resolver.getBundleClasspaths());
		bundleClasspaths.addAll(DependenciesCoreUtil.getStoredBundleClasspaths(additionProperties));

		// retrieve all export-packages
		exportPackages.addAll(resolver.getExportPackages());
		exportPackages.addAll(DependenciesCoreUtil.getStoredExportPackages(additionProperties));

		DependencyItems<?>[] itemsArray = {importPackages, requireBundles, bundleClasspaths, exportPackages};
		return itemsArray;
	}

}
