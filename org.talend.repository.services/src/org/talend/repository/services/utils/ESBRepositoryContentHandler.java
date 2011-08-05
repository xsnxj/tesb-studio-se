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
package org.talend.repository.services.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryContentHandler;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServicesFactory;
import org.talend.repository.services.model.services.ServicesPackage;

/**
 * DOC hwang class global comment. Detailled comment
 */
public class ESBRepositoryContentHandler implements IRepositoryContentHandler {

    private XmiResourceManager xmiResourceManager = new XmiResourceManager();

    public Item createNewItem(ERepositoryObjectType type) {
        Item item = ServicesFactory.eINSTANCE.createServiceItem();
        return item;
    }

    public Resource create(IProject project, Item item, int classifierID, IPath path) throws PersistenceException {
        Resource itemResource = null;
        ERepositoryObjectType type;
        switch (classifierID) {
        case ServicesPackage.SERVICE_ITEM:
            type = ESBRepositoryNodeType.SERVICES;
            itemResource = create(project, (ServiceItem) item, path, type);
            return itemResource;
        default:
            return null;
        }
    }

    private Resource create(IProject project, ServiceItem item, IPath path, ERepositoryObjectType type)
            throws PersistenceException {
        Resource itemResource = xmiResourceManager.createItemResource(project, item, path, type, false);
        itemResource.getContents().add(item.getProperty());

        return itemResource;
    }

    public Resource save(Item item) throws PersistenceException {
        Resource itemResource = null;
        EClass eClass = item.eClass();
        if (eClass.eContainer() == ServicesPackage.eINSTANCE) {
            switch (eClass.getClassifierID()) {
            case ServicesPackage.SERVICE_ITEM:
                itemResource = save((ServiceItem) item);
                return itemResource;
            default:
                return null;
            }
        }
        return null;
    }

    private Resource save(ServiceItem item) {
        Resource itemResource = xmiResourceManager.getItemResource(item);
        itemResource.getContents().clear();
        itemResource.getContents().add(item.getProperty());
        return itemResource;
    }

    public IImage getIcon(ERepositoryObjectType type) {
        if (type == ESBRepositoryNodeType.SERVICES) {
            return ESBImage.SERVICE_ICON;
        }
        return null;
    }

    public boolean isRepObjType(ERepositoryObjectType type) {
        boolean isESBType = false;
        if (type == ESBRepositoryNodeType.SERVICES) {
            isESBType = true;
        }
        return isESBType;
    }

    public ERepositoryObjectType getRepositoryObjectType(Item item) {
        if (item == null) {
            return ESBRepositoryNodeType.SERVICES;
        }
        EClass eClass = item.eClass();
        if (eClass.eContainer() == ServicesPackage.eINSTANCE) {
            switch (eClass.getClassifierID()) {
            case ServicesPackage.SERVICE_ITEM:
                return ESBRepositoryNodeType.SERVICES;
            default:
                return null;
            }
        }
        return null;
    }

    public RepositoryNode createRepositoryNode(RepositoryNode node) {
        RepositoryNode serviceNode = new RepositoryNode(null, node, ENodeType.SYSTEM_FOLDER);
        serviceNode.setProperties(EProperties.LABEL, ESBRepositoryNodeType.SERVICES);
        serviceNode.setProperties(EProperties.CONTENT_TYPE, ESBRepositoryNodeType.SERVICES);
        return serviceNode;
    }

    public boolean isProcess(Item item) {
        // TODO Auto-generated method stub
        return false;
    }

    public ERepositoryObjectType getProcessType() {
        // TODO Auto-generated method stub
        return null;
    }

    public ERepositoryObjectType getCodeType() {
        // TODO Auto-generated method stub
        return null;
    }

}
