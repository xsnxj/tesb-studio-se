package org.talend.designer.esb.webservice.ws.wsdlinfo;

import javax.xml.namespace.QName;


/**
 * 
 * @author gcui
 */
public class Function {

    private static final String ONE_WAY = "one-way"; //$NON-NLS-1$
    private static final String REQUEST_RESPONSE = "request-response"; //$NON-NLS-1$

    private String name;

    private String soapAction;

    private String nameSpaceURI;

    private String addressLocation;

    private QName serviceName;

    private String portName;

    private String communicationStyle;

    public Function(String name, String portName) {
        this.name = name;
        this.portName = portName;
    }

    public Function(ServiceInfo serviceInfo, OperationInfo oper) {
        String operationName = oper.getTargetMethodName() + '(';
        this.serviceName = serviceInfo.getServerName();
        this.portName = oper.getPortName();
        this.soapAction = oper.getSoapActionURI();
        this.nameSpaceURI = oper.getNamespaceURI();
        this.addressLocation = oper.getTargetURL();

        // input parameters
        ParameterInfo input = oper.getInput();
        if (input != null) {
            operationName += input.getDisplayName();
        }
        operationName += "):"; //$NON-NLS-1$

        // output parameters 
        ParameterInfo output = oper.getOutput();
        if (output != null) {
            communicationStyle = REQUEST_RESPONSE;
            operationName += output.getDisplayName();
        } else {
            communicationStyle = ONE_WAY;
        }

        Character sep = null;
        for (ParameterInfo fault : oper.getFaults()) {
            if (sep == null) {
                sep = ',';
                operationName += " throws "; //$NON-NLS-1$
            } else {
                operationName += sep;
            }
            operationName += fault.getDisplayName();
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
        return serviceName.getLocalPart();
    }

    public String getServiceNameSpace() {
        return serviceName.getNamespaceURI();
    }

    public String getPortName() {
        return this.portName;
    }

    public String getCommunicationStyle() {
        return communicationStyle;
    }

}
