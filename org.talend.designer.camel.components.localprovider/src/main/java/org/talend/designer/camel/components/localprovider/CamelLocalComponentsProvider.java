// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.components.localprovider;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.i18n.Messages;
import org.talend.core.model.components.AbstractComponentsProvider;
import org.talend.core.model.components.IComponentsFactory;
import org.talend.core.ui.branding.IBrandingService;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelLocalComponentsProvider extends AbstractComponentsProvider {

    private static Logger logger = Logger.getLogger(CamelLocalComponentsProvider.class);

    public CamelLocalComponentsProvider() {

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.model.components.AbstractComponentsProvider#getExternalComponentsLocation()
     */

    protected File getExternalComponentsLocation() {
        URL url = FileLocator.find(CamelComponentPlugin.getDefault().getBundle(), new Path("components"), null); //$NON-NLS-1$
        URL fileUrl;
        try {
            fileUrl = FileLocator.toFileURL(url);
            return new File(fileUrl.getPath());
        } catch (Exception e) {
        }
        return null;
    }

    public void preComponentsLoad() throws IOException {
        File installationFolder = super.getInstallationFolder();
        IBrandingService breaningService = (IBrandingService) GlobalServiceRegister.getDefault().getService(
                IBrandingService.class);
        if (breaningService.isPoweredOnlyCamel()) {
            installationFolder = getInstallationFolder();
        }
        FilesUtils.createFoldersIfNotExists(installationFolder.getAbsolutePath(), false);

        File externalComponentsLocation = getExternalComponentsLocation();
        if (externalComponentsLocation != null) {
            if (externalComponentsLocation.exists()) {
                try {
                    FileFilter ff = new FileFilter() {

                        public boolean accept(File pathname) {
                            if (pathname.getName().equals(".svn")) {
                                return false;
                            }
                            return true;
                        }

                    };
                    FilesUtils.copyFolder(externalComponentsLocation, installationFolder, false, ff, null, true, true);
                } catch (IOException e) {
                    ExceptionHandler.process(e);
                }
            } else {
                logger.warn(Messages
                        .getString("AbstractComponentsProvider.folderNotExist", externalComponentsLocation.toString())); //$NON-NLS-1$
            }
        }
    }

    public File getInstallationFolder() throws IOException {
        File installationFolder = null;
        IBrandingService breaningService = (IBrandingService) GlobalServiceRegister.getDefault().getService(
                IBrandingService.class);
        if (breaningService.isPoweredOnlyCamel()) {
            Bundle b = Platform.getBundle(IComponentsFactory.CAMEL_COMPONENTS_LOCATION);

            IPath nullPath = new Path(""); //$NON-NLS-1$
            URL url = FileLocator.find(b, nullPath, null);
            URL fileUrl = FileLocator.toFileURL(url);
            File bundleFolder = new File(fileUrl.getPath());

            IPath path = new Path(IComponentsFactory.COMPONENTS_INNER_FOLDER);
            // path = path.append(ComponentUtilities.getExtFolder(getFolderName()));

            installationFolder = new File(bundleFolder, path.toOSString());
        } else {
            installationFolder = super.getInstallationFolder();
        }
        return installationFolder;
    }

    public String getComponentsLocation() {
        IBrandingService breaningService = (IBrandingService) GlobalServiceRegister.getDefault().getService(
                IBrandingService.class);
        if (breaningService.isPoweredOnlyCamel()) {
            return new Path(IComponentsFactory.COMPONENTS_INNER_FOLDER).toString();
        }
        return super.getComponentsLocation();
    }
}
