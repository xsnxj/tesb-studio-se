package org.talend.designer.esb.webservice.ui.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.utils.TalendTextUtils;
import org.talend.designer.esb.webservice.WebServiceComponent;
import org.talend.designer.esb.webservice.WebServiceComponentMain;
import org.talend.designer.esb.webservice.data.ExternalWebServiceUIProperties;
import org.talend.designer.esb.webservice.managers.UIManager;
import org.talend.designer.esb.webservice.ui.WebServiceUI;
import org.talend.designer.esb.webservice.ws.wsdlinfo.Function;

public class WebServiceDialog extends Dialog implements WebServiceEventListener {

    private String title;

    private Rectangle size;

    private WebServiceUI webServiceUI;

    private WebServiceComponentMain webServiceComponentMain;

    private boolean maximized;

    private Button okButton;

    public WebServiceDialog(Shell parentShell, WebServiceComponentMain webServiceComponentMain) {
        super(parentShell);
        this.webServiceComponentMain = webServiceComponentMain;
        setShellStyle(ExternalWebServiceUIProperties.DIALOG_STYLE);
    }

    public WebServiceUI getWebServiceUI() {
        return this.webServiceUI;
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        if (title != null) {
            newShell.setText(title);
        }
        if (maximized) {
            newShell.setMaximized(true);
        } else {
            newShell.setBounds(size);
        }
    }

    public void setTitle(String title) {
        this.title = title;

    }

    /**
     * Sets the size.
     * 
     * @param size the size to set
     */
    public void setSize(Rectangle size) {
        this.size = size;
    }

    /**
     * Sets the maximizedSize.
     * 
     * @param maximizedSize the maxmimizedSize to set
     */
    public void setMaximized(boolean maximized) {
        this.maximized = maximized;
    }

    private UIManager getUIManager() {
        return webServiceComponentMain.getWebServiceManager().getUIManager();
    }

    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.OK_ID == buttonId) {
            okPressed();
        } else if (IDialogConstants.CANCEL_ID == buttonId) {
            cancelPressed();
        }
    }

    protected void cancelPressed() {
        super.cancelPressed();
        getUIManager().setDialogResponse(SWT.CANCEL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */

    protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
        webServiceUI.setWizardOkButton(okButton);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    protected Control createDialogArea(Composite parent) {
        Composite panel = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        panel.setLayout(layout);
        panel.setLayoutData(new GridData(GridData.FILL_BOTH));
        applyDialogFont(panel);
        webServiceUI = new WebServiceUI(panel, this.webServiceComponentMain);
        webServiceUI.init();
        return panel;
    }

    protected void okPressed() {
        getWebServiceUI().saveProperties();
        saveValue();
        getUIManager().setDialogResponse(SWT.OK);
    }

    private void saveValue() {
        String currentURL = webServiceUI.getURL();
        List<String> allPortNames = webServiceUI.getAllPortNames();
        Function function = webServiceUI.getCurrentFunction();
        String currentPortName = webServiceUI.getCurrentPortName();
        WebServiceComponent wenCom = webServiceComponentMain.getWebServiceComponent();

        if (!"".equals(currentURL) && currentURL != null) {
            IElementParameter ENDPOINTPara = wenCom.getElementParameter("ENDPOINT");
            ENDPOINTPara.setValue(currentURL);
        }

        if (currentPortName != null) {
            IElementParameter Port_Name = wenCom.getElementParameter("PORT_NAME");
            Port_Name.setValue(currentPortName);
        } else if (currentPortName == null && !allPortNames.isEmpty()) {
            currentPortName = allPortNames.get(0);
            IElementParameter Port_Name = wenCom.getElementParameter("PORT_NAME");
            Port_Name.setValue(currentPortName);
        }

        if (function != null) {
            if (function.getName() != null) {
                IElementParameter METHODPara = wenCom.getElementParameter("METHOD");
                METHODPara.setValue(function.getName());
            }
            if (function.getServerNameSpace() != null) {
                IElementParameter Service_NS = wenCom.getElementParameter("SERVICE_NS");
                Service_NS.setValue(function.getServerNameSpace());
            }
            if (function.getServerName() != null) {
                IElementParameter Service_Name = wenCom.getElementParameter("SERVICE_NAME");
                Service_Name.setValue(function.getServerName());
            }
            if (function.getServerNameSpace() != null) {
                IElementParameter Port_NS = wenCom.getElementParameter("PORT_NS");
                Port_NS.setValue(function.getServerNameSpace());
            }
            IElementParameter esbEndpoint = wenCom.getElementParameter("ESB_ENDPOINT");
            if (esbEndpoint != null) {
                esbEndpoint.setValue(TalendTextUtils.addQuotes(function.getAddressLocation()));
            }
            IElementParameter commStyle = wenCom.getElementParameter("COMMUNICATION_STYLE");
            if (commStyle != null) {
                commStyle.setValue(function.getOutput() == null ? "one-way":"request-response");
            }

        }
        super.okPressed();

    }

    public static final void warningDialog(String title) {
        MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK);
        box.setText("WARNING"); //$NON-NLS-1$
        box.setMessage(title); //$NON-NLS-1$
        box.open();
    }

    public void checkPerformed(boolean enable) {
        final Button okBtn = getButton(IDialogConstants.OK_ID);
        if (okBtn != null) {
            okBtn.setEnabled(enable);
        }

    }

}
