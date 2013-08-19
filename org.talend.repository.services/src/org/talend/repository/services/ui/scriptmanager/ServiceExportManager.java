// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.services.ui.scriptmanager;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EMap;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.designer.runprocess.IProcessor;
import org.talend.repository.RepositoryPlugin;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.ui.ServiceMetadataDialog;
import org.talend.repository.services.utils.WSDLUtils;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JobJavaScriptOSGIForESBManager;
import org.talend.repository.utils.TemplateProcessor;

public class ServiceExportManager extends JobJavaScriptOSGIForESBManager {

    private static final String TEMPLATE_BLUEPRINT = "/resources/blueprint-template.xml"; //$NON-NLS-1$

    private static final Logger LOG = Logger.getLogger(ServiceExportManager.class);

    public ServiceExportManager(Map<ExportChoice, Object> exportChoiceMap) {
        super(exportChoiceMap, null, null, IProcessor.NO_STATISTICS, IProcessor.NO_TRACES);
    }

    @SuppressWarnings("unchecked")
	public void createBlueprint(File outputFile, Map<ServicePort, Map<String, String>> ports,
            ServiceConnection serviceConnection, IFile wsdl, String studioServiceName)
                    throws IOException, CoreException {

        // TODO: support multiport!!!
        Entry<ServicePort, Map<String, String>> studioPort = ports.entrySet().iterator().next();
        // TODO: do this in looooooooop!!!

        Definition def = WSDLUtils.getDefinition(wsdl);
        String serviceName = null;
        String serviceNS = null;
        String endpointAddress = null;
        String endpointName = null;
        Map<QName, Service> services = def.getServices();
        ServicePort servicePort = studioPort.getKey();
        for (Entry<QName, Service> serviceEntry : services.entrySet()) { // TODO: support multi-services
            QName serviceQName = serviceEntry.getKey();
            Service service = serviceEntry.getValue();
            Collection<Port> servicePorts = service.getPorts().values(); // TODO: support multi-ports
            for (Port port : servicePorts) {
                if (servicePort.getName().equals(port.getBinding().getPortType().getQName().getLocalPart())) {
                    serviceName = serviceQName.getLocalPart();
                    serviceNS = serviceQName.getNamespaceURI();
                    endpointName = port.getName();
                    endpointAddress = WSDLUtils.getPortAddress(port);
                    if (null != endpointAddress) {
                        // http://jira.talendforge.org/browse/TESB-3638
                        try {
                            URI uri = new URI(endpointAddress);
                            endpointAddress = uri.getPath();
                            if(endpointAddress==null) {
                            	endpointAddress = uri.getRawSchemeSpecificPart();
                            	int interrogationMark=endpointAddress.indexOf('?');
                            	if(interrogationMark>0) {
                            		endpointAddress=endpointAddress.substring(0, interrogationMark);
                            	}
                            }

                            if (endpointAddress.equals("/services/") || endpointAddress.equals("/services")) {
                                // pass as is
                            } else if (endpointAddress.startsWith("/services/")) {
                                // remove forwarding "/services/" context as required by runtime
                                endpointAddress = endpointAddress.substring("/services/".length() - 1); // leave
                                                                                                        // forwarding
                                                                                                        // slash
                            } else if (endpointAddress.length() == 1) { // empty path
                                endpointAddress += studioServiceName;
                            }
                        } catch (URISyntaxException e) {
                            LOG.warn("Endpoint URI invalid: " + e);
                        }
                    }
                }
            }
        }

        Map<String, Object> endpointInfo = new HashMap<String, Object>();
        endpointInfo.put("namespace", serviceNS); //$NON-NLS-1$
        endpointInfo.put("service", serviceName); //$NON-NLS-1$
        endpointInfo.put("port", endpointName); //$NON-NLS-1$
        endpointInfo.put("address", endpointAddress); //$NON-NLS-1$
        endpointInfo.put("studioName", studioServiceName); //$NON-NLS-1$
        endpointInfo.put("wsdlLocation", wsdl.getName()); //$NON-NLS-1$

        Map<String, String> operation2job = new HashMap<String, String>();
        for (Map.Entry<ServicePort, Map<String, String>> port : ports.entrySet()) {
            // TODO: actual port work
            for (Map.Entry<String, String> operation : port.getValue().entrySet()) {
                operation2job.put(operation.getKey(), operation.getValue());
            }
        }
        endpointInfo.put("operation2job", operation2job); //$NON-NLS-1$

        EMap<String, String> additionalInfo = serviceConnection.getAdditionalInfo();
        boolean isStudioEEVersion = isStudioEEVersion();

        boolean useMonitor = Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.USE_SAM));
        boolean useLocator = Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.USE_SL));
        boolean useRegistry = isStudioEEVersion?Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.USE_SERVICE_REGISTRY)):false;
        boolean useSecurityToken = Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.SECURITY_BASIC));
        boolean useSecuritySAML = Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.SECURITY_SAML));
        boolean useAuthorization = isStudioEEVersion?Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.AUTHORIZATION)):false;
        boolean logMessages = Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.LOG_MESSAGES));
        boolean wsdlSchemaValidation = isStudioEEVersion?Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.WSDL_SCHEMA_VALIDATION)):false;
        boolean useBusinessCorrelation =  Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.USE_BUSINESS_CORRELATION));

        endpointInfo.put("useSL", useLocator /*&& !useRegistry*/); //$NON-NLS-1$
        endpointInfo.put("useSAM", useMonitor && !useRegistry); //$NON-NLS-1$
        endpointInfo.put("useSecurityToken", useSecurityToken && !useRegistry); //$NON-NLS-1$
        endpointInfo.put("useSecuritySAML", useSecuritySAML && !useRegistry); //$NON-NLS-1$
        endpointInfo.put("useAuthorization", useAuthorization && useSecuritySAML && !useRegistry); //$NON-NLS-1$
        endpointInfo.put("useServiceRegistry", useRegistry); //$NON-NLS-1$
        endpointInfo.put("logMessages", logMessages); //$NON-NLS-1$
        endpointInfo.put("useWsdlSchemaValidation", wsdlSchemaValidation); //$NON-NLS-1$
        endpointInfo.put("useBusinessCorrelation", useBusinessCorrelation && !useRegistry); //$NON-NLS-1$

        Map<String, String> slCustomProperties = new HashMap<String, String>();
        if (useLocator /*&& !useRegistry*/) {
            for (Map.Entry<String, String> prop : additionalInfo.entrySet()) {
                if (prop.getKey().startsWith(ServiceMetadataDialog.SL_CUSTOM_PROP_PREFIX)) {
                    slCustomProperties.put(prop.getKey().substring(ServiceMetadataDialog.SL_CUSTOM_PROP_PREFIX.length()),
                            prop.getValue());
                }
            }
        }
        endpointInfo.put("slCustomProps", slCustomProperties); //$NON-NLS-1$

        TemplateProcessor.processTemplate("DATA_SERVICE_BLUEPRINT_CONFIG", endpointInfo, outputFile, //$NON-NLS-1$
                new InputStreamReader(this.getClass().getResourceAsStream(TEMPLATE_BLUEPRINT)));
    }

    public Manifest getManifest(String artefactName, String serviceVersion) {
        Manifest manifest = new Manifest();
        Attributes a = manifest.getMainAttributes();
        a.put(Attributes.Name.MANIFEST_VERSION, "1.0"); //$NON-NLS-1$
        a.put(new Attributes.Name("Bundle-Name"), artefactName); //$NON-NLS-1$
        a.put(new Attributes.Name("Bundle-SymbolicName"), artefactName); //$NON-NLS-1$
        a.put(new Attributes.Name("Bundle-Version"), serviceVersion); //$NON-NLS-1$
        a.put(new Attributes.Name("Bundle-ManifestVersion"), "2"); //$NON-NLS-1$ //$NON-NLS-2$
        IBrandingService brandingService = (IBrandingService) GlobalServiceRegister.getDefault().getService(
                IBrandingService.class);
        a.put(new Attributes.Name("Created-By"), brandingService.getFullProductName() + " ("
                + brandingService.getAcronym() + "_"
                + RepositoryPlugin.getDefault().getBundle().getVersion().toString() + ")");
        a.put(new Attributes.Name("Import-Package"), //$NON-NLS-1$
                "javax.xml.ws,org.talend.esb.job.controller" //$NON-NLS-1$
                        + ",org.osgi.service.cm;version=\"[1.3,2)\"" //$NON-NLS-1$
                        + ",org.apache.ws.security.validate" //$NON-NLS-1$
                        + ",org.apache.cxf.management.counters" //$NON-NLS-1$
        );
        a.put(new Attributes.Name("Require-Bundle"), //$NON-NLS-1$
                "org.apache.cxf.bundle" //$NON-NLS-1$
                        + ",org.springframework.beans" //$NON-NLS-1$
                        + ",org.springframework.context" //$NON-NLS-1$
                        + ",org.springframework.osgi.core" //$NON-NLS-1$
                        + ",locator" //$NON-NLS-1$
                        + ",sam-agent" //$NON-NLS-1$
                        + ",sam-common" //$NON-NLS-1$
        );
        return manifest;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.
     * JobJavaScriptOSGIForESBManager#getOutputSuffix()
     */
    @Override
    public String getOutputSuffix() {
        return "/";
    }

    public static Map<ExportChoice, Object> getDefaultExportChoiceMap() {
        Map<ExportChoice, Object> exportChoiceMap = new EnumMap<ExportChoice, Object>(ExportChoice.class);
        exportChoiceMap.put(ExportChoice.needLauncher, true);
        exportChoiceMap.put(ExportChoice.needSystemRoutine, true);
        exportChoiceMap.put(ExportChoice.needUserRoutine, true);
        exportChoiceMap.put(ExportChoice.needTalendLibraries, true);
        exportChoiceMap.put(ExportChoice.needJobItem, true);
        exportChoiceMap.put(ExportChoice.needJobScript, true);
        exportChoiceMap.put(ExportChoice.needContext, true);
        exportChoiceMap.put(ExportChoice.needSourceCode, true);
        exportChoiceMap.put(ExportChoice.applyToChildren, false);
        exportChoiceMap.put(ExportChoice.doNotCompileCode, false);
        return exportChoiceMap;
    }

    public JobScriptsManager getJobManager(Map<ExportChoice, Object> exportChoiceMap, String parentPath,
            RepositoryNode node, String groupId, String serviceVersion) {
        if (exportChoiceMap == null) {
            exportChoiceMap = getDefaultExportChoiceMap();
        }
        JobJavaScriptOSGIForESBManager manager = new JobJavaScriptOSGIForESBManager(exportChoiceMap,
                "Default", serviceVersion, statisticPort, tracePort); //$NON-NLS-1$
        String artefactName = getNodeLabel(node);
        File path = getFilePath(parentPath, groupId, artefactName, serviceVersion);
        File file = new File(path, artefactName + '-' + serviceVersion + manager.getOutputSuffix());
        manager.setDestinationPath(file.getAbsolutePath());
        return manager;
    }

    public File getFilePath(String parentPath, String groupId, String artefactName, String serviceVersion) {
        File folder = new File(parentPath, "repository"); //$NON-NLS-1$
        File group = new File(folder, groupId.replace('.', File.separatorChar));
        File artefact = new File(group, artefactName);
        File version = new File(artefact, serviceVersion);
        version.mkdirs();
        return version;
    }

    public String getNodeLabel(RepositoryNode node) {
        return node.getObject().getProperty().getLabel();
    }

}
