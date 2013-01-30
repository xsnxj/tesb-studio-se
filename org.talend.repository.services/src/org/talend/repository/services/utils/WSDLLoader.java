/*******************************************************************************
 * Copyright (c) 2008 SOPERA GmbH
 * All rights reserved. 
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.talend.repository.services.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * class WSDLLoader
 * 
 * @author amarkevich
 */
public class WSDLLoader {

	private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";

	private static DocumentBuilder documentBuilder = null;

	private final Map<String, Collection<URL>> importedSchemas = new HashMap<String, Collection<URL>>();

	public ByteArrayOutputStream load(String wsdlLocation) throws InvocationTargetException {
		try {
			final URL wsdlURL = getURL(null, wsdlLocation);
			final Document wsdlDocument = getDocumentBuilder().parse(wsdlLocation);

			final NodeList schemas = wsdlDocument.getElementsByTagNameNS(
					XSD_NS, "schema");
			// copy elements to avoid reiterate
			Element[] schemaElements = new Element[schemas.getLength()];
			for(int index = 0; index < schemas.getLength(); ++index) {
				schemaElements[index] = (Element)schemas.item(index);
			}
			for (Element schema : schemaElements) {
				importedSchemas.put(schema.getAttribute("targetNamespace"), new HashSet<URL>()); //$NON-NLS-1$
				loadSchemas (schema, schema, wsdlURL);
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Source xmlSource = new DOMSource(wsdlDocument);
			Result outputTarget = new StreamResult(outputStream);
			TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
			//return new ByteArrayInputStream(outputStream.toByteArray());
			return outputStream;
		} catch (InvocationTargetException e) {
			throw e;
		} catch (Exception e) {
			throw new InvocationTargetException(e, "Error occured while processing schemas");
		} finally {
			importedSchemas.clear();
		}
	}
	
	/*
	  @param contextURL the context in which to attempt to resolve the spec.
	  Effectively a document base.
	 */
	private static URL getURL(final URL contextURL, final String spec) throws MalformedURLException {
		try {
			return new URL(contextURL, spec);
		} catch (MalformedURLException e) {
			File tempFile = new File(spec);
			if (contextURL == null ||
					(contextURL != null && tempFile.isAbsolute())) {
				return tempFile.toURI().toURL();
			}

			// only reach here if the contextURL !null, spec is relative path and
			// MalformedURLException thrown
			throw e;
		}
	}

	/*
	 * Importing xsd:import and xsd:include nodes
	 */
	private void loadSchemas(
			final Element ownerSchemaNode,
			final Element schemaNode,
			final URL ownerFile) throws InvocationTargetException {
		final Map<String, String> prefixMapping = new HashMap<String, String>();

		Node childNode = schemaNode.getFirstChild();
		while(childNode != null) {
			Node nextNode = childNode.getNextSibling();
			if(childNode.getNodeType() == Node.ELEMENT_NODE
					&& XSD_NS.equals(childNode.getNamespaceURI())) {
				if("import".equals(childNode.getLocalName())) {
					Element importElement = (Element)childNode;

					String schemaLocation = importElement.getAttribute("schemaLocation"); //$NON-NLS-1$
					final String schemaNS = importElement.getAttribute("namespace"); //$NON-NLS-1$

					if ((null != schemaLocation && 0 != schemaLocation.length())
							&& (null != schemaNS && 0 != schemaNS.length())) {

						if (!importedSchemas.containsKey(schemaNS)) {
							try {
								URL schemaURL = getURL(ownerFile, schemaLocation);
								Element schemaElement = loadSchema(schemaURL, false);
								Element schemaImported =
									(Element)ownerSchemaNode.getOwnerDocument().importNode(
											schemaElement, true);
								ownerSchemaNode.getParentNode().insertBefore(schemaImported, ownerSchemaNode);

								// add the schemas doc to the schemas imported map.
								importedSchemas.put(schemaNS, new HashSet<URL>());

								loadSchemas (schemaImported, schemaImported, schemaURL);
							} catch (InvocationTargetException e) {
								throw e;
							} catch (Exception e) {
								final String errMsg =
									"Unexpected error while loading external schema file: " + e.getMessage(); 
								throw new InvocationTargetException(e, errMsg);
							}
						//} else {
						// The URI = [" + schemaNS + "] is already present (skipped)
						}
					} else {
						// just ignore
//						final String errMsg = "The schema import is incorrect: schemaLocation = [" +
//							schemaLocation + "], namespace = [" + schemaNS + "]";
//						throw new InvocationTargetException(new Exception(errMsg));
					}

					// update import node
					importElement.removeAttribute("schemaLocation");
					// move up
					Node refChild;
					for (refChild = importElement.getPreviousSibling(); refChild != null; refChild = refChild.getPreviousSibling()) {
						if (refChild.getNodeType() == Node.ELEMENT_NODE
							&& XSD_NS.equals(refChild.getNamespaceURI())
							&& "import".equals(refChild.getLocalName())) {
							refChild = refChild.getNextSibling();
							break;
						}
					}
					if (null == refChild) {
						for (refChild = importElement.getParentNode().getFirstChild();
								refChild.getNodeType() != Node.ELEMENT_NODE; refChild = refChild.getNextSibling()) {
						}
					}
					importElement.getParentNode().insertBefore(importElement, refChild);
				} else if ("include".equals(childNode.getLocalName())) {
					Element includeElement = (Element)childNode;

					String schemaLocation = includeElement.getAttribute("schemaLocation"); //$NON-NLS-1$
					if ((null == schemaLocation || 0 == schemaLocation.length())) {
						final String errMsg = "The schema include is incorrect: schemaLocation = [" + schemaLocation + "]";
						throw new InvocationTargetException(new Exception(errMsg));
					}
					try {
						URL schemaURL = getURL(ownerFile, schemaLocation);
						final String schemaNamespace = ownerSchemaNode.getAttribute("targetNamespace");
						Collection<URL> includedSchemas = importedSchemas.get(schemaNamespace);
						if(includedSchemas.add(schemaURL)) {
							Element schemaIncluded = loadSchema(schemaURL, true);
							String includeNamespace = schemaIncluded.getAttribute("targetNamespace");
							if((includeNamespace != null && includeNamespace.length() != 0) // skip chameleon schema check
								&& !schemaNamespace.equals(includeNamespace)) {
								String errMsg = "The schema include is incorrect: namespaces are not equals";
								throw new InvocationTargetException(new Exception(errMsg));
							}
							loadSchemas (ownerSchemaNode, schemaIncluded, schemaURL);

							// add attributes
							NamedNodeMap nnm = schemaIncluded.getAttributes();
							for(int i = 0; i < nnm.getLength(); ++i) {
								Node attr = nnm.item(i);
								String attrName = attr.getNodeName();
								String attrValueNew = attr.getNodeValue();
								if(isAttributePresent(schemaNode, attrName)) {
									String attrValueOld = schemaNode.getAttribute(attrName);
									if(attrName.startsWith("xmlns") && !attrValueNew.equals(attrValueOld)) {
										String prefixOld = attrName.substring(attrName.indexOf(':') + 1);
										// looking for existing prefix
										String prefixNew = getPrefix(schemaNode, attrValueNew);
										if (null == prefixNew) {
											prefixNew = generatePrefix(schemaIncluded, prefixOld);
											schemaNode.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefixNew, attrValueNew);
										}
										prefixMapping.put(prefixOld, prefixNew);
									}
								} else {
									// namespace declaration
									if(attrName.startsWith("xmlns")) {
										// looking for existing prefix
										String prefixNew = getPrefix(schemaNode, attrValueNew);
										if (null != prefixNew) {
											String prefixOld = attrName.substring(attrName.indexOf(':') + 1);
											prefixMapping.put(prefixOld, prefixNew);
										} else {
											schemaNode.setAttributeNS("http://www.w3.org/2000/xmlns/", attrName, attrValueNew);
										}
									} else {
										schemaNode.setAttribute(attrName, attrValueNew);
									}
								}
							}

							// add child elements
							Node firstIncludedNode = null;
							NodeList childs = schemaIncluded.getChildNodes();
							/*iterate:*/ for(int i = 0; i < childs.getLength(); ++i) {
								Node child = childs.item(i);
								// check if element already present
								/*if(child.getNodeType() == Node.ELEMENT_NODE) {
									final String name = ((Element)child).getAttribute("name");
									if(name.length() > 0) {
										for(Node node = schemaNode.getFirstChild(); node != null; node = node.getNextSibling()) {
											if(node.getNodeType() == Node.ELEMENT_NODE) {
												if(name.equals(((Element)node).getAttribute("name"))) {
													continue iterate;
												}
											}
											
										}
									}
								}*/
								child = schemaNode.getOwnerDocument().importNode(child, true);
								if(child.getNodeType() == Node.ELEMENT_NODE) {
									Element element = (Element)child;
									fixPrefixes(element, prefixMapping);
								}
								child = schemaNode.insertBefore(child, includeElement);
								if(firstIncludedNode == null) {
									firstIncludedNode = child;
								}
							}
							if(firstIncludedNode != null) {
								nextNode = firstIncludedNode;
							}
						}
					} catch (InvocationTargetException e) {
						throw e;
					} catch (Exception e) {
						String errMsg =
							"Unexpected error while loading external schema file: " + e.getMessage(); 
						throw new InvocationTargetException(e, errMsg);
					}
					// remove include node
					includeElement.getParentNode().removeChild(includeElement);
				}
			}
			childNode = nextNode;
		}
	}

	private final static String generatePrefix(final Element element, final String initialPrefix) {
		String prefix = initialPrefix;
		
		for(
			int index = 0;
			isAttributePresent(element, "xmlns:" + prefix); //$NON-NLS-1$
			++index) {
			prefix = initialPrefix + index;
		}
		return prefix;
	}

	private final static String getPrefix(final Element element, final String namespace) {
		NamedNodeMap nnm = element.getAttributes();
		for(int i = 0; i < nnm.getLength(); ++i) {
			Node attr = nnm.item(i);
			String attrNameSchema = attr.getNodeName();
			if(attrNameSchema.startsWith("xmlns") && namespace.equals(attr.getNodeValue())) {
				int index = attrNameSchema.indexOf(':');
				if (-1 != index) {
					return attrNameSchema.substring(attrNameSchema.indexOf(':') + 1);
				} else {
					return "";
				}
			}
		}
		return null;
	}

	private final static boolean isAttributePresent(Element element, String attribute) {
		return (element.getAttributeNode(attribute) != null);
	}

	private static final void fixPrefixes(final Element element, Map<String, String> prefixMapping) {
		NamedNodeMap nnm = element.getAttributes();
		// update element name
		String prefix = element.getPrefix();
		String prefixNew = prefixMapping.get(prefix);
		if(prefixNew != null) {
			element.setPrefix(prefixNew);
		}
		// update values
		for(int i = 0; i < nnm.getLength(); ++i) {
			Node attr = nnm.item(i);
			String value = attr.getNodeValue(); 
			if(value != null) {
				// TODO: support for default namespace?
				int index = value.indexOf(':');
				if(index != -1) {
					String prefixOld = value.substring(0, index);
					/*String*/ prefixNew = prefixMapping.get(prefixOld);
					if(prefixNew != null) {
						if ("".equals(prefixNew)) {
							attr.setNodeValue(value.substring(index + 1));
						} else {
							attr.setNodeValue(prefixNew + ':' + value.substring(index + 1));
						}
					}
				}
			}
		}
		for(Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				fixPrefixes((Element)child, prefixMapping);
			}
		}
	}

	private static final Element loadSchema(URL schemaFile, boolean cleanup) throws IOException, SAXException, ParserConfigurationException {
		InputStream is = null;
		try {
			is = schemaFile.openStream();
			Element schemaElement = getDocumentBuilder().parse(is).getDocumentElement();
			if (cleanup) {
				cleanupSchemaElement(schemaElement);
			}
			return schemaElement;
		} finally {
			if (null != is) {
				is.close();
			}
		}
	}

	private static final void cleanupSchemaElement(final Element element) {
		Node node = element.getFirstChild();
		while(node != null) {
			Node next = node.getNextSibling();
			if(Node.COMMENT_NODE == node.getNodeType()) {
				element.removeChild(node);
			} else if(Node.ELEMENT_NODE == node.getNodeType()) {
				Element child = (Element)node;
				if(XSD_NS.equals(child.getNamespaceURI())
						&& "annotation".equals(child.getLocalName())) {
					element.removeChild(child);
				} else {
					cleanupSchemaElement(child);
				}
			}
			node = next;
		}
	}

	private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		if (documentBuilder == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			documentBuilder = factory.newDocumentBuilder();
		}
		return documentBuilder;
	}
}
