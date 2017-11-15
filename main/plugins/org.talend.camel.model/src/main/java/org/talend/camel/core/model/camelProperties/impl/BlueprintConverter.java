package org.talend.camel.core.model.camelProperties.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class BlueprintConverter {

    private static class NamespaceIterator implements Iterator<Namespace> {

        Namespace namespace;
        Iterator<Namespace> others;

        public NamespaceIterator(Namespace namespace, Iterator<Namespace> others) {
            super();
            this.namespace = namespace;
            this.others = others;
        }

        @Override
        public boolean hasNext() {
            return namespace != null || (others != null && others.hasNext());
        }

        @Override
        public Namespace next() {
            if (namespace != null) {
                Namespace result = namespace;
                namespace = null;
                return result;
            }
            return others == null ? null : others.next();
        }
    }

    private static final String BLUEPRINT_NS = "http://www.osgi.org/xmlns/blueprint/v1.0.0";
    private static final String BLUEPRINT_ROOT = "blueprint";
    private static final String SPRING_NS = "http://www.springframework.org/schema/beans";
    private static final String SPRING_ROOT = "beans";
    private static final String SPRING_IMPORT = "import";
    private static final String SPRING_RESOURCE = "resource";

    XMLInputFactory inputFactory;
    XMLOutputFactory outputFactory;
    XMLEventFactory eventFactory;
    EndElement bpEnd = null;

    public BlueprintConverter() {
        super();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            inputFactory = XMLInputFactory.newFactory();
            outputFactory = XMLOutputFactory.newFactory();
            eventFactory = XMLEventFactory.newFactory();
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

    @SuppressWarnings("unchecked")
    public String toBlueprintContent(String springContent, Set<String> importFileNames) throws XMLStreamException {
        XMLEventReader xmlIn = inputFactory.createXMLEventReader(new StringReader(springContent));
        StringWriter output = new StringWriter();
        XMLEventWriter xmlOut = outputFactory.createXMLEventWriter(output);
        try {
            while (xmlIn.hasNext()) {
                XMLEvent evt = xmlIn.nextEvent();
                if (evt.isStartElement()) {
                    StartElement el = (StartElement) evt;
                    QName tagName = el.getName();
                    if (SPRING_NS.equals(tagName.getNamespaceURI())) {
                        if (SPRING_ROOT.equals(tagName.getLocalPart())) {
                            if (bpEnd != null) {
                                throw new XMLStreamException("Unexpected duplicate \"beans\" element encountered. ");
                            }
                            String nsprefix = "bp";
                            int counter = 0;
                            while (el.getNamespaceURI(nsprefix) != null) {
                                nsprefix = "bp" + (++counter);
                            }
                            Namespace bpns = eventFactory.createNamespace(nsprefix, BLUEPRINT_NS);
                            xmlOut.add(eventFactory.createStartElement(nsprefix, BLUEPRINT_NS, BLUEPRINT_ROOT,
                                    el.getAttributes(), new NamespaceIterator(bpns, el.getNamespaces())));
                            xmlOut.add(bpns);
                            bpEnd = eventFactory.createEndElement(nsprefix, BLUEPRINT_NS, BLUEPRINT_ROOT);
                            continue;
                        }
                        if (SPRING_IMPORT.equals(tagName.getLocalPart())) {
                            Attribute att = el.getAttributeByName(QName.valueOf(SPRING_RESOURCE));
                            if (att != null) {
                                importFileNames.add(att.getValue());
                            }
                            while (xmlIn.hasNext()) {
                                XMLEvent ignored = xmlIn.nextEvent();
                                if (ignored.isEndElement()) {
                                    QName endTagName = ((EndElement) ignored).getName();
                                    if (SPRING_NS.equals(endTagName.getNamespaceURI()) &&
                                            SPRING_IMPORT.equals(endTagName.getLocalPart())) {
                                        break;
                                    }
                                }
                            }
                            continue;
                        }
                    }
                }
                if (evt.isEndElement()) {
                    EndElement el = (EndElement) evt;
                    QName tagName = el.getName();
                    if (SPRING_NS.equals(tagName.getNamespaceURI()) && SPRING_ROOT.equals(tagName.getLocalPart())) {
                        xmlOut.add(bpEnd);
                        bpEnd = null;
                        continue;
                    }
                }
                xmlOut.add(evt);
            }
        } finally {
            try {
                xmlIn.close();
            } catch (Exception e) {
                // ignore
            }
            try {
                xmlOut.close();
            } catch (Exception e) {
                // ignore
            }
        }
        return output.toString();
    }
}
