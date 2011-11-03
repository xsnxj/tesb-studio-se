package org.talend.repository.services.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;

import org.talend.commons.exception.SystemException;

public class TemplateProcessor {

	public static void processTemplate(String tempalatePath, Map<String, Object> contextParams,
			Writer outputWriter) throws SystemException {

		VelocityEngine engine = new VelocityEngine();
		engine.setProperty("resource.loader", "classpath"); //$NON-NLS-1$ //$NON-NLS-2$
		engine.setProperty("classpath.resource.loader.class", //$NON-NLS-1$
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"); //$NON-NLS-1$
		engine.init();

		VelocityContext context = new VelocityContext(contextParams);

		try {
			Template template = engine.getTemplate(tempalatePath);
			template.merge(context, outputWriter);
			outputWriter.flush();
//		} catch (ResourceNotFoundException rnfe) {
//			// couldn't find the template
//			throw new org.talend.commons.exception.ResourceNotFoundException(rnfe);
//		} catch (ParseErrorException pee) {
//			// syntax error: problem parsing the template
//			throw new org.talend.commons.exception.SystemException(pee);
//		} catch (MethodInvocationException mie) {
//			// something invoked in the template threw an exception
//			throw new org.talend.commons.exception.SystemException(mie);
		} catch (VelocityException ve) {
			// couldn't find the template
			// org.apache.velocity.exception.ResourceNotFoundException;
			// syntax error: problem parsing the template
			// org.apache.velocity.exception.ParseErrorException;
			// something invoked in the template threw an exception
			// org.apache.velocity.exception.MethodInvocationException;
			throw new SystemException(ve);
		} catch (IOException e) {
			throw new SystemException(e);
		}
	}

//	public static void main(String[] args) throws Throwable {
//
////		String template = "E:\\talend\\work\\TOS-TIS\\org.talend.repository.services\\resources\\wsdl-template.wsdl";
//		String template = "resources/wsdl-template.wsdl";
//
//        Map<String, Object> wsdlInfo = new HashMap<String, Object>();
//        wsdlInfo.put("serviceName", "test"); //$NON-NLS-1$
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		TemplateProcessor.processTemplate(template, wsdlInfo, new OutputStreamWriter(baos));
//
//		System.out.println(baos.toString());
//	}

}
