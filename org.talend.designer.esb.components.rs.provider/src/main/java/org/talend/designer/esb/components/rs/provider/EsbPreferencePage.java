package org.talend.designer.esb.components.rs.provider;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
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
		
		final StringFieldEditor defaultServiceNamespace=new StringFieldEditor(
				Activator.DEFAULT_SL_NAMESPACE_PREF,
				Messages.EsbPreferencePage_SL_NAMESPACE, getFieldEditorParent());
		defaultServiceNamespace.setEmptyStringAllowed(false);
		defaultServiceNamespace.setPropertyChangeListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Object value=event.getNewValue();
				if(value instanceof String) {
					String vString=(String) value;
					if(!vString.matches(".+")) {
						//defaut defaut is http://www.talend.org/rest/
						defaultServiceNamespace.setErrorMessage(Messages.EsbPreferencePage_SLNotValid);
					}
				}
			}
		});
		addField(defaultServiceNamespace);
	}

	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		Activator.getDefault().loadCustomProperty();
		return result;
	}
}
