// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDSchema;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.utils.VersionUtils;
import org.talend.commons.utils.data.list.UniqueStringGenerator;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.MappingTypeRetriever;
import org.talend.core.model.metadata.MetadataTalendType;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.connection.SchemaTarget;
import org.talend.core.model.metadata.builder.connection.XMLFileNode;
import org.talend.core.model.metadata.builder.connection.XmlFileConnection;
import org.talend.core.model.metadata.builder.connection.XmlXPathLoopDescriptor;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.XmlFileConnectionItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.ResourceModelUtils;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.datatools.xml.utils.ATreeNode;
import org.talend.datatools.xml.utils.OdaException;
import org.talend.datatools.xml.utils.XSDPopulationUtil2;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.Messages;
import org.talend.repository.services.model.services.ParameterInfo;
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

    private IStructuredSelection selection;

    private List<RepositoryNode> nodes;

    private XSDPopulationUtil2 populationUtil;
    
    private boolean needProgressBar = true;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse.jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public PublishMetadataAction() {
        super();
        this.setText(Messages.PublishMetadataAction_Name);
        this.setToolTipText(Messages.PublishMetadataAction_Name);
        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.HIERARCHY_ICON));
    }
    
    public PublishMetadataAction(boolean needProgressBar){
    	this.needProgressBar = needProgressBar;
    }

    public void init(TreeViewer viewer, IStructuredSelection selection) {
        boolean canWork = true;
        if (selection.isEmpty() || (selection.size() > 1)) {
            setEnabled(false);
            return;
        }
        this.selection = selection;
        @SuppressWarnings("unchecked")
        List<RepositoryNode> nodes = selection.toList();
        for (RepositoryNode node : nodes) {
            if (node.getType() != ENodeType.REPOSITORY_ELEMENT
                    || node.getProperties(EProperties.CONTENT_TYPE) != ESBRepositoryNodeType.SERVICES) {
                canWork = false;
                break;
            }
            if (canWork && node.getObject() != null
                    && ProxyRepositoryFactory.getInstance().getStatus(node.getObject()) == ERepositoryStatus.DELETED) {
                canWork = false;
                break;
            }
        }
        setEnabled(canWork);
    }

    @Override
    public boolean isVisible() {
        return isEnabled();
    }

    public void setNodes(List<RepositoryNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    protected void doRun() {
        final IWorkspaceRunnable op = new IWorkspaceRunnable() {

            public void run(IProgressMonitor monitor) throws CoreException {
                monitor.beginTask(Messages.PublishMetadataAction_Importing, 100);
                if (nodes == null) {
                    nodes = selection.toList();
                }
                int step = 100;
                int size = nodes.size();
                if (size > 0) {
                    step /= size;
                }
                for (RepositoryNode node : nodes) {
                    monitor.worked(step);
                    WSDLUtils.validateWsdl(node);
                    Definition wsdlDefinition = WSDLUtils.getWsdlDefinition(node);
                    process(wsdlDefinition);
                    if (monitor.isCanceled()) {
                        break;
                    }
                }
                nodes = null;
                monitor.done();
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
                    nodes = null;
                    throw new InvocationTargetException(e);
                }

            }
        };

        try {
        	if(needProgressBar){
        		new ProgressMonitorDialog(null).run(true, true, iRunnableWithProgress);
        	}else{
        		PlatformUI.getWorkbench().getProgressService().run(true, true, iRunnableWithProgress);
        	}
        } catch (InvocationTargetException e) {
            ExceptionHandler.process(e);
            nodes = null;
        } catch (InterruptedException e) {
            //
        }
    }

    public void process(Definition wsdlDefinition) {
        populationUtil = new XSDPopulationUtil2();
        List<String> alreadyCreated = new ArrayList<String>();
        SchemaUtil schemaUtil = new SchemaUtil(wsdlDefinition);
        int index = 0;
        // to modify later, have a map done on the byte[] is really not good.
        Map<byte[], String> fileToSchemaMap = new HashMap<byte[], String>();
        for (XmlSchema schema : schemaUtil.getSchemas().keySet()) {
            String file = initFileContent(schemaUtil.getSchemas().get(schema), index);
            fileToSchemaMap.put(schemaUtil.getSchemas().get(schema), file);
            try {
                populationUtil.addSchema(file);
            } catch (IOException e) {
                ExceptionHandler.process(e);
            }
            index++;
        }

        Map<QName, Binding> bindings = wsdlDefinition.getBindings();
        List<PortType> portTypes = new ArrayList<PortType>(bindings.size());
        for (Binding binding : bindings.values()) {
            PortType portType = binding.getPortType();
            if (!portTypes.contains(portType)) {
                portTypes.add(portType);
                List<BindingOperation> operations = binding.getBindingOperations();
                for (BindingOperation operation : operations) {
                    Operation oper = operation.getOperation();
                    Input inDef = oper.getInput();
                    if (inDef != null) {
                        Message inMsg = inDef.getMessage();
                        if (inMsg != null) {
                            // fix for TDI-20699
                            ParameterInfo parameterFromMessage = schemaUtil.getParameterFromMessage(inMsg);
                            if (alreadyCreated.contains(parameterFromMessage.getName())) {
                                continue;
                            } else {
                                alreadyCreated.add(parameterFromMessage.getName());
                            }
                            populateMessage2(parameterFromMessage, portType.getQName(), oper, fileToSchemaMap);
                        }
                    }

                    Output outDef = oper.getOutput();
                    if (outDef != null) {
                        Message outMsg = outDef.getMessage();
                        if (outMsg != null) {
                            ParameterInfo parameterFromMessage = schemaUtil.getParameterFromMessage(outMsg);
                            if (alreadyCreated.contains(parameterFromMessage.getName())) {
                                continue;
                            } else {
                                alreadyCreated.add(parameterFromMessage.getName());
                            }
                            populateMessage2(parameterFromMessage, portType.getQName(), oper, fileToSchemaMap);
                        }
                    }
                    Collection<Fault> faults = oper.getFaults().values();
                    for (Fault fault : faults) {
                        Message faultMsg = fault.getMessage();
                        if (faultMsg != null) {
                            ParameterInfo parameterFromMessage = schemaUtil.getParameterFromMessage(faultMsg);
                            if (alreadyCreated.contains(parameterFromMessage.getName())) {
                                continue;
                            } else {
                                alreadyCreated.add(parameterFromMessage.getName());
                            }
                            populateMessage2(parameterFromMessage, portType.getQName(), oper, fileToSchemaMap);
                        }
                    }
                }
            }
        }
    }

    /**
     * old system, shouldn't be used anymore.
     */
    @Deprecated
    private void populateMessage(ParameterInfo parameter, QName portTypeQName) {
        String name = /* componentName + "_"+ */parameter.getName();
        XmlFileConnection connection = ConnectionFactory.eINSTANCE.createXmlFileConnection();
        connection.setName(ERepositoryObjectType.METADATA_FILE_XML.getKey());
        connection.setXmlFilePath(name + ".xsd"); //$NON-NLS-1$
        XmlFileConnectionItem connectionItem = PropertiesFactory.eINSTANCE.createXmlFileConnectionItem();
        Property connectionProperty = PropertiesFactory.eINSTANCE.createProperty();
        connectionProperty.setAuthor(((RepositoryContext) CoreRuntimePlugin.getInstance().getContext()
                .getProperty(Context.REPOSITORY_CONTEXT_KEY)).getUser());
        connectionProperty.setLabel(name);
        connectionProperty.setVersion(VersionUtils.DEFAULT_VERSION);
        connectionProperty.setStatusCode(""); //$NON-NLS-1$

        connectionItem.setProperty(connectionProperty);
        connectionItem.setConnection(connection);

        connection.setInputModel(true);
        // schema
        connection.setFileContent(parameter.getSchema());
        XmlXPathLoopDescriptor xmlXPathLoopDescriptor = ConnectionFactory.eINSTANCE.createXmlXPathLoopDescriptor();
        connection.getSchema().add(xmlXPathLoopDescriptor);
        xmlXPathLoopDescriptor.setAbsoluteXPathQuery('/' + parameter.getName());
        xmlXPathLoopDescriptor.setLimitBoucle(50);
        xmlXPathLoopDescriptor.setConnection(connection);
        for (String[] leaf : parameter.getLeafList()) {
            SchemaTarget target = ConnectionFactory.eINSTANCE.createSchemaTarget();
            xmlXPathLoopDescriptor.getSchemaTargets().add(target);
            target.setRelativeXPathQuery(leaf[0]);
            target.setTagName(leaf[1]);
        }

        // TODO: temporary make a fake root for the connection
        EList root = connection.getRoot();
        XMLFileNode xmlFileNode = ConnectionFactory.eINSTANCE.createXMLFileNode();
        String currentPath = '/' + name;
        xmlFileNode.setXMLPath(currentPath);
        root.add(xmlFileNode);

        // save
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        String nextId = factory.getNextId();
        connectionProperty.setId(nextId);
        try {
            factory.create(connectionItem, new Path(parameter.getNameSpace() + '/' + portTypeQName.getLocalPart()));
            ProxyRepositoryFactory.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());
            RepositoryManager.refresh(ERepositoryObjectType.METADATA_FILE_XML);
        } catch (PersistenceException e) {
        }
    }

    private int orderId;

    private boolean loopElementFound;

    /**
     * To optimize, right now it will write the xsd file many times. Since there is no clues if the parameters comes
     * from the same xsd, generate it everytime right now.
     * 
     * @param oper
     * @param hashMap
     */
    private void populateMessage2(ParameterInfo parameter, QName portTypeQName, Operation oper,
            Map<byte[], String> schemaToFileMap) {
        String name = /* componentName + "_"+ */parameter.getName();
        XmlFileConnection connection = ConnectionFactory.eINSTANCE.createXmlFileConnection();
        connection.setName(ERepositoryObjectType.METADATA_FILE_XML.getKey());
        XmlFileConnectionItem connectionItem = PropertiesFactory.eINSTANCE.createXmlFileConnectionItem();
        Property connectionProperty = PropertiesFactory.eINSTANCE.createProperty();
        connectionProperty.setAuthor(((RepositoryContext) CoreRuntimePlugin.getInstance().getContext()
                .getProperty(Context.REPOSITORY_CONTEXT_KEY)).getUser());
        connectionProperty.setLabel(name);
        connectionProperty.setVersion(VersionUtils.DEFAULT_VERSION);
        connectionProperty.setStatusCode(""); //$NON-NLS-1$

        connectionItem.setProperty(connectionProperty);
        connectionItem.setConnection(connection);

        connection.setInputModel(false);

        // don't put any XSD directly inside the xml connection but put zip file
        Collection<String> fileStringCol = schemaToFileMap.values();
        File[] files = new File[fileStringCol.size()];
        Object[] fileStringArray = fileStringCol.toArray();
        for (int i = 0; i < fileStringCol.size(); i++) {
            String fileString = (String) fileStringArray[i];
            File file = new File(fileString);
            files[i] = file;
        }
        Project project = ProjectManager.getInstance().getCurrentProject();
        IProject fsProject = null;
        try {
            fsProject = ResourceModelUtils.getProject(project);
        } catch (PersistenceException e2) {
            ExceptionHandler.process(e2);
        }
        String Path = schemaToFileMap.get(parameter.getSchema());
        String zipFile = new Path(Path) + ".zip";
        try {
            org.talend.utils.io.FilesUtils.zips(files, zipFile);
        } catch (Exception e2) {
            ExceptionHandler.process(e2);
        }
        File zip = new File(zipFile);
        try {
            if (zip.exists()) {
                ByteArray byteArray = PropertiesFactory.eINSTANCE.createByteArray();
                byteArray.setInnerContentFromFile(zip);
                connection.setFileContent(byteArray.getInnerContent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // connection.setFileContent(parameter.getSchema());

        XSDSchema xsdSchema;
        try {
            String filePath = schemaToFileMap.get(parameter.getSchema()); // name of xsd file needed
            if (filePath == null) {
                // just in case, but should never happen
                return;
            }
            connection.setXmlFilePath(new Path(filePath).lastSegment() + ".zip"); //$NON-NLS-1$

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
        } catch (MalformedURLException e1) {
            ExceptionHandler.process(e1);
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
            String folderString = parameter.getNameSpace() + '/' + portTypeQName.getLocalPart();
            try {
                URI uri = new URI(folderString);
                String scheme = uri.getScheme();
                if (scheme != null) {
                    folderString = folderString.substring(scheme.length());
                }
            } catch (URISyntaxException e) {

            }
            if (folderString.startsWith(":")) { //$NON-NLS-1$
                folderString = folderString.substring(1);
            }
            folderString = FolderNameUtil.replaceAllLimited(folderString);
            IPath path = new Path(folderString + '/' + oper.getName());
            // if (path.segmentCount() > 0 && path.segment(0).startsWith(":")) {
            // path = path.removeFirstSegments(1);
            // }
            factory.create(connectionItem, path, true); // consider this as migration will overwrite the old metadata if
                                                        // existing in the same path

            ProxyRepositoryFactory.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());
            RepositoryManager.refresh(ERepositoryObjectType.METADATA_FILE_XML);
        } catch (PersistenceException e) {
        }
    }

    private void fillRootInfo(XmlFileConnection connection, ATreeNode node, String path, boolean inLoop) throws OdaException {
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

    private String initFileContent(byte[] fileContent, int index) {
        byte[] bytes = fileContent;
        Project project = ProjectManager.getInstance().getCurrentProject();
        IProject fsProject = null;
        try {
            fsProject = ResourceModelUtils.getProject(project);
        } catch (PersistenceException e2) {
            ExceptionHandler.process(e2);
        }
        if (fsProject == null) {
            return null;
        }
        String temPath = fsProject.getLocationURI().getPath() + File.separator + "temp"; //$NON-NLS-1$
        String fileName = "tempXSDFile" + index + ".XSD"; //$NON-NLS-1$ //$NON-NLS-2$
        File temfile = new File(temPath + File.separator + fileName);

        if (!temfile.exists()) {
            try {
                temfile.createNewFile();
            } catch (IOException e) {
                ExceptionHandler.process(e);
            }
        }
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(temfile);
            outStream.write(bytes);
            outStream.close();
        } catch (FileNotFoundException e1) {
            ExceptionHandler.process(e1);
        } catch (IOException e) {
            ExceptionHandler.process(e);
        }
        return temfile.getPath();
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

}
