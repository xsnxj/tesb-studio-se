package org.talend.designer.esb.webservice.ws.wsdlinfo;


/**
 * 
 * @author gcui
 */
public class Function {

    private static final String ONE_WAY = "one-way";
    private static final String REQUEST_RESPONSE = "request-response";

    private String name;

    private String soapAction;

    private String nameSpaceURI;

    private String addressLocation;

    private String serviceName;

    private String serviceNameSpace;

    private String portName;

    private String communicationStyle;

    public Function(String name, String portName) {
        this.name = name;
        this.portName = portName;
    }

    public Function(ServiceInfo serviceInfo, OperationInfo oper) {
        String operationName = oper.getTargetMethodName() + "(";
        this.serviceName = serviceInfo.getServerName();
        this.serviceNameSpace = serviceInfo.getServerNameSpace();
        this.portName = oper.getPortName();
        this.soapAction = oper.getSoapActionURI();
        this.nameSpaceURI = oper.getNamespaceURI();
        this.addressLocation = oper.getTargetURL();

        // input parameters
        FlowInfo input = oper.getInput();
        if (input == null) {
            operationName = operationName + "):";
        } else {
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
        FlowInfo output = oper.getOutput();
        if (output != null) {
            communicationStyle = REQUEST_RESPONSE;
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
        } else {
        	communicationStyle = ONE_WAY;
        }
        this.name = operationName;
    }

	public String getName() {
        return name;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public String getNameSpaceURI() {
        return nameSpaceURI;
    }

    public String getAddressLocation() {
        return this.addressLocation;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public String getServiceNameSpace() {
        return this.serviceNameSpace;
    }

    public String getPortName() {
        return this.portName;
    }

    public String getCommunicationStyle() {
        return communicationStyle;
    }

}
