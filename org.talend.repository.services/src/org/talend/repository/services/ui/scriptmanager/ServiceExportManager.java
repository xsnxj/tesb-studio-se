package org.talend.repository.services.ui.scriptmanager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.jar.Manifest;

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
import org.talend.core.model.properties.ProcessItem;
import org.talend.repository.documentation.ExportFileResource;
import org.talend.repository.services.Messages;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JobJavaScriptOSGIForESBManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ServiceExportManager extends JobJavaScriptOSGIForESBManager {

	private static final String SERVICE_NODE = "service"; //$NON-NLS-1$
	private static final String CLASS = "class"; //$NON-NLS-1$
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String BEAN_NODE = "bean"; //$NON-NLS-1$
	private static final String BLUEPRINT_NODE = "blueprint"; //$NON-NLS-1$
	private static final String BLUEPRINT_NS = "http://www.osgi.org/xmlns/blueprint/v1.0.0"; //$NON-NLS-1$
	private static final String TALEND_JOB_API = "routines.system.api.TalendJob"; //$NON-NLS-1$
	private static final String SERVICE_PROPERTIES_NODE = "service-properties"; //$NON-NLS-1$
	
	private static Logger logger = Logger.getLogger(ServiceExportManager.class);
	
	private String serviceName;

	public ServiceExportManager(String serviceName) {
		this.serviceName = serviceName;
	}

	/* (non-Javadoc)
	 * @see org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JobJavaScriptOSGIForESBManager#readAndReplaceInXmlTemplate(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	protected void readAndReplaceInXmlTemplate(String inputFile,
			String outputFile, String jobName, String jobClassName,
			String itemType) {
		File out = new File(outputFile);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			// read/create
			docBuilder = docFactory.newDocumentBuilder();
			Document doc;
			Element blueprint;
			if (out.exists()) {
				doc = docBuilder.parse(out);
				blueprint = (Element) doc.getFirstChild();
				if ((!BLUEPRINT_NODE.equalsIgnoreCase(blueprint.getNodeName())) /*||
						(!BLUEPRINT_NS.equalsIgnoreCase(blueprint.getNamespaceURI()))*/){
					throw new IOException(Messages.ServiceExportManager_Exception_invalid_blueprint_xml);
				}
			} else {
				doc = docBuilder.newDocument();
				blueprint = doc.createElementNS(BLUEPRINT_NS, BLUEPRINT_NODE);
				doc.appendChild(blueprint);
			}
			//add
			Element bean = createKid(blueprint, BEAN_NODE);
			bean.setAttribute(ID, jobName);
			bean.setAttribute(CLASS, jobClassName);
			
			Element service = createKid(blueprint, SERVICE_NODE);
			service.setAttribute("ref", jobName); //$NON-NLS-1$
			service.setAttribute("interface", TALEND_JOB_API); //$NON-NLS-1$
			
			Element serviceProperties = createKid(service, SERVICE_PROPERTIES_NODE);
			addProperty(serviceProperties, "name", jobName); //$NON-NLS-1$
			addProperty(serviceProperties, "type", itemType); //$NON-NLS-1$
			
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
	
	private void addProperty(Element props, String key,
			String value) {
		Element prop = createKid(props, "entry"); //$NON-NLS-1$
		prop.setAttribute("key", key); //$NON-NLS-1$
		prop.setAttribute("value", value); //$NON-NLS-1$
	}

	private Element createKid(Element parent, String kidsName) {
		Element kid = parent.getOwnerDocument().createElement(kidsName);
		parent.appendChild(kid);
		return kid;
	}

	/* (non-Javadoc)
	 * @see org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JobJavaScriptOSGIForESBManager#getManifest(org.talend.repository.documentation.ExportFileResource, java.util.List, java.lang.String)
	 */
	@Override
	protected Manifest getManifest(ExportFileResource libResource,
			List<ProcessItem> itemToBeExport, String bundleName)
			throws IOException {
		return super.getManifest(libResource, itemToBeExport, serviceName);
	}

	
}
