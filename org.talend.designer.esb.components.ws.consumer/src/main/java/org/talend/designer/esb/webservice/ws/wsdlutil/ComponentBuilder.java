package org.talend.designer.esb.webservice.ws.wsdlutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.talend.designer.esb.webservice.ws.wsdlinfo.OperationInfo;
import org.talend.designer.esb.webservice.ws.wsdlinfo.ParameterInfo;
import org.talend.designer.esb.webservice.ws.wsdlinfo.ServiceInfo;


/**
 * DOC gcui class global comment. Detailled comment
 */
@SuppressWarnings("unchecked")
public class ComponentBuilder {

    private Definition def;

    public ServiceInfo[] buildserviceinformation(final String wsdlURI) throws WSDLException {
        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
        WSDLReader newWSDLReader = wsdlFactory.newWSDLReader();

        newWSDLReader.setExtensionRegistry(wsdlFactory.newPopulatedExtensionRegistry());
        newWSDLReader.setFeature(com.ibm.wsdl.Constants.FEATURE_VERBOSE, false);
        def = newWSDLReader.readWSDL(wsdlURI);

        Collection<Service> services = def.getServices().values();
        if (services == null) return new ServiceInfo[]{};
        ServiceInfo[] value = new ServiceInfo[services.size()];

        int i = 0;
        for (Service service : services) {
            value[i] = populateComponent(wsdlURI, service);
            i++;
        }
        return value;
    }


    private static Collection<ExtensibilityElement> findExtensibilityElement(List<ExtensibilityElement> extensibilityElements, String elementType) {
        List<ExtensibilityElement> elements = new ArrayList<ExtensibilityElement>();
        if (extensibilityElements != null) {
            for (ExtensibilityElement elment : extensibilityElements) {
                if (elment.getElementType().getLocalPart().equalsIgnoreCase(elementType)) {
                    elements.add(elment);
                }
            }
        }
        return elements;
    }

    private ServiceInfo populateComponent(final String wsdlURI, Service service) {
        ServiceInfo serviceInfo = new ServiceInfo(wsdlURI);
        final QName qName = service.getQName();
        serviceInfo.setServerName(qName.getLocalPart());
        serviceInfo.setServerNameSpace(qName.getNamespaceURI());
        Collection<Port> ports = service.getPorts().values();
        for (Port port : ports) {
            Binding binding = port.getBinding();
            for (OperationInfo operation : buildOperations(binding)) {
                operation.setPortName(port.getName());
                Collection<ExtensibilityElement> addrElems = findExtensibilityElement(port.getExtensibilityElements(), "address");
                for (ExtensibilityElement element : addrElems) {
                    if (element instanceof SOAPAddress) {
                        SOAPAddress soapAddr = (SOAPAddress) element;
                        operation.setTargetURL(soapAddr.getLocationURI());
                    } else if (element != null && element instanceof SOAP12Address) {
                        SOAP12Address soapAddr = (SOAP12Address) element;
                        operation.setTargetURL(soapAddr.getLocationURI());
                    }
                }
                serviceInfo.addOperation(operation);
            }
        }
        return serviceInfo;
    }

    private List<OperationInfo> buildOperations(Binding binding) {
        List<OperationInfo> operationInfos = new ArrayList<OperationInfo>();

        List<BindingOperation> operations = binding.getBindingOperations();
        if (operations != null && !operations.isEmpty()) {
            for (BindingOperation operation : operations) {
                OperationInfo operationInfo = new OperationInfo();
                buildOperation(operationInfo, operation);
                operationInfo.setNamespaceURI(binding.getPortType().getQName().getNamespaceURI());
                operationInfos.add(operationInfo);
            }
        }

        return operationInfos;
    }

    private OperationInfo buildOperation(OperationInfo operationInfo, BindingOperation bindingOper) {
        Operation oper = bindingOper.getOperation();
        operationInfo.setTargetMethodName(oper.getName());
        for (ExtensibilityElement operElem : findExtensibilityElement(bindingOper.getExtensibilityElements(), "operation")) {
            if (operElem != null && operElem instanceof SOAPOperation) {
                SOAPOperation soapOperation = (SOAPOperation) operElem;
                operationInfo.setSoapActionURI(soapOperation.getSoapActionURI());
            }
        }

        Input inDef = oper.getInput();
        if (inDef != null) {
            Message inMsg = inDef.getMessage();
            if (inMsg != null) {
                operationInfo.setInput(getParameterFromMessage(inMsg));
            }
        }

        Output outDef = oper.getOutput();
        if (outDef != null) {
            Message outMsg = outDef.getMessage();
            if (outMsg != null) {
                operationInfo.setOutput(getParameterFromMessage(outMsg));
            }
        }
        Collection<Fault> faults = oper.getFaults().values();
        for (Fault fault : faults) {
            Message faultMsg = fault.getMessage();
            if (faultMsg != null) {
                operationInfo.addFault(getParameterFromMessage(faultMsg));
            }
        }

        return operationInfo;
    }

    private ParameterInfo getParameterFromMessage(Message msg) {
        ParameterInfo parameterRoot = new ParameterInfo();
        List<Part> msgParts = msg.getOrderedParts(null);
        if (msgParts.size() > 1) {
            parameterRoot.setName(ParameterInfo.MULTIPART);
        } else if (msgParts.size() == 1) {
            Part part = msgParts.iterator().next();
            if (part.getElementName() != null) {
                parameterRoot.setName(part.getElementName());
            } else if (part.getTypeName() != null) {
                parameterRoot.setName(part.getTypeName());
            }
        }
        return parameterRoot;
    }

//	public Definition getDefinition() {
//		return def;
//	}
}
