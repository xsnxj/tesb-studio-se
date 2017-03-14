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
package org.talend.designer.esb.runcontainer.ui.console;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;
import org.talend.designer.esb.runcontainer.server.RuntimeServerController;
import org.talend.designer.esb.runcontainer.server.RuntimeStatusChangeListener;
import org.talend.designer.esb.runcontainer.ui.actions.HaltRuntimeAction;
import org.talend.designer.esb.runcontainer.ui.actions.OpenRuntimeInfoAction;
import org.talend.designer.esb.runcontainer.ui.actions.RebootRuntimeAction;
import org.talend.designer.esb.runcontainer.ui.actions.StartRuntimeAction;

public class ESBRuntimeConsolePageParticipant implements IConsolePageParticipant {

    private StartRuntimeAction startRuntimeAction;

    private RebootRuntimeAction rebootRuntimeAction;

    private HaltRuntimeAction haltRuntimeAction;

    private OpenRuntimeInfoAction openRuntimeInfoAction;

    private RuntimeStatusChangeListener serverListener;

    @Override
    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void init(IPageBookViewPage page, IConsole console) {

        startRuntimeAction = new StartRuntimeAction();
        rebootRuntimeAction = new RebootRuntimeAction();
        haltRuntimeAction = new HaltRuntimeAction();
        openRuntimeInfoAction = new OpenRuntimeInfoAction();
        serverListener = new RuntimeStatusChangeListener() {

            @Override
            public void stopRunning() {
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        startRuntimeAction.setEnabled(true);
                        haltRuntimeAction.setEnabled(false);
                    }
                });
            }

            @Override
            public void startRunning() {
                // TODO Auto-generated method stub
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        startRuntimeAction.setEnabled(false);
                        haltRuntimeAction.setEnabled(true);
                    }
                });
            }

            @Override
            public void featureUninstalled(int id) {

            }

            @Override
            public void featureInstalled(int id) {

            }
        };
        RuntimeServerController.getInstance().addStatusChangeListener(serverListener);

        IActionBars actionBars = page.getSite().getActionBars();
        configureToolBar(actionBars.getToolBarManager());
    }

    private void configureToolBar(IToolBarManager mgr) {
        mgr.appendToGroup(IConsoleConstants.LAUNCH_GROUP, startRuntimeAction);
        // mgr.prependToGroup(IConsoleConstants.LAUNCH_GROUP, rebootRuntimeAction);
        mgr.appendToGroup(IConsoleConstants.LAUNCH_GROUP, haltRuntimeAction);
        mgr.appendToGroup(IConsoleConstants.OUTPUT_GROUP, openRuntimeInfoAction);
    }

    @Override
    public void dispose() {
        // startRuntimeAction = null;
        // rebootRuntimeAction = null;
        // haltRuntimeAction = null;
        // openRuntimeInfoAction = null;
    }

    @Override
    public void activated() {
        // TODO Auto-generated method stub
    }

    @Override
    public void deactivated() {
        // TODO Auto-generated method stub
    }

}
