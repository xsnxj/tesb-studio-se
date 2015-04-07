// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.utils.JavaResourcesHelper;
import org.talend.designer.core.IDesignerCoreService;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.utils.emf.talendfile.ConnectionType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.publish.core.models.FeatureModel;
import org.talend.designer.publish.core.models.FeaturesModel;
import org.talend.repository.RepositoryPlugin;
import org.talend.repository.utils.EmfModelUtils;

/**
 * Camel component feature
 * 
 * http://jira.talendforge.org/browse/TESB-5375
 * 
 * @author LiXiaopeng
 * 
 */
public final class CamelFeatureUtil {

	private static final FeatureModel FEATURE_CAMEL_GROOVY = new FeatureModel("camel-groovy"); //$NON-NLS-1$
	private static final FeatureModel FEATURE_CAMEL_SCRIPT_JAVASCRIPT = new FeatureModel("camel-script-javascript"); //$NON-NLS-1$
	private static final FeatureModel FEATURE_CAMEL_SCRIPT = new FeatureModel("camel-script"); //$NON-NLS-1$

	private static final FeatureModel FEATURE_ACTIVEMQ_OPTIONAL = new FeatureModel("activemq-http"); //$NON-NLS-1$

	private static final FeatureModel FEATURE_ESB_SAM = new FeatureModel("tesb-sam-agent"); //$NON-NLS-1$
	private static final FeatureModel FEATURE_ESB_LOCATOR = new FeatureModel("tesb-locator-client"); //$NON-NLS-1$

	private static final Map<String, FeatureModel[]> camelFeaturesMap = new HashMap<String, FeatureModel[]>() {{
            put("camel-cxf", new FeatureModel[] { new FeatureModel("camel-cxf"), new FeatureModel("cxf") });
            put("camel-http", new FeatureModel[] { new FeatureModel("camel-http"), new FeatureModel("http") });
            put("camel-jms", new FeatureModel[] { new FeatureModel("camel-jms"), new FeatureModel("spring-jms") });
            put("activemq-all", new FeatureModel[] { new FeatureModel("activemq"), new FeatureModel("activemq-camel") });
            put("tdm-camel", new FeatureModel[] { new FeatureModel("talend-data-mapper") });
            //put("camel-talendjob", new FeatureModel[] { new FeatureModel("camel-talendjob") });
            put("camel-cxf-transport", new FeatureModel[] { });
            put("camel-jetty-common", new FeatureModel[] { });
            put("camel-jetty8", new FeatureModel[] { });
	}};

//	private static final Map<String, Collection<BundleModel>> camelBundlesMap =
//			new HashMap<String, Collection<BundleModel>>();


	private static final String JAVA_SCRIPT = "javaScript"; //$NON-NLS-1$

	private static final String LANGUAGES = "LANGUAGES"; //$NON-NLS-1$
	private static final String LOOP_TYPE = "LOOP_TYPE"; //$NON-NLS-1$


    private static Collection<FeatureModel> computeFeature(String libraryName) {
        String nameWithoutVersion = getNameWithoutVersion(libraryName);
        FeatureModel[] features = camelFeaturesMap.get(nameWithoutVersion);
        if (null == features && nameWithoutVersion.startsWith("camel-")) { //$NON-NLS-1$
            features = new FeatureModel[] { new FeatureModel(
                nameWithoutVersion.endsWith("-alldep") //$NON-NLS-1$
                ? nameWithoutVersion.substring(0, nameWithoutVersion.length() - "-alldep".length()) //$NON-NLS-1$
                : nameWithoutVersion) };
        }
        return features != null ? Arrays.asList(features) : null;
    }

	/**
	 * 
	 * @param libraryName
	 * @return
	 */
//	private static Collection<BundleModel> computeBundle(String libraryName) {
//		if (camelBundlesMap.isEmpty()) {
//			initMap();
//		}
//		String nameWithoutVersion = getNameWithoutVersion(libraryName);
//		return camelBundlesMap.get(nameWithoutVersion);
//	}

	private static String getNameWithoutVersion(String libraryName){
		if(libraryName == null || libraryName.isEmpty() || !libraryName.endsWith(".jar")){
			return libraryName;
		}
		String interName = libraryName;
		int lastIndexOf = interName.lastIndexOf('-');
		while(lastIndexOf != -1){
			try{
				Integer.parseInt(Character.toString(interName.charAt(lastIndexOf+1)));
				interName = interName.substring(0, lastIndexOf);
				break;
			}catch(Exception e){
				interName = interName.substring(0, lastIndexOf);
				lastIndexOf = interName.lastIndexOf('-');
			}
		}
		return interName;
	}

	/**
	 * Get bundle in feature.xml
	 * 
	 * @param node
	 * @return
	 */
//	private static Collection<BundleModel> getBundlesOfRoute(
//			Collection<String> neededLibraries) {
//		Collection<BundleModel> bundles = new HashSet<BundleModel>();
//		for (String lib : neededLibraries) {
//			Collection<BundleModel> model = computeBundle(lib);
//			if (model != null) {
//				bundles.addAll(model);
//			}
//		}
//		return bundles;
//	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	private static Collection<FeatureModel> getFeaturesOfRoute(
			Collection<String> neededLibraries, ProcessType processType) {

		Collection<FeatureModel> features = new HashSet<FeatureModel>();
		for (String lib : neededLibraries) {
			Collection<FeatureModel> featureModel = computeFeature(lib);
			if (featureModel != null) {
				features.addAll(featureModel);
			}
		}

		addProcessSpecialFeatures(features, processType);

		return features;
	}

	private static void addProcessSpecialFeatures(Collection<FeatureModel> features,
			ProcessType processType) {
		addNodesSpecialFeatures(features, processType);
		addConnectionsSpecialFeatures(features, processType);
	}

	private static void addNodesSpecialFeatures(Collection<FeatureModel> features,
			ProcessType processType) {
		for (Object o : processType.getNode()) {
			if (o instanceof NodeType) {
				NodeType currentNode = (NodeType) o;
				if (!EmfModelUtils.isComponentActive(currentNode)) {
				    continue;
				}
				String componentName = currentNode.getComponentName();
				if ("cCXF".equals(componentName) || "cCXFRS".equals(componentName)) {
					handleCXFcase(features, currentNode);
				} else if("cLoop".equals(componentName)){
					handleLoopCase(features, currentNode);
				} else if("cMessageFilter".equals(componentName)){
					handleMessageFilterCase(features, currentNode);
				} else if("cRecipientList".equals(componentName)){
					handleRecipientListCase(features, currentNode);
				} else if("cSetBody".equals(componentName)){
					handleSetBodyCase(features, currentNode);
				} else if("cSetHeader".equals(componentName)){
					handleSetHeaderCase(features, currentNode);
				} else if("cMQConnectionFactory".equals(componentName)){
					handleMQConnectionFactory(features, currentNode);
				}
			}
		}
	}

	private static void handleMQConnectionFactory(
			Collection<FeatureModel> features, NodeType currentNode) {
		ElementParameterType mqType = EmfModelUtils.findElementParameterByName("MQ_TYPE", currentNode);
		if("ActiveMQ".equals(mqType.getValue())){
			if (EmfModelUtils.computeCheckElementValue("IS_AMQ_HTTP_BROKER", currentNode)) {
				features.add(FEATURE_ACTIVEMQ_OPTIONAL);
			}
		}
	}

	private static void addConnectionsSpecialFeatures(
			Collection<FeatureModel> features, ProcessType processType) {
		EList connections = processType.getConnection();
		Iterator iterator = connections.iterator();
		while(iterator.hasNext()){
			Object next = iterator.next();
			if(!(next instanceof ConnectionType)){
				continue;
			}
			ConnectionType con = (ConnectionType) next;
			if(!EConnectionType.ROUTE_WHEN.getName().equals(con.getConnectorName())){
				continue;
			}
			EList elementParameters = con.getElementParameter();
			Iterator paraIter = elementParameters.iterator();
			while(paraIter.hasNext()){
				Object paraNext = paraIter.next();
				if(!(paraNext instanceof ElementParameterType)){
					continue;
				}
				ElementParameterType ept = (ElementParameterType) paraNext;
				if(!EParameterName.ROUTETYPE.getName().equals(ept.getName())){
					continue;
				}
//	            String[] strList = { "constant", "el", "groovy", "header", "javaScript", "jxpath", "mvel", "ognl", "php", "property",
//	                    "python", "ruby", "simple", "spel", "sql", "xpath", "xquery" };
				if("groovy".equals(ept.getValue())){
					features.add(FEATURE_CAMEL_GROOVY);
				} else if ("javaScript".equals(ept.getValue())) {
					features.add(FEATURE_CAMEL_SCRIPT);
					features.add(FEATURE_CAMEL_SCRIPT_JAVASCRIPT);
				}
			}
		}
	}
	
	protected static void handleSetHeaderCase(Collection<FeatureModel> features,
			NodeType currentNode) {
		ElementParameterType element = EmfModelUtils.findElementParameterByName("VALUES", currentNode);
		EList elementValue = element.getElementValue();
		Iterator iterator = elementValue.iterator();
		while(iterator.hasNext()){
			Object next = iterator.next();
			if(!(next instanceof ElementValueType)){
				continue;
			}
			ElementValueType evt = (ElementValueType) next;
			String elementRef = evt.getElementRef();
			if("LANGUAGE".equals(elementRef) && JAVA_SCRIPT.equals(evt.getValue())){
				features.add(FEATURE_CAMEL_SCRIPT_JAVASCRIPT);
				break;
			}
		}
		
	}

	protected static void handleSetBodyCase(Collection<FeatureModel> features,
			NodeType currentNode) {
		ElementParameterType languages = EmfModelUtils.findElementParameterByName(LANGUAGES, currentNode);
		if (!JAVA_SCRIPT.equals(languages.getValue())){
			return;
		}
		features.add(FEATURE_CAMEL_SCRIPT_JAVASCRIPT);
	}

	private static void handleRecipientListCase(Collection<FeatureModel> features,
			NodeType currentNode) {
		ElementParameterType languages = EmfModelUtils.findElementParameterByName(LANGUAGES, currentNode);
		if (!JAVA_SCRIPT.equals(languages.getValue())){
			return;
		}
		features.add(FEATURE_CAMEL_SCRIPT_JAVASCRIPT);
	}

	protected static void handleMessageFilterCase(Collection<FeatureModel> features,
			NodeType currentNode) {
		ElementParameterType languages = EmfModelUtils.findElementParameterByName(LANGUAGES, currentNode);
		if (!JAVA_SCRIPT.equals(languages.getValue())){
			return;
		}
		features.add(FEATURE_CAMEL_SCRIPT_JAVASCRIPT);
	}

	protected static void handleLoopCase(Collection<FeatureModel> features,
			NodeType currentNode) {
		ElementParameterType found = EmfModelUtils.findElementParameterByName(LOOP_TYPE, currentNode);
		if (!"EXPRESSION_TYPE".equals(found.getValue())) {
			return;
		}
		found = EmfModelUtils.findElementParameterByName(LANGUAGES, currentNode);
		if (!JAVA_SCRIPT.equals(found.getValue())) {
			return;
		}
		features.add(FEATURE_CAMEL_SCRIPT_JAVASCRIPT);
	}

	protected static void handleCXFcase(Collection<FeatureModel> features,
			NodeType currentNode) {
		if (EmfModelUtils.computeCheckElementValue("ENABLE_SAM", currentNode)) {
			features.add(FEATURE_ESB_SAM);
		}
		if (EmfModelUtils.computeCheckElementValue("ENABLE_SL", currentNode)) {
			// http://jira.talendforge.org/browse/TESB-5461
			features.add(FEATURE_ESB_LOCATOR);
		}
	}

	/**
	 * Add feature and bundle to Feature Model
	 * 
	 * @param node
	 * @param featuresModel
	 */
	public static void addFeatureAndBundles(ProcessItem routeProcess, FeaturesModel featuresModel) {
        IDesignerCoreService designerService = RepositoryPlugin.getDefault().getDesignerCoreService();
        IProcess process = designerService.getProcessFromProcessItem(routeProcess, false);
		Collection<String> neededLibraries = process.getNeededLibraries(true);

		Collection<FeatureModel> features = getFeaturesOfRoute(neededLibraries, routeProcess.getProcess());
		for (FeatureModel model : features) {
			featuresModel.addFeature(model);
		}

//		Collection<BundleModel> bundles = getBundlesOfRoute(neededLibraries);
//		for (BundleModel model : bundles) {
//			featuresModel.addBundle(model);
//		}
	}

	/**
	 * 
	 * DOC ggu Comment method "getMavenGroupId".
	 * 
	 * @param item
	 * @return
	 */
	public static String getMavenGroupId(Item item) {
		if (item != null) {
			String projectName = JavaResourcesHelper.getProjectFolderName(item);
			String itemName = item.getProperty().getDisplayName(); // .getLabel()
																	// ?

			return projectName + '.' + itemName;
		}
		return null;
	}
}
