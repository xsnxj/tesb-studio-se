// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
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
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;

/**
 * @author xpli
 *
 */
public class Switch293ReleaseCamelVersionTask extends AbstractItemMigrationTask {

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
		GregorianCalendar gc = new GregorianCalendar(2012, 4, 19, 14, 00, 00);
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

	private void switchVersion(Item item) throws PersistenceException {
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
								evtValue = switchVersion(evtValue);
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
	 * @return
	 */
	private String switchVersion(String evtValue) {
		if (evtValue == null) {
			return evtValue;
		}

		String result = "";
		if (evtValue.startsWith("camel-")) {
			result = evtValue.replace("2.9.2", "2.9.3");
		}
		if (evtValue.startsWith("cxf-bundle")) {
			result = evtValue.replace("2.6.0", "2.6.2");
		}
		if (evtValue.startsWith("spring-")) {
			result = evtValue.replace("3.0.6", "3.0.7");
		}
		return result;
	}

}
