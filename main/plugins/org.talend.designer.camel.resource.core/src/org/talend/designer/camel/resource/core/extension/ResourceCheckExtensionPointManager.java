// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.resource.core.extension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.designer.camel.resource.core.model.ResourceDependencyModel;
import org.talend.designer.camel.resource.core.util.RouteResourceUtil;
import org.talend.designer.core.model.components.EParameterName;

/**
 * 
 * @author xpli
 * 
 */
public class ResourceCheckExtensionPointManager {

	private static final String ENABLEMENT = "enablement";

	private static final String PARAMETER_NAME = "parameterName";

	private static final String RESOURCE_PARAMETER = "resourceParameter";

	private static final String RESOURCE_CHECK_EXT = "org.talend.designer.camel.resource.RouteResourceCheck"; //$NON-NLS-1$

	public static ResourceCheckExtensionPointManager INSTANCE = new ResourceCheckExtensionPointManager();

	private Map<String, Set<ExResourceParamModel>> extensionMap;

	private ResourceCheckExtensionPointManager() {
		initialization();
	}

	/**
	 * Load Extension point
	 */
	private void initialization() {
		extensionMap = new HashMap<String, Set<ExResourceParamModel>>();
		IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						RESOURCE_CHECK_EXT);
		if (configurationElements == null) {
			return;
		}
		for (IConfigurationElement element : configurationElements) {
			String componentName = element.getAttribute("componentName");
			Set<ExResourceParamModel> extParamModels = extensionMap
					.get(componentName);
			if (extParamModels == null) {
				extParamModels = new HashSet<ExResourceParamModel>();
				extensionMap.put(componentName, extParamModels);
			}
			IConfigurationElement[] resourceParameters = element
					.getChildren(RESOURCE_PARAMETER);
			if (resourceParameters != null) {
				for (IConfigurationElement rElement : resourceParameters) {
					String parameterName = rElement
							.getAttribute(PARAMETER_NAME);
					ExResourceParamModel exResourceParamModel = new ExResourceParamModel();
					exResourceParamModel.setParamName(parameterName);
					extParamModels.add(exResourceParamModel);
					IConfigurationElement[] enablements = rElement
							.getChildren(ENABLEMENT);
					if (enablements != null) {
						exResourceParamModel
								.setExpress(createEnableExpression(enablements));
					}

				}
			}
		}

	}

	/**
	 * Create expression for Model
	 */
	private ExExpressionModel createEnableExpression(
			IConfigurationElement[] enablements) {
		if (enablements.length == 0) {
			return null;
		}
		if (enablements.length > 1) {
			// XXX???

		}
		IConfigurationElement rootElement = enablements[0];
		IConfigurationElement[] children = rootElement.getChildren();
		if (children == null || children.length == 0) {
			return null;
		}
		if (children.length > 1) {
			// XXX???
		}
		rootElement = children[0];
		ExExpressionModel root = new ExExpressionModel();
		root.setOperator(rootElement.getName());
		hanldeChildrenElement(root, rootElement.getChildren());
		return root;
	}

	/**
	 * 
	 * @param model
	 * @param children
	 */
	private void hanldeChildrenElement(ExExpressionModel model,
			IConfigurationElement[] children) {
		if (children == null) {
			return;
		}
		IConfigurationElement firstChild = null;
		IConfigurationElement secondChild = null;
		switch (children.length) {
		case 0:
			// Ignore
			break;
		case 1:
			firstChild = children[0];
			break;
		case 2:
			firstChild = children[0];
			secondChild = children[1];
			break;

		default:
			break;
		}

		if (firstChild != null) {
			ExExpressionModel leftExpress = createExpressionModel(firstChild);
			if (leftExpress != null) {
				model.setLeftModel(leftExpress);
				hanldeChildrenElement(leftExpress, firstChild.getChildren());
			}
		}

		if (secondChild != null) {
			ExExpressionModel rightExpress = createExpressionModel(secondChild);
			if (rightExpress != null) {
				model.setRightModel(rightExpress);
				hanldeChildrenElement(rightExpress, secondChild.getChildren());
			}
		}

	}

	private ExExpressionModel createExpressionModel(
			IConfigurationElement element) {
		if (element == null) {
			return null;
		}
		if (element.getName().equals("attribute")) {
			ExAttributeExpressionModel attrModel = new ExAttributeExpressionModel();
			String parameterName = element.getAttribute("parameterName");
			String parameterValue = element.getAttribute("parameterValue");
			String regularExpresson = element.getAttribute("regularExpresson");
			attrModel.setName(parameterName);
			attrModel.setValue(parameterValue);
			attrModel.setRegex("true".equals(regularExpresson));
			return attrModel;
		} else {
			ExExpressionModel exExpressionModel = new ExExpressionModel();
			exExpressionModel.setOperator(element.getName());
			return exExpressionModel;
		}

	}

	public Set<ResourceDependencyModel> getResourceModel(INode node) {
		Set<ResourceDependencyModel> models = new HashSet<ResourceDependencyModel>();
		String name = node.getComponent().getName();
		Set<ExResourceParamModel> resourceParams = extensionMap.get(name);
		if (resourceParams == null) {
			return models;
		}
		for (ExResourceParamModel model : resourceParams) {
			boolean eualate = model.eualate(node);
			if (eualate) {
				String paramName = model.getParamName();
				ResourceDependencyModel denpModel = createDenpendencyModel(
						paramName, node);
				if(denpModel == null){
					continue;
				}
				boolean isContained = false;
				for (ResourceDependencyModel rdm : models) {
					if (rdm.equals(denpModel)) {
						rdm.getRefNodes().add(node.getUniqueName());
						isContained = true;
						break;
					}
				}
				if (!isContained) {
					denpModel.getRefNodes().add(node.getUniqueName());
					models.add(denpModel);
				}
			}
		}
		return models;
	}

	/**
	 * Create ResourceDependencyModel
	 * 
	 * @param paramName
	 * @param node
	 * @return
	 */
	private ResourceDependencyModel createDenpendencyModel(String paramName,
			INode node) {
		IElementParameter idParam = node.getElementParameter(paramName + ":"
				+ EParameterName.ROUTE_RESOURCE_TYPE_ID);
		IElementParameter versionParam = node.getElementParameter(paramName
				+ ":" + EParameterName.ROUTE_RESOURCE_TYPE_VERSION);
		Object idObj = idParam.getValue();
		Object versionObj = versionParam.getValue();

		return RouteResourceUtil.createDependency((String) idObj,
				(String) versionObj);
	}

}
