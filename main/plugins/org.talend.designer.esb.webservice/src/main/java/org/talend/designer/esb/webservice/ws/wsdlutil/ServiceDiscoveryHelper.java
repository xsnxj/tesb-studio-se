/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.talend.designer.esb.webservice.ws.wsdlutil;

import java.io.IOException;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.ws.commons.schema.XmlSchemaCollection;

/**
 * This helper allow easy discovery of services and types
 * 
 * @author rlamarche
 */
public class ServiceDiscoveryHelper {

    private String wsdlUri;

    private WSDLFactory wsdlFactory;

    private Definition definition;

    private XmlSchemaCollection schemaCollection;

    public ServiceDiscoveryHelper(String wsdlUri) throws WSDLException, IOException {
        this.wsdlUri = wsdlUri;
        init();
    }

    /**
     * Read the wsdl and schema
     * 
     * @throws javax.wsdl.WSDLException
     */
    private void init() throws WSDLException, IOException {
        wsdlFactory = WSDLFactory.newInstance();
        WSDLReader newWSDLReader = wsdlFactory.newWSDLReader();

        newWSDLReader.setExtensionRegistry(wsdlFactory.newPopulatedExtensionRegistry());
        newWSDLReader.setFeature(com.ibm.wsdl.Constants.FEATURE_VERBOSE, false);
        definition = newWSDLReader.readWSDL(wsdlUri);
        schemaCollection = new XmlSchemaCollection();
        schemaCollection.setBaseUri(definition.getDocumentBaseURI());// bchen for bug 8674
    }

    /**
     * Return the parsed wsdl, it contains all services
     * 
     * @return
     */
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Return the xml schema collection
     * 
     * @return
     */
    public XmlSchemaCollection getSchema() {
        return schemaCollection;
    }

    public String getWsdlUri() {
        return wsdlUri;
    }
}
