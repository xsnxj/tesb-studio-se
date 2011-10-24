package org.talend.repository.services.ui.scriptmanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EMap;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.designer.runprocess.IProcessor;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.Messages;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.ui.ServiceMetadataDialog;
import org.talend.repository.services.utils.WSDLUtils;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JobJavaScriptOSGIForESBManager;

public class ServiceExportManager extends JobJavaScriptOSGIForESBManager {

	private static Logger logger = Logger.getLogger(ServiceExportManager.class);

	public ServiceExportManager() {
		super(null, null, null, IProcessor.NO_STATISTICS, IProcessor.NO_TRACES);
	}

	public void createSpringBeans(String outputFile,
			Map<ServicePort, Map<String, String>> ports, File wsdl,
			String studioServiceName) throws IOException, CoreException {

		//TODO: support multiport!!!
		Entry<ServicePort, Map<String, String>> studioPort = ports.entrySet().iterator().next();
		//TODO: do this in looooooooop!!!

		Definition def = WSDLUtils.getDefinition(wsdl.getAbsolutePath());
		String serviceName = null;
		String serviceNS = null;
		String endpointAddress = null;
		String endpointName = null;
		Map<QName, Service> services = def.getServices();
		ServicePort servicePort = studioPort.getKey();
		for (Entry<QName, Service> serviceEntry : services.entrySet()) { //TODO: support multiservice
			QName serviceQName = serviceEntry.getKey();
			Service service = serviceEntry.getValue();
			Collection<Port> servicePorts = service.getPorts().values(); //TODO: support multiport
			for (Port port : servicePorts) {
				if (servicePort.getName().equals(port.getBinding().getPortType().getQName().getLocalPart())) {
					serviceName = serviceQName.getLocalPart();
					serviceNS = serviceQName.getNamespaceURI();
					endpointName = port.getName();
					Collection<ExtensibilityElement> addrElems = findExtensibilityElement(
							port.getExtensibilityElements(), "address"); //$NON-NLS-1$
					for (ExtensibilityElement element : addrElems) {
						if (element != null && element instanceof SOAPAddress) {
							endpointAddress = ((SOAPAddress) element).getLocationURI();
							// http://jira.talendforge.org/browse/TESB-3601
							if (endpointAddress.contains("://")) { //$NON-NLS-1$
								endpointAddress = endpointAddress.substring(endpointAddress.lastIndexOf("://") + 3); //$NON-NLS-1$
								endpointAddress = endpointAddress.substring(endpointAddress.indexOf("/")); //$NON-NLS-1$
								if (endpointAddress.startsWith("/services/")) { //$NON-NLS-1$
									endpointAddress = endpointAddress.substring("/services".length()); //$NON-NLS-1$
								}
							}
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
			//TODO: actual port work
			for (Map.Entry<String, String> operation : port.getValue().entrySet()) {
				operation2job.put(operation.getKey(), operation.getValue());
			}
		}
		endpointInfo.put("operation2job", operation2job); //$NON-NLS-1$


		EMap<String, String> additionalInfo = servicePort.getAdditionalInfo();
		boolean useSl = Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.USE_SL));
		boolean useSam = Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.USE_SAM));
		boolean useSecurityToken = Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.SECURITY_BASIC));
		boolean useSecuritySaml = Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.SECURITY_SAML));

		endpointInfo.put("useSL", useSl); //$NON-NLS-1$
		endpointInfo.put("useSAM", useSam); //$NON-NLS-1$
		endpointInfo.put("useSecuritySAML", useSecuritySaml); //$NON-NLS-1$
		endpointInfo.put("useSecurityToken", useSecurityToken); //$NON-NLS-1$

		VelocityEngine engine = new VelocityEngine();
		engine.setProperty("resource.loader", "classpath"); //$NON-NLS-1$ //$NON-NLS-2$
		engine.setProperty("classpath.resource.loader.class", //$NON-NLS-1$
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"); //$NON-NLS-1$
		engine.init();

		VelocityContext context = new VelocityContext();
		context.put("endpoint", endpointInfo); //$NON-NLS-1$

		FileWriter fw = null;
		try {
			fw = new FileWriter(outputFile);
			Template template = engine.getTemplate("/resources/beans-template.xml"); //$NON-NLS-1$
			template.merge(context, fw);
			fw.flush();
		} catch (ResourceNotFoundException rnfe) {
			// couldn't find the template
			logger.error(Messages.ServiceExportManager_Exception_cannot_open_file, rnfe);
		} catch (ParseErrorException pee) {
			// syntax error: problem parsing the template
			logger.error(Messages.ServiceExportManager_Exception_cannot_open_file, pee);
		} catch (MethodInvocationException mie) {
			// something invoked in the template threw an exception
			logger.error(mie.getLocalizedMessage(), mie);
		} catch (IOException e) {
			// something wrong with output file
			logger.error(e.getLocalizedMessage(), e);
		} finally {
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
					logger.warn(e.getLocalizedMessage(), e);
				}
			}
		}
	}

	public static Collection<ExtensibilityElement> findExtensibilityElement(
			List<ExtensibilityElement> extensibilityElements, String elementType) {
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

	public Manifest getManifest(String artefactName, String serviceVersion) {
		Manifest manifest = new Manifest();
		Attributes a = manifest.getMainAttributes();
		a.put(Attributes.Name.MANIFEST_VERSION, "1.0"); //$NON-NLS-1$
		a.put(new Attributes.Name("Bundle-Name"), artefactName); //$NON-NLS-1$
		a.put(new Attributes.Name("Bundle-SymbolicName"), artefactName); //$NON-NLS-1$
		a.put(new Attributes.Name("Bundle-Version"), serviceVersion); //$NON-NLS-1$
		a.put(new Attributes.Name("Bundle-ManifestVersion"), "2"); //$NON-NLS-1$ //$NON-NLS-2$
		a.put(new Attributes.Name("Import-Package"), //$NON-NLS-1$
				"javax.xml.ws,org.talend.esb.job.controller" //$NON-NLS-1$
				+ ",org.osgi.service.cm;version=\"[1.3,2)\"" //$NON-NLS-1$
				+ ",org.apache.ws.security.validate" //$NON-NLS-1$
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
		Map<ExportChoice, Object> exportChoiceMap =
				new EnumMap<ExportChoice, Object>(ExportChoice.class);
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

	public JobScriptsManager getJobManager(RepositoryNode node, String groupId,
			String serviceVersion) {
		JobJavaScriptOSGIForESBManager manager = new JobJavaScriptOSGIForESBManager(
				getDefaultExportChoiceMap(), "Default", serviceVersion,
				statisticPort, tracePort);
		String artefactName = getNodeLabel(node);
		File path = getFilePath(groupId, artefactName, serviceVersion);
		File file = new File(path, artefactName + "-" + serviceVersion
				+ manager.getOutputSuffix());
		manager.setDestinationPath(file.getAbsolutePath());
		return manager;
	}

	public File getFilePath(String groupId, String artefactName,
			String serviceVersion) {
		File folder = new File(getDestinationPath(), "repository");
		folder.mkdirs();
		String path = groupId.replace('.', File.separatorChar);
		File group = new File(folder, path);
		File artefact = new File(group, artefactName);
		File version = new File(artefact, serviceVersion);
		version.mkdirs();
		return version;
	}

	public String getNodeLabel(RepositoryNode node) {
		if (node.getType() == ENodeType.REPOSITORY_ELEMENT) {
			IRepositoryViewObject repositoryObject = node.getObject();
			if (repositoryObject.getProperty().getItem() instanceof ProcessItem) {
				ProcessItem processItem = (ProcessItem) repositoryObject
						.getProperty().getItem();
				return processItem.getProperty().getLabel();
			}
		}
		return "Job";
	}

}
