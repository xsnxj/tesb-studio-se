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
import org.talend.designer.camel.dependencies.core.model.ExportPackage;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;

public class RouterOsgiDependenciesResolver implements IOsgiDependenciesService {

	private List<ImportPackage> importPackages = new ArrayList<ImportPackage>();
	private List<RequireBundle> requireBundles = new ArrayList<RequireBundle>();
	private List<BundleClasspath> bundleClasspaths = new ArrayList<BundleClasspath>();
	private List<ExportPackage> exportPackages = new ArrayList<ExportPackage>();
	
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
		
		List<BundleClasspath> additionClasspaths = DependenciesCoreUtil.getStoredBundleClasspaths(additions);
		for(BundleClasspath bcp: bundleClasspaths){
			int index = additionClasspaths.indexOf(bcp);
			if(index != -1){
				BundleClasspath stored = additionClasspaths.get(index);
				bcp.setChecked(stored.isChecked());
			}
		}

		//retrieve all export-packages
		exportPackages.addAll(Arrays.asList(resolver.getExportPackages()));
		List<ExportPackage> additionExports = DependenciesCoreUtil
				.getStoredExportPackages(additions);
		for (ExportPackage rb : additionExports) {
			if (exportPackages.contains(rb)) {
				continue;
			}
			exportPackages.add(rb);
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
	

	public List<ExportPackage> getExportPackages() {
		return exportPackages;
	}

	@Override
	public Map<String, String> getBundleDependences(ProcessItem pi,
			EMap<?, ?> additionProperties) {

		List<ImportPackage> importPackages = new ArrayList<ImportPackage>();
		List<RequireBundle> requireBundles = new ArrayList<RequireBundle>();
		List<BundleClasspath> bundleClasspaths = new ArrayList<BundleClasspath>();
		List<ExportPackage> exportPackages = new ArrayList<ExportPackage>();

		ExDependenciesResolver resolver = new ExDependenciesResolver(pi);

		//retrieve all import-packages
		importPackages.addAll(Arrays.asList(resolver.getImportPackages()));
		List<ImportPackage> additionImports = DependenciesCoreUtil
				.getStoredImportPackages(additionProperties);
		for (ImportPackage ip : additionImports) {
			if (importPackages.contains(ip)) {
				continue;
			}
			importPackages.add(ip);
		}
		
		//retrieve all require-bundles
		requireBundles.addAll(Arrays.asList(resolver.getRequireBundles()));
		List<RequireBundle> additionRequires = DependenciesCoreUtil
				.getStoredRequireBundles(additionProperties);
		for (RequireBundle rb : additionRequires) {
			if (requireBundles.contains(rb)) {
				continue;
			}
			requireBundles.add(rb);
		}
		
		//retrieve all bundle-classpaths
		bundleClasspaths.addAll(Arrays.asList(resolver.getBundleClasspaths()));
		List<BundleClasspath> additionClasspaths = DependenciesCoreUtil.getStoredBundleClasspaths(additionProperties);
		for(BundleClasspath bcp: bundleClasspaths){
			int index = additionClasspaths.indexOf(bcp);
			if(index != -1){
				BundleClasspath stored = additionClasspaths.get(index);
				bcp.setChecked(stored.isChecked());
			}
		}
		
		//retrieve all export-packages
		exportPackages.addAll(Arrays.asList(resolver.getExportPackages()));
		List<ExportPackage> additionExports = DependenciesCoreUtil
				.getStoredExportPackages(additionProperties);
		for (ExportPackage rb : additionExports) {
			if (exportPackages.contains(rb)) {
				continue;
			}
			exportPackages.add(rb);
		}

		Collections.sort(importPackages, sorter);
		Collections.sort(requireBundles, sorter);
		Collections.sort(bundleClasspaths, sorter);

		Map<String, String> map = new HashMap<String, String>();

		//convert bundle-classpaths to string
		StringBuilder sb = new StringBuilder();
		for (BundleClasspath bc : bundleClasspaths) {
			if(!bc.isChecked()){
				continue;
			}
			if (sb.length() == 0) {
				sb.append(bc);
			} else {
				sb.append(ITEM_SEPARATOR);
				sb.append(bc);
			}
		}
		map.put(BUNDLE_CLASSPATH, sb.toString());
		
		//convert import-packages to string
		sb.setLength(0);
		for (ImportPackage ip : importPackages) {
			if (sb.length() == 0) {
				sb.append(ip.toManifestString());
			} else {
				sb.append(ITEM_SEPARATOR);
				sb.append(ip.toManifestString());
			}
		}
		map.put(IMPORT_PACKAGE, sb.toString());
		
		//convert require-bundles to string
		sb.setLength(0);
		for (RequireBundle rb : requireBundles) {
			if (sb.length() == 0) {
				sb.append(rb.toManifestString());
			} else {
				sb.append(ITEM_SEPARATOR);
				sb.append(rb.toManifestString());
			}
		}
		map.put(REQUIRE_BUNDLE, sb.toString());
		
		//convert export-packages to string
		sb.setLength(0);
		for (ExportPackage rb : exportPackages) {
			if (sb.length() == 0) {
				sb.append(rb);
			} else {
				sb.append(ITEM_SEPARATOR);
				sb.append(rb);
			}
		}
		map.put(EXPORT_PACKAGE, sb.toString());
		
		return map;
	}

}
