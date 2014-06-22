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
package org.talend.repository.services.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.services.model.services.ServiceItem;

/**
 * hwang class global comment. Detailled comment
 */
public class OpenWSDLWizard extends Wizard {

    private OpenWSDLPage wsdlPage = null;

    private RepositoryNode repositoryNode;

    private IPath pathToSave;

    public OpenWSDLWizard(IWorkbench bench, RepositoryNode repositoryNode) {
        this.repositoryNode = repositoryNode;
        this.setWindowTitle(" Edit WSDL");
        switch (repositoryNode.getType()) {
        case SIMPLE_FOLDER:
        case REPOSITORY_ELEMENT:
            pathToSave = RepositoryNodeUtilities.getPath(repositoryNode);
            break;
        case SYSTEM_FOLDER:
            pathToSave = new Path(""); //$NON-NLS-1$
            break;
        }
    }

    @Override
    public boolean performFinish() {

        return wsdlPage.finish();
    }

    @Override
    public void addPages() {
        wsdlPage = new OpenWSDLPage(repositoryNode, pathToSave, (ServiceItem) repositoryNode.getObject().getProperty().getItem(),
                "Edit WSDL", false);
        addPage(wsdlPage);
        super.addPages();
    }

}
