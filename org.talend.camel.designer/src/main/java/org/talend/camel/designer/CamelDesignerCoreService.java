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

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.designer.ui.CreateCamelProcess;
import org.talend.camel.designer.ui.bean.CreateCamelBean;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
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
