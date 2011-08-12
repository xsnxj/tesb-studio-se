// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceItem;

/**
 * hwang class global comment. Detailled comment
 */
public class OpenWSDLWizard extends Wizard {

    private OpenWSDLPage wsdlPage = null;

    private RepositoryNode repositoryNode;

    public OpenWSDLWizard(IWorkbench bench, RepositoryNode repositoryNode) {
        this.repositoryNode = repositoryNode;
        this.setWindowTitle(" Edit WSDL");
    }

    @Override
    public boolean performFinish() {
        wsdlPage.finish();
        ServiceItem item = (ServiceItem) repositoryNode.getObject().getProperty().getItem();
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        try {
            factory.save(item);
            ProxyRepositoryFactory.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());
        } catch (PersistenceException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void addPages() {
        wsdlPage = new OpenWSDLPage(repositoryNode, "Edit WSDL");
        addPage(wsdlPage);
        super.addPages();
    }

}
