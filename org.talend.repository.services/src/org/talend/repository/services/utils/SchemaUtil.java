package org.talend.repository.services.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Types;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.extensions.schema.SchemaReference;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.w3c.dom.Element;

public class SchemaUtil {

    private static final String SCHEMA_SYSTEM_ID = "custom"; //$NON-NLS-1$

    private final XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();

    public SchemaUtil(Definition wsdlDefinition) {
        if (null != wsdlDefinition.getTypes()) {
            init(wsdlDefinition.getTypes());
        } else {
            @SuppressWarnings("unchecked")
            Map<String, List<Import>> imports = wsdlDefinition.getImports();
            for (List<Import> wsdlImports : imports.values()) {
                for (Import wsdlImport : wsdlImports) {
                    if (null != wsdlImport.getDefinition().getTypes()) {
                        init(wsdlImport.getDefinition().getTypes());
                    }
                }
            }
        }
    }

    private void init(Types types) {
        @SuppressWarnings("unchecked")
        Collection<Schema> schemas = findExtensibilityElement(types.getExtensibilityElements(), Schema.class);
        for (Schema schema : schemas) {
            createXmlSchema(schema.getElement(), schema.getDocumentBaseURI());

            Map importElement = schema.getImports();
            if (importElement != null && importElement.size() > 0) {
                findImportSchema(importElement);
            }
        }
    }

    private static <T> Collection<T> findExtensibilityElement(Collection<ExtensibilityElement> extensibilityElements,
            Class<T> clazz) {
        List<T> elements = new ArrayList<T>();
        if (extensibilityElements != null) {
            for (ExtensibilityElement element : extensibilityElements) {
                if (clazz.isAssignableFrom(element.getClass())) {
                    elements.add(clazz.cast(element));
                }
            }
        }
        return elements;
    }

    private void createXmlSchema(Element schemaElement, String documentBase) {
        xmlSchemaCollection.setBaseUri(documentBase);
        xmlSchemaCollection.read(schemaElement, SCHEMA_SYSTEM_ID);
    }

    private void findImportSchema(Map importElement) {
        findImportSchema(importElement, new ArrayList<String>());
    }

    private void findImportSchema(Map importElement,
            Collection<String> documentBaseList) {
        Iterator keyIterator = importElement.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next().toString();
            Vector importEle = (Vector) importElement.get(key);

            for (int i = 0; i < importEle.size(); i++) {
                Map recurseImport = null;
                SchemaImport schemaImport = (SchemaImport) importEle.elementAt(i);
                boolean isHaveImport = false;
                if (schemaImport.getReferencedSchema() != null) {
                    Element refSchema = schemaImport.getReferencedSchema().getElement();
                    String documentBase = schemaImport.getReferencedSchema().getDocumentBaseURI();

                    if (schemaImport != null) {
                        if (schemaImport.getReferencedSchema() != null) {
                            recurseImport = schemaImport.getReferencedSchema().getImports();
                            if (recurseImport != null && recurseImport.size() > 0 && !documentBaseList.contains(documentBase)) {
                                isHaveImport = true;
                                documentBaseList.add(documentBase);
                            }
                        }
                    }

                    createXmlSchema(refSchema, documentBase);
                }

                if (isHaveImport) {
                    findImportSchema(recurseImport);
                }

                if (schemaImport != null) {
                    if (schemaImport.getReferencedSchema() != null) {
                        @SuppressWarnings("unchecked")
                        List<SchemaReference> includeElements = schemaImport.getReferencedSchema().getIncludes();
                        findIncludesSchema(includeElements);
                    }
                }

            }
        }
    }

    private void findIncludesSchema(List<SchemaReference> includeElements) {
        if (includeElements == null || includeElements.size() == 0) {
            return;
        }
        for (SchemaReference schemaReference : includeElements) {
            Element schemaElement = schemaReference.getReferencedSchema().getElement();
            String documentBase = schemaReference.getReferencedSchema().getDocumentBaseURI();
            createXmlSchema(schemaElement, documentBase);
        }
    }

    public XmlSchema[] getSchemas() {
        return xmlSchemaCollection.getXmlSchema(SCHEMA_SYSTEM_ID);
    }

}
