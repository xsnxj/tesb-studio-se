package org.talend.designer.esb.webservice.ws.wsdlinfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author gcui
 */
public class ServiceInfo {

    /** server name */
    private String serverName;

    private String serverNameSpace;

    /** WSDL URI */
    private String wsdluri;//

    private String endpoint;

    private String targetnamespace;

    /** The list of operations that this service defines. */
    private final List<OperationInfo> operations = new ArrayList<OperationInfo>();

    public ServiceInfo(String wsdlURI) {
        this.wsdluri = wsdlURI;
    }

    public Collection<OperationInfo> getOperations() {
        return operations;
    }

    public void addOperation(OperationInfo operation) {
        operations.add(operation);
    }

    public String toString() {
        return getServerName();
    }

    public String getTargetnamespace() {
        return targetnamespace;
    }

    public void setTargetnamespace(String targetnamespace) {
        this.targetnamespace = targetnamespace;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getWsdlUri() {
        return wsdluri;
    }

    public void setWsdlUri(String wsdluri) {
        this.wsdluri = wsdluri;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String name) {
        this.serverName = name;
    }

    public String getServerNameSpace() {
        return serverNameSpace;
    }

    public void setServerNameSpace(String serverNameSpace) {
        this.serverNameSpace = serverNameSpace;
    }

}
