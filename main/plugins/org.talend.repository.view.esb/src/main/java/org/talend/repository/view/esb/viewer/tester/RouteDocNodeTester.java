package org.talend.repository.view.esb.viewer.tester;

import org.eclipse.core.expressions.PropertyTester;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.repository.model.RepositoryNode;

public class RouteDocNodeTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if(!(receiver instanceof RepositoryNode)){
			return false;
		}
		ERepositoryObjectType contentType = ((RepositoryNode)receiver).getContentType();
		if(!CamelRepositoryNodeType.repositoryDocumentationsType.equals(contentType) 
				&& !CamelRepositoryNodeType.repositoryDocumentationType.equals(contentType)){
			return false;
		}
		if(contentType != null)
			System.out.println(contentType.getFolder()+"   =========");
		if("isRouteDoc".equals(property)){
			return true;
		}
		return false;
	}

}
