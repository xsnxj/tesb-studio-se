package org.talend.designer.esb.webservice.ws.wsdlutil;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.reader.SchemaReader;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

/**
 *
 * @author gcui
 */
public class XMLSupport {

    public static Schema convertElementToSchema(Element element, String documentBase) throws IOException {

        String content = outputString(element);
        // System.out.println(content);
        if (content != null) {
            return readSchema(new StringReader(content), documentBase);
        }

        return null;
    }

    public static String outputString(Element elem) {
        XMLOutputter xmlWriter = new XMLOutputter("    ", true);
        return xmlWriter.outputString(elem);
    }

    public static Schema readSchema(Reader reader, String documentBase) throws IOException {

        InputSource inputSource = new InputSource(reader);
        inputSource.setSystemId(documentBase);

        SchemaReader schemaReader = new SchemaReader(inputSource);
        schemaReader.setValidation(false);
        Schema schema = null;
        schema = schemaReader.read();

        return schema;
    }
}
