package org.talend.camel.designer.ui.view;

import org.eclipse.core.runtime.IAdapterFactory;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;

public class SpringViewAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType == ISpringConfigurationPage.class && adaptableObject instanceof CamelMultiPageTalendEditor){
			return new SpringConfigurationPageImpl(((CamelMultiPageTalendEditor)adaptableObject).getDesignerEditor());
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[]{ISpringConfigurationPage.class};
	}

}
