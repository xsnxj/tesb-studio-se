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
package org.talend.designer.camel.spring.ui.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.talend.core.model.components.ComponentUtilities;
import org.talend.core.model.process.EConnectionType;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.camel.spring.core.ISpringParserListener;
import org.talend.designer.camel.spring.ui.RouteMapping;
import org.talend.designer.camel.spring.ui.imports.ConnectionParameterHandler;
import org.talend.designer.camel.spring.ui.imports.IParameterHandler;
import org.talend.designer.camel.spring.ui.imports.ParameterHandlerFactory;
import org.talend.designer.camel.spring.ui.layout.Routing;
import org.talend.designer.camel.spring.ui.layout.RoutingLayoutManager;
import org.talend.designer.camel.spring.ui.layout.RoutingNode;
import org.talend.designer.camel.spring.ui.utils.ParameterValueUtils;
import org.talend.designer.core.model.utils.emf.talendfile.ConnectionType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class SpringParserListener implements ISpringParserListener {

    private ProcessType processType;

    private TalendFileFactory fileFact;

    private Map<Integer, EConnectionType> connectionStyleMap;

    private Map<String, IParameterHandler> paramHandlers;

    private List<Routing> routings;

    private Map<String, RoutingNode> routingNodes;

    private RoutingLayoutManager layoutManager;

    private static final Log log = LogFactory.getLog(SpringParserListener.class);

    public SpringParserListener(ProcessType processType) {
        this.processType = processType;
        this.connectionStyleMap = RouteMapping.getConnectionMapping();
        this.paramHandlers = ParameterHandlerFactory.INSTANCE.getHandlers();
        this.routings = new ArrayList<Routing>();
        this.routingNodes = new HashMap<String, RoutingNode>();
        this.fileFact = TalendFileFactory.eINSTANCE;
        this.layoutManager = new RoutingLayoutManager();
    }

    /**
     * 
     * Add element parameters.
     * 
     * @param nodeType
     * @param componentType
     * @param parameters
     */
    private void addElementParameters(NodeType nodeType, int componentType, Map<String, String> parameters) {
        String componentName = RouteMapping.COMPOMENT_NAMES[componentType];
        String uniqueName = ComponentUtilities.generateUniqueNodeName(componentName, processType);

        IParameterHandler handler = paramHandlers.get(componentName);
        if (handler == null) {
            throw new IllegalArgumentException("Component " + componentName + " is not supported.");
        }

        handler.handle(nodeType, uniqueName, parameters);
        ComponentUtilities.setNodeUniqueName(nodeType, uniqueName);
        nodeType.setComponentName(componentName);
    }

    /**
     * 
     * Add possible connections to ProcessItem.
     * 
     * @param nodeType
     * @param connectionId
     * @param sourceId
     * @param connParameters
     */
    private void addPossibleConnections(NodeType nodeType, int connectionId, String sourceId, Map<String, String> connParameters) {
        if (sourceId != null) {// has a route connection
            String sourceNodeName = getUniqueName(sourceId);
            if (sourceNodeName != null) {
                EConnectionType eConnectionType = connectionStyleMap.get(connectionId);
                ConnectionType connectionType = fileFact.createConnectionType();
                connectionType.setConnectorName(eConnectionType.getName());
                connectionType.setSource(sourceNodeName);
                connectionType.setTarget(ComponentUtilities.getNodeUniqueName(nodeType));
                connectionType.setLabel(eConnectionType.getName());
                connectionType.setLineStyle(eConnectionType.getId());

                if (connParameters != null) {
                    ConnectionParameterHandler.addConnectionParameters(connectionType, connParameters);
                }
                processType.getConnection().add(connectionType);

            }
        }

    }

    /**
     * 
     * DOC LiXP Comment method "getUniqueName".
     * 
     * @param sourceId
     * @return
     */
    private String getUniqueName(String sourceId) {
        RoutingNode node = routingNodes.get(sourceId);
        if (node == null) {
            return null;
        }
        return ComponentUtilities.getNodeUniqueName(node.getNodeType());
        // for(Object obj: node.getNodeType().getElementParameter()){
        // ElementParameterType param = (ElementParameterType) obj;
        // if(param.getName().equals("UNIQUE_NAME")){
        // return param.getValue();
        // }
        // }
        // return null;
    }

    /**
     * 
     * DOC LiXP Comment method "createNode".
     * 
     * @param componentType
     * @param parameters
     * @return
     */
    private NodeType createNode(int componentType, Map<String, String> parameters, String sourceId) {
        NodeType nodeType = fileFact.createNodeType();
        nodeType.setSizeX(32);
        nodeType.setSizeY(32);

        String name = parameters.get(ICamelSpringConstants.UNIQUE_NAME_ID);
        RoutingNode routingNode = new RoutingNode(name);
        routingNode.setNodeType(nodeType);
        routingNodes.put(name, routingNode);

        if (sourceId == null) {
            Routing routing = new Routing();
            routing.setRoutingId(routings.size());
            routing.setFromNode(routingNode);
            routingNode.setRouting(routing);
            routings.add(routing);
        } else {
            RoutingNode parant = routingNodes.get(sourceId);
            if (parant != null) {
                routingNode.setParant(parant);
                parant.getChildren().add(routingNode);
            }
        }

        return nodeType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.camel.spring.core.ISpringParserListener#postProcess()
     */
    public void postProcess() {
        layoutManager.layout(routings);
        routingNodes.clear();
        routings.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.camel.spring.core.ISpringParserListener#preProcess()
     */
    public void preProcess() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.camel.spring.core.ISpringParserListener#process(int, java.util.Map, int,
     * java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public void process(int componentType, Map<String, String> parameters, int connectionId, String sourceId,
            Map<String, String> connParameters) {

        addQuotesToParams(parameters);
        addQuotesToParams(connParameters);

        log.info("[" + RouteMapping.COMPOMENT_NAMES[componentType] + "] " + parameters + " | Connection ["
                + connectionStyleMap.get(connectionId) + "] " + connParameters + " Source " + sourceId);

        NodeType nodeType = createNode(componentType, parameters, sourceId);
        addElementParameters(nodeType, componentType, parameters);
        addPossibleConnections(nodeType, connectionId, sourceId, connParameters);

        processType.getNode().add(nodeType);
    }

    /**
     * Add quotes to parameter values
     */
    private void addQuotesToParams(Map<String, String> parameters) {
        if (parameters == null) {
            return;
        }
        for (Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            if (key.equals(ICamelSpringConstants.UNIQUE_NAME_ID)) {
                continue;
            }
            String value = entry.getValue();
            if (value != null) {
                value = ParameterValueUtils.quotes(value);
                parameters.put(entry.getKey(), value);
            }
        }
    }

}
