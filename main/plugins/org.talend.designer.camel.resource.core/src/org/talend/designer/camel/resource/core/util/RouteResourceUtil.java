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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.general.Project;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.properties.Item;
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
        final Collection<ResourceDependencyModel> builtInModels = getBuiltInResourceDependencies(routeItem);
        final Collection<ResourceDependencyModel> models = new ArrayList<ResourceDependencyModel>(builtInModels);
        final Object resourcesObj = routeItem.getProperty().getAdditionalProperties().get(ROUTE_RESOURCES_PROP);
        if (resourcesObj != null) {
            for (String id : resourcesObj.toString().split(RouteResourceUtil.COMMA_TAG)) {
                final String[] parts = id.split(REPACE_SLASH_TAG);
                if (parts.length == 2) {
                    final ResourceDependencyModel model = createDependency(parts[0], parts[1]);
                    if (!builtInModels.contains(model)) {
                        models.add(model);
                    }
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
    private static Collection<ResourceDependencyModel> getBuiltInResourceDependencies(Item routeItem) {
        // Changed for TDI-24563
        // Process process = new org.talend.designer.core.ui.editor.process.Process(property);
        final Process process = getProcessFromItem(routeItem.getProperty().getItem());
        if (process == null) {
            return Collections.emptySet();
        }
        process.loadXmlFile();

        final Collection<ResourceDependencyModel> models = new HashSet<ResourceDependencyModel>();
        for (INode node : process.getGraphicalNodes()) {
            final Collection<ResourceDependencyModel> resourceModels =
                ResourceCheckExtensionPointManager.INSTANCE.getResourceModel(node);
            // Merge and add
            for (ResourceDependencyModel rdm : resourceModels) {
                if (!models.add(rdm)) {
                    for (ResourceDependencyModel model : models) {
                        if (model.equals(rdm)) {
                            model.getRefNodes().addAll(rdm.getRefNodes());
                            break;
                        }
                    }
                }
            }
        }

        process.dispose();
        return models;
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
     * 
     * @param id
     * @param version
     * @return
     */
    public static ResourceDependencyModel createDependency(String id, String version) {
        try {
            final IRepositoryViewObject rvo;
            if (RouteResourceUtil.LATEST_VERSION.equals(version)) {
                rvo = ProxyRepositoryFactory.getInstance().getLastVersion(id);
            } else {
                rvo = ProxyRepositoryFactory.getInstance().getSpecificVersion(id, version, true);
            }
            if (rvo != null) {
                final ResourceDependencyModel model =
                    new ResourceDependencyModel((RouteResourceItem) rvo.getProperty().getItem());
                model.setSelectedVersion(version);
                return model;
            }
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
        return null;
    }

    public static Collection<IPath> synchronizeRouteResource(Item item) {
        if (!(item instanceof CamelProcessItem)) {
            return null;
        }
        if (!GlobalServiceRegister.getDefault().isServiceRegistered(IRunProcessService.class)) {
            return null;
        }
        final IRunProcessService processService = (IRunProcessService) GlobalServiceRegister.getDefault().getService(
                IRunProcessService.class);
        final ITalendProcessJavaProject talendProcessJavaProject = processService.getTalendProcessJavaProject();
        if (talendProcessJavaProject == null) {
            return null;
        }

        final IFolder routeResourceFolder = talendProcessJavaProject.getResourcesFolder();

        final Collection<IPath> result = new ArrayList<IPath>();
        // https://jira.talendforge.org/browse/TESB-7893
        // add spring file
        final IFolder metaInf = routeResourceFolder.getFolder("META-INF/spring/");
        try {
            prepareFolder(metaInf);
            final IFile spring = metaInf.getFile(item.getProperty().getLabel().toLowerCase() + ".xml");
            final InputStream inputStream = new ByteArrayInputStream(((CamelProcessItem) item).getSpringContent().getBytes());
            if (spring.exists()) {
                spring.setContents(inputStream, 0, null);
            } else {
                spring.create(inputStream, true, null);
            }
            result.add(spring.getLocation());
        } catch (CoreException e) {
            ExceptionHandler.process(e);
        }

        for (ResourceDependencyModel model : getResourceDependencies(item)) {
            IFile file = copyResources(routeResourceFolder, model);
            if (file != null) {
                result.add(file.getLocation());
            }
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
        final InputStream inputStream = new ByteArrayInputStream(refFile.getContent().getInnerContent());

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
