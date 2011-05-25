// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.core.model.camelProperties.CamelPropertiesFactory;
import org.talend.camel.core.model.camelProperties.CamelPropertiesPackage;
import org.talend.camel.designer.ui.CreateCamelProcess;
import org.talend.camel.designer.ui.bean.CreateCamelBean;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.FileItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.designer.codegen.ITalendSynchronizer;
import org.talend.designer.core.ICamelDesignerCoreService;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelDesignerCoreService implements ICamelDesignerCoreService {

    private XmiResourceManager xmiResourceManager = new XmiResourceManager();

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.core.ICamelDesignerCoreService#getCreateProcessAction(boolean)
     */
    public IAction getCreateProcessAction(boolean isToolbar) {
        return new CreateCamelProcess(isToolbar);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.core.ICamelDesignerCoreService#getCreateBeanAction(boolean)
     */
    public IAction getCreateBeanAction(boolean isToolbar) {
        // TODO Auto-generated method stub
        return new CreateCamelBean(isToolbar);
    }

    public IImage getCamelIcon(ERepositoryObjectType type) {
        if (type == CamelRepositoryNodeType.repositoryRoutesType) {
            return ECoreImage.PROCESS_ICON;
        } else if (type == CamelRepositoryNodeType.repositoryBeansType) {
            return ECoreImage.ROUTINE_ICON;
        }
        return null;
    }

    public ERepositoryObjectType createCamelResource(Item item) {
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

    public Resource createCamel(IProject project, Item item, IPath path, ERepositoryObjectType type) throws PersistenceException {
        Resource itemResource = null;
        EClass eClass = item.eClass();
        if (eClass.eContainer() == CamelPropertiesPackage.eINSTANCE) {
            switch (eClass.getClassifierID()) {
            case CamelPropertiesPackage.CAMEL_PROCESS_ITEM:
                type = CamelRepositoryNodeType.repositoryRoutesType;
                itemResource = create(project, (CamelProcessItem) item, path, type);
                return itemResource;
            case CamelPropertiesPackage.BEAN_ITEM:
                type = CamelRepositoryNodeType.repositoryBeansType;
                itemResource = create(project, (BeanItem) item, path, type);
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

    public Resource saveCamel(Item item) throws PersistenceException {
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

    public Item createNewCamelItem(ERepositoryObjectType type) {
        Item item = null;
        if (type == CamelRepositoryNodeType.repositoryRoutesType) {
            item = CamelPropertiesFactory.eINSTANCE.createCamelProcessItem();
        } else if (type == CamelRepositoryNodeType.repositoryBeansType) {
            item = CamelPropertiesFactory.eINSTANCE.createBeanItem();
        }
        return item;
    }

    public ITalendSynchronizer createCamelJavaSynchronizer() {
        return new CamelJavaRoutesSychronizer();
    }

    public ITalendSynchronizer createCamelPerlSynchronizer() {
        return new CamelPerlBeanSychronizer();
    }

    public boolean isCamelRepObjType(ERepositoryObjectType type) {
        boolean isCamelType = false;
        if (type == CamelRepositoryNodeType.repositoryRoutesType || type == CamelRepositoryNodeType.repositoryBeansType) {
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
}
