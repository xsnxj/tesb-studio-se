// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2013 Talend – www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.designer.esb.runcontainer.ui.wizard;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.talend.designer.esb.runcontainer.util.RuntimeContainerUtil;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class AddRuntimeWizard extends Wizard {

    private String target;

    private AddRuntimeDirWizardPage dirPage;

    public AddRuntimeWizard(String target) {
        this.target = target;
        setWindowTitle("Add ESB Runtime Server");
    }

    @Override
    public void addPages() {
        dirPage = new AddRuntimeDirWizardPage(target);
        addPage(dirPage);
    }

    @Override
    public boolean performFinish() {

        if (dirPage.isCopyNeeded()) {
            try {
                String runtimeHome = dirPage.getRuntimeHome();
                getContainer().run(true, true, new IRunnableWithProgress() {

                    @Override
                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        try {
                            RuntimeContainerUtil.copyContainer(runtimeHome, target, monitor);
                        } catch (IOException e) {
                        }
                    }

                });
            } catch (Exception e) {
                MessageDialog.openError(this.getShell(), "Unable to copy runtime container", ExceptionUtils.getStackTrace(e));
                return false;
            }
        }
        // MessageDialog.openInformation(this.getShell(), "Not implemented", "Only copying is supported");
        target = dirPage.getRuntimeHome();

        // return false;
        return true;
    }

    public String getTarget() {
        return target;
    }

    // @Override
    // public boolean canFinish() {
    // return super.canFinish(); // dirPage.isPageComplete();
    // }
}
