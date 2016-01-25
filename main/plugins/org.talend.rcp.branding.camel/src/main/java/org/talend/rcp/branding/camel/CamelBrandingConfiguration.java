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
package org.talend.rcp.branding.camel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.PlatformUI;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.ui.branding.DefaultBrandingConfiguration;
import org.talend.core.ui.branding.IBrandingConfiguration;
import org.talend.repository.model.IRepositoryNode;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelBrandingConfiguration extends DefaultBrandingConfiguration {

    public List<IRepositoryNode> getHiddenRepositoryCategory(IRepositoryNode nodeParent, String type) {
        List<IRepositoryNode> nodes = super.getHiddenRepositoryCategory(nodeParent, type);

        // if ("DI".equals(type)) {
        //
        // } else {
        // RepositoryNode parent = (RepositoryNode) nodeParent;
        //
        // // 1. Business process
        // RepositoryNode businessProcessNode = new RepositoryNode(null, parent, ENodeType.SYSTEM_FOLDER);
        // businessProcessNode.setProperties(EProperties.LABEL, ERepositoryObjectType.BUSINESS_PROCESS);
        // businessProcessNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.BUSINESS_PROCESS);
        // nodes.add(businessProcessNode);
        //
        // // 2. Process
        // RepositoryNode processNode = new RepositoryNode(null, parent, ENodeType.SYSTEM_FOLDER);
        // processNode.setProperties(EProperties.LABEL, ERepositoryObjectType.PROCESS);
        // processNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.PROCESS);
        // nodes.add(processNode);
        //
        // // 4.1. Routines
        // RepositoryNode routineNode = new RepositoryNode(null, parent, ENodeType.SYSTEM_FOLDER);
        // routineNode.setProperties(EProperties.LABEL, ERepositoryObjectType.ROUTINES);
        // routineNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.ROUTINES);
        // nodes.add(routineNode);
        //
        // // 5. Sql patterns
        // RepositoryNode sqlPatternNode = new RepositoryNode(null, parent, ENodeType.SYSTEM_FOLDER);
        // sqlPatternNode.setProperties(EProperties.LABEL, ERepositoryObjectType.SQLPATTERNS);
        // sqlPatternNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.SQLPATTERNS);
        // nodes.add(sqlPatternNode);
        //
        // // 6. Documentation
        // RepositoryNode docNode = new RepositoryNode(null, parent, ENodeType.SYSTEM_FOLDER);
        // docNode.setProperties(EProperties.LABEL, ERepositoryObjectType.DOCUMENTATION);
        // docNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.DOCUMENTATION);
        // nodes.add(docNode);
        //
        // // 7. Metadata
        // RepositoryNode metadataNode = new RepositoryNode(null, parent, ENodeType.STABLE_SYSTEM_FOLDER);
        // metadataNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA);
        // metadataNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA);
        // nodes.add(metadataNode);
        // }

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
     * @see org.talend.core.ui.branding.IBrandingConfiguration#getAvailableComponents()
     */
    public String[] getAvailableComponents() {
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ui.branding.IBrandingConfiguration#getAvailableLanguages()
     */
    public String[] getAvailableLanguages() {
        return new String[] { ECodeLanguage.JAVA.getName() };
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
        return IBrandingConfiguration.PERSPECTIVE_CAMEL_ID;
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

}
