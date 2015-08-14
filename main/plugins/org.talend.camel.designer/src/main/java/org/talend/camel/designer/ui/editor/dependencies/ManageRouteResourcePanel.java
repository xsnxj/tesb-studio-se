// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.ui.editor.dependencies;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.camel.designer.dialog.RouteResourceSelectionDialog;
import org.talend.camel.designer.ui.editor.dependencies.controls.SearchCellLabelProvider;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.designer.camel.resource.core.model.ResourceDependencyModel;
import org.talend.designer.camel.resource.core.util.RouteResourceUtil;
import org.talend.repository.model.RepositoryNode;

/**
 * @author xpli
 * 
 */
public class ManageRouteResourcePanel extends Composite {

    private static final int COL_NAME = 0;
    private static final int COL_VERSION = 1;
    private static final int COL_PATH = 2;

	private ToolItem addBtn, delBtn, copyBtn;

	private TableViewer resourcesTV;
    private final SearchCellLabelProvider labelProvider;

	public ManageRouteResourcePanel(Composite parent, boolean isReadOnly, final IMessagePart messagePart) {
		super(parent, SWT.NONE);

		setLayout(new GridLayout(2, false));

        resourcesTV = new TableViewer(this, SWT.BORDER | SWT.SINGLE
                | SWT.FULL_SELECTION);
        Table table = resourcesTV.getTable();
        resourcesTV.getTable().setEnabled(!isReadOnly);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        TableViewerColumn tableViewerColumn = new TableViewerColumn(resourcesTV, SWT.NONE, COL_NAME);
        tableViewerColumn.getColumn().setText(Messages.ManageRouteResourceDialog_routeCol);
        tableViewerColumn.getColumn().setWidth(200);

        tableViewerColumn = new TableViewerColumn(resourcesTV, SWT.NONE, COL_VERSION);
        tableViewerColumn.getColumn().setText(Messages.ManageRouteResourceDialog_Version);
        tableViewerColumn.getColumn().setWidth(100);

        tableViewerColumn.setEditingSupport(new ResourceEditingSupport(resourcesTV));

        tableViewerColumn = new TableViewerColumn(resourcesTV, SWT.NONE, COL_PATH);
        tableViewerColumn.getColumn().setText(Messages.ManageRouteResourceDialog_Path);
        tableViewerColumn.getColumn().setWidth(200);

        labelProvider = new ResourceLabelProvider(resourcesTV);
        resourcesTV.setLabelProvider(labelProvider);
        resourcesTV.setContentProvider(new ArrayContentProvider());

        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        if (!isReadOnly) {
            resourcesTV.addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    ResourceDependencyModel item = getSelectedItem();
                    delBtn.setEnabled(false);
                    if (item != null) {
                        if (item.isBuiltIn()) {
                            messagePart.setMessage(MessageFormat.format(Messages.ManageRouteResourceDialog_usedBy,
                                item.getItem().getProperty().getLabel(),
                                item.getSelectedVersion(), 
                                item.getRefNodes()));
                        } else {
                            delBtn.setEnabled(true);
                            messagePart.setMessage(item.getItem().getProperty().getLabel() +
                                '(' + item.getSelectedVersion() + ')');
                        }
                    }
                    copyBtn.setEnabled(item != null);
                }
            });
        }

        ToolBar tb = new ToolBar(this, SWT.FLAT | SWT.VERTICAL);
        tb.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        addBtn = new ToolItem(tb, SWT.PUSH);
        addBtn.setText(Messages.RouterDependenciesPanel_addBtn);
//        addBtn.setImage(ImageProvider.getImage(EImage.ADD_ICON));
        addBtn.setEnabled(!isReadOnly);

        delBtn = new ToolItem(tb, SWT.PUSH);
        delBtn.setText(Messages.RouterDependenciesPanel_removeBtn);
//        delBtn.setImage(ImageProvider.getImage(EImage.DELETE_ICON));
        delBtn.setEnabled(false);

        copyBtn = new ToolItem(tb, SWT.PUSH);
        copyBtn.setText(Messages.ManageRouteResourceDialog_CopyPath);
        copyBtn.setEnabled(false);

        initListeners();
	}

    public void setInput(ProcessItem item) {
        resourcesTV.setInput(RouteResourceUtil.getResourceDependencies(item));
    }

    public void setFilterString(String filterString) {
        labelProvider.setFilterString(filterString);
        resourcesTV.refresh();
        resourcesTV.getTable().redraw();
    }

    public void setShowBuiltIn(boolean showBuiltIn) {
        labelProvider.setShowBuiltIn(showBuiltIn);
        resourcesTV.refresh();
    }

    protected Collection<ResourceDependencyModel> getInput() {
        return (Collection<ResourceDependencyModel>) resourcesTV.getInput();
    }

	protected void addData() {

		RouteResourceSelectionDialog dialog = new RouteResourceSelectionDialog(
				Display.getCurrent().getActiveShell());
		int open = dialog.open();
		if (open == Dialog.OK) {
			RepositoryNode result = dialog.getResult();
			Item item = result.getObject().getProperty().getItem();
			if (item instanceof RouteResourceItem) {
				for (ResourceDependencyModel rsmodel : getInput()) {
					if (rsmodel.getItem().getProperty().getId()
							.equals(item.getProperty().getId())) {
						resourcesTV.setSelection(new StructuredSelection(
								rsmodel));
						return;
					}
				}
				ResourceDependencyModel model = new ResourceDependencyModel(
						(RouteResourceItem) item);
				getInput().add(model);
				resourcesTV.refresh();
				resourcesTV.setSelection(new StructuredSelection(model));
			}
		}

	}

//	@Override
	// TODO: save
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			Set<ResourceDependencyModel> models = new HashSet<ResourceDependencyModel>();
			models.addAll(getInput());
//			RouteResourceUtil.saveResourceDependency(item, models);
		}
//		super.buttonPressed(buttonId);
	}

	private void initListeners() {

        final SelectionListener selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (e.getSource() == addBtn) {
                    addData();
                } else if (e.getSource() == delBtn) {
                    deleteData();
                } else if (e.getSource() == copyBtn) {
                    copyPath();
                }
            }
        };

        addBtn.addSelectionListener(selectionListener);
		delBtn.addSelectionListener(selectionListener);
		copyBtn.addSelectionListener(selectionListener);
	}

	/**
	 * Copy class path
	 */
	protected void copyPath() {
		ResourceDependencyModel item = getSelectedItem();
		if (item != null) {
			Clipboard clipboard = new Clipboard(copyBtn.getDisplay());
			clipboard.setContents(new String[] { item.getClassPathUrl() },
					new Transfer[] { TextTransfer.getInstance() });
			//TODO:
//			MessageDialog.openInformation(getShell(), Messages.getString("ManageRouteResourceDialog.copyOK"), //$NON-NLS-1$
//					"'" + item.getClassPathUrl() //$NON-NLS-1$
//							+ Messages.getString("ManageRouteResourceDialog.copy1")); //$NON-NLS-1$
		}

	}

	protected void deleteData() {
		ResourceDependencyModel item = getSelectedItem();
		if (item != null) {
		    getInput().remove(item);
			resourcesTV.refresh();
		}
	}

    private ResourceDependencyModel getSelectedItem() {
        IStructuredSelection selection2 = (IStructuredSelection) resourcesTV.getSelection();
        return (ResourceDependencyModel) selection2.getFirstElement();
    }

    private static class ResourceLabelProvider extends SearchCellLabelProvider implements ITableLabelProvider, IFontProvider {

        public ResourceLabelProvider(final StructuredViewer structuredViewer) {
            super(structuredViewer);
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            final ResourceDependencyModel item = (ResourceDependencyModel) element;
            switch (columnIndex) {
            case COL_NAME:
                return item.toString();
            case COL_VERSION:
                return item.getSelectedVersion();
            case COL_PATH:
                return item.getClassPathUrl();
            default:
                return null;
            }
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        @Override
        public Image getImage(Object element) {
            return null;
        }

        @Override
        protected boolean isBuiltIn(Object element) {
            return ((ResourceDependencyModel) element).isBuiltIn();
        }
    }

    private static class ResourceEditingSupport extends EditingSupport {

        private ComboBoxCellEditor comboBoxCellEditor;

        public ResourceEditingSupport(ColumnViewer viewer) {
            super(viewer);
        }

        @Override
        protected void setValue(Object element, Object value) {
            ResourceDependencyModel model = (ResourceDependencyModel) element;
            model.setSelectedVersion(model.getVersions().get(
                    (Integer) value));
            getViewer().refresh(element);
        }

        @Override
        protected Object getValue(Object element) {
            ResourceDependencyModel model = (ResourceDependencyModel) element;
            return model.getVersions().indexOf(model.getSelectedVersion());
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            // http://jira.talendforge.org/browse/TESB-6584 Xiaopeng Li
            ResourceDependencyModel model = (ResourceDependencyModel) element;
            String[] array = model.getVersions().toArray(new String[0]);
            if (comboBoxCellEditor == null) {
                comboBoxCellEditor = new ComboBoxCellEditor(
                    (Composite) getViewer().getControl(), array, SWT.READ_ONLY | SWT.CENTER);
            } else {
                comboBoxCellEditor.setItems(array);
            }
            return comboBoxCellEditor;
        }

        @Override
        protected boolean canEdit(Object element) {
            ResourceDependencyModel model = (ResourceDependencyModel) element;
            return !model.isBuiltIn();
        }
    }

}
