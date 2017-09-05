// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.talend.repository.ui.views.IRepositoryView;

/**
 * DOC nrousseau class global comment. Detailled comment
 */
public class CamelPerspectiveFactory implements IPerspectiveFactory {

    public static final String ID = "org.talend.camel.perspective";

	/*
     * (non-Jsdoc)
     * 
     * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
     */
    public void createInitialLayout(IPageLayout layout) {
        String componentSettingViewerId = "org.talend.designer.core.ui.views.properties.ComponentSettingsView";//$NON-NLS-1$
        String navigatorId = "org.eclipse.ui.views.ResourceNavigator"; //$NON-NLS-1$
        String outlineId = "org.eclipse.ui.views.ContentOutline"; //$NON-NLS-1$
        //        String codeId = "org.talend.designer.core.codeView"; //$NON-NLS-1$
        String repositoryId = IRepositoryView.VIEW_ID;

        String runProcessViewId = "org.talend.designer.runprocess.ui.views.processview"; //$NON-NLS-1$
        String problemsViewId = "org.talend.designer.core.ui.views.ProblemsView"; //$NON-NLS-1$
        //String modulesViewId = "org.talend.designer.codegen.perlmodule.ModulesView"; //$NON-NLS-1$
        // String ecosystemViewId = "org.talend.designer.components.ecosystem.ui.views.EcosystemView"; //$NON-NLS-1$
        //String schedulerViewId = "org.talend.scheduler.views.Scheduler"; //$NON-NLS-1$
        String contextsViewId = "org.talend.designer.core.ui.views.ContextsView"; //$NON-NLS-1$
        String gefPaletteViewId = "org.eclipse.gef.ui.palette_view"; //$NON-NLS-1$
        String jobSettingsViewId = "org.talend.designer.core.ui.views.jobsettings.JobSettingsView"; //$NON-NLS-1$
        // String jobHierarchyViewId = "org.talend.designer.core.ui.hierarchy.JobHierarchyViewPart"; //$NON-NLS-1$
        
        //ADDED for TESB-7887 By GangLiu
        String springView = "org.talend.camel.designer.spring.view";

        // leftTopLayout
        IFolderLayout leftTopLayout = layout.createFolder("navigatorLayout", IPageLayout.LEFT, new Float(0.3), //$NON-NLS-1$
                IPageLayout.ID_EDITOR_AREA);
        leftTopLayout.addView(repositoryId);
        leftTopLayout.addView(navigatorId);

        // leftBottomLayout
        IFolderLayout leftBottomLayout = layout.createFolder("outlineCodeLayout", IPageLayout.BOTTOM, new Float(0.6), //$NON-NLS-1$
                repositoryId);
        leftBottomLayout.addView(outlineId);
        // leftBottomLayout.addView(codeId);

        IFolderLayout rightTopLayout = layout.createFolder("paletteLayout", IPageLayout.RIGHT, new Float(0.8), //$NON-NLS-1$
                IPageLayout.ID_EDITOR_AREA);
        rightTopLayout.addView(gefPaletteViewId);

        // bottomLayout
        IFolderLayout bottomLayout = layout.createFolder("bottomLayout", IPageLayout.BOTTOM, new Float(0.6), //$NON-NLS-1$
                IPageLayout.ID_EDITOR_AREA);
        // bottomLayout.addView(propertyId);
        bottomLayout.addView(jobSettingsViewId);
        bottomLayout.addView(contextsViewId);
        
        //ADDED for TESB-7887 By GangLiu
        bottomLayout.addView(springView);
        
        bottomLayout.addView(componentSettingViewerId);

        bottomLayout.addView(runProcessViewId);
        
        
        bottomLayout.addView(problemsViewId);
        // bottomLayout.addView(modulesViewId);
        // bottomLayout.addView(ecosystemViewId);
        // bottomLayout.addView(schedulerViewId);
        // bottomLayout.addView(jobHierarchyViewId);
        bottomLayout.addPlaceholder("*");
    }

}
