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
package org.talend.camel.designer.migration;

import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.emf.common.util.EList;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.migration.AbstractItemMigrationTask;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;

/**
 * http://jira.talendforge.org/browse/TESB-6440
 * 
 * @author LiXiaopeng
 * 
 */
public class UpdatecJMSMigrationTask extends AbstractItemMigrationTask {

    private static final ProxyRepositoryFactory REPO_FACTORY = ProxyRepositoryFactory.getInstance();

    private static final TalendFileFactory FILE_FACTORY = TalendFileFactory.eINSTANCE;

    /**
     * 
     * Create a parameter of a node.
     * 
     * @param elemParams
     * @param field
     * @param name
     * @param value
     */
    protected ElementParameterType createParamType(String field, String name, String value) {
        return createParamType(field, name, value, null);
    }

    /**
     * 
     * Create a parameter of a node.
     * 
     * @param elemParams
     * @param field
     * @param name
     * @param value
     * @param elementParameterTypes
     */
    protected ElementParameterType createParamType(String field, String name, String value, EList<?> elementParameterTypes) {
        ElementParameterType paramType = FILE_FACTORY.createElementParameterType();
        paramType.setField(field);
        paramType.setName(name);
        paramType.setValue(value);
        if (elementParameterTypes != null) {
            paramType.getElementValue().addAll(elementParameterTypes);
        }
        return paramType;
    }

    @Override
    public ExecutionResult execute(Item item) {
        try {
            updateJMSComponent(item);
            return ExecutionResult.SUCCESS_NO_ALERT;
        } catch (Exception e) {
            ExceptionHandler.process(e);
            return ExecutionResult.FAILURE;
        }
    }

    /**
     * 
     * @param paramName
     * @param elementParameterTypes
     * @return
     */
    protected ElementParameterType findElementParameterByName(String paramName, EList<?> elementParameterTypes) {
        for (Object obj : elementParameterTypes) {
            ElementParameterType cpType = (ElementParameterType) obj;
            if (paramName.equals(cpType.getName())) {
                return cpType;
            }
        }
        return null;
    }

    public Date getOrder() {
		GregorianCalendar gc = new GregorianCalendar(2012, 7, 10, 14, 00, 00);
        return gc.getTime();
    }

    /**
     * 
     * @param paramName
     * @param elementParameterTypes
     * @return
     */
    protected String getParameterValue(String paramName, EList<?> elementParameterTypes) {
        ElementParameterType parameterType = findElementParameterByName(paramName, elementParameterTypes);
        if (parameterType != null) {
            return parameterType.getValue();
        }
        return null;
    }

    public ProcessType getProcessType(Item item) {
        if (item instanceof ProcessItem) {
            return ((ProcessItem) item).getProcess();
        }
        return null;
    }

    /**
     * Update cJMS, add cJMSConnectionFactory.
     * 
     * @param item
     * @throws PersistenceException
     */
    private void updateJMSComponent(Item item) throws PersistenceException {

        ProcessType processType = getProcessType(item);
        if (processType == null) {
            return;
        }

        for (Object o : processType.getNode()) {
            if (o instanceof NodeType) {
                NodeType currentNode = (NodeType) o;
                if ("cJMS".equals(currentNode.getComponentName())) {

					ElementParameterType oldConnectionFactoryParam = findElementParameterByName(
							"CONNECTION_FACOTRY",
							currentNode.getElementParameter());
					if (oldConnectionFactoryParam != null) {
						String connectionFacotryId = oldConnectionFactoryParam
								.getValue();
						if (connectionFacotryId != null) {
							ElementParameterType newConnectionFactoryParam = createParamType(
									EParameterFieldType.ROUTE_COMPONENT_TYPE
											.getName(),
									"CONNECTION_FACOTRY_CONFIGURATION", "");

							ElementParameterType idParam = createParamType(
									EParameterFieldType.TECHNICAL.getName(),
									"CONNECTION_FACOTRY_CONFIGURATION:ROUTE_COMPONENT_TYPE_ID",
									connectionFacotryId.replace(
											"cJMSConnectionFactory",
											"cJMSConnectionFactory_"));

							currentNode.getElementParameter().add(
									newConnectionFactoryParam);
							currentNode.getElementParameter().add(idParam);
						}
						currentNode.getElementParameter().remove(
								oldConnectionFactoryParam);
					}
                }
            }
        }
            REPO_FACTORY.save(item, true);
    }

}
