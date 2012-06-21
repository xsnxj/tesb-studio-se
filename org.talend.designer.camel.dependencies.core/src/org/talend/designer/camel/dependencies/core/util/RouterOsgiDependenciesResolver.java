package org.talend.designer.camel.dependencies.core.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EMap;
import org.talend.core.IOsgiDependenciesService;
import org.talend.core.model.properties.ProcessItem;
import org.talend.designer.camel.dependencies.core.ext.ExDependenciesResolver;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;

public class RouterOsgiDependenciesResolver implements IOsgiDependenciesService {

	private Set<ImportPackage> importPackages = new HashSet<ImportPackage>();
	private Set<RequireBundle> requireBundles = new HashSet<RequireBundle>();
	private Set<BundleClasspath> bundleClasspaths = new HashSet<BundleClasspath>();

	public RouterOsgiDependenciesResolver(ProcessItem pi, EMap additions) {
		initialize(pi, additions);
	}
	
	public RouterOsgiDependenciesResolver() {
	}

	private void initialize(ProcessItem pi, EMap additions) {
		ExDependenciesResolver resolver = new ExDependenciesResolver(
				pi);
		importPackages.addAll(Arrays.asList(resolver.getImportPackages()));
		requireBundles.addAll(Arrays.asList(resolver.getRequireBundles()));
		bundleClasspaths.addAll(Arrays.asList(resolver.getBundleClasspaths()));

		importPackages.addAll(DependenciesCoreUtil
				.getStoredImportPackages(additions));
		requireBundles.addAll(DependenciesCoreUtil
				.getStoredRequireBundles(additions));
	}

	public Set<BundleClasspath> getBundleClasspaths() {
		return bundleClasspaths;
	}

	public Set<RequireBundle> getRequireBundles() {
		return requireBundles;
	}

	public Set<ImportPackage> getImportPackages() {
		return importPackages;
	}

	@Override
	public Map<String, String> getBundleDependences(ProcessItem pi,
			EMap additionProperties) {
		Set<ImportPackage> importPackages = new HashSet<ImportPackage>();
		Set<RequireBundle> requireBundles = new HashSet<RequireBundle>();
		Set<BundleClasspath> bundleClasspaths = new HashSet<BundleClasspath>();
		ExDependenciesResolver resolver = new ExDependenciesResolver(pi);
		importPackages.addAll(Arrays.asList(resolver.getImportPackages()));
		requireBundles.addAll(Arrays.asList(resolver.getRequireBundles()));
		bundleClasspaths.addAll(Arrays.asList(resolver.getBundleClasspaths()));

		importPackages.addAll(DependenciesCoreUtil
				.getStoredImportPackages(additionProperties));
		requireBundles.addAll(DependenciesCoreUtil
				.getStoredRequireBundles(additionProperties));

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
			if(sb.length() == 0){
				sb.append(rb);
			}else{
				sb.append(ITEM_SEPARATOR);
				sb.append(rb);
			}
		}
		map.put(REQUIRE_BUNDLE, sb.toString());
		return map;
	}

}
