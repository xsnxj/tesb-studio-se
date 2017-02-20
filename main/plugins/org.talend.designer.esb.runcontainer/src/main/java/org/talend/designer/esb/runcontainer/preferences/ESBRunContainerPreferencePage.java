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
package org.talend.designer.esb.runcontainer.preferences;

import java.text.MessageFormat;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.talend.commons.ui.swt.advanced.dataeditor.LabelFieldEditor;
import org.talend.designer.esb.runcontainer.core.ESBRunContainerPlugin;
import org.talend.designer.esb.runcontainer.i18n.internal.Messages;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 *
 */
public class ESBRunContainerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public static final String ID = "org.talend.designer.esb.runcontainer.preferences.ESBRunContainerPreferencePage"; //$NON-NLS-1$

    public ESBRunContainerPreferencePage() {
        super(GRID);
        setPreferenceStore(ESBRunContainerPlugin.getDefault().getPreferenceStore());
        setDescription("Runtime Test Container"); //$NON-NLS-1$
    }

    /*
     * TESB-15283 Update container for Bonita BPM 6.5.2
     * 
     * String host = "localhost"; String jmxPort = "44444"; String karafPort = "1099"; String instanceName = "trun";
     * 
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
     */
    public void createFieldEditors() {
        addField(new StringFieldEditor(ESBRunContainerPreferenceInitializer.P_LOCAL_RUN_CONTAINER_PATH,
                "Location", getFieldEditorParent())); //$NON-NLS-1$
        addField(new StringFieldEditor("", "JMX port", getFieldEditorParent())); //$NON-NLS-1$
        addField(new StringFieldEditor("", "Runtime port", getFieldEditorParent())); //$NON-NLS-1$
        addField(new StringFieldEditor("", "Instance name", getFieldEditorParent())); //$NON-NLS-1$

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
    }

}