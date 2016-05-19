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
package org.talend.camel.designer.dialog;

import java.util.ArrayList;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.talend.camel.designer.CamelDesignerPlugin;
import org.talend.camel.designer.generator.RouteComponentController;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IProcess;
import org.talend.core.ui.CoreUIPlugin;
import org.talend.core.ui.process.IGEFProcess;

/**
 * @author LiXiaopeng Dialog for cJMS ConnectionFactory selection.
 * 
 */
public class RouteComponentSelectionDialog extends Dialog {

    static class ConnectionFactoryContentProvider implements ITreeContentProvider {

        public void dispose() {

        }

        public Object[] getChildren(Object parentElement) {
            return getElements(parentElement);
        }

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof java.util.List<?>) {
                return ((java.util.List<?>) inputElement).toArray();
            }
            return new Object[0];
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return getChildren(element).length > 0;
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        }

    }

    static class ConnectionFactoryLabelProvider extends LabelProvider {

        static final Image image = CamelDesignerPlugin.imageDescriptorFromPlugin(CamelDesignerPlugin.PLUGIN_ID,
                "icons/routes_icon.png").createImage();

        @Override
        public Image getImage(Object element) {
            return image;
        }

        @Override
        public String getText(Object element) {
            if (element instanceof INode) {

                return getLabel((INode) element);

            }
            return super.getText(element);
        }
    }

    public static String getLabel(INode element) {
        IElementParameter param = element.getElementParameter("LABEL");
        String label = "";
        if (param != null && !"__UNIQUE_NAME__".equals(param.getValue())) {
            label = (String) param.getValue();
        } else {
            label = element.getUniqueName();
        }
        return label;
    }

    private java.util.List<INode> nodes;

    private TreeViewer treeViewer;

    private Object[] nodeTypes;

    private INode result;

    private INode sourceNode;

    private String selectedId;

    public RouteComponentSelectionDialog(Shell parentShell, Object[] listItemsValue, INode sourceNode) {
        super(parentShell);
        this.nodeTypes = listItemsValue;
        this.sourceNode = sourceNode;

        initModels();

    }

    @Override
    protected void buttonPressed(int buttonId) {

        if (buttonId == IDialogConstants.OK_ID) {
            setSelectedNode();
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);

        newShell.setText("Select a Node:");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setLayout(new GridLayout());

        treeViewer = new TreeViewer(container, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
        Tree tree = treeViewer.getTree();

        treeViewer.setContentProvider(new ConnectionFactoryContentProvider());
        treeViewer.setLabelProvider(new ConnectionFactoryLabelProvider());
        treeViewer.setInput(nodes);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));

        tree.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                buttonPressed(IDialogConstants.OK_ID);
            }
        });
        setSelection();

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        if (treeViewer.getTree().getItemCount() == 0) {
            getButton(OK).setEnabled(false);
        }
    }

    public void executeCommand(Command cmd) {
        IProcess process = sourceNode.getProcess();
        boolean executed = false;
        if (process != null && process instanceof IGEFProcess) {
            executed = CoreUIPlugin.getDefault().getDesignerCoreUIService().executeCommand((IGEFProcess) process, cmd);
        }
        if (!executed) {
            cmd.execute();
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(320, 450);
    }

    /**
	 * 
	 */
    private void setSelectedNode() {
        IStructuredSelection sslection = (IStructuredSelection) treeViewer.getSelection();
        result = (INode) sslection.getFirstElement();
    }

    public INode getResult() {
        return result;
    }

    /**
     * Load the JMSConnectionFactory nodes.
     */
    private void initModels() {
        java.util.List<? extends INode> allNodes = sourceNode.getProcess().getGraphicalNodes();

        nodes = new ArrayList<INode>();

        for (INode node : allNodes) {
            if (RouteComponentController.validateNodeByFilter(node, sourceNode, nodeTypes)) {
                nodes.add(node);
            }
        }
        if (nodes.size() > 0) {
            INode excludeNode = sourceNode;
            while (!excludeNode.getIncomingConnections().isEmpty()) {
                excludeNode = excludeNode.getIncomingConnections().get(0).getSource();
            }
            // then excludeNode ref root node.
            nodes.remove(excludeNode);
        }
    }

    /**
     * Initial selection.
     */
    private void setSelection() {
        if (selectedId == null) {
            return;
        }
        for (INode node : nodes) {
            if (selectedId.equals(node.getUniqueName())) {
                treeViewer.setSelection(new StructuredSelection(node));
            }

        }
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }
}
