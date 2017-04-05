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
package org.talend.designer.esb.runcontainer.export;

import java.io.File;
import java.util.Map;

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.services.ui.scriptmanager.ServiceExportManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.esb.JobJavaScriptOSGIForESBManager;

public class ServiceExportForESBRuntimeManager extends ServiceExportManager {

    public ServiceExportForESBRuntimeManager(Map<ExportChoice, Object> exportChoiceMap, int statisticsPort, int tracePort) {
        super(exportChoiceMap);
        this.statisticPort = statisticsPort;
        this.tracePort = tracePort;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.services.ui.scriptmanager.ServiceExportManager#getJobManager(java.util.Map,
     * java.lang.String, org.talend.core.model.repository.IRepositoryViewObject, java.lang.String, java.lang.String)
     */
    @Override
    public JobScriptsManager getJobManager(Map<ExportChoice, Object> exportChoiceMap, String parentPath,
            IRepositoryViewObject node, String groupId, String serviceVersion) {
        if (exportChoiceMap == null) {
            exportChoiceMap = getDefaultExportChoiceMap();
        }
        JobJavaScriptOSGIForESBManager manager = new JobJavaScriptOSGIForESBRuntimeManager(exportChoiceMap, null, serviceVersion,
                statisticPort, tracePort);
        String artifactName = getNodeLabel(node);
        File path = getFilePath(parentPath, groupId, artifactName, serviceVersion);
        File file = new File(path, artifactName + '-' + serviceVersion + manager.getOutputSuffix());
        manager.setDestinationPath(file.getAbsolutePath());
        return manager;
    }

}
