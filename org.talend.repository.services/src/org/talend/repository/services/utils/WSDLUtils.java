// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.services.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

/**
 * DOC ycbai class global comment. Detailled comment
 */
public class WSDLUtils {

    public static final String SERVICE_NAME = "SERVICE_NAME"; //$NON-NLS-1$

    public static final String SERVICE_NS = "SERVICE_NS"; //$NON-NLS-1$

    public static final String PORT_NS = "PORT_NS"; //$NON-NLS-1$

    public static final String ENDPOINT = "ENDPOINT"; //$NON-NLS-1$

    public static final String ESB_ENDPOINT = "ESB_ENDPOINT"; //$NON-NLS-1$

    public static Map<String, String> getServiceParameters(String wsdlURI) throws WSDLException {
        Map<String, String> map = new HashMap<String, String>();
        if (wsdlURI == null)
            return map;
        map.put(ENDPOINT, wsdlURI);
        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
        WSDLReader newWSDLReader = wsdlFactory.newWSDLReader();
        newWSDLReader.setExtensionRegistry(wsdlFactory.newPopulatedExtensionRegistry());
        newWSDLReader.setFeature(com.ibm.wsdl.Constants.FEATURE_VERBOSE, false);
        Definition definition = newWSDLReader.readWSDL(wsdlURI);
        Map services = definition.getServices();
        Iterator servicesIter = services.keySet().iterator();
        while (servicesIter.hasNext()) {
            QName key = (QName) servicesIter.next();
            map.put(SERVICE_NAME, key.getLocalPart());
            map.put(SERVICE_NS, key.getNamespaceURI());
            map.put(PORT_NS, key.getNamespaceURI());
            Service service = (Service) services.get(key);
            Map ports = service.getPorts();
            Iterator portsIter = ports.keySet().iterator();
            while (portsIter.hasNext()) {
                String portKey = (String) portsIter.next();
                Port port = (Port) ports.get(portKey);
                List extElements = port.getExtensibilityElements();
                if (extElements != null && extElements.size() > 0) {
                    SOAPAddress address = (SOAPAddress) extElements.get(0);
                    map.put(ESB_ENDPOINT, address.getLocationURI());
                }
            }
        }

        return map;
    }

}
