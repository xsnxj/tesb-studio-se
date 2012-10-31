package org.talend.camel.designer.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.emf.common.util.EList;
import org.talend.camel.designer.model.ExportKarBundleModel;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.Property;
import org.talend.designer.core.model.utils.emf.talendfile.ContextParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.publish.core.models.BundleModel;
import org.talend.designer.publish.core.models.FeaturesModel;
import org.talend.repository.model.RepositoryNode;

public class KarFileGenerator {

    public static boolean generateKarFile(Set<ExportKarBundleModel> bundleModels, RepositoryNode routerNode, String version,
            String destination) throws IOException {

        Property routeProperty = routerNode.getObject().getProperty();
        String itemName = routeProperty.getDisplayName();
        String projectName = routerNode.getObject().getProjectLabel().toLowerCase();

        ZipOutputStream output = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destination)));

        StringBuilder sb = new StringBuilder();
        sb.append("repository/").append(projectName).append("/").append(itemName).append("/");
        /*
         * Bundle File path: repository/[projectName]/[itemName]/[itemName]-bundle
         * /[itemVersion]/[itemName]-bundle-[itemVersion].jar
         */
        String groupPrefix = sb.toString();
        /*
         * feature file path: repository/[projectName]/[itemName]/[itemName]-feature
         * /[itemVersion]/[itemName]-feature-[itemVersion]-feature.xml
         */
        String featurePrefix = sb.append(itemName).append("-feature/").append(version).append("/").append(itemName)
                .append("-feature-").append(version).append("-feature.xml").toString();

        String groupId = CamelFeatureUtil.getMavenGroupId(routeProperty.getItem());
        FeaturesModel featuresModel = new FeaturesModel(groupId, itemName, version);

        for (ExportKarBundleModel p : bundleModels) {
            if (p == null || p.getBundleFilePath() == null) {
                continue;
            }
            File f = new File(p.getBundleFilePath());
            if (!f.exists()) {
                continue;
            }
            // add bundle jar file
            RepositoryNode repositoryNode = p.getRepositoryNode();
            String displayName = repositoryNode.getObject().getProperty().getDisplayName();
            if (repositoryNode.equals(routerNode)) {
                displayName += "-bundle";
            }
            ZipEntry entry = new ZipEntry(groupPrefix + displayName + "/" + p.getRepositoryVersion() + "/" + f.getName());
            entry.setSize(f.length());
            entry.setTime(f.lastModified());
            output.putNextEntry(entry);

            // write file content
            byte[] buf = new byte[1024];
            int readLen = 0;
            InputStream is = new BufferedInputStream(new FileInputStream(f));
            while ((readLen = is.read(buf)) != -1) {
                output.write(buf, 0, readLen);
            }
            is.close();

            // add bundle dependencies
            BundleModel bundleModel = new BundleModel(f, groupId, displayName, p.getRepositoryVersion());
            featuresModel.setContexts(getContextsMap(repositoryNode));

            featuresModel.addSubBundle(bundleModel);

            // http://jira.talendforge.org/browse/TESB-6311, add sub-features
            // and bundles, Xiaopeng Li
            CamelFeatureUtil.addFeatureAndBundles(routerNode, featuresModel);

        }

        byte[] featureContent = featuresModel.toString().getBytes();

        ZipEntry entry = new ZipEntry(featurePrefix);
        entry.setSize(featureContent.length);
        entry.setTime(System.currentTimeMillis());
        output.putNextEntry(entry);
        output.write(featureContent);

        output.flush();
        output.close();
        return true;
    }

    private static Map<String, Map<String, String>> getContextsMap(RepositoryNode node) {
        Map<String, Map<String, String>> contextValues = new HashMap<String, Map<String, String>>();
        ProcessType process = ((ProcessItem) node.getObject().getProperty().getItem()).getProcess();
        if (process != null) {
            EList context = process.getContext();
            if (context != null) {
                Iterator iterator = context.iterator();
                while (iterator.hasNext()) {
                    Object next = iterator.next();
                    if (!(next instanceof ContextType)) {
                        continue;
                    }
                    ContextType ct = (ContextType) next;
                    String name = ct.getName();
                    HashMap<String, String> contextParams = new HashMap<String, String>();
                    contextValues.put(name, contextParams);
                    EList<ContextParameterType> params = ct.getContextParameter();
                    for (ContextParameterType param : params) {
                        contextParams.put(param.getName(), param.getValue());
                    }
                }
            }
        }
        return contextValues;
    }

}
