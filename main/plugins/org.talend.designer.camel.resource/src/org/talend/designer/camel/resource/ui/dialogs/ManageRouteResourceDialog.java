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
package org.talend.designer.camel.resource.ui.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.camel.designer.dialog.RouteResourceSelectionDialog;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.properties.Item;
import org.talend.designer.camel.resource.core.model.ResourceDependencyModel;
import org.talend.designer.camel.resource.core.util.RouteResourceUtil;
import org.talend.designer.camel.resource.i18n.Messages;
import org.talend.designer.camel.resource.ui.providers.ResourceContentProvider;
import org.talend.designer.camel.resource.ui.providers.ResourceLabelProvider;
import org.talend.repository.model.RepositoryNode;

/**
 * @author xpli
 * 
 */
public class ManageRouteResourceDialog extends TitleAreaDialog {

	private Button addBtn, delBtn;

	private List<ResourceDependencyModel> selectedModels;

	private TableViewer resourcesTV;

	private Item item;

	private Button copyBtn;

	private boolean isReadOnly;

	public ManageRouteResourceDialog(Shell parentShell, Item item, boolean readOnly) {
		super(parentShell);
		this.item = item;
		this.isReadOnly = readOnly;
		init();
	}

	protected void addData() {

		RouteResourceSelectionDialog dialog = new RouteResourceSelectionDialog(
				Display.getCurrent().getActiveShell());
		int open = dialog.open();
		if (open == Dialog.OK) {
			RepositoryNode result = dialog.getResult();
			Item item = result.getObject().getProperty().getItem();
			if (item instanceof RouteResourceItem) {
				for (ResourceDependencyModel rsmodel : selectedModels) {
					if (rsmodel.getItem().getProperty().getId()
							.equals(item.getProperty().getId())) {
						resourcesTV.setSelection(new StructuredSelection(
								rsmodel));
						return;
					}
				}
				ResourceDependencyModel model = new ResourceDependencyModel(
						(RouteResourceItem) item);
				selectedModels.add(model);
				resourcesTV.refresh();
				resourcesTV.setSelection(new StructuredSelection(model));
			}
		}

	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			Set<ResourceDependencyModel> models = new HashSet<ResourceDependencyModel>();
			models.addAll(selectedModels);
			RouteResourceUtil.saveResourceDependency(item, models);
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("ManageRouteResourceDialog.text")); //$NON-NLS-1$
	}

	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		setHelpAvailable(false);
		setTitle(Messages.getString("ManageRouteResourceDialog.title")); //$NON-NLS-1$
		if(isReadOnly){
			setMessage(Messages.getString("ManageRouteResourceDialog.itemLockedByOther"));
		}else{
			setMessage(Messages.getString("ManageRouteResourceDialog.message")); //$NON-NLS-1$
		}

		Composite container = (Composite) super.createDialogArea(parent);

		Composite area = new Composite(container, SWT.NONE);
		area.setLayout(new GridLayout(2, false));
		area.setLayoutData(new GridData(GridData.FILL_BOTH));

		resourcesTV = new TableViewer(area, SWT.BORDER | SWT.SINGLE
				| SWT.FULL_SELECTION);
		Table table = resourcesTV.getTable();
		resourcesTV.getTable().setEnabled(!isReadOnly);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				resourcesTV, SWT.NONE);
		tableViewerColumn.getColumn().setText(Messages.getString("ManageRouteResourceDialog.routeCol")); //$NON-NLS-1$
		tableViewerColumn.getColumn().setWidth(200);
		tableViewerColumn.getColumn().setAlignment(SWT.LEFT);

		tableViewerColumn = new TableViewerColumn(resourcesTV, SWT.NONE);
		tableViewerColumn.getColumn().setText(Messages.getString("ManageRouteResourceDialog.Version")); //$NON-NLS-1$
		tableViewerColumn.getColumn().setWidth(100);
		tableViewerColumn.getColumn().setAlignment(SWT.LEFT);

		tableViewerColumn.setEditingSupport(new EditingSupport(resourcesTV) {

			ComboBoxCellEditor comboBoxCellEditor;

			@Override
			protected void setValue(Object element, Object value) {
				ResourceDependencyModel model = (ResourceDependencyModel) element;
				model.setSelectedVersion(model.getVersions().get(
						(Integer) value));
				resourcesTV.refresh(element);
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
					comboBoxCellEditor = new ComboBoxCellEditor(resourcesTV
							.getTable(), array, SWT.READ_ONLY | SWT.CENTER);
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
		});

		tableViewerColumn = new TableViewerColumn(resourcesTV, SWT.NONE);
		tableViewerColumn.getColumn().setText(Messages.getString("ManageRouteResourceDialog.Type")); //$NON-NLS-1$
		tableViewerColumn.getColumn().setWidth(100);
		tableViewerColumn.getColumn().setAlignment(SWT.LEFT);

		tableViewerColumn = new TableViewerColumn(resourcesTV, SWT.NONE);
		tableViewerColumn.getColumn().setText(Messages.getString("ManageRouteResourceDialog.Path")); //$NON-NLS-1$
		tableViewerColumn.getColumn().setWidth(200);
		tableViewerColumn.getColumn().setAlignment(SWT.LEFT);

		resourcesTV.setLabelProvider(new ResourceLabelProvider(resourcesTV
				.getTable()));
		resourcesTV.setContentProvider(new ResourceContentProvider());
		resourcesTV.setInput(selectedModels);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(gridData);

		Composite buttonComp = new Composite(area, SWT.NONE);
		buttonComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		buttonComp.setLayout(new GridLayout(1, false));

		GridData gd = new GridData();
		gd.widthHint = 90;

		addBtn = new Button(buttonComp, SWT.PUSH);
		addBtn.setImage(ImageProvider.getImage(EImage.ADD_ICON));
		addBtn.setText(Messages.getString("ManageRouteResourceDialog.Add")); //$NON-NLS-1$
		addBtn.setLayoutData(gd);

		delBtn = new Button(buttonComp, SWT.PUSH);
		delBtn.setText(Messages.getString("ManageRouteResourceDialog.Remove")); //$NON-NLS-1$
		delBtn.setLayoutData(gd);
		delBtn.setImage(ImageProvider.getImage(EImage.DELETE_ICON));

		copyBtn = new Button(buttonComp, SWT.PUSH);
		copyBtn.setText(Messages.getString("ManageRouteResourceDialog.CopyPath")); //$NON-NLS-1$
		copyBtn.setLayoutData(gd);

		addBtn.setEnabled(!isReadOnly);
		delBtn.setEnabled(!isReadOnly);
		copyBtn.setEnabled(!isReadOnly);
		
		initListeners();

		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button createButton = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		createButton.setFocus();
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	private void initListeners() {
		resourcesTV
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						refreshButtonState();
					}
				});

		addBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addData();
			}
		});

		delBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteData();
			}
		});

		copyBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				copyPath();
			}
		});

	}

	protected void refreshButtonState() {
		if(isReadOnly){
			return;
		}
		ResourceDependencyModel item = getSelectiedItem();
		if (item != null) {
			if (item.isBuiltIn()) {
				delBtn.setEnabled(false);
				setMessage(item.getItem().getProperty().getLabel() + "(" //$NON-NLS-1$
						+ item.getSelectedVersion() + Messages.getString("ManageRouteResourceDialog.usedBy") //$NON-NLS-1$
						+ item.getRefNodes());
			} else {
				delBtn.setEnabled(true);
				setMessage(item.getItem().getProperty().getLabel() + "(" //$NON-NLS-1$
						+ item.getSelectedVersion() + ")"); //$NON-NLS-1$
			}
		}

	}

	/**
	 * Copy class path
	 */
	protected void copyPath() {
		ResourceDependencyModel item = getSelectiedItem();
		if (item != null) {
			Clipboard clipboard = new Clipboard(copyBtn.getDisplay());
			clipboard.setContents(new String[] { item.getClassPathUrl() },
					new Transfer[] { TextTransfer.getInstance() });
			MessageDialog.openInformation(getShell(), Messages.getString("ManageRouteResourceDialog.copyOK"), //$NON-NLS-1$
					"'" + item.getClassPathUrl() //$NON-NLS-1$
							+ Messages.getString("ManageRouteResourceDialog.copy1")); //$NON-NLS-1$
		}

	}

	protected void deleteData() {
		ResourceDependencyModel item = getSelectiedItem();
		if (item != null) {
			if (item.isBuiltIn()) {
				MessageDialog.openWarning(getShell(), Messages.getString("ManageRouteResourceDialog.warning"), //$NON-NLS-1$
						Messages.getString("ManageRouteResourceDialog.warningMesage1") + item.getRefNodes() //$NON-NLS-1$
								+ Messages.getString("ManageRouteResourceDialog.warningMesage2")); //$NON-NLS-1$
				return;
			}
			selectedModels.remove(item);
			resourcesTV.refresh();
		}
	}

	@Override
	protected Point getInitialSize() {
		return new Point(800, 480);
	}

	private ResourceDependencyModel getSelectiedItem() {
		IStructuredSelection selection2 = (IStructuredSelection) resourcesTV
				.getSelection();
		return (ResourceDependencyModel) selection2.getFirstElement();
	}

	private void init() {
		selectedModels = new ArrayList<ResourceDependencyModel>();
		selectedModels.addAll(RouteResourceUtil.getResourceDependencies(item));
	}

}
