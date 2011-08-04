// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.spring.ui.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.talend.core.model.components.ComponentUtilities;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class COnExceptionrParameterHandler extends AbstractParameterHandler {

    public COnExceptionrParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public Map<String, List<String>> getTableParameters() {

        Map<String, List<String>> tableParams = new HashMap<String, List<String>>();
        List<String> columns = new ArrayList<String>();
        columns.add("EXCEPTION");
        tableParams.put("EXCEPTIONS", columns);

        return tableParams;
    }

    @Override
    public void handleAddtionalParam(NodeType nodeType, Entry<String, String> param) {

        Map<String, List<String>> tableParameters = getTableParameters();

        if (tableParameters.size() == 0) {
            return;
        }

        if (tableParameters.size() == 1) {

            for (Entry<String, List<String>> tableParam : tableParameters.entrySet()) {

                String key = param.getKey();
                String value = param.getValue();

                if (key.equals("EXCEPTIONS")) {
                    String[] exceptions = value.split(";");
                    List<ElementValueType> valueTypes = new ArrayList<ElementValueType>();
                    for (String ex : exceptions) {
                        ElementValueType valueType = fileFact.createElementValueType();
                        valueType.setElementRef(tableParam.getValue().get(0));
                        valueType.setValue(ex);
                        valueTypes.add(valueType);
                    }
                    ComponentUtilities.addNodeProperty(nodeType, tableParam.getKey(), "TABLE");
                    ComponentUtilities.setNodeProperty(nodeType, tableParam.getKey(), valueTypes);
                }
            }

        } else {
            // /FIXME
        }

    }
}
