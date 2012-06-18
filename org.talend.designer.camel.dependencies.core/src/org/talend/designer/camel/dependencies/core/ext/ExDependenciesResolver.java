package org.talend.designer.camel.dependencies.core.ext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;

public class ExDependenciesResolver {

	private static final String JAVA_LIB_DIRECTORY = "lib";
	private static final String JAVA_PROJECT_NAME = ".Java";
	private ProcessType pt;
	private EList<?> nodes;

	private Set<BundleClasspath> classpaths = new HashSet<BundleClasspath>();
	private Set<ImportPackage> packages = new HashSet<ImportPackage>();
	private Set<RequireBundle> bundles = new HashSet<RequireBundle>();

	public ExDependenciesResolver(ProcessType pt) {
		this.pt = pt;
		nodes = this.pt.getNode();
		initialize();
	}

	private void initialize() {
		Map<String, Set<ExBundleClasspath>> exClasspaths = ExtensionPointsReader.INSTANCE
				.getBundleClasspaths();
		Map<String, Set<ExImportPackage>> exImportPackages = ExtensionPointsReader.INSTANCE
				.getComponentImportPackages();
		Map<String, Set<ExRequireBundle>> exRequireBundles = ExtensionPointsReader.INSTANCE
				.getComponentRequireBundles();
		
		Set<ExRequireBundle> requireBundlesForAll = ExtensionPointsReader.INSTANCE.getRequireBundlesForAll();
		Set<ExImportPackage> importPackagesForAll = ExtensionPointsReader.INSTANCE.getImportPackagesForAll();
		
		for(ExRequireBundle rb: requireBundlesForAll){
			bundles.add(rb.toTargetIgnorePredicates());
		}
		
		for(ExImportPackage ip: importPackagesForAll){
			packages.add(ip.toTargetIgnorePredicates());
		}

		for (Object o : nodes) {
			if (!(o instanceof NodeType)) {
				continue;
			}
			NodeType n = (NodeType) o;
			String componentName = n.getComponentName();
			Set<ExBundleClasspath> bcs = exClasspaths.get(componentName);
			if (bcs != null) {
				for (ExBundleClasspath bc : bcs) {
					Set<BundleClasspath> targets = bc.toTargets(n);
					classpaths.addAll(targets);
				}
			}
			Set<ExImportPackage> ips = exImportPackages.get(componentName);
			if (ips != null) {
				for (ExImportPackage ip : ips) {
					Set<ImportPackage> targets = ip.toTargets(n);
					packages.addAll(targets);
				}
			}

			Set<ExRequireBundle> rbs = exRequireBundles.get(componentName);
			if (rbs != null) {
				for (ExRequireBundle rb : rbs) {
					Set<RequireBundle> targets = rb.toTargets(n);
					bundles.addAll(targets);
				}
			}
		}

		exClasspaths = null;

		exImportPackages = null;

		exRequireBundles = null;
	}

	public BundleClasspath[] getBundleClasspaths() {
		return classpaths.toArray(new BundleClasspath[0]);
	}

	public RequireBundle[] getRequireBundles() {
		return bundles.toArray(new RequireBundle[0]);
	}

	public ImportPackage[] getImportPackages() {
		return packages.toArray(new ImportPackage[0]);
	}

}
