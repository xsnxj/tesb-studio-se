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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.designer.camel.dependencies.core.model.IDependencyItem;
import org.talend.designer.camel.dependencies.core.model.OsgiDependencies;
import org.talend.designer.camel.dependencies.ui.Messages;
import org.talend.designer.camel.dependencies.ui.dialog.NewOrEditDependencyDialog;

public class RouterDependenciesPanel extends Composite implements
		SelectionListener, ISelectionChangedListener {

	private int type;
	private RouterDependenciesTableViewer tableViewer;
	private Button addBtn;
	private Button remBtn;
	private Button upBtn;
	private Button downBtn;

	private List<IRouterDependenciesChangedListener> listeners = new ArrayList<IRouterDependenciesChangedListener>();
	private Button editBtn;

	public RouterDependenciesPanel(Composite parent, int style, int type,
			FormToolkit toolkit) {
		super(parent, style);
		this.type = type;
		initialize(toolkit);
	}

	private void initialize(FormToolkit toolkit) {
		setLayout(new GridLayout(2, false));
		setLayoutData(new GridData(GridData.FILL_BOTH));

		Table table = toolkit.createTable(this, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL | SWT.H_SCROLL);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		tableViewer = new RouterDependenciesTableViewer(table);
		tableViewer.addSelectionChangedListener(this);

		Composite bc = toolkit.createComposite(this);
		GridLayout layout = new GridLayout(1, false);
		layout.marginLeft = 0;
		layout.marginRight = 0;
		bc.setLayout(layout);

		addBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_addBtn, SWT.NONE);
		addBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addBtn.addSelectionListener(this);

		remBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_removeBtn, SWT.NONE);
		remBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		remBtn.addSelectionListener(this);
		remBtn.setEnabled(false);
		
		editBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_editBtn, SWT.NONE);
		editBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editBtn.setEnabled(false);
		editBtn.addSelectionListener(this);
		
		if (type == IDependencyItem.CLASS_PATH) {
			addBtn.setVisible(false);
			remBtn.setVisible(false);
			editBtn.setVisible(false);
		}
		
		upBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_upBtn, SWT.NONE);
		upBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		upBtn.addSelectionListener(this);
		upBtn.setEnabled(false);

		downBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_downBtn, SWT.NONE);
		downBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		downBtn.addSelectionListener(this);
		downBtn.setEnabled(false);
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
		if (e.getSource() == addBtn) {
			NewOrEditDependencyDialog dialog = new NewOrEditDependencyDialog(
					(List<?>) tableViewer.getInput(), getShell(), type);
			int open = dialog.open();
			if (open == Dialog.OK) {
				addNewItem(dialog);
			}
		}else if(e.getSource() == remBtn){
			removeItems();
		}else if(e.getSource() == upBtn){
			moveUp();
		}else if(e.getSource() == downBtn){
			moveDown();
		}else if(e.getSource() == editBtn){
			editSelected();
		}
	}

	private void editSelected() {
		OsgiDependencies<?> selected = (OsgiDependencies<?>)((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
		NewOrEditDependencyDialog dialog = new NewOrEditDependencyDialog(
				selected, getShell(), type);
		int open = dialog.open();
		if (open == Dialog.OK) {
			if(dialog.isChanged()){
				OsgiDependencies<?> item = dialog.getDependencyItem();
				selected.setName(item.getName());
				selected.setMaxVersion(item.getMaxVersion());
				selected.setMinVersion(item.getMinVersion());
				selected.setOptional(item.isOptional());
				
				tableViewer.update(selected, null);
				fireDependenciesChangedListener();
			}
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

	private void addNewItem(NewOrEditDependencyDialog dialog) {
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
			remBtn.setEnabled(false);
			upBtn.setEnabled(false);
			downBtn.setEnabled(false);
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

		if (hasBuiltIn) {
			remBtn.setEnabled(false);
		} else {
			remBtn.setEnabled(true);
		}
		
		if (hasBuiltIn || selection.size() > 1) {
			editBtn.setEnabled(false);
		} else {
			editBtn.setEnabled(true);
		}
		
		if(hasBuiltIn || selection.size()>1 || nonBuiltInCount < 2){
			upBtn.setEnabled(false);
			downBtn.setEnabled(false);
		}else {
			upBtn.setEnabled(true);
			downBtn.setEnabled(true);
		}
	}
}
