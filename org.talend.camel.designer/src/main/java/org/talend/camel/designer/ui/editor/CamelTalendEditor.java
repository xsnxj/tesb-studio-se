// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.ui.action.SaveAsProcessAction;
import org.talend.designer.core.ui.editor.AbstractTalendEditor;
import org.talend.designer.core.ui.editor.ITalendJobEditor;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.nodes.NodePart;
import org.talend.designer.core.ui.editor.outline.NodeTreeEditPart;
import org.talend.designer.core.ui.editor.process.Process;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelTalendEditor extends AbstractTalendEditor implements ITalendJobEditor {

    public CamelTalendEditor() {
        this(false);

    }

    public CamelTalendEditor(boolean readOnly) {
        super(readOnly);
    }

    public Process getProcess() {
        return (Process) super.getProcess();
    }

    @Override
    public Object getAdapter(final Class type) {
        return super.getAdapter(type);
    }

    public void setParent(CamelMultiPageTalendEditor multiPageTalendEditor) {
        super.setParent(multiPageTalendEditor);
    }

    public CamelMultiPageTalendEditor getParent() {
        return (CamelMultiPageTalendEditor) super.getParent();
    }

    @Override
    public void doSaveAs() {
        SaveAsProcessAction saveAsAction = new SaveAsProcessAction(this);
        saveAsAction.run();
    }
    
    private KeyHandler sharedKeyHandler;
    
    @Override
    public KeyHandler getCommonKeyHandler() {
        if (sharedKeyHandler == null) {
            sharedKeyHandler = new KeyHandler();
            sharedKeyHandler.put(KeyStroke.getPressed(SWT.F1, 0), new Action() {

                @Override
                public void run() {
                    ISelection selection = getGraphicalViewer().getSelection();
                    if (selection != null) {
                        if (selection instanceof IStructuredSelection) {

                            Object input = ((IStructuredSelection) selection).getFirstElement();
                            Node node = null;
                            if (input instanceof NodeTreeEditPart) {
                                NodeTreeEditPart nTreePart = (NodeTreeEditPart) input;
                                node = (Node) nTreePart.getModel();
                            } else {
                                if (input instanceof NodePart) {
                                    EditPart editPart = (EditPart) input;
                                    node = (Node) editPart.getModel();
                                }
                            }
                            if (node != null) {
                                String helpLink = (String) node.getPropertyValue(EParameterName.HELP.getName());
                                String requiredHelpLink = "org.talend.esb.help." + node.getComponent().getName();
                                if (helpLink == null || "".equals(helpLink) || !requiredHelpLink.equals(helpLink)) {
                                    helpLink = "org.talend.esb.help." + node.getComponent().getName();
                                }
                                PlatformUI.getWorkbench().getHelpSystem().displayHelp(helpLink);
                            }
                        }
                    }
                }
            });
            sharedKeyHandler.put(KeyStroke.getPressed(SWT.DEL, 0), getActionRegistry().getAction(ActionFactory.DELETE.getId()));
            // deactivate the F2 shortcut as it's not used anymore
            // sharedKeyHandler.put(KeyStroke.getPressed(SWT.F2, 0),
            // getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT));
        }
        return sharedKeyHandler;
    }

}
