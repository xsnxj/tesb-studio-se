// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.services.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SearchPattern;
import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.ui.swt.formtools.Form;
import org.talend.commons.ui.swt.formtools.UtilsButton;
import org.talend.commons.ui.swt.tableviewer.TableViewerCreator;
import org.talend.commons.ui.swt.tableviewer.TableViewerCreatorNotModifiable.LAYOUT_MODE;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.connection.XmlFileConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.repository.i18n.Messages;
import org.talend.repository.ui.swt.utils.AbstractForm;

/**
 * DOC talend2 class global comment. Detailled comment
 */
public class XmlTableForm extends AbstractForm {

    /**
     * FormTable Settings.
     */
    private static final int WIDTH_GRIDDATA_PIXEL = 300;

    /**
     * FormTable Var.
     */

    private Map<IRepositoryViewObject, String> itemTableName = new HashMap<IRepositoryViewObject, String>();

    private UtilsButton selectAllTablesButton;

    private UtilsButton selectNoneTablesButton;

    /**
     * Anothers Fields.
     */
    private final List<IRepositoryViewObject> fileRepObjList;

    protected Table table;

    private int count = 0;

    private int countSuccess = 0;

    private int countPending = 0;

    ScrolledComposite scrolledCompositeFileViewer;

    private Text nameFilter;

    private Map<String, IRepositoryViewObject> selectedItemData = null;

    /**
     * DOC Administrator FileTableForm constructor comment.
     * 
     * @param parent
     * @param style
     */
    public XmlTableForm(Composite parent, List<IRepositoryViewObject> fileRepObjList) {
        super(parent, SWT.NONE);
        this.fileRepObjList = fileRepObjList;
        setupForm();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#adaptFormToReadOnly()
     */
    @Override
    protected void adaptFormToReadOnly() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#addFields()
     */
    @Override
    protected void addFields() {
        int leftCompositeWidth = 80;
        int rightCompositeWidth = WIDTH_GRIDDATA_PIXEL - leftCompositeWidth;
        int headerCompositeHeight = 60;
        int tableSettingsCompositeHeight = 90;
        int tableCompositeHeight = 100;

        int height = headerCompositeHeight + tableSettingsCompositeHeight + tableCompositeHeight;

        // Main Composite : 2 columns
        Composite mainComposite = Form.startNewDimensionnedGridLayout(this, 1, leftCompositeWidth + rightCompositeWidth, height);
        mainComposite.setLayout(new GridLayout(1, false));
        GridData gridData = new GridData(GridData.FILL_BOTH);
        mainComposite.setLayoutData(gridData);

        Composite rightComposite = Form.startNewDimensionnedGridLayout(mainComposite, 1, rightCompositeWidth, height);

        // Group Table Settings
        Group groupTableSettings = Form.createGroup(rightComposite, 1, "Select Schema to rewrite", tableSettingsCompositeHeight);

        // Composite TableSettings
        Composite compositeTableSettings = Form.startNewDimensionnedGridLayout(groupTableSettings, 1, rightCompositeWidth,
                tableSettingsCompositeHeight);
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = rightCompositeWidth;
        gridData.horizontalSpan = 3;

        Composite filterComposite = new Composite(compositeTableSettings, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        filterComposite.setLayout(gridLayout);
        GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
        filterComposite.setLayoutData(gridData2);
        Label label = new Label(filterComposite, SWT.NONE);
        label.setText(Messages.getString("SelectorTableForm.nameFilter")); //$NON-NLS-1$
        nameFilter = new Text(filterComposite, SWT.BORDER);
        nameFilter.setToolTipText(Messages.getString("SelectorTableForm.enterType")); //$NON-NLS-1$
        nameFilter.setEditable(true);
        gridData2 = new GridData(GridData.FILL_HORIZONTAL);
        nameFilter.setLayoutData(gridData2);
        scrolledCompositeFileViewer = new ScrolledComposite(compositeTableSettings, SWT.H_SCROLL | SWT.V_SCROLL | SWT.NONE);
        scrolledCompositeFileViewer.setExpandHorizontal(true);
        scrolledCompositeFileViewer.setExpandVertical(true);
        GridData gridData1 = new GridData(GridData.FILL_BOTH);
        int width = 375;
        int hight = 200;
        gridData1.widthHint = width;
        gridData1.heightHint = hight;
        gridData1.horizontalSpan = 2;
        scrolledCompositeFileViewer.setLayoutData(gridData1);

        createTable();

        // Composite retreiveSchema
        Composite compositeRetreiveSchemaButton = Form.startNewGridLayout(compositeTableSettings, 3, false, SWT.CENTER,
                SWT.BOTTOM);

        GC gc = new GC(compositeRetreiveSchemaButton);
        // Button Create Table
        String displayStr = Messages.getString("SelectorTableForm.selectAllTables"); //$NON-NLS-1$
        Point buttonSize = gc.stringExtent(displayStr);
        selectAllTablesButton = new UtilsButton(compositeRetreiveSchemaButton, displayStr, buttonSize.x + 12, HEIGHT_BUTTON_PIXEL); //$NON-NLS-1$

        displayStr = Messages.getString("SelectorTableForm.selectNoneTables"); //$NON-NLS-1$
        buttonSize = gc.stringExtent(displayStr);
        selectNoneTablesButton = new UtilsButton(compositeRetreiveSchemaButton, displayStr, buttonSize.x + 12,
                HEIGHT_BUTTON_PIXEL);

        // Button Check Connection
        displayStr = Messages.getString("DatabaseTableForm.checkConnection"); //$NON-NLS-1$
        buttonSize = gc.stringExtent(displayStr);

        createAllItem(null);
        gc.dispose();
    }

    /**
     * DOC qzhang Comment method "createTable".
     */
    private void createTable() {
        // List Table
        TableViewerCreator tableViewerCreator = new TableViewerCreator(scrolledCompositeFileViewer);
        tableViewerCreator.setColumnsResizableByDefault(true);
        tableViewerCreator.setBorderVisible(true);
        tableViewerCreator.setLayoutMode(LAYOUT_MODE.FILL_HORIZONTAL);
        tableViewerCreator.setCheckboxInFirstColumn(true);
        tableViewerCreator.setFirstColumnMasked(true);
        tableViewerCreator.init();
        int columnWidth1 = 300;
        table = tableViewerCreator.createTable();
        tableViewerCreator.setReadOnly(true);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        TableColumn tableName = new TableColumn(table, SWT.NONE);
        tableName.setText(Messages.getString("SelectorTableForm.TableName")); //$NON-NLS-1$
        tableName.setWidth(columnWidth1);
        scrolledCompositeFileViewer.setContent(table);
        scrolledCompositeFileViewer.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        tableViewerCreator.setTriggerEditorActivate(false);
    }

    private void createAllItem(Map<IRepositoryViewObject, String> newList) {
        table.clearAll();
        // fill the combo
        if (newList != null && !newList.isEmpty()) {
            // itemTableName = newList;
            Iterator<Entry<IRepositoryViewObject, String>> iterator = newList.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<IRepositoryViewObject, String> entry = iterator.next();
                if (entry != null) {
                    String nameTable = entry.getValue();
                    IRepositoryViewObject obj = entry.getKey();
                    if (obj != null && nameTable != null) {
                        Connection conn = ((ConnectionItem) obj.getProperty().getItem()).getConnection();
                        TableItem item = new TableItem(table, SWT.NONE);
                        item.setText(0, nameTable);
                        item.setData(obj);
                        if (conn instanceof XmlFileConnection) {
                            item.setImage(ImageProvider.getImage(ECoreImage.METADATA_FILE_XML_ICON));
                        }
                    }
                }
            }
        } else {
            for (IRepositoryViewObject obj : getRepositoryViewObjects()) {
                Connection conn = ((ConnectionItem) obj.getProperty().getItem()).getConnection();
                if (ConnectionHelper.getTables(conn).size() > 0) {
                    Object[] array = ConnectionHelper.getTables(conn).toArray();
                    if (array.length > 0) {
                        String nameTable = ((MetadataTable) array[0]).getLabel();
                        if (nameTable != null) {
                            nameTable = obj.getLabel() + "-" + nameTable;
                            itemTableName.put(obj, nameTable);
                            TableItem item = new TableItem(table, SWT.NONE);
                            item.setText(0, nameTable);
                            item.setData(obj);
                            if (conn instanceof XmlFileConnection) {
                                item.setImage(ImageProvider.getImage(ECoreImage.METADATA_FILE_XML_ICON));
                            }
                        }
                    }
                }
            }
        }
        if (getSelectionItems().size() <= 0) {
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#addFieldsListeners()
     */
    @Override
    protected void addFieldsListeners() {
        nameFilter.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                // List<String> newList = new ArrayList<String>();
                Map<IRepositoryViewObject, String> newList = new HashMap<IRepositoryViewObject, String>();
                String pattern = nameFilter.getText();
                SearchPattern matcher = new SearchPattern();
                matcher.setPattern(pattern);
                Iterator<Entry<IRepositoryViewObject, String>> iterator = itemTableName.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<IRepositoryViewObject, String> entry = iterator.next();
                    if (entry != null) {
                        String tableName = entry.getValue();
                        if (tableName != null && matcher.matches(tableName)) {
                            newList.put(entry.getKey(), tableName);
                        }
                    }
                }
                for (int j = 0; j < table.getItemCount(); j++) {
                    TableItem item = table.getItem(j);
                    // item.setImage(image);
                    if (item.getChecked()) {
                        item.setChecked(false);
                    }
                }
                table.clearAll();
                if (!table.isDisposed()) {
                    table.dispose();
                    table = null;
                    createTable();
                    addTableListener();
                }
                createAllItem(newList);
            }

        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#addUtilsButtonListeners()
     */
    @Override
    protected void addUtilsButtonListeners() {
        selectAllTablesButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                // updateStatus(IStatus.ERROR, null);
                TableItem[] tableItems = table.getItems();
                int size = tableItems.length;
                for (int i = 0; i < tableItems.length; i++) {
                    TableItem tableItem = tableItems[i];
                    if (!tableItem.getChecked()) {
                    } else {
                        // updateStatus(IStatus.OK, null);
                    }
                    tableItem.setChecked(true);
                }
                if (size > 0) {
                }
                setSelectionItems();
                updateStatus(IStatus.OK, null);
            }
        });

        selectNoneTablesButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                count = 0;
                countSuccess = 0;
                countPending = 0;
                TableItem[] tableItems = table.getItems();
                for (int i = 0; i < tableItems.length; i++) {
                    TableItem tableItem = tableItems[i];
                    if (tableItem.getChecked()) {
                        tableItem.setChecked(false);
                    }
                }
                setSelectionItems();
                updateStatus(IStatus.ERROR, null);
            }

        });
        addTableListener();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#checkFieldsValue()
     */
    @Override
    protected boolean checkFieldsValue() {
        // TODO Auto-generated method stub
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#initialize()
     */
    @Override
    protected void initialize() {
        // TODO Auto-generated method stub

    }

    //
    // public List<ConnectionItem> getConnectionItems() {
    // List<ConnectionItem> connectionItems = new ArrayList<ConnectionItem>();
    // for (IRepositoryViewObject obj : getRepositoryViewObjects()) {
    // connectionItems.add((ConnectionItem) obj.getProperty().getItem());
    // }
    // return connectionItems;
    // }

    public List<IRepositoryViewObject> getRepositoryViewObjects() {
        return this.fileRepObjList;
    }

    public void setSelectionItems() {
        Map<String, IRepositoryViewObject> itemMap = new HashMap<String, IRepositoryViewObject>();
        TableItem[] items = table.getItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i].getChecked()) {
                if (items[i].getData() instanceof IRepositoryViewObject) {
                    IRepositoryViewObject repObj = (IRepositoryViewObject) items[i].getData();
                    itemMap.put(items[i].getText(), repObj);
                }
            }
        }
        selectedItemData = itemMap;
    }

    public Map<String, IRepositoryViewObject> getSelectionItems() {
        if (selectedItemData == null) {
            selectedItemData = new HashMap<String, IRepositoryViewObject>();
        }
        return selectedItemData;
    }

    private void addTableListener() {
        // Event checkBox action
        table.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (e.detail == SWT.CHECK) {
                    TableItem tableItem = (TableItem) e.item;
                    boolean promptNeeded = tableItem.getChecked();
                    if (promptNeeded || (getSelectionItems().size() > 0)) {

                    } else {

                    }
                    setSelectionItems();
                    if (getSelectionItems().isEmpty()) {
                        updateStatus(IStatus.ERROR, null);
                    } else {
                        updateStatus(IStatus.OK, null);
                    }
                }
            }
        });
    }

}
