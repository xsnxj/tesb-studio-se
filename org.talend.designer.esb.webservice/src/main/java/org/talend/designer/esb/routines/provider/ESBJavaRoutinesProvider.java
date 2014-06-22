package org.talend.designer.esb.routines.provider;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.routines.IRoutinesProvider;
import org.talend.designer.esb.webservice.WebServiceComponentPlugin;

public class ESBJavaRoutinesProvider implements IRoutinesProvider {

    public List<URL> getSystemRoutines() {
        List<URL> toReturn = FilesUtils.getFilesFromFolder(WebServiceComponentPlugin.getDefault()
                .getBundle(), "resources/java/routines", ".java", false, false);
        return toReturn;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.model.routines.IRoutinesProvider#getTalendRoutinesFolder()
     */
    public URL getTalendRoutinesFolder() throws IOException {
        URL url = WebServiceComponentPlugin.getDefault().getBundle()
                .getEntry("resources/java/routines/system"); //$NON-NLS-1$
        return FileLocator.resolve(url);
    }

    public List<URL> getTalendRoutines() {
        return FilesUtils.getFilesFromFolder(WebServiceComponentPlugin.getDefault().getBundle(),
                "resources/java/routines/system", "");
    }
}
