package org.talend.repository.services.service;

import java.io.IOException;
import java.util.Map;

import org.talend.repository.services.ui.scriptmanager.ServiceExportManager;

public interface IGeneratorService {
	public String generateFeature(final String serviceName,
			String serviceVersion, String groupId, Map<String, String> bundles,
			ServiceExportManager serviceManager) throws IOException;
}
