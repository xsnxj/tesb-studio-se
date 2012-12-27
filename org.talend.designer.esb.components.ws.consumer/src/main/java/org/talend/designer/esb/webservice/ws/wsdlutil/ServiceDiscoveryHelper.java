/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.talend.designer.esb.webservice.ws.wsdlutil;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

/**
 * This helper allow easy discovery of services and types
 * 
 * @author rlamarche
 */
public class ServiceDiscoveryHelper {

    /**
     * Return the parsed wsdl, it contains all services
     * 
     * @return
     * @throws WSDLException 
     */
    public static Definition getDefinition(String wsdlUri) throws WSDLException {
        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
        WSDLReader newWSDLReader = wsdlFactory.newWSDLReader();

        newWSDLReader.setExtensionRegistry(wsdlFactory.newPopulatedExtensionRegistry());
        newWSDLReader.setFeature(com.ibm.wsdl.Constants.FEATURE_VERBOSE, false);
        return newWSDLReader.readWSDL(wsdlUri);
    }

}
