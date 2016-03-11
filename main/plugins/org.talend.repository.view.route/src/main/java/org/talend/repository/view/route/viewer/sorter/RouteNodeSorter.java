// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.view.route.viewer.sorter;

import org.talend.camel.model.CamelRepositoryNodeType;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.view.route.viewer.tester.RouteNodeTester;
import org.talend.repository.view.sorter.RepositoryRootNodeCompareSorter;

/**
 * DOC ggu class global comment. Detailled comment
 */
public class RouteNodeSorter extends RepositoryRootNodeCompareSorter {

    RouteNodeTester routeTester = new RouteNodeTester();

    /**
     * should be second.
     */
    @Override
    protected void sortChildren(Object[] children) {
        int jobDesignIndex = -1;
        int routeIndex = -1;
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) children[i];
                if (routeTester.isTypeTopNode(node, ERepositoryObjectType.PROCESS)) {
                    jobDesignIndex = i;
                } else if (routeTester.isTypeTopNode(node, CamelRepositoryNodeType.repositoryRoutesType)) {
                    routeIndex = i;
                }
            }
        }

        int realIndex = 0; // top by default
        if (jobDesignIndex > -1) { // existed
            realIndex = 1;
        }
        if (routeIndex > -1) {
            swap(children, routeIndex, realIndex);
        }
    }
}
