package org.talend.designer.publish.core.models;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.properties.ProcessItem;
import org.talend.designer.core.model.utils.emf.talendfile.ColumnType;
import org.talend.designer.core.model.utils.emf.talendfile.ContextParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.MetadataType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.publish.core.models.meta.ContextItem;
import org.talend.designer.publish.core.models.meta.DataField;
import org.talend.designer.publish.core.models.meta.Metadata;
import org.talend.core.model.metadata.types.JavaTypesManager;

public class IPaasMetadataModel extends BaseModel {

	private static final String JOB_TYPE = "Job";
	private static final String TIPAAS_OUTPUT = "tIPaasOutput";
	private static final String TIPAAS_INPUT = "tIPaasInput";
	
	public static final String NAME_SUFFIX = "-metadata";

	private ProcessItem processItem;
	private FeaturesModel featureModel;

	public IPaasMetadataModel(ProcessItem processItem, FeaturesModel featureModel, String groupId,
			String artifactId, String version) {
		super(groupId, artifactId+NAME_SUFFIX, version, "pom");
		this.processItem = processItem;
		this.featureModel = featureModel;
	}

	public byte[] getContent() {
		return constructContent();
	}
	
	public FeaturesModel getFeatureModel() {
		return featureModel;
	}

	private byte[] constructContent() {

		Metadata metadata = new Metadata(processItem.getProperty().getLabel(),
				processItem.getProperty().getVersion(), JOB_TYPE);

		// mvn:org.example/ActionJob-feature/0.2.0-SNAPSHOT/xml
		String repository = "mvn:" + featureModel.getGroupId() + "/" + featureModel.getArtifactId() + "/"
				+ featureModel.getVersion() + "/xml";
		metadata.setRepository(repository);

		addInputOutputFields(metadata);

		addContextItems(metadata);

		return saveModel(metadata);
	}

	private void addInputOutputFields(Metadata metadata) {
		NodeType tIPaasInputNode = null;
		NodeType tIPaasOutputNode = null;

		// found input and output component
		EList<?> nodes = processItem.getProcess().getNode();
		for (Object n : nodes) {
			NodeType node = (NodeType) n;
			String componentName = node.getComponentName();
			if (TIPAAS_INPUT.equals(componentName)) {
				if (isActive(node)) {
					tIPaasInputNode = node;
				}
			} else if (TIPAAS_OUTPUT.equals(componentName)) {
				if (isActive(node)) {
					tIPaasOutputNode = node;
				}
			}
			if (tIPaasInputNode != null && tIPaasOutputNode != null) {
				break;
			}
		}

		// add input
		addFields(tIPaasInputNode, metadata, true);

		// add output
		addFields(tIPaasOutputNode, metadata, false);
	}

	private void addFields(NodeType node, Metadata metadata, boolean isIn) {
		// add input
		if (node != null) {
			EList<?> nodeMetadatas = node.getMetadata();
			for (Object m : nodeMetadatas) {
				MetadataType mt = (MetadataType) m;
				EList<?> columns = mt.getColumn();
				for (Object c : columns) {
					ColumnType ct = (ColumnType) c;
					String name = ct.getName();
					String type = ct.getType();
					type = JavaTypesManager.getTypeToGenerate(type, false);
					if (isIn) {
						metadata.addInputField(new DataField(name, type));
					} else {
						metadata.addOutputField(new DataField(name, type));
					}
				}
			}
		}
	}

	private void addContextItems(Metadata metadata) {
		ProcessType process = processItem.getProcess();
		String defaultContext = process.getDefaultContext();
		EList<?> context = process.getContext();
		ContextType defaultContextType = null;
		Iterator<?> iterator = context.iterator();
		while (iterator.hasNext()) {
			Object next = iterator.next();
			if (!(next instanceof ContextType)) {
				continue;
			}
			ContextType ct = (ContextType) next;
			if (!ct.getName().equals(defaultContext)) {
				continue;
			}
			defaultContextType = ct;
			break;
		}
		if (defaultContextType != null) {
			EList<?> params = defaultContextType.getContextParameter();
			for (Object param : params) {
				metadata.addConfigItem(new ContextItem(
						((ContextParameterType) param).getName(),
						((ContextParameterType) param).getValue()));
			}
		}
	}

	private byte[] saveModel(Metadata metadata) {
		// convert model to string
		ByteArrayOutputStream os = null;
		try {
			JAXBContext context = JAXBContext.newInstance(Metadata.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			os = new ByteArrayOutputStream();
			marshaller.marshal(metadata, os);
			return os.toByteArray();
		} catch (JAXBException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	private boolean isActive(NodeType node) {
		EList<?> parameters = node.getElementParameter();
		for (Object obj : parameters) {
			ElementParameterType cpType = (ElementParameterType) obj;
			if ("ACTIVATE".equals(cpType.getName())) {
				return false;
			}
		}
		return true;
	}

}
