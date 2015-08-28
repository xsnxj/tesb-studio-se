package org.talend.designer.camel.dependencies.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.relationship.RelationshipItemBuilder;
import org.talend.core.model.utils.JavaResourcesHelper;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.dependencies.core.ext.ExBundleClasspath;
import org.talend.designer.camel.dependencies.core.ext.ExImportPackage;
import org.talend.designer.camel.dependencies.core.ext.ExRequireBundle;
import org.talend.designer.camel.dependencies.core.ext.ExtensionPointsReader;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ExportPackage;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.ManifestItem;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;
import org.talend.designer.camel.dependencies.core.util.DependenciesCoreUtil;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.utils.emf.talendfile.ConnectionType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class DependenciesResolver {

    /**
     * builtIn items will be sorted automatically and can't be re-sorted
     * Non-BuiltIn items no sort and can be re-sorted
     * @author liugang
     *
     */
    private static final Comparator<ManifestItem> SORTER = new Comparator<ManifestItem>() {
        @Override
        public int compare(ManifestItem e1, ManifestItem e2) {
              return e1.toString().compareTo(e2.toString());
        }
    };

    private Collection<ImportPackage> importPackages = new TreeSet<ImportPackage>(SORTER);
    private Collection<RequireBundle> requireBundles = new TreeSet<RequireBundle>(SORTER);
    private Collection<BundleClasspath> bundleClasspaths = new TreeSet<BundleClasspath>(SORTER);
    private final Collection<ExportPackage> exportPackages = new ArrayList<ExportPackage>();

    private Collection<BundleClasspath> userBundleClasspaths;

	public DependenciesResolver(final ProcessItem item) {
        for (ExRequireBundle rb: ExtensionPointsReader.INSTANCE.getRequireBundlesForAll()) {
            RequireBundle target = rb.toTargets(null);
            target.setDescription(Messages.ExDependenciesResolver_commonRequireBundle);
            requireBundles.add(target);
        }

        for (ExImportPackage ip: ExtensionPointsReader.INSTANCE.getImportPackagesForAll()) {
            for (ImportPackage importPackage : ip.toTargets(null)) {
                importPackage.setDescription(Messages.ExDependenciesResolver_commonImportPackage);
                importPackages.add(importPackage);
            }
        }

        final Map<?, ?> additionProperties = item.getProperty().getAdditionalProperties().map();
        userBundleClasspaths = DependenciesCoreUtil.getStoredBundleClasspaths(additionProperties);

        handleAllNodes(item.getProcess().getNode());
        handleAllConnections(item.getProcess().getConnection());

        Collection<ImportPackage> customImportPackages = new ArrayList<ImportPackage>(importPackages);
        customImportPackages.addAll(DependenciesCoreUtil.getStoredImportPackages(additionProperties));
        importPackages = customImportPackages;

        Collection<RequireBundle> customRequireBundles = new ArrayList<RequireBundle>(requireBundles);
        customRequireBundles.addAll(DependenciesCoreUtil.getStoredRequireBundles(additionProperties));
        requireBundles = customRequireBundles;

        String version = item.getProperty().getVersion();
        if (RelationshipItemBuilder.LATEST_VERSION.equals(version)) {
            try {
                version = ProxyRepositoryFactory.getInstance().getLastVersion(item.getProperty().getId()).getVersion();
            } catch (Exception e) {
            }
        }
        String routePackageName = JavaResourcesHelper.getJobFolderName(item.getProperty().getLabel(), version);
        final ExportPackage exportPackage = new ExportPackage();
        exportPackage.setName(JavaResourcesHelper.getProjectFolderName(item) + '.' + routePackageName);
        exportPackage.setBuiltIn(true);
        exportPackage.setDescription(Messages.ExDependenciesResolver_generatedPackage);
        exportPackages.add(exportPackage);
        exportPackages.addAll(DependenciesCoreUtil.getStoredExportPackages(additionProperties));
	}

    /**
     * most of the datas of a node are coming from extension point except the
     * cTalendJob
     */
    private void handleAllNodes(Collection<?> nodes) {
        for (Object o : nodes) {
            if (!(o instanceof NodeType)) {
                continue;
            }
            NodeType n = (NodeType) o;
            if (!isActivate(n)) {
                continue;
            }
            handleNode(n);
        }
    }

    private void handleNode(NodeType n) {
        final String componentName = n.getComponentName();
        final String uniqueName = ElementParameterParser.getUNIQUENAME(n);

        final Collection<ExImportPackage> ips =
            ExtensionPointsReader.INSTANCE.getImportPackagesForComponent(componentName);
        if (ips != null) {
            for (ExImportPackage ip : ips) {
                Collection<ImportPackage> targets = ip.toTargets(n);
                if (targets == null) {
                    continue;
                }
                for (ImportPackage importPackage : targets) {
                    if (!importPackages.add(importPackage)) {
                        for (ImportPackage obj : importPackages) {
                            if (importPackage.equals(obj)) {
                                importPackage = obj;
                                break;
                            }
                        }
                    }
                    importPackage.addRelativeComponent(uniqueName);
                }
            }
        }

        final Collection<ExRequireBundle> rbs =
            ExtensionPointsReader.INSTANCE.getRequireBundlesForComponent(componentName);
        if (rbs != null) {
            for (ExRequireBundle rb : rbs) {
                RequireBundle target = rb.toTargets(n);
                if (target == null) {
                    continue;
                }
                if (!requireBundles.add(target)) {
                    for (RequireBundle obj : requireBundles) {
                        if (target.equals(obj)) {
                            target = obj;
                            break;
                        }
                    }
                }
                target.addRelativeComponent(uniqueName);
            }
        }

        final Collection<ExBundleClasspath> bcs =
            ExtensionPointsReader.INSTANCE.getBundleClasspathsForComponent(componentName);
        if (bcs != null) {
            for (ExBundleClasspath bc : bcs) {
                Collection<BundleClasspath> targets = bc.toTargets(n);
                if (targets == null) {
                    continue;
                }
                for (BundleClasspath bcp : targets) {
                    if (!bundleClasspaths.add(bcp)) {
                        for (BundleClasspath obj : bundleClasspaths){
                            if (bcp.equals(obj)) {
                                bcp = obj;
                                break;
                            }
                        }
                    }
                    bcp.addRelativeComponent(uniqueName);
                    if (!bcp.isBuiltIn()) {
                        bcp.setOptional(!userBundleClasspaths.contains(bcp));
                    }
                }
            }
        }
    }

    private static boolean isActivate(NodeType node) {
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
	private void handleAllConnections(Collection<?> connections) {
		for (Object next : connections) {
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
	        Collection<ExImportPackage> languageImportPackages =
	            ExtensionPointsReader.INSTANCE.getImportPackagesForComponent(languageName);
			if(languageImportPackages == null){
				continue;
			}
			for(ExImportPackage eip : languageImportPackages){
				importPackages.addAll(eip.toTargets(null));
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

    public Collection<BundleClasspath> getBundleClasspaths() {
        return bundleClasspaths;
    }

    public Collection<RequireBundle> getRequireBundles() {
        return requireBundles;
    }

    public Collection<ImportPackage> getImportPackages() {
        return importPackages;
    }

    public Collection<ExportPackage> getExportPackages() {
        return exportPackages;
    }

    public String getManifestBundleClasspath(char separator) {
        return DependenciesCoreUtil.toManifestString(bundleClasspaths, separator, true);
    }

    public String getManifestRequireBundle(char separator) {
        return DependenciesCoreUtil.toManifestString(requireBundles, separator, true);
    }

    public String getManifestImportPackage(char separator) {
        return DependenciesCoreUtil.toManifestString(importPackages, separator, true);
    }

    public String getManifestExportPackage(char separator) {
        return DependenciesCoreUtil.toManifestString(exportPackages, separator, true);
    }
}
