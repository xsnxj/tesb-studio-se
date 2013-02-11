// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.publish.core.models;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FeatureModel extends BaseModel {

	public static final String NAME_SUFFIX = "-feature";

    private static final String JOB_CONTROLLER_FEATURE = "talend-job-controller";

    private static final String JOB_CONTROLLER_VERSION = "[5,6)";

	private final String name;

	private String configName;

	private Collection<String> subFeatures = new HashSet<String>();

	private Collection<BundleModel> subBundles = new HashSet<BundleModel>();

	private String[] contextList = new String[] { "Default" };
	private Map<String, Map<String, String>> contexts = new HashMap<String, Map<String, String>>();

	public FeatureModel(String groupId, String namePrefix, String version) {
		super(groupId, namePrefix + NAME_SUFFIX, version);
		name = namePrefix;
        // <feature version='[5,6)'>talend-job-controller</feature>
		addSubFeature(JOB_CONTROLLER_FEATURE, JOB_CONTROLLER_VERSION);
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public void addSubFeature(String name, String version) {
		StringBuilder sb = new StringBuilder();
		sb.append("<feature version='");
		sb.append(version);
		sb.append("\'>");
		sb.append(name);
		sb.append("</feature>");
		subFeatures.add(sb.toString());
	}

	public void addBundle(BundleModel model) {
		subBundles.add(model);
	}

	public Collection<BundleModel> getBundles() {
		return subBundles;
	}

	private void setContextList(String[] contextList) {
		if (contextList != null) {
			this.contextList = contextList;
		}
	}
	
	public void setContexts(Map<String, Map<String, String>> contexts) {
		Collection<String> contextNames = new HashSet<String>(Arrays.asList(this.contextList));
		contextNames.addAll(contexts.keySet());
		setContextList((String[]) contextNames.toArray(new String[0]));
		for (Map.Entry<String, Map<String, String>> context : contexts.entrySet()) {
			String contextName = context.getKey();
			if (this.contexts.containsKey(contextName)) {
				this.contexts.get(contextName).putAll(context.getValue());
			} else {
				this.contexts.put(contextName, context.getValue());
			}
		}
		this.contexts = contexts;
	}

	private static String toBundleString(BundleModel model) {
		StringBuilder sb = new StringBuilder();
		sb.append("<bundle>mvn:");
		sb.append(model.getGroupId());
		sb.append('/');
		sb.append(model.getArtifactId());
		sb.append('/');
		sb.append(model.getVersion());
		sb.append("</bundle>");
		return sb.toString();
	}

	public String getContent() {
		StringBuilder sb = new StringBuilder();
		// add headers
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<features name=\"").append(getArtifactId()).append("\" xmlns=\"http://karaf.apache.org/xmlns/features/v1.0.0\">\n");
		sb.append("\t<feature name=\"");
		sb.append(getArtifactId());
		sb.append("\" version=\"");
		sb.append(getVersion());
		sb.append("\">\n");
		// add sub features
		for (String s : subFeatures) {
			sb.append("\t\t");
			sb.append(s);
			sb.append("\n");
		}

		// add sub bundles
		for (BundleModel s : subBundles) {
			sb.append("\t\t");
			sb.append(toBundleString(s));
			sb.append("\n");
		}

		if (null == contexts || contexts.isEmpty()) {
			// add config
			sb.append("\t\t<config name=\"");
			sb.append(configName);
			sb.append("\">\n");
			sb.append("\t\t\ttalendcontext=\"");
			for (int i = 0; i < contextList.length; i++) {
				if (i != 0) {
					sb.append(',');
				}
				sb.append(contextList[i]);
			}
			sb.append("\"\n");
			sb.append("\t\t</config>\n");
		} else {
			// add contexts config
			for (Map.Entry<String, Map<String, String>> context : contexts.entrySet()) {
				sb.append("\t\t<config name=\"");
				sb.append(name).append(".talendcontext.").append(context.getKey());
				sb.append("\">\n");
				for (Map.Entry<String, String> property : context.getValue().entrySet()) {
					sb.append("\t\t\t");
					sb.append(property.getKey());
					sb.append('=');
					sb.append(property.getValue());
					sb.append("\n");
				}
				sb.append("\t\t</config>\n");
			}
		}
		
		sb.append("\t</feature>\n");
		sb.append("</features>");

		return sb.toString();
	}

//	public static void main(String[] args) {
//		FeatureModel featureModel = new FeatureModel("aaa",
//				"CustomService", "1.0.0");
//		featureModel.addBundle(new BundleModel("talend", "job-control-bundle", "1.0", null));
//		featureModel.addBundle(new BundleModel("talend", "ProviderJob", "1.0", null));
//		featureModel.addBundle(new BundleModel("talend", "ESBProvider2", "1.0", null));
//		featureModel.addSubFeature("talend-job-controller", "5.0-SNAPSHOT");
//		featureModel.setConfigName("aa.bb");
//		featureModel
//				.setContextList(new String[] { "Default", "Product", "Dev" });
//		System.out.println(featureModel.getContent());
//	}

}
