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
package org.talend.designer.esb.runcontainer.ui.actions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.talend.camel.designer.ui.wizards.actions.JavaCamelJobScriptsExportWSAction;
import org.talend.designer.publish.core.models.BundleModel;
import org.talend.designer.publish.core.models.FeaturesModel;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager.ExportChoice;

/**
 * It's for export the karaf feature to a target folder without zipping.
 *
 */
public class JavaCamelJobScriptsExportWSForRuntimeAction extends JavaCamelJobScriptsExportWSAction {

    private String exportFolder;

    public JavaCamelJobScriptsExportWSForRuntimeAction(Map<ExportChoice, Object> exportChoiceMap, IRepositoryNode routeNode,
            String version, String destinationKar, boolean addStatisticsCode) {
        super(exportChoiceMap, routeNode, version, destinationKar, addStatisticsCode);
    }

    public JavaCamelJobScriptsExportWSForRuntimeAction(IRepositoryNode routeNode, String version, String bundleVersion) {
        super(routeNode, version, bundleVersion);
    }

    public JavaCamelJobScriptsExportWSForRuntimeAction(IRepositoryNode routeNode, String version, String destinationKar,
            boolean addStatisticsCode) {
        // add statistics for runtime debug
        super(routeNode, version, destinationKar, addStatisticsCode);
        assert addStatisticsCode;
    }

    public JavaCamelJobScriptsExportWSForRuntimeAction(IRepositoryNode routeNode, String version, String destinationKar,
            boolean addStatisticsCode, int statisticPort, int tracePort) {
        // add statistics for runtime debug
        super(routeNode, version, destinationKar, addStatisticsCode, statisticPort, tracePort);
        assert addStatisticsCode;
    }

    public String getExportDir() {
        return exportFolder;
    }

    @Override
    protected void processResults(FeaturesModel featuresModel, IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
        exportFolder = getTempDir();

        try {
            File parentDestFile = new File(exportFolder);
            if (!parentDestFile.exists()) {
                parentDestFile.mkdirs();
            }

            File featureFile = new File(exportFolder + featuresModel.getRepositoryLocation(null));
            featureFile.mkdirs();
            Files.copy(featuresModel.getContent(), featureFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            for (BundleModel bundleModel : featuresModel.getBundles()) {
                // add bundle jar file
                File f = bundleModel.getFile();
                if (null == f) {
                    continue;
                }
                File bundleFile = new File(exportFolder + bundleModel.getRepositoryLocation(null));
                bundleFile.mkdirs();
                Files.copy(bundleModel.getFile().toPath(), bundleFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new InvocationTargetException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.camel.designer.ui.wizards.actions.JavaCamelJobScriptsExportWSAction#removeTempFiles()
     */
    @Override
    protected void removeTempFiles() {
        // do nothing
    }

    public void removeTempFilesAfterDeploy() {
        // do nothing for test
        // super.removeTempFiles();
    }
}
