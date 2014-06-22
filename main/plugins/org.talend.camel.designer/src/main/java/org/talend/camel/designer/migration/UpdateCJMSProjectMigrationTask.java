// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

/**
 * @author LiXiaopeng
 * 
 */
public class UpdateCJMSProjectMigrationTask extends AbstractItemMigrationTask {

    private static final ProxyRepositoryFactory REPO_FACTORY = ProxyRepositoryFactory.getInstance();

    private static final TalendFileFactory FILE_FACTORY = TalendFileFactory.eINSTANCE;

    /**
     * 
     * @param nodeType
     * @param field
     * @param name
     * @param value
     * @param eList
     */
    private void addParamTtpe(NodeType nodeType, String field, String name, String value, EList eList) {
        ElementParameterType paramType = createParamType(field, name, value, eList);
        nodeType.getElementParameter().add(paramType);

    }

    /**
     * 
     * @param currentNode
     * @return
     */
    private NodeType createConnectionFactoryNode(NodeType currentNode) {
        NodeType nodeType = FILE_FACTORY.createNodeType();
        nodeType.setSizeX(32);
        nodeType.setSizeY(32);
        nodeType.setComponentName("cJMSConnectionFactory");

        String label = getParameterValue("LABEL", currentNode.getElementParameter());
        if (label == null || label.equals("_UNIQUE_NAME_")) {
            label = getParameterValue("UNIQUE_NAME", currentNode.getElementParameter());

        }
        label += "_ConnectionFacotry";

        addParamTtpe(nodeType, "TEXT", "LABEL", label, null);
        addParamTtpe(nodeType, "TEXT", "UNIQUE_NAME", label, null);

        // Don't forget set the Connection for cJMS
        addParamTtpe(currentNode, "TEXT", "CONNECTION_FACOTRY_LABEL", label, null);
        addParamTtpe(currentNode, "TEXT", "CONNECTION_FACOTRY", label.replace("_", ""), null);

        for (Object e : currentNode.getElementParameter()) {
            ElementParameterType p = (ElementParameterType) e;
            if (!("UNIQUE_NAME".equals(p.getName()) || "LABEL".equals(p.getName()) || "TYPE".equals(p.getName()) || "DESTINATION"
                    .equals(p.getName()))) {

                addParamTtpe(nodeType, p.getField(), p.getName(), p.getValue(), p.getElementValue());
            }
        }

        return nodeType;
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

    /**
     * Get maximum Y position of the process.
     * 
     * @param processType
     * @return
     */
    private int getMaxY(ProcessType processType) {
        int max = 0;
        for (Object o : processType.getNode()) {
            if (o instanceof NodeType) {
                NodeType currentNode = (NodeType) o;
                if (currentNode.getPosY() > max) {
                    max = currentNode.getPosY();
                }
            }
        }
        return max;
    }

    public Date getOrder() {
        GregorianCalendar gc = new GregorianCalendar(2012, 2, 16, 10, 00, 00);
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
     * Compute the location of cJMSConnectionFactory Node
     * 
     * @param processType
     * @param nodes
     */
    private void locateNodes(ProcessType processType, List<NodeType> nodes) {

        int maxY = getMaxY(processType);
        int index = 1;
        for (NodeType node : nodes) {
            node.setPosX(100 * index);
            node.setPosY(maxY + 100);
            index++;
        }
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
        boolean modified = false;

        List<NodeType> nodes = new ArrayList<NodeType>();

        for (Object o : processType.getNode()) {
            if (o instanceof NodeType) {
                NodeType currentNode = (NodeType) o;
                if ("cJMS".equals(currentNode.getComponentName())) {
                    modified = true;
                    NodeType cfNode = createConnectionFactoryNode(currentNode);
                    nodes.add(cfNode);
                }
            }
        }

        if (modified) {
            locateNodes(processType, nodes);

            processType.getNode().addAll(nodes);
            REPO_FACTORY.save(item, true);
        }
    }

}
