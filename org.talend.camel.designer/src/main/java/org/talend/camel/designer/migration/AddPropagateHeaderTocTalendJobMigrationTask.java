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
import org.talend.migration.IMigrationTask.ExecutionResult;

public class AddPropagateHeaderTocTalendJobMigrationTask extends AbstractItemMigrationTask {
	
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
			cTalendJobAddCheckbox(item);
			return ExecutionResult.SUCCESS_NO_ALERT;
		} catch (Exception e) {
			ExceptionHandler.process(e);
			return ExecutionResult.FAILURE;
		}
	}

	private void cTalendJobAddCheckbox(Item item) throws PersistenceException {
		ProcessType processType = getProcessType(item);
		for (Object o : processType.getNode()) {
			if (o instanceof NodeType) {
				NodeType currentNode = (NodeType) o;
				String componentName = currentNode.getComponentName();
				if ("cTalendJob".equals(componentName)) {
					addPropagateHeader(currentNode);
				}
			}
		}
		FACTORY.save(item, true);
	}
	
	private void addPropagateHeader(NodeType currentNode) {
		EList elementParameter = currentNode.getElementParameter();
		boolean isNewElement = false;
		for (Object obj : elementParameter) {
			if (!(obj instanceof ElementParameterType)) {
				continue;
			}
			ElementParameterType param = (ElementParameterType) obj;
			String name = param.getName();
			if ("PROPAGATE_HEADER".equals(name)) {
				isNewElement = true;
				break;
			}
		}
		if (isNewElement) {
			return;
		}

		ElementParameterType propageteHeader = TalendFileFactory.eINSTANCE
				.createElementParameterType();
		propageteHeader.setName("PROPAGATE_HEADER");
		propageteHeader.setField("CHECK");		
		propageteHeader.setValue("true");
		elementParameter.add(propageteHeader);
	}
}
