// ============================================================================
package org.talend.camel.designer.migration;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.migration.AbstractItemMigrationTask;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;

/**
 * Migration on cJMS and cActiveMQ component
 */
public class NewJMSComponentMigrationTask extends AbstractItemMigrationTask {

	private static final ProxyRepositoryFactory FACTORY = ProxyRepositoryFactory
			.getInstance();

	@Override
	public List<ERepositoryObjectType> getTypes() {
		List<ERepositoryObjectType> toReturn = new ArrayList<ERepositoryObjectType>();
		toReturn.add(CamelRepositoryNodeType.repositoryRoutesType);
		return toReturn;
	}

	public ProcessType getProcessType(Item item) {
		if (item instanceof ProcessItem) {
			return ((ProcessItem) item).getProcess();
		}
		return null;
	}

	public Date getOrder() {
		GregorianCalendar gc = new GregorianCalendar(2011, 11, 6, 10, 30, 00);
		return gc.getTime();
	}

	@Override
	public ExecutionResult execute(Item item) {

		try {
			adapteJMS(item);
			return ExecutionResult.SUCCESS_NO_ALERT;
		} catch (Exception e) {
			ExceptionHandler.process(e);
			return ExecutionResult.FAILURE;
		}

	}

	/**
	 * Switch cActiveMQ to cJMS and adjust some parameters for cJMS.
	 * 
	 * @param item
	 * @throws PersistenceException
	 */
	private void adapteJMS(Item item) throws PersistenceException {

		ProcessType processType = getProcessType(item);
		for (Object o : processType.getNode()) {
			if (o instanceof NodeType) {
				NodeType currentNode = (NodeType) o;
				if ("cActiveMQ".equals(currentNode.getComponentName())) {
					currentNode.setComponentName("cJMS");
					// Set schema name
					String name = computeTextElementValue("UNIQUE_NAME",
							currentNode.getElementParameter());
					if (name != null) {
						name = name.replace("_", "");
					} else {
						name = "name";
					}
					ElementParameterType paramType = createParamType("TEXT",
							"NAME", "\"" + name + "\"");
					currentNode.getElementParameter().add(paramType);
					ElementParameterType brokerType = createParamType(
							"CLOSED_LIST", "MQ_TYPE", "ActiveMQ");
					currentNode.getElementParameter().add(brokerType);

					ElementParameterType param = findElementParameterByName(
							"BROKER_URI", currentNode.getElementParameter());
					if (param != null) {
						param.setName("AMQ_BROKER_URI");
					}
				} else if ("cJMS".equals(currentNode.getComponentName())) {
					ElementParameterType param = createParamType("CLOSED_LIST",
							"MQ_TYPE", "Other");
					currentNode.getElementParameter().add(param);
					param = findElementParameterByName("CODE",
							currentNode.getElementParameter());
					if (param != null) {
						param.setName("OTHER_CODE");
					}
					param = findElementParameterByName("DRIVER_JAR",
							currentNode.getElementParameter());
					if (param != null) {
						param.setName("OTHER_DRIVER_JAR");
					}
				}
			}
		}

		FACTORY.save(item, true);

	}

	protected ElementParameterType findElementParameterByName(String paramName,
			EList<?> elementParameterTypes) {
		for (Object obj : elementParameterTypes) {
			ElementParameterType cpType = (ElementParameterType) obj;
			if (paramName.equals(cpType.getName())) {
				return cpType;
			}
		}
		return null;
	}

	protected boolean computeCheckElementValue(String paramName,
			EList<?> elementParameterTypes) {
		ElementParameterType cpType = findElementParameterByName(paramName,
				elementParameterTypes);
		if (cpType == null) {
			return false;
		}
		String isNone = cpType.getValue();
		return "true".equals(isNone);
	}

	protected String computeTextElementValue(String paramName,
			EList<?> elementParameterTypes) {
		ElementParameterType cpType = findElementParameterByName(paramName,
				elementParameterTypes);
		if (cpType == null) {
			return "";
		}
		return cpType.getValue() == null ? "" : cpType.getValue();
	}

	/**
	 * 
	 * Create a parameter of a node.
	 * 
	 * @param elemParams
	 * @param field
	 * @param name
	 * @param value
	 */
	protected ElementParameterType createParamType(String field, String name,
			String value) {
		ElementParameterType paramType = TalendFileFactory.eINSTANCE
				.createElementParameterType();
		paramType.setField(field);
		paramType.setName(name);
		paramType.setValue(value);
		return paramType;
	}
}
