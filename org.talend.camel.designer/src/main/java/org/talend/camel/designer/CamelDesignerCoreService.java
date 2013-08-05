// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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
import java.util.Set;

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
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.camel.designer.ui.CreateCamelProcess;
import org.talend.camel.designer.ui.bean.CreateCamelBean;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.utils.generation.JavaUtils;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.designer.camel.resource.core.model.ResourceDependencyModel;
import org.talend.designer.camel.resource.core.util.RouteResourceUtil;
import org.talend.designer.codegen.ITalendSynchronizer;
import org.talend.designer.core.ICamelDesignerCoreService;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelDesignerCoreService implements ICamelDesignerCoreService {

	// private XmiResourceManager xmiResourceManager = new XmiResourceManager();

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

	public String getDeleteFolderName(ERepositoryObjectType type){
		return CamelRepositoryNodeType.AllRouteRespositoryTypes.get(type);
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
	
	public ERepositoryObjectType getResourcesType() {
		return CamelRepositoryNodeType.repositoryRouteResourceType;
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
		if (item instanceof BeanItem || item instanceof CamelProcessItem || item instanceof RouteResourceItem) {
			return true;
		}
		return false;
	}

	public ITalendSynchronizer createCamelJavaSynchronizer() {
		return new CamelJavaRoutesSychronizer();
	}

	public boolean isCamelMulitPageEditor(IEditorPart editor) {
		boolean isCamelEditor = false;
		if (editor instanceof CamelMultiPageTalendEditor) {
			isCamelEditor = true;
		}
		return isCamelEditor;
	}

	public List<IPath> synchronizeRouteResource(Item item) {

		RouteResourceUtil.clearRouteResources();

		List<IPath> paths = new ArrayList<IPath>();

		if (!(item instanceof CamelProcessItem)) {
			return paths;
		}

		Set<ResourceDependencyModel> models = RouteResourceUtil
				.getResourceDependencies(item);
		for (ResourceDependencyModel model : models) {
			IFile file = copyResources(model);
			if (file != null) {
				paths.add(file.getLocation());
			}
		}

		RouteResourceUtil.addRouteResourcesDesc(models);

		forceBuildProject();
		
		//https://jira.talendforge.org/browse/TESB-7893
		//add spring file
		IPath springFilePath = getRouteResourceFolder().getLocation().append("/META-INF/spring/"+item.getProperty().getLabel().toLowerCase()+".xml");
		paths.add(springFilePath);

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
	 * @param model
	 * @throws CoreException
	 */
	public static IFile copyResources(ResourceDependencyModel model) {

		IFolder folder = getRouteResourceFolder();

		RouteResourceItem item = model.getItem();
		ByteArray content = null;
		EList referenceResources = item.getReferenceResources();
		if (referenceResources.isEmpty()) {
			return null;
		}
		ReferenceFileItem refFile = (ReferenceFileItem) referenceResources
				.get(0);
		content = refFile.getContent();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				content.getInnerContent());

		String classPathUrl = model.getClassPathUrl();
		IFile classpathFile = folder.getFile(new Path(classPathUrl));
		IFolder parentFolder = (IFolder) classpathFile.getParent();

		// Check parent folder exists
		File parentFolderFile = parentFolder.getLocation().toFile();
		if (!parentFolderFile.exists()) {
			parentFolderFile.mkdirs();
		}

		// Check resource class path file not exist
		File classpathLocalFile = classpathFile.getLocation().toFile();
		if (classpathLocalFile.exists()) {
			classpathLocalFile.delete();
		}

		try {
			try {
				parentFolder.refreshLocal(IResource.DEPTH_ONE,
						new NullProgressMonitor());
				classpathFile.create(inputStream, true,
						new NullProgressMonitor());
			} finally {
				inputStream.close();
			}
			return classpathFile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

}
