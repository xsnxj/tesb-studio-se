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
package org.talend.repository.services.ui.viewer.tester;

import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.tester.AbstractNodeTester;

/**
 * DOC ggu class global comment. Detailled comment
 */
public class ServicesNodeTester extends AbstractNodeTester {

    private static final String IS_SERVICES_TOP_NODE = "isServicesTopNode"; //$NON-NLS-1$

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
            if (IS_SERVICES_TOP_NODE.equals(property)) {
                return isServicesTopNode(repositoryNode);
            }
        }
        return null;
    }

    public boolean isServicesTopNode(RepositoryNode repositoryNode) {
        return isTypeNode(repositoryNode, ESBRepositoryNodeType.SERVICES);
    }
}
