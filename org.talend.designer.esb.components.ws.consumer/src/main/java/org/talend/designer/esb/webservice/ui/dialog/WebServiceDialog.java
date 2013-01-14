package org.talend.designer.esb.webservice.ui.dialog;

import org.eclipse.jface.wizard.Wizard;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.designer.esb.webservice.WebServiceComponent;
import org.talend.designer.esb.webservice.ui.WebServiceUI;

public class WebServiceDialog extends Wizard {

    private final WebServiceComponent webServiceComponent;

    private WebServiceUI webServiceUI;

    public WebServiceDialog(WebServiceComponent webServiceComponent) {
        this.webServiceComponent = webServiceComponent;
        setWindowTitle(((IBrandingService) GlobalServiceRegister.getDefault().getService(IBrandingService.class)).getFullProductName() +
                " - " + webServiceComponent.getComponent().getName() + //$NON-NLS-1$
                " - " + webServiceComponent.getUniqueName()); //$NON-NLS-1$
    }

    @Override
    public void addPages() {
        webServiceUI = new WebServiceUI(webServiceComponent);
        addPage(webServiceUI);
    }

    @Override
    public boolean performFinish() {
        return webServiceUI.performFinish();
    }

}
