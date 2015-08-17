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
package org.talend.designer.camel.resource.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.general.Project;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.core.model.relationship.RelationshipItemBuilder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.process.ITalendProcessJavaProject;
import org.talend.designer.camel.resource.core.extension.ResourceCheckExtensionPointManager;
import org.talend.designer.camel.resource.core.model.ResourceDependencyModel;
import org.talend.designer.core.IDesignerCoreService;
import org.talend.designer.core.ui.editor.process.Process;
import org.talend.designer.runprocess.IRunProcessService;
import org.talend.repository.ProjectManager;

/**
 * Route Resource Utility
 * 
 * @author xpli
 * 
 */
public class RouteResourceUtil {

    private static final String COMMA_TAG = ",";

    private static final String SLASH_TAG = "|";

    private static final String REPACE_SLASH_TAG = "\\|";

    public static final String LATEST_VERSION = RelationshipItemBuilder.LATEST_VERSION;

    private static final String ROUTE_RESOURCES_DESC_FILE = ".route_resources";

    public static final String ROUTE_RESOURCES_PROP = "ROUTE_RESOURCES_PROP";

    /**
     * Get source file of Item.
     * 
     * @param item
     * @return
     */
    public static IFile getSourceFile(RouteResourceItem item) {
        // the file may come from a reference project
        IFolder rrfolder = null;
        Resource eResource = item.eResource();
        if (eResource != null) {
            URI uri = eResource.getURI();
            if (uri != null && uri.isPlatformResource()) {
                String platformString = uri.toPlatformString(true);
                IContainer parentContainer = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString))
                        .getParent();
                if (parentContainer instanceof IFolder) {
                    rrfolder = (IFolder) parentContainer;
                }
            }
        }
        if (rrfolder == null) {
            Project talendProject = ProjectManager.getInstance().getCurrentProject();
            String technicalLabel = talendProject.getTechnicalLabel();

            IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(technicalLabel);
            String folderPath = item.getState().getPath();
            rrfolder = project.getFolder(RouteResourceItem.ROUTE_RESOURCES_FOLDER);
            if (folderPath != null && !folderPath.isEmpty()) {
                rrfolder = rrfolder.getFolder(folderPath);
            }
        }
        String itemName = item.getProperty().getLabel();
        String version = item.getProperty().getVersion();

        String fileExtension = item.getBindingExtension();
        String fileName = itemName + "_" + version + "." + fileExtension;
        IFile file = rrfolder.getFile(fileName);

        return file;
    }

    /**
     * 
     * @param routeItem
     * @param models
     */
    public static void saveResourceDependency(Map<Object, Object> map, Collection<ResourceDependencyModel> models) {
        final StringBuilder sb = new StringBuilder();
        for (ResourceDependencyModel item : models) {
            if (item.isBuiltIn()) {
                continue;
            }
            if (sb.length() != 0) {
                sb.append(RouteResourceUtil.COMMA_TAG);
            }
            sb.append(item.getItem().getProperty().getId());
            sb.append(RouteResourceUtil.SLASH_TAG);
            sb.append(item.getSelectedVersion());
        }
        if (sb.length() > 0) {
            map.put(ROUTE_RESOURCES_PROP, sb.toString());
        } else {
            map.remove(ROUTE_RESOURCES_PROP);
        }
    }

    /**
     * 
     * @param routeItem
     * @param models
     */
    public static Collection<ResourceDependencyModel> getResourceDependencies(Item routeItem) {

        Collection<ResourceDependencyModel> models =
            new HashSet<ResourceDependencyModel>(getBuiltInResourceDependencies(routeItem));
        Object resourcesObj = routeItem.getProperty().getAdditionalProperties().get(ROUTE_RESOURCES_PROP);
        if (resourcesObj != null) {
            String[] resourceIds = resourcesObj.toString().split(RouteResourceUtil.COMMA_TAG);
            for (String id : resourceIds) {
                String[] parts = id.split(REPACE_SLASH_TAG);
                if (parts.length != 2) {
                    continue;
                }
                String idPart = parts[0];
                String versionPart = parts[1];

                try {
                    IRepositoryViewObject rvo = null;
                    if (RouteResourceUtil.LATEST_VERSION.equals(versionPart)) {
                        rvo = ProxyRepositoryFactory.getInstance().getLastVersion(idPart);
                    } else {
                        rvo = ProxyRepositoryFactory.getInstance().getSpecificVersion(idPart, versionPart, false);
                    }
                    if (rvo != null) {
                        Item item = rvo.getProperty().getItem();
                        ResourceDependencyModel model = new ResourceDependencyModel((RouteResourceItem) item);
                        model.setSelectedVersion(versionPart);
                        model.setBuiltIn(false);
                        models.add(model);
                    }
                } catch (PersistenceException e) {
                    ExceptionHandler.process(e);
                }
            }
        }
        return models;
    }

    /**
     * @param routeItem
     * 
     * @param models
     */
    public static Set<ResourceDependencyModel> getBuiltInResourceDependencies(Item routeItem) {

        Property property = routeItem.getProperty();
        // Changed for TDI-24563
        // Process process = new org.talend.designer.core.ui.editor.process.Process(property);
        Process process = getProcessFromItem(property.getItem());
        if (process == null) {
            return new HashSet<ResourceDependencyModel>();
        }
        process.loadXmlFile();
        List<? extends INode> nodes = process.getGraphicalNodes();
        Set<ResourceDependencyModel> models = getBuiltInResourceDependencies(nodes);
        process.dispose();
        return models;
    }

    /**
     * @param routeItem
     * 
     * @param models
     */
    public static Set<ResourceDependencyModel> getBuiltInResourceDependencies(IRepositoryViewObject node) {
        Property property = node.getProperty();
        // Changed for TDI-24563
        // Process process = new org.talend.designer.core.ui.editor.process.Process(property);
        Item item = property.getItem();
        Process process = getProcessFromItem(item);
        if (process == null) {
            return new HashSet<ResourceDependencyModel>();
        }
        process.loadXmlFile();
        List<? extends INode> nodes = process.getGraphicalNodes();
        return getBuiltInResourceDependencies(nodes);
    }

    private static Process getProcessFromItem(Item item) {
        IProcess process = null;
        IDesignerCoreService designerCoreService = (IDesignerCoreService) GlobalServiceRegister.getDefault().getService(
                IDesignerCoreService.class);
        if (designerCoreService != null) {
            process = designerCoreService.getProcessFromItem(item);
        }
        if (process != null && process instanceof Process) {
            return (Process) process;
        }
        return null;
    }

    /**
     * @param nodes
     * 
     * @param models
     */
    public static Set<ResourceDependencyModel> getBuiltInResourceDependencies(List<? extends INode> nodes) {

        Set<ResourceDependencyModel> models = new HashSet<ResourceDependencyModel>();
        for (INode node : nodes) {
            Set<ResourceDependencyModel> resourceModels = ResourceCheckExtensionPointManager.INSTANCE.getResourceModel(node);
            boolean isContained = false;
            // Merge and add
            for (ResourceDependencyModel rdm : resourceModels) {
                for (ResourceDependencyModel model : models) {
                    if (model.equals(rdm)) {
                        model.getRefNodes().addAll(rdm.getRefNodes());
                        isContained = true;
                        break;
                    }
                }
                if (!isContained) {
                    models.add(rdm);
                }
            }
        }

        return models;
    }

    /**
     * 
     * @param routeItem
     * @param model
     */
    public static void addResourceDependency(Item routeItem, ResourceDependencyModel model) {
        Property property = routeItem.getProperty();
        String newModelId = model.getItem().getProperty().getId();
        String newStoreValue = "";
        EMap additionalProperties = property.getAdditionalProperties();
        if (additionalProperties != null) {
            String resourcesObj = (String) additionalProperties.get(ROUTE_RESOURCES_PROP);
            if (resourcesObj != null) {
                boolean duplicated = false;
                String[] resourceIdVersions = resourcesObj.split(COMMA_TAG);
                for (String idVersion : resourceIdVersions) {
                    String[] parts = idVersion.split(REPACE_SLASH_TAG);
                    String idPart = parts[0];

                    // Id duplicate
                    if (model.getItem().getProperty().getId().equals(idPart)) {
                        String newId = idPart + SLASH_TAG + model.getSelectedVersion();
                        newStoreValue = resourcesObj.toString().replace(idVersion, newId);
                        duplicated = true;
                    }
                }
                if (!duplicated) {
                    // New id
                    newStoreValue = resourcesObj + COMMA_TAG + newModelId + SLASH_TAG + model.getSelectedVersion();
                }
            } else {
                newStoreValue = newModelId + SLASH_TAG + model.getSelectedVersion();
            }
            additionalProperties.put(ROUTE_RESOURCES_PROP, newStoreValue);
        }
        try {
            ProxyRepositoryFactory.getInstance().save(routeItem, false);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param id
     * @param version
     * @return
     */
    public static ResourceDependencyModel createDependency(String id, String version) {
        IRepositoryViewObject rvo = null;
        try {
            if (RouteResourceUtil.LATEST_VERSION.equals(version)) {
                rvo = ProxyRepositoryFactory.getInstance().getLastVersion(id);
            } else {
                rvo = ProxyRepositoryFactory.getInstance().getSpecificVersion(id, version, true);
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        if (rvo != null) {
            Item item = rvo.getProperty().getItem();
            ResourceDependencyModel resourceDependencyModel = new ResourceDependencyModel((RouteResourceItem) item);
            resourceDependencyModel.setSelectedVersion(version);
            resourceDependencyModel.setBuiltIn(true);
            return resourceDependencyModel;
        }
        return null;
    }

    public static Collection<IPath> synchronizeRouteResource(Item item) {
        if (!GlobalServiceRegister.getDefault().isServiceRegistered(IRunProcessService.class)) {
            return null;
        }
        final IRunProcessService processService = (IRunProcessService) GlobalServiceRegister.getDefault().getService(
                IRunProcessService.class);
        final ITalendProcessJavaProject talendProcessJavaProject = processService.getTalendProcessJavaProject();
        if (talendProcessJavaProject == null) {
            return null;
        }

        final Collection<IPath> result = new ArrayList<IPath>();
        // https://jira.talendforge.org/browse/TESB-7893
        // add spring file
        final IPath springFilePath = talendProcessJavaProject.getSrcFolder().getLocation().append(
                "/META-INF/spring/" + item.getProperty().getLabel().toLowerCase() + ".xml");
        result.add(springFilePath);

        final IFolder routeResourceFolder = talendProcessJavaProject.getResourcesFolder();
        // Clear route resources before running
        final IFile resourceDescFile = talendProcessJavaProject.getProject().getFile(ROUTE_RESOURCES_DESC_FILE);
        if (resourceDescFile.exists()) {
            InputStream fileInputStream = null;
            try {
                fileInputStream = resourceDescFile.getContents();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Delete resources
                    final IFile resFile = routeResourceFolder.getFile(new Path(line));
                    if (resFile.exists()) {
                        resFile.delete(true, null);
                    }
                }
            } catch (Exception e) {
                ExceptionHandler.process(e);
            } finally {
                if (null != fileInputStream) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        // nothing
                    }
                }
            }
        }

        final StringBuilder buffer = new StringBuilder();
        for (ResourceDependencyModel model : getResourceDependencies(item)) {
            IFile file = copyResources(routeResourceFolder, model);
            if (file != null) {
                result.add(file.getLocation());
                buffer.append(model.getClassPathUrl()).append("\n");
            }
        }

        // Add resources to .route_resources file
        final InputStream is = new ByteArrayInputStream(buffer.toString().getBytes());
        try {
            if (resourceDescFile.exists()) {
                resourceDescFile.setContents(is, 0, null);
            } else {
                resourceDescFile.create(is, true, null);
            }
        } catch (CoreException e) {
            ExceptionHandler.process(e);
        }

        return result;
    }

    /**
     * Copy route resource
     * 
     * @param model
     * @throws CoreException
     */
    private static IFile copyResources(final IFolder folder, final ResourceDependencyModel model) {
        final RouteResourceItem item = model.getItem();
        EList<?> referenceResources = item.getReferenceResources();
        if (referenceResources.isEmpty()) {
            return null;
        }
        final ReferenceFileItem refFile = (ReferenceFileItem) referenceResources.get(0);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(refFile.getContent().getInnerContent());

        final IFile classpathFile = folder.getFile(new Path(model.getClassPathUrl()));

        try {
            if (classpathFile.exists()) {
                classpathFile.setContents(inputStream, 0, null);
            } else {
                if (!classpathFile.getParent().exists()) {
                    prepareFolder((IFolder) classpathFile.getParent());
                }
                classpathFile.create(inputStream, true, null);
            }
        } catch (CoreException e) {
            ExceptionHandler.process(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // nothing
            }
        }
        return classpathFile;
    }

    private static void prepareFolder(IFolder folder) throws CoreException {
        IContainer parent = folder.getParent();
        if (IResource.FOLDER == parent.getType()) {
            prepareFolder((IFolder) parent);
        }
        if (!folder.exists()) {
            folder.create(true, true, null);
        }
    }
}
