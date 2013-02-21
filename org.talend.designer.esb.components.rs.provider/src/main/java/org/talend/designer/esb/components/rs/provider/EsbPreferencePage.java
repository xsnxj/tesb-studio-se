package org.talend.designer.esb.components.rs.provider;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class EsbPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public EsbPreferencePage() {
		super(GRID);
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		// setDescription(Messages.getString("esb.preferences.rest.service.uri.default"));
	}

	@Override
	protected void createFieldEditors() {
		StringFieldEditor localRestServiceUri = new StringFieldEditor(
				Activator.REST_URI_PREFERENCE,
				Messages.getString("esb.preferences.rest.service.uri.default"),
				getFieldEditorParent());
		localRestServiceUri.setEmptyStringAllowed(false);
		addField(localRestServiceUri);
	}

	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		Activator.getDefault().loadCustomProperty();
		return result;
	}
}
