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
package org.talend.designer.camel.spring.ui.imports;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.talend.core.model.components.ComponentUtilities;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.camel.spring.ui.SpringUIConstants;
import org.talend.designer.camel.spring.ui.utils.ParameterValueUtils;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public abstract class AbstractParameterHandler implements IParameterHandler {

    public static final String FIELD_CHECK = SpringUIConstants.FIELD_CHECK;

    public static final String FIELD_TEXT = SpringUIConstants.FIELD_TEXT;

    public static final String FIELD_RADIO = SpringUIConstants.FIELD_RADIO;

    public static final String FIELD_DIRECTORY = SpringUIConstants.FIELD_DIRECTORY;

    public static final String FIELD_CLOSED_LIST = SpringUIConstants.FIELD_CLOSED_LIST;

    public static final String FIELD_TABLE = SpringUIConstants.FIELD_TABLE;

    public static final String VALUE_TRUE = SpringUIConstants.VALUE_TRUE;

    public static final String VALUE_FALSE = SpringUIConstants.VALUE_FALSE;

    /**
     * Component name, and also the properties file name.
     */
    private String componentName;

    protected TalendFileFactory fileFact;

    /**
     * Basic parameter configurations in properties files under mappings folder.
     */
    private Properties basicParameters;

    public AbstractParameterHandler(String componentName) {
        this.componentName = componentName;
        fileFact = TalendFileFactory.eINSTANCE;
    }

    /**
     * 
     * Create adn add a parameter of a node.
     * 
     * @param elemParams
     * @param field
     * @param name
     * @param value
     */
    protected void addParamType(List<ElementParameterType> elemParams, String field, String name, String value) {
        ElementParameterType paramType = createParamType(field, name, value);
        elemParams.add(paramType);
    }

    /**
     * 
     * Create a parameter of a node.
     * 
     * @param elemParams
     * @param field
     * @param name
     * @param value
     */
    protected ElementParameterType createParamType(String field, String name, String value) {
        ElementParameterType paramType = fileFact.createElementParameterType();
        paramType.setField(field);
        paramType.setName(name);
        paramType.setValue(value);
        return paramType;
    }

    /**
     * 
     * Reserved parameter configurations.
     * 
     * @return
     */
    protected Map<String, String> getAddtionalParameters() {
        return Collections.emptyMap();
    }

    protected Properties getBasicParameters() {
        if (basicParameters != null) {
            return basicParameters;
        }

        try {
            basicParameters = new Properties();
            String propFile = SpringUIConstants.PROPERTY_FOLDER + "/" + componentName + SpringUIConstants.PROPERTY_POSTFIX;
            InputStream inputStream = SpringUIConstants.class.getResourceAsStream(propFile);
            basicParameters.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            basicParameters = null;
            return basicParameters;
        }
        return basicParameters;
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
     * Table field parameter configurations, such as CFile's ADVARGUMENTS
     * 
     * @return
     */
    protected Map<String, List<String>> getTableParameters() {
        return Collections.emptyMap();
    }

    /**
     * For regular parameters described in mapping properties files, using key-value to find the corresponding FIELD and
     * NAME or possible REF_CHECK parameter element to set.
     */
    public void handle(NodeType nodeType, String uniqueName, Map<String, String> parameters) {

        List<ElementParameterType> elemParams = new ArrayList<ElementParameterType>();
        Properties params = getBasicParameters();
        if (params == null) {// If no mapping properties file found, do nothing.
            return;
        }

        for (Entry<String, String> param : parameters.entrySet()) {

            String key = param.getKey();
            String value = param.getValue();

            if (key.equals(ICamelSpringConstants.UNIQUE_NAME_ID)) {// Add UNIQUE_NAME parameter
                addParamType(elemParams, FIELD_TEXT, "UNIQUE_NAME", uniqueName);
            } else {
                String field = params.getProperty(key + SpringUIConstants.FIELD_POSTFIX);
                String name = params.getProperty(key + SpringUIConstants.NAME_POSTFIX);
                String ref = params.getProperty(key + SpringUIConstants.REF_POSTFIX);

                if (ref != null) { // Handle reference check
                    addParamType(elemParams, FIELD_CHECK, ref, VALUE_TRUE);
                }

                if (field != null && name != null) { // Basic parameters
                    if (field.equals(FIELD_CHECK) || field.equals(FIELD_CLOSED_LIST)) {
                        value = unquotes(value);
                    }
                    addParamType(elemParams, field, name, value);
                } else {
                    handleAddtionalParam(nodeType, param);
                }
            }
        }

        nodeType.getElementParameter().addAll(elemParams);
    }

    /**
     * Handle additional parameters, currently only TABLE FIELD are supported, and only one TABLE parameter is supported
     * for a component. This method only for table with two columns, and if different, subclass may override it.
     */
    protected void handleAddtionalParam(NodeType nodeType, Entry<String, String> param) {
        Map<String, List<String>> tableParameters = getTableParameters();

        if (tableParameters.size() == 1) {

            for (Entry<String, List<String>> tableParam : tableParameters.entrySet()) {

                String key = param.getKey();
                String value = param.getValue();

                String tableKey = tableParam.getKey();
                List<String> tableValue = tableParam.getValue();

                List<ElementValueType> valueTypes = new ArrayList<ElementValueType>();

                ElementValueType valueType = fileFact.createElementValueType();
                valueType.setElementRef(tableValue.get(0));
                valueType.setValue(key);
                valueTypes.add(valueType);

                valueType = fileFact.createElementValueType();
                valueType.setElementRef(tableValue.get(1));
                valueType.setValue(value);
                valueTypes.add(valueType);

                ElementParameterType nodeProperty = ComponentUtilities.getNodeProperty(nodeType, tableKey);
                if (nodeProperty == null) {
                    ComponentUtilities.addNodeProperty(nodeType, tableKey, FIELD_TABLE);
                    ComponentUtilities.setNodeProperty(nodeType, tableKey, valueTypes);
                } else {
                    nodeProperty.getElementValue().addAll(valueTypes);
                }
            }

        } else {
            // /Currently don't support more than one table paremeters.
        }

    }

    /**
     * 
     * Ensure that the string is surrounded by quotes.
     * 
     * @param string
     * @return
     */
    protected String quotes(String string) {
        return ParameterValueUtils.quotes(string);
    }

    /**
     * 
     * Ensure that the string is not surrounded by quotes.
     * 
     * @param string
     * @return
     */
    protected String unquotes(String string) {
        return ParameterValueUtils.unquotes(string);
    }
}
