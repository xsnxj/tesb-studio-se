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
package org.talend.repository.view.esb.viewer.action;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.talend.repository.viewer.action.RepoNodeActionProvider;

public class ESBRepositoryNodeActionProvider extends RepoNodeActionProvider {

    public ESBRepositoryNodeActionProvider() {
    }

    @Override
    public void init(ICommonActionExtensionSite aSite) {
        super.init(aSite);
    }

    @Override
    public void fillContextMenu(IMenuManager manager) {
        super.fillContextMenu(manager);
    }

    @Override
    public void fillActionBars(IActionBars actionBars) {
        super.fillActionBars(actionBars);
    }

}
