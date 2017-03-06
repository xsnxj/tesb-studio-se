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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.designer.esb.runcontainer.core.ESBRunContainerPlugin;

/**
 * Class used to initialize default preference values.
 */
public class ESBRunContainerPreferenceInitializer extends AbstractPreferenceInitializer {

    //
    public static final String P_ESB_RUNTIME_LOCATION = "ESB_RUNTIME_LOCATION";

    public static final String P_DEFAULT_ESB_RUNTIME_LOCATION = "";

    //
    public static final String P_ESB_RUNTIME_HOST = "P_ESB_RUNTIME_HOST";

    public static final String P_DEFAULT_ESB_RUNTIME_HOST = "localhost";

    //
    public static final String P_ESB_RUNTIME_PORT = "P_ESB_RUNTIME_PORT";

    public static final String P_DEFAULT_ESB_RUNTIME_PORT = "1099";

    //
    public static final String P_ESB_RUNTIME_USERNAME = "ESB_RUNTIME_USERNAME";

    public static final String P_DEFAULT_ESB_RUNTIME_USERNAME = "karaf";

    //
    public static final String P_ESB_RUNTIME_PASSWORD = "ESB_RUNTIME_PASSWORD";

    public static final String P_DEFAULT_ESB_RUNTIME_PASSWORD = "karaf";

    //
    public static final String P_ESB_RUNTIME_MAVEN_SCRIPT = "ESB_RUNTIME_MAVEN_SCRIPT";

    public static final String P_DEFAULT_ESB_RUNTIME_MAVEN_SCRIPT = "";

    //
    public static final String P_ESB_RUNTIME_CLEAN_CACHE = "ESB_RUNTIME_CLEAN_CACHE";

    public static final String P_DEFAULT_ESB_RUNTIME_CLEAN_CACHE = "";

    //
    public static final String P_ESB_RUNTIME_JMX = "ESB_RUNTIME_JMX";

    public static final String P_DEFAULT_ESB_RUNTIME_JMX = "";

    //
    public static final String P_ESB_RUNTIME_JMX_PORT = "ESB_RUNTIME_JMX_PORT";

    public static final String P_DEFAULT_ESB_RUNTIME_JMX_PORT = "44444";

    //
    public static final String P_ESB_RUNTIME_SYS_LOG = "ESB_RUNTIME_SYS_LOG";

    public static final String P_DEFAULT_ESB_RUNTIME_SYS_LOG = "";

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences() {
        IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
        store.setDefault(P_ESB_RUNTIME_LOCATION, P_DEFAULT_ESB_RUNTIME_LOCATION);
    }
}
