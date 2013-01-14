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
package org.talend.designer.esb.webservice.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.talend.commons.ui.swt.formtools.LabelledFileField;
import org.talend.core.model.components.IComponent;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.MetadataTable;
import org.talend.core.model.metadata.builder.connection.WSDLSchemaConnection;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.service.IWebService;
import org.talend.core.ui.AbstractWebService;
import org.talend.core.ui.webService.WebServiceSaveManager;
import org.talend.designer.esb.webservice.WebServiceComponent;
import org.talend.repository.model.ComponentsFactoryProvider;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class WebServiceHelper implements IWebService {

    private static final String COMPONENT_NAME = "tESBConsumer";

    private WebServiceUI webServiceUI;

    private final WebServiceSaveManager manager = WebServiceSaveManager.getInstance();

    private ConnectionItem connectionItem;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.repository.ui.wizards.metadata.connection.wsdl.IWebService#getWebServiceUI(org.eclipse.swt.widgets
     * .Composite, org.talend.core.model.components.IComponent)
     */
    public AbstractWebService getWebServiceUI(Composite uiParent, ConnectionItem connectionItem) {
        this.connectionItem = connectionItem;
        WebServiceComponent wenCom = new WebServiceComponent();
        wenCom.initialize();
        IComponent iComponent = ComponentsFactoryProvider.getInstance().get(COMPONENT_NAME);
        List<? extends IElementParameter> parameters = iComponent.createElementParameters(wenCom);
        wenCom.setElementParameters(parameters);
        if (connectionItem.getState() != null) {
            WSDLSchemaConnection connection = (WSDLSchemaConnection) connectionItem.getConnection();
            wenCom.getElementParameter("ENDPOINT").setValue(connection.getEndpointURI());
            String currentURL = connection.getWSDL();
            String method = connection.getMethodName();
            String currePortName = connection.getPortName();
            String nameSpace = connection.getServerNameSpace();
            String serverName = connection.getServerName();
            String portNameSpace = connection.getPortNameSpace();
            if (!"".equals(currentURL) && currentURL != null) {
                IElementParameter ENDPOINTPara = wenCom.getElementParameter("ENDPOINT");
                ENDPOINTPara.setValue(currentURL);
            }

            if (currePortName != null) {
                IElementParameter Port_Name = wenCom.getElementParameter("PORT_NAME");
                Port_Name.setValue(currePortName);
            }
            //
            if (method != null) {
                IElementParameter METHODPara = wenCom.getElementParameter("METHOD");
                METHODPara.setValue(method);
            }
            if (nameSpace != null) {
                IElementParameter Service_NS = wenCom.getElementParameter("SERVICE_NS");
                Service_NS.setValue(nameSpace);
            }
            if (serverName != null) {
                IElementParameter Service_Name = wenCom.getElementParameter("SERVICE_NAME");
                Service_Name.setValue(serverName);
            }
            if (portNameSpace != null) {
                IElementParameter Port_NS = wenCom.getElementParameter("PORT_NS");
                Port_NS.setValue(portNameSpace);
            }
        }
        MetadataTable inputMetadata = new MetadataTable();
        inputMetadata.setAttachedConnector("FLOW");
        inputMetadata.setLabel("Input");
//        List<org.talend.core.model.metadata.IMetadataColumn> newColumnList = new ArrayList<org.talend.core.model.metadata.IMetadataColumn>();
//        List<org.talend.core.model.metadata.IMetadataColumn> newInputColumnList = new ArrayList<org.talend.core.model.metadata.IMetadataColumn>();

//        if (connectionItem.getState() != null) {
//            outputMetadaTable = new MetadataTable();
//            outputMetadaTable.setAttachedConnector("OUTPUT");
//            outputMetadaTable.setLabel("Output");
//            WSDLSchemaConnection connection = (WSDLSchemaConnection) connectionItem.getConnection();
//            Set<org.talend.core.model.metadata.builder.connection.MetadataTable> tables = ConnectionHelper.getTables(connection);
//            //
//            // EList<MetadataColumn> schemaMetadataColumn =
//            // ((org.talend.core.model.metadata.builder.connection.MetadataTable) ConnectionHelper
//            // .getTables(connection).toArray()[0]).getColumns();
//            Iterator it = tables.iterator();
//            while (it.hasNext()) {
//                org.talend.core.model.metadata.builder.connection.MetadataTable metadatatable = (org.talend.core.model.metadata.builder.connection.MetadataTable) it
//                        .next();
//                if (metadatatable.getLabel().equals("Output")) {
//                    for (int i = 0; i < metadatatable.getColumns().size(); i++) {
//                        org.talend.core.model.metadata.builder.connection.MetadataColumn col = (MetadataColumn) metadatatable
//                                .getColumns().get(i);
//                        org.talend.core.model.metadata.MetadataColumn newColumn = new org.talend.core.model.metadata.MetadataColumn();
//                        newColumn.setLabel(col.getLabel());
//                        newColumn.setTalendType(col.getTalendType());
//                        newColumn.setOriginalDbColumnName(col.getLabel());
//                        newColumnList.add(newColumn);
//                    }
//                    outputMetadaTable.setListColumns(newColumnList);
//                } else {
//                    // EList inschemaMetadataColumn = ((org.talend.core.model.metadata.builder.connection.MetadataTable)
//                    // ConnectionHelper
//                    // .getTables(connection).toArray()[1]).getColumns();
//                    for (int i = 0; i < metadatatable.getColumns().size(); i++) {
//                        org.talend.core.model.metadata.builder.connection.MetadataColumn col = (MetadataColumn) metadatatable
//                                .getColumns().get(i);
//                        org.talend.core.model.metadata.MetadataColumn newColumn = new org.talend.core.model.metadata.MetadataColumn();
//                        newColumn.setLabel(col.getLabel());
//                        newColumn.setTalendType(col.getTalendType());
//                        newColumn.setOriginalDbColumnName(col.getLabel());
//                        newInputColumnList.add(newColumn);
//                    }
//                    inputMetadata.setListColumns(newInputColumnList);
//                }
//            }
//        } else {
//            outputMetadaTable = new MetadataTable();
//            outputMetadaTable.setAttachedConnector("OUTPUT");
//        }

        List<IMetadataTable> metadataTableList = new ArrayList<IMetadataTable>(4);
        metadataTableList.add(inputMetadata);

        String[] connectors = new String[]{"Response", "Fault"};
        for (String connectionName : connectors) {
            MetadataTable outputMetadaTable = new MetadataTable();
            outputMetadaTable.setAttachedConnector(connectionName.toUpperCase());
            outputMetadaTable.setLabel(connectionName);
        metadataTableList.add(outputMetadaTable);
        }

        wenCom.setMetadataList(metadataTableList);
        webServiceUI = new WebServiceUI(wenCom, connectionItem);
        webServiceUI.createControl(uiParent);
        manager.addWebServiceSaveListener(this);
        return webServiceUI;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.ui.IWebService#getCurrentFunction()
     */
    public Boolean getCurrentFunction() {
        boolean flag = true;
        if (webServiceUI.getCurrentFunction() == null) {
            return false;
        }
        return flag;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.ui.IWebService#getTabFolder()
     */
    public CTabFolder getTabFolder() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.ui.IWebService#getTable()
     */
    public Table getTable() {
        return webServiceUI.getTable();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.repository.ui.wizards.metadata.connection.wsdl.webService.tree.WebServiceSaveListener#saveValue()
     */
    public void saveValue() {
        webServiceUI.saveInputValue();
        connectionItem.getConnection().getResourceConnection();
    }

    public LabelledFileField getWSDLLabel(Boolean b) {
        return webServiceUI.getWSDLLabel(b);
    }

}
