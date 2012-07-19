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
package org.talend.designer.camel.resource.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EMap;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.generation.JavaUtils;
import org.talend.core.model.general.Project;
import org.talend.core.model.process.INode;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.FileItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.resource.core.extension.ResourceCheckExtensionPointManager;
import org.talend.designer.camel.resource.core.model.ResourceDependencyModel;
import org.talend.designer.core.ui.editor.process.Process;
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

	public static final String LATEST_VERSION = "Latest";

	private static final String ROUTE_RESOURCES_PROP = "ROUTE_RESOURCES_PROP";

	private static final String ROUTE_RESOURCES_DESC_FILE = ".route_resources";

	/**
	 * Clear route resource
	 * 
	 * @param item
	 * @throws CoreException
	 */
	public static void clearResources() throws CoreException {

		IFolder folder = getRouteResourceFolder();

		File resFolder = folder.getLocation().toFile();
		if (resFolder.exists()) {
			resFolder.delete();
		}
		folder.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
	}

	/**
	 * Copy route resource
	 * 
	 * @param item
	 * @throws CoreException
	 */
	public static void copyResources(FileItem item) throws CoreException {

		IFolder folder = getRouteResourceFolder();
		File resFolder = folder.getLocation().toFile();
		if (!resFolder.exists()) {
			resFolder.mkdirs();
		}

		ByteArray content = item.getContent();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				content.getInnerContent());

		IFile resFile = folder.getFile(item.getProperty().getLabel());

		File file = resFile.getLocation().toFile();
		if (file.exists()) {
			file.delete();
		}
		folder.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		resFile.create(inputStream, true, new NullProgressMonitor());
	}

	/**
	 * Delete route resource
	 * 
	 * @param item
	 * @throws CoreException
	 */
	public static void deleteResources(FileItem item) throws CoreException {

		IFolder folder = getRouteResourceFolder();
		File resFolder = folder.getLocation().toFile();
		if (!resFolder.exists()) {
			return;
		}
		IFile resFile = folder.getFile(item.getProperty().getLabel());
		resFile.getLocation().toFile().delete();
		folder.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
	}

	private static IFolder getRouteResourceFolder() {
		IPath path = new Path(JavaUtils.JAVA_SRC_DIRECTORY);
		path = path.append(RouteResourceItem.ROUTE_RESOURCES_FOLDER);

		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(JavaUtils.JAVA_PROJECT_NAME);
		return project.getFolder(path);
	}

	/**
	 * Get source file of Item.
	 * 
	 * @param item
	 * @return
	 */
	public static IFile getSourceFile(Item item) {
		Project talendProject = ProjectManager.getInstance()
				.getCurrentProject();
		String technicalLabel = talendProject.getTechnicalLabel();

		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(technicalLabel);
		String folderPath = item.getState().getPath();
		IFolder rrfolder = project
				.getFolder(RouteResourceItem.ROUTE_RESOURCES_FOLDER);
		if (folderPath != null && !folderPath.isEmpty()) {
			rrfolder = rrfolder.getFolder(folderPath);
		}
		String itemName = item.getProperty().getLabel();
		String version = item.getProperty().getVersion();
		String fileName = itemName + "_" + version + ".item";
		IFile file = rrfolder.getFile(fileName);

		return file;
	}

	/**
	 * 
	 * @param routeItem
	 * @param models
	 */
	public static void saveResourceDependency(Item routeItem,
			Set<ResourceDependencyModel> models) {
		EMap additionalProperties = routeItem.getProperty()
				.getAdditionalProperties();

		if (additionalProperties != null) {
			StringBuffer sb = new StringBuffer();
			for (ResourceDependencyModel item : models) {
				if (item.isBuiltIn()) {
					continue;
				}
				sb.append(item.getItem().getProperty().getId());
				sb.append(RouteResourceUtil.SLASH_TAG);
				sb.append(item.getSelectedVersion());
				sb.append(RouteResourceUtil.COMMA_TAG);
			}
			if (sb.length() > 0) {
				String string = sb.substring(0, sb.length() - 1);
				additionalProperties.put(ROUTE_RESOURCES_PROP, string);
			} else {
				additionalProperties.put(ROUTE_RESOURCES_PROP, "");
			}
		}

		try {
			ProxyRepositoryFactory.getInstance().save(routeItem, false);
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param routeItem
	 * @param models
	 */
	public static Set<ResourceDependencyModel> getResourceDependencies(
			Item routeItem) {

		Set<ResourceDependencyModel> models = new HashSet<ResourceDependencyModel>();
		Property property = routeItem.getProperty();

		EMap additionalProperties = property.getAdditionalProperties();
		if (additionalProperties != null) {
			Object resourcesObj = additionalProperties
					.get(ROUTE_RESOURCES_PROP);
			if (resourcesObj != null) {
				String[] resourceIds = resourcesObj.toString().split(
						RouteResourceUtil.COMMA_TAG);
				for (String id : resourceIds) {
					String[] parts = id
							.split(RouteResourceUtil.REPACE_SLASH_TAG);
					if (parts.length != 2) {
						continue;
					}
					String idPart = parts[0];
					String versionPart = parts[1];

					try {
						IRepositoryViewObject rvo = null;
						if (RouteResourceUtil.LATEST_VERSION
								.equals(versionPart)) {
							rvo = ProxyRepositoryFactory.getInstance()
									.getLastVersion(idPart);
						} else {
							rvo = ProxyRepositoryFactory.getInstance()
									.getSpecificVersion(idPart, versionPart,
											false);
						}
						if (rvo != null) {
							Item item = rvo.getProperty().getItem();
							ResourceDependencyModel model = new ResourceDependencyModel(
									(RouteResourceItem) item);
							model.setSelectedVersion(versionPart);
							model.setBuiltIn(false);
							models.add(model);
						}
					} catch (PersistenceException e) {
						e.printStackTrace();
					}
				}

			}
		}
		models.addAll(getBuiltInResourceDependencies(routeItem));
		return models;
	}
	
	/**
	 * @param routeItem
	 * 
	 * @param models
	 */
	public static Set<ResourceDependencyModel> getBuiltInResourceDependencies(
			Item routeItem) {
		
		Property property = routeItem.getProperty();
		Process process = new org.talend.designer.core.ui.editor.process.Process(
				property);
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
	public static Set<ResourceDependencyModel> getBuiltInResourceDependencies(
			IRepositoryViewObject node) {
		Property property = node.getProperty();
		Process process = new org.talend.designer.core.ui.editor.process.Process(
				property);
		process.loadXmlFile();
		List<? extends INode> nodes = process.getGraphicalNodes();
		return getBuiltInResourceDependencies(nodes);
	}

	/**
	 * @param nodes
	 * 
	 * @param models
	 */
	public static Set<ResourceDependencyModel> getBuiltInResourceDependencies(
			List<? extends INode> nodes) {

		Set<ResourceDependencyModel> models = new HashSet<ResourceDependencyModel>();
		for (INode node : nodes) {
			Set<ResourceDependencyModel> resourceModels = ResourceCheckExtensionPointManager.INSTANCE
					.getResourceModel(node);
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
	public static void addResourceDependency(Item routeItem,
			ResourceDependencyModel model) {
		Property property = routeItem.getProperty();
		String newModelId = model.getItem().getProperty().getId();
		String newStoreValue = "";
		EMap additionalProperties = property.getAdditionalProperties();
		if (additionalProperties != null) {
			String resourcesObj = (String) additionalProperties
					.get(ROUTE_RESOURCES_PROP);
			if (resourcesObj != null) {
				boolean duplicated = false;
				String[] resourceIdVersions = resourcesObj.split(COMMA_TAG);
				for (String idVersion : resourceIdVersions) {
					String[] parts = idVersion.split(REPACE_SLASH_TAG);
					String idPart = parts[0];

					// Id duplicate
					if (model.getItem().getProperty().getId().equals(idPart)) {
						String newId = idPart + SLASH_TAG
								+ model.getSelectedVersion();
						newStoreValue = resourcesObj.toString().replace(
								idVersion, newId);
						duplicated = true;
					}
				}
				if (!duplicated) {
					// New id
					newStoreValue = resourcesObj + "," + newModelId + SLASH_TAG
							+ model.getSelectedVersion();
				}
			} else {
				newStoreValue = newModelId + SLASH_TAG
						+ model.getSelectedVersion();
			}
		}
		additionalProperties.put(ROUTE_RESOURCES_PROP, newStoreValue);
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
	public static ResourceDependencyModel createDependency(String id,
			String version) {
		IRepositoryViewObject rvo = null;
		try {
			if (RouteResourceUtil.LATEST_VERSION.equals(version)) {
				rvo = ProxyRepositoryFactory.getInstance()
						.getLastVersion(id);
			} else {
				rvo = ProxyRepositoryFactory.getInstance().getSpecificVersion(
						id, version, true);
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		Item item = rvo.getProperty().getItem();
		ResourceDependencyModel resourceDependencyModel = new ResourceDependencyModel(
				(RouteResourceItem) item);
		resourceDependencyModel.setSelectedVersion((String) version);
		resourceDependencyModel.setBuiltIn(true);
		return resourceDependencyModel;
	}

	/**
	 * Clear route resources before running
	 */
	public static void clearRouteResources() {

		File localFile = getResourceDescFile();
		IProject srcFolder = getProject();

		Set<String> resFileNames = new HashSet<String>();
		try {
			InputStream fileInputStream = null;
			BufferedReader reader = null;
			try{
				if (!localFile.exists()) {
					localFile.createNewFile();
				}
				fileInputStream = new FileInputStream(localFile);
				reader = new BufferedReader(new InputStreamReader(
						fileInputStream, "utf-8"));
				String line = reader.readLine();
				while (line != null) {
					resFileNames.add(line);
					line = reader.readLine();

				}
			} finally {
				if (reader != null) {
					reader.close();
				}
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

		// Delete resources
		for (String name : resFileNames) {
			IFile resFile = srcFolder.getFile(new Path(name));
			if (resFile.exists()) {
				try {
					resFile.delete(true, new NullProgressMonitor());
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static IProject getProject() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(JavaUtils.JAVA_PROJECT_NAME);
		return project;
	}

	private static File getResourceDescFile() {
		IProject srcFolder = getProject();
		IFile file = srcFolder.getFile(ROUTE_RESOURCES_DESC_FILE);
		File localFile = file.getLocation().toFile();
		return localFile;
	}

	/**
	 * Add resources to .route_resources file
	 * 
	 * @param models
	 */
	public static void addRouteResourcesDesc(
			Set<ResourceDependencyModel> models) {

		File localFile = getResourceDescFile();
		StringBuffer buffer = new StringBuffer();
		for(ResourceDependencyModel model: models){
			buffer.append(model.getClassPathUrl()).append("\n");
		}
		
		try {
			OutputStream outputStream = null;
			OutputStreamWriter writer = null;
			try {
				outputStream = new FileOutputStream(localFile);
				writer = new OutputStreamWriter(
						outputStream, "utf-8");
				writer.write(buffer.toString());
				writer.flush();
			} finally {
				if (writer != null) {
					writer.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
