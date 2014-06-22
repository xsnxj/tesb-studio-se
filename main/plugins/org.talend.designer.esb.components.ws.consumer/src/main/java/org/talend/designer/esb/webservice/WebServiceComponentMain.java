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
package org.talend.designer.esb.webservice;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.designer.esb.webservice.data.ExternalWebServiceUIProperties;
import org.talend.designer.esb.webservice.managers.WebServiceManager;
import org.talend.designer.esb.webservice.ui.dialog.WebServiceDialog;

/**
 * gcui class global comment. Detailled comment
 */
public class WebServiceComponentMain {

    private WebServiceComponent connector;

    private WebServiceManager webServiceManager;

    private WebServiceDialog dialog;

    public WebServiceDialog getDialog() {
        return this.dialog;
    }

    public WebServiceComponentMain(WebServiceComponent connector) {
        super();
        this.connector = connector;
        this.webServiceManager = new WebServiceManager(connector, 0);
    }

    public void loadInitialParamters() {
        //
    }

    public Dialog createDialog(Shell parentShell) {
        dialog = new WebServiceDialog(parentShell, this);
        IBrandingService brandingService = (IBrandingService) GlobalServiceRegister.getDefault().getService(
                IBrandingService.class);
        String productName = brandingService.getFullProductName();
        dialog.setTitle(productName + 
        		" - " + connector.getComponent().getName() + 
        		" - " + connector.getUniqueName());

        Rectangle boundsMapper = ExternalWebServiceUIProperties.getBoundsMapper();
        if (ExternalWebServiceUIProperties.isShellMaximized()) {
            dialog.setMaximized(ExternalWebServiceUIProperties.isShellMaximized());
        } else {
            boundsMapper = ExternalWebServiceUIProperties.getBoundsMapper();
            if (boundsMapper.x < 0) {
                boundsMapper.x = 0;
            }
            if (boundsMapper.y < 0) {
                boundsMapper.y = 0;
            }
            dialog.setSize(boundsMapper);
        }
        dialog.open();
        return dialog;
    }

    public void createUI(Display display) {
        Shell shell = new Shell(display, ExternalWebServiceUIProperties.DIALOG_STYLE);
        createDialog(shell);
    }

    public WebServiceManager getWebServiceManager() {
        return this.webServiceManager;
    }

    public int getDialogResponse() {
        return webServiceManager.getUIManager().getDialogResponse();
    }

    public WebServiceComponent getWebServiceComponent() {
        return this.connector;
    }
}
