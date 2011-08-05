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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.talend.commons.ui.swt.formtools.LabelledFileField;
import org.talend.repository.model.RepositoryNode;

/**
 * hwang class global comment. Detailled comment
 */
public class OpenWSDLPage extends WizardPage {

    private RepositoryNode repositoryNode;

    private LabelledFileField wsdlText;

    protected OpenWSDLPage(RepositoryNode repositoryNode, String pageName) {
        super(pageName);
        this.repositoryNode = repositoryNode;
    }

    public void createControl(Composite parent) {
        Composite parentArea = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        // layout.marginLeft = 100;
        // layout.marginWidth = 10;
        // layout.marginHeight = 10;
        layout.numColumns = 5;
        parentArea.setLayout(layout);

        String[] xmlExtensions = { "*.xml;*.xsd,*.wsdl", "*.*", "*" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        wsdlText = new LabelledFileField(parentArea, "WSDL", //$NON-NLS-1$
                xmlExtensions);

        // boolean canFinish = checkFieldsValue();
        // this.setPageComplete(canFinish);
        // addListener();
        setControl(parentArea);

    }

    private void sddListener() {
        wsdlText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String text = wsdlText.getText();
            }

        });
    }

}
