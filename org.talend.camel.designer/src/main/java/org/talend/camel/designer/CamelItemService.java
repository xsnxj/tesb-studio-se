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

import org.eclipse.emf.ecore.EObject;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.core.model.camelProperties.util.CamelPropertiesSwitch;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.core.ICamelItemService;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelItemService implements ICamelItemService {

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ICamelItemService#getCamelRepObjType(org.talend.core.Item)
     */
    public ERepositoryObjectType getCamelRepObjType(Item item) {
        return (ERepositoryObjectType) new CamelPropertiesSwitch<Object>() {

            public Object caseBeanItem(BeanItem item) {
                return CamelRepositoryNodeType.repositoryBeansType;
            }

            public Object caseCamelProcessItem(CamelProcessItem item) {
                return CamelRepositoryNodeType.repositoryRoutesType;
            }

            public Object defaultCase(EObject object) {
                return null;
            }
        }.doSwitch(item);
    }

}
