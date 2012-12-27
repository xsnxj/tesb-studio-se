package org.talend.designer.esb.webservice.ws.wsdlinfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author gcui
 */
public class ParameterInfo {

    private String name;

    private String type;

    /* list of parameters, only filled if complex type */
    private final List<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();

    public List<ParameterInfo> getParameterInfos() {
        return parameterInfos;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
