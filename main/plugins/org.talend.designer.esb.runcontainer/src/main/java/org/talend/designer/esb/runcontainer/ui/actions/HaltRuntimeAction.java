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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.designer.esb.runcontainer.i18n.RunContainerMessages;
import org.talend.designer.esb.runcontainer.server.RuntimeServerController;
import org.talend.designer.runprocess.ui.ERunprocessImages;

public class HaltRuntimeAction extends Action {

    public HaltRuntimeAction() {
        setToolTipText(RunContainerMessages.getString("HaltRuntimeAction.Stop")); //$NON-NLS-1$
        setImageDescriptor(ImageProvider.getImageDesc(ERunprocessImages.KILL_PROCESS_ACTION));
        setEnabled(RuntimeServerController.getInstance().isRunning());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
        try {
            dialog.run(true, true, new HaltRuntimeProgress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
