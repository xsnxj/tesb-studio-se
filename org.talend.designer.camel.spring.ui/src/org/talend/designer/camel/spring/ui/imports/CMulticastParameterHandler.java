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
package org.talend.designer.camel.spring.ui.imports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.talend.core.model.components.ComponentUtilities;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class CMulticastParameterHandler extends AbstractParameterHandler {

    public CMulticastParameterHandler(String componentName) {
        super(componentName);
    }

    @Override
    public Map<String, List<String>> getTableParameters() {

        Map<String, List<String>> tableParams = new HashMap<String, List<String>>();
        List<String> columns = new ArrayList<String>();
        columns.add("URI");
        tableParams.put("URIS", columns);

        return tableParams;
    }

    @Override
    public void handleAddtionalParam(NodeType nodeType, Entry<String, String> param) {

        Map<String, List<String>> tableParameters = getTableParameters();

        if (tableParameters.size() == 1) {

            for (Entry<String, List<String>> tableParam : tableParameters.entrySet()) {
                String key = param.getKey();
                String value = param.getValue();

                if (key.equals(ICamelSpringConstants.ML_DESTINATIONS)) {
                    String[] values = value.split(";");
                    List<ElementValueType> valueTypes = new ArrayList<ElementValueType>();
                    for (String ex : values) {
                        ex = quotes(ex);
                        ElementValueType valueType = fileFact.createElementValueType();
                        valueType.setElementRef(tableParam.getValue().get(0));
                        valueType.setValue(ex);
                        valueTypes.add(valueType);
                    }
                    ElementParameterType nodeProperty = ComponentUtilities.getNodeProperty(nodeType, tableParam.getKey());
                    if(nodeProperty == null){
                        ComponentUtilities.addNodeProperty(nodeType, tableParam.getKey(), "TABLE");
                      ComponentUtilities.setNodeProperty(nodeType, tableParam.getKey(), valueTypes);
                    }else{
                        nodeProperty.getElementValue().addAll(valueTypes);
                    }
                }
            }

        } else {
            // /FIXME
        }

    }
}
