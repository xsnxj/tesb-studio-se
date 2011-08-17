package org.talend.designer.camel.spring.core;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.talend.designer.camel.spring.core.models.SpringRoute;
import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.talend.designer.camel.spring.core.saver.AbstractComponentSaver;
import org.talend.designer.camel.spring.core.saver.ActivemqComponentSaver;
import org.talend.designer.camel.spring.core.saver.AggregateComponentSaver;
import org.talend.designer.camel.spring.core.saver.BeanComponentSaver;
import org.talend.designer.camel.spring.core.saver.CatchComponentSaver;
import org.talend.designer.camel.spring.core.saver.ContentEnrichComponentSaver;
import org.talend.designer.camel.spring.core.saver.ConvertBodyComponentSaver;
import org.talend.designer.camel.spring.core.saver.CxfComponentSaver;
import org.talend.designer.camel.spring.core.saver.DelayerComponentSaver;
import org.talend.designer.camel.spring.core.saver.DynamicComponentSaver;
import org.talend.designer.camel.spring.core.saver.ExchangePatternComponentSaver;
import org.talend.designer.camel.spring.core.saver.FileComponentSaver;
import org.talend.designer.camel.spring.core.saver.FinallyComponentSaver;
import org.talend.designer.camel.spring.core.saver.FtpComponentSaver;
import org.talend.designer.camel.spring.core.saver.IdempotentComponentSaver;
import org.talend.designer.camel.spring.core.saver.InterceptComponentSaver;
import org.talend.designer.camel.spring.core.saver.JmsComponentSaver;
import org.talend.designer.camel.spring.core.saver.LoadBalanceComponentSaver;
import org.talend.designer.camel.spring.core.saver.LoopComponentSaver;
import org.talend.designer.camel.spring.core.saver.MsgEndpointComponentSaver;
import org.talend.designer.camel.spring.core.saver.MsgFilterComponentSaver;
import org.talend.designer.camel.spring.core.saver.MsgRouterComponentSaver;
import org.talend.designer.camel.spring.core.saver.MulticastComponentSaver;
import org.talend.designer.camel.spring.core.saver.OnExceptionComponentSaver;
import org.talend.designer.camel.spring.core.saver.OtherwiseComponentSaver;
import org.talend.designer.camel.spring.core.saver.PipeLineComponentSaver;
import org.talend.designer.camel.spring.core.saver.ProcessorComponentSaver;
import org.talend.designer.camel.spring.core.saver.RoutingSlipComponentSaver;
import org.talend.designer.camel.spring.core.saver.SetBodyComponentSaver;
import org.talend.designer.camel.spring.core.saver.SetHeaderComponentSaver;
import org.talend.designer.camel.spring.core.saver.SplitterComponentSaver;
import org.talend.designer.camel.spring.core.saver.StopComponentSaver;
import org.talend.designer.camel.spring.core.saver.ThrottlerComponentSaver;
import org.talend.designer.camel.spring.core.saver.TryComponentSaver;
import org.talend.designer.camel.spring.core.saver.WhenComponentSaver;
import org.talend.designer.camel.spring.core.saver.WireTapComponentSaver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CamelSpringSaver implements ICamelSpringConstants {

	private String outputPath;
	private Element rootElement;
	private Element contextElement;

	private AbstractComponentSaver[] savers = new AbstractComponentSaver[LENGTH];
	private Document document;

	public CamelSpringSaver(String outputPath) {
		super();
		this.outputPath = outputPath;
	}

	public boolean save(SpringRoute[] routes, boolean hasActiveMQ,
			boolean hasCxf) throws ParserConfigurationException,
			FileNotFoundException, TransformerException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(false);
			document = factory.newDocumentBuilder().newDocument();

			initialDocument(document, hasActiveMQ, hasCxf);
			initialSavers();
			beforeSave();
			if (routes != null) {
				for (SpringRoute sr : routes) {
					save(sr, document, rootElement, contextElement);
				}
			}
			outputDocument(document);
		} catch (ParserConfigurationException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw e;
		} catch (TransformerException e) {
			throw e;
		} finally {
			afterSaved();
		}
		return true;
	}

	private void beforeSave() {
		for (AbstractComponentSaver s : savers) {
			if (s != null) {
				s.beforeSave();
			}
		}
	}

	private void initialSavers() {
		savers[FILE] = new FileComponentSaver(document, rootElement,
				contextElement);
		savers[CXF] = new CxfComponentSaver(document, rootElement,
				contextElement);
		savers[JMS] = new JmsComponentSaver(document, rootElement,
				contextElement);
		savers[FTP] = new FtpComponentSaver(document, rootElement,
				contextElement);
		savers[ACTIVEMQ] = new ActivemqComponentSaver(document, rootElement,
				contextElement);
		savers[MSGENDPOINT] = new MsgEndpointComponentSaver(document,
				rootElement, contextElement);
		savers[SPLIT] = new SplitterComponentSaver(document, rootElement,
				contextElement);
		savers[SETHEADER] = new SetHeaderComponentSaver(document, rootElement,
				contextElement);
		savers[SETBODY] = new SetBodyComponentSaver(document, rootElement,
				contextElement);
		savers[CONVERT] = new ConvertBodyComponentSaver(document, rootElement,
				contextElement);
		savers[ENRICH] = new ContentEnrichComponentSaver(document, rootElement,
				contextElement);
		savers[INTERCEPT] = new InterceptComponentSaver(document, rootElement,
				contextElement);
		savers[EXCEPTION] = new OnExceptionComponentSaver(document,
				rootElement, contextElement);
		savers[TRY] = new TryComponentSaver(document, rootElement,
				contextElement);
		savers[BEAN] = new BeanComponentSaver(document, rootElement,
				contextElement);
		savers[PF] = new PipeLineComponentSaver(document, rootElement,
				contextElement);
		savers[LOOP] = new LoopComponentSaver(document, rootElement,
				contextElement);
		savers[STOP] = new StopComponentSaver(document, rootElement,
				contextElement);
		savers[DELAY] = new DelayerComponentSaver(document, rootElement,
				contextElement);
		savers[PROCESSOR] = new ProcessorComponentSaver(document, rootElement,
				contextElement);
		savers[THROTTLER] = new ThrottlerComponentSaver(document, rootElement,
				contextElement);
		savers[AGGREGATE] = new AggregateComponentSaver(document, rootElement,
				contextElement);
		savers[DYNAMIC] = new DynamicComponentSaver(document, rootElement,
				contextElement);
		savers[IDEM] = new IdempotentComponentSaver(document, rootElement,
				contextElement);
		savers[BALANCE] = new LoadBalanceComponentSaver(document, rootElement,
				contextElement);
		savers[FILTER] = new MsgFilterComponentSaver(document, rootElement,
				contextElement);
		savers[MSGROUTER] = new MsgRouterComponentSaver(document, rootElement,
				contextElement);
		savers[MULTICAST] = new MulticastComponentSaver(document, rootElement,
				contextElement);
		savers[ROUTINGSLIP] = new RoutingSlipComponentSaver(document,
				rootElement, contextElement);
		savers[WIRETAP] = new WireTapComponentSaver(document, rootElement,
				contextElement);
		savers[PATTERN] = new ExchangePatternComponentSaver(document,
				rootElement, contextElement);
		savers[WHEN] = new WhenComponentSaver(document, rootElement,
				contextElement);
		savers[CATCH] = new CatchComponentSaver(document, rootElement,
				contextElement);
		savers[FINALLY] = new FinallyComponentSaver(document, rootElement,
				contextElement);
		savers[OTHER] = new OtherwiseComponentSaver(document, rootElement,
				contextElement);

	}

	private void initialDocument(Document document, boolean hasActiveMQ,
			boolean hasCxf) {
		// create beans
		rootElement = document.createElement(BEANS_ELE);
		document.appendChild(rootElement);

		// add xmlns
		rootElement.setAttribute(XMLNS, BEANS_NS);

		// add xmlns:xsi
		rootElement.setAttribute(XMLNS_XSI, XSI_NS);

		// add xmlns:cxf
		if (hasCxf) {
			rootElement.setAttribute(XMLNS_CXF, CXF_NS);
		}

		// add xmlns:broker
		if (hasActiveMQ) {
			rootElement.setAttribute(XMLNS_AMQ, AMQ_NS);
		}

		// add xsi:schemaLocation
		StringBuilder sb = new StringBuilder();
		sb.append(BEANS_NS);
		sb.append(" ");
		sb.append(BEANS_XSD);
		sb.append(" ");
		sb.append(CAMEL_NS);
		sb.append(" ");
		sb.append(CAMEL_XSD);
		if (hasActiveMQ) {
			sb.append(" ");
			sb.append(AMQ_NS);
			sb.append(" ");
			sb.append(AMQ_XSD);
		}
		if (hasCxf) {
			sb.append(" ");
			sb.append(CXF_NS);
			sb.append(" ");
			sb.append(CXF_XSD);
		}
		rootElement.setAttribute(NS_LOCATION, sb.toString());

		// add imports
		if (hasCxf) {
			Element importEle = document.createElement(IMPORT_ELE);
			importEle.setAttribute(RESOURCE_ATT, IMPORT_CXF);
			rootElement.appendChild(importEle);

			importEle = document.createElement(IMPORT_ELE);
			importEle.setAttribute(RESOURCE_ATT, IMPORT_SOAP);
			rootElement.appendChild(importEle);

			importEle = document.createElement(IMPORT_ELE);
			importEle.setAttribute(RESOURCE_ATT, IMPORT_JETTY);
			rootElement.appendChild(importEle);
		}

		// create camelContext
		contextElement = document.createElement(CAMEL_CONTEXT_ELE);

		contextElement.setAttribute("id", "camel-1");
		contextElement.setAttribute(XMLNS, CAMEL_NS);
		rootElement.appendChild(contextElement);
	}

	private void save(SpringRoute sr, Document document, Element rootElement,
			Element parentElement) {
		SpringRouteNode from = sr.getFrom();
		Element routeElement = parentElement;
		if (from.getType() != INTERCEPT && from.getType() != EXCEPTION) {
			routeElement = document.createElement(ROUTE_ELE);
			parentElement.appendChild(routeElement);
		}
		save(from, document, rootElement, routeElement);

	}

	private void save(SpringRouteNode node, Document document,
			Element rootElement, Element parentElement) {
		Element save = savers[node.getType()].save(node, parentElement);
		SpringRouteNode firstChild = node.getFirstChild();
		if (firstChild != null) {
			save(firstChild, document, rootElement, save);
		}
		SpringRouteNode next = node.getSibling();
		if (next != null) {
			save(next, document, rootElement, parentElement);
		}
		return;
	}

	private void outputDocument(Document document)
			throws FileNotFoundException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		tf.setAttribute("indent-number", new Integer(4));
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(new DOMSource(document), new StreamResult(
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
						outputPath)))));
	}

	private void afterSaved() {
		for (AbstractComponentSaver s : savers) {
			if (s != null) {
				s.afterSaved();
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException,
			ParserConfigurationException, TransformerException {
		CamelSpringSaver saver = new CamelSpringSaver("output/output.xml");
		saver.save(null, true, true);
	}
}
