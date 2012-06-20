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
package org.talend.designer.camel.resource.ui.util;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.talend.commons.utils.generation.JavaUtils;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.FileItem;

/**
 * Route Resource Utility
 * 
 * @author xpli
 * 
 */
public class RouteResourceUtil {

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
		path = path.append("route_resources");

		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(JavaUtils.JAVA_PROJECT_NAME);
		return project.getFolder(path);
	}

}
