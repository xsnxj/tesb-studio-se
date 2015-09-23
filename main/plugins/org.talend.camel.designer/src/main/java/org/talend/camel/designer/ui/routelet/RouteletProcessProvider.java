// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.ui.routelet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.camel.core.model.camelProperties.RouteletProcessItem;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.designer.ui.editor.CamelProcessEditorInput;
import org.talend.camel.designer.ui.editor.RouteProcess;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.CorePlugin;
import org.talend.core.model.components.IComponent;
import org.talend.core.model.context.ContextUtils;
import org.talend.core.model.context.JobContextManager;
import org.talend.core.model.context.JobContextParameter;
import org.talend.core.model.general.Project;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.IContext;
import org.talend.core.model.process.IContextManager;
import org.talend.core.model.process.IContextParameter;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.IExternalNode;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.IProcess2;
import org.talend.core.model.properties.ContextItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.JobletProcessItem;
import org.talend.core.model.properties.ProjectReference;
import org.talend.core.model.properties.Property;
import org.talend.core.model.relationship.RelationshipItemBuilder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.update.UpdateResult;
import org.talend.core.ui.context.view.Contexts;
import org.talend.designer.core.DesignerPlugin;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.process.AbstractProcessProvider;
import org.talend.designer.core.model.utils.emf.talendfile.ContextParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;
import org.talend.designer.core.ui.editor.connections.Connection;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.process.Process;
import org.talend.designer.core.ui.editor.properties.controllers.ComponentListController;
import org.talend.designer.core.ui.editor.properties.controllers.ConnectionListController;
import org.talend.designer.core.ui.editor.update.UpdateManagerUtils;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;

/**
 * DOC qzhang class global comment. Detailled comment
 */
public class RouteletProcessProvider extends AbstractProcessProvider {

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.designer.core.model.process.AbstractProcessProvider#
     * isExtensionProcess(org.talend.core.model.process.IProcess)
     */
    @Override
    public boolean isExtensionProcess(IProcess process) {
        if (process instanceof RouteProcess) {
            return true;
        }
        return super.isExtensionProcess(process);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.designer.core.model.process.AbstractProcessProvider# openNewProcessEditor()
     */
    @Override
    public void openNewProcessEditor(INode node) {
        IComponent component = node.getComponent();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (component instanceof RouteletComponent) {
            Property property = ((RouteletComponent) component).getProperty();
            if (property == null) {
                return;
            }
            IProxyRepositoryFactory factory = DesignerPlugin.getDefault().getProxyRepositoryFactory();
            try {
                IRepositoryViewObject selectedProcess = factory.getLastVersion(property.getId());
                // bug 6158 open the last version.
                // List<IRepositoryObject> list = factory.getAll(ERepositoryObjectType.JOBLET);
                //
                // for (IRepositoryObject process : list) {
                // if (component.getName().equals(process.getLabel())) {
                // if (process.getProperty().getItem() instanceof JobletProcessItem) {
                // selectedProcess = process;
                // break;
                // }
                // }
                // }

                if (selectedProcess != null) {
                    RouteletProcessItem processItem = (RouteletProcessItem) selectedProcess.getProperty().getItem();
                    CamelProcessEditorInput fileEditorInput = new CamelProcessEditorInput(processItem, true, true);
                    IEditorPart editorPart = page.findEditor(fileEditorInput);
                    if (editorPart == null) {
                        fileEditorInput.setRepositoryNode(null);
                        page.openEditor(fileEditorInput, CamelMultiPageTalendEditor.ID, true);
                    } else {
                        page.activate(editorPart);
                    }
                }
            } catch (PartInitException e) {
                MessageBoxExceptionHandler.process(e);
            } catch (PersistenceException e) {
                MessageBoxExceptionHandler.process(e);
            }
        }
    }

    @Override
    public void loadComponentsFromExtensionPoint() {
        if (ProjectManager.getInstance().getCurrentProject() != null) {
            RouteletComponentsUtils.loadComponentsFromRoutelets();
        }
    }

    private Process buildNewGraphicProcess(INode node) {
        // modify for feature 13361
        // open a job which with joblet component, run it, alway run the last version of the joblet not the version it
        // stand for.
        String version = "";
        IElementParameter elementParameter = node.getElementParameter(EParameterName.PROCESS_TYPE_VERSION.getName());
        if (elementParameter != null) {
            version = (String) elementParameter.getValue();
        }
        IComponent component = node.getComponent();
        if (component instanceof RouteletComponent) {
            RouteletComponent jobletComponent = (RouteletComponent) component;
            Property property = jobletComponent.getProperty();
            if (version.equals(property.getVersion())) {
                return (Process) getProcessFromJobletProcessItem((RouteletProcessItem) property.getItem());
            } else {
                Item jobletItem = getJobletItem(node, version);
                property = jobletItem.getProperty();
                return (Process) getProcessFromJobletProcessItem((RouteletProcessItem) jobletItem);
            }
        }
        return null;
    }

    /**
     * DOC qzhang Comment method "buildGraphicProcessFromProperty".
     * 
     * @param property
     * @return
     */
    private Process buildGraphicProcessFromProperty(Property property) {
        Process sProcess = null;
        sProcess = new RouteProcess(property);
        sProcess.loadXmlFile();
        return sProcess;
    }

    private Process buildGraphicProcessFromProperty(Property property, boolean needScreenshot) {
        Process sProcess = null;
        sProcess = new RouteProcess(property);
        sProcess.loadXmlFile(needScreenshot);
        if (!needScreenshot) {
            // actually if we don't need the screenshot
            // it's because we need job for generation or only or few info, so no need to keep as well the item
            // set this to null will save lots of memory when generate a job.
            sProcess.setProperty(null);
        }
        return sProcess;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.model.process.AbstractProcessProvider#canCreateNode
     * (org.talend.designer.core.ui.editor.nodes.Node)
     */
    @Override
    public boolean canCreateNode(INode node) {
        if (node.getComponent() instanceof RouteletComponent && node.getProcess() instanceof RouteProcess) {
            IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
            if (activeEditor instanceof CamelMultiPageTalendEditor) {
                if (null == ((RouteProcess)((CamelMultiPageTalendEditor) activeEditor).getProcess()).getSpringContent()) {
                    return false;
                }
                return !containCurrentRouteletNode(node.getProcess().getName(), node);
            }
        }
        return super.canCreateNode(node);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.model.process.AbstractProcessProvider#getIcons()
     */
    @Override
    public ImageDescriptor getIcons(IProcess2 process) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.designer.core.model.process.AbstractProcessProvider#setIcons(org.talend.core.model.process.IProcess,
     * org.eclipse.swt.graphics.Image)
     */
    @Override
    public void setIcons(IProcess process, ImageDescriptor image) {
    }

    /**
     * DOC qzhang Comment method "containCurrentJobletNode".
     * 
     * @param name
     */
    private boolean containCurrentRouteletNode(String curName, INode node) {
        setCanCreateNode(true);
        if (node.getComponent() instanceof RouteletComponent) {
            RouteletComponent jobletComponent = (RouteletComponent) node.getComponent();
            boolean inMemory = false;
            String name2 = jobletComponent.getName();

            RouteletProcessItem item = (RouteletProcessItem) jobletComponent.getProperty().getItem();
            Process buildNewGraphicProcess = buildNewGraphicProcess(item, false);
            boolean inProcess = containCurrentNodeFromProcess(curName, buildNewGraphicProcess);

            if (node.getProcess() instanceof RouteProcess) {
                boolean b = curName.equals(name2);
                if (b) {
                    return b;
                }
                // for process that are not saved
                String componentName = node.getComponent().getName();
                IEditorReference[] reference = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .getEditorReferences();
                List<IProcess2> list = CorePlugin.getDefault().getDesignerCoreService().getOpenedProcess(reference);
                for (IProcess2 process : list) {
                    if (process instanceof RouteProcess) {
                        if (componentName.equals(process.getName())) {
                            inMemory = containCurrentNodeFromProcess(curName, (Process) process);

                            if (inProcess == true && inMemory == false) {
                                setCanCreateNode(false);
                                setComponentProcess((Process) ((RouteletComponent) node.getComponent()).getProcess());
                                return false;
                            }
                            break;
                        }
                    }
                }
            }

            return inProcess || inMemory;
        }
        return false;
    }

    /**
     * DOC qzhang Comment method "containCurrentNodeFromProcess".
     * 
     * @param curName
     * @param buildNewGraphicProcess
     * @return
     */
    private boolean containCurrentNodeFromProcess(String curName, Process buildNewGraphicProcess) {
        List<? extends INode> graphicalNodes = buildNewGraphicProcess.getGraphicalNodes();
        for (INode node : graphicalNodes) {
            if (containCurrentRouteletNode(curName, node)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.designer.core.model.process.AbstractProcessProvider#
     * buildNewGraphicProcess(org.talend.core.model.properties.Item)
     */
    @Override
    public Process buildNewGraphicProcess(Item item) {
        if (item instanceof RouteletProcessItem) {
            return buildGraphicProcessFromProperty(item.getProperty());
        } else {
            throw new IllegalArgumentException("Illegal Argument: " + item.getClass().getName());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.model.process.AbstractProcessProvider#buildNewGraphicProcess(org.talend.core.model.
     * properties.Item, boolean)
     */
    @Override
    public Process buildNewGraphicProcess(Item node, boolean needScreenshot) {
        if (node instanceof RouteletProcessItem) {
            return buildGraphicProcessFromProperty(node.getProperty(), needScreenshot);
        } else {
            throw new IllegalArgumentException("Illegal Argument: " + node.getClass().getName());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.designer.core.model.process.AbstractProcessProvider# rebuildGraphicProcessFromNode
     * (org.talend.designer.core.ui.editor.nodes.Node, java.util.List)
     */
    @Override
    public void rebuildGraphicProcessFromNode(INode node, List<INode> graphicalNodeList) {
        if (node.getComponent() instanceof RouteletComponent) {
            Process buildNewGraphicProcess = buildNewGraphicProcess(node);
            String prefix = node.getUniqueName();

            List<INode> checkedNodeForPrefix = new ArrayList<INode>();
            // the old connection variabls map new ones (feature 2962)
            Map<String, String> connVarsMap = new HashMap<String, String>();

            List<INode> jobletNodes = (List<INode>) buildNewGraphicProcess.getGraphicalNodes();
            for (INode jobletNode : jobletNodes) {
                if (jobletNode.isSubProcessStart() && jobletNode.isActivate()) {
                    addJobletPrefix(jobletNode, prefix + '_', checkedNodeForPrefix, connVarsMap);
                }
            }
            // (feature 2962)
//            UpdateJobletVariablesUtils.updateConnVarsForJobletNodes(jobletNodes, connVarsMap);

            List<INode> copyOfJobletNodes = new ArrayList<INode>(jobletNodes);
            for (INode curNode : copyOfJobletNodes) {
                if (curNode.getComponent() instanceof RouteletComponent) {
                    rebuildGraphicProcessFromNode(curNode, jobletNodes);
                    // fix for TDI-33313
                    String uniqueNamePrefix = prefix + '_';
                    String uniqueName = curNode.getUniqueName();
                    String originalName = uniqueName.substring(uniqueNamePrefix.length(), uniqueName.length());
                    if (connVarsMap.containsKey(originalName)) {
                        connVarsMap.remove(originalName);
                    }
                }
            }

            //plugNewProcess(graphicalNodeList, node, jobletNodes, connVarsMap);
        }
    }

    /**
     * DOC nrousseau Comment method "addJobletPrefix".
     * 
     * @param node
     * @param uniqueName
     * @return
     */
    private void addJobletPrefix(INode node, String prefix, List<INode> checkedNode, Map<String, String> connVarsMap) {
        if (checkedNode.contains(node)) {
            return;
        }
        checkedNode.add(node);
        String originalNodeUniqueName = node.getUniqueName();
        node.setPropertyValue(EParameterName.UNIQUE_NAME.getName(), prefix + originalNodeUniqueName);
        node.setLabel(originalNodeUniqueName);

        ComponentListController.renameComponentUniqueName(originalNodeUniqueName, prefix + originalNodeUniqueName,
                (List<Node>) node.getProcess().getGraphicalNodes());

        // see bug 4724
        if (connVarsMap != null) {
            if (!connVarsMap.containsKey(originalNodeUniqueName)) {
                connVarsMap.put(originalNodeUniqueName, prefix + originalNodeUniqueName);
            }
        }

        for (Connection connection : (List<Connection>) node.getOutgoingConnections()) {
            // rename the connection if it's a flow connection
            if (connection.getLineStyle().hasConnectionCategory(IConnectionCategory.FLOW)) {
                String oldConnectionName = connection.getName();
                String newConnectionName = prefix + connection.getName();
                IExternalNode externalNode = node.getExternalNode();
                if (externalNode != null) {
                    // if (DEBUG_JOBLET_BUILD) {
                    // System.out.println("jobletProcess: rename output:" +
                    // oldConnectionName + " to " +
                    // newConnectionName
                    // + " for component:" + node.getUniqueName());
                    // }
                    externalNode.renameOutputConnection(oldConnectionName, newConnectionName);
                }

                externalNode = connection.getTarget().getExternalNode();
                if (externalNode != null) {
                    // if (DEBUG_JOBLET_BUILD) {
                    // System.out.println("jobletProcess: rename input:" +
                    // oldConnectionName + " to " +
                    // newConnectionName
                    // + " for component:" + prefix +
                    // connection.getTarget().getUniqueName());
                    // }
                    externalNode.renameInputConnection(oldConnectionName, newConnectionName);
                }
                ConnectionListController.renameConnection(oldConnectionName, newConnectionName, (List<INode>) node.getProcess()
                        .getGraphicalNodes());
                connection.setName(newConnectionName);

                if (connection.getMetadataTable() != null) {
//                    UpdateJobletVariablesUtils.addConnVarsMap(connection.getMetadataTable().getListColumns(), oldConnectionName,
//                            newConnectionName, connVarsMap);
                }

            } else if (connection.getLineStyle().hasConnectionCategory(IConnectionCategory.UNIQUE_NAME)) {
                connection.setUniqueName(prefix + connection.getUniqueName());
            }
            if (!connection.getTarget().isSubProcessStart()) {
                addJobletPrefix(connection.getTarget(), prefix, checkedNode, connVarsMap);
            }
        }
    }

    private void addParameterFromNode(List<ContextType> contextList, IContextManager contextManager, List<String> addedParam,
            final String defaultContext, final Map<String, Item> itemMap) {
        for (IContext context : contextManager.getListContext()) {

            ContextType type = ContextUtils.getContextTypeByName(contextList, context.getName(), defaultContext);

            if (type != null) {
                for (String name : addedParam) {
                    if (contextManager.getContext(context.getName()).getContextParameter(name) != null) {
                        continue;
                    }
                    ContextParameterType param = ContextUtils.getContextParameterTypeByName(type, name);
                    if (param != null) {
                        JobContextParameter contextParam = new JobContextParameter();

                        ContextUtils.updateParameter(param, contextParam);

                        String source = IContextParameter.BUILT_IN;

                        String repositoryContextId = param.getRepositoryContextId();
                        if (repositoryContextId != null && itemMap != null) {
                            Item item = itemMap.get(repositoryContextId);
                            source = repositoryContextId;
                            if (item instanceof ContextItem) {
                                if (item != null) {
                                    // perhaps, need upate the parameter.
                                    if (ContextUtils.updateParameterFromRepository((ContextItem) item, contextParam,
                                            context.getName())) {
                                        source = item.getProperty().getId();
                                    }
                                }
                            }

                        }

                        contextParam.setSource(source);
                        contextParam.setContext(context);

                        context.getContextParameterList().add(contextParam);
                    }
                }
            }
        }
    }

    private void changeParameterFromNode(List<ContextType> contextList, IContextManager contextManager,
            List<String> changedParam, final String defaultContext, final Map<String, Item> itemMap, final Map renameMap) {
        for (IContext context : contextManager.getListContext()) {

            ContextType type = ContextUtils.getContextTypeByName(contextList, context.getName(), defaultContext);

            if (type != null) {
                for (String name : changedParam) {
                    ContextParameterType param = ContextUtils.getContextParameterTypeByName(type, name);
                    if (param != null) {
                        for (IContextParameter paraNeedChange : context.getContextParameterList()) {
                            if (renameMap != null && renameMap.size() > 0) {
                                // change name
                                String newName = getRenamedVarName(paraNeedChange.getName(), renameMap);
                                if (newName != null && param.getName().equals(newName)
                                        && param.getRepositoryContextId().equals(paraNeedChange.getSource())) {
                                    ContextUtils.updateParameter(param, paraNeedChange);
                                }
                            } else {
                                // change other property
                                if (param.getName().equals(paraNeedChange.getName())) {
                                    ContextUtils.updateParameter(param, paraNeedChange);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void removeParameterFromNode(IContextManager contextManager, List<IContextParameter> removedParam) {
        for (IContext context : contextManager.getListContext()) {
            for (IContextParameter para : removedParam) {
                context.getContextParameterList().remove(para);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.designer.core.model.process.AbstractProcessProvider# updateJobletContext(java.util.List)
     */
    @Override
    public List<String> updateProcessContexts(IProcess process2) {
        if (process2 == null) {
            return null;
        }
        List<String> labelList = new ArrayList<String>();
        // get the id to item mapping
        Map<String, Item> itemMap = ContextUtils.getRepositoryContextItemIdMapping();

        getContextsFromProcess(process2, process2, labelList, itemMap);
        // show the node context information.
        // if (!labelList.isEmpty()) {
        // Collections.sort(labelList);
        // ShowJobletContextDialog dialog = new
        // ShowJobletContextDialog(PlatformUI.getWorkbench().
        // getActiveWorkbenchWindow()
        // .getShell(), labelList, process2.getName());
        // dialog.open();
        // }

        //
        Contexts.switchToCurContextsView();
        return labelList;
    }

    @Override
    public List<String> updateProcessContextsWithoutUI(IProcess process2) {
        if (process2 == null) {
            return null;
        }
        List<String> labelList = new ArrayList<String>();
        // get the id to item mapping
        Map<String, Item> itemMap = ContextUtils.getRepositoryContextItemIdMapping();

        getContextsFromProcess(process2, process2, labelList, itemMap);
        return labelList;
    }

    /**
     * DOC qzhang Comment method "getConextsFromProcess".
     * 
     * @param mainProcess
     * @param process22
     * @param addedParam
     * @param labelList
     */
    private void getContextsFromProcess(IProcess mainProcess, IProcess process2, List<String> labelList,
            final Map<String, Item> itemMap) {
        List<? extends INode> graphicalNodes = process2.getGraphicalNodes();
        for (INode node : graphicalNodes) {
            getContextsFromNode(mainProcess, labelList, node, process2, itemMap);
        }
    }

    /**
     * DOC qzhang Comment method "getContextsFromNode".
     * 
     * @param mainProcess
     * 
     * @param addedParam
     * @param labelList
     * @param node
     * @param process2
     */
    private void getContextsFromNode(IProcess mainProcess, List<String> labelList, INode node, IProcess process2,
            final Map<String, Item> itemMap) {
        if (node.getComponent() instanceof RouteletComponent) {
            List<String> addedNodeParam = new ArrayList<String>();
            List<String> changedNodeParam = new ArrayList<String>();
            List<IContextParameter> removedNodeParam = new ArrayList<IContextParameter>();
            RouteletComponent component = (RouteletComponent) node.getComponent();
            Property property = component.getProperty();
            RouteletProcessItem item = (RouteletProcessItem) property.getItem();
            List<ContextType> contextList = item.getProcess().getContext();
            Map namedMap = getRenameMapFromJoblet(component.getName());
            final String defaultContext = item.getProcess().getDefaultContext();
            if (contextList.size() > 0) {
                List<ContextParameterType> jobletParameterList = contextList.get(0).getContextParameter();
                IContext context = mainProcess.getContextManager().getDefaultContext();
                for (ContextParameterType param : jobletParameterList) {
                    final String paramName = param.getName();
                    if (context != null && context.getContextParameter(paramName) != null) {
                        // for the case change the exist related parameter value,prompt,etc
                        if (!context.getContextParameter(paramName).getSource().equals(IContextParameter.BUILT_IN)) {
                            if (needToChangeParameterFromNode(context, param)) {
                                String repositoryContextId = param.getRepositoryContextId();
                                if (repositoryContextId == null) {
                                    repositoryContextId = property.getId();
                                    param.setRepositoryContextId(repositoryContextId);
                                    itemMap.put(property.getId(), item);
                                }
                                labelList.add(param.getName() + " (existed) from " + node.getUniqueName());
                                changedNodeParam.add(param.getName());
                            }
                        }
                    } else {
                        boolean isAddNodeParam = true;

                        String source = IContextParameter.BUILT_IN;
                        String repositoryContextId = param.getRepositoryContextId();
                        if (repositoryContextId != null && itemMap != null) {
                            Item contextItem = itemMap.get(repositoryContextId);
                            if (contextItem != null) {
                                source = contextItem.getProperty().getLabel();
                            } else {
                                // in case exist other subjoblet's context parameter in the jobletNode
                                itemMap.put(repositoryContextId, item);
                                source = repositoryContextId;
                            }
                        } else {
                            // need to support propagate the build-in parameter in joblet to the main process as
                            // repository mode after drag the joblet to the job
                            repositoryContextId = property.getId();
                            param.setRepositoryContextId(repositoryContextId);
                            itemMap.put(repositoryContextId, item);
                            source = repositoryContextId;
                        }
                        String label = param.getName() + " (" + source + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                        if (!labelList.contains(label)) {
                            labelList.add(label);
                        }
                        if (context != null) {
                            for (IContextParameter contextParaInProcess : context.getContextParameterList()) {
                                if (contextParaInProcess.getSource().equals(repositoryContextId)) {
                                    String newName = getRenamedVarName(contextParaInProcess.getName(), namedMap);
                                    if (newName != null && newName.equals(paramName)) {
                                        isAddNodeParam = false;
                                    }
                                }
                            }
                        }
                        if (isAddNodeParam) {
                            addedNodeParam.add(paramName);
                        } else {
                            changedNodeParam.add(paramName);
                        }
                    }
                }
                if (!addedNodeParam.isEmpty()) {
                    addParameterFromNode(contextList, mainProcess.getContextManager(), addedNodeParam, defaultContext, itemMap);
                }
                if (!changedNodeParam.isEmpty()) {
                    // support change the parameter name in joblet and propagate to job
                    changeParameterFromNode(contextList, mainProcess.getContextManager(), changedNodeParam, defaultContext,
                            itemMap, namedMap);
                }
                if (!removedNodeParam.isEmpty()) {
                    removeParameterFromNode(mainProcess.getContextManager(), removedNodeParam);
                }
                getContextsFromProcess(mainProcess, buildGraphicProcessFromProperty(property, false), labelList, itemMap);
            }
        }
    }

    private boolean needToChangeParameterFromNode(IContext mainProcessContext, ContextParameterType jobletPara) {
        IContextParameter paraInJob = mainProcessContext.getContextParameter(jobletPara.getName());
        boolean isSameRepositoryContextParam = jobletPara.getRepositoryContextId() != null
                && jobletPara.getRepositoryContextId().equals(paraInJob.getSource());
        if (isSameRepositoryContextParam) {
            return false;
        }
        if (!paraInJob.getValue().equals(jobletPara.getValue()) || !paraInJob.getPrompt().equals(jobletPara.getPrompt())
                || !paraInJob.isPromptNeeded() == (jobletPara.isPromptNeeded())) {
            return true;
        }
        return false;
    }

    private Map getRenameMapFromJoblet(String jobletComponentName) {
        List<IProcess2> list = UpdateManagerUtils.getOpenedProcess();
        for (IProcess2 process : list) {
            if (process instanceof RouteProcess) {
                if (jobletComponentName.equals(process.getName())) {
                    final JobContextManager manager = (JobContextManager) process.getContextManager();
                    return manager.getNameMap();
                }
            }
        }
        return Collections.emptyMap();
    }

    private static String getRenamedVarName(final String varName, Map<String, String> renamedMap) {
        if (varName == null || renamedMap == null) {
            return null;
        }
        for (String newName : renamedMap.keySet()) {
            if (renamedMap.get(newName).equals(varName) && !newName.equals(varName)) {
                return newName;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.model.process.AbstractProcessProvider#addJobletEntry
     * (org.eclipse.gef.palette.PaletteRoot)
     */
    @Override
    public List<PaletteEntry> addJobletEntry() {
        return Collections.emptyList();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.designer.core.model.process.AbstractProcessProvider# isExtensionComponent()
     */
    @Override
    public boolean isExtensionComponent(INode node) {
        if (node.getComponent() instanceof RouteletComponent) {
            return true;
        }
        return super.isExtensionComponent(node);
    }

    /**
     * check the joblet schema changed. (feature 3232)
     */
    @Override
    public List<UpdateResult> checkJobletNodeSchema(IProcess process) {
        return Collections.emptyList();
    }

    @Override
    public boolean hasJobletComponent(IProcess curProcess) {
        if (curProcess != null) {
            for (INode node : curProcess.getGraphicalNodes()) {
                if (node.getComponent() instanceof RouteletComponent) {
                    return true;
                }
            }
        }
        return false;
    }

    public IProcess getProcessFromJobletProcessItem(RouteletProcessItem jobletProcessItem) {
        Process process = new RouteProcess(jobletProcessItem.getProperty());
        process.loadXmlFile();
        return process;
    }

    @Override
    public Item getJobletItem(INode node) {
        return getJobletItem(node, null);
    }

    @Override
    public Item getJobletItem(INode node, String version) {
        if (isExtensionComponent(node)) {
            RouteletComponent component = (RouteletComponent) node.getComponent();
            IRepositoryViewObject specificVersion = null;
            // get the latest joblet item.
            if (version == null || version.equals(RelationshipItemBuilder.LATEST_VERSION)) {
                specificVersion = getLastVersion(ProjectManager.getInstance().getCurrentProject(), component.getJobletId());
            } else {
                specificVersion = getSpecificVersion(ProjectManager.getInstance().getCurrentProject(), component.getJobletId(),
                        version);
            }
            if (specificVersion != null) {
                return specificVersion.getProperty().getItem();
            } else {
                return component.getProperty().getItem();
            }
        }
        return null;
    }

    private IRepositoryViewObject getSpecificVersion(Project project, String id, String version) {
        IProxyRepositoryFactory factory = CorePlugin.getDefault().getRepositoryService().getProxyRepositoryFactory();
        try {
            IRepositoryViewObject specificVersion = factory.getSpecificVersion(project, id, version, false);
            if (specificVersion != null) {
                return specificVersion;
            }

            for (ProjectReference refProject : (List<ProjectReference>) project.getEmfProject().getReferencedProjects()) {
                org.talend.core.model.properties.Project p = refProject.getReferencedProject();
                if (p != null) {
                    specificVersion = getSpecificVersion(new Project(p), id, version);
                }
                if (specificVersion != null) {
                    return specificVersion;
                }
            }

        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
        return null;
    }

    private IRepositoryViewObject getLastVersion(Project project, String id) {
        IProxyRepositoryFactory factory = CorePlugin.getDefault().getRepositoryService().getProxyRepositoryFactory();
        try {
            IRepositoryViewObject lastVersion = factory.getLastVersion(project, id);
            if (lastVersion != null) {
                return lastVersion;
            }

            for (ProjectReference refProject : (List<ProjectReference>) project.getEmfProject().getReferencedProjects()) {
                org.talend.core.model.properties.Project p = refProject.getReferencedProject();
                if (p != null) {
                    lastVersion = getLastVersion(new Project(p), id);
                }
                if (lastVersion != null) {
                    return lastVersion;
                }
            }

        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
        return null;
    }

    @Override
    public boolean isNeedForceRebuild(IProcess2 process) {
        for (INode node : process.getGraphicalNodes()) {
            if (node.getComponent() instanceof RouteletComponent) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void beforeRunJobInGUI(IProcess2 process) {
        // nothing
    }

    @Override
    public IProcess getProcessFromJobletProcessItem(JobletProcessItem jobletProcessItem) {
        // TODO Auto-generated method stub
        return null;
    }

}
