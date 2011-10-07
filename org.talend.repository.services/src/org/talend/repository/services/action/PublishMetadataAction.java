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
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.SchemaTarget;
import org.talend.core.model.metadata.builder.connection.XmlFileConnection;
import org.talend.core.model.metadata.builder.connection.XmlXPathLoopDescriptor;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.XmlFileConnectionItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ParameterInfo;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.services.utils.SchemaUtil;
import org.talend.repository.services.utils.WSDLUtils;
import org.talend.repository.ui.actions.AContextualAction;

/**
 * Action used to export job scripts. <br/>
 * 
 * $Id: ExportJobScriptAction.java 1 2006-12-13 下午03:12:05 bqian
 * 
 */
public class PublishMetadataAction extends AContextualAction {

	protected static final String ACTION_LABEL = "Publish WSDL Schemas";
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
		@SuppressWarnings("unchecked")
		List<RepositoryNode> nodes = (List<RepositoryNode>)selection.toList();
		for (RepositoryNode node : nodes) {
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
									populateMessage(schemaUtil.getParameterFromMessage(inMsg), portType.getQName());
								}
							}

							Output outDef = oper.getOutput();
							if (outDef != null) {
								Message outMsg = outDef.getMessage();
								if (outMsg != null) {
									populateMessage(schemaUtil.getParameterFromMessage(outMsg), portType.getQName());
								}
							}
							Collection<Fault> faults = oper.getFaults().values();
							for (Fault fault : faults) {
								Message faultMsg = fault.getMessage();
								if (faultMsg != null) {
									populateMessage(schemaUtil.getParameterFromMessage(faultMsg), portType.getQName());
								}
							}
						}
					}
				}

			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void populateMessage(ParameterInfo parameter, QName portTypeQName) {
		String name = /*componentName + "_"+*/parameter.getName();
		XmlFileConnection connection = ConnectionFactory.eINSTANCE.createXmlFileConnection();
		connection.setName(ERepositoryObjectType.METADATA_FILE_XML.getKey());
		connection.setXmlFilePath(name+".xsd");
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
		//schema
		connection.setFileContent(parameter.getSchema());
		XmlXPathLoopDescriptor xmlXPathLoopDescriptor = ConnectionFactory.eINSTANCE.createXmlXPathLoopDescriptor();
		connection.getSchema().add(xmlXPathLoopDescriptor);
		xmlXPathLoopDescriptor.setAbsoluteXPathQuery("/"+parameter.getName());
		xmlXPathLoopDescriptor.setLimitBoucle(50);
		xmlXPathLoopDescriptor.setConnection(connection);
		for (String[] leaf : parameter.getLeafList()) {
			SchemaTarget target = ConnectionFactory.eINSTANCE.createSchemaTarget();
			xmlXPathLoopDescriptor.getSchemaTargets().add(target);
			target.setRelativeXPathQuery(leaf[0]);
			target.setTagName(leaf[1]);
		}
		// save
		IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
		String nextId = factory.getNextId();
		connectionProperty.setId(nextId);
		try {
			factory.create(connectionItem, new Path(portTypeQName.getNamespaceURI() + "/" + portTypeQName.getLocalPart()));
			ProxyRepositoryFactory.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());
			RepositoryManager.refresh(ERepositoryObjectType.METADATA_FILE_XML);
		} catch (PersistenceException e) {
		}
	}

}
