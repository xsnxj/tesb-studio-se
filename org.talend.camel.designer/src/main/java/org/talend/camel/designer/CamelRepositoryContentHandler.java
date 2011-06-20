// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.core.model.camelProperties.CamelPropertiesFactory;
import org.talend.camel.core.model.camelProperties.CamelPropertiesPackage;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.FileItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryContentHandler;
import org.talend.core.repository.utils.XmiResourceManager;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelRepositoryContentHandler implements IRepositoryContentHandler {

    private XmiResourceManager xmiResourceManager = new XmiResourceManager();

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.repository.IRepositoryContentHandler#createResource(org.talend.core.model.properties.Item)
     */
    public ERepositoryObjectType createResource(Item item) {

        EClass eClass = item.eClass();
        if (eClass.eContainer() == CamelPropertiesPackage.eINSTANCE) {
            switch (eClass.getClassifierID()) {
            case CamelPropertiesPackage.CAMEL_PROCESS_ITEM:
                return CamelRepositoryNodeType.repositoryRoutesType;
            case CamelPropertiesPackage.BEAN_ITEM:
                return CamelRepositoryNodeType.repositoryBeansType;
            default:
                throw new UnsupportedOperationException();
            }
        }
        return null;
    }

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
        switch (classifierID) {
        case CamelPropertiesPackage.CAMEL_PROCESS_ITEM:
            type = CamelRepositoryNodeType.repositoryRoutesType;
            itemResource = create(project, (CamelProcessItem) item, path, type);
            return itemResource;
        case CamelPropertiesPackage.BEAN_ITEM:
            type = CamelRepositoryNodeType.repositoryBeansType;
            itemResource = create(project, (FileItem) item, path, type);
            return itemResource;
        default:
            throw new UnsupportedOperationException();
        }
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
        EClass eClass = item.eClass();
        if (eClass.eContainer() == CamelPropertiesPackage.eINSTANCE) {
            switch (eClass.getClassifierID()) {
            case CamelPropertiesPackage.CAMEL_PROCESS_ITEM:
                itemResource = save((CamelProcessItem) item);
                return itemResource;
            case CamelPropertiesPackage.BEAN_ITEM:
                itemResource = save((BeanItem) item);
                return itemResource;
            default:
                throw new UnsupportedOperationException();
            }
        } else {
            if (itemResource == null) {
                throw new UnsupportedOperationException();
            }
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

}
