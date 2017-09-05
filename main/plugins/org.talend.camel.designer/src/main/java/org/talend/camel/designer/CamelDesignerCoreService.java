// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.core.model.camelProperties.CamelPropertiesFactory;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.camel.designer.ui.CreateCamelProcess;
import org.talend.camel.designer.ui.bean.CreateCamelBean;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.components.ComponentCategory;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.INodeConnector;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.FileItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.runtime.process.ITalendProcessJavaProject;
import org.talend.designer.camel.dependencies.core.model.IDependencyItem;
import org.talend.designer.camel.dependencies.core.util.OsgiDependenciesService;
import org.talend.designer.camel.resource.core.model.ResourceDependencyModel;
import org.talend.designer.camel.resource.core.util.RouteResourceUtil;
import org.talend.designer.codegen.CodeGeneratorActivator;
import org.talend.designer.codegen.ITalendSynchronizer;
import org.talend.designer.core.ICamelDesignerCoreService;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.runprocess.IRunProcessService;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelDesignerCoreService implements ICamelDesignerCoreService {

    // private XmiResourceManager xmiResourceManager = new XmiResourceManager();

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.core.ICamelDesignerCoreService#getCreateProcessAction (boolean)
     */
    public IAction getCreateProcessAction(boolean isToolbar) {
        return new CreateCamelProcess(isToolbar);
    }

    public String getDeleteFolderName(ERepositoryObjectType type) {
        return CamelRepositoryNodeType.AllRouteRespositoryTypes.get(type);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.core.ICamelDesignerCoreService#getCreateBeanAction (boolean)
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

    public ERepositoryObjectType getRouteDocType() {
        return CamelRepositoryNodeType.repositoryDocumentationType;
    }

    @Override
    public ERepositoryObjectType getRouteDocsType() {
        return CamelRepositoryNodeType.repositoryDocumentationsType;
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

        Set<ResourceDependencyModel> models = RouteResourceUtil.getResourceDependencies(item);
        for (ResourceDependencyModel model : models) {
            IFile file = copyResources(model);
            if (file != null) {
                paths.add(file.getLocation());
            }
        }

        RouteResourceUtil.addRouteResourcesDesc(models);

        forceBuildProject();

        // https://jira.talendforge.org/browse/TESB-7893
        // add spring file
        IFolder routeResourceFolder = getRouteResourceFolder();
        if (routeResourceFolder != null) {
            IPath springFilePath = routeResourceFolder.getLocation().append(
                    "/META-INF/spring/" + item.getProperty().getLabel().toLowerCase() + ".xml");
            paths.add(springFilePath);
        }

        return paths;
    }

    /**
     * Build project
     */
    private void forceBuildProject() {
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IRunProcessService.class)) {
            IRunProcessService processService = (IRunProcessService) GlobalServiceRegister.getDefault().getService(
                    IRunProcessService.class);
            processService.buildJavaProject();
        }

    }

    private static IFolder getRouteResourceFolder() {
        IRunProcessService service = CodeGeneratorActivator.getDefault().getRunProcessService();
        ITalendProcessJavaProject talendProcessJavaProject = service.getTalendProcessJavaProject();
        if (talendProcessJavaProject == null) {
            return null;
        }
        return talendProcessJavaProject.getSrcFolder();
    }

    /**
     * Copy route resource
     * 
     * @param model
     * @throws CoreException
     */
    public static IFile copyResources(ResourceDependencyModel model) {

        IFolder folder = getRouteResourceFolder();
        if (folder == null) {
            return null;
        }
        RouteResourceItem item = model.getItem();
        ByteArray content = null;
        EList<?> referenceResources = item.getReferenceResources();
        if (referenceResources.isEmpty()) {
            return null;
        }
        ReferenceFileItem refFile = (ReferenceFileItem) referenceResources.get(0);
        content = refFile.getContent();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getInnerContent());

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
                parentFolder.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
                classpathFile.create(inputStream, true, new NullProgressMonitor());
            } finally {
                inputStream.close();
            }
            return classpathFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public boolean isRouteBuilderNode(INode node) {
        return ComponentCategory.CATEGORY_4_CAMEL.getName().equals(node.getProcess().getComponentsType());
    }

    public boolean canCreateNodeOnLink(IConnection connection, INode node) {
        INodeConnector connector = node.getConnectorFromType(EConnectionType.ROUTE);
        if (connector.getMaxLinkOutput() > 0) {
            return true;
        }
        connector = node.getConnectorFromType(EConnectionType.ROUTE_ENDBLOCK);
        if (connector.getMaxLinkOutput() > 0) {
            return true;
        }
        return false;
    }

    public EConnectionType getTargetConnectionType(INode node) {
        INodeConnector connector = node.getConnectorFromType(EConnectionType.ROUTE);
        if (connector.getMaxLinkOutput() > 0) {
            return EConnectionType.ROUTE;
        }
        connector = node.getConnectorFromType(EConnectionType.ROUTE_ENDBLOCK);
        if (connector.getMaxLinkOutput() > 0) {
            return EConnectionType.ROUTE_ENDBLOCK;
        }
        return EConnectionType.ROUTE;
    }

    @Override
    public void appendRouteInfo2Doc(Item item, Element jobElement) {
        addSpringContent(item, jobElement);
        addManifestContent(item, jobElement);
        addResourcesContent(item, jobElement);
    }

    private void addResourcesContent(Item item, Element jobElement) {
        Element resourcesElement = jobElement.addElement("RouteResources");

        Set<ResourceDependencyModel> resourceDependencies = RouteResourceUtil.getResourceDependencies(item);
        for (ResourceDependencyModel resource : resourceDependencies) {
            Element resourceElement = resourcesElement.addElement("Resource");
            resourceElement.addAttribute("name", resource.getFileName());
            resourceElement.addAttribute("version", resource.getSelectedVersion());
            resourceElement.addAttribute("path", resource.getClassPathUrl());
        }
    }

    private void addManifestContent(Item item, Element jobElement) {
        Element manifestElement = jobElement.addElement("RouteManifest");
        manifestElement.addAttribute(QName.get("space", Namespace.XML_NAMESPACE), "preserve");

        OsgiDependenciesService resolver = OsgiDependenciesService.fromProcessItem((ProcessItem) item);
        manifestElement.addElement("Import-package").addText(getDependencyItems(resolver.getImportPackages()));
        manifestElement.addElement("Export-package").addText(getDependencyItems(resolver.getExportPackages()));
        manifestElement.addElement("Required-bundle").addText(getDependencyItems(resolver.getRequireBundles()));
        manifestElement.addElement("Bundle-classpath").addText(getDependencyItems(resolver.getBundleClasspaths()));
    }

    private static String getDependencyItems(Collection<? extends IDependencyItem> dependencyItems) {
        StringBuilder sb = new StringBuilder();
        for (IDependencyItem item : dependencyItems) {
            String text = item.toManifestString();
            if (null == text) {
                continue;
            }
            sb.append(text);
            sb.append("\n");
        }
        return sb.toString();
    }

    private void addSpringContent(Item item, Element jobElement) {
        Element routeSpringElement = jobElement.addElement("RouteSpring");
        routeSpringElement.addAttribute(QName.get("space", Namespace.XML_NAMESPACE), "preserve");
        String springContent = ((CamelProcessItem) item).getSpringContent();
        routeSpringElement.addText(springContent);
    }

    @Override
    public FileItem newRouteDocumentationItem() {
        return CamelPropertiesFactory.eINSTANCE.createRouteDocumentItem();
    }

}
