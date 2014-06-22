package org.talend.designer.esb.webservice.ws.wsdlinfo;

import javax.wsdl.Message;

public class FlowInfo {

	private ParameterInfo parameterRoot;
    private Message message;
    private byte[] schema;

    public FlowInfo(Message message, byte[] schema, ParameterInfo parameterRoot) {
		this.message = message;
		this.schema = schema;
		this.parameterRoot = parameterRoot;
	}

	/**
	 * @return the parameterRoot
	 */
	public ParameterInfo getParameterRoot() {
		return parameterRoot;
	}

	/**
	 * @return the message
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * @return the schema
	 */
	public byte[] getSchema() {
		return schema;
	}

    public String getMessageName() {
        return message.getQName().getLocalPart();
    }

}
