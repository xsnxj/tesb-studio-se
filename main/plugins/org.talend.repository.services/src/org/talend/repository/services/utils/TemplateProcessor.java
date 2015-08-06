// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
            Writer outputWriter) throws SystemException, IOException {

        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("resource.loader", "classpath"); //$NON-NLS-1$ //$NON-NLS-2$
        engine.setProperty("classpath.resource.loader.class", //$NON-NLS-1$
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"); //$NON-NLS-1$
        engine.setProperty("eventhandler.referenceinsertion.class", //$NON-NLS-1$
                "org.apache.velocity.app.event.implement.EscapeXmlReference"); //$NON-NLS-1$
        engine.init();

        VelocityContext context = new VelocityContext(contextParams);

        try {
            Template template = engine.getTemplate(tempalatePath);
            template.merge(context, outputWriter);
            outputWriter.flush();
//      } catch (ResourceNotFoundException rnfe) {
//          // couldn't find the template
//          throw new org.talend.commons.exception.ResourceNotFoundException(rnfe);
//      } catch (ParseErrorException pee) {
//          // syntax error: problem parsing the template
//          throw new org.talend.commons.exception.SystemException(pee);
//      } catch (MethodInvocationException mie) {
//          // something invoked in the template threw an exception
//          throw new org.talend.commons.exception.SystemException(mie);
        } catch (VelocityException ve) {
            // couldn't find the template
            // org.apache.velocity.exception.ResourceNotFoundException;
            // syntax error: problem parsing the template
            // org.apache.velocity.exception.ParseErrorException;
            // something invoked in the template threw an exception
            // org.apache.velocity.exception.MethodInvocationException;
            throw new SystemException(ve);
        }
    }
}
