// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.libraries;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.routines.IRoutinesProvider;
import org.talend.librariesmanager.model.service.JavaLibrariesService;

/**
 * @author Administrator
 * 
 */
public class CamelJavaRoutinesProvider implements IRoutinesProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.routines.IRoutinesProvider#getSystemRoutines()
     */

    public List<URL> getSystemRoutines() {
        List<URL> toReturn = FilesUtils.getFilesFromFolder(Activator.BUNDLE, "resources/java/" //$NON-NLS-1$
                + JavaLibrariesService.SOURCE_JAVA_ROUTINES_FOLDER, ".java", false, false); //$NON-NLS-1$
        return toReturn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.routines.IRoutinesProvider#getTalendRoutinesFolder()
     */
    public URL getTalendRoutinesFolder() throws IOException {
        URL url = Activator.BUNDLE.getEntry("resources/java/routines/system"); //$NON-NLS-1$
        return FileLocator.resolve(url);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.routines.IRoutinesProvider#getTalendRoutines()
     */
    public List<URL> getTalendRoutines() {
        List<URL> toReturn = FilesUtils.getFilesFromFolder(Activator.BUNDLE, "resources/java/routines/system", ".java"); //$NON-NLS-1$ //$NON-NLS-2$
        return toReturn;
    }

}
