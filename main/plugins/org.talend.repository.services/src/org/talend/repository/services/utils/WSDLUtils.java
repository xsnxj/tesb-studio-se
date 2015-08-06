// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import java.util.List;
import java.util.Map;

import javax.wsdl.BindingOutput;
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
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.wst.wsdl.validation.internal.IValidationReport;
import org.eclipse.wst.wsdl.validation.internal.eclipse.WSDLValidator;
import org.eclipse.wst.wsdl.validation.internal.eclipse.URIResolverWrapper;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.ResourceModelUtils;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.Activator;
import org.talend.repository.services.Messages;
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

    public static final String COMMUNICATION_STYLE = "COMMUNICATION_STYLE"; //$NON-NLS-1$        

    public static final String ONE_WAY = "one-way"; //$NON-NLS-1$

    public static final String REQUEST_RESPONSE = "request-response"; //$NON-NLS-1$

    public static Map<String, String> getServiceOperationParameters(String wsdlURI, String operationName, String portTypeName)
            throws CoreException {
        // NOTE: all below in assuming standalone (no another WSDL's imports) WS-I complaint WSDL !
        Map<String, String> map = new HashMap<String, String>();
        if (null == wsdlURI) { // no WSDL provided
            return map;
        }

        Definition wsdl = getDefinition(wsdlURI);
        String targetNs = wsdl.getTargetNamespace();
        QName portTypeQName = new QName(targetNs, portTypeName);
        if (null == wsdl.getPortType(portTypeQName)) { // portType not found
            return map;
        }
        boolean isOneWay = false;
        String serviceName = null;
        String portName = null;
        String endpointUri = null;
        for (Object serviceObject : wsdl.getServices().values()) {
            Service service = (Service) serviceObject;
            for (Object portObject : service.getPorts().values()) {
                Port port = (Port) portObject;
                if (portTypeQName.equals(port.getBinding().getPortType().getQName())) {
                    portName = port.getName();
                    BindingOutput out = port.getBinding().getBindingOperation(operationName, null, null).getBindingOutput();
                    if (null == out) {
                        // it is oneway
                        isOneWay = true;
                    } else {
                        // it is request response
                        isOneWay = false;
                    }
                    @SuppressWarnings("rawtypes")
                    List extElements = port.getExtensibilityElements();
                    if (null != extElements) {
                        for (Object extElement : extElements) {
                            if (extElement instanceof SOAPAddress) {
                                endpointUri = ((SOAPAddress) extElement).getLocationURI();
                                break;
                            } else if (extElement instanceof SOAP12Address) {
                                endpointUri = ((SOAP12Address) extElement).getLocationURI();
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            if (null != portName) {
                serviceName = service.getQName().getLocalPart();
            }
        }

        if (null != serviceName) {
            map.put(SERVICE_NAME, serviceName);
            map.put(SERVICE_NS, targetNs);
            map.put(PORT_NAME, portName);
            map.put(PORT_NS, targetNs);
            map.put(OPERATION_NAME, operationName);
            // map.put(OPERATION_NS, targetNs);
            map.put(ENDPOINT_URI, endpointUri);
            map.put(WSDL_LOCATION, wsdlURI);
            if (isOneWay) {
                map.put(COMMUNICATION_STYLE, ONE_WAY);
            } else {
                map.put(COMMUNICATION_STYLE, REQUEST_RESPONSE);
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

    public static IFile getWsdlFile(IRepositoryViewObject serviceViewObject) {
        ServiceItem serviceItem = (ServiceItem) serviceViewObject.getProperty().getItem();
        IProject currentProject = ProjectManager.getInstance().getResourceProject(serviceItem);
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
                serviceViewObject.getProperty().getLabel() + "_" + serviceViewObject.getProperty().getVersion() + ".wsdl");
        if (!file.exists()) {
            // copy file to item
            IFile fileTemp = null;
            try {
                folder = "";
                if (!foldPath.equals("")) {
                    folder = "/" + foldPath;
                }
                fileTemp = currentProject.getFolder("services" + folder)
                        .getFile(
                                serviceViewObject.getProperty().getLabel() + "_" + serviceViewObject.getProperty().getVersion()
                                        + ".wsdl");
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
    }

    public static IFile getWsdlFile(IRepositoryNode repositoryNode) {
        return getWsdlFile(repositoryNode.getObject());
    }

    public static IFile getWsdlFile(ServiceItem serviceItem) {
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
                    serviceItem.getProperty().getLabel() + "_" + serviceItem.getProperty().getVersion() + ".wsdl");
            if (!file.exists()) {
                // copy file to item
                IFile fileTemp = null;
                try {
                    folder = "";
                    if (!foldPath.equals("")) {
                        folder = "/" + foldPath;
                    }
                    fileTemp = currentProject.getFolder("services" + folder).getFile(
                            serviceItem.getProperty().getLabel() + "_" + serviceItem.getProperty().getVersion() + ".wsdl");
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

    /**
     * Validate WSDL file.
     * 
     * @param node
     * @throws CoreException
     */
    public static void validateWsdl(RepositoryNode node) throws CoreException {
        IFile wsdlFile = getWsdlFile(node);
        if (null == wsdlFile) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                    Messages.PublishMetadata_Exception_wsdl_not_found));
        }
        String wsdlPath = wsdlFile.getLocationURI().toString();
        validateWsdl(wsdlPath);
    }

    /**
     * Validate WSDL file.
     * 
     * @param wsdlUri
     * @throws CoreException
     */
    @SuppressWarnings("restriction")
    public static void validateWsdl(String wsdlUri) throws CoreException {
        WSDLValidator wsdlValidator = WSDLValidator.getInstance();
        // wsdlValidator.addURIResolver(new URIResolverWrapper());
        IValidationReport validationReport = wsdlValidator.validate(wsdlUri);

        if (!validationReport.isWSDLValid()) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                    Messages.PublishMetadata_Exception_wsdl_not_valid));
        }
    }

}
