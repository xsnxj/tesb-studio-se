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
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.utils.JavaResourcesHelper;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.ui.editor.process.Process;
import org.talend.designer.publish.core.models.BundleModel;
import org.talend.designer.publish.core.models.FeaturesModel;
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
    protected static class XMLFeatureModel {

        private String name = "";

        private String version = "";

        public XMLFeatureModel() {
        }

        /**
         * @param name
         * @param version
         */
        public XMLFeatureModel(String name, String version) {
            this.name = name;
            this.version = version;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof XMLFeatureModel) {
                XMLFeatureModel model = (XMLFeatureModel) obj;
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

    /**
     * Bundle model for XML parse.
     * 
     * @author xpli
     * 
     */
    protected static class XMLBundleModel {

        private String version = "";

        private String symbolicName = "";

        private String groudId = "";

        /**
         * @return the groudId
         */
        public String getGroudId() {
            return groudId;
        }

        /**
         * @param groudId the groudId to set
         */
        public void setGroudId(String groudId) {
            this.groudId = groudId;
        }

        public XMLBundleModel() {

        }

        /**
         * @param version
         * @param symbolicName
         */
        public XMLBundleModel(String symbolicName, String groupId, String version) {
            this.version = version;
            this.groudId = groupId;
            this.symbolicName = symbolicName;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof XMLBundleModel) {
                return this.symbolicName.equals(((XMLBundleModel) obj).symbolicName)
                        && this.groudId.equals(((XMLBundleModel) obj).groudId);
            }
            return super.equals(obj);
        }

        /**
         * @param version the version to set
         */
        public void setVersion(String version) {
            this.version = version;
        }

        /**
         * @return the version
         */
        public String getVersion() {
            return version;
        }

        /**
         * @return the symbolicName
         */
        public String getSymbolicName() {
            return symbolicName;
        }

        /**
         * @param symbolicName the symbolicName to set
         */
        public void setSymbolicName(String symbolicName) {
            this.symbolicName = symbolicName;
        }

        @Override
        public int hashCode() {
            if (symbolicName != null && groudId != null) {
                return symbolicName.hashCode() * 31 + groudId.hashCode();
            }
            return super.hashCode();
        }
    }

    private static final String MAPPING_XML_FILE = "CamelFeatures.xml";

    private static final String CAMEL_VERSION_RANGE = "[2,5)";

    private static final String SPRING_VERSION_RANGE = "[3,5)";

    private static Map<String, Set<XMLFeatureModel>> camelFeaturesMap;

    private static Map<String, Set<XMLBundleModel>> camelBundlesMap;

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
    protected static boolean computeCheckElementValue(String paramName, EList<?> elementParameterTypes) {
        ElementParameterType cpType = findElementParameterByName(paramName, elementParameterTypes);
        if (cpType == null) {
            return false;
        }
        String isNone = cpType.getValue();
        return "true".equals(isNone);
    }

    /**
     * 
     * @param evtValue
     * @return
     */
    private static Set<XMLFeatureModel> computeFeature(String evtValue) {
        if (camelFeaturesMap == null || camelFeaturesMap.isEmpty()) {
            initMap();
        }
        Set<XMLFeatureModel> features = camelFeaturesMap.get(evtValue);
        return features;
    }

    /**
     * 
     * @param lib
     * @return
     */
    private static Set<XMLBundleModel> computeBundle(String lib) {
        if (camelBundlesMap == null || camelBundlesMap.isEmpty()) {
            initMap();
        }
        initMap();
        Set<XMLBundleModel> models = camelBundlesMap.get(lib);
        return models;
    }

    protected static ElementParameterType findElementParameterByName(String paramName, EList<?> elementParameterTypes) {
        for (Object obj : elementParameterTypes) {
            ElementParameterType cpType = (ElementParameterType) obj;
            if (paramName.equals(cpType.getName())) {
                return cpType;
            }
        }
        return null;
    }

    /**
     * Get bundle in feature.xml
     * 
     * @param node
     * @return
     */
    private static Set<XMLBundleModel> getBundlesOfRoute(Set<String> neededLibraries) {
        Set<XMLBundleModel> bundles = new HashSet<XMLBundleModel>();
        for (String lib : neededLibraries) {
            Set<XMLBundleModel> model = computeBundle(lib);
            if (model != null) {
                bundles.addAll(model);
            }
        }
        return bundles;
    }

    /**
     * 
     * @param node
     * @return
     */
    private static Set<XMLFeatureModel> getFeaturesOfRoute(Set<String> neededLibraries, Property property) {

        Set<XMLFeatureModel> features = new HashSet<XMLFeatureModel>();

        for (String lib : neededLibraries) {
            Set<XMLFeatureModel> featureModel = computeFeature(lib);
            if (featureModel != null) {
                features.addAll(featureModel);
            }
        }

        CamelProcessItem processItem = (CamelProcessItem) property.getItem();
        ProcessType processType = processItem.getProcess();

        features.add(new XMLFeatureModel("camel-spring", CAMEL_VERSION_RANGE));
        features.add(new XMLFeatureModel("camel-blueprint", CAMEL_VERSION_RANGE));
        features.add(new XMLFeatureModel("camel", CAMEL_VERSION_RANGE));
        features.add(new XMLFeatureModel("camel-core", CAMEL_VERSION_RANGE));
        features.add(new XMLFeatureModel("spring", SPRING_VERSION_RANGE));
        features.add(new XMLFeatureModel("spring-tx", SPRING_VERSION_RANGE));
        features.add(new XMLFeatureModel("talend-job-controller", "[5,6)"));

        for (Object o : processType.getNode()) {
            if (o instanceof NodeType) {
                NodeType currentNode = (NodeType) o;
                if ("cCXF".equals(currentNode.getComponentName())) {
                    boolean sam = computeCheckElementValue("ENABLE_SAM", currentNode.getElementParameter());
                    if (sam) {
                        features.add(new XMLFeatureModel("tesb-sam-common", "[2,10)"));
                        features.add(new XMLFeatureModel("tesb-sam-agent", "[2,10)"));
                    }

                    boolean sl = computeCheckElementValue("ENABLE_SL", currentNode.getElementParameter());
                    if (sl) {
                        // http://jira.talendforge.org/browse/TESB-5461
                        features.add(new XMLFeatureModel("tesb-zookeeper", "[2,10)"));
                    }
                }
            }
        }

        return features;
    }

    /**
	 * 
	 */
    private static void initMap() {
        camelFeaturesMap = new HashMap<String, Set<XMLFeatureModel>>();
        camelBundlesMap = new HashMap<String, Set<XMLBundleModel>>();

        XPathFactory xpFactory = XPathFactory.newInstance();
        XPath newXPath = xpFactory.newXPath();

        try {
            InputStream input = CamelFeatureUtil.class.getResourceAsStream(MAPPING_XML_FILE);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(input);

            try {
                NodeList list = (NodeList) newXPath.evaluate("//FeatureMaps", document, XPathConstants.NODESET);
                String camelVersion = list.item(0).getAttributes().getNamedItem("CamelVersion").getNodeValue();
                list = (NodeList) newXPath.evaluate("//FeatureMap/Feature", document, XPathConstants.NODESET);

                for (int index = 0; index < list.getLength(); index++) {

                    Node node = list.item(index);
                    String hotLib = node.getParentNode().getAttributes().getNamedItem("HotLib").getNodeValue();
                    // Use version properties
                    hotLib = hotLib.replace("$version$", camelVersion);
                    Set<XMLFeatureModel> features = camelFeaturesMap.get(hotLib);
                    if (features == null) {
                        features = new HashSet<XMLFeatureModel>();
                        camelFeaturesMap.put(hotLib, features);
                    }

                    String featureVersion = node.getAttributes().getNamedItem("version").getNodeValue();
                    String featureName = node.getFirstChild().getNodeValue();
                    features.add(new XMLFeatureModel(featureName, featureVersion));
                }
                list = (NodeList) newXPath.evaluate("//FeatureMap/Bundle", document, XPathConstants.NODESET);

                for (int index = 0; index < list.getLength(); index++) {

                    Node node = list.item(index);
                    String hotLib = node.getParentNode().getAttributes().getNamedItem("HotLib").getNodeValue();
                    hotLib = hotLib.replace("$version$", camelVersion);
                    Set<XMLBundleModel> bundles = camelBundlesMap.get(hotLib);
                    if (bundles == null) {
                        bundles = new HashSet<XMLBundleModel>();
                        camelBundlesMap.put(hotLib, bundles);
                    }

                    String version = node.getAttributes().getNamedItem("version").getNodeValue();
                    String groupId = node.getAttributes().getNamedItem("groupId").getNodeValue();
                    String name = node.getFirstChild().getNodeValue();
                    bundles.add(new XMLBundleModel(name, groupId, version));
                }
            } finally {
                input.close();
            }

        } catch (Exception e) {
            ExceptionHandler.process(e);
        }

    }

    /**
     * Add feature and bundle to Feature Model
     * 
     * @param node
     * @param featuresModel
     */
    public static void addFeatureAndBundles(RepositoryNode node, FeaturesModel featuresModel) {

        if (!checkNode(node)) {
            return;
        }

        Property property = node.getObject().getProperty();
        Process process = new org.talend.designer.core.ui.editor.process.Process(property);
        process.loadXmlFile();
        Set<String> neededLibraries = process.getNeededLibraries(true);
        Set<XMLFeatureModel> features = getFeaturesOfRoute(neededLibraries, property);
        Set<XMLBundleModel> bundles = getBundlesOfRoute(neededLibraries);

        for (XMLFeatureModel model : features) {
            featuresModel.addSubFeature(model.getName(), model.getVersion());
        }

        for (XMLBundleModel model : bundles) {
            BundleModel bundleModel = new BundleModel(model.getGroudId(), model.getSymbolicName(), model.getVersion());
            featuresModel.addSubBundle(bundleModel);
        }

        process.dispose();

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
            String itemName = item.getProperty().getDisplayName(); // .getLabel() ?

            return projectName + '.' + itemName;
        }
        return null;
    }
}