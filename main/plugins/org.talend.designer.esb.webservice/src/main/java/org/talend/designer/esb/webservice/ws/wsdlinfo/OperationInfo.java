package org.talend.designer.esb.webservice.ws.wsdlinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

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

    private FlowInfo input;
    private FlowInfo output;
    private List<FlowInfo> faults = new ArrayList<FlowInfo>();

    private String serviceid;
	private QName portTypeName;

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

    public FlowInfo getInput() {
        return input;
    }

    public FlowInfo getOutput() {
        return output;
    }
    
    public List<FlowInfo> getFaults() {
    	return Collections.unmodifiableList(faults);
    }

    public void setInput(FlowInfo input) {
        this.input = input;
    }

    public void setOutput(FlowInfo output) {
        this.output = output;
    }


	public void addFault(FlowInfo fault) {
		this.faults.add(fault);		
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

	public QName getPortTypeName() {
		return portTypeName;
	}

    public void setPortTypeName(QName qName) {
    	portTypeName = qName;
    }

}
