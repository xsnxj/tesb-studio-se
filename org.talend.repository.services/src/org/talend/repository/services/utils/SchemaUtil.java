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
package org.talend.repository.services.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.w3c.dom.Element;

public class SchemaUtil {

    private static final String SCHEMA_SYSTEM_ID = "custom"; //$NON-NLS-1$

    private final XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();

    public SchemaUtil(Definition wsdlDefinition) {
        if (null != wsdlDefinition.getTypes()) {
            init(wsdlDefinition.getTypes());
        }
    }

    private void init(Types types) {
        @SuppressWarnings("unchecked")
        Collection<Schema> schemas = findExtensibilityElement(types.getExtensibilityElements(), Schema.class);
        for (Schema schema : schemas) {
            createXmlSchema(schema.getElement(), schema.getDocumentBaseURI());
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


    public XmlSchema[] getSchemas() {
        return xmlSchemaCollection.getXmlSchema(SCHEMA_SYSTEM_ID);
    }

}
