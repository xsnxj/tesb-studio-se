package org.talend.designer.esb.webservice.ws.wsdlutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
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
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.designer.esb.webservice.ws.wsdlinfo.OperationInfo;
import org.talend.designer.esb.webservice.ws.wsdlinfo.ParameterInfo;
import org.talend.designer.esb.webservice.ws.wsdlinfo.ServiceInfo;

/**
 * DOC gcui class global comment. Detailled comment
 */
public class ComponentBuilder {

    WSDLFactory wsdlFactory = null;

    private Vector<XmlSchema> wsdlTypes = new Vector<XmlSchema>();

    private int inOrOut;

    private List<String> parametersName = new ArrayList<String>();

    private List<String> schemaNames = new ArrayList<String>();

    private List<String> documentBaseList = new ArrayList<String>();

    private List<XmlSchemaElement> allXmlSchemaElement = new ArrayList<XmlSchemaElement>();

    private List<XmlSchemaType> allXmlSchemaType = new ArrayList<XmlSchemaType>();

    private XmlSchemaCollection schemaCollection;

    public String exceptionMessage = "";

    public final static String DEFAULT_SOAP_ENCODING_STYLE = "http://schemas.xmlsoap.org/soap/encoding/";

    // SimpleTypesFactory simpleTypesFactory = null;

    public ComponentBuilder() {
        try {
            wsdlFactory = WSDLFactory.newInstance();
            // simpleTypesFactory = new SimpleTypesFactory();
        } catch (Exception e) {
            exceptionMessage = exceptionMessage + e.getMessage();
            ExceptionHandler.process(e);
        }
    }

    public ServiceInfo[] buildserviceinformation(ServiceInfo serviceinfo) throws Exception {
        // WSDLReader reader = wsdlFactory.newWSDLReader();
        // Definition def = reader.readWSDL(null, serviceinfo.getWsdlUri());
        ServiceDiscoveryHelper sdh;
//        if (serviceinfo.getAuthConfig() != null && serviceinfo.getWsdlUri().indexOf("http") == 0) {
//            sdh = new ServiceDiscoveryHelper(serviceinfo.getWsdlUri(), serviceinfo.getAuthConfig());
//        } else {
            sdh = new ServiceDiscoveryHelper(serviceinfo.getWsdlUri());
//        }
        Definition def = sdh.getDefinition();

        wsdlTypes = createSchemaFromTypes(def);

        collectAllXmlSchemaElement();

        collectAllXmlSchemaType();

        Collection<Service> services = def.getServices().values();
        if (services == null) return new ServiceInfo[]{}; 
        ServiceInfo[] value = new ServiceInfo[services.size()];
        int i = 0;
        for (Service service : services) {
            value[i] = populateComponent(serviceinfo, service);
            i++;
        }
        return value;
    }

    /**
     * DOC gcui Comment method "collectAllElement".
     *
     * @return
     */
    private void collectAllXmlSchemaElement() {
        for (int i = 0; i < wsdlTypes.size(); i++) {
            XmlSchema xmlSchema = (XmlSchema) (wsdlTypes.elementAt(i));
            if (xmlSchema == null) {
                continue;
            }
            Map<QName, XmlSchemaElement> elements = xmlSchema.getElements();
            Iterator<XmlSchemaElement> elementsItr = elements.values().iterator();
            while (elementsItr.hasNext()) {
                XmlSchemaElement xmlSchemaElement = elementsItr.next();
                allXmlSchemaElement.add(xmlSchemaElement);
            }
        }
    }

    /**
     * DOC gcui Comment method "collectAllXmlSchemaType".
     */
    private void collectAllXmlSchemaType() {
        for (int i = 0; i < wsdlTypes.size(); i++) {
            XmlSchema xmlSchema = (XmlSchema) (wsdlTypes.elementAt(i));
            if (xmlSchema == null) {
                continue;
            }
            //XmlSchemaObjectTable
            Map<QName, XmlSchemaType> xmlSchemaObjectTable = xmlSchema.getSchemaTypes();
            Iterator<XmlSchemaType> typesItr = xmlSchemaObjectTable.values().iterator();
            while (typesItr.hasNext()) {
                XmlSchemaType xmlSchemaType = typesItr.next();
                allXmlSchemaType.add(xmlSchemaType);
            }
        }

    }

    protected Vector<XmlSchema> createSchemaFromTypes(Definition wsdlDefinition) {
        Vector<XmlSchema> schemas = new Vector<XmlSchema>();
        org.w3c.dom.Element schemaElementt = null;
        Map importElement = null;
        List includeElement = null;
        if (wsdlDefinition.getTypes() != null) {
            Vector schemaExtElem = findExtensibilityElement(wsdlDefinition.getTypes().getExtensibilityElements(), "schema");
            for (int i = 0; i < schemaExtElem.size(); i++) {
                ExtensibilityElement schemaElement = (ExtensibilityElement) schemaExtElem.elementAt(i);
                if (schemaElement != null && schemaElement instanceof UnknownExtensibilityElement) {
                    schemaElementt = ((UnknownExtensibilityElement) schemaElement).getElement();

                    String documentBase = ((javax.wsdl.extensions.schema.Schema) schemaElement).getDocumentBaseURI();
                    XmlSchema schema = createschemafromtype(schemaElementt, wsdlDefinition, documentBase);
                    if (schema != null) {
                        schemas.add(schema);
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
                        Iterator keyIterator = importElement.keySet().iterator();
                        // while (keyIterator.hasNext()) {
                        // String key = keyIterator.next().toString();
                        // Vector importEle = (Vector) importElement.get(key);
                        // for (int j = 0; j < importEle.size(); j++) {
                        // com.ibm.wsdl.extensions.schema.SchemaImportImpl importValidate =
                        // (com.ibm.wsdl.extensions.schema.SchemaImportImpl) importEle
                        // .elementAt(j);
                        // if (importValidate.getSchemaLocationURI() == null) {
                        // importElement.remove(key);
                        // }
                        // }
                        // }
                        if (importElement.size() > 0) {
                            isHaveImport = true;
                        }
                        // validateImportUrlPath(importElement);
                    }

                    XmlSchema schema = createschemafromtype(schemaElementt, wsdlDefinition, documentBase);
                    if (schema != null) {
                        schemas.add(schema);
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
    private void findIncludesSchema(Definition wsdlDefinition, Vector schemas, List includeElement) {
        org.w3c.dom.Element schemaElementt;
        for (int i = 0; i < includeElement.size(); i++) {

            schemaElementt = ((com.ibm.wsdl.extensions.schema.SchemaReferenceImpl) includeElement.get(i)).getReferencedSchema()
                    .getElement();
            String documentBase = ((com.ibm.wsdl.extensions.schema.SchemaReferenceImpl) includeElement.get(i))
                    .getReferencedSchema().getDocumentBaseURI();
            XmlSchema schemaInclude = createschemafromtype(schemaElementt, wsdlDefinition, documentBase);
            if (schemaInclude != null) {
                schemas.add(schemaInclude);
                if (schemaInclude.getTargetNamespace() != null) {
                    schemaNames.add(schemaInclude.getTargetNamespace());
                }
            }
        }
    }

    private void findImportSchema(Definition wsdlDefinition, Vector schemas, Map importElement) {
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
                        schemas.add(schemaImport);
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

    private Vector findExtensibilityElement(List extensibilityElements, String elementType) {

        Vector elements = new Vector();
        if (extensibilityElements != null) {
            Iterator iter = extensibilityElements.iterator();
            while (iter.hasNext()) {
                ExtensibilityElement elment = (ExtensibilityElement) iter.next();
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

    private ServiceInfo populateComponent(ServiceInfo component, Service service) {
        ServiceInfo value = new ServiceInfo(component);
        QName qName = service.getQName();
        String namespace = qName.getNamespaceURI();
        String name = qName.getLocalPart();
        value.setServerName(name);
        value.setServerNameSpace(namespace);
        Map ports = service.getPorts();
        Iterator portIter = ports.values().iterator();
        while (portIter.hasNext()) {
            Port port = (Port) portIter.next();
            Binding binding = port.getBinding();
            if (port.getName() != null) {
                if (value.getPortNames() == null) {
                    value.setPortNames(new ArrayList<String>());
                }
                value.getPortNames().add(port.getName());
            }
            List operations = buildOperations(binding);
            Iterator operIter = operations.iterator();
            while (operIter.hasNext()) {
                OperationInfo operation = (OperationInfo) operIter.next();
                Vector addrElems = findExtensibilityElement(port.getExtensibilityElements(), "address");
                ExtensibilityElement element = (ExtensibilityElement) addrElems.elementAt(0);
                if (element != null && element instanceof SOAPAddress) {
                    SOAPAddress soapAddr = (SOAPAddress) element;
                    operation.setTargetURL(soapAddr.getLocationURI());
                }
                value.addOperation(operation);
            }
        }
        return value;
    }

    private List buildOperations(Binding binding) {
        List operationInfos = new ArrayList();

        List operations = binding.getBindingOperations();

        if (operations != null && !operations.isEmpty()) {

            Vector soapBindingElems = findExtensibilityElement(binding.getExtensibilityElements(), "binding");
            String style = "document"; // default

            ExtensibilityElement soapBindingElem = (ExtensibilityElement) soapBindingElems.elementAt(0);
            if (soapBindingElem != null && soapBindingElem instanceof SOAPBinding) {
                SOAPBinding soapBinding = (SOAPBinding) soapBindingElem;
                style = soapBinding.getStyle();
            }

            Iterator opIter = operations.iterator();

            while (opIter.hasNext()) {
                BindingOperation oper = (BindingOperation) opIter.next();
                Vector<ExtensibilityElement> operElems = findExtensibilityElement(oper.getExtensibilityElements(), "operation");
                for (ExtensibilityElement operElem : operElems) {
//                    ExtensibilityElement operElem = (ExtensibilityElement) operElems.elementAt(0);
                    if (operElem != null && operElem instanceof SOAPOperation) {
    
                        OperationInfo operationInfo = new OperationInfo(style);
                        buildOperation(operationInfo, oper);
                        operationInfos.add(operationInfo);
                    }
                }
            }
        }

        return operationInfos;
    }

    private OperationInfo buildOperation(OperationInfo operationInfo, BindingOperation bindingOper) {
        Operation oper = bindingOper.getOperation();
        operationInfo.setTargetMethodName(oper.getName());
        Vector operElems = findExtensibilityElement(bindingOper.getExtensibilityElements(), "operation");
        ExtensibilityElement operElem = (ExtensibilityElement) operElems.elementAt(0);
        if (operElem != null && operElem instanceof SOAPOperation) {
            SOAPOperation soapOperation = (SOAPOperation) operElem;
            operationInfo.setSoapActionURI(soapOperation.getSoapActionURI());
        }
        BindingInput bindingInput = bindingOper.getBindingInput();
        BindingOutput bindingOutput = bindingOper.getBindingOutput();
        Vector bodyElems = findExtensibilityElement(bindingInput.getExtensibilityElements(), "body");
        ExtensibilityElement bodyElem = (ExtensibilityElement) bodyElems.elementAt(0);

        if (bodyElem != null && bodyElem instanceof SOAPBody) {
            SOAPBody soapBody = (SOAPBody) bodyElem;
            List styles = soapBody.getEncodingStyles();
            String encodingStyle = null;
            if (styles != null) {
                encodingStyle = styles.get(0).toString();
            }
            if (encodingStyle == null) {
                encodingStyle = DEFAULT_SOAP_ENCODING_STYLE;
            }
            operationInfo.setEncodingStyle(encodingStyle.toString());
            operationInfo.setTargetObjectURI(soapBody.getNamespaceURI());
        }

        Input inDef = oper.getInput();
        if (inDef != null) {
            Message inMsg = inDef.getMessage();
            if (inMsg != null) {
                operationInfo.setInputMessageName(inMsg.getQName().getLocalPart());
                getParameterFromMessage(operationInfo, inMsg, 1);
                operationInfo.setInmessage(inMsg);
            }
        }

        Output outDef = oper.getOutput();
        if (outDef != null) {
            Message outMsg = outDef.getMessage();
            if (outMsg != null) {
                operationInfo.setOutputMessageName(outMsg.getQName().getLocalPart());
                getParameterFromMessage(operationInfo, outMsg, 2);
                operationInfo.setOutmessage(outMsg);
            }
        }

        return operationInfo;
    }

    private void getParameterFromMessage(OperationInfo operationInfo, Message msg, int manner) {

        // inOrOut = manner;
        List msgParts = msg.getOrderedParts(null);
        // msg.getQName();
        // Schema wsdlType = null;
        // XmlSchema xmlSchema = null;
        Iterator iter = msgParts.iterator();
        while (iter.hasNext()) {
            Part part = (Part) iter.next();
            String partName = part.getName();
            String partElement = null;
            if (part.getElementName() != null) {
                partElement = part.getElementName().getLocalPart();
            } else if (part.getTypeName() != null) {
                partElement = part.getTypeName().getLocalPart();
            }
            // add first parameter from message.
            ParameterInfo parameterRoot = new ParameterInfo();
            parameterRoot.setName(partName);
            if (manner == 1) {
                operationInfo.addInparameter(parameterRoot);
            } else {
                operationInfo.addOutparameter(parameterRoot);
            }
            // String targetnamespace = "";
            // ComplexType complexType = null;

            // for (int i = 0; i < wsdlTypes.size(); i++) {
            // xmlSchema = (XmlSchema) (wsdlTypes.elementAt(i));
            // if (xmlSchema == null) {
            // continue;
            // }
            // if (xmlSchema != null && xmlSchema.getTargetNamespace() != null) {
            // targetnamespace = xmlSchema.getTargetNamespace();
            // operationInfo.setNamespaceURI(targetnamespace);
            // }
            if (allXmlSchemaElement.size() > 0) {

                buildParameterFromElements(partElement, parameterRoot, manner);

            } else if (allXmlSchemaType.size() > 0) {

                buileParameterFromTypes(partElement, parameterRoot, manner);
            }
            //operationInfo.setWsdltype(wsdlTypes);
        }
    }

    private void buildParameterFromElements(String partElement, ParameterInfo parameterRoot, int ioOrRecursion) {
        // XmlSchemaObjectTable elements = xmlSchema.getElements();
        if (ioOrRecursion < 3) {
            parametersName.clear();
            parametersName.add(parameterRoot.getName());
        } else if (ioOrRecursion == 3) {
            parametersName.add(parameterRoot.getName());
        }
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
                                            parameterRoot, ioOrRecursion);
                                }
                            } else if (xmlSchemaElement.getSchemaTypeName() != null) {
                                String paraTypeName = xmlSchemaElement.getSchemaTypeName().getLocalPart();
                                if (paraTypeName != null) {
                                    parameterRoot.setType(paraTypeName);
                                    buileParameterFromTypes(paraTypeName, parameterRoot, ioOrRecursion);
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
                            buileParameterFromTypes(paraTypeName, parameterRoot, ioOrRecursion);
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
    private void buileParameterFromTypes(String paraType, ParameterInfo parameter, int ioOrRecursion) {
        if (ioOrRecursion < 3) {
            parametersName.clear();
            parametersName.add(parameter.getName());
        } else if (ioOrRecursion == 3) {
            parametersName.add(parameter.getName());
        }
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
                        buildParameterFromCollection(xmlSchemaObjectCollection, parameter, 3);
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
            ParameterInfo parameter, int ioOrRecursion) {
        // XmlSchemaSequence xmlSchemaSequence = (XmlSchemaSequence) xmlSchemaParticle;
        // XmlSchemaObjectCollection xmlSchemaObjectCollection = xmlSchemaSequence.getItems();
        for (XmlSchemaObjectBase xmlSchemaObject : xmlSchemaObjectCollection) {
            if (xmlSchemaObject instanceof XmlSchemaGroupParticle) {
                Collection<XmlSchemaObjectBase> items =
                    getXmlSchemaObjectsFromXmlSchemaGroupParticle(
                            (XmlSchemaGroupParticle) xmlSchemaObject);
                if (null != items && !items.isEmpty()) {
                    buildParameterFromCollection(items, parameter, ioOrRecursion);
                }
            } else if (xmlSchemaObject instanceof XmlSchemaAny) {
                ParameterInfo parameterSon = new ParameterInfo();
                parameterSon.setName("_content_");
                parameterSon.setParent(parameter);
                parameter.getParameterInfos().add(parameterSon);

            } else if (xmlSchemaObject instanceof XmlSchemaElement) {
                XmlSchemaElement xmlSchemaElement = (XmlSchemaElement) xmlSchemaObject;
                String elementName = xmlSchemaElement.getName();
                ParameterInfo parameterSon = new ParameterInfo();
                parameterSon.setName(elementName);
                parameterSon.setParent(parameter);
                Long min = xmlSchemaElement.getMinOccurs();
                Long max = xmlSchemaElement.getMaxOccurs();
                if (max - min > 1) {
                    parameterSon.setArraySize(-1);
                    parameterSon.setIndex("*");
                }
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
                        buileParameterFromTypes(elementTypeName, parameterSon, ioOrRecursion);
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
                                buildParameterFromCollection(childCollection, parameterSon, ioOrRecursion);
                            }
                        } else if (xmlSchemaElement.getSchemaTypeName() != null) {
                            String paraTypeName = xmlSchemaElement.getSchemaTypeName().getLocalPart();
                            if (paraTypeName != null && !isHave) {
                                parameter.setType(paraTypeName);
                                buileParameterFromTypes(paraTypeName, parameterSon, ioOrRecursion);
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
                        buildParameterFromElements(elementTypeName, parameterSon, ioOrRecursion);
                    }
                }

            } else if (xmlSchemaObject instanceof XmlSchemaAttribute) {
                XmlSchemaAttribute xmlSchemaAttribute = (XmlSchemaAttribute) xmlSchemaObject;
                String elementName = xmlSchemaAttribute.getName();
                ParameterInfo parameterSon = new ParameterInfo();
                parameterSon.setName(elementName);
                parameterSon.setParent(parameter);

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
                        buileParameterFromTypes(elementTypeName, parameterSon, ioOrRecursion);
                    }
                } else if (xmlSchemaAttribute.getTargetQName() != null) {
                    String refName = xmlSchemaAttribute.getTargetQName().getLocalPart();
                    parameterSon.setType(refName);
                    if (!isHave) {
                        buildParameterFromElements(refName, parameterSon, ioOrRecursion);

                    }
                }
            }
        }

    }

    public String getExceptionMessage() {
        return this.exceptionMessage;
    }
}
