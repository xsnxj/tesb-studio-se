// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.components.AbstractComponentsProvider;
import org.talend.core.model.components.IComponentsFactory;
import org.talend.core.ui.branding.IBrandingService;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelLocalComponentsProvider extends AbstractComponentsProvider {

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

    // public void preComponentsLoad() throws IOException {
    // File installationFolder = getInstallationFolder();
    //
    // FilesUtils.createFoldersIfNotExists(installationFolder.getAbsolutePath(), false);
    //
    // File externalComponentsLocation = getExternalComponentsLocation();
    // if (externalComponentsLocation != null) {
    // if (externalComponentsLocation.exists()) {
    // try {
    // FileFilter ff = new FileFilter() {
    //
    // public boolean accept(File pathname) {
    // if (pathname.getName().equals(".svn")) {
    // return false;
    // }
    // return true;
    // }
    //
    // };
    // FilesUtils.copyFolder(externalComponentsLocation, installationFolder, false, ff, null, true, true);
    // } catch (IOException e) {
    // ExceptionHandler.process(e);
    // }
    // } else {
    // logger.warn(Messages
    //                        .getString("AbstractComponentsProvider.folderNotExist", externalComponentsLocation.toString())); //$NON-NLS-1$
    // }
    // }
    // }

    @Override
    public String getComponentsLocation() {
        IBrandingService brandingService = (IBrandingService) GlobalServiceRegister.getDefault().getService(
                IBrandingService.class);
        if (brandingService.isPoweredOnlyCamel()) {
            return new Path(IComponentsFactory.COMPONENTS_INNER_FOLDER).toString();
        } else {
            return super.getComponentsLocation();
        }
    }
}
