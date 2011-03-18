/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.talend.ws.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.talend.ws.helper.conf.ServiceHelperConfiguration;
import org.w3c.dom.Element;

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

    private ServiceHelperConfiguration configuration;

    private File localWsdl;

    private boolean isLocal;

    private String tempPath;

    private File wsdlTmpDir;

    public ServiceDiscoveryHelper(String wsdlUri) throws WSDLException, IOException, TransformerException, URISyntaxException {
        this(wsdlUri, null, null);
    }

    public ServiceDiscoveryHelper(String wsdlUri, String tempPath) throws WSDLException, IOException, TransformerException,
            URISyntaxException {
        this(wsdlUri, null, tempPath);
    }

    public ServiceDiscoveryHelper(String wsdlUri, ServiceHelperConfiguration configuration) throws WSDLException, IOException,
            TransformerException, URISyntaxException {
        this(wsdlUri, configuration, null);
    }

    public ServiceDiscoveryHelper(String wsdlUri, ServiceHelperConfiguration configuration, String tempPath)
            throws WSDLException, IOException, TransformerException, URISyntaxException {
        this.wsdlUri = wsdlUri;
        this.configuration = configuration;
        this.tempPath = tempPath;
        init();
    }

    /**
     * Read the wsdl and schema
     * 
     * @throws javax.wsdl.WSDLException
     * @throws TransformerException
     * @throws URISyntaxException
     */
    private void init() throws WSDLException, IOException, TransformerException, URISyntaxException {
        wsdlFactory = WSDLFactory.newInstance();
        WSDLReader newWSDLReader = wsdlFactory.newWSDLReader();
        newWSDLReader.setFeature(com.ibm.wsdl.Constants.FEATURE_VERBOSE, false);

        if (configuration == null) {
            definition = newWSDLReader.readWSDL(wsdlUri);
        } else {
            definition = newWSDLReader.readWSDL(configuration.createWSDLLocator(wsdlUri));
        }
        schemaCollection = new XmlSchemaCollection();

        createTempWsdlFile(definition);

        // bchen bug for 8674
        int tmpCount = 0;
        String tmpTNName = "";
        // end

        Types types = definition.getTypes();
        if (types != null) {
            List<ExtensibilityElement> extensibilityElements = types.getExtensibilityElements();
            for (ExtensibilityElement el : extensibilityElements) {
                if (el instanceof Schema) {
                    Schema schema = (Schema) el;
                    // bchen bug for 8764
                    // set base uri for relative path in schemaLocation.
                    schemaCollection.setBaseUri(schema.getDocumentBaseURI());

                    if (!isLocal) {
                        createTempImportSchemaFile(schema, localWsdl);
                    }
                    // synthetic URI for the schemas without targetNamespace,avoid conflict error.
                    if (schema.getElement().getAttributeNode("targetNamespace") == null) {
                        tmpTNName = schema.getDocumentBaseURI() + "tmpSchema" + tmpCount;
                        schemaCollection.read(schema.getElement(), tmpTNName);
                        tmpCount++;
                    } else {
                        schemaCollection.read(schema.getElement());
                    }
                    // ends
                }
            }
        }
        // not suitable for relative path in schemaLocation(between wsdl and xsd file).
        // localWsdl = File.createTempFile("service-", ".wsdl");
        // localWsdl.deleteOnExit();
        if (!isLocal) {
            wsdlFactory.newWSDLWriter().writeWSDL(definition, new FileOutputStream(localWsdl));
        }
    }

    private void createTempWsdlFile(Definition definition) throws MalformedURLException, FileNotFoundException, WSDLException {
        URL url = new URL(definition.getDocumentBaseURI());
        if ("http".equals(url.getProtocol()) || "https".equals(url.getProtocol())) {
            if (tempPath != null && !"".equals(tempPath)) {
                wsdlTmpDir = new File(tempPath, "wsdl" + String.valueOf(new Date().getTime()));
            } else {
                wsdlTmpDir = new File(System.getProperty("java.io.tmpdir"), "wsdl" + String.valueOf(new Date().getTime()));
            }

            if (!wsdlTmpDir.mkdir()) {
                throw new SecurityException("Unable to create temporary directory," + wsdlTmpDir.getAbsolutePath());
            }
            wsdlTmpDir.deleteOnExit();
            String path = url.getPath();

            path = path.substring(0, path.lastIndexOf("/"));

            File parentDir = new File(wsdlTmpDir, path);
            parentDir.mkdirs();
            localWsdl = new File(parentDir, "mainWSDL.wsdl");
            targetDeleteTmpFile(localWsdl);
            isLocal = false;
        } else {
            localWsdl = new File(wsdlUri); // file Protocol, no need to create temp wsdl file for local.
            isLocal = true;
        }
    }

    private void targetDeleteTmpFile(File file) {
        List<File> fileList = new ArrayList<File>();
        targetDeleteTmpFile(fileList, file);
        for (int i = fileList.size() - 1; i >= 0; i--) {
            fileList.get(i).deleteOnExit();
        }
    }

    private void targetDeleteTmpFile(List<File> fileList, File file) {
        if (!wsdlTmpDir.equals(file)) {
            fileList.add(file);
            targetDeleteTmpFile(fileList, file.getParentFile());
        }
    }

    Map<String, String> importXsdMap = new HashMap<String, String>();

    private void createTempImportSchemaFile(Schema schema, File baseFile) throws FileNotFoundException, TransformerException,
            URISyntaxException {
        Iterator importSchemaIte = schema.getImports().values().iterator();
        while (importSchemaIte.hasNext()) {
            List importSchemaList = (List) importSchemaIte.next();
            for (Object importSchemaObj : importSchemaList) {
                if (importSchemaObj instanceof SchemaImport) {
                    SchemaImport importSchema = (SchemaImport) importSchemaObj;
                    String ns = importSchema.getNamespaceURI();
                    Schema refSchema = importSchema.getReferencedSchema();
                    String schLocUri = importSchema.getSchemaLocationURI();
                    if (schLocUri != null && refSchema != null && !"true".equals(importXsdMap.get(ns))) {
                        Element e = refSchema.getElement();
                        DOMSource domSource = new DOMSource(e);
                        FileOutputStream fileOutputStream = null;

                        String tempXsdPath = importSchema.getSchemaLocationURI();
                        URI schemaLocationUri = new URI(tempXsdPath);
                        if (!schemaLocationUri.isAbsolute()) {
                            File xsdFile = new File(baseFile.getParentFile(), tempXsdPath);
                            xsdFile.getParentFile().mkdirs();
                            targetDeleteTmpFile(xsdFile);

                            fileOutputStream = new FileOutputStream(xsdFile);
                            StreamResult streamResult = new StreamResult(fileOutputStream);
                            TransformerFactory tf = TransformerFactory.newInstance();
                            Transformer transformer = tf.newTransformer();
                            transformer.transform(domSource, streamResult);

                            importXsdMap.put(ns, "true");
                            createTempImportSchemaFile(refSchema, xsdFile);
                        }
                    }
                }
            }
        }
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

    public String getLocalWsdlUri() {
        return localWsdl.toURI().toString();
    }
}
