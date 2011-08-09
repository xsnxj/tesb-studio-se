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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.talend.core.model.components.ComponentUtilities;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.camel.spring.ui.CamelSpringUIPlugin;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public abstract class AbstractParameterHandler implements IParameterHandler {

    private static final org.apache.commons.logging.Log log = LogFactory.getLog(AbstractParameterHandler.class);
    
    private String componentName;

    protected TalendFileFactory fileFact;

    private HashMap<String, String> map;

    private static final String PROPERTY_FOLDER = "mappings";

    private static final String PROPERTY_POSTFIX = ".properties";
    
    public AbstractParameterHandler(String componentName) {
        this.componentName = componentName;
        fileFact = TalendFileFactory.eINSTANCE;
    }

    protected Map<String, String> getBasicParameters() {
        
        if(map != null){
            return map;
        }
        
        map = new HashMap<String, String>();

        try {
            Path propPath = new Path(File.separator + PROPERTY_FOLDER + File.separator + componentName + PROPERTY_POSTFIX);
            URL resolve = FileLocator.resolve(FileLocator.find(CamelSpringUIPlugin.getDefault().getBundle(), propPath,
                    Collections.EMPTY_MAP));
            InputStream input = new FileInputStream(resolve.getFile());
            Properties prop = new Properties();
            prop.load(input);
            for (Entry<Object, Object> entry : prop.entrySet()) {
                map.put(entry.getKey().toString(), entry.getValue().toString());
            }
            input.close();
        } catch (IOException e) {
            return Collections.emptyMap();
        }
        return map;
    }

    protected Map<String, String> getAddtionalParameters() {
        return Collections.emptyMap();
    }

    protected Map<String, List<String>> getTableParameters() {
        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {

        log.info("node:" + uniqueName + " params: " +parameters);
        
        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();
       
        Map<String, String> params = getBasicParameters();

        for (Entry<String, String> param : parameters.entrySet()) {

            ElementParameterType paramType;


            String key = param.getKey();
            String value = param.getValue();
            
            if (key.equals(ICamelSpringConstants.UNIQUE_NAME_ID)) {// Add UNIQUE_NAME parameter
                paramType = fileFact.createElementParameterType();
                paramType.setField("TEXT");
                paramType.setName("UNIQUE_NAME");
                paramType.setValue(uniqueName);
                elemParams.add(paramType);
                continue;
            }

            String field = params.get(key + FIELD_POSTFIX);
            String name = params.get(key + NAME_POSTFIX);
            String ref = params.get(key + REF_POSTFIX);
            
            if(ref != null){ //Handle reference check
                paramType = fileFact.createElementParameterType();
                paramType.setField("CHECK");
                paramType.setName(ref);
                paramType.setValue("true");
                elemParams.add(paramType);
            }
            
            if (field != null && name != null) { // Basic parameters
                paramType = fileFact.createElementParameterType();
                paramType.setField(field);
                paramType.setName(name);
                paramType.setValue(value);
                elemParams.add(paramType);
                continue;
            } else {
                handleAddtionalParam(nodeType, param);
            }
            
           
        }

        nodeType.getElementParameter().addAll(elemParams);
    }

    /**
     * Getter for componentName.
     * 
     * @return the componentName
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * 
     */
    public void handleAddtionalParam(NodeType nodeType, Entry<String, String> param) {
        Map<String, List<String>> tableParameters = getTableParameters();

        if (tableParameters.size() == 0) {
            return;
        }

        if (tableParameters.size() == 1) {

            for (Entry<String, List<String>> tableParam : tableParameters.entrySet()) {

                String key = param.getKey();
                String value = param.getValue();

                List<ElementValueType> valueTypes = new ArrayList<ElementValueType>();
                ElementValueType valueType = fileFact.createElementValueType();
                valueType.setElementRef(tableParam.getValue().get(0));

                valueType.setValue(key);
                valueTypes.add(valueType);

                valueType = fileFact.createElementValueType();
                valueType.setElementRef(tableParam.getValue().get(1));
                valueType.setValue(value);
                valueTypes.add(valueType);

                ElementParameterType nodeProperty = ComponentUtilities.getNodeProperty(nodeType, tableParam.getKey());
                if(nodeProperty == null){
                    ComponentUtilities.addNodeProperty(nodeType, tableParam.getKey(), "TABLE");
                  ComponentUtilities.setNodeProperty(nodeType, tableParam.getKey(), valueTypes);
                }else{
                    nodeProperty.getElementValue().addAll(valueTypes);
                }
            }

        } else {
            ///FIXME
        }

    }

}
