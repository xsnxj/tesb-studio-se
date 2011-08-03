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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.designer.ui.editor.CamelProcessEditorInput;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.model.components.ComponentUtilities;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.camel.spring.core.ISpringParserListener;
import org.talend.designer.camel.spring.ui.handlers.IParameterHandler;
import org.talend.designer.camel.spring.ui.handlers.ParameterHandlerFactory;
import org.talend.designer.camel.spring.ui.layout.RelativeLayoutManager;
import org.talend.designer.camel.spring.ui.utils.RouteMapping;
import org.talend.designer.core.model.utils.emf.talendfile.ConnectionType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.ui.views.IRepositoryView;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class SpringParserListener implements ISpringParserListener {

    private CamelProcessItem camelProcessItem;

    private IRepositoryView view;

    private ProcessType processType;

    private TalendFileFactory fileFact;

    private RelativeLayoutManager layoutManager;

    private Map<Integer, EConnectionType> connectionStyleMap;

    private Map<String, String> nodeCache;

    private ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

    private Map<String, IParameterHandler> paramHandlers;

    public SpringParserListener(CamelProcessItem camelProcessItem, IRepositoryView view) {
        this.camelProcessItem = camelProcessItem;
        this.view = view;
        layoutManager = RelativeLayoutManager.getInstance();
        connectionStyleMap = RouteMapping.getConnectionMapping();
        nodeCache = new HashMap<String, String>();
        paramHandlers = ParameterHandlerFactory.INSTANCE.getHandlers();
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
        String uniqueId = parameters.get(ICamelSpringConstants.UNIQUE_NAME_ID);
        String uniqueName = ComponentUtilities.generateUniqueNodeName(componentName, processType);

        IParameterHandler handler = paramHandlers.get(componentName);
        if (handler == null) {
            throw new IllegalArgumentException("Component " + componentName + " is not supported.");
        }

        handler.handle(nodeType, uniqueName, parameters);
        ComponentUtilities.setNodeUniqueName(nodeType, uniqueName);
        nodeType.setComponentName(componentName);
        nodeCache.put(uniqueId, uniqueName);
    }

    /**
     * 
     * Add possible connections to ProcessItem.
     * 
     * @param nodeType
     * @param connectionId
     * @param sourceId
     */
    @SuppressWarnings("unchecked")
    private void addPossibleConnections(NodeType nodeType, int connectionId, String sourceId) {
        if (sourceId != null) {// has a route connection
            String sourceNodeName = nodeCache.get(sourceId);
            if (sourceNodeName != null) {
                EConnectionType eConnectionType = connectionStyleMap.get(connectionId);
                ConnectionType connectionType = fileFact.createConnectionType();
                connectionType.setConnectorName(eConnectionType.getName());
                connectionType.setSource(sourceNodeName);
                connectionType.setTarget(ComponentUtilities.getNodeUniqueName(nodeType));
                connectionType.setLabel(eConnectionType.getName());
                connectionType.setLineStyle(eConnectionType.getId());
                processType.getConnection().add(connectionType);

            }
        }

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

        Point position = layoutManager.getNextPosition(parameters.get(ICamelSpringConstants.UNIQUE_NAME_ID), sourceId);

        nodeType.setPosX(position.x);
        nodeType.setPosY(position.y);
        nodeType.setSizeX(32);
        nodeType.setSizeY(32);

        return nodeType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.camel.spring.core.ISpringParserListener#postProcess()
     */
    public void postProcess() {

        layoutManager.stopLayout();

        CamelProcessEditorInput fileEditorInput;
        try {

            factory.save(camelProcessItem, true);

            // Set readonly to false since created job will always be editable.
            fileEditorInput = new CamelProcessEditorInput(camelProcessItem, true, true, false);

            fileEditorInput.setView(view);
            IRepositoryNode repositoryNode = RepositoryNodeUtilities.getRepositoryNode(fileEditorInput.getItem().getProperty()
                    .getId(), false);
            fileEditorInput.setRepositoryNode(repositoryNode);

            IWorkbenchPage page = view.getViewSite().getPage();
            page.openEditor(fileEditorInput, CamelMultiPageTalendEditor.ID, true);
            // // use project setting true
            // ProjectSettingManager.defaultUseProjectSetting(fileEditorInput.getLoadedProcess());
        } catch (PartInitException e) {
            ExceptionHandler.process(e);
        } catch (PersistenceException e) {
            MessageBoxExceptionHandler.process(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.camel.spring.core.ISpringParserListener#preProcess()
     */
    public void preProcess() {

        if (camelProcessItem == null) {
            throw new IllegalArgumentException("CamelProcessItem can not be null");
        }

        fileFact = TalendFileFactory.eINSTANCE;
        processType = camelProcessItem.getProcess();

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

        NodeType nodeType = createNode(componentType, parameters, sourceId);

        addElementParameters(nodeType, componentType, parameters);

        addPossibleConnections(nodeType, connectionId, sourceId);

        processType.getNode().add(nodeType);
    }

}
