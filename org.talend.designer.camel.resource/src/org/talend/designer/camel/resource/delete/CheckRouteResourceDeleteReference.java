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
package org.talend.designer.camel.resource.delete;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EMap;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.ICheckDeleteItemReference;
import org.talend.core.repository.model.AbstractCheckDeleteItemReference;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.ItemReferenceBean;
import org.talend.repository.ui.actions.DeleteActionCache;

/**
 * @author xpli
 * 
 */
public class CheckRouteResourceDeleteReference extends
		AbstractCheckDeleteItemReference implements ICheckDeleteItemReference {

	@Override
	protected Set<ItemReferenceBean> checkItemReferenceBeans(
			IProxyRepositoryFactory factory,
			DeleteActionCache deleteActionCache, IRepositoryNode currentJobNode) {
		IRepositoryViewObject object = currentJobNode.getObject();
		Item nodeItem = object.getProperty().getItem();
		Set<ItemReferenceBean> list = new HashSet<ItemReferenceBean>();
		List<IRepositoryViewObject> allRoutes;
		try {
			allRoutes = factory
					.getAll(CamelRepositoryNodeType.repositoryRoutesType);
			if (allRoutes == null || allRoutes.isEmpty()) {
				return list;
			}
			for (IRepositoryViewObject obj : allRoutes) {
				Property property = obj.getProperty();
				Item routeItem = property.getItem();
				EMap additionalProperties = property.getAdditionalProperties();
				if (additionalProperties != null) {
					Object resourcesObj = additionalProperties
							.get("ROUTE_RESOURCES_PROP");
					if (resourcesObj != null) {
						String[] resourceIds = resourcesObj.toString().split(
								",");
						for (String id : resourceIds) {
							if (nodeItem.getProperty().getId().equals(id)) {
								ItemReferenceBean bean = new ItemReferenceBean();
								bean.setItemName(object.getProperty()
										.getLabel());
								bean.setItemVersion(object.getProperty()
										.getVersion());
								bean.setItemType(CamelRepositoryNodeType.repositoryRouteResourceType);
								bean.setItemDeleted(object.isDeleted());
								bean.setReferenceItemName(property.getLabel());
								bean.setReferenceItemVersion(property
										.getVersion());
								bean.setReferenceItemType(CamelRepositoryNodeType.repositoryRoutesType);
								bean.setReferenceItemPath(routeItem.getState()
										.getPath());
								bean.setReferenceProjectName(obj
										.getProjectLabel());
								bean.setReferenceItemDeleted(obj.isDeleted());
								boolean found = false;
								for (ItemReferenceBean b : list) {
									if (b.equals(bean)) {
										found = true;
										b.addNodeNum();
										break;
									}
								}
								if (!found) {
									list.add(bean);
								}
							}
						}

					}
				}
			}
		} catch (PersistenceException e1) {
			e1.printStackTrace();
		}

		return list;
	}

}
