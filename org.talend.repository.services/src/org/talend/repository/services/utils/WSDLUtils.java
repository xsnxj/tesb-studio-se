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

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.ResourceModelUtils;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;

/**
 * DOC ycbai class global comment. Detailled comment
 */
public class WSDLUtils {

    public static final String SERVICE_NAME = "SERVICE_NAME"; //$NON-NLS-1$

    public static final String SERVICE_NS = "SERVICE_NS"; //$NON-NLS-1$

    public static final String PORT_NAME = "PORT_NAME"; //$NON-NLS-1$

    public static final String PORT_NS = "PORT_NS"; //$NON-NLS-1$

    public static final String OPERATION_NAME = "OPERATION_NAME"; //$NON-NLS-1$

    public static final String OPERATION_NS = "OPERATION_NS"; //$NON-NLS-1$

    public static final String ENDPOINT_URI = "ENDPOINT_URI"; //$NON-NLS-1$

    public static final String WSDL_LOCATION = "WSDL_LOCATION"; //$NON-NLS-1$

    public static Map<String, String> getServiceParameters(String wsdlURI) throws CoreException {
        Map<String, String> map = new HashMap<String, String>();
        if (wsdlURI == null)
            return map;
        map.put(ENDPOINT_URI, wsdlURI);

        Definition definition = getDefinition(wsdlURI);

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
                    Object obj = extElements.get(0);
                    if (obj instanceof SOAPAddress) {
                        SOAPAddress address = (SOAPAddress) extElements.get(0);
                        map.put(ENDPOINT_URI, address.getLocationURI());
                    } else if (obj instanceof SOAP12Address) {
                        SOAP12Address address = (SOAP12Address) extElements.get(0);
                        map.put(ENDPOINT_URI, address.getLocationURI());
                    }
                }
            }
        }

        return map;
    }

    public static boolean isValidService(ServiceItem serviceItem) {
        boolean isValid = false;
        List<ServicePort> listPort = ((ServiceConnection) serviceItem.getConnection()).getServicePort();
        for (ServicePort port : listPort) {
            List<ServiceOperation> listOperation = port.getServiceOperation();
            for (ServiceOperation operation : listOperation) {
                if (operation.getReferenceJobId() != null) {
                    isValid = true;
                    break;
                }
            }
        }

        return isValid;
    }

    public static IFile getWsdlFile(IRepositoryNode repositoryNode) {
        ServiceItem serviceItem = (ServiceItem) repositoryNode.getObject().getProperty().getItem();
        try {
            IProject currentProject = ResourceModelUtils.getProject(ProjectManager.getInstance().getCurrentProject());
            List<ReferenceFileItem> list = serviceItem.getReferenceResources();
            for (ReferenceFileItem item : list) {
                IPath path = Path.fromOSString(item.eResource().getURI().path());
            }
            String foldPath = serviceItem.getState().getPath();
            String folder = "";
            if (!foldPath.equals("")) {
                folder = "/" + foldPath;
            }
            IFile file = currentProject.getFolder("services" + folder).getFile(
                    repositoryNode.getObject().getProperty().getLabel() + "_"
                            + repositoryNode.getObject().getProperty().getVersion() + ".wsdl");
            if (!file.exists()) {
                // copy file to item
                IFile fileTemp = null;
                try {
                    folder = "";
                    if (!foldPath.equals("")) {
                        folder = "/" + foldPath;
                    }
                    fileTemp = currentProject.getFolder("services" + folder).getFile(
                            repositoryNode.getObject().getProperty().getLabel() + "_"
                                    + repositoryNode.getObject().getProperty().getVersion() + ".wsdl");
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[0]);
                    if (!fileTemp.exists()) {
                        fileTemp.create(byteArrayInputStream, true, null);
                    } else {
                        fileTemp.delete(true, null);
                        fileTemp.create(byteArrayInputStream, true, null);
                    }
                } catch (CoreException e) {
                    ExceptionHandler.process(e);
                }
                //
                ReferenceFileItem createReferenceFileItem = PropertiesFactory.eINSTANCE.createReferenceFileItem();
                ByteArray byteArray = PropertiesFactory.eINSTANCE.createByteArray();
                createReferenceFileItem.setContent(byteArray);
                createReferenceFileItem.setExtension("wsdl");
                serviceItem.getReferenceResources().add(createReferenceFileItem);
                createReferenceFileItem.getContent().setInnerContent(new byte[0]);
                IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
                try {
                    factory.save(serviceItem);
                } catch (PersistenceException e) {
                    ExceptionHandler.process(e);
                }
            }
            return file;
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
        return null;
    }

    public static Definition getDefinition(String pathToWsdl) throws CoreException {
        Definition definition = null;
        try {
            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            WSDLReader newWSDLReader = wsdlFactory.newWSDLReader();

            newWSDLReader.setExtensionRegistry(wsdlFactory.newPopulatedExtensionRegistry());
            newWSDLReader.setFeature(com.ibm.wsdl.Constants.FEATURE_VERBOSE, false);
            definition = newWSDLReader.readWSDL(pathToWsdl);
        } catch (WSDLException e) {
            throw new CoreException(StatusUtil.newStatus(IStatus.ERROR, e.getLocalizedMessage(), e));
        }
        return definition;
    }

    public static Definition getWsdlDefinition(RepositoryNode repositoryNode) throws CoreException {
        return getDefinition(getWsdlFile(repositoryNode).getLocation().toOSString());
    }

}
