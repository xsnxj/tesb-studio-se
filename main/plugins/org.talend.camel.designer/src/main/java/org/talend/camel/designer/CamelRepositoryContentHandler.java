// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.core.model.camelProperties.CamelPropertiesFactory;
import org.talend.camel.core.model.camelProperties.CamelPropertiesPackage;
import org.talend.camel.core.model.camelProperties.RouteDocumentItem;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.FileItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Status;
import org.talend.core.model.repository.AbstractRepositoryContentHandler;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.core.runtime.CoreRuntimePlugin;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelRepositoryContentHandler extends AbstractRepositoryContentHandler {

    private XmiResourceManager xmiResourceManager = new XmiResourceManager();

    @Override
    public List<Status> getPropertyStatus(Item item) {
    	if(item.eClass() == CamelPropertiesPackage.Literals.ROUTE_RESOURCE_ITEM){
    		 try {
                 return CoreRuntimePlugin.getInstance().getProxyRepositoryFactory().getTechnicalStatus();
             } catch (PersistenceException e) {
                 ExceptionHandler.process(e);
             }
    	}
    	return super.getPropertyStatus(item);
    }
    
    @Override
    public boolean isProcess(Item item) {
        if (item.eClass() == CamelPropertiesPackage.Literals.CAMEL_PROCESS_ITEM) {
            return true;
        }
        return false;
    }

    public boolean isRepObjType(ERepositoryObjectType type) {
    	for(ERepositoryObjectType tmp: CamelRepositoryNodeType.AllRouteRespositoryTypes.keySet()){
			if(type == tmp){
				return true;
			}
		}
		return false;
    }

    @Override
    public ERepositoryObjectType getProcessType() {
        return CamelRepositoryNodeType.repositoryRoutesType;
    }

    @Override
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
        if (item.eClass() == CamelPropertiesPackage.Literals.CAMEL_PROCESS_ITEM) {
            type = CamelRepositoryNodeType.repositoryRoutesType;
            itemResource = create(project, (CamelProcessItem) item, path, type);
            Resource screenshotsResource = createScreenshotResource(project, item, path, type);
            xmiResourceManager.saveResource(screenshotsResource);
            return itemResource;
        }
        if (item.eClass() == CamelPropertiesPackage.Literals.BEAN_ITEM) {
            type = CamelRepositoryNodeType.repositoryBeansType;
            itemResource = create(project, (FileItem) item, path, type);
            return itemResource;
        }
        if (item.eClass() == CamelPropertiesPackage.Literals.ROUTE_RESOURCE_ITEM) {
            type = CamelRepositoryNodeType.repositoryRouteResourceType;
            itemResource = create(project, (FileItem) item, path, type);
            return itemResource;
        }
        if(item.eClass() == CamelPropertiesPackage.Literals.ROUTE_DOCUMENT_ITEM){
        	type = CamelRepositoryNodeType.repositoryDocumentationType;
        	itemResource = create(project, (FileItem)item, path, type);
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
        if (item.eClass() == CamelPropertiesPackage.Literals.CAMEL_PROCESS_ITEM) {
            itemResource = saveRoute((CamelProcessItem) item);
            return itemResource;
        }
        if (item.eClass() == CamelPropertiesPackage.Literals.BEAN_ITEM) {
            itemResource = saveFile((BeanItem) item);
            return itemResource;
        }
        if (item.eClass() == CamelPropertiesPackage.Literals.ROUTE_RESOURCE_ITEM) {
            itemResource = saveFile((RouteResourceItem) item);
            return itemResource;
        }
        if(item.eClass() == CamelPropertiesPackage.Literals.ROUTE_DOCUMENT_ITEM) {
        	itemResource = saveFile((RouteDocumentItem)item);
        	return itemResource;
        }
        return null;
    }

    private Resource saveRoute(CamelProcessItem item) {
        Resource itemResource = xmiResourceManager.getItemResource(item);
        itemResource.getContents().clear();
        itemResource.getContents().add(item.getProcess());
        return itemResource;
    }

    @Override
    public Resource saveScreenShots(Item item) throws PersistenceException {
    	if(!(item instanceof CamelProcessItem)){
    		return null;
    	}
        Resource itemResource = xmiResourceManager.getScreenshotResource(item, true, true);
        EMap screenshots =  ((CamelProcessItem) item).getProcess().getScreenshots();
        if (screenshots != null && !screenshots.isEmpty()) {
            itemResource.getContents().clear();
            itemResource.getContents().addAll(EcoreUtil.copyAll(screenshots));
        }
        return itemResource;
    }
    
    private Resource saveFile(FileItem item) {
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
    @Override
    public IImage getIcon(ERepositoryObjectType type) {
        if (type == CamelRepositoryNodeType.repositoryRoutesType) {
            return ECamelCoreImage.ROUTES_ICON;
        } else if (type == CamelRepositoryNodeType.repositoryBeansType) {
            return ECamelCoreImage.BEAN_ICON;
        } else if (type == CamelRepositoryNodeType.repositoryRouteResourceType) {
            return ECamelCoreImage.ROUTE_RESOURCE_ICON;
        } else if (type == CamelRepositoryNodeType.repositoryDocumentationType) {
        	return ECoreImage.DOCUMENTATION_ICON;
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
        } else if (type == CamelRepositoryNodeType.repositoryRouteResourceType) {
            item = CamelPropertiesFactory.eINSTANCE.createRouteResourceItem();
        } else if (type == CamelRepositoryNodeType.repositoryDocumentationType) {
        	item = CamelPropertiesFactory.eINSTANCE.createRouteDocumentItem();
        }
        return item;
    }

    public ERepositoryObjectType getRepositoryObjectType(Item item) {
        if (item.eClass() == CamelPropertiesPackage.Literals.CAMEL_PROCESS_ITEM) {
            return CamelRepositoryNodeType.repositoryRoutesType;
        }
        if (item.eClass() == CamelPropertiesPackage.Literals.BEAN_ITEM) {
            return CamelRepositoryNodeType.repositoryBeansType;
        }
        if (item.eClass() == CamelPropertiesPackage.Literals.ROUTE_RESOURCE_ITEM) {
            return CamelRepositoryNodeType.repositoryRouteResourceType;
        }
        if (item.eClass() == CamelPropertiesPackage.Literals.ROUTE_DOCUMENT_ITEM) {
        	return CamelRepositoryNodeType.repositoryDocumentationType;
        }
        return null;
    }

}
