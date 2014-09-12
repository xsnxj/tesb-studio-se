package org.talend.designer.camel.dependencies.core.ext;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.utils.JavaResourcesHelper;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.dependencies.core.Messages;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ExportPackage;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.utils.emf.talendfile.ConnectionType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExDependenciesResolver {

	private EList<?> nodes;

	private Set<BundleClasspath> classpaths = new HashSet<BundleClasspath>();
	private Set<ImportPackage> importPackages = new HashSet<ImportPackage>();
	private Set<RequireBundle> bundles = new HashSet<RequireBundle>();
	private Set<ExportPackage> exportPackages = new HashSet<ExportPackage>();
	private ProcessItem item;

	public ExDependenciesResolver(ProcessItem item) {
		this.item = item;
		nodes = this.item.getProcess().getNode();

		initialize();
	}

	private void initialize() {
		handleAllNodes();
		handleAllConnections();
	}

	/**
	 * most of the datas of a node are coming from extension point
	 * except the cTalendJob
	 */
	private void handleAllNodes() {
		Set<ExRequireBundle> requireBundlesForAll = ExtensionPointsReader.INSTANCE.getRequireBundlesForAll();
		Set<ExImportPackage> importPackagesForAll = ExtensionPointsReader.INSTANCE.getImportPackagesForAll();

		for(ExRequireBundle rb: requireBundlesForAll){
			RequireBundle target = rb.toTargetIgnorePredicates();
			target.setDescription(Messages.ExDependenciesResolver_commonRequireBundle);
			bundles.add(target);
		}

		for(ExImportPackage ip: importPackagesForAll){
			ImportPackage target = ip.toTargetIgnorePredicates();
			target.setDescription(Messages.ExDependenciesResolver_commonImportPackage);
			importPackages.add(target);
		}

		String projectFolderName = JavaResourcesHelper.getProjectFolderName(item);
		for (Object o : nodes) {
			if (!(o instanceof NodeType)) {
				continue;
			}
			NodeType n = (NodeType) o;
			if(!isActivate(n)){
				continue;
			}
			handleNode(n);

		}

		String version = item.getProperty().getVersion();
		if("Latest".equals(version)){ //$NON-NLS-1$
			try {
				version = ProxyRepositoryFactory.getInstance().getLastVersion(item.getProperty().getId()).getVersion();
			} catch (Exception e) {
			}
		}
		if (version!=null) {
	        String routePackageName = JavaResourcesHelper.getJobFolderName(item.getProperty().getLabel(), version);
			ExportPackage exportPackage = new ExportPackage();
			exportPackage.setName(projectFolderName+"."+routePackageName); //$NON-NLS-1$
			exportPackage.setBuiltIn(true);
			exportPackage.setDescription(Messages.ExDependenciesResolver_generatedPackage);
			exportPackages.add(exportPackage);
		}
	}

	private void handleNode(NodeType n) {
		Map<String, Set<ExBundleClasspath>> exClasspaths = ExtensionPointsReader.INSTANCE
				.getBundleClasspaths();
		Map<String, Set<ExImportPackage>> exImportPackages = ExtensionPointsReader.INSTANCE
				.getComponentImportPackages();
		Map<String, Set<ExRequireBundle>> exRequireBundles = ExtensionPointsReader.INSTANCE
				.getComponentRequireBundles();

		String uniqueName = ""; //$NON-NLS-1$
		for (Object obj : n.getElementParameter()) {
			ElementParameterType cpType = (ElementParameterType) obj;
			if ("UNIQUE_NAME".equals(cpType.getName())) { //$NON-NLS-1$
				uniqueName = cpType.getValue();
				break;
			}
		}

		String componentName = n.getComponentName();
		Set<ExBundleClasspath> bcs = exClasspaths.get(componentName);
		if (bcs != null) {
			for (ExBundleClasspath bc : bcs) {
				Set<BundleClasspath> targets = bc.toTargets(n);
				if(targets == null){
					continue;
				}
				BundleClasspath[] array = classpaths.toArray(new BundleClasspath[0]);
				for(BundleClasspath bcp: targets){
					boolean found = false;
					for(BundleClasspath obj :array){
						if(obj!=null && obj.equals(bcp)){
							obj.setChecked(obj.isChecked()
									|| bcp.isChecked());
							bcp = obj;
							found = true;
							break;
						}
					}
					if(bcp != null){
						bcp.addRelativeComponent(uniqueName);
					}
					if(!found){
						classpaths.add(bcp);
					}
				}
			}
		}
		Set<ExImportPackage> ips = exImportPackages.get(componentName);
		if (ips != null) {
			for (ExImportPackage ip : ips) {
				ImportPackage target = ip.toTargets(n);
				if(target == null){
					continue;
				}
				ImportPackage[] array = importPackages.toArray(new ImportPackage[0]);
				boolean found = false;
				for(ImportPackage obj :array){
					if(obj!=null && obj.equals(target)){
						target = obj;
						found = true;
						break;
					}
				}
				target.addRelativeComponent(uniqueName);
				if(!found){
					importPackages.add(target);
				}
			}
		}

		Set<ExRequireBundle> rbs = exRequireBundles.get(componentName);
		if (rbs != null) {
			for (ExRequireBundle rb : rbs) {
				RequireBundle target = rb.toTargets(n);
				if(target == null){
					continue;
				}
				RequireBundle[] array = bundles.toArray(new RequireBundle[0]);
				boolean found = false;
				for(RequireBundle obj :array){
					if(obj!=null && obj.equals(target)){
						target = obj;
						found = true;
						break;
					}
				}
				target.addRelativeComponent(uniqueName);
				if(!found){
					bundles.add(target);
				}
			}
		}
	}

    private boolean isActivate(NodeType node) {
        for (Object obj : node.getElementParameter()) {
            ElementParameterType cpType = (ElementParameterType) obj;
            if ("ACTIVATE".equals(cpType.getName())) {
                return Boolean.parseBoolean(cpType.getValue());
            }
        }
        return true;
    }

	/**
	 * special for ROUTE_WHEN connection case
	 * we need to handle it specially according the selected language
	 */
	private void handleAllConnections() {
		List<?> connections = item.getProcess().getConnection();
		Iterator<?> iterator = connections.iterator();
		Map<String, Set<ExImportPackage>> languageImportPackages = ExtensionPointsReader.INSTANCE.getLanguageImportPackages();
		while(iterator.hasNext()){
			Object next = iterator.next();
			if(next == null || !(next instanceof ConnectionType)){
				continue;
			}
			ConnectionType connection = (ConnectionType) next;
			String connectorName = connection.getConnectorName();
			if(!EConnectionType.ROUTE_WHEN.getName().equals(connectorName)){
				continue;
			}
			String languageName = handleROUTEWHENconnection(connection);
			if(languageName == null){
				continue;
			}
			Set<ExImportPackage> languageImportSet = languageImportPackages.get(languageName);
			if(languageImportSet == null){
				continue;
			}
			for(ExImportPackage eip: languageImportSet){
				importPackages.add(eip.toTargetIgnorePredicates());
			}
		}


	}

	private String handleROUTEWHENconnection(ConnectionType connection) {
		List<?> elementParameter = connection.getElementParameter();
		Iterator<?> iterator = elementParameter.iterator();
		while(iterator.hasNext()){
			Object next = iterator.next();
			if(next == null || !(next instanceof ElementParameterType)){
				continue;
			}
			ElementParameterType ept = (ElementParameterType) next;
			if(!EParameterName.ROUTETYPE.getName().equals(ept.getName())){
				continue;
			}
			String value = ept.getValue();
			if(value==null){
				continue;
			}
			return value;
		}
		return null;
	}

	public BundleClasspath[] getBundleClasspaths() {
		return classpaths.toArray(new BundleClasspath[0]);
	}

	public RequireBundle[] getRequireBundles() {
		return bundles.toArray(new RequireBundle[0]);
	}

	public ImportPackage[] getImportPackages() {
		return importPackages.toArray(new ImportPackage[0]);
	}

	public ExportPackage[] getExportPackages() {
		return exportPackages.toArray(new ExportPackage[0]);
	}

}
