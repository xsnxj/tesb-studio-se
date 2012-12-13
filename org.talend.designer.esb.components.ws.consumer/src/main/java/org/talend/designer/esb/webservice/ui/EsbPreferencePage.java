package org.talend.designer.esb.webservice.ui;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.talend.designer.esb.webservice.WebServiceComponentPlugin;
import org.talend.designer.esb.webservice.i18n.Messages;

public class EsbPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public static final String WS_HTTP_PORT_PREFERENCE = "wsHttpPort";

    public EsbPreferencePage() {
        super(GRID);
    }

    public void init(IWorkbench arg0) {
        setPreferenceStore(WebServiceComponentPlugin.getDefault().getPreferenceStore());
        // setDescription(Messages.getString("esb.preferences.webservice.port.default"));
    }

    @Override
    protected void createFieldEditors() {

        IntegerFieldEditor localWebServiceHttpPort = new IntegerFieldEditor(WS_HTTP_PORT_PREFERENCE,
                Messages.getString("esb.preferences.webservice.port.default"), getFieldEditorParent());
        // localWebServiceHttpPort.setValidRange(0, 65535);
        localWebServiceHttpPort.setValidRange(1024, 65535);

        addField(localWebServiceHttpPort);
    }

    @Override
    public boolean performOk() {
        boolean result = super.performOk();
        // TESB 3602 Hide ESB preference page
		// WebServiceComponentPlugin.getDefault().loadCustomProperty();
        return result;
    }

}
