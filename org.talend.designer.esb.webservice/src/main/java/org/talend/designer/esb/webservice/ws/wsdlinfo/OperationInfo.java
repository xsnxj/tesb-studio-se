package org.talend.designer.esb.webservice.ws.wsdlinfo;

import java.util.ArrayList;
import java.util.List;

import javax.wsdl.Message;

/**
 * 
 * @author gcui
 */
public class OperationInfo {

    /** SOAP operation type */
    private String operationType = "";

    /** The SOAP encoding style to use. */
    private String encodingStyle = "";

    /** The URL where the target object is located. */
    private String targetURL = "";

    /** The namespace URI used for this SOAP operation. */
    private String namespaceURI = "";

    /** The URI of the target object to invoke for this SOAP operation. */
    private String targetObjectURI = "";

    /** The name used to when making an invocation. */
    private String targetMethodName = "";

    /** The action URI value to use when making a invocation. */
    private String soapActionURI = "";

    /** The encoding type "document" vs. "rpc" */
    private String style = "document";

    private List<ParameterInfo> inparameters = new ArrayList<ParameterInfo>();

    private List<ParameterInfo> outparameters = new ArrayList<ParameterInfo>();

    private Message inputMessage;

    private Message outputMessage;
    
    private byte[] inputSchema;
    private byte[] outputSchema;

    private String serviceid;

    public OperationInfo() {
        super();
    }

    public OperationInfo(String style) {
        super();
        setStyle(style);
    }

    public String getServiceid() {
        return serviceid;
    }

    public void setServiceid(String serviceid) {
        this.serviceid = serviceid;
    }

    public Message getInputMessage() {
        return inputMessage;
    }

    public void setInputMessage(Message message, byte[] schema) {
        this.inputMessage = message;
        this.inputSchema = schema;
    }

    public Message getOutputMessage() {
        return outputMessage;
    }

    public void setOutputMessage(Message outputMessage, byte[] schema) {
        this.outputMessage = outputMessage;
        this.outputSchema = schema;
    }

    public void addInparameter(ParameterInfo parameter) {
        this.inparameters.add(parameter);
    }

    public List<ParameterInfo> getInparameters() {
        return inparameters;
    }

    public void addOutparameter(ParameterInfo parameter) {
        this.outparameters.add(parameter);
    }

    public List<ParameterInfo> getOutparameters() {
        return this.outparameters;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public void setEncodingStyle(String value) {
        encodingStyle = value;
    }

    public String getEncodingStyle() {
        return encodingStyle;
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

    public void setTargetObjectURI(String value) {
        targetObjectURI = value;
    }

    public String getTargetObjectURI() {
        return targetObjectURI;
    }

    public void setTargetMethodName(String value) {
        targetMethodName = value;
    }

    public String getTargetMethodName() {
        return targetMethodName;
    }

    public String getInputMessageName() {
        return inputMessage.getQName().getLocalPart();
    }

    public String getOutputMessageName() {
        return outputMessage.getQName().getLocalPart();
    }

    public void setSoapActionURI(String value) {
        soapActionURI = value;
    }

    public String getSoapActionURI() {
        return soapActionURI;
    }

    public void setStyle(String value) {
        style = value;
    }

    public String getStyle() {
        return style;
    }

    public String toString() {
        return getTargetMethodName();
    }

	/**
	 * @return the inputSchema
	 */
	public byte[] getInSchema() {
		return inputSchema;
	}

	/**
	 * @return the outputSchema
	 */
	public byte[] getOutSchema() {
		return outputSchema;
	}
    
    
}
