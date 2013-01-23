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
package org.talend.repository.services.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xsd.XSDSchema;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.utils.VersionUtils;
import org.talend.commons.utils.data.list.UniqueStringGenerator;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.metadata.MappingTypeRetriever;
import org.talend.core.model.metadata.MetadataTalendType;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.connection.XMLFileNode;
import org.talend.core.model.metadata.builder.connection.XmlFileConnection;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.XmlFileConnectionItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.datatools.xml.utils.ATreeNode;
import org.talend.datatools.xml.utils.OdaException;
import org.talend.datatools.xml.utils.XSDPopulationUtil2;
import org.talend.designer.core.DesignerPlugin;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.Activator;
import org.talend.repository.services.Messages;
import org.talend.repository.services.ui.RewriteSchemaDialog;
import org.talend.repository.services.ui.preferences.EsbSoapServicePreferencePage;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.services.utils.FolderNameUtil;
import org.talend.repository.services.utils.SchemaUtil;
import org.talend.repository.services.utils.WSDLUtils;
import org.talend.repository.ui.actions.AContextualAction;
import orgomg.cwm.resource.record.RecordFactory;
import orgomg.cwm.resource.record.RecordFile;

/**
 * Action used to export job scripts. <br/>
 * 
 * $Id: ExportJobScriptAction.java 1 2006-12-13 ä¸‹å�ˆ03:12:05 bqian
 * 
 */
public class PublishMetadataAction extends AContextualAction {

    private Shell shell;

    private String wsdlLocation;

    private Definition wsdlDefinition;

    private XSDPopulationUtil2 populationUtil;

    public PublishMetadataAction() {
        super();
        this.setText(Messages.PublishMetadataAction_Name);
        this.setToolTipText(Messages.PublishMetadataAction_Name);
        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.HIERARCHY_ICON));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse.jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(TreeViewer viewer, IStructuredSelection selection) {
        wsdlDefinition = null;
        setEnabled(false);
        if (selection.size() != 1) {
            return;
        }
        RepositoryNode node = (RepositoryNode) selection.iterator().next();
        if (node.getType() == ENodeType.REPOSITORY_ELEMENT
                && node.getProperties(EProperties.CONTENT_TYPE) == ESBRepositoryNodeType.SERVICES
                && node.getObject() != null
                && ProxyRepositoryFactory.getInstance().getStatus(node.getObject()) != ERepositoryStatus.DELETED) {
            wsdlLocation = WSDLUtils.getWsdlFile(node).getLocationURI().toString();
            shell = viewer.getTree().getShell();
            setEnabled(true);
        }
    }

    /*
     * used from org.talend.designer.esb.webservice.ui.WebServiceUI
     */
    public void run(Definition wsdlDefinition) {
        this.wsdlDefinition = wsdlDefinition;
        doRun();
    }

    /*
     * used from org.talend.designer.esb.bpm.ui.wizard.ImportFromBPMProcessWizard
     */
    public void run(RepositoryNode node, IProgressMonitor monitor) throws CoreException {
        wsdlDefinition = WSDLUtils.getWsdlDefinition(node);
        Map<String, IRepositoryViewObject> selectTables = Collections.emptyMap();
        importSchema(monitor, selectTables);
    }

    @Override
    protected void doRun() {
        if (null == wsdlDefinition) {
            try {
                wsdlDefinition = WSDLUtils.getDefinition(wsdlLocation);
            } catch (CoreException e) {
                return;
            }
        }

        Collection<IRepositoryViewObject> xmlObjs = initFileConnection();
        final Map<String, IRepositoryViewObject> selectTables = new HashMap<String, IRepositoryViewObject>();
        if (xmlObjs.size() > 0) {
            if (null == shell) {
                shell = Display.getDefault().getActiveShell();
            }
            RewriteSchemaDialog selectContextDialog = new RewriteSchemaDialog(shell, xmlObjs);
            if (selectContextDialog.open() == Window.OK) {
                selectTables.putAll(selectContextDialog.getSelectionTables());
            } else {
                return;
            }
        }

        final IWorkspaceRunnable op = new IWorkspaceRunnable() {

            public void run(IProgressMonitor monitor) throws CoreException {
                importSchema(monitor, selectTables);
            }

        };
        IRunnableWithProgress iRunnableWithProgress = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                IWorkspace workspace = ResourcesPlugin.getWorkspace();
                try {
                    ISchedulingRule schedulingRule = workspace.getRoot();
                    // the update the project files need to be done in the workspace runnable to avoid all
                    // notification
                    // of changes before the end of the modifications.
                    workspace.run(op, schedulingRule, IWorkspace.AVOID_UPDATE, monitor);
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                }

            }
        };

        try {
            new ProgressMonitorDialog(null).run(true, true, iRunnableWithProgress);
        } catch (InvocationTargetException e) {
            ExceptionHandler.process(e);
        } catch (InterruptedException e) {
            //
        }
    }

    private Collection<IRepositoryViewObject> initFileConnection() {
        Collection<String> paths = getAllPaths();
        List<IRepositoryViewObject> connItems = new ArrayList<IRepositoryViewObject>();

        IProxyRepositoryFactory factory = DesignerPlugin.getDefault().getProxyRepositoryFactory();
        try {
            for (IRepositoryViewObject object : factory.getAll(ERepositoryObjectType.METADATA, true)) {
                Item item = object.getProperty().getItem();
                if (item instanceof ConnectionItem) {
                    Connection conn = ((ConnectionItem) item).getConnection();
                    if (conn instanceof XmlFileConnection) {
                        String sPath = item.getState().getPath();
                        if (paths.contains(sPath)) {
                            Object[] array = ConnectionHelper.getTables(conn).toArray();
                            if (array.length > 0) {
                                connItems.add(object);
                            }
                        }
                    }
                }
            }
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
        return connItems;
    }

    private Collection<String> getAllPaths() {
        final Set<String> paths = new HashSet<String>();
        Collection<Binding> bindings = wsdlDefinition.getBindings().values();
        Set<PortType> portTypes = new HashSet<PortType>(bindings.size());
        final Set<QName> alreadyCreated = new HashSet<QName>();
        for (Binding binding : bindings) {
            PortType portType = binding.getPortType();
            if (portTypes.add(portType)) {
                List<BindingOperation> operations = binding.getBindingOperations();
                for (BindingOperation operation : operations) {
                    Operation oper = operation.getOperation();
                    Input inDef = oper.getInput();
                    if (inDef != null) {
                        Message inMsg = inDef.getMessage();
                        if (inMsg != null) {
                            // fix for TDI-20699
                            QName parameterFromMessage = getParameterFromMessage(inMsg);
                            if (alreadyCreated.add(parameterFromMessage)) {
                                try {
                                    String folderPath = FolderNameUtil.getImportedXmlSchemaPath(parameterFromMessage.getNamespaceURI(),
                                            portType.getQName().getLocalPart(), oper.getName());
                                    paths.add(new Path(folderPath).toString());
                                } catch (URISyntaxException e) {
                                    ExceptionHandler.process(e);
                                }
                            }
                        }
                    }

                    Output outDef = oper.getOutput();
                    if (outDef != null) {
                        Message outMsg = outDef.getMessage();
                        if (outMsg != null) {
                            QName parameterFromMessage = getParameterFromMessage(outMsg);
                            if (alreadyCreated.add(parameterFromMessage)) {
                                try {
                                    String folderPath = FolderNameUtil.getImportedXmlSchemaPath(parameterFromMessage.getNamespaceURI(),
                                            portType.getQName().getLocalPart(), oper.getName());
                                    paths.add(new Path(folderPath).toString());
                                } catch (URISyntaxException e) {
                                    ExceptionHandler.process(e);
                                }
                            }
                        }
                    }
                    Collection<Fault> faults = oper.getFaults().values();
                    for (Fault fault : faults) {
                        Message faultMsg = fault.getMessage();
                        if (faultMsg != null) {
                            QName parameterFromMessage = getParameterFromMessage(faultMsg);
                            if (alreadyCreated.add(parameterFromMessage)) {
                                try {
                                    String folderPath = FolderNameUtil.getImportedXmlSchemaPath(parameterFromMessage.getNamespaceURI(),
                                            portType.getQName().getLocalPart(), oper.getName());
                                    paths.add(new Path(folderPath).toString());
                                } catch (URISyntaxException e) {
                                    ExceptionHandler.process(e);
                                }
                            }
                        }
                    }
                }
            }
        }
        return paths;
    }

    /**
     * https://jira.talendforge.org/browse/TESB-6845
     * 
     * Import schema
     * 
     * @param monitor
     * @throws CoreException
     */
    private void importSchema(IProgressMonitor monitor, Map<String, IRepositoryViewObject> selectTables) throws CoreException {
        monitor.beginTask(Messages.PublishMetadataAction_Importing, 2);
        boolean validateWsdl = Activator.getDefault().getPreferenceStore().getBoolean(EsbSoapServicePreferencePage.ENABLE_WSDL_VALIDATION);
        if(validateWsdl){
            WSDLUtils.validateWsdl(wsdlDefinition.getDocumentBaseURI());
        }
        monitor.worked(1);
        if (monitor.isCanceled()) {
            return;
        }
        try {
            process(selectTables);
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                    "Error during scema processing", e));
        }
        monitor.done();
    }

    private void process(Map<String, IRepositoryViewObject> selectTables) throws IOException {
        Map<String, File> fileToSchemaMap = new HashMap<String, File>();
        File zip = null;
        final SchemaUtil schemaUtil = new SchemaUtil(wsdlDefinition);

        try {
	        populationUtil = new XSDPopulationUtil2();
	        for (XmlSchema schema : schemaUtil.getSchemas()) {
	            File file = initFileContent(schema);
	            fileToSchemaMap.put(schema.getTargetNamespace(), file);
	            populationUtil.addSchema(file.getPath());
	        }
	
	        zip = File.createTempFile("tempXSDFile", ".zip");
	        Collection<File> files = fileToSchemaMap.values();
	        org.talend.utils.io.FilesUtils.zips(files.toArray(new File[files.size()]), zip.getPath());
	
	        final Set<QName> alreadyCreated = new HashSet<QName>();
	        Collection<Binding> bindings = wsdlDefinition.getBindings().values();
	        Set<PortType> portTypes = new HashSet<PortType>(bindings.size());
	        for (Binding binding : bindings) {
	            PortType portType = binding.getPortType();
	            if (portTypes.add(portType)) {
	                Collection<BindingOperation> operations = binding.getBindingOperations();
	                for (BindingOperation operation : operations) {
	                    Operation oper = operation.getOperation();
	                    Input inDef = oper.getInput();
	                    if (inDef != null) {
	                        Message inMsg = inDef.getMessage();
	                        if (inMsg != null) {
	                            // fix for TDI-20699
	                            QName parameterFromMessage = getParameterFromMessage(inMsg);
	                            if (alreadyCreated.add(parameterFromMessage)) {
		                            populateMessage2(parameterFromMessage, portType.getQName().getLocalPart(), oper.getName(), fileToSchemaMap, selectTables, zip);
	                            }
	                        }
	                    }
	
	                    Output outDef = oper.getOutput();
	                    if (outDef != null) {
	                        Message outMsg = outDef.getMessage();
	                        if (outMsg != null) {
	                            QName parameterFromMessage = getParameterFromMessage(outMsg);
	                            if (alreadyCreated.add(parameterFromMessage)) {
		                            populateMessage2(parameterFromMessage, portType.getQName().getLocalPart(), oper.getName(), fileToSchemaMap, selectTables, zip);
	                            }
	                        }
	                    }
	                    Collection<Fault> faults = oper.getFaults().values();
	                    for (Fault fault : faults) {
	                        Message faultMsg = fault.getMessage();
	                        if (faultMsg != null) {
	                            QName parameterFromMessage = getParameterFromMessage(faultMsg);
	                            if (alreadyCreated.add(parameterFromMessage)) {
		                            populateMessage2(parameterFromMessage, portType.getQName().getLocalPart(), oper.getName(), fileToSchemaMap, selectTables, zip);
	                            }
	                        }
	                    }
	                }
	            }
	        }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (File file : fileToSchemaMap.values()) {
                file.delete();
            }
            if (null != zip) {
                zip.delete();
            }
        }
    }


    private int orderId;

    private boolean loopElementFound;

    /**
     * To optimize, right now it will write the xsd file many times. Since there is no clues if the parameters comes
     * from the same xsd, generate it everytime right now.
     * 
     * @param operationName
     * @param hashMap
     * @throws IOException 
     */
    private void populateMessage2(QName parameter, String portTypeName, String operationName,
            Map<String, File> schemaToFileMap, Map<String, IRepositoryViewObject> selectItems, File zip) throws IOException {
        String name = /* componentName + "_"+ */parameter.getLocalPart();
        XmlFileConnection connection = null;
        Property connectionProperty = null;
        XmlFileConnectionItem connectionItem = null;
        boolean needRewrite = false;

        if (selectItems.size() > 0) {
            Set<Entry<String, IRepositoryViewObject>> tableSet = selectItems.entrySet();
            Iterator<Entry<String, IRepositoryViewObject>> iterator = tableSet.iterator();
            while (iterator.hasNext()) {
                Entry<String, IRepositoryViewObject> entry = iterator.next();
                IRepositoryViewObject repObj = entry.getValue();
                Item item = repObj.getProperty().getItem();
                if (item instanceof XmlFileConnectionItem) {
                    connectionItem = (XmlFileConnectionItem) item;
                    connection = (XmlFileConnection) connectionItem.getConnection();
                    connectionProperty = item.getProperty();
                    if (connectionProperty.getLabel().equals(name)) {
                        needRewrite = true;
                        break;
                    }
                }
            }
            if (!needRewrite && ConnectionHelper.getTables(connection).size() > 0) {
                return;
            }
        }

        // if (!exist) {
        connection = ConnectionFactory.eINSTANCE.createXmlFileConnection();
        connection.setName(ERepositoryObjectType.METADATA_FILE_XML.getKey());
        connectionItem = PropertiesFactory.eINSTANCE.createXmlFileConnectionItem();
        connectionProperty = PropertiesFactory.eINSTANCE.createProperty();
        connectionProperty.setAuthor(((RepositoryContext) CoreRuntimePlugin.getInstance().getContext()
                .getProperty(Context.REPOSITORY_CONTEXT_KEY)).getUser());
        connectionProperty.setLabel(name);
        connectionProperty.setVersion(VersionUtils.DEFAULT_VERSION);
        connectionProperty.setStatusCode(""); //$NON-NLS-1$

        connectionItem.setProperty(connectionProperty);
        connectionItem.setConnection(connection);

        connection.setInputModel(false);
        // }

        ByteArray byteArray = PropertiesFactory.eINSTANCE.createByteArray();
        byteArray.setInnerContentFromFile(zip);
        connection.setFileContent(byteArray.getInnerContent());

        XSDSchema xsdSchema;
        try {
            // don't put any XSD directly inside the xml connection but put zip file
            String filePath = schemaToFileMap.get(parameter.getNamespaceURI()).getPath(); // name of xsd file needed
            connection.setXmlFilePath(zip.getName());

            xsdSchema = populationUtil.getXSDSchema(filePath);
            List<ATreeNode> rootNodes = populationUtil.getAllRootNodes(xsdSchema);

            ATreeNode node = null;

            // try to find the root element needed from XSD file.
            // note: if there is any prefix, it will get the node with the first correct name, no matter the prefix.

            // once the we can get the correct prefix value from the wsdl, this code should be modified.
            for (ATreeNode curNode : rootNodes) {
                String curName = (String) curNode.getValue();
                if (curName.contains(":")) { //$NON-NLS-1$
                    // if with prefix, don't care about it for now, just compare the name.
                    if (curName.split(":")[1].equals(name)) { //$NON-NLS-1$
                        node = curNode;
                        break;
                    }
                } else if (curName.equals(name)) {
                    node = curNode;
                    break;
                }
            }

            node = populationUtil.getSchemaTree(xsdSchema, node);
            orderId = 1;
            loopElementFound = false;
            if (ConnectionHelper.getTables(connection).isEmpty()) {
                MetadataTable table = ConnectionFactory.eINSTANCE.createMetadataTable();
                table.setId(ProxyRepositoryFactory.getInstance().getNextId());
                RecordFile record = (RecordFile) ConnectionHelper.getPackage(connection.getName(), connection, RecordFile.class);
                if (record != null) { // hywang
                    PackageHelper.addMetadataTable(table, record);
                } else {
                    RecordFile newrecord = RecordFactory.eINSTANCE.createRecordFile();
                    newrecord.setName(connection.getName());
                    ConnectionHelper.addPackage(newrecord, connection);
                    PackageHelper.addMetadataTable(table, newrecord);
                }
            }
            boolean haveElement = false;
            for (Object curNode : node.getChildren()) {
                if (((ATreeNode) curNode).getType() == ATreeNode.ELEMENT_TYPE) {
                    haveElement = true;
                    break;
                }
            }
            fillRootInfo(connection, node, "", !haveElement); //$NON-NLS-1$
        } catch (IOException e) {
            throw e;
        } catch (URISyntaxException e1) {
            ExceptionHandler.process(e1);
        } catch (OdaException e) {
            ExceptionHandler.process(e);
        }

        // save
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        String nextId = factory.getNextId();
        connectionProperty.setId(nextId);
        try {
            // http://jira.talendforge.org/browse/TESB-3655 Remove possible
            // schema prefix
            String folderPath = FolderNameUtil.getImportedXmlSchemaPath(parameter.getNamespaceURI(), portTypeName,
                    operationName);
            IPath path = new Path(folderPath);
            factory.create(connectionItem, path, true); // consider this as migration will overwrite the old metadata if
                                                        // existing in the same path

            ProxyRepositoryFactory.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());
            RepositoryManager.refresh(ERepositoryObjectType.METADATA_FILE_XML);
        } catch (PersistenceException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void fillRootInfo(XmlFileConnection connection, ATreeNode node, String path, boolean inLoop) {
        XMLFileNode xmlNode = ConnectionFactory.eINSTANCE.createXMLFileNode();
        xmlNode.setXMLPath(path + '/' + node.getValue());
        xmlNode.setOrder(orderId);
        orderId++;
        MappingTypeRetriever retriever;
        String nameWithoutPrefixForColumn;
        String curName = (String) node.getValue();
        if (curName.contains(":")) { //$NON-NLS-1$
            nameWithoutPrefixForColumn = curName.split(":")[1]; //$NON-NLS-1$
        } else {
            nameWithoutPrefixForColumn = curName;
        }
        retriever = MetadataTalendType.getMappingTypeRetriever("xsd_id"); //$NON-NLS-1$
        xmlNode.setAttribute("attri"); //$NON-NLS-1$
        xmlNode.setType(retriever.getDefaultSelectedTalendType(node.getDataType()));
        MetadataColumn column = null;
        MetadataTable metadataTable = ConnectionHelper.getTables(connection).toArray(new MetadataTable[0])[0];
        switch (node.getType()) {
        case ATreeNode.ATTRIBUTE_TYPE:
            // fix for TDI-20390 and TDI-20671 ,XMLPath for attribute should only store attribute name but not full
            // xpath
            xmlNode.setXMLPath("" + node.getValue()); //$NON-NLS-1$
            column = ConnectionFactory.eINSTANCE.createMetadataColumn();
            column.setTalendType(xmlNode.getType());
            String uniqueName = extractColumnName(nameWithoutPrefixForColumn, metadataTable.getColumns());
            column.setLabel(uniqueName);
            xmlNode.setRelatedColumn(uniqueName);
            metadataTable.getColumns().add(column);
            break;
        case ATreeNode.ELEMENT_TYPE:
            boolean haveElementOrAttributes = false;
            for (Object curNode : node.getChildren()) {
                if (((ATreeNode) curNode).getType() != ATreeNode.NAMESPACE_TYPE) {
                    haveElementOrAttributes = true;
                    break;
                }
            }
            if (!haveElementOrAttributes) {
                xmlNode.setAttribute("branch"); //$NON-NLS-1$
                column = ConnectionFactory.eINSTANCE.createMetadataColumn();
                column.setTalendType(xmlNode.getType());
                uniqueName = extractColumnName(nameWithoutPrefixForColumn, metadataTable.getColumns());
                column.setLabel(uniqueName);
                xmlNode.setRelatedColumn(uniqueName);
                metadataTable.getColumns().add(column);
            } else {
                xmlNode.setAttribute("main"); //$NON-NLS-1$
            }
            break;
        case ATreeNode.NAMESPACE_TYPE:
            xmlNode.setAttribute("ns"); //$NON-NLS-1$
            // specific for namespace... no path set, there is only the prefix value.
            // this value is saved now in node.getDataType()
            xmlNode.setXMLPath(node.getDataType());

            xmlNode.setDefaultValue((String) node.getValue());
            break;
        case ATreeNode.OTHER_TYPE:
            break;
        }
        boolean subElementsInLoop = inLoop;
        // will try to get the first element (branch or main), and set it as loop.
        if ((!loopElementFound && path.split("/").length == 2 && node.getType() == ATreeNode.ELEMENT_TYPE) || subElementsInLoop) { //$NON-NLS-1$
            connection.getLoop().add(xmlNode);

            loopElementFound = true;
            subElementsInLoop = true;
        } else {
            connection.getRoot().add(xmlNode);
        }
        if (node.getChildren().length > 0) {
            for (Object curNode : node.getChildren()) {
                fillRootInfo(connection, (ATreeNode) curNode, path + '/' + node.getValue(), subElementsInLoop);
            }
        }
    }

    private static File initFileContent(final XmlSchema schema) throws IOException {
        FileOutputStream outStream = null;
        try {
            File temfile = File.createTempFile("tempXSDFile", ".xsd"); //$NON-NLS-1$ //$NON-NLS-2$
            outStream = new FileOutputStream(temfile);
            schema.write(outStream); // this method hangs when using invalid wsdl.
            return temfile;
        } finally {
            outStream.close();
        }
    }

    private String extractColumnName(String currentExpr, List<MetadataColumn> fullSchemaTargetList) {

        String columnName = currentExpr.startsWith("@") ? currentExpr.substring(1) : currentExpr; //$NON-NLS-1$
        columnName = columnName.replaceAll("[^a-zA-Z0-9]", "_"); //$NON-NLS-1$ //$NON-NLS-2$

        UniqueStringGenerator<MetadataColumn> uniqueStringGenerator = new UniqueStringGenerator<MetadataColumn>(columnName,
                fullSchemaTargetList) {

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.commons.utils.data.list.UniqueStringGenerator#getBeanString(java.lang.Object)
             */
            @Override
            protected String getBeanString(MetadataColumn bean) {
                return bean.getLabel();
            }

        };
        columnName = uniqueStringGenerator.getUniqueString();
        return columnName;
    }

    private static QName getParameterFromMessage(Message msg) {
        // add first parameter from message.
        @SuppressWarnings("unchecked")
        Part part = ((Collection<Part>)msg.getParts().values()).iterator().next();
        if (part.getElementName() != null) {
            return part.getElementName();
        } else if (part.getTypeName() != null) {
            return part.getTypeName();
        }
        return null;
    }
}
