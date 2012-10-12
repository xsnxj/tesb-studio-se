// ============================================================================
package org.talend.camel.designer.migration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

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
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;

/**
 * DOC GangLiu class global comment. Detailled comment
 */
public class FinalVersionUpgradeMigrationTask extends AbstractItemMigrationTask {

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
		GregorianCalendar gc = new GregorianCalendar(2012, 10, 8, 14, 00, 00);
		return gc.getTime();
	}

	@Override
	public ExecutionResult execute(Item item) {

		try {
			switchVersion(item);
			return ExecutionResult.SUCCESS_NO_ALERT;
		} catch (Exception e) {
			ExceptionHandler.process(e);
			return ExecutionResult.FAILURE;
		}

	}

	private void switchVersion(Item item) throws PersistenceException,
			IOException {
		InputStream is = getClass()
				.getResourceAsStream("VersionMap.properties");
		Properties properties = new Properties();
		properties.load(is);
		is.close();
		ProcessType processType = getProcessType(item);
		for (Object o : processType.getNode()) {
			if (o instanceof NodeType) {
				NodeType currentNode = (NodeType) o;
				if ("cMessagingEndpoint".equals(currentNode.getComponentName())) {
					for (Object e : currentNode.getElementParameter()) {
						ElementParameterType p = (ElementParameterType) e;
						if ("HOTLIBS".equals(p.getName())) {
							EList elementValue = p.getElementValue();
							for (Object pv : elementValue) {
								ElementValueType evt = (ElementValueType) pv;
								String evtValue = evt.getValue();
								evtValue = switchVersion(evtValue, properties);
								evt.setValue(evtValue);
							}
						}
					}
				}
			}
		}

		FACTORY.save(item, true);

	}

	/**
	 * 
	 * DOC LiXP Comment method "switchVersion".
	 * 
	 * @param evtValue
	 * @param properties
	 * @return
	 */
	private String switchVersion(String evtValue, Properties properties) {
		if (evtValue == null) {
			return evtValue;
		}
		int lastIndexOf = evtValue.lastIndexOf("-");
		if (lastIndexOf == -1) {
			return evtValue;
		}
		evtValue = evtValue.substring(0, lastIndexOf);
		Object result = properties.get(evtValue);
		if (result == null) {
			return evtValue;
		}
		return result.toString();
	}

}
