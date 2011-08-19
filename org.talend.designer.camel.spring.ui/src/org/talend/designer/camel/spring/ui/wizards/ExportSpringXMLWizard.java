// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.spring.ui.wizards;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.designer.camel.spring.core.CamelSpringSaver;
import org.talend.designer.camel.spring.core.models.SpringRoute;
import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.talend.designer.camel.spring.ui.exports.SpringXMLExporter;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;

/**
 * Wizard for the creation of a new project. <br/>
 * 
 * $Id: ImportSpringXMLWizard.java 52559 2010-12-13 04:14:06Z $
 * 
 */
public class ExportSpringXMLWizard extends Wizard {

    private ExportSpringXMLWizardPage exportPage;

    private ProcessType process;

    private SpringXMLExporter exporter;
    
    public ExportSpringXMLWizard(ProcessType process) {
        this.process = process;
        this.exporter = SpringXMLExporter.getInstance();
    }

    @Override
    public void addPages() {
        exportPage = new ExportSpringXMLWizardPage("Export As Spring XML");
        addPage(exportPage);
        setWindowTitle("Export Spring");
    }

    @Override
    public boolean performFinish() {
        
        SpringRoute[] routes = exporter.buildSpringRoute1(process);
        String outputPath = exportPage.getOutputPath();
        if (new File(outputPath).exists()) {
            if (!MessageDialog.openQuestion(getShell(), "Question", "File '" + outputPath
                    + "' exists, would you like to overwrite it? ")) {
                return false;
            }
        }
        
        testPrint(routes);
        CamelSpringSaver saver = new CamelSpringSaver(exportPage.getOutputPath());
        try {
            saver.save(routes, exporter.isHasActiveMQ(), exporter.isHasCXF());
        } catch (Exception e) {
            ExceptionHandler.process(e);
            MessageDialog
                    .openError(getShell(), "Error", "Export to Spring XML failed, details:" + e.getMessage());
            return false;
        } 
        
        return true;
    }

    private void testPrint(SpringRoute[] springRoutes) {
        System.out.println("==========start============");
        String tab = "";
        for (SpringRoute route : springRoutes) {
            tab = "";
            System.out.println("*********[Route " + route.getRouteId() + " ]**********");
            SpringRouteNode routeNode = route.getFrom();
            print(routeNode, tab);
        }
        System.out.println("==========stop============\n");
    }

    private void print(SpringRouteNode routeNode, String tab) {
        System.out.println(tab + "[Node " + routeNode.getUniqueName() + " ] " + routeNode.getParameter());
        SpringRouteNode nextChild = routeNode.getFirstChild();
        SpringRouteNode nextSibling = routeNode.getSibling();
        if (nextSibling != null) {
            System.out.println(tab + "[Next sibling " + nextSibling.getUniqueName() + " ]");
            print(nextSibling, tab);
        }
        if (nextChild != null) {
            tab += " ";
            System.out.println(tab + "[Parent " + routeNode.getUniqueName() + " ]");
            System.out.println(tab + "[Next child " + nextChild.getUniqueName() + " ]");
            print(nextChild, tab);
        }
    }
  
}
