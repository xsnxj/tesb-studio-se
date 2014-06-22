package org.talend.repository.services.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaReference;
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
import org.talend.repository.services.model.services.ParameterInfo;
import org.w3c.dom.Element;

import com.ibm.wsdl.extensions.schema.SchemaImportImpl;

public class SchemaUtil {

    private List<XmlSchemaElement> allXmlSchemaElement = new ArrayList<XmlSchemaElement>();

    private List<XmlSchemaType> allXmlSchemaType = new ArrayList<XmlSchemaType>();

    private HashMap<XmlSchema, byte[]> schemas;

    private List<String> parametersName = new ArrayList<String>();

    public SchemaUtil(Definition wsdlDefinition) {
        schemas = new HashMap<XmlSchema, byte[]>();
        org.w3c.dom.Element schemaElementt = null;
        Map importElement = null;
        if (wsdlDefinition.getTypes() != null) {
            Collection<ExtensibilityElement> schemaExtElem = findExtensibilityElement(wsdlDefinition.getTypes()
                    .getExtensibilityElements(), "schema");
            for (ExtensibilityElement schemaElement : schemaExtElem) {
                if (schemaElement != null && schemaElement instanceof UnknownExtensibilityElement) {
                    schemaElementt = ((UnknownExtensibilityElement) schemaElement).getElement();

                    String documentBase = ((Schema) schemaElement).getDocumentBaseURI();
                    XmlSchema schema = createschemafromtype(schemaElementt, wsdlDefinition, documentBase);
                    if (schema != null) {
                        addSchema(schemas, schema);
                    }
                    importElement = ((Schema) schemaElement).getImports();
                    if (importElement != null && importElement.size() > 0) {
                        findImportSchema(wsdlDefinition, schemas, importElement);
                    }
                }

                if (schemaElement != null && schemaElement instanceof Schema) {
                    schemaElementt = ((Schema) schemaElement).getElement();
                    String documentBase = ((Schema) schemaElement).getDocumentBaseURI();
                    Boolean isHaveImport = false;
                    importElement = ((Schema) schemaElement).getImports();
                    if (importElement != null && importElement.size() > 0) {
                        if (importElement.size() > 0) {
                            isHaveImport = true;
                        }
                    }

                    XmlSchema schema = createschemafromtype(schemaElementt, wsdlDefinition, documentBase);
                    if (schema != null) {
                        addSchema(schemas, schema);
                    }

                    if (isHaveImport) {
                        findImportSchema(wsdlDefinition, schemas, importElement);
                    }
                }
            }

        }
    }

    private static Collection<ExtensibilityElement> findExtensibilityElement(List<ExtensibilityElement> extensibilityElements,
            String elementType) {
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

    private static XmlSchema createschemafromtype(Element schemaElement, Definition wsdlDefinition, String documentBase) {
        if (schemaElement == null) {
            return null;
        }
        XmlSchema xmlSchema = null;
        XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();
        xmlSchemaCollection.setBaseUri(documentBase);
        xmlSchema = xmlSchemaCollection.read(schemaElement);
        return xmlSchema;
    }

    private void addSchema(Map<XmlSchema, byte[]> map, final XmlSchema schema) {
        try {
            final ByteArrayOutputStream fos = new ByteArrayOutputStream();
            // one more crutch for the disabled
            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<Object> task = new Callable<Object>() {

                public Object call() {
                    try {
                        schema.write(fos); // this method hangs when using invalid wsdl.
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            Future<Object> future = executor.submit(task);
            try {
                future.get(30, TimeUnit.SECONDS);
            } catch (TimeoutException ex) {
                // handle the timeout
            } catch (InterruptedException e) {
                // handle the interrupts
            } catch (ExecutionException e) {
                // handle other exceptions
            } finally {
                future.cancel(true); // may or may not desire this
            }
            fos.close();
            map.put(schema, fos.toByteArray());
            allXmlSchemaElement.addAll(schema.getElements().values());
            allXmlSchemaType.addAll(schema.getSchemaTypes().values());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void findImportSchema(Definition wsdlDefinition, Map<XmlSchema, byte[]> schemas, Map importElement) {
        findImportSchema(wsdlDefinition, schemas, importElement, new ArrayList<String>());
    }

    private void findImportSchema(Definition wsdlDefinition, Map<XmlSchema, byte[]> schemas, Map importElement,
            Collection<String> documentBaseList) {
        Iterator keyIterator = importElement.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next().toString();
            Vector importEle = (Vector) importElement.get(key);

            for (int i = 0; i < importEle.size(); i++) {
                Map recurseImport = null;
                SchemaImportImpl schemaImport = (SchemaImportImpl) importEle.elementAt(i);
                SchemaImportImpl importImpl = schemaImport;
                boolean isHaveImport = false;
                if (importImpl.getReferencedSchema() != null) {
                    Element refSchema = importImpl.getReferencedSchema().getElement();
                    String documentBase = importImpl.getReferencedSchema().getDocumentBaseURI();

                    if (schemaImport != null) {
                        if (schemaImport.getReferencedSchema() != null) {
                            recurseImport = schemaImport.getReferencedSchema().getImports();
                            if (recurseImport != null && recurseImport.size() > 0 && !documentBaseList.contains(documentBase)) {
                                isHaveImport = true;
                                documentBaseList.add(documentBase);
                            }
                        }
                    }

                    XmlSchema importedSchema = createschemafromtype(refSchema, wsdlDefinition, documentBase);
                    if (importedSchema != null) {
                        addSchema(schemas, importedSchema);
                    }
                }

                if (isHaveImport) {
                    findImportSchema(wsdlDefinition, schemas, recurseImport);
                }

                if (schemaImport != null) {
                    if (schemaImport.getReferencedSchema() != null) {
                        @SuppressWarnings("unchecked")
                        List<SchemaReference> includeElements = schemaImport.getReferencedSchema().getIncludes();
                        findIncludesSchema(wsdlDefinition, schemas, includeElements);
                    }
                }

            }
        }
    }

    private void findIncludesSchema(Definition wsdlDefinition, Map<XmlSchema, byte[]> schemas,
            List<SchemaReference> includeElements) {
        if (includeElements == null || includeElements.size() == 0) {
            return;
        }
        for (SchemaReference schemaReference : includeElements) {
            Element schemaElement = schemaReference.getReferencedSchema().getElement();
            String documentBase = schemaReference.getReferencedSchema().getDocumentBaseURI();
            XmlSchema schemaInclude = createschemafromtype(schemaElement, wsdlDefinition, documentBase);
            if (schemaInclude != null) {
                addSchema(schemas, schemaInclude);
            }
        }
    }

    private byte[] getSchema(Message message) {
        for (Part part : (Collection<Part>) message.getParts().values()) {
            QName elementQname = part.getElementName();
            // if element is missing - try to get type attribute
            if (null == elementQname) {
                elementQname = part.getTypeName();
            }
            // it's possible if type also will be null. in this case just return null
            if (null == elementQname) {
                return null;
            }
            for (XmlSchema schema : schemas.keySet()) {
                for (XmlSchemaElement element : schema.getElements().values()) {
                    if (element.getName().equals(elementQname.getLocalPart())) {// TODO: check namespaces too
                        return schemas.get(schema);
                    }
                }
            }
        }
        return null;
    }

    public ParameterInfo getParameterFromMessage(Message msg) {
        List<Part> msgParts = msg.getOrderedParts(null);
        if (msgParts.size() != 1) {
            // TODO: warn user
        }
        ParameterInfo parameterRoot = new ParameterInfo();
        for (Part part : msgParts) {
            String partName = part.getName();
            String partElement = null;
            if (part.getElementName() != null) {
                partElement = part.getElementName().getLocalPart();
            } else if (part.getTypeName() != null) {
                partElement = part.getTypeName().getLocalPart();
            }
            // add first parameter from message.
            parameterRoot.setName(partElement);
            parameterRoot.setSchema(getSchema(msg));
            if (allXmlSchemaElement.size() > 0) {
                buildParameterFromElements(partElement, parameterRoot);
            } else if (allXmlSchemaType.size() > 0) {
                buildParameterFromTypes(partElement, parameterRoot);
            }
        }
        return parameterRoot;
    }

    private void buildParameterFromElements(String partElement, ParameterInfo parameterRoot) {
        String parameterName = parameterRoot.getName();
        if (parameterName == null) {
        	return;
        }
		parametersName.add(parameterName);

        Iterator<XmlSchemaElement> elementsItr = allXmlSchemaElement.iterator();
        if (partElement != null) {
            while (elementsItr.hasNext()) {
                XmlSchemaElement xmlSchemaElement = elementsItr.next();
                if (partElement.equals(xmlSchemaElement.getName())) {
                    parameterRoot.setNameSpace(xmlSchemaElement.getQName().getNamespaceURI());
                    // ParameterInfo parameter = new ParameterInfo();
                    // parameter.setName(partName);
                    if (xmlSchemaElement.getSchemaType() != null) {
                        if (xmlSchemaElement.getSchemaType() instanceof XmlSchemaComplexType) {
                            XmlSchemaComplexType xmlElementComplexType = (XmlSchemaComplexType) xmlSchemaElement.getSchemaType();
                            XmlSchemaParticle xmlSchemaParticle = xmlElementComplexType.getParticle();
                            if (xmlSchemaParticle instanceof XmlSchemaGroupParticle) {
                                Collection<XmlSchemaObjectBase> xmlSchemaObjectCollection = getXmlSchemaObjectsFromXmlSchemaGroupParticle((XmlSchemaGroupParticle) xmlSchemaParticle);
                                if (xmlSchemaObjectCollection != null) {
                                    buildParameterFromCollection(xmlSchemaObjectCollection, parameterRoot);
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
                parameter.setNameSpace(type.getQName().getNamespaceURI());
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
                                xmlSchemaObjectCollection = getXmlSchemaObjectsFromXmlSchemaGroupParticle((XmlSchemaGroupParticle) xmlSchemaParticle);
                            }
                        } else if (obj instanceof XmlSchemaComplexContentRestriction) {
                            XmlSchemaComplexContentRestriction xsccr = (XmlSchemaComplexContentRestriction) obj;
                            List<XmlSchemaAttributeOrGroupRef> attrs = xsccr.getAttributes();
                            if (null != attrs && !attrs.isEmpty()) {
                                xmlSchemaObjectCollection = new ArrayList<XmlSchemaObjectBase>(attrs);
                            }
                        }
                    } else if (xmlSchemaParticle instanceof XmlSchemaGroupParticle) {
                        xmlSchemaObjectCollection = getXmlSchemaObjectsFromXmlSchemaGroupParticle((XmlSchemaGroupParticle) xmlSchemaParticle);
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

    private Collection<XmlSchemaObjectBase> getXmlSchemaObjectsFromXmlSchemaGroupParticle(XmlSchemaGroupParticle xmlSchemaParticle) {
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

    private void buildParameterFromCollection(Collection<XmlSchemaObjectBase> xmlSchemaObjectCollection, ParameterInfo parameter) {
        // XmlSchemaSequence xmlSchemaSequence = (XmlSchemaSequence) xmlSchemaParticle;
        // XmlSchemaObjectCollection xmlSchemaObjectCollection = xmlSchemaSequence.getItems();
        for (XmlSchemaObjectBase xmlSchemaObject : xmlSchemaObjectCollection) {
            if (xmlSchemaObject instanceof XmlSchemaGroupParticle) {
                Collection<XmlSchemaObjectBase> items = getXmlSchemaObjectsFromXmlSchemaGroupParticle((XmlSchemaGroupParticle) xmlSchemaObject);
                if (null != items && !items.isEmpty()) {
                    buildParameterFromCollection(items, parameter);
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
                    if (!isHave && !isJavaBasicType(elementTypeName)) {
                        buildParameterFromTypes(elementTypeName, parameterSon);
                    }

                } else if (xmlSchemaElement.getSchemaType() != null) {
                    if (xmlSchemaElement.getSchemaType() instanceof XmlSchemaComplexType) {
                        XmlSchemaComplexType xmlElementComplexType = (XmlSchemaComplexType) xmlSchemaElement.getSchemaType();
                        XmlSchemaParticle xmlSchemaParticle = xmlElementComplexType.getParticle();
                        if (xmlSchemaParticle instanceof XmlSchemaGroupParticle) {
                            Collection<XmlSchemaObjectBase> childCollection = getXmlSchemaObjectsFromXmlSchemaGroupParticle((XmlSchemaGroupParticle) xmlSchemaParticle);
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
                    if (!isHave && !isJavaBasicType(elementTypeName)) {
                        buildParameterFromElements(elementTypeName, parameterSon);
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
                    if (!isHave && !isJavaBasicType(elementTypeName)) {
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

    public static Boolean isJavaBasicType(String typeName) {
        Boolean isJavaBasicType = false;
        if (typeName == null) {
            return false;
        }
        if ("String".equalsIgnoreCase(typeName)) {
            isJavaBasicType = true;
        } else if ("int".equalsIgnoreCase(typeName)) {
            isJavaBasicType = true;
        } else if ("long".equalsIgnoreCase(typeName)) {
            isJavaBasicType = true;
        } else if ("double".equalsIgnoreCase(typeName)) {
            isJavaBasicType = true;
        } else if ("float".equalsIgnoreCase(typeName)) {
            isJavaBasicType = true;
        } else if ("char".equalsIgnoreCase(typeName)) {
            isJavaBasicType = true;
        }

        return isJavaBasicType;

    }

    public HashMap<XmlSchema, byte[]> getSchemas() {
        return schemas;
    }

}
