// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.eclipse.emf.common.util.EList;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.properties.Property;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.ui.editor.process.Process;
import org.talend.repository.model.RepositoryNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Camel component feature
 * 
 * http://jira.talendforge.org/browse/TESB-5375
 * 
 * @author LiXiaopeng
 * 
 */
public final class CamelFeatureUtil {

	/**
	 * Inner model
	 * 
	 * @author LiXiaopeng
	 * 
	 */
	protected static class FeatureModel {

		private String name = "";

		private String version = "";

		public FeatureModel() {
		}

		/**
		 * @param name
		 * @param version
		 */
		public FeatureModel(String name, String version) {
			this.name = name;
			this.version = version;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null && obj instanceof FeatureModel) {
				FeatureModel model = (FeatureModel) obj;
				return model.getName().equals(this.name);
				// && model.getVersion().equals(this.version); // fix the equals
				// method, only compare the name, else, camel-spring will be
				// added more times.
			}
			return super.equals(obj);
		}

		public String getName() {
			return name;
		}

		public String getVersion() {
			return version;
		}

		@Override
		public int hashCode() {
			if (name != null) {
				return name.hashCode();
			}
			return super.hashCode();
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		@Override
		public String toString() {
			return name + ", " + version;
		}
	}

	private static final String MAPPING_XML_FILE = "CamelFeatures.xml";

	private static final String CAMEL_VERSION_RANGE = "[2,5)";

	private static final String SPRING_VERSION_RANGE = "[3,5)";

	private static Map<String, Set<FeatureModel>> camelFeaturesMap;

	/**
	 * Check the node is Route
	 * 
	 * @param node
	 * @return
	 */
	private static boolean checkNode(RepositoryNode node) {

		if (node == null) {
			return false;
		}

		if (node.getObjectType() != CamelRepositoryNodeType.repositoryRoutesType) {
			return false;
		}

		return true;
	}

	/**
	 * Compute check field parameter value with a given parameter name
	 * 
	 * @param paramName
	 * @param elementParameterTypes
	 * @return
	 */
	protected static boolean computeCheckElementValue(String paramName,
			EList<?> elementParameterTypes) {
		ElementParameterType cpType = findElementParameterByName(paramName,
				elementParameterTypes);
		if (cpType == null) {
			return false;
		}
		String isNone = cpType.getValue();
		return "true".equals(isNone);
	}

	private static Set<FeatureModel> computeFeature(String evtValue) {
		if (camelFeaturesMap == null || camelFeaturesMap.isEmpty()) {
			initMap();
		}
		Set<FeatureModel> features = camelFeaturesMap.get(evtValue);
		return features;
	}

	protected static ElementParameterType findElementParameterByName(
			String paramName, EList<?> elementParameterTypes) {
		for (Object obj : elementParameterTypes) {
			ElementParameterType cpType = (ElementParameterType) obj;
			if (paramName.equals(cpType.getName())) {
				return cpType;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	public static String[][] getFeaturesByNode(RepositoryNode node) {

		Set<FeatureModel> features = new HashSet<FeatureModel>();

		features.addAll(getFeaturesOfRoute(node));

		String[][] featureStrs = new String[features.size()][2];

		int index = 0;
		for (FeatureModel feature : features) {
			featureStrs[index][0] = feature.getName();
			featureStrs[index][1] = feature.getVersion();
			index++;
		}

		return featureStrs;
	}

	private static Collection<? extends FeatureModel> getFeaturesOfRoute(
			RepositoryNode node) {

		Set<FeatureModel> features = new HashSet<FeatureModel>();
		if (!checkNode(node)) {
			return features;
		}

		Property property = (Property) node.getObject().getProperty();
		Process process = new org.talend.designer.core.ui.editor.process.Process(
				property);
		process.loadXmlFile();
		Set<String> neededLibraries = process.getNeededLibraries(true);
		for (String lib : neededLibraries) {
			Set<FeatureModel> featureModel = computeFeature(lib);
			if (featureModel != null) {
				features.addAll(featureModel);
			}
		}

		CamelProcessItem processItem = (CamelProcessItem) property.getItem();
		ProcessType processType = processItem.getProcess();

		features.add(new FeatureModel("camel-spring", CAMEL_VERSION_RANGE));
		features.add(new FeatureModel("camel-blueprint", CAMEL_VERSION_RANGE));
		features.add(new FeatureModel("camel", CAMEL_VERSION_RANGE));
		features.add(new FeatureModel("camel-core", CAMEL_VERSION_RANGE));
		features.add(new FeatureModel("spring", SPRING_VERSION_RANGE));
		features.add(new FeatureModel("spring-tx", SPRING_VERSION_RANGE));
		// features.add(new FeatureModel("spring-web", SPRING_VERSION_RANGE));
		features.add(new FeatureModel("talend-job-controller", "[5,6)"));

		for (Object o : processType.getNode()) {
			if (o instanceof NodeType) {
				NodeType currentNode = (NodeType) o;
				if ("cCXF".equals(currentNode.getComponentName())) {
					boolean sam = computeCheckElementValue("ENABLE_SAM",
							currentNode.getElementParameter());
					if (sam) {
						features.add(new FeatureModel("tesb-sam-common",
								"[2,10)"));
						features.add(new FeatureModel("tesb-sam-agent",
								"[2,10)"));
					}

					boolean sl = computeCheckElementValue("ENABLE_SL",
							currentNode.getElementParameter());
					if (sl) {
						// http://jira.talendforge.org/browse/TESB-5461
						features.add(new FeatureModel("tesb-zookeeper",
								"[2,10)"));
					}
				}
			}
		}

		process.dispose();

		return features;
	}

	private static void initMap() {
		camelFeaturesMap = new HashMap<String, Set<FeatureModel>>();

		XPathFactory xpFactory = XPathFactory.newInstance();
		XPath newXPath = xpFactory.newXPath();

		try {
			InputStream input = CamelFeatureUtil.class
					.getResourceAsStream(MAPPING_XML_FILE);
			try {

				NodeList list = (NodeList) newXPath.evaluate(
						"//FeatureMap/Feature", new InputSource(input),
						XPathConstants.NODESET);

				for (int index = 0; index < list.getLength(); index++) {

					Node node = list.item(index);
					String hotLib = node.getParentNode().getAttributes()
							.getNamedItem("HotLib").getNodeValue();
					Set<FeatureModel> features = camelFeaturesMap.get(hotLib);
					if (features == null) {
						features = new HashSet<FeatureModel>();
						camelFeaturesMap.put(hotLib, features);
					}

					String featureVersion = node.getAttributes()
							.getNamedItem("version").getNodeValue();
					String featureName = node.getFirstChild().getNodeValue();
					features.add(new FeatureModel(featureName, featureVersion));
					// camelFeaturesMap.put(hotLib, features);
				}
			} finally {
				input.close();
			}

		} catch (Exception e) {
			ExceptionHandler.process(e);
		}

	}

}