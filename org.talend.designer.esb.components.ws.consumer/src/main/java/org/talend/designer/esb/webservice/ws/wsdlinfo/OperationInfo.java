package org.talend.designer.esb.webservice.ws.wsdlinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author gcui
 */
public class OperationInfo {

    /** The URL where the target object is located. */
    private String targetURL = "";

    /** The namespace URI used for this SOAP operation. */
    private String namespaceURI = "";

    /** The name used to when making an invocation. */
    private String targetMethodName = "";

    /** The action URI value to use when making a invocation. */
    private String soapActionURI = "";

    private ParameterInfo input;
    private ParameterInfo output;
    private List<ParameterInfo> faults = new ArrayList<ParameterInfo>();

	private String portName;


    public ParameterInfo getInput() {
        return input;
    }

    public ParameterInfo getOutput() {
        return output;
    }
    
    public List<ParameterInfo> getFaults() {
    	return Collections.unmodifiableList(faults);
    }

    public void setInput(ParameterInfo input) {
        this.input = input;
    }

    public void setOutput(ParameterInfo output) {
        this.output = output;
    }


	public void addFault(ParameterInfo fault) {
		this.faults.add(fault);		
	}

    public void setTargetURL(String value) {
        targetURL = value;
    }

    public String getTargetURL() {
        return targetURL;
    }

    public void setNamespaceURI(String value) {
        namespaceURI = value;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public void setTargetMethodName(String value) {
        targetMethodName = value;
    }

    public String getTargetMethodName() {
        return targetMethodName;
    }

    public void setSoapActionURI(String value) {
        soapActionURI = value;
    }

    public String getSoapActionURI() {
        return soapActionURI;
    }

    public String toString() {
        return getTargetMethodName();
    }

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}
}
