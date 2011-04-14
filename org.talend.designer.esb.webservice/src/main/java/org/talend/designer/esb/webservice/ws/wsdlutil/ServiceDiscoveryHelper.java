/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.talend.designer.esb.webservice.ws.wsdlutil;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.talend.ws.helper.conf.ServiceHelperConfiguration;

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

    public ServiceDiscoveryHelper(String wsdlUri) throws WSDLException, IOException {
        this(wsdlUri, null);
    }

    public ServiceDiscoveryHelper(String wsdlUri, ServiceHelperConfiguration configuration) throws WSDLException, IOException {
        this.wsdlUri = wsdlUri;
        this.configuration = configuration;
        init();
    }

    /**
     * Read the wsdl and schema
     * 
     * @throws javax.wsdl.WSDLException
     */
    private void init() throws WSDLException, IOException {
        wsdlFactory = WSDLFactory.newInstance();
        // wsdlFactory.
        WSDLReader newWSDLReader = wsdlFactory.newWSDLReader();

        newWSDLReader.setExtensionRegistry(wsdlFactory.newPopulatedExtensionRegistry());
        // newWSDLReader.getExtensionRegistry()
        newWSDLReader.setFeature(com.ibm.wsdl.Constants.FEATURE_VERBOSE, false);
//        if (configuration == null) {
            definition = newWSDLReader.readWSDL(wsdlUri);
//        } else {
//            definition = newWSDLReader.readWSDL(configuration.createWSDLLocator(wsdlUri));
//        }
        schemaCollection = new XmlSchemaCollection();

        schemaCollection.setBaseUri(definition.getDocumentBaseURI());// bchen for bug 8674

        // Types types = definition.getTypes();
        // if (types != null) {
        // List<ExtensibilityElement> extensibilityElements = types.getExtensibilityElements();
        // for (ExtensibilityElement el : extensibilityElements) {
        // if (el instanceof Schema) {
        // Schema schema = (Schema) el;
        //
        // // validateSchemaTargetNamespace(schema);
        // // validateImportSchemaLocation(schema);
        //
        // // schemaCollection.read(schema.getElement());
        // }
        // }
        // }
        // localWsdl = File.createTempFile("service-", ".wsdl");
        // localWsdl.deleteOnExit();
        //
        // wsdlFactory.newWSDLWriter().writeWSDL(definition, new FileOutputStream(localWsdl));
    }

    // bchen for bug 8674
    private void validateSchemaTargetNamespace(Schema schema) {
        if (schema.getElement().getAttributeNode("targetNamespace") == null) {
            String tNs = "";
            try {
                tNs = schema.getElement().getFirstChild().getNextSibling().getAttributes().getNamedItem("namespace")
                        .getNodeValue();
                schema.getElement().setAttribute("targetNamespace", tNs);
            } catch (Exception ex) {

            }
        }
    }

    private void validateImportSchemaLocation(Schema schema) {
        Iterator importSchemaIte = schema.getImports().values().iterator();

        while (importSchemaIte.hasNext()) {
            List importSchemaList = (List) importSchemaIte.next();
            for (Object importSchemaObj : importSchemaList) {
                if (importSchemaObj instanceof SchemaImport) {
                    SchemaImport importSchema = (SchemaImport) importSchemaObj;
                    Schema refSchema = importSchema.getReferencedSchema();
                    String schLocUri = importSchema.getSchemaLocationURI();
                    if (schLocUri != null && refSchema != null) {
                        importSchema.setSchemaLocationURI(refSchema.getDocumentBaseURI());
                        // validateImportSchemaLocation(refSchema);
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
        localWsdl.toURI().toString();
        return "file:/d:/service-8862108648722059106.wsdl";

    }
}
