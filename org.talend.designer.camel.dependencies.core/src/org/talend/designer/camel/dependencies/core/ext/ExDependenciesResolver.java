package org.talend.designer.camel.dependencies.core.ext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.utils.JavaResourcesHelper;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExDependenciesResolver {

	private EList<?> nodes;

	private Set<BundleClasspath> classpaths = new HashSet<BundleClasspath>();
	private Set<ImportPackage> packages = new HashSet<ImportPackage>();
	private Set<RequireBundle> bundles = new HashSet<RequireBundle>();
	private ProcessItem item;

	public ExDependenciesResolver(ProcessItem item) {
		this.item = item;
		nodes = this.item.getProcess().getNode();
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

		String projectFolderName = JavaResourcesHelper.getProjectFolderName(item);
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
			
			if("cTalendJob".equals(componentName)){
				String jobId = null;
				String jobVersion = null;
				String jobName = null;
				EList<?> parameters = n.getElementParameter();
				for (Object p : parameters) {
					if (!(p instanceof ElementParameterType)) {
						continue;
					}
					ElementParameterType ept = (ElementParameterType) p;
					String eptName = ept.getName();
					System.out.println(eptName);
					if ("FROM_EXTERNAL_JAR".equals(eptName)
							&& "true".equals(ept.getValue())) {
						jobName = null;
						break ;
					}
					if (jobId == null && "SELECTED_JOB_NAME:PROCESS_TYPE_PROCESS".equals(eptName)) {
						jobId = ept.getValue();
					}
					if (jobVersion == null && "SELECTED_JOB_NAME:PROCESS_TYPE_VERSION".equals(eptName)) {
						jobVersion = ept.getValue();
					}
					if(jobName==null && "SELECTED_JOB_NAME".equals(eptName)){
						jobName = ept.getValue();
					}
				}

				if("Latest".equals(jobVersion)&& jobId!=null){
					try {
						jobVersion = ProxyRepositoryFactory.getInstance().getLastVersion(jobId).getVersion();
					} catch (Exception e) {
						jobVersion = null;
					}
				}else{
					jobVersion = null;
				}
				if (jobName!=null && jobVersion != null) {
			        String jobFolderName = JavaResourcesHelper.getJobFolderName(jobName, jobVersion);
					ImportPackage importPackage = new ImportPackage();
					importPackage.setBuiltIn(true);
					importPackage.setName(projectFolderName+"."+jobFolderName);
					packages.add(importPackage);
				}
			}
		}

		exClasspaths = null;

		exImportPackages = null;

		exRequireBundles = null;

		// Add Route Resource Import packages
		// http://jira.talendforge.org/browse/TESB-6227
		addRouteResourcePackages();
	}

	/**
	 * Add route resource packages.
	 */
	private void addRouteResourcePackages() {
		EMap additionalProperties = item.getProperty()
				.getAdditionalProperties();
		if (additionalProperties == null) {
			return;
		}
		Object resourcesObj = additionalProperties.get("ROUTE_RESOURCES_PROP");
		if (resourcesObj == null) {
			return;
		}

		String[] resourceIds = resourcesObj.toString().split(",");
		for (String id : resourceIds) {
			try {
				IRepositoryViewObject rvo = ProxyRepositoryFactory
						.getInstance().getLastVersion(id);
				if (rvo != null) {
					ImportPackage importPackage = new ImportPackage();
					importPackage.setBuiltIn(true);

					Item item = rvo.getProperty().getItem();
					String path = item.getState().getPath();
					if (path != null && !path.isEmpty()) {
						importPackage.setName("route_resources."
								+ path.replace("/", "."));
					} else {
						importPackage.setName("route_resources");
					}
					packages.add(importPackage);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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
