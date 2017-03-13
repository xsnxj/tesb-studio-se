// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2013 Talend – www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.designer.esb.runcontainer.preferences;

import java.io.File;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.designer.esb.runcontainer.core.ESBRunContainerPlugin;

/**
 * Class used to initialize default preference values.
 */
public class RunContainerPreferenceInitializer extends AbstractPreferenceInitializer {

    //
    public static final String P_ESB_RUNTIME_LOCATION = "ESB_RUNTIME_LOCATION";

    public static final String P_DEFAULT_ESB_RUNTIME_LOCATION = 
    		System.getProperty("user.dir") + File.separator + "esb" + File.separator + "container";

    //
    public static final String P_ESB_RUNTIME_HOST = "P_ESB_RUNTIME_HOST";

    public static final String P_DEFAULT_ESB_RUNTIME_HOST = "localhost";

    //
    public static final String P_ESB_RUNTIME_PORT = "P_ESB_RUNTIME_PORT";

    public static final int P_DEFAULT_ESB_RUNTIME_PORT = 1099;

    //
    public static final String P_ESB_RUNTIME_USERNAME = "ESB_RUNTIME_USERNAME";

    public static final String P_DEFAULT_ESB_RUNTIME_USERNAME = "tesb";

    //
    public static final String P_ESB_RUNTIME_PASSWORD = "ESB_RUNTIME_PASSWORD";

    public static final String P_DEFAULT_ESB_RUNTIME_PASSWORD = "tesb";

    //
    public static final String P_ESB_RUNTIME_MAVEN_SCRIPT = "ESB_RUNTIME_MAVEN_SCRIPT";

    public static final boolean P_DEFAULT_ESB_RUNTIME_MAVEN_SCRIPT = false;

    //
    public static final String P_ESB_RUNTIME_CLEAN_CACHE = "ESB_RUNTIME_CLEAN_CACHE";

    public static final boolean P_DEFAULT_ESB_RUNTIME_CLEAN_CACHE = false;

    //
    public static final String P_ESB_RUNTIME_JMX = "ESB_RUNTIME_JMX";

    public static final boolean P_DEFAULT_ESB_RUNTIME_JMX = true;

    //
    public static final String P_ESB_RUNTIME_JMX_PORT = "ESB_RUNTIME_JMX_PORT";

    public static final int P_DEFAULT_ESB_RUNTIME_JMX_PORT = 44444;

    //
    public static final String P_ESB_RUNTIME_SYS_LOG = "ESB_RUNTIME_SYS_LOG";

    public static final boolean P_DEFAULT_ESB_RUNTIME_SYS_LOG = false;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences() {
        IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
        store.setDefault(P_ESB_RUNTIME_LOCATION, P_DEFAULT_ESB_RUNTIME_LOCATION);
        store.setDefault(P_ESB_RUNTIME_HOST, P_DEFAULT_ESB_RUNTIME_HOST);
        store.setDefault(P_ESB_RUNTIME_PORT, P_DEFAULT_ESB_RUNTIME_PORT);
        store.setDefault(P_ESB_RUNTIME_USERNAME, P_DEFAULT_ESB_RUNTIME_USERNAME);
        store.setDefault(P_ESB_RUNTIME_PASSWORD, P_DEFAULT_ESB_RUNTIME_PASSWORD);
        store.setDefault(P_ESB_RUNTIME_MAVEN_SCRIPT, P_DEFAULT_ESB_RUNTIME_MAVEN_SCRIPT);
        store.setDefault(P_ESB_RUNTIME_CLEAN_CACHE, P_DEFAULT_ESB_RUNTIME_CLEAN_CACHE);
        store.setDefault(P_ESB_RUNTIME_JMX, P_DEFAULT_ESB_RUNTIME_JMX);
        store.setDefault(P_ESB_RUNTIME_JMX_PORT, P_DEFAULT_ESB_RUNTIME_JMX_PORT);
        store.setDefault(P_ESB_RUNTIME_SYS_LOG, P_DEFAULT_ESB_RUNTIME_SYS_LOG);
    }
}
