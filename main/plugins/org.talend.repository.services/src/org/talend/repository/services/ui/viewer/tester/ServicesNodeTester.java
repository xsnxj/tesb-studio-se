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
package org.talend.repository.services.ui.viewer.tester;

import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.tester.AbstractNodeTester;

/**
 * DOC ggu class global comment. Detailled comment
 */
public class ServicesNodeTester extends AbstractNodeTester {

    private static final String IS_SERVICES_NODE = "isServicesNode"; //$NON-NLS-1$

    private static final String IS_SERVICES_PORT_NODE = "isServicesPortNode"; //$NON-NLS-1$

    private static final String IS_SERVICES_OPERATION_NODE = "isServicesOperationNode"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.tester.AbstractNodeTester#testProperty(java.lang.Object, java.lang.String,
     * java.lang.Object[], java.lang.Object)
     */
    @Override
    protected Boolean testProperty(Object receiver, String property, Object[] args, Object expectedValue) {
        if (receiver instanceof RepositoryNode) {
            RepositoryNode repositoryNode = (RepositoryNode) receiver;
            if (IS_SERVICES_NODE.equals(property)) {
                return isServicesNode(repositoryNode);
            } else if (IS_SERVICES_PORT_NODE.equals(property)) {
                return isServicesPortNode(repositoryNode);
            } else if (IS_SERVICES_OPERATION_NODE.equals(property)) {
                return isServicesOperationNode(repositoryNode);
            }
        }
        return null;
    }

    public boolean isServicesNode(RepositoryNode repositoryNode) {
        return isTypeNode(repositoryNode, ESBRepositoryNodeType.SERVICES);
    }

    public boolean isServicesPortNode(RepositoryNode repositoryNode) {
        return isTypeNode(repositoryNode, ESBRepositoryNodeType.SERVICEPORT);
    }

    public boolean isServicesOperationNode(RepositoryNode repositoryNode) {
        return isTypeNode(repositoryNode, ESBRepositoryNodeType.SERVICESOPERATION);
    }

    @Override
    public boolean isTypeNode(RepositoryNode repositoryNode, ERepositoryObjectType type) {
        // boolean is = repositoryNode.getContentType() == type;
        // return is;
        return super.isTypeNode(repositoryNode, type);
    }
}
