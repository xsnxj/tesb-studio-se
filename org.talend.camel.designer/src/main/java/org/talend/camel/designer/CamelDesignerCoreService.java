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
package org.talend.camel.designer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.camel.designer.ui.CreateCamelProcess;
import org.talend.camel.designer.ui.bean.CreateCamelBean;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.generation.JavaUtils;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.process.Element;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.Problem.ProblemStatus;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.FileItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.resource.core.model.ResourceDependencyModel;
import org.talend.designer.camel.resource.core.util.RouteResourceUtil;
import org.talend.designer.codegen.ITalendSynchronizer;
import org.talend.designer.core.ICamelDesignerCoreService;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.ui.views.problems.Problems;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelDesignerCoreService implements ICamelDesignerCoreService {

	// private XmiResourceManager xmiResourceManager = new XmiResourceManager();

	private static final String ROUTE_RESOURCES = RouteResourceItem.ROUTE_RESOURCES_FOLDER;

	/*
	 * (non-Jsdoc)
	 * 
	 * @see
	 * org.talend.designer.core.ICamelDesignerCoreService#getCreateProcessAction
	 * (boolean)
	 */
	public IAction getCreateProcessAction(boolean isToolbar) {
		return new CreateCamelProcess(isToolbar);
	}

	/*
	 * (non-Jsdoc)
	 * 
	 * @see
	 * org.talend.designer.core.ICamelDesignerCoreService#getCreateBeanAction
	 * (boolean)
	 */
	public IAction getCreateBeanAction(boolean isToolbar) {
		// TODO Auto-generated method stub
		return new CreateCamelBean(isToolbar);
	}

	public ERepositoryObjectType getRoutes() {
		return CamelRepositoryNodeType.repositoryRoutesType;
	}

	public ERepositoryObjectType getBeansType() {
		return CamelRepositoryNodeType.repositoryBeansType;
	}

	public ProcessType getCamelProcessType(Item item) {
		if (item instanceof CamelProcessItem) {
			CamelProcessItem camelItem = (CamelProcessItem) item;
			return camelItem.getProcess();
		}
		return null;
	}

	public boolean isInstanceofCamelRoutes(Item item) {
		if (item instanceof CamelProcessItem) {
			return true;
		}
		return false;
	}

	public boolean isInstanceofCamelBeans(Item item) {
		if (item instanceof BeanItem) {
			return true;
		}
		return false;
	}

	public boolean isInstanceofCamel(Item item) {
		if (item instanceof BeanItem || item instanceof CamelProcessItem) {
			return true;
		}
		return false;
	}

	public ITalendSynchronizer createCamelJavaSynchronizer() {
		return new CamelJavaRoutesSychronizer();
	}

	public boolean isCamelRepObjType(ERepositoryObjectType type) {
		boolean isCamelType = false;
		if (type == CamelRepositoryNodeType.repositoryRoutesType
				|| type == CamelRepositoryNodeType.repositoryBeansType) {
			isCamelType = true;
		}
		return isCamelType;
	}

	public boolean isCamelMulitPageEditor(IEditorPart editor) {
		boolean isCamelEditor = false;
		if (editor instanceof CamelMultiPageTalendEditor) {
			isCamelEditor = true;
		}
		return isCamelEditor;
	}

	public List<IPath> synchronizeRouteResource(Item item) {

		List<IPath> paths = new ArrayList<IPath>();

		if (!(item instanceof CamelProcessItem)) {
			return paths;
		}

		List<ResourceDependencyModel> models = RouteResourceUtil
				.getResourceDependencies(item);
		for (ResourceDependencyModel model : models) {
			IFile file = copyResources(model.getItem());
			if (file != null) {
				paths.add(file.getLocation());
			}
		}

		forceBuildProject();

		return paths;
	}

	/**
	 * Build project
	 */
	private void forceBuildProject() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(JavaUtils.JAVA_PROJECT_NAME);
		try {
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD,
					new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private static IFolder getRouteResourceFolder() {
		IPath path = new Path(JavaUtils.JAVA_SRC_DIRECTORY);
		// http://jira.talendforge.org/browse/TESB-6437
		// path = path.append(ROUTE_RESOURCES);
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(JavaUtils.JAVA_PROJECT_NAME);
		return project.getFolder(path);
	}

	/**
	 * Copy route resource
	 * 
	 * @param item
	 * @throws CoreException
	 */
	public static IFile copyResources(FileItem item) {

		IFolder folder = getRouteResourceFolder();

		ByteArray content = item.getContent();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				content.getInnerContent());

		String path = item.getState().getPath();
		String label = item.getProperty().getLabel();
		IFolder resFolder = folder;
		if (path != null && !path.isEmpty()) {
			resFolder = folder.getFolder(path);
		}

		File resFileFolder = resFolder.getLocation().toFile();

		if (!resFileFolder.exists()) {
			resFileFolder.mkdirs();
		}

		IFile resFile = resFolder.getFile(label);

		File file = resFile.getLocation().toFile();
		if (file.exists()) {
			file.delete();
		}
		try {
			resFolder.refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
			resFile.create(inputStream, true, new NullProgressMonitor());
			return resFile;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

}
