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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.properties.ProcessItem;
import org.talend.designer.core.model.utils.emf.talendfile.ContextParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.publish.core.models.BundleModel;
import org.talend.designer.publish.core.models.FeaturesModel;
import org.talend.repository.model.RepositoryNode;

public class KarFileGenerator {

	public static boolean generateKarFile(String jarFile, String version,
			RepositoryNode node, String destination) throws IOException {

		String itemName = node.getObject().getProperty().getDisplayName();
		String projectName = node.getObject().getProjectLabel().toLowerCase();

		ZipOutputStream output = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(destination)));

		/*
		 * Bundle File path:
		 * repository/[projectName]/[itemName]/[itemName]-bundle
		 * /[itemVersion]/[itemName]-bundle-[itemVersion].jar
		 */
		StringBuilder sb = new StringBuilder();
		sb.append("repository/");
		sb.append(projectName);
		sb.append("/");
		sb.append(itemName);
		sb.append("/");
		sb.append(itemName);
		sb.append("-bundle/");
		sb.append(version);
		sb.append("/");
		sb.append(itemName);
		sb.append("-bundle-");
		sb.append(version);
		sb.append(".jar");

		File f = new File(jarFile);
		ZipEntry entry = new ZipEntry(sb.toString());
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

		/*
		 * feature file path:
		 * repository/[projectName]/[itemName]/[itemName]-feature
		 * /[itemVersion]/[itemName]-feature-[itemVersion]-feature.xml
		 */
		sb = new StringBuilder();
		sb.append("repository/");
		sb.append(projectName);
		sb.append("/");
		sb.append(itemName);
		sb.append("/");
		sb.append(itemName);
		sb.append("-feature");
		sb.append("/");
		sb.append(version);
		sb.append("/");
		sb.append(itemName);
		sb.append("-feature-");
		sb.append(version);
		sb.append("-feature.xml");

		String groupId = projectName + "." + itemName;
		BundleModel bundleModel = new BundleModel(f, groupId, itemName
				+ "-bundle",
				version);
		FeaturesModel featuresModel = new FeaturesModel(projectName + "."
				+ itemName, itemName, version);
		featuresModel.setContexts(getContextsMap(node));

		featuresModel.addSubBundle(bundleModel);

		String[][] subFeatures = getSubFeatures(node);
		for (String[] fm : subFeatures) {
			featuresModel.addSubFeature(fm[0], fm[1]);
		}

		byte[] featureContent = featuresModel.toString().getBytes();

		entry = new ZipEntry(sb.toString());
		entry.setSize(featureContent.length);
		entry.setTime(System.currentTimeMillis());
		output.putNextEntry(entry);
		output.write(featureContent);

		output.flush();
		output.close();
		return true;
	}

	private static Map<String, Map<String, String>> getContextsMap(
			RepositoryNode node) {
		Map<String, Map<String, String>> contextValues = new HashMap<String, Map<String, String>>();
		ProcessType process = ((ProcessItem) node.getObject().getProperty()
				.getItem()).getProcess();
		if (process != null) {
			EList context = process.getContext();
			if (context != null) {
				Iterator iterator = context.iterator();
				while (iterator.hasNext()) {
					Object next = iterator.next();
					if (!(next instanceof ContextType))
						continue;
					ContextType ct = (ContextType) next;
					String name = ct.getName();
					HashMap<String, String> contextParams = new HashMap<String, String>();
					contextValues.put(name, contextParams);
					EList<ContextParameterType> params = ct
							.getContextParameter();
					for (ContextParameterType param : params) {
						contextParams.put(param.getName(), param.getValue());
					}
				}
			}
		}
		return contextValues;
	}

	private static String[][] getSubFeatures(RepositoryNode node) {
		// Waiting for TESB-5329 and TESB-5375
		return new String[][] { { "camel-jetty", "[2,3)" } };
	}

}
