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
package org.talend.designer.esb.runcontainer.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.designer.esb.runcontainer.core.ESBRunContainerPlugin;

/**
 * Class used to initialize default preference values.
 */
public class ESBRunContainerPreferenceInitializer extends AbstractPreferenceInitializer {

    //
    public static final String P_LOCAL_RUN_CONTAINER_PATH = "LOCAL_RUN_CONTAINER_PATH";

    public static final String P_DEFAULT_LOCAL_RUN_CONTAINER_PATH = "${studio_path}/esb/test_container/";

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences() {
        IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
        store.setDefault(P_LOCAL_RUN_CONTAINER_PATH, P_DEFAULT_LOCAL_RUN_CONTAINER_PATH);
    }
}
