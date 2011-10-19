package org.talend.repository.services.ui.action;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
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
		Set<String> contexts = new HashSet<String>();
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
						EList context = process.getContext();
						if (context == null) {
							continue;
						}
						Iterator iterator = context.iterator();
						while (iterator.hasNext()) {
							Object next = iterator.next();
							if (!(next instanceof ContextType))
								continue;
							ContextType ct = (ContextType) next;
							String name = ct.getName();
							contexts.add(name);
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
							contexts.add(name);
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contexts.toArray(new String[0]);
	}
}
