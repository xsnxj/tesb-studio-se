package org.talend.designer.camel.dependencies.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.talend.core.model.properties.ProcessItem;
import org.talend.designer.camel.dependencies.core.ext.ExDependenciesResolver;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ExportPackage;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;

public class RouterOsgiDependenciesResolver {

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

}
