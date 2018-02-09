// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.ItemState;
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

	private String classPathUrl;

	private Set<String> refNodes = new HashSet<String>();

	/**
	 * Built in type can not be deleted.
	 */
	private boolean isBuiltIn;

	public ResourceDependencyModel(RouteResourceItem item) {
		this.item = item;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ResourceDependencyModel) {
			ResourceDependencyModel model = (ResourceDependencyModel) obj;
			return model.getItem().getProperty().getId()
					.equals(this.item.getProperty().getId())
					&& model.getSelectedVersion().equals(this.selectedVersion);
		}
		return super.equals(obj);
	}

	/**
	 * @return the classPathUrl
	 */
	public String getClassPathUrl() {
		ItemState state = item.getState();
		String path = state.getPath();
		if (path != null && !path.isEmpty()) {
			classPathUrl = path + "/" + getFileName();
		} else {
			classPathUrl = getFileName();
		}
		return classPathUrl;
	}

	/**
	 * Get file name
	 * 
	 * @return
	 */
	public String getFileName() {

		String label = item.getProperty().getLabel();

		if (RouteResourceUtil.LATEST_VERSION.equals(selectedVersion)) {
			return label;
		}

		IPath path = new Path(label);
		String fileExtension = path.getFileExtension();
		String fileName = path.removeFileExtension().toPortableString();
		fileName = fileName + "_" + selectedVersion;
		if (fileExtension != null && !fileExtension.isEmpty()) {
			fileName = fileName + "." + fileExtension;
		}

		return fileName;
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

	@Override
	public int hashCode() {
		if (item != null && selectedVersion != null) {
			return item.getProperty().getId().hashCode() * 31
					+ selectedVersion.hashCode();
		}
		return super.hashCode();
	}

	/**
	 * @return the isBuiltIn
	 */
	public boolean isBuiltIn() {
		return isBuiltIn;
	}

	/**
	 * @param isBuiltIn
	 *            the isBuiltIn to set
	 */
	public void setBuiltIn(boolean isBuiltIn) {
		this.isBuiltIn = isBuiltIn;
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

	/**
	 * @param refNodes
	 *            the refNodes to set
	 */
	public void setRefNodes(Set<String> refNodes) {
		this.refNodes = refNodes;
	}

	/**
	 * @return the refNodes
	 */
	public Set<String> getRefNodes() {
		return refNodes;
	}

}
