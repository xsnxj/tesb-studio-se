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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.eclipse.emf.common.util.EList;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.properties.Property;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.repository.model.RepositoryNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
				return model.getName().equals(this.name)
						&& model.getVersion().equals(this.version);
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

	private static final String C_MESSAGINGE_NDPOINT = "cMessagingEndpoint";

	private static final String C_CONFIG = "cConfig";

	private static final String MAPPING_XML_FILE = "CamelFeatures.xml";

	private static final String CAMEL_VERSION_RANGE = "[2, 5)";

	private static final String SPRING_VERSION_RANGE = "[3, 5)";

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
	 * @param currentNode2
	 * @return
	 */
	private static Collection<? extends FeatureModel> getFeatureByAdvancedlibs(
			NodeType currentNode) {

		Set<FeatureModel> features = new HashSet<FeatureModel>();

		if (C_MESSAGINGE_NDPOINT.equals(currentNode.getComponentName())
				|| C_CONFIG.equals(currentNode.getComponentName())) {
			for (Object e : currentNode.getElementParameter()) {
				ElementParameterType p = (ElementParameterType) e;
				if ("HOTLIBS".equals(p.getName())) {
					EList elementValue = p.getElementValue();
					for (Object pv : elementValue) {
						ElementValueType evt = (ElementValueType) pv;
						String evtValue = evt.getValue();
						Set<FeatureModel> featureModel = computeFeature(evtValue);
						if (featureModel != null) {
							features.addAll(featureModel);
						}
					}
				}
			}
		}

		return features;
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
		CamelProcessItem processItem = (CamelProcessItem) property.getItem();
		ProcessType process = processItem.getProcess();

		features.add(new FeatureModel("camel-spring", CAMEL_VERSION_RANGE));
		features.add(new FeatureModel("camel-blueprint", CAMEL_VERSION_RANGE));
		features.add(new FeatureModel("camel", CAMEL_VERSION_RANGE));
		features.add(new FeatureModel("camel-core", CAMEL_VERSION_RANGE));
		features.add(new FeatureModel("spring", SPRING_VERSION_RANGE));
		features.add(new FeatureModel("spring-tx", SPRING_VERSION_RANGE));
		// features.add(new FeatureModel("spring-web", SPRING_VERSION_RANGE));
		features.add(new FeatureModel("talend-job-controller", "[5,6)"));

		for (Object o : process.getNode()) {
			if (o instanceof NodeType) {
				NodeType currentNode = (NodeType) o;
				if ("cCXF".equals(currentNode.getComponentName())) {

					features.add(new FeatureModel("camel-cxf",
							CAMEL_VERSION_RANGE));
					features.add(new FeatureModel("cxf", "[2,10)"));
					// features.add(new FeatureModel("cxf-specs", "[2,10)"));
					// features.add(new FeatureModel("cxf-jaxb", "[2,10)"));
					// features.add(new FeatureModel("cxf-abdera", "[2,10)"));

					boolean sl = computeCheckElementValue("ENABLE_SL",
							currentNode.getElementParameter());
					if (sl) {
						features.add(new FeatureModel("tesb-zookeeper",
								"[2,10)"));
					}

					boolean sam = computeCheckElementValue("ENABLE_SAM",
							currentNode.getElementParameter());
					if (sam) {
						features.add(new FeatureModel("tesb-sam-common",
								"[2,10)"));
						features.add(new FeatureModel("tesb-sam-agent",
								"[2,10)"));
					}

				} else if ("cFtp".equals(currentNode.getComponentName())) {
					features.add(new FeatureModel("camel-ftp",
							CAMEL_VERSION_RANGE));

				} else if ("cJMS".equals(currentNode.getComponentName())) {
					features.add(new FeatureModel("camel-jms",
							CAMEL_VERSION_RANGE));
					features.add(new FeatureModel("spring-jms",
							SPRING_VERSION_RANGE));
				} else if ("cMail".equals(currentNode.getComponentName())) {
					features.add(new FeatureModel("camel-mail",
							CAMEL_VERSION_RANGE));
				} else if ("cHttp".equals(currentNode.getComponentName())) {
					features.add(new FeatureModel("camel-http",
							CAMEL_VERSION_RANGE));
					features.add(new FeatureModel("http", "[2,10)"));
				} else if ("cTalendJob".equals(currentNode.getComponentName())) {
					features.add(new FeatureModel("camel-talendjob ",
							CAMEL_VERSION_RANGE));
				} else if (C_MESSAGINGE_NDPOINT.equals(currentNode
						.getComponentName())
						|| C_CONFIG.equals(currentNode.getComponentName())) {
					features.addAll(getFeatureByAdvancedlibs(currentNode));
				}
			}
		}

		return features;
	}

	private static void initMap() {
		camelFeaturesMap = new HashMap<String, Set<FeatureModel>>();

		XPathFactory xpFactory = XPathFactory.newInstance();
		XPath newXPath = xpFactory.newXPath();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			InputStream input = CamelFeatureUtil.class
					.getResourceAsStream(MAPPING_XML_FILE);
			Document doc = documentBuilder.parse(input);
			NodeList list = (NodeList) newXPath.evaluate(
					"//FeatureMap/Feature",
					doc,
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
				camelFeaturesMap.put(hotLib, features);
			}
		} catch (Exception e) {
			ExceptionHandler.process(e);
		}


	}

}