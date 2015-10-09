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
package org.talend.designer.camel.resource.core.extension;

import java.util.Collection;
import java.util.Collections;
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

    private static final String RESOURCE_CHECK_EXT = "org.talend.designer.camel.resource.RouteResourceCheck"; //$NON-NLS-1$

    private static final String RESOURCE_PARAMETER = "resourceParameter"; //$NON-NLS-1$

    private static final String PARAMETER_NAME = "parameterName"; //$NON-NLS-1$

    public static ResourceCheckExtensionPointManager INSTANCE = new ResourceCheckExtensionPointManager();

    private Map<String, Set<ExResourceParamModel>> extensionMap;

    private ResourceCheckExtensionPointManager() {
    }

	public Collection<ResourceDependencyModel> getResourceModel(INode node) {
        init();
        final Collection<ExResourceParamModel> resourceParams = extensionMap.get(node.getComponent().getName());
        if (resourceParams == null) {
            return Collections.emptyList();
        }
        final Collection<ResourceDependencyModel> models = new HashSet<ResourceDependencyModel>();
        for (ExResourceParamModel model : resourceParams) {
            if (model.eualate(node)) {
                final ResourceDependencyModel denpModel = createDenpendencyModel(model.getParamName(), node);
                if (denpModel != null) {
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
    private static ResourceDependencyModel createDenpendencyModel(String paramName, final INode node) {
        final IElementParameter idParam =
            node.getElementParameter(paramName);
        final IElementParameter versionParam =
            node.getElementParameter(paramName + ':' + EParameterName.ROUTE_RESOURCE_TYPE_VERSION);
        final ResourceDependencyModel model =
            RouteResourceUtil.createDependency((String) idParam.getValue(), (String)  versionParam.getValue());
        if (null != model) {
            model.setBuiltIn(true);
            model.getRefNodes().add(node.getUniqueName());
        }
        return model;
    }

    /**
     * Load Extension point
     */
    private void init() {
        if (null != extensionMap) {
            return;
        }
        extensionMap = new HashMap<String, Set<ExResourceParamModel>>();
        IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(
            RESOURCE_CHECK_EXT);
        if (configurationElements == null) {
            return;
        }
        for (IConfigurationElement element : configurationElements) {
            final String componentName = element.getAttribute("componentName");
            Set<ExResourceParamModel> extParamModels = extensionMap.get(componentName);
            if (extParamModels == null) {
                extParamModels = new HashSet<ExResourceParamModel>();
                extensionMap.put(componentName, extParamModels);
            }
            final IConfigurationElement[] resourceParameters = element.getChildren(RESOURCE_PARAMETER);
            if (resourceParameters != null) {
                for (IConfigurationElement rElement : resourceParameters) {
                    final ExResourceParamModel exResourceParamModel = new ExResourceParamModel(
                        rElement.getAttribute(PARAMETER_NAME));
                    extParamModels.add(exResourceParamModel);
                }
            }
        }
    }

}
