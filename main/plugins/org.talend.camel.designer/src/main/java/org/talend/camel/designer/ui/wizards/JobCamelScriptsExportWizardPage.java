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
package org.talend.camel.designer.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.wizards.exportjob.ExportTreeViewer;
import org.talend.repository.ui.wizards.exportjob.JobScriptsExportWizardPage;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager.ExportChoice;

/**
 * Page of the Job Scripts Export Wizard. <br/>
 * 
 * @referto WizardArchiveFileResourceExportPage1 $Id: JobScriptsExportWizardPage.java 1 2006-12-13 03:09:07 bqian
 * 
 */
public abstract class JobCamelScriptsExportWizardPage extends JobScriptsExportWizardPage {

    // private ExportCamelTreeViewer treeViewer;

    /**
     * Create an instance of this class.
     * 
     * @param name java.lang.String
     */
    public JobCamelScriptsExportWizardPage(String name, IStructuredSelection selection) {
        super(name, selection);
    }

    /**
     * Create an instance of this class.
     * 
     * @param selection the selection
     */
    public JobCamelScriptsExportWizardPage(IStructuredSelection selection) {
        super("jobscriptsExportPage1", selection); //$NON-NLS-1$
    }

    @Override
    protected List getDefaultFileName() {
        List list = null;
        if (nodes.length >= 1) {
            String label = null;
            String version = null;
            RepositoryNode node = nodes[0];
            if (node.getType() == ENodeType.SYSTEM_FOLDER || node.getType() == ENodeType.SIMPLE_FOLDER) {
                label = node.getProperties(EProperties.LABEL).toString();
            } else if (node.getType() == ENodeType.REPOSITORY_ELEMENT) {
                IRepositoryViewObject repositoryObject = node.getObject();
                if (repositoryObject.getProperty().getItem() instanceof CamelProcessItem) {
                    CamelProcessItem processItem = (CamelProcessItem) repositoryObject.getProperty().getItem();
                    label = processItem.getProperty().getLabel();
                    version = processItem.getProperty().getVersion();
                    list = new ArrayList();
                    list.add(label);
                    list.add(version);
                }
            }
            return list;
        }
        return null;
    }

    @Override
    protected ExportTreeViewer getExportTree() {
        return new ExportCamelTreeViewer(selection, this);
    }

    /**
     * Create the buttons for the group that determine if the entire or selected directory structure should be created.
     * 
     * @param optionsGroup
     * @param font
     */
    @Override
    public void createOptions(final Composite optionsGroup, Font font) {
        super.createOptions(optionsGroup, font);
    }

    @Override
    protected Map<ExportChoice, Object> getExportChoiceMap() {
        Map<ExportChoice, Object> exportChoiceMap = super.getExportChoiceMap();
        exportChoiceMap.put(ExportChoice.needSystemRoutine, true);
        exportChoiceMap.put(ExportChoice.needUserRoutine, true);
        exportChoiceMap.put(ExportChoice.needDependencies, Boolean.TRUE);
        return exportChoiceMap;
    }

    /**
     * Answer the suffix that files exported from this wizard should have. If this suffix is a file extension (which is
     * typically the case) then it must include the leading period character.
     * 
     */
    @Override
    protected String getOutputSuffix() {
        // TESB-2944 set default suffix to .jar
        return FileConstants.JAR_FILE_SUFFIX;
    }

    @Override
    protected String getProcessType() {
        return "Route";
    }
}
