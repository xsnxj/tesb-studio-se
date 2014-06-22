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

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.core.model.camelProperties.CamelPropertiesFactory;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.FileItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryContentHandler;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelRepositoryContentHandler implements IRepositoryContentHandler {

    private XmiResourceManager xmiResourceManager = new XmiResourceManager();

    public boolean isProcess(Item item) {
        if (item instanceof CamelProcessItem) {
            return true;
        }
        return false;
    }

    public boolean isRepObjType(ERepositoryObjectType type) {
        boolean isCamelType = false;
        if (type == CamelRepositoryNodeType.repositoryRoutesType || type == CamelRepositoryNodeType.repositoryBeansType) {
            isCamelType = true;
        }
        return isCamelType;
    }

    public ERepositoryObjectType getProcessType() {
        return CamelRepositoryNodeType.repositoryRoutesType;
    }

    public ERepositoryObjectType getCodeType() {
        return CamelRepositoryNodeType.repositoryBeansType;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.repository.IRepositoryContentHandler#create(org.eclipse.core.resources.IProject,
     * org.talend.core.model.properties.Item, int, org.eclipse.core.runtime.IPath)
     */
    public Resource create(IProject project, Item item, int classifierID, IPath path) throws PersistenceException {

        Resource itemResource = null;
        ERepositoryObjectType type;
        if (item instanceof CamelProcessItem) {
            type = CamelRepositoryNodeType.repositoryRoutesType;
            itemResource = create(project, (CamelProcessItem) item, path, type);
            Resource screenshotsResource = createScreenshotResource(project, (CamelProcessItem) item, path, type);
            xmiResourceManager.saveResource(screenshotsResource);
            return itemResource;
        }
        if (item instanceof BeanItem) {
            type = CamelRepositoryNodeType.repositoryBeansType;
            itemResource = create(project, (FileItem) item, path, type);
            return itemResource;
        }
        return null;
    }

    // TODO refer to LocalRepositoryFactory
    private Resource createScreenshotResource(IProject project, Item item, IPath path, ERepositoryObjectType type)
            throws PersistenceException {
        Resource itemResource = xmiResourceManager.createScreenshotResource(project, item, path, type, false);
        itemResource.getContents().addAll(((CamelProcessItem) item).getProcess().getScreenshots());

        return itemResource;
    }

    private Resource create(IProject project, CamelProcessItem item, IPath path, ERepositoryObjectType type)
            throws PersistenceException {
        Resource itemResource = xmiResourceManager.createItemResource(project, item, path, type, false);
        itemResource.getContents().add(item.getProcess());

        return itemResource;
    }

    private Resource create(IProject project, FileItem item, IPath path, ERepositoryObjectType type) throws PersistenceException {
        Resource itemResource = xmiResourceManager.createItemResource(project, item, path, type, true);
        itemResource.getContents().add(item.getContent());
        return itemResource;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.repository.IRepositoryContentHandler#save(org.talend.core.model.properties.Item)
     */
    public Resource save(Item item) throws PersistenceException {
        Resource itemResource = null;
        if (item instanceof CamelProcessItem) {
            itemResource = save((CamelProcessItem) item);
            return itemResource;
        }
        if (item instanceof BeanItem) {
            itemResource = save((BeanItem) item);
            return itemResource;
        }
        return null;
    }

    private Resource save(CamelProcessItem item) {
        Resource itemResource = xmiResourceManager.getItemResource(item);
        itemResource.getContents().clear();
        itemResource.getContents().add(item.getProcess());
        return itemResource;
    }

    private Resource save(FileItem item) {
        Resource itemResource = xmiResourceManager.getItemResource(item);

        ByteArray content = item.getContent();
        itemResource.getContents().clear();
        itemResource.getContents().add(content);

        return itemResource;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.talend.core.repository.IRepositoryContentHandler#getIcon(org.talend.core.model.repository.ERepositoryObjectType
     * )
     */
    public IImage getIcon(ERepositoryObjectType type) {
        if (type == CamelRepositoryNodeType.repositoryRoutesType) {
            return ECamelCoreImage.ROUTES_ICON;
        } else if (type == CamelRepositoryNodeType.repositoryBeansType) {
            return ECamelCoreImage.BEAN_ICON;
        }
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.repository.IRepositoryContentHandler#createNewItem(org.talend.core.model.repository.
     * ERepositoryObjectType)
     */
    public Item createNewItem(ERepositoryObjectType type) {
        Item item = null;
        if (type == CamelRepositoryNodeType.repositoryRoutesType) {
            item = CamelPropertiesFactory.eINSTANCE.createCamelProcessItem();
        } else if (type == CamelRepositoryNodeType.repositoryBeansType) {
            item = CamelPropertiesFactory.eINSTANCE.createBeanItem();
        }
        return item;
    }

    public ERepositoryObjectType getRepositoryObjectType(Item item) {
        if (item instanceof CamelProcessItem) {
            return CamelRepositoryNodeType.repositoryRoutesType;
        }
        if (item instanceof BeanItem) {
            return CamelRepositoryNodeType.repositoryBeansType;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.repository.IRepositoryContentHandler#addNode(org.talend.core.model.repository.
     * ERepositoryObjectType, org.talend.repository.model.RepositoryNode,
     * org.talend.core.model.repository.IRepositoryViewObject, org.talend.repository.model.RepositoryNode)
     */
    public void addNode(ERepositoryObjectType type, RepositoryNode recBinNode, IRepositoryViewObject repositoryObject,
            RepositoryNode node) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.repository.IRepositoryContentHandler#addContents(java.util.Collection,
     * org.eclipse.emf.ecore.resource.Resource)
     */
    public void addContents(Collection<EObject> collection, Resource resource) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.repository.IRepositoryContentHandler#getIcon(org.talend.core.model.properties.Item)
     */
    public IImage getIcon(Item item) {
        // TODO Auto-generated method stub
        return null;
    }

}
