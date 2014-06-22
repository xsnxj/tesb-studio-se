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

public class CBeanCTalendJobMigrationTask extends AbstractItemMigrationTask {

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
		GregorianCalendar gc = new GregorianCalendar(2012, 9, 18, 14, 00, 00);
		return gc.getTime();
	}

	@Override
	public ExecutionResult execute(Item item) {

		try {
			cBeancTalendJob(item);
			return ExecutionResult.SUCCESS_NO_ALERT;
		} catch (Exception e) {
			ExceptionHandler.process(e);
			return ExecutionResult.FAILURE;
		}

	}

	private void cBeancTalendJob(Item item) throws PersistenceException {
		ProcessType processType = getProcessType(item);
		for (Object o : processType.getNode()) {
			if (o instanceof NodeType) {
				NodeType currentNode = (NodeType) o;
				String componentName = currentNode.getComponentName();
				if ("cBean".equals(componentName)) {
					migrateCBean(currentNode);
				} else if ("cTalendJob".equals(componentName)) {
					migrateCTalendJob(currentNode);
				}
			}
		}

		FACTORY.save(item, true);

	}

	private void migrateCBean(NodeType currentNode) {
		EList elementParameter = currentNode.getElementParameter();
		boolean isNewElement = false;
		for (Object obj : elementParameter) {
			if (!(obj instanceof ElementParameterType)) {
				continue;
			}
			ElementParameterType param = (ElementParameterType) obj;
			String name = param.getName();
			if ("FROM_REGISTRY".equals(name) || "FROM_CLASS".equals(name)) {
				isNewElement = true;
				break;
			}
		}
		if (isNewElement) {
			return;
		}

		ElementParameterType fromClass = TalendFileFactory.eINSTANCE
				.createElementParameterType();
		fromClass.setName("FROM_CLASS");
		fromClass.setValue("true");
		elementParameter.add(fromClass);

		ElementParameterType fromRegistry = TalendFileFactory.eINSTANCE
				.createElementParameterType();
		fromRegistry.setName("FROM_REGISTRY");
		fromRegistry.setValue("false");
		elementParameter.add(fromRegistry);

	}

	private void migrateCTalendJob(NodeType currentNode) {
		EList elementParameter = currentNode.getElementParameter();
		boolean isNewElement = false;
		for (Object obj : elementParameter) {
			if (!(obj instanceof ElementParameterType)) {
				continue;
			}
			ElementParameterType param = (ElementParameterType) obj;
			String name = param.getName();
			if ("FROM_REPOSITORY_JOB".equals(name)
					|| "FROM_EXTERNAL_JAR".equals(name)) {
				isNewElement = true;
				break;
			}
		}
		if (isNewElement) {
			return;
		}

		ElementParameterType fromExternal = TalendFileFactory.eINSTANCE
				.createElementParameterType();
		fromExternal.setName("FROM_EXTERNAL_JAR");
		fromExternal.setValue("true");
		elementParameter.add(fromExternal);

		ElementParameterType fromRepository = TalendFileFactory.eINSTANCE
				.createElementParameterType();
		fromRepository.setName("FROM_REPOSITORY_JOB");
		fromRepository.setValue("false");
		elementParameter.add(fromRepository);
	}
}