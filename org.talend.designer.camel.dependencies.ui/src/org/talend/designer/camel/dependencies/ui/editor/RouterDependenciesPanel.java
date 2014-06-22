package org.talend.designer.camel.dependencies.ui.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.designer.camel.dependencies.core.model.ExportPackage;
import org.talend.designer.camel.dependencies.core.model.IDependencyItem;
import org.talend.designer.camel.dependencies.core.model.OsgiDependencies;
import org.talend.designer.camel.dependencies.ui.Messages;
import org.talend.designer.camel.dependencies.ui.dialog.NewExportPackageDialog;
import org.talend.designer.camel.dependencies.ui.dialog.NewOrEditDependencyDialog;

/**
 * uneditable if readonly
 */
public class RouterDependenciesPanel extends Composite implements
		SelectionListener, ISelectionChangedListener {

	private int type;
	private RouterDependenciesTableViewer tableViewer;
	
	private Button addBtn;
	private Button remBtn;
	private Button upBtn;
	private Button downBtn;
	private Button editBtn;

	private List<IRouterDependenciesChangedListener> listeners = new ArrayList<IRouterDependenciesChangedListener>();
	private Button selectAll;
	private Button deselectAll;
	private boolean isReadOnly;

	public RouterDependenciesPanel(Composite parent, int style, int type,
			FormToolkit toolkit, boolean isReadOnly) {
		super(parent, SWT.NONE);
		this.type = type;
		this.isReadOnly = isReadOnly;
		initialize(toolkit, style);
	}

	private void initialize(FormToolkit toolkit, int style) {
		setLayout(new GridLayout(2, false));
		setLayoutData(new GridData(GridData.FILL_BOTH));

		Table table = toolkit.createTable(this, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL | SWT.H_SCROLL | style);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.detail == SWT.CHECK){
					((IDependencyItem)e.item.getData()).setChecked(((TableItem)e.item).getChecked());
					fireDependenciesChangedListener();
				}
			}
		});
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.NONE) {
					if (remBtn != null && remBtn.isEnabled()
							&& e.keyCode == SWT.DEL) {
						removeItems();
					} else if (e.keyCode == SWT.INSERT) {
						addNewItem();
					} else if (editBtn != null && editBtn.isEnabled()
							&& e.keyCode == SWT.F2) {
						editSelected();
					}
				}
			}
		});
		if(isReadOnly){
			table.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		}

		tableViewer = new RouterDependenciesTableViewer(table);
		tableViewer.addSelectionChangedListener(this);

		Composite bc = toolkit.createComposite(this);
		GridLayout layout = new GridLayout(1, false);
		layout.marginLeft = 0;
		layout.marginRight = 0;
		bc.setLayout(layout);

		if (type != IDependencyItem.CLASS_PATH) {
			addBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_addBtn, SWT.NONE);
			addBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			addBtn.addSelectionListener(this);
			addBtn.setEnabled(!isReadOnly);

			remBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_removeBtn, SWT.NONE);
			remBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			remBtn.addSelectionListener(this);
			remBtn.setEnabled(false);

			editBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_editBtn, SWT.NONE);
			editBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			editBtn.setEnabled(false);
			editBtn.addSelectionListener(this);
		}
		
		if (type != IDependencyItem.EXPORT_PACKAGE && type != IDependencyItem.CLASS_PATH) {
			upBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_upBtn, SWT.NONE);
			upBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			upBtn.addSelectionListener(this);
			upBtn.setEnabled(false);
	
			downBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_downBtn, SWT.NONE);
			downBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			downBtn.addSelectionListener(this);
			downBtn.setEnabled(false);
		}
		
		if((style & SWT.CHECK) != 0){
			selectAll = toolkit.createButton(bc, Messages.RouterDependenciesPanel_selectAll, SWT.NONE);
			selectAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			selectAll.addSelectionListener(this);
			selectAll.setEnabled(!isReadOnly);
	
			deselectAll = toolkit.createButton(bc, Messages.RouterDependenciesPanel_deselectAll, SWT.NONE);
			deselectAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			deselectAll.addSelectionListener(this);
			deselectAll.setEnabled(!isReadOnly);
		}
		
	}

	public RouterDependenciesTableViewer getTableViewer() {
		return tableViewer;
	}

	public void addDependenciesChangedListener(
			IRouterDependenciesChangedListener l) {
		listeners.add(l);
	}

	public void removeDependenciesChangedListener(
			IRouterDependenciesChangedListener l) {
		listeners.remove(l);
	}

	protected void fireDependenciesChangedListener() {
		for (IRouterDependenciesChangedListener l : listeners) {
			l.dependencesChanged();
		}
	}

	@Override
	protected void checkSubclass() {
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (addBtn != null && e.getSource() == addBtn) {
			addNewItem();
		}else if(remBtn !=null && e.getSource() == remBtn){
			removeItems();
		}else if(upBtn != null && e.getSource() == upBtn){
			moveUp();
		}else if(downBtn != null && e.getSource() == downBtn){
			moveDown();
		}else if(editBtn != null && e.getSource() == editBtn){
			editSelected();
		}else if(deselectAll != null && e.getSource() == deselectAll){
			deselectAll();
		}else if(selectAll != null && e.getSource() == selectAll){
			selectAll();
		}
	}

	private void selectAll() {
		boolean hasChanged = false;
		List<?> input = (List<?>) getTableViewer().getInput();
		for (Object o : input) {
			if (o != null && o instanceof IDependencyItem) {
				if(((IDependencyItem)o).isChecked()){
					continue;
				}
				hasChanged = true;
				((IDependencyItem) o).setChecked(true);
			}
		}
		TableItem[] items = getTableViewer().getTable().getItems();
		for(TableItem ti:items){
			if(ti.getChecked()){
				continue;
			}
			ti.setChecked(true);
		}
		if(hasChanged){
			fireDependenciesChangedListener();
		}
	}

	private void deselectAll() {
		boolean hasChanged = false;
		List<?> input = (List<?>) getTableViewer().getInput();
		for (Object o : input) {
			if (o != null && o instanceof IDependencyItem) {
				if(!((IDependencyItem)o).isChecked()){
					continue;
				}
				hasChanged = true;
				((IDependencyItem) o).setChecked(false);
			}
		}
		TableItem[] items = getTableViewer().getTable().getItems();
		for(TableItem ti:items){
			ti.setChecked(false);
		}
		if(hasChanged){
			fireDependenciesChangedListener();
		}
	}

	private void editSelected() {
		switch (type) {
		case IDependencyItem.REQUIRE_BUNDLE:
		case IDependencyItem.IMPORT_PACKAGE:
			editRequiredItem();
			break;
		case IDependencyItem.EXPORT_PACKAGE:
			editExportPackage();
		default:
			break;
		}
	}

	private void editExportPackage() {
		ExportPackage selected = (ExportPackage)((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
		NewExportPackageDialog dialog = new NewExportPackageDialog(getShell(), selected, (List<?>) tableViewer.getInput());
		if(Dialog.OK == dialog.open()){
			ExportPackage exportPackage = dialog.getExportPackage();
			selected.setName(exportPackage.getName());
			selected.setVersion(exportPackage.getVersion());
			tableViewer.update(selected, null);
			fireDependenciesChangedListener();
		}
	}

	private void editRequiredItem() {
		OsgiDependencies<?> selected = (OsgiDependencies<?>)((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
		NewOrEditDependencyDialog dialog = new NewOrEditDependencyDialog((List<?>) tableViewer.getInput(),
				selected, getShell(), type);
		int open = dialog.open();
		if (open == Dialog.OK) {
			OsgiDependencies<?> item = dialog.getDependencyItem();
			selected.setName(item.getName());
			selected.setMaxVersion(item.getMaxVersion());
			selected.setMinVersion(item.getMinVersion());
			selected.setOptional(item.isOptional());

			tableViewer.update(selected, null);
			fireDependenciesChangedListener();
		}
	}

	private void moveDown() {
		IDependencyItem selected = (IDependencyItem) ((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
		
		List input = (List) tableViewer.getInput();
		int index = input.indexOf(selected);
		int size = input.size();
		int targetIndex = index + 1;
		if(selected.isBuiltIn()){
			if(size<= targetIndex || !((IDependencyItem)input.get(targetIndex)).isBuiltIn()){
				targetIndex = 0;
			}
		}else{
			if(targetIndex >= size){
				for(int i = 0; i<size; i++){
					if(((IDependencyItem)input.get(i)).isBuiltIn()){
						continue;
					}
					targetIndex = i;
					break;
				}
			}
		}
		input.remove(selected);
		input.add(targetIndex, selected);
		tableViewer.refresh();
		tableViewer.setSelection(new StructuredSelection(selected));
		tableViewer.getTable().showSelection();
		fireDependenciesChangedListener();
	}

	private void moveUp() {
		IDependencyItem selected = (IDependencyItem) ((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
		
		List input = (List) tableViewer.getInput();
		int index = input.indexOf(selected);
		int size = input.size();
		int targetIndex = index - 1;
		if(selected.isBuiltIn()){
			if(targetIndex<0){
				for(int i = 0; i<size; i++){
					if(((IDependencyItem)input.get(i)).isBuiltIn()){
						continue;
					}
					targetIndex = i-1;
					break;
				}
				if(targetIndex<0){
					targetIndex = size -1;
				}
			}
		}else{
			if(targetIndex < 0 || ((IDependencyItem)input.get(targetIndex)).isBuiltIn()){
				targetIndex = size -1;
			}
		}
		input.remove(selected);
		input.add(targetIndex, selected);
		tableViewer.refresh();
		tableViewer.setSelection(new StructuredSelection(selected));
		tableViewer.getTable().showSelection();
		fireDependenciesChangedListener();
	}

	private void removeItems() {
		boolean yes = MessageDialog.openConfirm(getShell(), Messages.RouterDependenciesPanel_deleteTitle, Messages.RouterDependenciesPanel_deleteMsg);
		if(!yes){
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		List<?> input = (List<?>) tableViewer.getInput();
		Iterator<?> iterator = selection.iterator();
		while(iterator.hasNext()){
			input.remove(iterator.next());
		}
		tableViewer.refresh();
		fireDependenciesChangedListener();
	}

	private void addNewItem() {
		switch (type) {
		case IDependencyItem.IMPORT_PACKAGE:
		case IDependencyItem.REQUIRE_BUNDLE:
			addNewRequiredItem();
			break;
		case IDependencyItem.EXPORT_PACKAGE:
			addNewExportPackage();
			break;
		default:
			break;
		}
	}

	private void addNewExportPackage() {
		List input = (List) tableViewer.getInput();
		NewExportPackageDialog dialog = new NewExportPackageDialog(getShell(), input);
		if(Dialog.OK == dialog.open()){
			ExportPackage exportPackage = dialog.getExportPackage();
			input.add(exportPackage);
			tableViewer.refresh();
			tableViewer.setSelection(new StructuredSelection(exportPackage));
			tableViewer.getTable().showSelection();
			tableViewer.getTable().setFocus();
			fireDependenciesChangedListener();
		}
	}

	private void addNewRequiredItem() {
		NewOrEditDependencyDialog dialog = new NewOrEditDependencyDialog(
				(List<?>) tableViewer.getInput(), getShell(), type);
		int open = dialog.open();
		if (open != Dialog.OK) {
			return;
		}
		OsgiDependencies addedItem = dialog.getDependencyItem();
		List input = (List) tableViewer.getInput();
		input.add(addedItem);
		tableViewer.refresh();
		tableViewer.setSelection(new StructuredSelection(addedItem));
		tableViewer.getTable().showSelection();
		tableViewer.getTable().setFocus();
		fireDependenciesChangedListener();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		if(selection == null || selection.isEmpty()){
			if(remBtn !=null)
				remBtn.setEnabled(false);
			if(upBtn!=null)
				upBtn.setEnabled(false);
			if(downBtn!=null)
				downBtn.setEnabled(false);
			if(editBtn!=null)
				editBtn.setEnabled(false);
			return;
		}
		
		int nonBuiltInCount = 0;
		List<?> input = (List<?>) tableViewer.getInput();
		Iterator<?> iterator = input.iterator();
		while(iterator.hasNext()){
			Object next = iterator.next();
			if(next!=null && next instanceof IDependencyItem){
				if(!((IDependencyItem)next).isBuiltIn()){
					nonBuiltInCount ++;
				}
			}
		}
		
		boolean hasBuiltIn = false;
		iterator = selection.iterator();
		while(iterator.hasNext()){
			Object next = iterator.next();
			if(next!=null && next instanceof IDependencyItem){
				if(((IDependencyItem)next).isBuiltIn()){
					hasBuiltIn = true;
					break;
				}
			}
		}

		if(remBtn!=null){
			if (hasBuiltIn) {
				remBtn.setEnabled(false);
			} else {
				remBtn.setEnabled(true);
			}
		}
		
		if(editBtn!=null){
			if (hasBuiltIn || selection.size() > 1) {
				editBtn.setEnabled(false);
			} else {
				editBtn.setEnabled(true);
			}
		}
		
		if(upBtn!=null && downBtn !=null){
			if(hasBuiltIn || selection.size()>1 || nonBuiltInCount < 2){
				upBtn.setEnabled(false);
				downBtn.setEnabled(false);
			}else {
				upBtn.setEnabled(true);
				downBtn.setEnabled(true);
			}
		}
	}
}
