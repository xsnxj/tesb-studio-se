package org.talend.repository.services.ui.action;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.utils.JobContextUtils;

public class ContextNodeRetriever {

	public static String[] getAllContext(IRepositoryNode node) {
		return getContextsMap(node).keySet().toArray(new String[0]);
	}

	public static Map<String, Map<String, String>> getContextsMap(IRepositoryNode node) {
		Item item = node.getObject().getProperty().getItem();
		if (item instanceof ServiceItem) {
			return getContextsMap((ServiceItem) item);
		} else if (item instanceof ProcessItem) {
			return JobContextUtils.getContextsMap((ProcessItem) item);
		}
		return Collections.emptyMap();
	}

	public static Map<String, Map<String, String>> getContextsMap(ServiceItem serviceItem) {
		Map<String, Map<String, String>> contextValues = new HashMap<String, Map<String, String>>();
		ServiceConnection connection = (ServiceConnection) serviceItem.getConnection();
		EList<ServicePort> servicePort = connection.getServicePort();
		for (ServicePort sp : servicePort) {
			EList<ServiceOperation> serviceOperation = sp.getServiceOperation();
			for (ServiceOperation so : serviceOperation) {
				String jobId = so.getReferenceJobId();
				if (jobId == null) {
					continue;
				}
				IRepositoryNode jobNode = RepositoryNodeUtilities.getRepositoryNode(jobId, false);
				if (jobNode == null) {
					continue;
				}
				ProcessItem processItem = (ProcessItem) jobNode.getObject().getProperty().getItem();
				contextValues.putAll(JobContextUtils.getContextsMap(processItem));
			}
		}
		return contextValues;
	}

}
