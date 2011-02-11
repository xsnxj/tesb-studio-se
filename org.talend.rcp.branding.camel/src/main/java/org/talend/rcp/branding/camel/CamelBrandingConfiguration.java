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
package org.talend.rcp.branding.camel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PlatformUI;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.service.ICorePerlService;
import org.talend.core.ui.branding.IActionBarHelper;
import org.talend.core.ui.branding.IBrandingConfiguration;
import org.talend.repository.model.IRepositoryNode;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelBrandingConfiguration implements IBrandingConfiguration {

    String ROUTES = "Routes";

    public List<IRepositoryNode> getHiddenRepositoryCategory(IRepositoryNode parent) {

        List<IRepositoryNode> nodes = new ArrayList<IRepositoryNode>();

        return nodes;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IActionBarHelper#fillMenuBar(org.eclipse.jface.action.IMenuManager)
     */
    public void fillMenuBar(IMenuManager menuBar) {
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IActionBarHelper#fillCoolBar(org.eclipse.jface.action.ICoolBarManager)
     */
    public void fillCoolBar(ICoolBarManager coolBar) {
        coolBar.removeAll();
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#initPerspective(org.eclipse.ui.IPageLayout)
     */
    public void initPerspective(IPageLayout layout) {
        String componentSettingViewerId = "org.talend.designer.core.ui.views.properties.ComponentSettingsView";//$NON-NLS-1$
        String navigatorId = "org.eclipse.ui.views.ResourceNavigator"; //$NON-NLS-1$
        String outlineId = "org.eclipse.ui.views.ContentOutline"; //$NON-NLS-1$
        String codeId = "org.talend.designer.core.codeView"; //$NON-NLS-1$
        String repositoryId = "org.talend.repository.views.repository"; //$NON-NLS-1$

        String runProcessViewId = "org.talend.designer.runprocess.ui.views.processview"; //$NON-NLS-1$
        String problemsViewId = "org.talend.designer.core.ui.views.ProblemsView"; //$NON-NLS-1$
        //String modulesViewId = "org.talend.designer.codegen.perlmodule.ModulesView"; //$NON-NLS-1$
        String ecosystemViewId = "org.talend.designer.components.ecosystem.ui.views.EcosystemView"; //$NON-NLS-1$
        String schedulerViewId = "org.talend.scheduler.views.Scheduler"; //$NON-NLS-1$
        String contextsViewId = "org.talend.designer.core.ui.views.ContextsView"; //$NON-NLS-1$
        String gefPaletteViewId = "org.eclipse.gef.ui.palette_view"; //$NON-NLS-1$
        String jobSettingsViewId = "org.talend.designer.core.ui.views.jobsettings.JobSettingsView"; //$NON-NLS-1$
        // String jobHierarchyViewId = "org.talend.designer.core.ui.hierarchy.JobHierarchyViewPart"; //$NON-NLS-1$

        // leftTopLayout
        IFolderLayout leftTopLayout = layout.createFolder("navigatorLayout", IPageLayout.LEFT, new Float(0.3), //$NON-NLS-1$
                IPageLayout.ID_EDITOR_AREA);
        leftTopLayout.addView(repositoryId);
        leftTopLayout.addView(navigatorId);

        // leftBottomLayout
        IFolderLayout leftBottomLayout = layout.createFolder("outlineCodeLayout", IPageLayout.BOTTOM, new Float(0.6), //$NON-NLS-1$
                repositoryId);
        leftBottomLayout.addView(outlineId);
        leftBottomLayout.addView(codeId);

        IFolderLayout rightTopLayout = layout.createFolder("paletteLayout", IPageLayout.RIGHT, new Float(0.8), //$NON-NLS-1$
                IPageLayout.ID_EDITOR_AREA);
        rightTopLayout.addView(gefPaletteViewId);

        // bottomLayout
        IFolderLayout bottomLayout = layout.createFolder("bottomLayout", IPageLayout.BOTTOM, new Float(0.6), //$NON-NLS-1$
                IPageLayout.ID_EDITOR_AREA);
        // bottomLayout.addView(propertyId);
        bottomLayout.addView(jobSettingsViewId);
        bottomLayout.addView(contextsViewId);
        bottomLayout.addView(componentSettingViewerId);

        bottomLayout.addView(runProcessViewId);
        bottomLayout.addView(problemsViewId);
        // bottomLayout.addView(modulesViewId);
        bottomLayout.addView(ecosystemViewId);
        bottomLayout.addView(schedulerViewId);
        // bottomLayout.addView(jobHierarchyViewId);
        bottomLayout.addPlaceholder("*");
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#getAvailableComponents()
     */
    public String[] getAvailableComponents() {
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#setHelper(org.talend.core.ui.branding.IActionBarHelper)
     */
    public void setHelper(IActionBarHelper helper) {

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#getHelper()
     */
    public IActionBarHelper getHelper() {
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#getAvailableLanguages()
     */
    public String[] getAvailableLanguages() {
        String[] languages;
        if (GlobalServiceRegister.getDefault().isServiceRegistered(ICorePerlService.class)) {
            languages = new String[] { ECodeLanguage.JAVA.getName(), ECodeLanguage.PERL.getName() };
        } else {
            languages = new String[] { ECodeLanguage.JAVA.getName() };
        }
        return languages;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#getJobEditorSettings()
     */
    public Map<String, Object> getJobEditorSettings() {
        return new HashMap<String, Object>();
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#isUseMailLoginCheck()
     */
    public boolean isUseMailLoginCheck() {
        return true;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#isUseProductRegistration()
     */
    public boolean isUseProductRegistration() {
        return true;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#isAllowDebugMode()
     */
    public boolean isAllowDebugMode() {
        return true;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#isUseDemoProjects()
     */
    public boolean isUseDemoProjects() {
        return false;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#getAdditionalTitle()
     */
    public String getAdditionalTitle() {
        return "";
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#setAdditionalTitle(java.lang.String)
     */
    public void setAdditionalTitle(String title) {

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#getInitialWindowPerspectiveId()
     */
    public String getInitialWindowPerspectiveId() {
        PreferenceManager pm = PlatformUI.getWorkbench().getPreferenceManager();
        pm.find("org.talend.core.prefs").remove("org.talend.repository.prefs.documentation");
        pm.find("org.talend.core.prefs").findSubNode("org.talend.designer.core.ui.preferences.SpecificSettingPreferencePage")
                .remove("org.talend.sqlbuilder.ui.prefs.sqlbuilder");
        pm.find("org.talend.core.prefs").remove("org.talend.designer.core.ui.preferences.PerformancePreferencePage");
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#setUseMailLoginCheck(boolean)
     */
    public void setUseMailLoginCheck(boolean useMainLoginCheck) {

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#setUseProductRegistration(boolean)
     */
    public void setUseProductRegistration(boolean useProductRegistration) {

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#getJobDesignName()
     */
    public String getJobDesignName() {
        return ROUTES;
    }

}
