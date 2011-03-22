/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.talend.ws.helper;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.apache.cxf.jaxb.JAXBUtils;
import org.apache.cxf.jaxb.JAXBUtils.IdentifierType;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.ws.commons.schema.XmlSchemaAny;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexContent;
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension;
import org.apache.ws.commons.schema.XmlSchemaComplexContentRestriction;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaContent;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.dom4j.dom.DOMElement;
import org.talend.ws.exception.LocalizedException;
import org.talend.ws.helper.conf.ServiceHelperConfiguration;
import org.talend.ws.helper.map.MapConverter;
import org.talend.ws.mapper.ClassMapper;
import org.talend.ws.mapper.EmptyMessageMapper;
import org.talend.ws.mapper.MapperFactory;
import org.talend.ws.mapper.MessageMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 *
 * @author rlamarche
 */
public class ServiceInvokerHelper implements ClassMapper {

    private ServiceDiscoveryHelper serviceDiscoveryHelper;

    private DynamicClientFactory dynamicClientFactory;

    private final String packagePrefix;

    private Map<String, String> namespacePackageMap;

    private Map<String, String> packageNamespaceMap;

    private Map<QName, Map<QName, Client>> clients;

    private List<String> bindingFiles;

    private Map<Message, MessageMapper> mappers;

    private MapperFactory mapperFactory;

    private ServiceHelperConfiguration configuration;

    protected ServiceInvokerHelper() {
        packagePrefix = "tmp" + (String.valueOf((new Random(Calendar.getInstance().getTimeInMillis())).nextInt()).substring(1));
        dynamicClientFactory = DynamicClientFactory.newInstance();
        namespacePackageMap = new HashMap<String, String>();
        packageNamespaceMap = new HashMap<String, String>();
        clients = new HashMap<QName, Map<QName, Client>>();
        mappers = new HashMap<Message, MessageMapper>();
    }

    public ServiceInvokerHelper(String wsdlUri) throws WSDLException, IOException, TransformerException, URISyntaxException {
        this(new ServiceDiscoveryHelper(wsdlUri));
    }

    public ServiceInvokerHelper(String wsdlUri, String tempPath) throws WSDLException, IOException, TransformerException,
            URISyntaxException {
        this(new ServiceDiscoveryHelper(wsdlUri, tempPath));
    }

    public ServiceInvokerHelper(String wsdlUri, ServiceHelperConfiguration configuration) throws WSDLException, IOException,
            TransformerException, URISyntaxException {
        this(new ServiceDiscoveryHelper(wsdlUri, configuration));
    }

    public ServiceInvokerHelper(String wsdlUri, ServiceHelperConfiguration configuration, String tempPath) throws WSDLException,
            IOException, TransformerException, URISyntaxException {
        this(new ServiceDiscoveryHelper(wsdlUri, configuration, tempPath), configuration);
    }

    public ServiceInvokerHelper(ServiceDiscoveryHelper serviceDiscoveryHelper, ServiceHelperConfiguration configuration) {
        this(serviceDiscoveryHelper);
        this.configuration = configuration;
    }

    public ServiceInvokerHelper(ServiceDiscoveryHelper serviceDiscoveryHelper) {
        this();
        this.serviceDiscoveryHelper = serviceDiscoveryHelper;
        Set<Map.Entry<String, String>> entrySet = serviceDiscoveryHelper.getDefinition().getNamespaces().entrySet();

        bindingFiles = new ArrayList<String>(entrySet.size());
        Set<String> namespaces = new HashSet<String>();
        for (Map.Entry<String, String> entry : entrySet) {
            if (namespaces.contains(entry.getValue())) {
                continue;
            }
            namespaces.add(entry.getValue());
            String packageName = packagePrefix + JAXBUtils.namespaceURIToPackage(entry.getValue());
            namespacePackageMap.put(entry.getValue(), packageName);
            packageNamespaceMap.put(packageName, entry.getValue());

            File f = org.apache.cxf.tools.util.JAXBUtils.getPackageMappingSchemaBindingFile(entry.getValue(), packageName);
            f.deleteOnExit();
            bindingFiles.add(f.getAbsolutePath());
        }
        // bchen , some targetnamespace in schema missing in definitions,code for bug 9900 (for bare webservice)
        XmlSchemaCollection xmlSchemaCollection = serviceDiscoveryHelper.getSchema();
        if (xmlSchemaCollection != null) {
            org.apache.ws.commons.schema.XmlSchema[] xs = xmlSchemaCollection.getXmlSchemas();
            if (xs != null) {
                for (org.apache.ws.commons.schema.XmlSchema x : xs) {
                    if (namespaces.contains(x.getTargetNamespace()) || x.getTargetNamespace() == null) {
                        continue;
                    }
                    namespaces.add(x.getTargetNamespace());
                    String packageName = packagePrefix + JAXBUtils.namespaceURIToPackage(x.getTargetNamespace());
                    namespacePackageMap.put(x.getTargetNamespace(), packageName);
                    packageNamespaceMap.put(packageName, x.getTargetNamespace());
                    File f = org.apache.cxf.tools.util.JAXBUtils.getPackageMappingSchemaBindingFile(x.getTargetNamespace(),
                            packageName);
                    f.deleteOnExit();
                    bindingFiles.add(f.getAbsolutePath());
                }
            }
        }
        // end bchen ,code for bug 9900
        mapperFactory = new MapperFactory(this, serviceDiscoveryHelper.getSchema());
    }

    public Client getClient(QName service, QName port) {
        Map<QName, Client> serviceClients = clients.get(service);
        if (serviceClients == null) {
            serviceClients = new HashMap<QName, Client>();
            clients.put(service, serviceClients);
        }

        if (serviceClients.get(port) == null) {
            serviceClients.put(port, createClient(service, port));
        }

        return serviceClients.get(port);
    }

    protected Client createClient(QName service, QName port) {
        // bchen bug for 8674
        Client client = dynamicClientFactory.createClient(serviceDiscoveryHelper.getLocalWsdlUri(), service, Thread
                .currentThread().getContextClassLoader(), port, bindingFiles);
        // end
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        if (configuration != null) {
            configuration.configureHttpConduit(conduit);
        }

        return client;
    }

    private MessageMapper getMessageMapper(Message message) throws LocalizedException {

        MessageMapper messageMapper = mappers.get(message);
        if (messageMapper == null) {
            messageMapper = createMessageMapper(message);
            mappers.put(message, messageMapper);
        }

        return messageMapper;
    }

    private MessageMapper createMessageMapper(Message message) throws LocalizedException {
        return mapperFactory.createMessageMapper(message);
    }

    protected Map<String, Object> invoke(Client client, Operation operation, Object value) throws Exception, LocalizedException {

        Input input = operation.getInput();
        Output output = operation.getOutput();
        MessageMapper inMessageMapper = null;
        MessageMapper outMessageMapper = null;

        BindingOperationInfo bindingOperationInfo = client.getEndpoint().getEndpointInfo().getBinding().getOperation(
                new QName(client.getEndpoint().getService().getName().getNamespaceURI(), operation.getName()));
        if (input != null) {
            inMessageMapper = getMessageMapper(input.getMessage());
        } else {
            inMessageMapper = new EmptyMessageMapper();
        }
        if (output != null) {
            outMessageMapper = getMessageMapper(output.getMessage());
        } else {
            outMessageMapper = new EmptyMessageMapper();
        }
        if (bindingOperationInfo.isUnwrappedCapable()) {
            inMessageMapper.setUnwrapped(true);
            outMessageMapper.setUnwrapped(true);
        }

        Object[] retParams;
        if (value != null) {
            Object[] params = inMessageMapper.convertToParams(value);
            retParams = client.invoke(operation.getName(), params);
        } else {
            retParams = client.invoke(operation.getName()/*, new Object[]{new DOMSource(new DOMElement("<req>do something more</req>"))}*/);
        }

        Map<String, Object> retValues = outMessageMapper.convertToValue(retParams);

        return retValues;
    }

    public String invoke(QName serviceName, QName portName,
    		String operationName, String payload)
    		throws SOAPFaultException, Exception {

    	Source request = new javax.xml.transform.stream.StreamSource(
    			new java.io.ByteArrayInputStream(payload.getBytes()));

    	Source response = invoke(serviceName, portName, operationName, request);

    	if (null == response) {
    		return null;
    	}

    	java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    	javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(
    			response, new javax.xml.transform.stream.StreamResult(baos));

    	return new String(baos.toByteArray());
    }

    public org.dom4j.Document invoke(QName serviceName, QName portName,
    		String operationName, org.dom4j.Document payload)
    		throws SOAPFaultException, Exception {

    	Source requestSource = new org.dom4j.io.DocumentSource(payload);

    	Source responseSource = invoke(serviceName, portName, operationName, requestSource);

    	if (null == responseSource) {
    		return null;
    	}

    	org.dom4j.io.DocumentResult docResult = new org.dom4j.io.DocumentResult();
    	javax.xml.transform.TransformerFactory.newInstance()
    		.newTransformer().transform(responseSource, docResult);
    	org.dom4j.Document response = docResult.getDocument();

    	return response;
    }

    public Source invoke(QName serviceName, QName portName, String operationName, Source payload)
    		throws SOAPFaultException, Exception {

    	if (serviceName == null) {
            throw new IllegalArgumentException("serviceName is mandatory.");
        }
        Definition definition = serviceDiscoveryHelper.getDefinition();
		Service service = definition.getService(serviceName);
        if (service == null) {
            throw new IllegalArgumentException("Service " + serviceName.toString() + " does not exists.");
        }

        if (portName == null) {
            throw new IllegalArgumentException("portName is mandatory.");
        }
        Port port = service.getPort(portName.getLocalPart());
        if (port == null) {
            throw new IllegalArgumentException("Port " + portName + " does not exists for service " + serviceName.toString()
                    + ".");
        }

        if (operationName == null) {
        	throw new IllegalArgumentException("operationName is mandatory.");
        }
        Operation operation = port.getBinding().getPortType().getOperation(operationName, null, null);
        if (operation == null) {
        	throw new IllegalArgumentException("Operation " + operationName + " does not exists for service "
        			+ serviceName.toString() + ".");
        }

    	URL wsdlUrl = new URL(definition.getDocumentBaseURI());
    	javax.xml.ws.Service service1 = javax.xml.ws.Service.create(wsdlUrl, serviceName);
    	javax.xml.ws.Dispatch<javax.xml.transform.Source> disp = service1.createDispatch(portName, javax.xml.transform.Source.class, javax.xml.ws.Service.Mode.PAYLOAD);
    	java.util.Map requestContext = disp.getRequestContext();
    	if(requestContext == null) {
    		throw new Exception("setSOAPActionURI:getRequestContext() returned null");
    	} else {
    		BindingOperation bindingOperation = port.getBinding().getBindingOperation(operationName, null, null);
    		List ele = bindingOperation.getExtensibilityElements();
    		for (Object obj: ele) {
    			if (obj instanceof SOAPOperation) {
    				SOAPOperation soapOperation = (SOAPOperation) obj;
    	    		requestContext.put(disp.SOAPACTION_URI_PROPERTY, soapOperation.getSoapActionURI());
    	    		break;
    			}
    			throw new Exception("SoapAction URI was not found for operation "+operationName);
    		}
    	}

    	if (null != operation.getOutput()) {
    		Source response = disp.invoke(payload);
    		return response;
    	} else {
    		disp.invokeOneWay(payload);
    		return null;
    	}
    }

    /**
     * Invoke a service with a simple map of parametes (address.city=LYON, address.zipCode=69003, etc ...) Returned
     * results are also in this format
     *
     * @param serviceName
     * @param portName
     * @param operationName
     * @param params
     * @return
     * @throws java.lang.Exception
     * @throws org.talend.ws.exception.LocalizedException
     */
    public Map<String, Object> invokeSimple(QName serviceName, QName portName, String operationName, Object params)
            throws Exception, LocalizedException {
//        if (params instanceof Map) {
//            params = MapConverter.mapToDeepMap((Map<String, Object>) params);
//        }
//
//        Map<String, Object> result = invoke(serviceName, portName, operationName, params);
//
//        return MapConverter.deepMapToMap(result);
    	throw new IllegalStateException("invokeSimple method deprecated");
    }

    protected String getClassNameForType(XmlSchemaType schemaType) {
        StringBuilder sb = new StringBuilder();
        sb.append(getPackageForNamespaceURI(schemaType.getQName().getNamespaceURI()));
        sb.append(".");
        sb.append(getClassNameForTypeName(schemaType.getName()));
        String className = sb.toString();

        return className;
    }

    protected String getPackageForNamespaceURI(String ns) {
        return namespacePackageMap.get(ns);
    }

    protected String getNamespaceURIForPackage(String packageName) {
        return packageNamespaceMap.get(packageName);
    }

    protected String getClassNameForTypeName(String typeName) {
        return toCamelCase(org.apache.cxf.jaxb.JAXBUtils.nameToIdentifier(typeName, IdentifierType.CLASS), true);
    }

    public Class<?> getClassForType(XmlSchemaType xmlSchemaType) {
        String className = getClassNameForType(xmlSchemaType);
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            return checkClass(xmlSchemaType, clazz);

        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    // if class name conflict, the former will be unchanged "className" ,the latter will be add count++ in the end of
    // the name "className1,className2"
    private Class checkClass(XmlSchemaType xmlSchemaType, Class<?> clazz) {
        // only check for complex type.
        if (xmlSchemaType instanceof XmlSchemaComplexType) {
            XmlSchemaComplexType xmlSchemaComplexType = (XmlSchemaComplexType) xmlSchemaType;
            XmlSchemaSequence xmlSchemaSequence = null;
            XmlSchemaComplexContent xmlSchemaComplexContent = (XmlSchemaComplexContent) xmlSchemaComplexType.getContentModel();
            if (xmlSchemaComplexContent != null) {
                XmlSchemaContent xmlSchemaContent = xmlSchemaComplexContent.getContent();
                if (xmlSchemaContent instanceof XmlSchemaComplexContentExtension) {
                    XmlSchemaComplexContentExtension xmlSchemaComplexContentExtension = (XmlSchemaComplexContentExtension) xmlSchemaContent;
                    if (xmlSchemaComplexContentExtension.getParticle() instanceof XmlSchemaSequence) {
                        xmlSchemaSequence = (XmlSchemaSequence) xmlSchemaComplexContentExtension.getParticle();
                    }
                } else if (xmlSchemaContent instanceof XmlSchemaComplexContentRestriction) {
                    throw new IllegalArgumentException("XmlSchemaComplexContentRestriction is not yet supported.");
                } else {
                    throw new IllegalArgumentException("Invalid XmlSchemaContent for a XmlSchemaComplexContent.");
                }
            } else {
                XmlSchemaParticle xmlSchemaParticle = xmlSchemaComplexType.getParticle();
                if (xmlSchemaParticle instanceof XmlSchemaSequence) {
                    xmlSchemaSequence = (XmlSchemaSequence) xmlSchemaParticle;
                }
            }
            if (xmlSchemaSequence != null) {
                Class<?> finalClazz = null;
                boolean allCorrect = false;
                int tempSuffix = 0;
                // bug13001 by bchen, deal with choice in sequence
                // Iterator<XmlSchemaObject> iterator = MapperFactory.getXmlSchemaObjectIter(xmlSchemaSequence);
                Iterator<XmlSchemaObject> iterator = xmlSchemaSequence.getItems().getIterator();
                while (!allCorrect) {
                    // if (iterator == null) {// bug 14053 created by bchen, handle <any/> tag
                    // return clazz;
                    // }
                    if (!iterator.hasNext()) {
                        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
                        if (descriptors.length == 1 && "class".equals(descriptors[0].getName())) {
                            allCorrect = true;
                        }
                    }
                    String propertyName = "";
                    while (iterator.hasNext()) {
                        XmlSchemaObject xmlSchemaObject = iterator.next();
                        if (xmlSchemaObject instanceof XmlSchemaElement) {
                            XmlSchemaElement xmlSchemaElement = (XmlSchemaElement) xmlSchemaObject;
                            propertyName = xmlSchemaElement.getName();
                            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
                            for (PropertyDescriptor descriptor : descriptors) {
                                if (propertyName.equalsIgnoreCase(descriptor.getName())) {
                                    allCorrect = true;
                                    break;
                                } else {
                                    allCorrect = false;
                                }
                            }
                        } else if (xmlSchemaObject instanceof XmlSchemaChoice) {
                            XmlSchemaChoice xmlSchemaChoice = (XmlSchemaChoice) xmlSchemaObject;
                            XmlSchemaObjectCollection xmlSchemaObjectCollection = xmlSchemaChoice.getItems();
                            Iterator<XmlSchemaObject> choiceIterator = xmlSchemaObjectCollection.getIterator();
                            while (choiceIterator.hasNext()) {
                                XmlSchemaObject choiceXmlSchemaObject = choiceIterator.next();
                                if (choiceXmlSchemaObject instanceof XmlSchemaElement) {
                                    XmlSchemaElement xmlSchemaElement = (XmlSchemaElement) choiceXmlSchemaObject;
                                    propertyName = xmlSchemaElement.getName();
                                    PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
                                    for (PropertyDescriptor descriptor : descriptors) {
                                        if (propertyName.equalsIgnoreCase(descriptor.getName())) {
                                            allCorrect = true;
                                            break;
                                        } else {
                                            allCorrect = false;
                                        }
                                    }
                                    if (allCorrect) {// all correct? or one of them correct
                                        break;
                                    }
                                }
                            }
                        } else if (xmlSchemaObject instanceof XmlSchemaAny) {
                            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
                            for (PropertyDescriptor descriptor : descriptors) {
                                if ("any".equalsIgnoreCase(descriptor.getName())
                                        || "content".equalsIgnoreCase(descriptor.getName())) {
                                    allCorrect = true;
                                    break;
                                } else {
                                    allCorrect = false;
                                }
                            }
                        }
                        if (!allCorrect) {
                            break;
                        }
                    }

                    if (allCorrect) {
                        finalClazz = clazz;
                    } else {
                        tempSuffix++;
                        try {
                            clazz = Thread.currentThread().getContextClassLoader().loadClass(clazz.getName() + tempSuffix);
                        } catch (ClassNotFoundException e) {
                            throw new IllegalArgumentException("Unable to get propertyDescriptor for bean " + clazz.getName()
                                    + " and property " + propertyName);
                        }
                    }
                }
                if (finalClazz == null) {
                    throw new IllegalArgumentException("Unable to get propertyDescriptor for bean " + clazz.getName());
                }
                return finalClazz;
            } else {
                return clazz;
            }
        } else {
            return clazz;
        }
    }

    public XmlSchemaType getTypeForClass(Class<?> clazz) {
        if (clazz.isAnnotationPresent(XmlType.class)) {
            XmlType type = clazz.getAnnotation(XmlType.class);
            XmlSchema schema = clazz.getPackage().getAnnotation(XmlSchema.class);
            QName qname = new QName(schema.namespace(), type.name());

            return serviceDiscoveryHelper.getSchema().getTypeByQName(qname);
        } else {
            QName type = MapperFactory.javaTypeToBuiltInType(clazz.getName());
            if (type != null) {
                return serviceDiscoveryHelper.getSchema().getTypeByQName(type);
            } else {
                throw new IllegalArgumentException("Unmapped class : " + clazz.getName());
            }
        }
    }

    public ServiceDiscoveryHelper getServiceDiscoveryHelper() {
        return serviceDiscoveryHelper;
    }

    private String toCamelCase(String value, boolean startWithLowerCase) {
        String[] strings = StringUtils.split(value, "_");
        for (int i = startWithLowerCase ? 1 : 0; i < strings.length; i++) {
            strings[i] = StringUtils.capitalize(strings[i]);
        }
        return StringUtils.join(strings);
    }
}
