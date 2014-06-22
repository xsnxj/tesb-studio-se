package org.talend.repository.services.ui.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.designer.core.model.utils.emf.talendfile.ContextParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;

public class ContextNodeRetriever {

	public static String[] getAllContext(RepositoryNode node) {
		return getContextsMap(node).keySet().toArray(new String[0]);
	}

	public static Map<String, Map<String, String>> getContextsMap(
			RepositoryNode node) {
		Map <String, Map<String, String>> contextValues = new HashMap<String, Map<String, String>>();
		try {
			Item item = node.getObject().getProperty().getItem();
			if (item instanceof ServiceItem) {
				ServiceItem serviceItem = (ServiceItem) node.getObject()
						.getProperty().getItem();
				ServiceConnection connection = (ServiceConnection) serviceItem
						.getConnection();
				EList<ServicePort> servicePort = connection.getServicePort();
				for (ServicePort sp : servicePort) {
					EList<ServiceOperation> serviceOperation = sp
							.getServiceOperation();
					for (ServiceOperation so : serviceOperation) {
						String jobId = so.getReferenceJobId();
						if (jobId == null) {
							continue;
						}
						RepositoryNode jobNode = RepositoryNodeUtilities
								.getRepositoryNode(jobId, false);
						if (jobNode == null) {
							continue;
						}
						ProcessItem processItem = (ProcessItem) jobNode
								.getObject().getProperty().getItem();
						ProcessType process = processItem.getProcess();
						if (process == null) {
							continue;
						}
						EList contexts = process.getContext();
						if (contexts == null) {
							continue;
						}
						Iterator iterator = contexts.iterator();
						while (iterator.hasNext()) {
							Object next = iterator.next();
							if (!(next instanceof ContextType)) {
								continue;
							}
							ContextType ct = (ContextType) next;
							String name = ct.getName();
							Map<String, String> contextParams = contextValues.get(name); 
							if (contextParams == null)	{
								contextParams = new HashMap<String, String>();
							}
							contextValues.put(name, contextParams);
							EList<ContextParameterType> params = ct.getContextParameter();
							for (ContextParameterType param : params) {
								contextParams.put(param.getName(), param.getValue());
							}
						}
					}
				}
			} else if (item instanceof ProcessItem) {
				ProcessType process = ((ProcessItem) item).getProcess();
				if (process != null) {
					EList context = process.getContext();
					if (context != null) {
						Iterator iterator = context.iterator();
						while (iterator.hasNext()) {
							Object next = iterator.next();
							if (!(next instanceof ContextType))
								continue;
							ContextType ct = (ContextType) next;
							String name = ct.getName();
							HashMap<String, String> contextParams = new HashMap<String, String>();
							contextValues.put(name, contextParams);
							EList<ContextParameterType> params = ct.getContextParameter();
							for (ContextParameterType param : params) {
								contextParams.put(param.getName(), param.getValue());
							}
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contextValues;
	}
}
