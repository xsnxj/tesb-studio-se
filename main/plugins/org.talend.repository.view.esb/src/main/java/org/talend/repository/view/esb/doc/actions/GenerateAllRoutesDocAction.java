// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.view.esb.doc.actions;

import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.repository.documentation.actions.AbstractGenerateAllItemsDocAction;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC ftang class global comment. Detailled comment
 */
public class GenerateAllRoutesDocAction extends AbstractGenerateAllItemsDocAction {

	/**
     * Constructs a new ExportAllJobsDocAction.
     */
    public GenerateAllRoutesDocAction() {
        super("Generate all routes Documentations",
        		"Generate all routes Documentations",
        		CamelRepositoryNodeType.repositoryDocumentationsType,
        		CamelRepositoryNodeType.repositoryDocumentationType,
        		CamelRepositoryNodeType.repositoryRoutesType);
    }


    public GenerateAllRoutesDocAction(boolean allVersions) {
		super(allVersions, CamelRepositoryNodeType.repositoryDocumentationsType,
        		CamelRepositoryNodeType.repositoryDocumentationType,
        		CamelRepositoryNodeType.repositoryRoutesType);
	}


	public void setJobNode(RepositoryNode jobNode) {
        setItemNode(jobNode);
    }
}
