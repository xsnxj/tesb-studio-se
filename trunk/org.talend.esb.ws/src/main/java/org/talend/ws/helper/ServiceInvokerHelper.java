/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.talend.ws.helper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
//import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

//import org.apache.cxf.endpoint.Client;
//import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.apache.cxf.jaxb.JAXBUtils;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.talend.ws.helper.conf.ServiceHelperConfiguration;
//import org.talend.ws.mapper.MapperFactory;
//import org.talend.ws.mapper.MessageMapper;

/**
 *
 * @author rlamarche
 */
public class ServiceInvokerHelper {

    private static final String HTTP_PROXY_PASSWORD = "http.proxyPassword";
    private static final String HTTP_PROXY_USER = "http.proxyUser";
    private static final String HTTP_PROXY_PORT = "http.proxyPort";
    private static final String HTTP_PROXY_HOST = "http.proxyHost";
    private static final QName LOCAL_OVERRIDE_QNAME = QName.valueOf("{local}override");

    private ServiceDiscoveryHelper serviceDiscoveryHelper;

//    private DynamicClientFactory dynamicClientFactory;

    private final String packagePrefix;

    private Map<String, String> namespacePackageMap;

    private Map<String, String> packageNamespaceMap;

//    private Map<QName, Map<QName, Client>> clients;

    private List<String> bindingFiles;

//    private Map<Message, MessageMapper> mappers;

//    private MapperFactory mapperFactory;

    private ServiceHelperConfiguration configuration;

    protected ServiceInvokerHelper() {
        packagePrefix = "tmp" + (String.valueOf((new Random(Calendar.getInstance().getTimeInMillis())).nextInt()).substring(1));
//        dynamicClientFactory = DynamicClientFactory.newInstance();
        namespacePackageMap = new HashMap<String, String>();
        packageNamespaceMap = new HashMap<String, String>();
//        clients = new HashMap<QName, Map<QName, Client>>();
//        mappers = new HashMap<Message, MessageMapper>();
    }

    public ServiceInvokerHelper(String wsdlUri)
            throws WSDLException, IOException, TransformerException, URISyntaxException {
        this(new ServiceDiscoveryHelper(wsdlUri));
    }

    public ServiceInvokerHelper(String wsdlUri, String tempPath)
            throws WSDLException, IOException, TransformerException, URISyntaxException {
        this(new ServiceDiscoveryHelper(wsdlUri, tempPath));
    }

    public ServiceInvokerHelper(String wsdlUri, ServiceHelperConfiguration configuration)
            throws WSDLException, IOException, TransformerException, URISyntaxException {
        this(new ServiceDiscoveryHelper(wsdlUri, configuration));
    }

    public ServiceInvokerHelper(String wsdlUri, ServiceHelperConfiguration configuration, String tempPath)
            throws WSDLException, IOException, TransformerException, URISyntaxException {
        this(new ServiceDiscoveryHelper(wsdlUri, configuration, tempPath), configuration);
    }

    public ServiceInvokerHelper(ServiceDiscoveryHelper serviceDiscoveryHelper, ServiceHelperConfiguration configuration) {
        this(serviceDiscoveryHelper);
        this.configuration = configuration;
    }

    public ServiceInvokerHelper(ServiceDiscoveryHelper serviceDiscoveryHelper) {
        this();
        this.serviceDiscoveryHelper = serviceDiscoveryHelper;
        @SuppressWarnings("unchecked")
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
                    File f = org.apache.cxf.tools.util.JAXBUtils.getPackageMappingSchemaBindingFile(
                            x.getTargetNamespace(), packageName);
                    f.deleteOnExit();
                    bindingFiles.add(f.getAbsolutePath());
                }
            }
        }
    }


    public org.dom4j.Document invoke(QName serviceName, QName portName,
            String operationName, org.dom4j.Document payload, String overrideEndpoint)
            throws SOAPFaultException, Exception {

        Source requestSource = new org.dom4j.io.DocumentSource(payload);

        Source responseSource = invoke(serviceName, portName, operationName, requestSource, overrideEndpoint);

        if (null == responseSource) {
            return null;
        }

        org.dom4j.io.DocumentResult docResult = new org.dom4j.io.DocumentResult();
        TransformerFactory.newInstance().newTransformer().transform(responseSource, docResult);
        org.dom4j.Document response = docResult.getDocument();

        return response;
    }

    public Source invoke(QName serviceName, QName portName, String operationName, Source payload, String overrideEndpoint)
            throws SOAPFaultException, Exception {

        if (serviceName == null) {
            throw new IllegalArgumentException(
                    "serviceName is mandatory.");
        }
        Definition definition = serviceDiscoveryHelper.getDefinition();
        Service service = definition.getService(serviceName);
        if (service == null) {
            throw new IllegalArgumentException(
                    "Service " + serviceName + " does not exists.");
        }

        if (portName == null) {
            throw new IllegalArgumentException(
                    "portName is mandatory.");
        }
        Port port = service.getPort(portName.getLocalPart());
        if (port == null) {
            throw new IllegalArgumentException(
                    "Port " + portName + " does not exists for service " + serviceName + ".");
        }

        if (operationName == null) {
            throw new IllegalArgumentException(
                    "operationName is mandatory.");
        }
        Operation operation = port.getBinding().getPortType().getOperation(operationName, null, null);
        if (operation == null) {
            throw new IllegalArgumentException(
                    "Operation " + operationName + " does not exists for service " + serviceName + ".");
        }

        URL wsdlUrl = new URL(serviceDiscoveryHelper.getLocalWsdlUri());
        javax.xml.ws.Service service1 = javax.xml.ws.Service.create(wsdlUrl, serviceName);
        QName dipatchPortName;
        if (null != overrideEndpoint) {
            service1.addPort(LOCAL_OVERRIDE_QNAME, SOAPBinding.SOAP11HTTP_BINDING, overrideEndpoint);
            dipatchPortName = LOCAL_OVERRIDE_QNAME;
        } else {
            dipatchPortName = portName;
        }
        javax.xml.ws.Dispatch<Source> disp = service1.createDispatch(dipatchPortName,
                        Source.class, javax.xml.ws.Service.Mode.PAYLOAD);
        java.util.Map<String, Object> requestContext = disp.getRequestContext();
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
                //throw new Exception("SoapAction URI was not found for operation "+operationName);
            }
        }

        if (configuration.isBasicAuth()) {
            requestContext.put(BindingProvider.USERNAME_PROPERTY, configuration.getUsername());
            requestContext.put(BindingProvider.PASSWORD_PROPERTY, configuration.getPassword());
        } else if (configuration.isTokenAuth()) {
            Map<String, Object> wssProps = new HashMap<String, Object>();
            wssProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
            wssProps.put(WSHandlerConstants.USER, configuration.getUsername());
            wssProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
            wssProps.put(WSHandlerConstants.PW_CALLBACK_REF,
                    new CallbackHandler() {
                        public void handle(Callback[] callbacks)
                                throws IOException, UnsupportedCallbackException {
                            WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
                            pc.setPassword(configuration.getPassword());
                        }
                    });
            ((DispatchImpl) disp).getClient().getEndpoint()
                    .getOutInterceptors().add(new WSS4JOutInterceptor(wssProps));
        }

        boolean useProxy = configuration.getProxyServer() != null;
        String proxyHost = System.getProperty(HTTP_PROXY_HOST);
        String proxyPort = System.getProperty(HTTP_PROXY_PORT);
        if (useProxy) {
            System.setProperty(HTTP_PROXY_HOST, configuration.getProxyServer());
            System.setProperty(HTTP_PROXY_PORT, String.valueOf(configuration.getProxyPort()));
            String proxyUsername = configuration.getProxyUsername();
            if ((proxyUsername != null) && (proxyUsername.length() > 0)) {
                System.setProperty(HTTP_PROXY_USER, proxyUsername);
                System.setProperty(HTTP_PROXY_PASSWORD, configuration.getProxyPassword());
            }
        }
        Source response = null;
        if (null != operation.getOutput()) {
            response = disp.invoke(payload);
        } else {
            disp.invokeOneWay(payload);
        }
        if (useProxy) {
            setPropertyBack(HTTP_PROXY_HOST, proxyHost);
            setPropertyBack(HTTP_PROXY_PORT, proxyPort);
        }
        return response;
    }

    private static void setPropertyBack(String key, String value) {
        if (null != value) {
            System.setProperty(key, value);
        } else {
            System.clearProperty(key);
        }
    }

}
