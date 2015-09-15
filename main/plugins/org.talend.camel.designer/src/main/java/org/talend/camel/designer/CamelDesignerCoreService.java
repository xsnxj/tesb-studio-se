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
package org.talend.camel.designer;

import java.util.Collection;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.core.model.camelProperties.CamelPropertiesFactory;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.camel.designer.ui.CreateCamelProcess;
import org.talend.camel.designer.ui.bean.CreateCamelBean;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.model.CamelRepositoryNodeType;
import org.talend.core.model.components.ComponentCategory;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.INodeConnector;
import org.talend.core.model.properties.FileItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.designer.camel.dependencies.core.DependenciesResolver;
import org.talend.designer.camel.dependencies.core.model.ManifestItem;
import org.talend.designer.camel.resource.core.model.ResourceDependencyModel;
import org.talend.designer.camel.resource.core.util.RouteResourceUtil;
import org.talend.designer.core.ICamelDesignerCoreService;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelDesignerCoreService implements ICamelDesignerCoreService {

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.core.ICamelDesignerCoreService#getCreateProcessAction (boolean)
     */
    public IAction getCreateProcessAction(boolean isToolbar) {
        return new CreateCamelProcess(isToolbar);
    }

    public String getDeleteFolderName(ERepositoryObjectType type) {
        return type.getKey();
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

    public boolean isCamelMulitPageEditor(IEditorPart editor) {
        boolean isCamelEditor = false;
        if (editor instanceof CamelMultiPageTalendEditor) {
            isCamelEditor = true;
        }
        return isCamelEditor;
    }

    public Collection<IPath> synchronizeRouteResource(Item item) {
        return RouteResourceUtil.synchronizeRouteResource(item);
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

        for (ResourceDependencyModel resource : RouteResourceUtil.getResourceDependencies(item)) {
            Element resourceElement = resourcesElement.addElement("Resource");
            resourceElement.addAttribute("name", resource.getFileName());
            resourceElement.addAttribute("version", resource.getSelectedVersion());
            resourceElement.addAttribute("path", resource.getClassPathUrl());
        }
    }

    private void addManifestContent(Item item, Element jobElement) {
        Element manifestElement = jobElement.addElement("RouteManifest");
        manifestElement.addAttribute(QName.get("space", Namespace.XML_NAMESPACE), "preserve");

        final DependenciesResolver resolver = new DependenciesResolver((ProcessItem) item);
        manifestElement.addElement(ManifestItem.IMPORT_PACKAGE).addText(resolver.getManifestImportPackage('\n'));
        manifestElement.addElement(ManifestItem.EXPORT_PACKAGE).addText(resolver.getManifestExportPackage('\n'));
        manifestElement.addElement(ManifestItem.REQUIRE_BUNDLE).addText(resolver.getManifestRequireBundle('\n'));
        manifestElement.addElement(ManifestItem.BUNDLE_CLASSPATH).addText(resolver.getManifestBundleClasspath('\n'));
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
