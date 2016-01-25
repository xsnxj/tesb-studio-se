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
package org.talend.designer.esb.webservice;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.process.AbstractExternalNode;
import org.talend.core.model.process.IComponentDocumentation;
import org.talend.core.model.process.IExternalData;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.esb.webservice.ui.dialog.WebServiceDialog;

/**
 * gcui class global comment. Detailled comment
 */
public class WebServiceComponent extends AbstractExternalNode {

    public int open(Display display) { // button event
        return open(display.getActiveShell());
    }

    public int open(Composite parent) {// double click in job
       return open(parent.getShell());
    }

    private int open(Shell shell) {
        this.getElementParameter(EParameterName.UPDATE_COMPONENTS.getName()).setValue(Boolean.TRUE);
        WizardDialog wizardDialog = new WizardDialog(shell, new WebServiceDialog(this));
        return (Window.OK == wizardDialog.open()) ? SWT.OK : SWT.CANCEL;
    }

    @Override
    protected void renameMetadataColumnName(String conectionName, String oldColumnName, String newColumnName) {
    }

    public IComponentDocumentation getComponentDocumentation(String componentName, String tempFolderPath) {
        return null;
    }

    public void initialize() {
    }

    public void renameInputConnection(String oldName, String newName) {
    }

    public void renameOutputConnection(String oldName, String newName) {
    }

    public void setExternalData(IExternalData persistentData) {
    }

    public IExternalData getTMapExternalData() {
        return null;
    }

}
