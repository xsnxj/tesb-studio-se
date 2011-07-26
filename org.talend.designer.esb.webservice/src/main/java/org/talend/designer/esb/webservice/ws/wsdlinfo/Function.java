package org.talend.designer.esb.webservice.ws.wsdlinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author gcui
 */
public class Function {

    private String name;

    private String soapAction;

    private String nameSpaceURI;

    private String encodingStyle;

    private String addressLocation;

    private String serverName;

    private String serverNameSpace;

    private List<String> portNames;
    
    private FlowInfo input;
    private FlowInfo output;
    private List<FlowInfo> faults = new ArrayList<FlowInfo>();


    
    public Function(String name) {
        this.name = name;
    }

    public Function(ServiceInfo serviceInfo, OperationInfo oper) {
        String operationName = oper.getTargetMethodName() + "(";
        this.serverName = serviceInfo.getServerName();
        this.serverNameSpace = serviceInfo.getServerNameSpace();
        this.portNames = new ArrayList<String>();
        if (serviceInfo.getPortNames() != null) {
            this.portNames.addAll(serviceInfo.getPortNames());
        }
        this.soapAction = (oper.getSoapActionURI());
        this.nameSpaceURI = oper.getNamespaceURI();
        this.encodingStyle = oper.getEncodingStyle();
        this.addressLocation = oper.getTargetURL();
        
        
        // input parameters
        input = oper.getInput();
        if (input == null) {
            operationName = operationName + "):";
        } else {
        	input.getSchema();
            ParameterInfo element = input.getParameterRoot();
            if (element.getType() != null) {
                operationName = operationName + element.getType() + ",";
            } else if (element.getType() == null && element.getName() != null) {
                operationName = operationName + element.getName() + ",";
            } else if (element.getType() == null) {
                operationName = operationName + "noType" + ",";
            }
            if (element.getType() == null
                    && (element.getParameterInfos() == null || element.getParameterInfos().isEmpty())) {
                element.setName(element.getName());
            }
            int operationNamelen = operationName.length();
            operationName = operationName.substring(0, operationNamelen - 1) + "):";
        }
        // output parameters 
        output = oper.getOutput();
        if (output != null) {
            ParameterInfo element = output.getParameterRoot();
            if (element.getType() != null) {
                operationName = operationName + element.getType() + ",";
            } else if (element.getParameterInfos() != null && !element.getParameterInfos().isEmpty()) {
                for (ParameterInfo elementBranch : element.getParameterInfos()) {
                    if (elementBranch.getType() != null) {
                        operationName = operationName + elementBranch.getType() + ",";
                    } else {
                        operationName = operationName + "noType" + ",";
                    }
                }
            }
            operationName = operationName.substring(0, operationName.length() - 1);
        }
        this.name = operationName;
        this.faults = oper.getFaults();
    }

    /**
	 * @return the input
	 */
	public FlowInfo getInput() {
		return input;
	}

	/**
	 * @return the output
	 */
	public FlowInfo getOutput() {
		return output;
	}

	/**
	 * @return the faults
	 */
	public List<FlowInfo> getFaults() {
		return Collections.unmodifiableList(faults);
	}

	public String getName() {
        return name;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public String getNameSpaceURI() {
        return nameSpaceURI;
    }

    public void setNameSpaceURI(String nameSpaceURI) {
        this.nameSpaceURI = nameSpaceURI;
    }

    public String getEncodingStyle() {
        return this.encodingStyle;
    }

    public void setEncodingStyle(String encodingStyle) {
        this.encodingStyle = encodingStyle;
    }

    public String getAddressLocation() {
        return this.addressLocation;
    }

    public void setAddressLocation(String addressLocation) {
        this.addressLocation = addressLocation;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerNameSpace() {
        return this.serverNameSpace;
    }

    public void setServerNameSpace(String serverNameSpace) {
        this.serverNameSpace = serverNameSpace;
    }

    public List<String> getPortNames() {
        return this.portNames;
    }

    public void setPortNames(List<String> portNames) {
        this.portNames = portNames;
    }

}
