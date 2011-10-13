package org.talend.repository.services.ui.scriptmanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EMap;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.designer.runprocess.IProcessor;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.Activator;
import org.talend.repository.services.Messages;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.ui.ServiceMetadataDialog;
import org.talend.repository.services.utils.WSDLUtils;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JobJavaScriptOSGIForESBManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ServiceExportManager extends JobJavaScriptOSGIForESBManager {

    private static final String CXF_NS = "http://cxf.apache.org/core";
    private static final String JAXWS_NS = "http://cxf.apache.org/jaxws";
    private static final String POLICY_NS = "http://cxf.apache.org/policy";

    private static Logger logger = Logger.getLogger(ServiceExportManager.class);

    public ServiceExportManager() {
        super(null, null, null, IProcessor.NO_STATISTICS, IProcessor.NO_TRACES);
    }

    public void createPolicyAttachment(String outputFile, File wsdl,
            String policyReferenceUri) throws IOException, CoreException {
        String templateFile = FileLocator.toFileURL(
                FileLocator.find(Platform.getBundle(Activator.PLUGIN_ID),
                        new Path("resources/policy-template.xml"), null)) //$NON-NLS-1$
                .getFile();
        Map<String, String> serviceParams = WSDLUtils.getServiceParameters(wsdl.getAbsolutePath());

        //TODO: remove template processing
        Map<String, String> replacements = new HashMap<String, String>();
        replacements.put("@policy.reference@", policyReferenceUri); //$NON-NLS-1$
        replacements.put("@endpoint.uri@", serviceParams.get(WSDLUtils.ENDPOINT_URI)); //$NON-NLS-1$
        uglyTemplateProcessing(templateFile, outputFile, replacements);
    }

    public void createSpringBeans(String outputFile,
            Map<ServicePort, Map<String, String>> ports, File wsdl,
            String studioServiceName, boolean policyEnabled) throws IOException, CoreException {
        String inputFile = FileLocator.toFileURL(
                FileLocator.find(Platform.getBundle(Activator.PLUGIN_ID),
                        new Path("resources/beans-template.xml"), null)) //$NON-NLS-1$
                .getFile();
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
                    Collection<ExtensibilityElement> addrElems = findExtensibilityElement(port.getExtensibilityElements(), "address");
                    for (ExtensibilityElement element : addrElems) {
                        if (element != null && element instanceof SOAPAddress) {
                            endpointAddress = ((SOAPAddress) element).getLocationURI();
                        }
                    }
                }
            }
        }

        //TODO: remove template processing
        Map<String, String> replacements = new HashMap<String, String>();
        replacements.put("@Service.NS@", serviceNS); //$NON-NLS-1$
        replacements.put("@Service.Name@", serviceName); //$NON-NLS-1$
        replacements.put("@Endpoint.Name@", endpointName); //$NON-NLS-1$
        replacements.put("@Endpoint.Address@", endpointAddress); //$NON-NLS-1$
        replacements.put("@Service.Studio.Name@", studioServiceName); //$NON-NLS-1$
        replacements.put("@Wsdl.Location@", wsdl.getName()); //$NON-NLS-1$
        uglyTemplateProcessing(inputFile, outputFile, replacements);

        //create mapping operation-job
        File out = new File(outputFile);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(out);
            //include SAM/SL features
            EMap<String, String> additionalInfo = servicePort.getAdditionalInfo();
            Element root = doc.getDocumentElement();
            Element features = null;
            NodeList endpoints = root.getElementsByTagNameNS(JAXWS_NS,"endpoint");
            for (int i=0; i<endpoints.getLength(); i++) {
                Element endpoint = (Element)endpoints.item(i);
                if (("serviceNamespace:"+endpointName).equals(endpoint.getAttribute("endpointName"))) {
                    NodeList featuresNodes = endpoint.getElementsByTagNameNS(JAXWS_NS, "features");
                    if (featuresNodes.getLength() > 0) {
                        features = (Element) featuresNodes.item(0);
                    }
                }
            }
            if (Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.USE_SL))) {
                Element im = createKid(root, "import");
                im.setAttribute("resource", "classpath:META-INF/tesb/locator/beans-osgi.xml"); //SL
                Element bean = createKid(features, "bean");//<bean class="org.talend.esb.servicelocator.cxf.LocatorFeature"/>
                bean.setAttribute("class", "org.talend.esb.servicelocator.cxf.LocatorFeature");
            }
            Boolean useSam = Boolean.valueOf(additionalInfo.get(ServiceMetadataDialog.USE_SAM));
            if (useSam) {
                Element im = createKid(root, "import");
                im.setAttribute("resource", "classpath:META-INF/tesb/agent-osgi.xml"); //SAM
                Element bean = createKid(features, "ref");//<ref bean="eventFeature"/>
                bean.setAttribute("bean", "eventFeature");
            }
            NodeList mapEls = doc.getElementsByTagName("map");
            if (mapEls.getLength() > 0) {
                Element map = (Element) mapEls.item(0);
                for (Map.Entry<ServicePort, Map<String, String>> port : ports.entrySet()) {
                    //TODO: actual port work
                    for (Map.Entry<String, String> operation : port.getValue().entrySet()) {
                        Element entry = createKid(map, "entry");
                        Element key = createKid(createKid(entry, "key"), "value");
                        key.setTextContent(operation.getKey());
                        Element value = createKid(entry, "value");
                        value.setTextContent(operation.getValue());
                    }
                }
                if (useSam) {
                    Element property = createKid((Element) map.getParentNode().getParentNode(), "property");
                    property.setAttribute("name", "eventFeature");
                    property.setAttribute("ref", "eventFeature");
                }
            }

            if (policyEnabled) {
//                createKidNS(
//                        createKidNS(
//                                createKidNS(root,
//                                        "bus", CXF_NS),
//                                "features", CXF_NS),
//                        "policies", POLICY_NS);
//
//                createKidNS(root, "externalAttachment", POLICY_NS)
//                    .setAttribute("location", "classpath:/policy.xml");
            } else {
                Node node2remove = root.getElementsByTagNameNS(CXF_NS, "bus").item(0);
                if (null != node2remove) {
                    root.removeChild(node2remove);
                }
                node2remove = root.getElementsByTagNameNS(POLICY_NS, "externalAttachment").item(0);
                if (null != node2remove) {
                    root.removeChild(node2remove);
                }
            }

            //output
            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer serializer;
            serializer = tfactory.newTransformer();
            //Setup indenting to "pretty print"
            serializer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$

            serializer.transform(new DOMSource(doc), new StreamResult(out));

        } catch (ParserConfigurationException e) {
            logger.error(Messages.ServiceExportManager_Exception_cannot_create_document_builder, e);
        } catch (SAXException e) {
            logger.error(Messages.ServiceExportManager_Exception_Cannot_parse_job_xml, e);
        } catch (IOException e) {
            logger.error(Messages.ServiceExportManager_Exception_cannot_open_file, e);
        } catch (TransformerConfigurationException e) {
            logger.error(Messages.ServiceExportManager_Exception_cannot_serialize_xml, e);
        } catch (TransformerException e) {
            logger.error(e.getLocalizedMessage(), e);
        }

    }

//	private void addProperty(Element props, String key,
//			String value) {
//		Element prop = createKid(props, "entry"); //$NON-NLS-1$
//		prop.setAttribute("key", key); //$NON-NLS-1$
//		prop.setAttribute("value", value); //$NON-NLS-1$
//	}
//
    private Element createKid(Element parent, String kidName) {
        Element kid = parent.getOwnerDocument().createElement(kidName);
        parent.appendChild(kid);
        return kid;
    }
    private Element createKidNS(Element parent, String kidName, String kidNamespace) {
        Element kid = parent.getOwnerDocument().createElementNS(kidNamespace, kidName);
        parent.appendChild(kid);
        return kid;
    }

    public static Collection<ExtensibilityElement> findExtensibilityElement(List<ExtensibilityElement> extensibilityElements, String elementType) {
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
        a.put(new Attributes.Name("Import-Package"), "javax.xml.ws,org.talend.esb.job.controller,org.osgi.service.cm;version=\"[1.3,2)\"");
        a.put(new Attributes.Name("Require-Bundle"), "org.apache.cxf.bundle,org.springframework.beans,org.springframework.context,org.springframework.osgi.core,locator,sam-agent,sam-common");
        return manifest;
    }

    /* (non-Javadoc)
     * @see org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JobJavaScriptOSGIForESBManager#getOutputSuffix()
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


    public JobScriptsManager getJobManager(RepositoryNode node, String groupId, String serviceVersion) {
        JobJavaScriptOSGIForESBManager manager = new JobJavaScriptOSGIForESBManager(getDefaultExportChoiceMap(), "Default", serviceVersion, statisticPort, tracePort);
        String artefactName = getNodeLabel(node);
        File path = getFilePath(groupId, artefactName, serviceVersion);
        File file = new File(path, artefactName+"-"+serviceVersion+manager.getOutputSuffix());
        manager.setDestinationPath(file.getAbsolutePath());
        return manager;
    }

    public File getFilePath(String groupId, String artefactName, String serviceVersion) {
        File folder = new File(getDestinationPath(),"repository");
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
                ProcessItem processItem = (ProcessItem) repositoryObject.getProperty().getItem();
                return processItem.getProperty().getLabel();
            }
        }
        return "Job";
    }

    private void uglyTemplateProcessing(String inputFile, String outputFile,
            Map<String, String> replacements) {

        FileReader fr = null;
        FileWriter fw = null;
        try {
            fr = new FileReader(inputFile);
            BufferedReader br = new BufferedReader(fr);

            fw = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(fw);

            String line = br.readLine();
            while (line != null) {
                for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                    line = line.replace(replacement.getKey(), replacement.getValue());
                }
                bw.write(line + "\n"); //$NON-NLS-1$
                line = br.readLine();
            }
            bw.flush();
        } catch (FileNotFoundException e) {
            ExceptionHandler.process(e);
            logger.error(e);
        } catch (IOException e) {
            ExceptionHandler.process(e);
            logger.error(e);
        } finally {
            if (null != fw) {
                try { fw.close(); } catch (IOException e) { }
            }
            if (null != fr) {
                try { fr.close(); } catch (IOException e) { }
            }
        }
    }

}
