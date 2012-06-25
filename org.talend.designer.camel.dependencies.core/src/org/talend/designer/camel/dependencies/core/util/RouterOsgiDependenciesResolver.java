package org.talend.designer.camel.dependencies.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.talend.core.IOsgiDependenciesService;
import org.talend.core.model.properties.ProcessItem;
import org.talend.designer.camel.dependencies.core.ext.ExDependenciesResolver;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;

public class RouterOsgiDependenciesResolver implements IOsgiDependenciesService {

	private List<ImportPackage> importPackages = new ArrayList<ImportPackage>();
	private List<RequireBundle> requireBundles = new ArrayList<RequireBundle>();
	private List<BundleClasspath> bundleClasspaths = new ArrayList<BundleClasspath>();
	private DependenciesListSorter sorter = new DependenciesListSorter();

	public RouterOsgiDependenciesResolver(ProcessItem pi, EMap<?, ?> additions) {
		initialize(pi, additions);
	}

	public RouterOsgiDependenciesResolver() {
	}

	private void initialize(ProcessItem pi, EMap<?, ?> additions) {
		ExDependenciesResolver resolver = new ExDependenciesResolver(pi);
		importPackages.addAll(Arrays.asList(resolver.getImportPackages()));
		requireBundles.addAll(Arrays.asList(resolver.getRequireBundles()));
		bundleClasspaths.addAll(Arrays.asList(resolver.getBundleClasspaths()));

		List<ImportPackage> additionImports = DependenciesCoreUtil
				.getStoredImportPackages(additions);
		for (ImportPackage ip : additionImports) {
			if (importPackages.contains(ip)) {
				continue;
			}
			importPackages.add(ip);
		}
		List<RequireBundle> additionRequires = DependenciesCoreUtil
				.getStoredRequireBundles(additions);
		for (RequireBundle rb : additionRequires) {
			if (requireBundles.contains(rb)) {
				continue;
			}
			requireBundles.add(rb);
		}

		Collections.sort(importPackages, sorter);
		Collections.sort(requireBundles, sorter);
		Collections.sort(bundleClasspaths, sorter);
	}

	public List<BundleClasspath> getBundleClasspaths() {
		return bundleClasspaths;
	}

	public List<RequireBundle> getRequireBundles() {
		return requireBundles;
	}

	public List<ImportPackage> getImportPackages() {
		return importPackages;
	}

	@Override
	public Map<String, String> getBundleDependences(ProcessItem pi,
			EMap<?, ?> additionProperties) {

		List<ImportPackage> importPackages = new ArrayList<ImportPackage>();
		List<RequireBundle> requireBundles = new ArrayList<RequireBundle>();
		List<BundleClasspath> bundleClasspaths = new ArrayList<BundleClasspath>();

		ExDependenciesResolver resolver = new ExDependenciesResolver(pi);
		importPackages.addAll(Arrays.asList(resolver.getImportPackages()));
		requireBundles.addAll(Arrays.asList(resolver.getRequireBundles()));
		bundleClasspaths.addAll(Arrays.asList(resolver.getBundleClasspaths()));

		List<ImportPackage> additionImports = DependenciesCoreUtil
				.getStoredImportPackages(additionProperties);
		for (ImportPackage ip : additionImports) {
			if (importPackages.contains(ip)) {
				continue;
			}
			importPackages.add(ip);
		}
		List<RequireBundle> additionRequires = DependenciesCoreUtil
				.getStoredRequireBundles(additionProperties);
		for (RequireBundle rb : additionRequires) {
			if (requireBundles.contains(rb)) {
				continue;
			}
			requireBundles.add(rb);
		}

		Collections.sort(importPackages, sorter);
		Collections.sort(requireBundles, sorter);
		Collections.sort(bundleClasspaths, sorter);

		Map<String, String> map = new HashMap<String, String>();

		StringBuilder sb = new StringBuilder();
		for (BundleClasspath bc : bundleClasspaths) {
			if (sb.length() == 0) {
				sb.append(bc);
			} else {
				sb.append(ITEM_SEPARATOR);
				sb.append(bc);
			}
		}
		map.put(BUNDLE_CLASSPATH, sb.toString());
		sb = null;
		sb = new StringBuilder();
		for (ImportPackage ip : importPackages) {
			if (sb.length() == 0) {
				sb.append(ip);
			} else {
				sb.append(ITEM_SEPARATOR);
				sb.append(ip);
			}
		}
		map.put(IMPORT_PACKAGE, sb.toString());
		sb = null;
		sb = new StringBuilder();
		for (RequireBundle rb : requireBundles) {
			if (sb.length() == 0) {
				sb.append(rb);
			} else {
				sb.append(ITEM_SEPARATOR);
				sb.append(rb);
			}
		}
		map.put(REQUIRE_BUNDLE, sb.toString());
		return map;
	}

}
