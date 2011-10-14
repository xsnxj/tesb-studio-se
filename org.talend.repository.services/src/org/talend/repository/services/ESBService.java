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
package org.talend.repository.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.IESBService;
import org.talend.core.model.metadata.builder.connection.AbstractMetadataObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.ui.editor.ProcessEditorInput;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.action.CreateNewJobAction;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.utils.OperationRepositoryObject;
import org.talend.repository.services.utils.WSDLUtils;

/**
 * DOC nrousseau class global comment. Detailled comment
 */
public class ESBService implements IESBService {

    public AbstractMetadataObject getServicesOperation(Connection connection, String operationName) {
        List<ServiceOperation> list = new ArrayList<ServiceOperation>();
        if (connection instanceof ServiceConnection) {
            ServiceConnection serConnection = (ServiceConnection) connection;
            EList<ServicePort> serPort = serConnection.getServicePort();
            for (ServicePort port : serPort) {
                list.addAll(port.getServiceOperation());
            }
        }
        for (ServiceOperation ope : list) {
            if (ope.getLabel().equals(operationName)) {
                return ope;
            }
        }
        return null;
    }

    public void changeOperationLabel(RepositoryNode newNode, INode node, Connection connection) {
        if (!(connection instanceof ServiceConnection)) {
            return;
        }
        ServiceConnection serConn = (ServiceConnection) connection;
        changeOldOperationLabel(serConn, node);
        changenewOperationLabel(newNode, node, serConn);
    }

    private void changeOldOperationLabel(ServiceConnection serConn, INode node) {
        EList<ServicePort> portList = serConn.getServicePort();
        IElementParameter portPara = node.getElementParameter(WSDLUtils.PORT_NAME);
        IElementParameter opePara = node.getElementParameter(WSDLUtils.OPERATION_NAME);
        if (portPara != null && opePara != null) {
            String portValue = (String) portPara.getValue();
            String opeValue = (String) opePara.getValue();
            if (portValue != null && !"".equals(portValue) && opeValue != null && !"".equals(opeValue)) {
                out: for (ServicePort port : portList) {
                    if (port.getName().equals(portValue)) {
                        for (ServiceOperation ope : port.getServiceOperation()) {
                            if (ope.getName().equals(opeValue)) {
                                if (ope.getLabel().contains("-")) {
                                    ope.setLabel(opeValue);
                                    ope.setReferenceJobId(null);
                                    break out;
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private void changenewOperationLabel(RepositoryNode newNode, INode node, ServiceConnection serConn) {
        String operationName = newNode.getObject().getLabel();
        String parentPortName = newNode.getParent().getObject().getLabel();

        String wsdlPath = serConn.getWSDLPath();
        try {
            Map<String, String> serviceParameters = WSDLUtils.getServiceParameters(wsdlPath);
            IRepositoryViewObject newObj = newNode.getObject();
            if (newObj instanceof OperationRepositoryObject) {
                ServiceOperation newOpe = (ServiceOperation) ((OperationRepositoryObject) newObj).getAbstractMetadataObject();

                IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

                if (newOpe.getReferenceJobId() != null) {
                    changeOtherJobSchemaValue(factory, newOpe, serConn);
                    MessageDialog.openWarning(new Shell(), "warning",
                            "This other job which based on the Operation will be unset!");
                }

                IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                IEditorInput input = activeEditor.getEditorInput();
                if (input instanceof ProcessEditorInput) {
                    Item jobItem = ((ProcessEditorInput) input).getItem();
                    String jobID = jobItem.getProperty().getId();
                    String jobName = jobItem.getProperty().getLabel();

                    newOpe.setReferenceJobId(jobID);
                    newOpe.setLabel(newOpe.getName() + "-" + jobName);

                    serviceParameters.put(WSDLUtils.PORT_NAME, parentPortName);
                    serviceParameters.put(WSDLUtils.OPERATION_NAME, operationName);

                    CreateNewJobAction.setProviderRequestComponentConfiguration(node, serviceParameters);

                    try {
                        factory.save(jobItem);
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }
                    try {
                        factory.save(newNode.getParent().getParent().getObject().getProperty().getItem());
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }
                    RepositoryManager.refreshSavedNode(newNode);
                }
            }

        } catch (CoreException e1) {
            ExceptionHandler.process(e1);
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
    }

    private void changeOtherJobSchemaValue(IProxyRepositoryFactory factory, ServiceOperation newOpe, ServiceConnection serConn)
            throws PersistenceException, CoreException {
        IRepositoryViewObject jobObj = factory.getLastVersion(newOpe.getReferenceJobId());
        ProcessItem item = (ProcessItem) jobObj.getProperty().getItem();
        ProcessType process = item.getProcess();
        EList<NodeType> nodeList = process.getNode();
        String wsdlPath = serConn.getWSDLPath();
        Map<String, String> serviceParameters = WSDLUtils.getServiceParameters(wsdlPath);
        for (NodeType node : nodeList) {
            EList parameters = node.getElementParameter();
            for (Object paramObj : parameters) {
                ElementParameterType param = (ElementParameterType) paramObj;
                String name = param.getName();
                if (name.equals(WSDLUtils.OPERATION_NAME)) {
                    if (!newOpe.getName().equals(param.getValue())) {
                        break;
                    }
                    param.setValue(null);
                }
                if (name.equals("SCHEMA:SCHEMA_TYPE")) {
                    param.setValue("BUILT_IN");
                    break;
                }

            }

        }
        factory.save(item);
    }

}
