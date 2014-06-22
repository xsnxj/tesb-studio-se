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
package org.talend.designer.esb.webservice.migration;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.migration.AbstractItemMigrationTask;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;
import org.talend.designer.core.model.utils.emf.talendfile.impl.ElementParameterTypeImpl;

public class EmptyWSDLLocationMigrationTask extends AbstractItemMigrationTask {

	private static final ProxyRepositoryFactory FACTORY = ProxyRepositoryFactory
			.getInstance();

	public Date getOrder() {
		GregorianCalendar gc = new GregorianCalendar(2011, 12, 30, 17, 21, 00);
		return gc.getTime();
	}

	public ExecutionResult execute(Item item) {
		try {
			setWSDLLocation(item);
		} catch (Exception e) {
			ExceptionHandler.process(e);
			return ExecutionResult.FAILURE;
		}
		return ExecutionResult.SUCCESS_NO_ALERT;
	}

	private void setWSDLLocation(Item item) throws PersistenceException {
		if (item instanceof ProcessItem) {
			ProcessType processType = ((ProcessItem) item).getProcess();
			for (Object o : processType.getNode()) {
				if (o instanceof NodeType) {
					NodeType currentNode = (NodeType) o;
					if ("tESBConsumer".equals(currentNode.getComponentName())) {
						EList elements = currentNode.getElementParameter();
						Iterator iterator = elements.iterator();
						String endpoint = null;
						boolean isFoundWSDLLocation = false;
						while (iterator.hasNext()) {
							ElementParameterType elementParameter = (ElementParameterType) iterator
									.next();
							if ("ENDPOINT".equals(elementParameter.getName())) {
								endpoint = (String) elementParameter.getValue();
							}
							if ("WSDL_LOCATION".equals(elementParameter
									.getName())) {
								isFoundWSDLLocation = true;
							}
						}
						if (!isFoundWSDLLocation && endpoint != null) {
							ElementParameterType et = TalendFileFactory.eINSTANCE
									.createElementParameterType();
							et.setField("TEXT");
							et.setName("WSDL_LOCATION");
							et.setValue(endpoint);
							currentNode.getElementParameter().add(et);
						}
					}
				}
			}
			FACTORY.save(item, true);
		}

	}
}
