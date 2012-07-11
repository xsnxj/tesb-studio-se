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
package org.talend.designer.camel.resource.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.resource.core.util.RouteResourceUtil;

/**
 * @author xpli
 *
 */
public class ResourceDependencyModel {

	private RouteResourceItem item;

	private String selectedVersion = RouteResourceUtil.LATEST_VERSION;

	private List<String> versions;

	public ResourceDependencyModel(RouteResourceItem item) {
		this.item = item;
	}

	/**
	 * @return the item
	 */
	public RouteResourceItem getItem() {
		return item;
	}

	/**
	 * @return the selectedVersion
	 */
	public String getSelectedVersion() {
		return selectedVersion;
	}

	/**
	 * @return the versions
	 */
	public List<String> getVersions() {
		if (versions == null) {
			versions = new ArrayList<String>();
			try {
				List<IRepositoryViewObject> allVersions = ProxyRepositoryFactory
						.getInstance()
						.getAllVersion(item.getProperty().getId());
				for (IRepositoryViewObject obj : allVersions) {
					versions.add(obj.getVersion());
				}
			} catch (PersistenceException e) {
			}
			versions.add(RouteResourceUtil.LATEST_VERSION);
			Collections.sort(versions);
		}
		return versions;
	}

	/**
	 * @param item
	 *            the item to set
	 */
	public void setItem(RouteResourceItem item) {
		this.item = item;
	}

	/**
	 * @param selectedVersion
	 *            the selectedVersion to set
	 */
	public void setSelectedVersion(String selectedVersion) {
		this.selectedVersion = selectedVersion;
	}

}
