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
package org.talend.repository.services.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.talend.commons.ui.swt.formtools.LabelledFileField;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicesFactory;

/**
 * hwang class global comment. Detailled comment
 */
public class OpenWSDLPage extends WizardPage {

    private RepositoryNode repositoryNode;

    private LabelledFileField wsdlText;

    private String path;

    private Definition definition;

    protected OpenWSDLPage(RepositoryNode repositoryNode, String pageName) {
        super(pageName);
        this.repositoryNode = repositoryNode;
    }

    public void createControl(Composite parent) {
        Composite parentArea = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        // layout.marginLeft = 100;
        // layout.marginWidth = 10;
        // layout.marginHeight = 10;
        layout.numColumns = 5;
        parentArea.setLayout(layout);

        String[] xmlExtensions = { "*.xml;*.xsd;*.wsdl", "*.*", "*" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        wsdlText = new LabelledFileField(parentArea, "WSDL", //$NON-NLS-1$
                xmlExtensions);
        ServiceItem item = (ServiceItem) repositoryNode.getObject().getProperty().getItem();
        if (item.getServiceConnection() != null) {
            wsdlText.setText(item.getServiceConnection().getWSDLPath());
        }
        path = wsdlText.getText();

        // boolean canFinish = checkFieldsValue();
        // this.setPageComplete(canFinish);
        addListener();
        setControl(parentArea);

    }

    private void addListener() {
        wsdlText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                path = wsdlText.getText();
                if (path.trim().length() > 0) {
                    setPageComplete(true);
                } else {
                    setPageComplete(false);
                }
            }

        });
    }

    public void finish() {
        ServiceItem item = (ServiceItem) repositoryNode.getObject().getProperty().getItem();
        if (path != null && !path.trim().equals("")) {
            item.setServiceConnection(ServicesFactory.eINSTANCE.createServiceConnection());
            item.getServiceConnection().setWSDLPath(path);
            File file = new File(path);
            StringBuffer buffer = new StringBuffer();
            try {
                BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                String temp;
                while ((temp = read.readLine()) != null) {
                    buffer.append(temp);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            item.getServiceConnection().setWSDLContent(buffer.toString().getBytes());
            //
            WSDLFactory wsdlFactory;
            try {
                wsdlFactory = WSDLFactory.newInstance();
                WSDLReader newWSDLReader = wsdlFactory.newWSDLReader();
                newWSDLReader.setExtensionRegistry(wsdlFactory.newPopulatedExtensionRegistry());
                newWSDLReader.setFeature(com.ibm.wsdl.Constants.FEATURE_VERBOSE, false);
                definition = newWSDLReader.readWSDL(path);
                Map portTypes = definition.getAllPortTypes();
                Iterator it = portTypes.keySet().iterator();
                repositoryNode.getChildren().clear();
                item.getServiceConnection().getServiceOperation().clear();
                while (it.hasNext()) {
                    QName key = (QName) it.next();
                    PortType portType = (PortType) portTypes.get(key);
                    List<Operation> list = portType.getOperations();
                    for (Operation operation : list) {
                        ServiceOperation serviceOperation = ServicesFactory.eINSTANCE.createServiceOperation();
                        RepositoryNode operationNode = new RepositoryNode(new RepositoryViewObject(item.getProperty()),
                                repositoryNode, ENodeType.REPOSITORY_ELEMENT);
                        operationNode.setProperties(EProperties.LABEL, repositoryNode.getObject().getLabel());
                        operationNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.SERVICESOPERATION);
                        serviceOperation.setOperationName(operation.getName());
                        serviceOperation.setDocumentation(operation.getDocumentationElement().getTextContent());
                        serviceOperation.setLabel(operation.getName());
                        item.getServiceConnection().getServiceOperation().add(serviceOperation);
                    }
                }
            } catch (WSDLException e) {
                e.printStackTrace();
            }
        }
    }

}
