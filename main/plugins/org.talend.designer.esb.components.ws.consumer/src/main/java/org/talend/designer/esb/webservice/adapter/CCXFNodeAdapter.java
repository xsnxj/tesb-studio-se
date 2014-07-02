package org.talend.designer.esb.webservice.adapter;

import static org.talend.designer.esb.webservice.WebServiceConstants.*;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.talend.core.model.utils.TalendTextUtils;
import org.talend.designer.esb.webservice.ServiceSetting;
import org.talend.designer.esb.webservice.WebServiceNode;
import org.talend.designer.esb.webservice.ws.wsdlinfo.Function;

class CCXFNodeAdapter extends AbstractNodeAdapter {



	CCXFNodeAdapter(WebServiceNode node) {
		super(node);
	}

	@Override
	public IStatus setNodeSetting(ServiceSetting setting) {
		Function currentFunction = setting.getFunction();
		node.setParamValue("WSDL_FILE", setting.getWsdlLocation());
		String operationName = currentFunction.getName();
		operationName = operationName.substring(0, operationName.indexOf('('));
		node.setParamValue(OPERATION_NAME, TalendTextUtils.addQuotes(operationName));

		String fullServiceName = "{" + currentFunction.getServiceNameSpace() + "}" + currentFunction.getServiceName();
		node.setParamValue(SERVICE_NAME, TalendTextUtils.addQuotes(fullServiceName));

		String fullPortName = "{" + currentFunction.getServiceNameSpace() + "}" + currentFunction.getPortName();
		node.setParamValue(PORT_NAME, TalendTextUtils.addQuotes(fullPortName));
		return Status.OK_STATUS;
	}

	@Override
	public Function loadCurrentFunction() {
		String operationName = node.getParamStringValue(OPERATION_NAME);
		if (operationName == null) {
			return null;
		}
		operationName = getExpressionParamValue(operationName);
		String portName = node.getParamStringValue(PORT_NAME);
		if (portName == null) {
			return null;
		}
		portName = getExpressionParamValue(portName);
		portName = portName.substring(portName.indexOf('}') + 1);
		String serviceName = node.getParamStringValue(SERVICE_NAME);
		// String targetNamespace = node.getParamStringValue(SERVICE_NS);
		if (serviceName == null /* || targetNamespace == null */) {
			return null;
		}
		QName serviceQName = QName.valueOf(serviceName);
		return new Function(operationName, portName, serviceQName);
	}

	@Override
	public String getInitialWsdlLocation() {
		return node.getParamStringValue("WSDL_FILE");
	}

	@Override
	public boolean isServiceOperationRequired() {
		//only required when acts as consumer
		return isConsumerNode();
	}

	public boolean isConsumerNode() {
		return node.getIncomingConnections().size() > 0;
	}

	@Override
	public boolean allowPopulateSchema() {
		return false;
	}
}