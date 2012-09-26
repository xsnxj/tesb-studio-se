package org.talend.camel.designer.prefs;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.camel.designer.CamelDesignerPlugin;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.GlobalServiceRegister;
import org.talend.resource.IResourceService;

public class CamelPreferenceInitializer extends AbstractPreferenceInitializer {

    private static final String EMPTY_STR = ""; //$NON-NLS-1$

    @Override
    public void initializeDefaultPreferences() {
        final IPreferenceStore preferenceStore = CamelDesignerPlugin.getDefault().getPreferenceStore();
        preferenceStore.setDefault(ICamelPrefConstants.MAVEN_KARAF_SCRIPT_TEMPLATE, getMavenScriptTemplate());
    }

    private String getMavenScriptTemplate() {
        IResourceService resourceService = (IResourceService) GlobalServiceRegister.getDefault().getService(
                IResourceService.class);
        if (resourceService == null) {
            return EMPTY_STR;
        }

        File templateScriptFile = new File(resourceService.getMavenScriptFilePath("pom_kar.xml")); //$NON-NLS-1$
        if (!templateScriptFile.exists()) {
            return EMPTY_STR;
        }

        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(templateScriptFile);
        } catch (DocumentException e) {
            ExceptionHandler.process(e);
        }

        return document.asXML();
    }

}
