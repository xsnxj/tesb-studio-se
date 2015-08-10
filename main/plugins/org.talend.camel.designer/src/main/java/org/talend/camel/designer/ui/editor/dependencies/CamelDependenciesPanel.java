package org.talend.camel.designer.ui.editor.dependencies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.camel.designer.ui.editor.dependencies.dialog.NewOrEditDependencyDialog;
import org.talend.designer.camel.dependencies.core.model.IDependencyItem;
import org.talend.designer.camel.dependencies.core.model.OsgiDependencies;

/**
 * uneditable if readonly
 */
public class CamelDependenciesPanel extends Composite implements SelectionListener, ISelectionChangedListener {

    private final int type;

	private final FilterTableViewerAdapter filterTableViewerAdapter;
	protected final TableViewer tableViewer;

    private Button addBtn;
    private Button remBtn;
    private Button editBtn;
    private Button upBtn;
    private Button downBtn;

	private Collection<IRouterDependenciesChangedListener> listeners = new ArrayList<IRouterDependenciesChangedListener>();

	public CamelDependenciesPanel(Composite parent, int type, FormToolkit toolkit, boolean isReadOnly) {
		super(parent, SWT.NONE);
		this.type = type;

		setLayout(new GridLayout(2, false));
//		setLayoutData(new GridData(GridData.FILL_BOTH));

		tableViewer = createTableViewer();
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 10));
        tableViewer.addSelectionChangedListener(this);

        if (isReadOnly) {
            // table.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
            tableViewer.getTable().setEnabled(false);
        } else {
            tableViewer.getTable().addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.stateMask == SWT.NONE) {
                        if (remBtn != null && remBtn.isEnabled() && e.keyCode == SWT.DEL) {
                            removeItems();
                        } else if (e.keyCode == SWT.INSERT) {
                            addNewItem();
                        } else if (editBtn != null && editBtn.isEnabled() && e.keyCode == SWT.F2) {
                            editSelected();
                        }
                    }
                }
            });
        }

		filterTableViewerAdapter = new FilterTableViewerAdapter(tableViewer);

		createButtonComposite(toolkit);

		if (null != addBtn) {
	        addBtn.setEnabled(!isReadOnly);
		}
	}

	public FilterTableViewerAdapter getFilterTableViewerAdapter() {
		return filterTableViewerAdapter;
	}

	public TableViewer getTableViewer() {
        return tableViewer;
    }

	public void addDependenciesChangedListener(IRouterDependenciesChangedListener l) {
		listeners.add(l);
	}

	public void removeDependenciesChangedListener(IRouterDependenciesChangedListener l) {
		listeners.remove(l);
	}

	protected void fireDependenciesChangedListener() {
		for (IRouterDependenciesChangedListener l : listeners) {
			l.dependencesChanged();
		}
	}

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() == addBtn) {
			addNewItem();
		} else if (e.getSource() == remBtn) {
			removeItems();
		} else if (e.getSource() == upBtn) {
			moveUp();
		} else if (e.getSource() == downBtn) {
			moveDown();
		} else if (e.getSource() == editBtn) {
			editSelected();
		}
	}

	private void editSelected() {
		final OsgiDependencies selected =
		    (OsgiDependencies) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
		final NewOrEditDependencyDialog dialog =
		    new NewOrEditDependencyDialog((Collection<? extends IDependencyItem>) tableViewer.getInput(),
				selected, getShell(), type);
		if (dialog.open() == Dialog.OK) {
			OsgiDependencies item = dialog.getDependencyItem();
			selected.setName(item.getName());
//			selected.setMaxVersion(item.getMaxVersion());
//			selected.setMinVersion(item.getMinVersion());
			selected.setOptional(item.isOptional());
			selected.setVersion(item.getVersion());
//			selected.setIncludeMinimum(item.getIncludeMinimum());
//			selected.setIncludeMaximum(item.getIncludeMaximum());
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
	    final Collection<OsgiDependencies> input = (Collection<OsgiDependencies>) tableViewer.getInput();
		NewOrEditDependencyDialog dialog = new NewOrEditDependencyDialog(
				input, getShell(), type);
		if (dialog.open() == Dialog.OK) {
	        final OsgiDependencies addedItem = dialog.getDependencyItem();
	        input.add(addedItem);
	        tableViewer.refresh();
	        tableViewer.setSelection(new StructuredSelection(addedItem));
	        tableViewer.getTable().showSelection();
	        tableViewer.getTable().setFocus();
	        fireDependenciesChangedListener();
		}
	}

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        if (selection == null || selection.isEmpty()) {
            if (remBtn != null) {
                remBtn.setEnabled(false);
            }
            if (upBtn != null) {
                upBtn.setEnabled(false);
            }
            if (downBtn != null) {
                downBtn.setEnabled(false);
            }
            if (editBtn != null) {
                editBtn.setEnabled(false);
            }
            return;
        }

        boolean hasBuiltIn = false;
        Iterator<?> iterator = selection.iterator();
        while (iterator.hasNext()) {
            IDependencyItem next = (IDependencyItem) iterator.next();
            if (((IDependencyItem) next).isBuiltIn()) {
                hasBuiltIn = true;
                break;
            }
        }

        if (remBtn != null) {
            remBtn.setEnabled(!hasBuiltIn);
        }

        if (editBtn != null) {
            editBtn.setEnabled(!hasBuiltIn && selection.size() == 1);
        }

        if (upBtn != null && downBtn != null) {
            int nonBuiltInCount = 0;
            for (IDependencyItem item : (Collection<? extends IDependencyItem>) tableViewer.getInput()) {
                if (!item.isBuiltIn()) {
                    nonBuiltInCount++;
                }
            }
            boolean state = !hasBuiltIn && selection.size() == 1 && nonBuiltInCount > 1;
            upBtn.setEnabled(state);
            downBtn.setEnabled(state);
        }
    }

    protected TableViewer createTableViewer() {
        return new TableViewer(this);
    }

    protected Composite createButtonComposite(FormToolkit toolkit) {
        Composite bc = toolkit.createComposite(this);
        FillLayout layout = new FillLayout(SWT.VERTICAL);
        layout.spacing = new GridLayout().verticalSpacing;
        bc.setLayout(layout);

        if (type != IDependencyItem.CLASS_PATH) {
            addBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_addBtn, SWT.NONE);
            addBtn.addSelectionListener(this);

            remBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_removeBtn, SWT.NONE);
            remBtn.addSelectionListener(this);
            remBtn.setEnabled(false);

            editBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_editBtn, SWT.NONE);
            editBtn.setEnabled(false);
            editBtn.addSelectionListener(this);
        }
        if (type != IDependencyItem.EXPORT_PACKAGE && type != IDependencyItem.CLASS_PATH) {
            upBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_upBtn, SWT.NONE);
            upBtn.addSelectionListener(this);
            upBtn.setEnabled(false);
    
            downBtn = toolkit.createButton(bc, Messages.RouterDependenciesPanel_downBtn, SWT.NONE);
            downBtn.addSelectionListener(this);
            downBtn.setEnabled(false);
        }
        return bc;
    }

}
