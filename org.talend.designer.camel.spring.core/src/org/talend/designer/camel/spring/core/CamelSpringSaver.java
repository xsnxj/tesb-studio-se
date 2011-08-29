package org.talend.designer.camel.spring.core;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	
	private Document document;
	private Element beansElement;
	private Element contextElement;

	private AbstractComponentSaver[] savers = new AbstractComponentSaver[LENGTH];

	public CamelSpringSaver(String outputPath) {
		this.outputPath = outputPath;
	}

	public boolean save(SpringRoute[] routes) throws ParserConfigurationException,
			TransformerException, IOException {
		try {
			initialDocument();
			initialSavers();
			beforeSave();
			handleRoutes(routes);
			saveDocument();
		} catch (ParserConfigurationException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw e;
		} catch (TransformerException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			afterSaved();
		}
		return true;
	}

	/**
	 * create Doucment, "beans" element and "camelContext" element.
	 * config the basic namespace and schema locations of "beans" element
	 * @throws ParserConfigurationException
	 */
	private void initialDocument() throws ParserConfigurationException {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory
		.newInstance();
		factory.setNamespaceAware(false);
		document = factory.newDocumentBuilder().newDocument();

		// create beans
		beansElement = document.createElement(BEANS_ELE);
		document.appendChild(beansElement);

		// add xmlns for beans
		beansElement.setAttribute(XMLNS, BEANS_NS);

		// add xmlns:xsi for beans
		beansElement.setAttribute(XMLNS_XSI, XSI_NS);

		// add xsi:schemaLocation for beans
		StringBuilder sb = new StringBuilder();
		sb.append(BEANS_NS);
		sb.append(" ");
		sb.append(BEANS_XSD);
		sb.append(" ");
		sb.append(CAMEL_NS);
		sb.append(" ");
		sb.append(CAMEL_XSD);
		beansElement.setAttribute(NS_LOCATION, sb.toString());

		// create camelContext
		contextElement = document.createElement(CAMEL_CONTEXT_ELE);
		contextElement.setAttribute("id", "camel-1");
		contextElement.setAttribute(XMLNS, CAMEL_NS);
		
		beansElement.appendChild(contextElement);
	}
	
	private void initialSavers() {
		savers[FILE] = new FileComponentSaver(document, beansElement,
				contextElement);
		savers[CXF] = new CxfComponentSaver(document, beansElement,
				contextElement);
		savers[JMS] = new JmsComponentSaver(document, beansElement,
				contextElement);
		savers[FTP] = new FtpComponentSaver(document, beansElement,
				contextElement);
		savers[ACTIVEMQ] = new ActivemqComponentSaver(document, beansElement,
				contextElement);
		savers[MSGENDPOINT] = new MsgEndpointComponentSaver(document,
				beansElement, contextElement);
		savers[SPLIT] = new SplitterComponentSaver(document, beansElement,
				contextElement);
		savers[SETHEADER] = new SetHeaderComponentSaver(document, beansElement,
				contextElement);
		savers[SETBODY] = new SetBodyComponentSaver(document, beansElement,
				contextElement);
		savers[CONVERT] = new ConvertBodyComponentSaver(document, beansElement,
				contextElement);
		savers[ENRICH] = new ContentEnrichComponentSaver(document, beansElement,
				contextElement);
		savers[INTERCEPT] = new InterceptComponentSaver(document, beansElement,
				contextElement);
		savers[EXCEPTION] = new OnExceptionComponentSaver(document,
				beansElement, contextElement);
		savers[TRY] = new TryComponentSaver(document, beansElement,
				contextElement);
		savers[BEAN] = new BeanComponentSaver(document, beansElement,
				contextElement);
		savers[PF] = new PipeLineComponentSaver(document, beansElement,
				contextElement);
		savers[LOOP] = new LoopComponentSaver(document, beansElement,
				contextElement);
		savers[STOP] = new StopComponentSaver(document, beansElement,
				contextElement);
		savers[DELAY] = new DelayerComponentSaver(document, beansElement,
				contextElement);
		savers[PROCESSOR] = new ProcessorComponentSaver(document, beansElement,
				contextElement);
		savers[THROTTLER] = new ThrottlerComponentSaver(document, beansElement,
				contextElement);
		savers[AGGREGATE] = new AggregateComponentSaver(document, beansElement,
				contextElement);
		savers[DYNAMIC] = new DynamicComponentSaver(document, beansElement,
				contextElement);
		savers[IDEM] = new IdempotentComponentSaver(document, beansElement,
				contextElement);
		savers[BALANCE] = new LoadBalanceComponentSaver(document, beansElement,
				contextElement);
		savers[FILTER] = new MsgFilterComponentSaver(document, beansElement,
				contextElement);
		savers[MSGROUTER] = new MsgRouterComponentSaver(document, beansElement,
				contextElement);
		savers[MULTICAST] = new MulticastComponentSaver(document, beansElement,
				contextElement);
		savers[ROUTINGSLIP] = new RoutingSlipComponentSaver(document,
				beansElement, contextElement);
		savers[WIRETAP] = new WireTapComponentSaver(document, beansElement,
				contextElement);
		savers[PATTERN] = new ExchangePatternComponentSaver(document,
				beansElement, contextElement);
		savers[WHEN] = new WhenComponentSaver(document, beansElement,
				contextElement);
		savers[CATCH] = new CatchComponentSaver(document, beansElement,
				contextElement);
		savers[FINALLY] = new FinallyComponentSaver(document, beansElement,
				contextElement);
		savers[OTHER] = new OtherwiseComponentSaver(document, beansElement,
				contextElement);

	}

	private void beforeSave() {
		for (AbstractComponentSaver s : savers) {
			if (s != null) {
				s.beforeSave();
			}
		}
	}
	
	private void handleRoutes(SpringRoute[] routes) {
		if (routes != null) {
			for (SpringRoute sr : routes) {
				handleRoute(sr);
			}
		}
	}
	
	private void handleRoute(SpringRoute sr) {
		SpringRouteNode from = sr.getFrom();
		Element routeElement = contextElement;
		if (from.getType() != INTERCEPT && from.getType() != EXCEPTION) {
			routeElement = document.createElement(ROUTE_ELE);
			contextElement.appendChild(routeElement);
		}
		handleNode(from, document, beansElement, routeElement);

	}

	private void handleNode(SpringRouteNode node, Document document,
			Element beansElement, Element parentElement) {
		Element save = savers[node.getType()].save(node, parentElement);
		SpringRouteNode firstChild = node.getFirstChild();
		if (firstChild != null) {
			handleNode(firstChild, document, beansElement, save);
		}
		SpringRouteNode next = node.getSibling();
		if (next != null) {
			handleNode(next, document, beansElement, parentElement);
		}
		return;
	}

	private void saveDocument()
			throws TransformerException, IOException {
		TransformerFactory tf = TransformerFactory.newInstance();
		tf.setAttribute("indent-number", new Integer(4));
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		FileOutputStream fileOutput = new FileOutputStream(outputPath);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOutput));
		transformer.transform(new DOMSource(document), new StreamResult(
				writer));
		writer.close();
		fileOutput.close();
	}

	private void afterSaved() {
		for (AbstractComponentSaver s : savers) {
			if (s != null) {
				s.afterSaved();
			}
		}
	}
}
