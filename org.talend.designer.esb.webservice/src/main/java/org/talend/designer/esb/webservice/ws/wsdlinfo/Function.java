package org.talend.designer.esb.webservice.ws.wsdlinfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author gcui
 */
public class Function {

    private List<ParameterInfo> inputParameters;

    private List<ParameterInfo> outputParameters;

    private String name;

    private String soapAction;

    private String nameSpaceURI;

    private String encodingStyle;

    private String addressLocation;

    private String serverName;

    private String serverNameSpace;

    private List<String> portNames;
    
    private byte[] inputSchema;
    private byte[] outputSchema;


    
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
        inputParameters = new ArrayList<ParameterInfo>();
        List<ParameterInfo> inps = oper.getInparameters();
        if ((inps == null) || (inps.size() == 0)) {
            inputParameters.add(new ParameterInfo());
            operationName = operationName + "):";
        } else {
        	inputSchema = oper.getInSchema();
            for (ParameterInfo element : inps ) {
                getParaFullName(element);
                inputParameters.add(element);
                if (element.getType() != null) {
                    operationName = operationName + element.getType() + ",";
                } else if (element.getType() == null && element.getName() != null) {
                    operationName = operationName + element.getName() + ",";
                } else if (element.getType() == null) {
                    operationName = operationName + "noType" + ",";
                }
                if (element.getType() == null
                        && (element.getParameterInfos() == null || element.getParameterInfos().isEmpty())
                        && inps.size() == 1) {

                    element.setName(element.getName());
                }
            }
            int operationNamelen = operationName.length();
            operationName = operationName.substring(0, operationNamelen - 1) + "):";
        }
        // output parameters 
        outputParameters = new ArrayList<ParameterInfo>();
        List<ParameterInfo> outps = oper.getOutparameters();
        if ((outps == null) || (outps.size() == 0)) {
            //outputParameters.add(new ParameterInfo());
        } else {
        	outputSchema = oper.getOutSchema();
            for (ParameterInfo element : outps) {
                getParaFullName(element);
                outputParameters.add(element);
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
            }
            int operationNamelen = operationName.length();
            operationName = operationName.substring(0, operationNamelen - 1);
        }
        this.name = operationName;
    }

    /**
	 * @return the inputSchema
	 */
	public byte[] getInputSchema() {
		return inputSchema;
	}

	/**
	 * @return the outputSchema
	 */
	public byte[] getOutputSchema() {
		return outputSchema;
	}

	private final static void getParaFullName(ParameterInfo paraElement) {
        paraElement.setParaFullName(paraElement.getName());
        getAllChildren(paraElement);

    }

    private final static List<ParameterInfo> getAllChildren(ParameterInfo para) {
        List<ParameterInfo> list = new ArrayList<ParameterInfo>();
        List<ParameterInfo> childList = para.getParameterInfos();
        for (ParameterInfo paraC : childList) {
            if (paraC.getParent().getParaFullName() != null) {
                paraC.setParaFullName(paraC.getParent().getParaFullName() + "." + paraC.getName());
            }
        }
        list.addAll(childList);
        for (ParameterInfo paraC : childList) {
            if (paraC.getParameterInfos().size() > 0) {
                list.addAll(getAllChildren(paraC));
            }
        }
        return list;
    }
    
    public List<ParameterInfo> getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(List<ParameterInfo> inputParameters) {
        this.inputParameters = inputParameters;
    }

    public List<ParameterInfo> getOutputParameters() {
        return outputParameters;
    }

    public void setOutputParameters(List<ParameterInfo> outputParameters) {
        this.outputParameters = outputParameters;
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
