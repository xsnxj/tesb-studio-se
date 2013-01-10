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
package org.talend.designer.camel.spring.ui.exports;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.emf.common.util.EList;
import org.talend.designer.camel.spring.ui.SpringUIConstants;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public abstract class AbstractExParameterHandler implements IExportParameterHandler {

    private static final String TABLE = SpringUIConstants.FIELD_TABLE;

    public static final String VALUE_TRUE = SpringUIConstants.VALUE_TRUE;

    public static final String VALUE_FALSE = SpringUIConstants.VALUE_FALSE;
    
    private String component;

    private Map<String, String> parameters;

    /**
     * DOC LiXP AbstractParameterProvider constructor comment.
     * 
     * @param component
     */
    public AbstractExParameterHandler(String component) {
        this.component = component;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.camel.spring.ui.exports.IParameterProvider#getComponentName()
     */
    public String getComponentName() {
        return component;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.camel.spring.ui.exports.IParameterProvider#getAvaiableParameters()
     */
    protected Map<String, String> getAvaiableParameters() {
        if (parameters != null) {
            return parameters;
        }
        parameters = new HashMap<String, String>();
        Properties prop = new Properties();
        try {
            String propFile = SpringUIConstants.PROPERTY_FOLDER + "/" + component + SpringUIConstants.PROPERTY_POSTFIX;
            InputStream inputStream = SpringUIConstants.class.getResourceAsStream(propFile);
            prop.load(inputStream);
            for (Entry<Object, Object> entry : prop.entrySet()) {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                if (key.endsWith(SpringUIConstants.NAME_POSTFIX)) {
                    key = key.replace(SpringUIConstants.NAME_POSTFIX, "");
                    parameters.put(value, key);
                }else if(key.endsWith(SpringUIConstants.REF_POSTFIX)){
                    //REF_CHECK
                    key = key.replace(SpringUIConstants.REF_POSTFIX, "");
                    String refName = prop.getProperty(key + SpringUIConstants.NAME_POSTFIX);
                    parameters.put(refName + SpringUIConstants.REF_POSTFIX, value);
                }
            }
            inputStream.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Component " + component + " is not supported.");
        }
        return parameters;
    }

    public void handleParameters(EList<?> elementParameterTypes, Map<String, String> parameters) {
        
        Map<String, String> avaiableParamMapping = getAvaiableParameters();
        for (Object obj : elementParameterTypes) {
            ElementParameterType param = (ElementParameterType) obj;
            if (param.getField().equals(TABLE)) {
                handleTableParameter(param, parameters);
            } else {
                String paramName = param.getName();
                String paramValue = param.getValue();
                String matchParam = avaiableParamMapping.get(paramName);
                String paramRef = avaiableParamMapping.get(paramName + SpringUIConstants.REF_POSTFIX);
                if(paramRef != null){
                    boolean paramRefChecked = computeCheckElementValue(paramRef, elementParameterTypes);
                    if(paramRefChecked){
                        if (matchParam != null) {
                            parameters.put(matchParam, paramValue);
                        }
                    }
                }else{
                    if (matchParam != null) {
                        parameters.put(matchParam, paramValue);
                    }
                }
            }

        }

    }

    /**
     * 
     * DOC LiXP Comment method "handleTableParameter".
     * @param param
     * @param parameters2
     */
    protected void handleTableParameter(ElementParameterType param, Map<String, String> parameters) {
        Map<String, String> avaiableParamMapping = getAvaiableParameters();
        EList<?> elementValues = param.getElementValue();
        if (elementValues == null || elementValues.isEmpty()) {
            return;
        }
        if (isTwoColumnTable(param.getName())) {
            for (int index = 0; index < elementValues.size() - 1; index++) {
                ElementValueType value1 = (ElementValueType) elementValues.get(index);
                index++;
                ElementValueType value2 = (ElementValueType) elementValues.get(index);
                parameters.put(value1.getValue(), value2.getValue());
            }
        } else if (isOneColumnTable(param.getName())) {
            StringBuffer sb = new StringBuffer();
            ElementValueType firstValue = (ElementValueType) elementValues.get(0);
            sb.append(firstValue.getValue());
            for (int index = 1; index < elementValues.size(); index++) {
                ElementValueType elementValueType = (ElementValueType) elementValues.get(index);
                sb.append(";" + elementValueType.getValue());
            }
            String paramName = firstValue.getElementRef();
            String matchParam = avaiableParamMapping.get(paramName);
            if (matchParam != null) {
                parameters.put(matchParam, sb.toString());
            }
        }

    }

    protected boolean isTwoColumnTable(String name) {
        return "ADVARGUMENTS".equals(name) || "URI_OPTIONS".equals(name);
    }

    protected boolean isOneColumnTable(String name) {
        return "EXCEPTIONS".equals(name) || "URIS".equals(name);
    }
    
    protected ElementParameterType findElementParameterByName(String paramName, EList<?> elementParameterTypes) {
        for(Object obj: elementParameterTypes){
            ElementParameterType cpType = (ElementParameterType) obj;
            if(paramName.equals(cpType.getName())){
                return cpType;
            }
        }
        return null;
    }
    
    protected boolean computeCheckElementValue(String paramName, EList<?> elementParameterTypes) {
        ElementParameterType cpType = findElementParameterByName(paramName, elementParameterTypes);
        if(cpType == null){
            return false;
        }
        String isNone = cpType.getValue();
        return VALUE_TRUE.equals(isNone);
    }
    
    protected String computeTextElementValue(String paramName, EList<?> elementParameterTypes) {
        ElementParameterType cpType = findElementParameterByName(paramName, elementParameterTypes);
        if(cpType == null){
            return "";
        }
        return cpType.getValue()==null?"":cpType.getValue();
    }
    
    /**
     * 
     * Ensure that the string is surrounded by quotes.
     * 
     * @param string
     * @return
     */
    protected String quotes(String string) {
        String result = string;
        if (!result.startsWith("\"")) {
            result = "\"" + result;
        }

        if (!result.endsWith("\"")) {
            result = result + "\"";
        }
        return result;
    }

    /**
     * 
     * Ensure that the string is not surrounded by quotes.
     * 
     * @param string
     * @return
     */
    protected String unquotes(String string) {
        String result = string;
        if (result.startsWith("\"")) {
            result = result.substring(1);
        }

        if (result.endsWith("\"")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
