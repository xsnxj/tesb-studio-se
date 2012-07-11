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
package org.talend.camel.designer.check;

import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.process.Element;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.Problem.ProblemStatus;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.ICheckNodesService;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.views.problems.Problems;

/**
 * @author xpli
 * 
 */
public class CheckCXFNodesService implements ICheckNodesService {

	/**
	 * 
	 */
	public CheckCXFNodesService() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.talend.designer.core.ICheckNodesService#checkNode(org.talend.designer
	 * .core.ui.editor.nodes.Node)
	 */
	public void checkNode(Node node) {
		if (!node.getComponent().getName().equals("cCXF")) {
			return;
		}
		IElementParameter resourceParam = node
				.getElementParameter(EParameterName.ROUTE_RESOURCE_TYPE_ID
						.getName());
		IElementParameter wsdlFileParam = node.getElementParameter("WSDL_FILE");
		IElementParameter serviceParam = node
				.getElementParameter("SERVICE_TYPE");
		IElementParameter wsdlTypeParam = node.getElementParameter("WSDL_TYPE");
		IElementParameter clazzParam = node
				.getElementParameter("SERVICE_CLASS");

		// Select WSDL
		if (serviceParam != null && "wsdlURL".equals(serviceParam.getValue())) {
			// Select File
			if (wsdlTypeParam != null
					&& "file".equals(wsdlTypeParam.getValue())) {
				// WSDL file is empty
				if (wsdlFileParam == null || wsdlFileParam.getValue() == null
						|| wsdlFileParam.getValue().toString().isEmpty()
						|| wsdlFileParam.getValue().toString().equals("\"\"")) {
					String errorMessage = "Parameter ("
							+ wsdlFileParam.getDisplayName()
							+ ") is empty but is required.";
					Problems.add(ProblemStatus.ERROR, (Element) node,
							errorMessage);
				}
			} // Select Repository
			else if (wsdlTypeParam != null
					&& "repo".equals(wsdlTypeParam.getValue())) {
				// WSDL file is empty
				String errorMessage = "";
				if (resourceParam == null || resourceParam.getValue() == null
						|| resourceParam.getValue().toString().isEmpty()) {
					errorMessage = "Parameter ("
							+ wsdlFileParam.getDisplayName()
							+ ") is empty but is required.";
					Problems.add(ProblemStatus.ERROR, (Element) node,
							errorMessage);
				} else {
					String id = (String) resourceParam.getValue();
					try {
						IRepositoryViewObject lastVersion = ProxyRepositoryFactory
								.getInstance().getLastVersion(id);
						if (lastVersion == null) {
							errorMessage = "Parameter ("
									+ wsdlFileParam.getDisplayName()
									+ ") doesn't exist.";
							Problems.add(ProblemStatus.ERROR, (Element) node,
									errorMessage);
						} else if (lastVersion.isDeleted()) {
							errorMessage = "Resource used by "
									+ resourceParam.getDisplayName()
									+ " has been deleted.";
							Problems.add(ProblemStatus.ERROR, (Element) node,
									errorMessage);
						}
					} catch (PersistenceException e) {
						errorMessage = "Parameter ("
								+ wsdlFileParam.getDisplayName()
								+ ") is empty but is required.";
						Problems.add(ProblemStatus.ERROR, (Element) node,
								errorMessage);
					}
				}
			}
		}
		// Select Service class
		else if (serviceParam != null
				&& "serviceClass".equals(serviceParam.getValue())) {
			// Service class is empty
			if (clazzParam == null || clazzParam.getValue() == null
					|| clazzParam.getValue().toString().isEmpty()) {
				String errorMessage = "Parameter ("
						+ wsdlFileParam.getDisplayName()
						+ ") is empty but is required.";
				Problems.add(ProblemStatus.ERROR, (Element) node, errorMessage);
			}
		}

	}

}
