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
package org.talend.repository.services.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.enablement.oda.xml.util.ui.ATreeNode;
import org.eclipse.datatools.enablement.oda.xml.util.ui.XSDPopulationUtil2;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.xsd.XSDSchema;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.utils.VersionUtils;
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
import org.talend.repository.ProjectManager;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.Activator;
import org.talend.repository.services.model.services.ParameterInfo;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.services.utils.FolderNameUtil;
import org.talend.repository.services.utils.SchemaUtil;
import org.talend.repository.services.utils.WSDLUtils;
import org.talend.repository.ui.actions.AContextualAction;
import org.talend.repository.ui.wizards.metadata.connection.files.xml.util.StringUtil;
import orgomg.cwm.resource.record.RecordFactory;
import orgomg.cwm.resource.record.RecordFile;

/**
 * Action used to export job scripts. <br/>
 * 
 * $Id: ExportJobScriptAction.java 1 2006-12-13 下午03:12:05 bqian
 * 
 */
public class PublishMetadataAction extends AContextualAction {

    protected static final String ACTION_LABEL = "Import WSDL Schemas";

    private IStructuredSelection selection;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse.jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public PublishMetadataAction() {
        super();
        this.setText(ACTION_LABEL);
        this.setToolTipText(ACTION_LABEL);
        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.HIERARCHY_ICON));
    }

    public void init(TreeViewer viewer, IStructuredSelection selection) {
        boolean canWork = true;
        if (selection.isEmpty() || (selection.size() > 1)) {
            setEnabled(false);
            return;
        }
        this.selection = selection;
        @SuppressWarnings("unchecked")
        List<RepositoryNode> nodes = (List<RepositoryNode>) selection.toList();
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

    public boolean isVisible() {
        return isEnabled();
    }

    protected void doRun() {
        UIJob job = new UIJob("importing...") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                monitor.beginTask("importing", 100);
                @SuppressWarnings("unchecked")
                List<RepositoryNode> nodes = (List<RepositoryNode>) selection.toList();
                int step = 100;
                int size = nodes.size();
                if (size > 0) {
                    step /= size;
                }
                for (RepositoryNode node : nodes) {
                    monitor.worked(step);
                    try {
                        Definition wsdlDefinition = WSDLUtils.getWsdlDefinition(node);
                        SchemaUtil schemaUtil = new SchemaUtil(wsdlDefinition);
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
                                            populateMessage2(schemaUtil.getParameterFromMessage(inMsg), portType.getQName());
                                        }
                                    }

                                    Output outDef = oper.getOutput();
                                    if (outDef != null) {
                                        Message outMsg = outDef.getMessage();
                                        if (outMsg != null) {
                                            populateMessage2(schemaUtil.getParameterFromMessage(outMsg), portType.getQName());
                                        }
                                    }
                                    Collection<Fault> faults = oper.getFaults().values();
                                    for (Fault fault : faults) {
                                        Message faultMsg = fault.getMessage();
                                        if (faultMsg != null) {
                                            populateMessage2(schemaUtil.getParameterFromMessage(faultMsg), portType.getQName());
                                        }
                                    }
                                }
                            }
                        }

                    } catch (CoreException e) {
                        e.printStackTrace();
                        monitor.done();
                        return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
                    }
                }
                monitor.done();
                return Status.OK_STATUS;
            }

        };
        job.setUser(true);
        job.schedule();
    }

    /**
     * old system, shouldn't be used anymore.
     */
    @Deprecated
    private void populateMessage(ParameterInfo parameter, QName portTypeQName) {
        String name = /* componentName + "_"+ */parameter.getName();
        XmlFileConnection connection = ConnectionFactory.eINSTANCE.createXmlFileConnection();
        connection.setName(ERepositoryObjectType.METADATA_FILE_XML.getKey());
        connection.setXmlFilePath(name + ".xsd");
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
        xmlXPathLoopDescriptor.setAbsoluteXPathQuery("/" + parameter.getName());
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
        String currentPath = "/" + name;
        xmlFileNode.setXMLPath(currentPath);
        root.add(xmlFileNode);

        // save
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        String nextId = factory.getNextId();
        connectionProperty.setId(nextId);
        try {
			factory.create(connectionItem, new Path(parameter.getNameSpace()
					+ "/" + portTypeQName.getLocalPart()));
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
     */
    private void populateMessage2(ParameterInfo parameter, QName portTypeQName) {
        String name = /* componentName + "_"+ */parameter.getName();
        XmlFileConnection connection = ConnectionFactory.eINSTANCE.createXmlFileConnection();
        connection.setName(ERepositoryObjectType.METADATA_FILE_XML.getKey());
        connection.setXmlFilePath(name + ".xsd");
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
        // schema
        connection.setFileContent(parameter.getSchema());
        String filePath = initFileContent(parameter.getSchema());
        XSDSchema xsdSchema;
        try {
            xsdSchema = new XSDPopulationUtil2().getXSDSchema(filePath);
            List<ATreeNode> rootNodes = new XSDPopulationUtil2().getAllRootNodes(xsdSchema);

            ATreeNode node = null;

            // try to find the root element needed from XSD file.
            // note: if there is any prefix, it will get the node with the first correct name, no matter the prefix.

            // once the we can get the correct prefix value from the wsdl, this code should be modified.
            for (ATreeNode curNode : rootNodes) {
                String curName = (String) curNode.getValue();
                if (curName.contains(":")) {
                    // if with prefix, don't care about it for now, just compare the name.
                    if (curName.split(":")[1].equals(name)) {
                        node = curNode;
                        break;
                    }
                } else if (curName.equals(name)) {
                    node = curNode;
                    break;
                }
            }

            node = new XSDPopulationUtil2().getSchemaTree(xsdSchema, node, true);
            orderId = 1;
            loopElementFound = false;
            if (ConnectionHelper.getTables(connection).isEmpty()) {
                MetadataTable table = ConnectionFactory.eINSTANCE.createMetadataTable();
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
            fillRootInfo(connection, node, "");

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
			String folderString = parameter.getNameSpace() + "/"
					+ portTypeQName.getLocalPart();
			try {
				URI uri = new URI(folderString);
				String scheme = uri.getScheme();
				if (scheme != null) {
					folderString = folderString.substring(scheme.length());
				}
			} catch (URISyntaxException e) {

			}
			if (folderString.startsWith(":")) {
				folderString = folderString.substring(1);
			}
			folderString = FolderNameUtil.replaceAllLimited(folderString);
			IPath path = new Path(folderString);
//			if (path.segmentCount() > 0 && path.segment(0).startsWith(":")) {
//				path = path.removeFirstSegments(1);
//			}
			factory.create(connectionItem, path);

            ProxyRepositoryFactory.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());
            RepositoryManager.refresh(ERepositoryObjectType.METADATA_FILE_XML);
        } catch (PersistenceException e) {
        }
    }

    private void fillRootInfo(XmlFileConnection connection, ATreeNode node, String path) throws OdaException {
        XMLFileNode xmlNode = ConnectionFactory.eINSTANCE.createXMLFileNode();
        xmlNode.setXMLPath(path + "/" + node.getValue());
        xmlNode.setOrder(orderId);
        orderId++;
        MappingTypeRetriever retriever;
        String nameWithoutPrefixForColumn;
        String curName = (String) node.getValue();
        if (curName.contains(":")) {
            nameWithoutPrefixForColumn = curName.split(":")[1];
        } else {
            nameWithoutPrefixForColumn = curName;
        }
        retriever = MetadataTalendType.getMappingTypeRetriever("xsd_id");
        xmlNode.setAttribute("attri");
        xmlNode.setType(retriever.getDefaultSelectedTalendType(node.getDataType()));
        MetadataColumn column = null;
        switch (node.getType()) {
        case ATreeNode.ATTRIBUTE_TYPE:
            xmlNode.setRelatedColumn(nameWithoutPrefixForColumn);
            column = ConnectionFactory.eINSTANCE.createMetadataColumn();
            column.setTalendType(xmlNode.getType());
            column.setLabel(nameWithoutPrefixForColumn);
            ConnectionHelper.getTables(connection).toArray(new MetadataTable[0])[0].getColumns().add(column);
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
                xmlNode.setAttribute("branch");
                retriever = MetadataTalendType.getMappingTypeRetriever("xsd_id");
                xmlNode.setRelatedColumn(nameWithoutPrefixForColumn);
                xmlNode.setType(retriever.getDefaultSelectedTalendType(node.getDataType()));
                column = ConnectionFactory.eINSTANCE.createMetadataColumn();
                column.setTalendType(xmlNode.getType());
                column.setLabel(nameWithoutPrefixForColumn);
                ConnectionHelper.getTables(connection).toArray(new MetadataTable[0])[0].getColumns().add(column);
            }
            break;
        case ATreeNode.NAMESPACE_TYPE:
            xmlNode.setAttribute("ns");
            // specific for namespace... no path set, there is only the prefix value.
            // this value is saved now in node.getDataType()
            xmlNode.setXMLPath(node.getDataType());

            xmlNode.setDefaultValue((String) node.getValue());
            break;
        case ATreeNode.OTHER_TYPE:
            break;
        }
        // will try to get the first element (branch or main), and set it as loop.
        if (!loopElementFound && path.split("/").length == 2 && node.getType() == ATreeNode.ELEMENT_TYPE) {
            connection.getLoop().add(xmlNode);

            for (XMLFileNode curNode : connection.getRoot()) {
                if (curNode.getXMLPath().startsWith(path)) {
                    curNode.setAttribute("main");
                }
            }
            xmlNode.setAttribute("main");
            loopElementFound = true;
        } else {
            connection.getRoot().add(xmlNode);
        }
        if (node.getChildren().length > 0) {
            for (Object curNode : node.getChildren()) {
                fillRootInfo(connection, (ATreeNode) curNode, path + "/" + node.getValue());
            }
        }
    }

    private String initFileContent(byte[] fileContent) {
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
        String fileName = StringUtil.TMP_XSD_FILE;
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

}
