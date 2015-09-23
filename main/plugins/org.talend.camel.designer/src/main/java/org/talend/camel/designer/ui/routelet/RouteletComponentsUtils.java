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
package org.talend.camel.designer.ui.routelet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.talend.camel.core.model.camelProperties.RouteletProcessItem;
import org.talend.camel.designer.CamelDesignerPlugin;
import org.talend.camel.model.CamelRepositoryNodeType;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.runtime.model.emf.EmfHelper;
import org.talend.core.CorePlugin;
import org.talend.core.model.components.ComponentCategory;
import org.talend.core.model.components.IComponent;
import org.talend.core.model.components.IComponentsFactory;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.ui.component.ComponentsFactoryProvider;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;

public class RouteletComponentsUtils {

    private static final String ATTR_PID = "pluginId"; //$NON-NLS-1$
    private static final String ATTR_PID_VALUE = CamelDesignerPlugin.getDefault().getBundle().getSymbolicName();

    private static List<IComponent> routeletComponents = null;

    /**
     * DOC qzhang Comment method "loadComponentsFromJoblets".
     */
    public static void loadComponentsFromRoutelets() {
        loadComponentsFromRoutelets(null);
    }

    /**
     * DOC qzhang Comment method "loadComponentsFromJoblets".
     * 
     * @param compFac
     */
    public static void loadComponentsFromRoutelets(IComponentsFactory compFac) {
        IComponentsFactory components = compFac;
        if (components == null) {
            components = ComponentsFactoryProvider.getInstance();
        }
        Set<IComponent> componentsList = components.getComponents();
        if (routeletComponents == null) {
            routeletComponents = new ArrayList<IComponent>();
        } else {
            componentsList.removeAll(routeletComponents);
        }
        Map<String, IComponent> existComponents = new HashMap<String, IComponent>();

        for (IComponent component : componentsList) {
            existComponents.put(component.getName(), component);
        }

        try {
            IProxyRepositoryFactory factory = CorePlugin.getDefault().getRepositoryService().getProxyRepositoryFactory();
            final List<IRepositoryViewObject> list = new ArrayList<IRepositoryViewObject>(
                factory.getAll(CamelRepositoryNodeType.repositoryRouteletType));
            for (Project project : ProjectManager.getInstance().getAllReferencedProjects()) {
                list.addAll(factory.getAll(project, CamelRepositoryNodeType.repositoryRouteletType, false));
            }

            // bug 6158
            Set<String> existCNames = existComponents.keySet();
            for (IRepositoryViewObject repositoryObject : list) {
                Property property = repositoryObject.getProperty();
                boolean alreadyLoadedAndUpToDate = false;
                List<IComponent> componentsToUpdate = new ArrayList<IComponent>();
                for (IComponent existingJobletComponent : routeletComponents) {
                    if (existingJobletComponent.getName().equals(property.getLabel())) {
                        if (((RouteletComponent) existingJobletComponent).getLastUpdated()
                                .compareTo(property.getModificationDate()) >= 0) {
                            alreadyLoadedAndUpToDate = true;
                            existingJobletComponent.setPaletteType(ComponentCategory.CATEGORY_4_CAMEL.getName());
                            componentsList.add(existingJobletComponent);
                        } else {
                            componentsToUpdate.add(existingJobletComponent);
                        }
                        break;
                    }
                }
                if (alreadyLoadedAndUpToDate) {
                    continue;
                }
                routeletComponents.removeAll(componentsToUpdate);
                RouteletProcessItem jobletProcessItem = (RouteletProcessItem) property.getItem();
                EmfHelper.visitChilds(jobletProcessItem.getProcess());
                String name = property.getLabel();
                if (!existCNames.contains(name)) {
                    RouteletComponent comp = createRouteletComponent();
                    if (comp == null) {
                        return;
                    }
                    comp.setPaletteType(ComponentCategory.CATEGORY_4_CAMEL.getName());
                    comp.setProperty(property);
                    comp.setLastUpdated(property.getModificationDate());
                    if (!componentsList.contains(comp)) {
                        componentsList.add(comp);
                        // System.out.println("add or update joblet:" + comp.getName());
                        routeletComponents.add(comp);
                    }

                } else {
                    IComponent component = existComponents.get(name);
                    if (component instanceof RouteletComponent) {
                        RouteletComponent exJCom = (RouteletComponent) component;
                        if (exJCom != null) {
                            exJCom.setProperty(property);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ExceptionHandler.process(ex);
        }
    }

    private static RouteletComponent createRouteletComponent() throws CoreException {
        for (IConfigurationElement configElement :
            Platform.getExtensionRegistry().getConfigurationElementsFor(IComponentsFactory.COMPONENT_DEFINITION)) {
            if (ATTR_PID_VALUE.equals(configElement.getAttribute(ATTR_PID))) {
                final Object execObj = configElement.createExecutableExtension("class"); //$NON-NLS-1$
                if (execObj instanceof RouteletComponent) {
                    return (RouteletComponent) execObj;
                }
            }
        }
        return null;
    }

}
