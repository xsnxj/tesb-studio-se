package org.talend.designer.esb.runcontainer.ui.dialog;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class RuntimeInfoDialog extends Dialog {

    protected Object result;

    protected Shell shell;

    private Table table;

    private List<Map<String, String>> attrInfo;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public RuntimeInfoDialog(Shell parent, int style) {
        super(parent, style);
    }

    /**
     * DOC yyi RuntimeInfoDialog constructor comment.
     * 
     * @param activeShell
     * @param list
     * @param applicationModal
     */
    public RuntimeInfoDialog(List<Map<String, String>> list) {
        super(Display.getCurrent().getActiveShell(), SWT.SHELL_TRIM | SWT.BORDER | SWT.APPLICATION_MODAL);
        this.attrInfo = list;
    }

    /**
     * Open the dialog.
     * 
     * @return the result
     */
    public Object open() {
        createContents();
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return result;
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shell = new Shell(getParent(), getStyle());
        shell.setSize(650, 500);
        shell.setText("Runtime Information");
        shell.setLayout(new GridLayout(1, false));

        Composite parent = new Composite(shell, SWT.NONE);
        parent.setLayout(new GridLayout(1, false));
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableColumn tblName = new TableColumn(table, SWT.NONE);
        tblName.setText("Name");
        TableColumn tblValue = new TableColumn(table, SWT.NONE);
        tblValue.setText("Value");
        // TableColumn tblDesc = new TableColumn(table, SWT.NONE);
        // tblDesc.setText("Type");

        for (Map<String, String> attr : attrInfo) {
            TableItem tableItem = new TableItem(table, SWT.NONE);
            tableItem.setText(new String[] { attr.get("name"), attr.get("value") });
        }

        tblName.pack();
        tblName.setWidth(tblName.getWidth() + 50);
        tblValue.pack();
        // tblDesc.pack();
    }

}
