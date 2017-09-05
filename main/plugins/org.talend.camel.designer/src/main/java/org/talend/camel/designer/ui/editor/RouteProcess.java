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
package org.talend.camel.designer.ui.editor;

import java.io.IOException;

import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.core.model.components.ComponentCategory;
import org.talend.core.model.properties.Property;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.ui.editor.process.Process;

/**
 * DOC nrousseau class global comment. Detailled comment
 */
public class RouteProcess extends Process {

    private String springContent = null;

    public RouteProcess(Property property) {
        super(property);
        loadSpringContent();
        setComponentsType(ComponentCategory.CATEGORY_4_CAMEL.getName());
    }

    @Override
    protected void createJobSettingsParameters() {
        // no job settings for routes
    }

    @Override
    public String getBaseHelpLink() {
        return "org.talend.esb.help.";
    }

    // ADDED for TESB-7887 By GangLiu
    /*
     * only routeBuilder needs spring
     */
    @Override
    public boolean needsSpring() {
        return true;
    }

    @Override
    public String getSpringContent() {
        return springContent;
    }

    public void setSpringContent(String springContent) {
        this.springContent = springContent;
    }

    /*
     * used to load spring content when opening Editor
     */
    private final void loadSpringContent() {
        springContent = ((CamelProcessItem) getProperty().getItem()).getSpringContent();
    }

    @Override
    public ProcessType saveXmlFile() throws IOException {
        ((CamelProcessItem) getProperty().getItem()).setSpringContent(springContent);
        return super.saveXmlFile();
    }
    // END ADDED for TESB-7887
}
