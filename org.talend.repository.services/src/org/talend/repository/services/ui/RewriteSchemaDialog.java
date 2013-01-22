// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.ui.swt.utils.AbstractForm;

/**
 * DOC hwang class global comment. Detailled comment
 */
public class RewriteSchemaDialog extends Dialog {

    private final Collection<IRepositoryViewObject> xmlObjs;

    private XmlTableForm tableForm;

    /**
     * DOC hwang RewriteSchemaDialog constructor comment.
     * 
     * @param parentShell
     */
    public RewriteSchemaDialog(Shell parentShell, Collection<IRepositoryViewObject> xmlObjs) {
        super(parentShell);
        this.xmlObjs = xmlObjs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        createTable(container);
        return container;
    }

    /**
     * DOC hwang Comment method "createTable".
     */
    private void createTable(Composite container) {
        tableForm = new XmlTableForm(container, xmlObjs);
        tableForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        AbstractForm.ICheckListener listener = new AbstractForm.ICheckListener() {

            public void checkPerformed(final AbstractForm source) {
                if (getSelectionTables().size() <= 0) {
                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                } else {
                    getButton(IDialogConstants.OK_ID).setEnabled(true);
                }
            }
        };
        tableForm.setListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Import WSDL Schemas");
        newShell.setSize(new Point(400, 500));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        createButton(parent, IDialogConstants.OK_ID, "OK", true); //$NON-NLS-1$
        getButton(IDialogConstants.OK_ID).setEnabled(false);

        createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false); //$NON-NLS-1$
    }

    public Map<String, IRepositoryViewObject> getSelectionTables() {
        return tableForm.getSelectionItems();
    }

}
