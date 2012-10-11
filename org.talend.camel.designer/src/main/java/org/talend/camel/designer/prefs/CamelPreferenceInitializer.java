package org.talend.camel.designer.prefs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.camel.designer.CamelDesignerPlugin;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.GlobalServiceRegister;
import org.talend.repository.constants.ExportJobConstants;
import org.talend.resource.IResourceService;

public class CamelPreferenceInitializer extends AbstractPreferenceInitializer {

    private static final String EMPTY_STR = ""; //$NON-NLS-1$

    @Override
    public void initializeDefaultPreferences() {
        final IPreferenceStore preferenceStore = CamelDesignerPlugin.getDefault().getPreferenceStore();
        // main
        preferenceStore.setDefault(ICamelPrefConstants.MAVEN_KARAF_SCRIPT_TEMPLATE,
                getMavenScriptTemplate(ExportJobConstants.MAVEN_BUILD_FILE_NAME));
        // parent
        preferenceStore.setDefault(ICamelPrefConstants.MAVEN_KARAF_SCRIPT_TEMPLATE_PARENT,
                getMavenScriptTemplate(ExportJobConstants.MAVEN_KARAF_BUILD_PARENT_FILE_NAME));
        // bundle
        preferenceStore.setDefault(ICamelPrefConstants.MAVEN_KARAF_SCRIPT_TEMPLATE_BUNDLE,
                getMavenScriptTemplate(ExportJobConstants.MAVEN_KARAF_BUILD_BUNDLE_FILE_NAME));
        // feature
        preferenceStore.setDefault(ICamelPrefConstants.MAVEN_KARAF_SCRIPT_TEMPLATE_FEATURE,
                getMavenScriptTemplate(ExportJobConstants.MAVEN_KARAF_BUILD_FEATURE_FILE_NAME));

    }

    private String getMavenScriptTemplate(String pomName) {
        IResourceService resourceService = (IResourceService) GlobalServiceRegister.getDefault().getService(
                IResourceService.class);
        if (resourceService == null) {
            return EMPTY_STR;
        }

        File templateScriptFile = new File(resourceService.getMavenScriptFilePath("karaf/" + pomName)); //$NON-NLS-1$
        if (!templateScriptFile.exists()) {
            return EMPTY_STR;
        }

        try {
            return new Scanner(templateScriptFile).useDelimiter("\\A").next(); //$NON-NLS-1$
        } catch (FileNotFoundException e) {
            ExceptionHandler.process(e);
        }
        return EMPTY_STR;
    }

}
