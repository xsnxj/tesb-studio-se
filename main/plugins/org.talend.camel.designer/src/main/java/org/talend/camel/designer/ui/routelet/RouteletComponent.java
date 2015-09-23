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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.talend.camel.designer.CamelDesignerPlugin;
import org.talend.camel.designer.ui.editor.RouteProcess;
import org.talend.commons.CommonsPlugin;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.CorePlugin;
import org.talend.core.model.components.ComponentCategory;
import org.talend.core.model.components.EComponentType;
import org.talend.core.model.components.IComponent;
import org.talend.core.model.components.IMultipleComponentManager;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.general.Project;
import org.talend.core.model.process.EComponentCategory;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.INodeConnector;
import org.talend.core.model.process.INodeReturn;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.IProcess2;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.relationship.RelationshipItemBuilder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.temp.ECodePart;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.components.ElementParameter;
import org.talend.designer.core.model.components.NodeConnector;
import org.talend.designer.core.model.components.NodeReturn;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;

public class RouteletComponent implements IComponent {

    private boolean isUseLookUp, isUseMerge;

    private ImageDescriptor icon32;

    private ImageDescriptor icon24;

    private ImageDescriptor icon16;

    private IProcess2 routeletProcess;

    private Date lastUpdated;

    private String paletteType;

    private String routeletId;

    private String routeletVersion;

    private String routeletProcessName;

    private String routeletDescription;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#createConnectors()
     */
    @Override
    public List<? extends INodeConnector> createConnectors(INode node) {
        List<INodeConnector> listConnector = new ArrayList<INodeConnector>();
        INodeConnector nodeConnector;
        int nbInput = 0;

        int jobletNbInput = 0;

        for (int i = 0; i < EConnectionType.values().length; i++) {
            EConnectionType currentType = EConnectionType.values()[i];

            if ((currentType == EConnectionType.FLOW_REF) || (currentType == EConnectionType.FLOW_MERGE)) {
                continue;
            }
            boolean exists = false;
            for (INodeConnector curNodeConn : listConnector) {
                if (curNodeConn.getDefaultConnectionType().equals(currentType)) {
                    exists = true;
                }
            }
            if (!exists) { // will add by default all connectors not defined in
                // the xml files
                nodeConnector = new NodeConnector(node);
                nodeConnector.setDefaultConnectionType(currentType);
                nodeConnector.setName(currentType.getName());
                nodeConnector.setBaseSchema(currentType.getName());
                nodeConnector.addConnectionProperty(currentType, currentType.getRGB(), currentType.getDefaultLineStyle());
                nodeConnector.setLinkName(currentType.getDefaultLinkName());
                nodeConnector.setMenuName(currentType.getDefaultMenuName());
                int allowLinkNumber = currentType == EConnectionType.ON_SUBJOB_OK
                        || currentType == EConnectionType.ON_SUBJOB_ERROR || currentType == EConnectionType.ON_COMPONENT_OK
                        || currentType == EConnectionType.ON_COMPONENT_ERROR || currentType == EConnectionType.RUN_IF ? 1 : 0;

                // If joblet include input nodes, then only allow main flow connection, else allow all orchestration
                // connections, see feature 5166
                nodeConnector.setMaxLinkInput(jobletNbInput > 0 ? 0 : allowLinkNumber);
                nodeConnector.setMinLinkInput(0);

                nodeConnector.setMaxLinkOutput(allowLinkNumber);
                nodeConnector.setMinLinkOutput(0);
                if (currentType == EConnectionType.FLOW_MAIN) {
                    nodeConnector.addConnectionProperty(EConnectionType.FLOW_REF, EConnectionType.FLOW_REF.getRGB(),
                            EConnectionType.FLOW_REF.getDefaultLineStyle());
                    nodeConnector.addConnectionProperty(EConnectionType.FLOW_MERGE, EConnectionType.FLOW_MERGE.getRGB(),
                            EConnectionType.FLOW_MERGE.getDefaultLineStyle());
                }
                listConnector.add(nodeConnector);
            }
        }

        INodeConnector mainConnector = null;
        for (INodeConnector connector : listConnector) {
            if (connector.getDefaultConnectionType().equals(EConnectionType.FLOW_MAIN)) {
                mainConnector = connector;
            }
        }

        mainConnector.setMaxLinkInput(nbInput);
        mainConnector.setMinLinkInput(nbInput);
        mainConnector.setMaxLinkOutput(0);
        mainConnector.setMinLinkOutput(0);

        return listConnector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#createElementParameters(org.talend.core.model.process.INode)
     */
    @Override
    public List<? extends IElementParameter> createElementParameters(INode node) {
        List<IElementParameter> listParam = new ArrayList<IElementParameter>();
        ElementParameter param = new ElementParameter(node);
        param.setName(EParameterName.UNIQUE_NAME.getName());
        param.setValue(""); //$NON-NLS-1$
        param.setDisplayName(EParameterName.UNIQUE_NAME.getDisplayName());
        param.setFieldType(EParameterFieldType.TEXT);
        param.setCategory(EComponentCategory.MAIN);
        param.setNumRow(1);
        param.setReadOnly(true);
        param.setShow(true);
        listParam.add(param);

        param = new ElementParameter(node);
        param.setName(EParameterName.FAMILY.getName());
        param.setValue(getOriginalFamilyName());
        param.setDisplayName(EParameterName.FAMILY.getDisplayName());
        param.setFieldType(EParameterFieldType.TEXT);
        param.setCategory(EComponentCategory.MAIN);
        param.setNumRow(3);
        param.setReadOnly(true);
        param.setRequired(false);
        param.setShow(true);
        listParam.add(param);

        param = new ElementParameter(node);
        param.setName(EParameterName.ACTIVATE.getName());
        param.setValue(new Boolean(true));
        param.setDisplayName(EParameterName.ACTIVATE.getDisplayName());
        param.setFieldType(EParameterFieldType.CHECK);
        param.setCategory(EComponentCategory.MAIN);
        param.setNumRow(5);
        param.setReadOnly(false);
        param.setRequired(false);
        param.setShow(true);
        listParam.add(param);

        param = new ElementParameter(node);
        param.setName(EParameterName.DUMMY.getName());
        param.setValue(Boolean.FALSE);
        param.setDisplayName(EParameterName.DUMMY.getDisplayName());
        param.setFieldType(EParameterFieldType.CHECK);
        param.setCategory(EComponentCategory.MAIN);
        param.setNumRow(5);
        param.setReadOnly(false);
        param.setRequired(false);
        param.setShow(false);
        listParam.add(param);

        param = new ElementParameter(node);
        param.setName(EParameterName.STARTABLE.getName());
        Boolean startable = Boolean.TRUE;
        IElementParameter elementParameter = getProcess().getElementParameter(EParameterName.STARTABLE.getName());
        if (elementParameter != null) {
            startable = (Boolean) elementParameter.getValue();
        }

        param.setValue(startable);
        param.setDisplayName(EParameterName.STARTABLE.getDisplayName());
        param.setFieldType(EParameterFieldType.CHECK);
        param.setCategory(EComponentCategory.MAIN);
        param.setNumRow(1);
        param.setReadOnly(true);
        param.setShow(false);
        listParam.add(param);

        param = new ElementParameter(node);
        param.setName(EParameterName.START.getName());
        param.setValue(Boolean.FALSE);
        param.setDisplayName(EParameterName.START.getDisplayName());
        param.setFieldType(EParameterFieldType.CHECK);
        param.setCategory(EComponentCategory.MAIN);
        param.setNumRow(1);
        param.setReadOnly(true);
        param.setShow(false);
        listParam.add(param);

        param = new ElementParameter(node);
        param.setName("UPDATE_COMPONENTS"); //$NON-NLS-1$
        param.setValue(Boolean.TRUE);
        param.setDisplayName("UPDATE_COMPONENTS"); //$NON-NLS-1$
        param.setFieldType(EParameterFieldType.CHECK);
        param.setCategory(EComponentCategory.BASIC);
        param.setNumRow(5);
        param.setReadOnly(true);
        param.setShow(false);
        listParam.add(param);

        param = new ElementParameter(node);
        param.setName(EParameterName.SUBTREE_START.getName());
        param.setValue(new Boolean(startable));
        param.setDisplayName(EParameterName.SUBTREE_START.getDisplayName());
        param.setFieldType(EParameterFieldType.CHECK);
        param.setCategory(EComponentCategory.TECHNICAL);
        param.setNumRow(5);
        param.setReadOnly(true);
        param.setRequired(false);
        param.setShow(false);
        listParam.add(param);

        param = new ElementParameter(node);
        param.setName(EParameterName.END_OF_FLOW.getName());
        param.setValue(new Boolean(startable));
        param.setDisplayName(EParameterName.END_OF_FLOW.getDisplayName());
        param.setFieldType(EParameterFieldType.CHECK);
        param.setCategory(EComponentCategory.TECHNICAL);
        param.setNumRow(5);
        param.setReadOnly(true);
        param.setRequired(false);
        param.setShow(false);
        listParam.add(param);

        // hywang add for feature 6549
        List<String> allVersionArray = new ArrayList<String>();
        try {
            List<IRepositoryViewObject> allVersion = ProxyRepositoryFactory.getInstance().getAllVersion(this.routeletId);

            if (allVersion != null) {
                for (IRepositoryViewObject obj : allVersion) {
                    String version = obj.getVersion();
                    allVersionArray.add(version);
                }
            }

            Collections.sort(allVersionArray);
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
        if (allVersionArray.size() > 0) {
            allVersionArray.add(0, RelationshipItemBuilder.LATEST_VERSION);
        } else {
            allVersionArray.add(RelationshipItemBuilder.LATEST_VERSION);
        }
        String[] allVersions = allVersionArray.toArray(new String[0]);

        param = new ElementParameter(node);
        param.setName(EParameterName.PROCESS_TYPE_VERSION.getName());
        param.setDisplayName(EParameterName.PROCESS_TYPE_VERSION.getDisplayName());
        param.setFieldType(EParameterFieldType.CLOSED_LIST);
        param.setRequired(true);
        param.setCategory(EComponentCategory.BASIC);
        param.setValue(allVersions[0]); // default value of the closedlist is "Latest"
        param.setListItemsDisplayCodeName(allVersions);
        param.setListItemsDisplayName(allVersions);
        param.setListItemsValue(allVersions);
        param.setNumRow(1);
        param.setShow(true);
        listParam.add(param);

        // config the view tabs of component setting view.
        param = new ElementParameter(node);
        param.setName(EParameterName.LABEL.getName());
        param.setDisplayName(EParameterName.LABEL.getDisplayName());
        param.setFieldType(EParameterFieldType.TEXT);
        param.setCategory(EComponentCategory.VIEW);
        param.setNumRow(1);
        param.setReadOnly(false);
        param.setRequired(false);
        param.setShow(true);
        listParam.add(param);

        param = new ElementParameter(node);
        param.setName(EParameterName.HINT.getName());
        param.setDisplayName(EParameterName.HINT.getDisplayName());
        param.setFieldType(EParameterFieldType.TEXT);
        param.setCategory(EComponentCategory.VIEW);
        param.setNumRow(2);
        param.setReadOnly(false);
        param.setRequired(false);
        param.setShow(true);
        listParam.add(param);

        param = new ElementParameter(node);
        param.setName(EParameterName.CONNECTION_FORMAT.getName());
        param.setDisplayName(EParameterName.CONNECTION_FORMAT.getDisplayName());
        param.setFieldType(EParameterFieldType.TEXT);
        param.setCategory(EComponentCategory.VIEW);
        param.setNumRow(3);
        param.setReadOnly(false);
        param.setRequired(false);
        param.setShow(true);
        listParam.add(param);

        param = new ElementParameter(node);
        param.setName(EParameterName.INFORMATION.getName());
        param.setValue(new Boolean(false));
        param.setDisplayName(EParameterName.INFORMATION.getDisplayName());
        param.setFieldType(EParameterFieldType.CHECK);
        param.setCategory(EComponentCategory.DOC);
        param.setNumRow(1);
        param.setReadOnly(false);
        param.setRequired(false);
        param.setShow(true);
        listParam.add(param);

        // config the documentation tab.
        param = new ElementParameter(node);
        param.setName(EParameterName.COMMENT.getName());
        param.setValue(""); //$NON-NLS-1$
        param.setDisplayName(EParameterName.COMMENT.getDisplayName());
        param.setFieldType(EParameterFieldType.MEMO);
        param.setNbLines(10);
        param.setCategory(EComponentCategory.DOC);
        param.setNumRow(2);
        param.setReadOnly(false);
        param.setRequired(false);
        param.setShow(true);
        listParam.add(param);

        return listParam;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#createReturns()
     */
    @Override
    public List<? extends INodeReturn> createReturns() {
        return new ArrayList<NodeReturn>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getAvailableCodeParts()
     */
    @Override
    public List<ECodePart> getAvailableCodeParts() {
        return new ArrayList<ECodePart>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getOriginalFamilyName()
     */
    @Override
    public String getOriginalFamilyName() {
        return "Joblets";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getTranslatedFamilyName()
     */
    @Override
    public String getTranslatedFamilyName() {
        return getOriginalFamilyName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getIcon16()
     */
    @Override
    public ImageDescriptor getIcon16() {
        return icon16;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getIcon24()
     */
    @Override
    public ImageDescriptor getIcon24() {
        return icon24;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getIcon32()
     */
    @Override
    public ImageDescriptor getIcon32() {
        return icon32;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getLongName()
     */
    @Override
    public String getLongName() {
//        switch (this.jobletNodeType) {
//        case ELEMENT:
//            return 
//        default:
//            return jobletNodeType.getLongName();
//        }
        return routeletDescription != null ? routeletDescription : ""; //$NON-NLS-1$;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getModulesNeeded()
     */
    @Override
    public List<ModuleNeeded> getModulesNeeded() {
        return new ArrayList<ModuleNeeded>();
        // return new ArrayList<ModuleNeeded>(JavaProcessUtil.getNeededModules(getJobletGEFProcess(), true));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getMultipleComponentManager()
     */
    @Override
    public List<IMultipleComponentManager> getMultipleComponentManagers() {
        return Collections.emptyList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getName()
     */
    @Override
    public String getName() {
//        switch (this.jobletNodeType) {
//        case ELEMENT:
            return routeletProcessName;
//        default:
//            return jobletNodeType.toString();
//        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getPathSource()
     */
    @Override
    public String getPathSource() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getPluginDependencies()
     */
    @Override
    public List<String> getPluginDependencies() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getPluginFullName()
     */
    @Override
    public String getPluginExtension() {
        return CamelDesignerPlugin.getDefault().getBundle().getSymbolicName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getTranslatedName()
     */
    public String getTranslatedName() {
//        switch (this.jobletNodeType) {
//        case ELEMENT:
            return routeletProcessName;
//        default:
//            return jobletNodeType.getDisplayName();
//        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getVersion()
     */
    @Override
    public String getVersion() {
//        switch (this.jobletNodeType) {
//        case ELEMENT:
            return routeletVersion;
//        default:
//            return "0.1"; //$NON-NLS-1$
//        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#hasConditionalOutputs()
     */
    @Override
    public boolean hasConditionalOutputs() {
        return Boolean.FALSE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isDataAutoPropagated()
     */
    @Override
    public boolean isDataAutoPropagated() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isLoaded()
     */
    @Override
    public boolean isLoaded() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isMultiplyingOutputs()
     */
    @Override
    public boolean isMultiplyingOutputs() {
        return Boolean.FALSE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isMultiplyingOutputs()
     */
    public Boolean isSubtreeWithLoop() {
        return Boolean.FALSE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isSchemaAutoPropagated()
     */
    @Override
    public boolean isSchemaAutoPropagated() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isVisible(java.lang.String)
     */
    @Override
    public boolean isVisible(String family) {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isVisible()
     */
    @Override
    public boolean isVisible() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#useMerge()
     */
    @Override
    public boolean useMerge() {
        return this.isUseMerge;
    }

    /**
     * Getter for property.
     * 
     * @return the property
     */
    public Property getProperty() {
        if (routeletId == null || routeletVersion == null) {
            return null;
        }
        if (routeletProcess != null && routeletProcess.getProperty() != null) {
            // property will be set while load the joblet, but after load the joblet it will be null.
            // this can help for performance to avoid to reload from repository for nothing
            return routeletProcess.getProperty();
        }
        Property property = null;
        try {
            property = ProxyRepositoryFactory.getInstance().getSpecificVersion(routeletId, routeletVersion, true).getProperty();
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
        return property;
    }

    /**
     * Sets the property.
     * 
     * @param property the property to set
     */
    public void setProperty(Property property) {
        routeletDescription = property.getDescription();
        routeletId = property.getId();
        routeletVersion = property.getVersion();
        routeletProcessName = property.getLabel();
        if (!CommonsPlugin.isHeadless()) {
            icon32 = ERouteletImages.getURLImageDescriptor(ERouteletImages.ROUTELET_COMPONENT_32);
            icon24 = ImageDescriptor.createFromImageData(icon32.getImageData().scaledTo(24, 24));
            // fixed about the generate documnents.
            // icon16 = ImageProvider.getImageDesc(ERouteletImages.ROUTELET_COMPONENT_16);
            icon16 = ERouteletImages.getURLImageDescriptor(ERouteletImages.ROUTELET_COMPONENT_16);
        } else {
            icon32 = ERouteletImages.getURLImageDescriptor(ERouteletImages.ROUTELET_COMPONENT_32);
        }
    }

//    public void forceReloadProcess(Property property) {
//        // this must be after.
//        this.setProperty(property);
//        retrieveJobletProcess(property, false);
//    }

    private void retrieveRouteletProcess(Property property, boolean firstLoad) {
        try {
            if (!CommonsPlugin.isHeadless() && PlatformUI.isWorkbenchRunning()) {
                // if there is any editor opened for this joblet, don't reload the property
                final List<IEditorReference> list = new ArrayList<IEditorReference>();
                Display.getDefault().syncExec(new Runnable() {

                    @Override
                    public void run() {
                        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                        if (page != null) {
                            IEditorReference[] reference = page.getEditorReferences();
                            list.addAll(Arrays.asList(reference));
                        }
                    }
                });

                List<IProcess2> openedProcessList = CorePlugin.getDefault().getDesignerCoreService()
                        .getOpenedProcess(list.toArray(new IEditorReference[0]));

                for (IProcess2 openedProcess : openedProcessList) {
                    if (openedProcess.getProperty().getId().equals(property.getId())) {
                        // same process.
                        routeletProcess = openedProcess;
                        routeletProcess.loadXmlFile(false);
                        // cause NPE
//                        routeletProcess.setProperty(null); // no need to keep emf object in memory
                        return;
                    }
                }
            }

            Property propertyToLoad = property;
            if (!firstLoad) {
                // if it's the first time we load the joblet, no need to reload it from repository since it's already
                // the good version
                Project project = new Project(ProjectManager.getInstance().getProject(property.getItem()));
                IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
                propertyToLoad = factory.getUptodateProperty(project, property);
            }
            routeletProcess = new RouteProcess(propertyToLoad);
            routeletProcess.loadXmlFile(false);
            // cause NPE
//            routeletProcess.setProperty(null); // no need to keep emf object in memory
            EObject parent = propertyToLoad.getItem().getParent();
            if (parent != null) {
                ((FolderItem) parent).getChildren().remove(propertyToLoad.getItem());
            }
            propertyToLoad.getItem().setParent(null);
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isMultipleOutput()
     */
    @Override
    public boolean isMultipleOutput() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#useLookup()
     */
    @Override
    public boolean useLookup() {
        return this.isUseLookUp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#useImport()
     */
    @Override
    public boolean useImport() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getComponentType()
     */
    @Override
    public EComponentType getComponentType() {
        return EComponentType.JOBLET;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isHashComponent()
     */
    @Override
    public boolean isHashComponent() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isTechnical()
     */
    @Override
    public boolean isTechnical() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isVisibleInComponentDefinition()
     */
    @Override
    public boolean isVisibleInComponentDefinition() {
        return true;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isMainCodeCalled()
     */
    @Override
    public boolean isMainCodeCalled() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#canParallelize()
     */
    @Override
    public boolean canParallelize() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getShortName()
     */
    @Override
    public String getShortName() {
        return "routelet";
    }

    /**
     * Getter for lastUpdated.
     * 
     * @return the lastUpdated
     */
    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    /**
     * Sets the lastUpdated.
     * 
     * @param lastUpdated the lastUpdated to set
     */
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String getCombine() {
        return null;
    }

    @Override
    public IProcess getProcess() {
        if (routeletProcess == null) {
            retrieveRouteletProcess(getProperty(), true);
        }
        return routeletProcess;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.model.components.IComponent#getPaletteType()
     */
    @Override
    public String getPaletteType() {
        return paletteType;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.model.components.IComponent#setPaletteType(java.lang.String)
     */
    @Override
    public void setPaletteType(String paletteType) {
        this.paletteType = paletteType;
    }

    @Override
    public void setImageRegistry(Map<String, ImageDescriptor> imageRegistry) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getRepositoryType() {
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
        result = prime * result + ((this.getVersion() == null) ? 0 : this.getVersion().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RouteletComponent)) {
            return false;
        }
        final RouteletComponent other = (RouteletComponent) obj;
        String thisName = this.getName();
        String otherName = other.getName();
        if (thisName == null) {
            if (otherName != null) {
                return false;
            }
        } else if (!thisName.equals(otherName)) {
            return false;
        }
        String thisVersion = this.getVersion();
        String otherVersion = other.getVersion();
        if (thisVersion == null) {
            if (otherVersion != null) {
                return false;
            }
        } else if (!thisVersion.equals(otherVersion)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getType()
     */
    @Override
    public String getType() {
        return ComponentCategory.CATEGORY_4_CAMEL.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isReduce()
     */
    @Override
    public boolean isReduce() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Getter for routeletId.
     * 
     * @return the routeletId
     */
    public String getJobletId() {
        return this.routeletId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getPartitioning()
     */
    @Override
    public String getPartitioning() {
        return "AUTO"; //$NON-NLS-1$
    }

    @Override
    public boolean isSupportDbType() {
        return false;
    }

    @Override
    public Map<String, ImageDescriptor> getImageRegistry() {
        return null;
    }

    @Override
    public boolean isLog4JEnabled() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getInputType()
     */
    @Override
    public String getInputType() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getOutputType()
     */
    @Override
    public String getOutputType() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#getCONNECTORList()
     */
    @Override
    public EList getCONNECTORList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAllowedPropagated() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.components.IComponent#isSparkAction()
     */
    @Override
    public boolean isSparkAction() {
        // TODO Auto-generated method stub
        return false;
    }

}
