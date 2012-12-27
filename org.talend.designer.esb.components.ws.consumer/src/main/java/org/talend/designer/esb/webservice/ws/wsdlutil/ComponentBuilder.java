package org.talend.designer.esb.webservice.ws.wsdlutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAll;
import org.apache.ws.commons.schema.XmlSchemaAny;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaAttributeOrGroupRef;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension;
import org.apache.ws.commons.schema.XmlSchemaComplexContentRestriction;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaGroupParticle;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSequenceMember;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.utils.XmlSchemaObjectBase;
import org.talend.designer.esb.webservice.WebServiceComponentPlugin;
import org.talend.designer.esb.webservice.ws.wsdlinfo.FlowInfo;
import org.talend.designer.esb.webservice.ws.wsdlinfo.OperationInfo;
import org.talend.designer.esb.webservice.ws.wsdlinfo.ParameterInfo;
import org.talend.designer.esb.webservice.ws.wsdlinfo.ServiceInfo;


/**
 * DOC gcui class global comment. Detailled comment
 */
public class ComponentBuilder {

    private List<String> parametersName = new ArrayList<String>();

    private List<String> schemaNames = new ArrayList<String>();

    private List<String> documentBaseList = new ArrayList<String>();

    private List<XmlSchemaElement> allXmlSchemaElement = new ArrayList<XmlSchemaElement>();

    private List<XmlSchemaType> allXmlSchemaType = new ArrayList<XmlSchemaType>();

    public String exceptionMessage = "";

	private Definition def;

    // SimpleTypesFactory simpleTypesFactory = null;

    public ServiceInfo[] buildserviceinformation(final String wsdlURI) throws WSDLException {
        def = ServiceDiscoveryHelper.getDefinition(wsdlURI);

        createSchemaFromTypes(def);

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

    private void addSchema(Map<XmlSchema, byte[]> map, XmlSchema schema) {
    	try {
    		ByteArrayOutputStream fos = new ByteArrayOutputStream();
    		schema.write(fos);
    		fos.close();
    		map.put(schema, fos.toByteArray());
            allXmlSchemaElement.addAll(schema.getElements().values());
        	allXmlSchemaType.addAll(schema.getSchemaTypes().values());
    	} catch (IOException e) {
            WebServiceComponentPlugin.getDefault().getLog().log(
                    WebServiceComponentPlugin.getStatus(null, e));
    	}
    }
    
    private Map<XmlSchema, byte[]> createSchemaFromTypes(Definition wsdlDefinition) {
        allXmlSchemaElement.clear();
    	allXmlSchemaType.clear();
        Map<XmlSchema, byte[]> schemas = new HashMap<XmlSchema, byte[]>();
        org.w3c.dom.Element schemaElementt = null;
        Map importElement = null;
        if (wsdlDefinition.getTypes() != null) {
            Collection<ExtensibilityElement> schemaExtElem = findExtensibilityElement(wsdlDefinition.getTypes().getExtensibilityElements(), "schema");
            for (ExtensibilityElement schemaElement : schemaExtElem) {
                if (schemaElement != null && schemaElement instanceof UnknownExtensibilityElement) {
                    schemaElementt = ((UnknownExtensibilityElement) schemaElement).getElement();

                    String documentBase = ((javax.wsdl.extensions.schema.Schema) schemaElement).getDocumentBaseURI();
                    XmlSchema schema = createschemafromtype(schemaElementt, wsdlDefinition, documentBase);
                    if (schema != null) {
                        addSchema(schemas, schema);
                        if (schema.getTargetNamespace() != null) {
                            schemaNames.add(schema.getTargetNamespace());
                        }
                    }
                    importElement = ((javax.wsdl.extensions.schema.Schema) schemaElement).getImports();
                    if (importElement != null && importElement.size() > 0) {
                        findImportSchema(wsdlDefinition, schemas, importElement);
                    }
                }

                if (schemaElement != null && schemaElement instanceof javax.wsdl.extensions.schema.Schema) {
                    schemaElementt = ((javax.wsdl.extensions.schema.Schema) schemaElement).getElement();
                    String documentBase = ((javax.wsdl.extensions.schema.Schema) schemaElement).getDocumentBaseURI();
                    Boolean isHaveImport = false;
                    importElement = ((javax.wsdl.extensions.schema.Schema) schemaElement).getImports();
                    if (importElement != null && importElement.size() > 0) {
                        if (importElement.size() > 0) {
                            isHaveImport = true;
                        }
                    }

                    XmlSchema schema = createschemafromtype(schemaElementt, wsdlDefinition, documentBase);
                    if (schema != null) {
                    	addSchema(schemas, schema);
                        if (schema.getTargetNamespace() != null) {
                            schemaNames.add(schema.getTargetNamespace());
                        }
                    }

                    if (isHaveImport) {
                        findImportSchema(wsdlDefinition, schemas, importElement);
                    }
                }
            }

        }
        return schemas;
    }

    /**
     * DOC gcui Comment method "findIncludesSchema".
     *
     * @param wsdlDefinition
     * @param schemas
     * @param includeElement
     */
    private void findIncludesSchema(Definition wsdlDefinition, Map<XmlSchema, byte[]> schemas, List includeElement) {
        org.w3c.dom.Element schemaElementt;
        for (int i = 0; i < includeElement.size(); i++) {

            schemaElementt = ((com.ibm.wsdl.extensions.schema.SchemaReferenceImpl) includeElement.get(i)).getReferencedSchema()
                    .getElement();
            String documentBase = ((com.ibm.wsdl.extensions.schema.SchemaReferenceImpl) includeElement.get(i))
                    .getReferencedSchema().getDocumentBaseURI();
            XmlSchema schemaInclude = createschemafromtype(schemaElementt, wsdlDefinition, documentBase);
            if (schemaInclude != null) {
            	addSchema(schemas, schemaInclude);
                if (schemaInclude.getTargetNamespace() != null) {
                    schemaNames.add(schemaInclude.getTargetNamespace());
                }
            }
        }
    }

    private void findImportSchema(Definition wsdlDefinition, Map<XmlSchema, byte[]> schemas, Map importElement) {
        org.w3c.dom.Element schemaElementt;
        List includeElement = null;
        Iterator keyIterator = importElement.keySet().iterator();
        Boolean isHaveImport = false;
        while (keyIterator.hasNext()) {
            String key = keyIterator.next().toString();
            Vector importEle = (Vector) importElement.get(key);

            for (int i = 0; i < importEle.size(); i++) {
                Map importChildElement = null;
                com.ibm.wsdl.extensions.schema.SchemaImportImpl importImpl = (com.ibm.wsdl.extensions.schema.SchemaImportImpl) importEle
                        .elementAt(i);
                if (importImpl.getReferencedSchema() != null) {

                    schemaElementt = importImpl.getReferencedSchema().getElement();
                    String documentBase = importImpl.getReferencedSchema().getDocumentBaseURI();

                    if ((com.ibm.wsdl.extensions.schema.SchemaImportImpl) importEle.elementAt(i) != null) {
                        if (((com.ibm.wsdl.extensions.schema.SchemaImportImpl) importEle.elementAt(i)).getReferencedSchema() != null) {
                            importChildElement = ((com.ibm.wsdl.extensions.schema.SchemaImportImpl) importEle.elementAt(i))
                                    .getReferencedSchema().getImports();
                            if (importChildElement != null && importChildElement.size() > 0 && !isIncludeSchema(documentBase)) {
                                isHaveImport = true;
                                documentBaseList.add(documentBase);
                                // validateImportUrlPath(importElement);
                            }
                        }
                    }

                    XmlSchema schemaImport = createschemafromtype(schemaElementt, wsdlDefinition, documentBase);
                    if (schemaImport != null) {
                    	addSchema(schemas, schemaImport);
                        if (schemaImport.getTargetNamespace() != null) {
                            schemaNames.add(schemaImport.getTargetNamespace());
                        }
                    }
                }

                if (isHaveImport) {
                    findImportSchema(wsdlDefinition, schemas, importChildElement);
                }

                if ((com.ibm.wsdl.extensions.schema.SchemaImportImpl) importEle.elementAt(i) != null) {
                    if (((com.ibm.wsdl.extensions.schema.SchemaImportImpl) importEle.elementAt(i)).getReferencedSchema() != null) {
                        includeElement = ((com.ibm.wsdl.extensions.schema.SchemaImportImpl) importEle.elementAt(i))
                                .getReferencedSchema().getIncludes();
                        if (includeElement != null && includeElement.size() > 0) {

                            findIncludesSchema(wsdlDefinition, schemas, includeElement);
                        }
                    }
                }

            }
        }
    }

    private Collection<ExtensibilityElement> findExtensibilityElement(List<ExtensibilityElement> extensibilityElements, String elementType) {
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

    private XmlSchema createschemafromtype(org.w3c.dom.Element schemaElement, Definition wsdlDefinition, String documentBase) {
        if (schemaElement == null) {
            exceptionMessage = exceptionMessage + "Unable to find schema extensibility element in WSDL";
            return null;
        }
        XmlSchema xmlSchema = null;
        XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();
        xmlSchemaCollection.setBaseUri(documentBase);

        xmlSchema = xmlSchemaCollection.read(schemaElement);

        // XmlSchemaObjectTable xmlSchemaObjectTable = xmlSchema.getSchemaTypes();

        return xmlSchema;
    }

    private Boolean isIncludeSchema(String documentBase) {
        Boolean isHaveSchema = false;
        for (int i = 0; i < documentBaseList.size(); i++) {
            String documentBaseTem = documentBaseList.get(i);
            if (documentBaseTem.equals(documentBase)) {
                isHaveSchema = true;
            }
        }
        return isHaveSchema;
    }

    @SuppressWarnings("unchecked")
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
                operationInfo.setInput(new FlowInfo(getParameterFromMessage(inMsg)));
            }
        }

        Output outDef = oper.getOutput();
        if (outDef != null) {
            Message outMsg = outDef.getMessage();
            if (outMsg != null) {
                operationInfo.setOutput(new FlowInfo(getParameterFromMessage(outMsg)));
            }
        }
        Collection<Fault> faults = oper.getFaults().values();
        for (Fault fault : faults) {
        	Message faultMsg = fault.getMessage();
        	if (faultMsg != null) {
                operationInfo.addFault(new FlowInfo(getParameterFromMessage(faultMsg)));
        	}
        }

        return operationInfo;
    }

	private ParameterInfo getParameterFromMessage(Message msg) {
        parametersName.clear();
		List<Part> msgParts = msg.getOrderedParts(null);
		if (msgParts.size() != 1) {
			//TODO: warn user
		}
        ParameterInfo parameterRoot = new ParameterInfo();
        for (Part part : msgParts) {
            String partElement = null;
            if (part.getElementName() != null) {
                partElement = part.getElementName().getLocalPart();
            } else if (part.getTypeName() != null) {
                partElement = part.getTypeName().getLocalPart();
            }
            // add first parameter from message.
            parameterRoot.setName(partElement);
            if (allXmlSchemaElement.size() > 0) {
                buildParameterFromElements(partElement, parameterRoot);
            } else if (allXmlSchemaType.size() > 0) {
                buildParameterFromTypes(partElement, parameterRoot);
            }
        }
        return parameterRoot;
    }

    private void buildParameterFromElements(String partElement, ParameterInfo parameterRoot) {
        parametersName.add(parameterRoot.getName());
        Iterator<XmlSchemaElement> elementsItr = allXmlSchemaElement.iterator();
        if (partElement != null) {
            while (elementsItr.hasNext()) {
                XmlSchemaElement xmlSchemaElement = elementsItr.next();
                if (partElement.equals(xmlSchemaElement.getName())) {
                    // ParameterInfo parameter = new ParameterInfo();
                    // parameter.setName(partName);
                    if (xmlSchemaElement.getSchemaType() != null) {
                        if (xmlSchemaElement.getSchemaType() instanceof XmlSchemaComplexType) {
                            XmlSchemaComplexType xmlElementComplexType = (XmlSchemaComplexType) xmlSchemaElement.getSchemaType();
                            XmlSchemaParticle xmlSchemaParticle = xmlElementComplexType.getParticle();
                            if (xmlSchemaParticle instanceof XmlSchemaGroupParticle) {
                                Collection<XmlSchemaObjectBase> xmlSchemaObjectCollection =
                                    getXmlSchemaObjectsFromXmlSchemaGroupParticle(
                                            (XmlSchemaGroupParticle) xmlSchemaParticle);
                                if (xmlSchemaObjectCollection != null) {
                                    buildParameterFromCollection(
                                            xmlSchemaObjectCollection,
                                            parameterRoot);
                                }
                            } else if (xmlSchemaElement.getSchemaTypeName() != null) {
                                String paraTypeName = xmlSchemaElement.getSchemaTypeName().getLocalPart();
                                if (paraTypeName != null) {
                                    parameterRoot.setType(paraTypeName);
                                    buildParameterFromTypes(paraTypeName, parameterRoot);
                                }
                            }
                        } else if (xmlSchemaElement.getSchemaType() instanceof XmlSchemaSimpleType) {
                            XmlSchemaSimpleType xmlSchemaSimpleType = (XmlSchemaSimpleType) xmlSchemaElement.getSchemaType();
                            String typeName = xmlSchemaSimpleType.getName();
                            parameterRoot.setType(typeName);
                        }
                    } else if (xmlSchemaElement.getSchemaTypeName() != null) {
                        String paraTypeName = xmlSchemaElement.getSchemaTypeName().getLocalPart();
                        if (paraTypeName != null) {
                            parameterRoot.setType(paraTypeName);
                            buildParameterFromTypes(paraTypeName, parameterRoot);
                        }
                    }
                }
            }
        }

    }

    /**
     * DOC gcui Comment method "buileParameterFromTypes".
     *
     * @param paraType
     * @param parameter
     * @param operationInfo
     * @param i
     */
    private void buildParameterFromTypes(String paraType, ParameterInfo parameter) {
        parametersName.add(parameter.getName());
        for (int i = 0; i < allXmlSchemaType.size(); i++) {
            XmlSchemaType type = allXmlSchemaType.get(i);
            String typeName = type.getName();
            if (paraType.equals(typeName)) {
                if (type instanceof XmlSchemaComplexType) {
                    XmlSchemaComplexType xmlSchemaComplexType = (XmlSchemaComplexType) type;
                    XmlSchemaParticle xmlSchemaParticle = xmlSchemaComplexType.getParticle();
                    Collection<XmlSchemaObjectBase> xmlSchemaObjectCollection = null;
                    if (xmlSchemaParticle == null && xmlSchemaComplexType.getContentModel() != null) {
                        Object obj = xmlSchemaComplexType.getContentModel().getContent();
                        if (obj instanceof XmlSchemaComplexContentExtension) {
                            XmlSchemaComplexContentExtension xscce = (XmlSchemaComplexContentExtension) obj;
                            if (xscce != null) {
                                xmlSchemaParticle = xscce.getParticle();
                            }
                            if (xmlSchemaParticle instanceof XmlSchemaGroupParticle) {
                                xmlSchemaObjectCollection =
                                    getXmlSchemaObjectsFromXmlSchemaGroupParticle(
                                            (XmlSchemaGroupParticle) xmlSchemaParticle);
                            }
                        } else if (obj instanceof XmlSchemaComplexContentRestriction) {
                            XmlSchemaComplexContentRestriction xsccr = (XmlSchemaComplexContentRestriction) obj;
                            List<XmlSchemaAttributeOrGroupRef> attrs = xsccr.getAttributes();
                            if (null != attrs && !attrs.isEmpty()) {
                                xmlSchemaObjectCollection = new ArrayList<XmlSchemaObjectBase>(attrs);
                            }
                        }
                    } else if (xmlSchemaParticle instanceof XmlSchemaGroupParticle) {
                        xmlSchemaObjectCollection =
                            getXmlSchemaObjectsFromXmlSchemaGroupParticle(
                                    (XmlSchemaGroupParticle) xmlSchemaParticle);
                    }
                    if (xmlSchemaObjectCollection != null) {
                        buildParameterFromCollection(xmlSchemaObjectCollection, parameter);
                    }
                } else if (type instanceof XmlSchemaSimpleType) {
                    // Will TO DO if need.
                    // System.out.println("XmlSchemaSimpleType");
                }
            }
        }
    }

    private Collection<XmlSchemaObjectBase> getXmlSchemaObjectsFromXmlSchemaGroupParticle(
            XmlSchemaGroupParticle xmlSchemaParticle) {
        Collection<XmlSchemaObjectBase> xmlSchemaObjectCollection = null;
        if (xmlSchemaParticle instanceof XmlSchemaAll) {
            XmlSchemaAll xmlSchemaAll = (XmlSchemaAll) xmlSchemaParticle;
            List<XmlSchemaElement> items = xmlSchemaAll.getItems();
            if (null != items && !items.isEmpty()) {
                xmlSchemaObjectCollection = new ArrayList<XmlSchemaObjectBase>(items);
            }
        } else if (xmlSchemaParticle instanceof XmlSchemaChoice) {
            XmlSchemaChoice xmlSchemaChoice = (XmlSchemaChoice) xmlSchemaParticle;
            List<XmlSchemaObject> items = xmlSchemaChoice.getItems();
            if (null != items && !items.isEmpty()) {
                xmlSchemaObjectCollection = new ArrayList<XmlSchemaObjectBase>(items);
            }
        } else if (xmlSchemaParticle instanceof XmlSchemaSequence) {
            XmlSchemaSequence xmlSchemaSequence = (XmlSchemaSequence) xmlSchemaParticle;
            List<XmlSchemaSequenceMember> items = xmlSchemaSequence.getItems();
            if (null != items && !items.isEmpty()) {
                xmlSchemaObjectCollection = new ArrayList<XmlSchemaObjectBase>(items);
            }
        }
        return xmlSchemaObjectCollection;
    }

    private void buildParameterFromCollection(Collection<XmlSchemaObjectBase> xmlSchemaObjectCollection,
            ParameterInfo parameter) {
        // XmlSchemaSequence xmlSchemaSequence = (XmlSchemaSequence) xmlSchemaParticle;
        // XmlSchemaObjectCollection xmlSchemaObjectCollection = xmlSchemaSequence.getItems();
        for (XmlSchemaObjectBase xmlSchemaObject : xmlSchemaObjectCollection) {
            if (xmlSchemaObject instanceof XmlSchemaGroupParticle) {
                Collection<XmlSchemaObjectBase> items =
                    getXmlSchemaObjectsFromXmlSchemaGroupParticle(
                            (XmlSchemaGroupParticle) xmlSchemaObject);
                if (null != items && !items.isEmpty()) {
                    buildParameterFromCollection(items, parameter);
                }
            } else if (xmlSchemaObject instanceof XmlSchemaAny) {
                ParameterInfo parameterSon = new ParameterInfo();
                parameterSon.setName("_content_");
                parameter.getParameterInfos().add(parameterSon);

            } else if (xmlSchemaObject instanceof XmlSchemaElement) {
                XmlSchemaElement xmlSchemaElement = (XmlSchemaElement) xmlSchemaObject;
                String elementName = xmlSchemaElement.getName();
                ParameterInfo parameterSon = new ParameterInfo();
                parameterSon.setName(elementName);
                parameter.getParameterInfos().add(parameterSon);

                Boolean isHave = false;
                if (!parametersName.isEmpty() && parameterSon.getName() != null) {
                    for (int p = 0; p < parametersName.size(); p++) {
                        if (parameterSon.getName().equals(parametersName.get(p))) {
                            isHave = true;
                        }
                    }
                }
                if (xmlSchemaElement.getSchemaTypeName() != null) {
                    String elementTypeName = xmlSchemaElement.getSchemaTypeName().getLocalPart();
                    parameterSon.setType(elementTypeName);
                    if (!isHave && !WsdlTypeUtil.isJavaBasicType(elementTypeName)) {
                        buildParameterFromTypes(elementTypeName, parameterSon);
                    }

                } else if (xmlSchemaElement.getSchemaType() != null) {
                    if (xmlSchemaElement.getSchemaType() instanceof XmlSchemaComplexType) {
                        XmlSchemaComplexType xmlElementComplexType = (XmlSchemaComplexType) xmlSchemaElement.getSchemaType();
                        XmlSchemaParticle xmlSchemaParticle = xmlElementComplexType.getParticle();
                        if (xmlSchemaParticle instanceof XmlSchemaGroupParticle) {
                            Collection<XmlSchemaObjectBase> childCollection =
                                getXmlSchemaObjectsFromXmlSchemaGroupParticle(
                                        (XmlSchemaGroupParticle) xmlSchemaParticle);
                            if (childCollection != null && !isHave) {
                                buildParameterFromCollection(childCollection, parameterSon);
                            }
                        } else if (xmlSchemaElement.getSchemaTypeName() != null) {
                            String paraTypeName = xmlSchemaElement.getSchemaTypeName().getLocalPart();
                            if (paraTypeName != null && !isHave) {
                                parameter.setType(paraTypeName);
                                buildParameterFromTypes(paraTypeName, parameterSon);
                            }
                        }
                    } else if (xmlSchemaElement.getSchemaType() instanceof XmlSchemaSimpleType) {
                        XmlSchemaSimpleType xmlSchemaSimpleType = (XmlSchemaSimpleType) xmlSchemaElement.getSchemaType();
                        String typeName = xmlSchemaSimpleType.getName();
                        parameter.setType(typeName);
                    }

                } else if (xmlSchemaElement.getTargetQName() != null) {
                    String elementTypeName = xmlSchemaElement.getTargetQName().getLocalPart();
                    if (!isHave && !WsdlTypeUtil.isJavaBasicType(elementTypeName)) {
                        buildParameterFromElements(elementTypeName, parameterSon);
                    }
                }

            } else if (xmlSchemaObject instanceof XmlSchemaAttribute) {
                XmlSchemaAttribute xmlSchemaAttribute = (XmlSchemaAttribute) xmlSchemaObject;
                String elementName = xmlSchemaAttribute.getName();
                ParameterInfo parameterSon = new ParameterInfo();
                parameterSon.setName(elementName);

                parameter.getParameterInfos().add(parameterSon);
                Boolean isHave = false;
                if (!parametersName.isEmpty() && parameterSon.getName() != null) {
                    for (int p = 0; p < parametersName.size(); p++) {
                        if (parameterSon.getName().equals(parametersName.get(p))) {
                            isHave = true;
                        }
                    }
                }
                if (xmlSchemaAttribute.getSchemaTypeName() != null) {
                    String elementTypeName = xmlSchemaAttribute.getSchemaTypeName().getLocalPart();
                    parameterSon.setType(elementTypeName);
                    if (!isHave && !WsdlTypeUtil.isJavaBasicType(elementTypeName)) {
                        buildParameterFromTypes(elementTypeName, parameterSon);
                    }
                } else if (xmlSchemaAttribute.getTargetQName() != null) {
                    String refName = xmlSchemaAttribute.getTargetQName().getLocalPart();
                    parameterSon.setType(refName);
                    if (!isHave) {
                        buildParameterFromElements(refName, parameterSon);

                    }
                }
            }
        }

    }

    public String getExceptionMessage() {
        return this.exceptionMessage;
    }

	public Definition getDefinition() {
		return def;
	}
}
