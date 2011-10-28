// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.talend.core.model.components.IComponent;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.utils.IDragAndDropServiceHandler;
import org.talend.designer.core.model.components.EmfComponent;
import org.talend.designer.core.ui.editor.process.EDatabaseComponentName;
import org.talend.repository.model.ComponentsFactoryProvider;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC hwang class global comment. Detailled comment
 */
public class DragAndDropServiceHandler implements IDragAndDropServiceHandler {

    public boolean canHandle(Connection connection) {
        // if (connection instanceof Connection) {
        // return true;
        // }
        return false;
    }

    public String getComponentValue(Connection connection, String value) {
        return getSchemaValue(connection, value);
    }

    private String getSchemaValue(Connection connection, String value) {
        return "";
    }

    public List<IComponent> filterNeededComponents(Item item, RepositoryNode seletetedNode, ERepositoryObjectType type) {
        EDatabaseComponentName name = EDatabaseComponentName.getCorrespondingComponentName(item, type);
        String productNameWanted = name.getProductName();

        Set<IComponent> components = ComponentsFactoryProvider.getInstance().getComponents();
        List<IComponent> neededComponents = new ArrayList<IComponent>();

        EmfComponent emfComponent = null;
        for (IComponent component : components) {
            if (component instanceof EmfComponent) {
                emfComponent = (EmfComponent) component;
                String componentProductname = emfComponent.getRepositoryType();
                boolean value = true;
                if (type == ERepositoryObjectType.METADATA_CON_TABLE) {
                    if (emfComponent.getName().toUpperCase().endsWith("MAP")) {
                        value = false;
                    }
                }

                if (((componentProductname != null && productNameWanted.endsWith(componentProductname)) && value)) {
                    neededComponents.add(emfComponent);
                }
            }
        }
        return sortFilteredComponnents(item, seletetedNode, type, neededComponents);
    }

    private List<IComponent> sortFilteredComponnents(Item item, RepositoryNode seletetedNode, ERepositoryObjectType type,
            List<IComponent> neededComponents) {

        List<IComponent> normalTopComponents = new ArrayList<IComponent>();
        List<IComponent> specialTopComponents = new ArrayList<IComponent>();

        for (IComponent component : neededComponents) {
            String name = component.getName();
            if (name.contains("Output") || name.contains("Input")) {
                normalTopComponents.add(component);
            }
        }

        List<IComponent> sortedComponents = new ArrayList<IComponent>();
        sortedComponents.addAll(specialTopComponents);
        sortedComponents.addAll(normalTopComponents);

        // add the left components
        neededComponents.removeAll(specialTopComponents);
        neededComponents.removeAll(normalTopComponents);
        sortedComponents.addAll(neededComponents);

        return sortedComponents;
    }

}
