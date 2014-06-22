// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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
import org.talend.camel.designer.component.JMSExternalComponent;
import org.talend.camel.designer.component.JSMExternalComponentMain;
import org.talend.camel.designer.component.SetConnectionFactoryCommand;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.IGEFProcess;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IProcess;
import org.talend.core.service.IDesignerCoreUIService;
import org.talend.core.ui.CoreUIPlugin;

/**
 * @author LiXiaopeng Dialog for cJMS ConnectionFactory selection.
 * 
 */
public class JMSDialog extends Dialog {

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
                "icons/cJMSConnectionFactory_16.png").createImage();

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

    private JSMExternalComponentMain main;

    private java.util.List<INode> jmsConnectionFactories;

    private TreeViewer treeViewer;

    public JMSDialog(Shell parentShell, JSMExternalComponentMain main) {
        super(parentShell);
        this.main = main;

        initModels();

    }

    @Override
    protected void buttonPressed(int buttonId) {

        if (buttonId == IDialogConstants.OK_ID) {
            resetParameter();
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);

        newShell.setText("Select JMS ConnectionFactory:");
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
        treeViewer.setInput(jmsConnectionFactories);
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

    public void executeCommand(Command cmd) {
        IProcess process = this.main.getExternalComponent().getProcess();
        boolean executed = false;
        if (process != null && process instanceof IGEFProcess) {
            IDesignerCoreUIService designerCoreUIService = CoreUIPlugin.getDefault().getDesignerCoreUIService();
            if (designerCoreUIService != null) {
                executed = designerCoreUIService.executeCommand((IGEFProcess) process, cmd);
            }
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
     * @return selected ConnectionFactory node
     */
    private INode getSelectedNode() {
        IStructuredSelection sslection = (IStructuredSelection) treeViewer.getSelection();
        return (INode) sslection.getFirstElement();
    }

    /**
     * Load the JMSConnectionFactory nodes.
     */
    private void initModels() {
        JMSExternalComponent component = main.getExternalComponent();
        java.util.List<? extends INode> nodes = component.getProcess().getGraphicalNodes();
        jmsConnectionFactories = new ArrayList<INode>();

        for (INode node : nodes) {
            if (node.getComponent().getName().equals("cJMSConnectionFactory")) {
                jmsConnectionFactories.add(node);
            }
        }

    }

    /**
     * Reset ConnectionFactory field.
     */
    private void resetParameter() {
        SetConnectionFactoryCommand command = new SetConnectionFactoryCommand(main, getSelectedNode());
        executeCommand(command);

    }

    /**
     * Initial selection.
     */
    private void setSelection() {
        JMSExternalComponent jmsExternalComponent = main.getExternalComponent();
        IElementParameter elementParameter = jmsExternalComponent.getElementParameter("CONNECTION_FACOTRY_LABEL");
        if (elementParameter != null) {
            Object valueObj = elementParameter.getValue();
            if (valueObj != null) {
                String value = valueObj.toString();
                for (INode node : jmsConnectionFactories) {
                    if (value.equals(getLabel(node))) {
                        treeViewer.setSelection(new StructuredSelection(node));
                    }
                }

            }
        }

    }

}
