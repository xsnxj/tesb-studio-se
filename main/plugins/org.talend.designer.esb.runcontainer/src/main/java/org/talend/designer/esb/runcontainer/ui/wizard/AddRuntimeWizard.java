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

import org.eclipse.jface.wizard.Wizard;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class AddRuntimeWizard extends Wizard {

    private AddRuntimeDirWizardPage dirPage;

    public AddRuntimeWizard() {
        setWindowTitle("Add ESB Runtime Server");
    }

    @Override
    public void addPages() {
        dirPage = new AddRuntimeDirWizardPage();

        addPage(dirPage);
    }

    @Override
    public boolean performFinish() {
        return false;
    }

    // @Override
    // public boolean canFinish() {
    // return super.canFinish(); // dirPage.isPageComplete();
    // }
}
