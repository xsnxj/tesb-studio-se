package org.talend.designer.esb.webservice.util;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.util.URI;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.repository.model.IProxyRepositoryFactory;

/**
 * Helper class to provide methods to handle Route Resources Item.
 */
public class RouteResourcesHelper {

	private static final String VERSION_ID_LATEST = "Latest";

	/**
	 * Gets the route resources location.
	 *
	 * @param repoItemId the repo item id
	 * @param version the version
	 * @return the route resources location with absolutely operation path.
	 */
	public static String getRouteResourcesLocation(String repoItemId, String version) {
		if(StringUtils.isEmpty(repoItemId) || StringUtils.isEmpty(version)) {
			return null;
		}
		IRepositoryViewObject repositoryObject = getRepositoryObject(repoItemId, version);
		if(repositoryObject == null) {
			return null;
		}
		Item item = repositoryObject.getProperty().getItem();
		if(item.getReferenceResources().isEmpty()) {
			return null;
		}
		ReferenceFileItem refFileItem = (ReferenceFileItem) item.getReferenceResources().get(0);
		URI uri = refFileItem.getContent().eResource().getURI();
		try {
			return FileLocator.toFileURL(new URL(uri.toString())).getFile();
		} catch (IOException e) {
			ExceptionHandler.process(e);
		}
		return null;
	}
	
	private static IRepositoryViewObject getRepositoryObject(String repoId, String repoVersion) {
		IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
		try {
			if(VERSION_ID_LATEST.equals(repoVersion)) {
					return factory.getLastVersion(repoId);
			}else {
				List<IRepositoryViewObject> allVersion = factory.getAllVersion(repoId);
				for (IRepositoryViewObject someVersion : allVersion) {
					if(someVersion.getVersion().equals(repoVersion)){
						return someVersion;
					}
				}
			}
		} catch (PersistenceException e) {
			ExceptionHandler.process(e);
		}
		return null;
	}
}
