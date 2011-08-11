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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.draw2d.geometry.Point;
import org.talend.core.model.components.ComponentUtilities;
import org.talend.core.model.process.EConnectionType;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.camel.spring.core.ISpringParserListener;
import org.talend.designer.camel.spring.ui.handlers.ConnectionParameterHandler;
import org.talend.designer.camel.spring.ui.handlers.IParameterHandler;
import org.talend.designer.camel.spring.ui.handlers.ParameterHandlerFactory;
import org.talend.designer.camel.spring.ui.layout.ILayoutManager;
import org.talend.designer.camel.spring.ui.layout.RelativeLayoutManager;
import org.talend.designer.camel.spring.ui.utils.RouteMapping;
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

    private ILayoutManager layoutManager;

    private Map<Integer, EConnectionType> connectionStyleMap;

    private Map<String, IParameterHandler> paramHandlers;
    
    private Map<String, ComponentNode> nodes;
    
    private int routingId;

    private static final Log log = LogFactory.getLog(SpringParserListener.class);

    public SpringParserListener(ProcessType processType) {
        this.processType = processType;
        layoutManager = new RelativeLayoutManager();
        connectionStyleMap = RouteMapping.getConnectionMapping();
        paramHandlers = ParameterHandlerFactory.INSTANCE.getHandlers();
        nodes = new HashMap<String, ComponentNode>();
        routingId = 0;
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
                
                if(connParameters != null){
                    ConnectionParameterHandler.addConnectionParameters(connectionType, connParameters);
                }
                processType.getConnection().add(connectionType);

            }
        }

    }

    /**
     * 
     * DOC LiXP Comment method "getUniqueName".
     * @param sourceId
     * @return
     */
    private String getUniqueName(String sourceId) {
        ComponentNode node = nodes.get(sourceId);
        if(node == null){
            return null;
        }
        return ComponentUtilities.getNodeUniqueName(node.getNodeType());
//        for(Object obj: node.getNodeType().getElementParameter()){
//            ElementParameterType param = (ElementParameterType) obj;
//            if(param.getName().equals("UNIQUE_NAME")){
//                return param.getValue();
//            }
//        }
//        return null;
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

        String name = parameters.get(ICamelSpringConstants.UNIQUE_NAME_ID);
		Point position = layoutManager.getNextPosition(name, sourceId);

        nodeType.setPosX(position.x);
        nodeType.setPosY(position.y);
        nodeType.setSizeX(32);
        nodeType.setSizeY(32);

        ComponentNode componentNode = new ComponentNode(name);
        componentNode.setNodeType(nodeType);
        componentNode.setRoutingId(routingId);
        
        if(sourceId == null){
            routingId ++;
        }else{
            ComponentNode parent = nodes.get(sourceId);
            if(parent != null){
                componentNode.setParent(componentNode);
                parent.getChildren().add(componentNode);
            }
        }
        
        nodes.put(name, componentNode);
        
        return nodeType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.camel.spring.core.ISpringParserListener#postProcess()
     */
    public void postProcess() {
        layoutManager.stopLayout();
//        LayoutUtils.simpleLayout(nodes.values());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.camel.spring.core.ISpringParserListener#preProcess()
     */
    public void preProcess() {
        fileFact = TalendFileFactory.eINSTANCE;
        layoutManager.startLayout();
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
        log.info("[" + RouteMapping.COMPOMENT_NAMES[componentType] + "] " + parameters + " connection: " +connParameters);
        
        NodeType nodeType = createNode(componentType, parameters, sourceId);
        addElementParameters(nodeType, componentType, parameters);
        addPossibleConnections(nodeType, connectionId, sourceId, connParameters);

        processType.getNode().add(nodeType);
    }

}
