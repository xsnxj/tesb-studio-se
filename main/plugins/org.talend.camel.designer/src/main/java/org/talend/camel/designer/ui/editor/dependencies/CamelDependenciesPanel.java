package org.talend.camel.designer.ui.editor.dependencies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.camel.designer.CamelDesignerPlugin;
import org.talend.camel.designer.ui.editor.dependencies.dialog.NewOrEditDependencyDialog;
import org.talend.designer.camel.dependencies.core.model.ManifestItem;

/**
 * uneditable if readonly
 */
public class CamelDependenciesPanel extends Composite {

    // hightlight of filterString
    private static Color hightLight = null;
    // builtIn font
    private static Font builtInFont = null;

    protected final TableViewer tableViewer;

    private String filterString;
    private boolean showBuiltIn = true;

    private final String type;

    private ToolItem addBtn;
    private ToolItem remBtn;
    private ToolItem editBtn;
    private ToolItem upBtn;
    private ToolItem downBtn;

    private final Collection<IRouterDependenciesChangedListener> listeners =
        new ArrayList<IRouterDependenciesChangedListener>();

    public CamelDependenciesPanel(Composite parent, String type, FormToolkit toolkit, boolean isReadOnly) {
		super(parent, SWT.NONE);
		this.type = type;

		setLayout(new GridLayout(2, false));

		tableViewer = createTableViewer();
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

        if (isReadOnly) {
            // table.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
            tableViewer.getTable().setEnabled(false);
        } else {
            tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
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
                        if (((ManifestItem) iterator.next()).isBuiltIn()) {
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
                        for (ManifestItem item : getInput()) {
                            if (!item.isBuiltIn()) {
                                nonBuiltInCount++;
                            }
                        }
                        boolean state = !hasBuiltIn && selection.size() == 1 && nonBuiltInCount > 1;
                        upBtn.setEnabled(state);
                        downBtn.setEnabled(state);
                    }
                }
            });
            tableViewer.getTable().addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.stateMask == SWT.NONE) {
                        if (remBtn != null && remBtn.isEnabled() && e.keyCode == SWT.DEL) {
                            removeItems();
                        } else if (e.keyCode == SWT.INSERT) {
                            addNewItem();
                        } else if (editBtn != null && editBtn.isEnabled() && e.keyCode == SWT.CR) {
                            editSelected();
                        }
                    }
                }
            });
        }

        Display display = tableViewer.getTable().getDisplay();
        if (hightLight == null) {
            hightLight = display.getSystemColor(SWT.COLOR_YELLOW);
        }
        if (builtInFont == null) {
            FontData[] fontData = tableViewer.getTable().getFont().getFontData();
            fontData[0].setStyle(fontData[0].getStyle() | SWT.ITALIC);
            builtInFont = new Font(display, fontData[0]);
        }

        tableViewer.setLabelProvider(new DependenciesTableLabelProvider());
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.addFilter(new DependencesTableFilter());

        ToolBar tb = new ToolBar(this, SWT.FLAT | SWT.VERTICAL);
        tb.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		createButtons(tb);

		if (null != addBtn) {
	        addBtn.setEnabled(!isReadOnly);
		}
	}

    public void setFilterString(String filterString) {
        this.filterString = filterString;
        tableViewer.refresh();
        tableViewer.getTable().redraw();
    }

    public void setShowBuiltIn(boolean showBuiltIn) {
        this.showBuiltIn = showBuiltIn;
        tableViewer.refresh();
    }

    public void setInput(Collection<? extends ManifestItem> manifestItems) {
        tableViewer.setInput(manifestItems);
        tableViewer.refresh();
    }

    public Collection<? extends ManifestItem> getInput() {
        return (Collection<? extends ManifestItem>) tableViewer.getInput();
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

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
	    tableViewer.addSelectionChangedListener(listener);
	}

	private void editSelected() {
		final ManifestItem selected =
		    (ManifestItem) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
		final NewOrEditDependencyDialog dialog =
		    new NewOrEditDependencyDialog(getInput(), selected, getShell(), type);
		if (dialog.open() == Dialog.OK) {
			ManifestItem item = dialog.getManifestItem();
			selected.setName(item.getName());
			selected.setOptional(item.isOptional());
			selected.setVersion(item.getVersion());
			tableViewer.update(selected, null);
			fireDependenciesChangedListener();
		}
	}

	private void moveDown() {
		ManifestItem selected = (ManifestItem) ((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
		
		List input = (List) tableViewer.getInput();
		int index = input.indexOf(selected);
		int size = input.size();
		int targetIndex = index + 1;
		if(selected.isBuiltIn()){
			if(size<= targetIndex || !((ManifestItem)input.get(targetIndex)).isBuiltIn()){
				targetIndex = 0;
			}
		}else{
			if(targetIndex >= size){
				for(int i = 0; i<size; i++){
					if(((ManifestItem)input.get(i)).isBuiltIn()){
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
	    ManifestItem selected = (ManifestItem) ((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
		
		List input = (List) tableViewer.getInput();
		int index = input.indexOf(selected);
		int size = input.size();
		int targetIndex = index - 1;
		if(selected.isBuiltIn()){
			if(targetIndex<0){
				for(int i = 0; i<size; i++){
					if(((ManifestItem)input.get(i)).isBuiltIn()){
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
			if(targetIndex < 0 || ((ManifestItem)input.get(targetIndex)).isBuiltIn()){
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
		Collection<? extends ManifestItem> input = getInput();
		Iterator<?> iterator = selection.iterator();
		while(iterator.hasNext()){
			input.remove(iterator.next());
		}
		tableViewer.refresh();
		fireDependenciesChangedListener();
	}

	private void addNewItem() {
	    final Collection<ManifestItem> input = (Collection<ManifestItem>) getInput();
		NewOrEditDependencyDialog dialog = new NewOrEditDependencyDialog(input, getShell(), type);
		if (dialog.open() == Dialog.OK) {
	        final ManifestItem addedItem = dialog.getManifestItem();
	        input.add(addedItem);
	        tableViewer.refresh();
	        tableViewer.setSelection(new StructuredSelection(addedItem));
	        tableViewer.getTable().showSelection();
	        tableViewer.getTable().setFocus();
	        fireDependenciesChangedListener();
		}
	}

    protected TableViewer createTableViewer() {
        return new TableViewer(this);
    }

    protected void createButtons(ToolBar tb) {
        final SelectionListener selectionListener = new SelectionAdapter() {
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
        };

        addBtn = new ToolItem(tb, SWT.PUSH);
        addBtn.setText(Messages.RouterDependenciesPanel_addBtn);
        addBtn.addSelectionListener(selectionListener);

        remBtn = new ToolItem(tb, SWT.PUSH);
        remBtn.setText(Messages.RouterDependenciesPanel_removeBtn);
        remBtn.addSelectionListener(selectionListener);
        remBtn.setEnabled(false);

        editBtn = new ToolItem(tb, SWT.PUSH);
        editBtn.setText(Messages.RouterDependenciesPanel_editBtn);
        editBtn.setEnabled(false);
        editBtn.addSelectionListener(selectionListener);

        if (type != ManifestItem.EXPORT_PACKAGE) {
            upBtn = new ToolItem(tb, SWT.PUSH);
            upBtn.setText(Messages.RouterDependenciesPanel_upBtn);
            upBtn.addSelectionListener(selectionListener);
            upBtn.setEnabled(false);
    
            downBtn = new ToolItem(tb, SWT.PUSH);
            downBtn.setText(Messages.RouterDependenciesPanel_downBtn);
            downBtn.addSelectionListener(selectionListener);
            downBtn.setEnabled(false);
        }
    }

    /**
     * filter according to the filterString and show BuiltIn items or not
     * @author liugang
     *
     */
    private class DependencesTableFilter extends ViewerFilter {

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (!showBuiltIn) {
                if (((ManifestItem) element).isBuiltIn()) {
                    return false;
                }
            }
            if (filterString == null || filterString.isEmpty()) {
                return true;
            }
            return ((ManifestItem) element).toString().contains(filterString);
        }

    }

    private class DependenciesTableLabelProvider extends StyledCellLabelProvider {

        public Image getImage(Object element) {
            switch (((ManifestItem) element).getHeader()) {
            case ManifestItem.IMPORT_PACKAGE:
                return CamelDesignerPlugin.getImage(((ManifestItem) element).isOptional()
                    ? CamelDesignerPlugin.IMPORT_PACKAGE_OVERLAY_ICON
                    : CamelDesignerPlugin.IMPORT_PKG_ICON);
            case ManifestItem.REQUIRE_BUNDLE:
                return CamelDesignerPlugin.getImage(((ManifestItem) element).isOptional()
                    ? CamelDesignerPlugin.REQUIRE_BUNDLE_OVERLAY_ICON
                    : CamelDesignerPlugin.REQUIRE_BD_ICON);
            case ManifestItem.BUNDLE_CLASSPATH:
                return CamelDesignerPlugin.getImage(CamelDesignerPlugin.BUNDLE_CP_ICON);
            case ManifestItem.EXPORT_PACKAGE:
                return CamelDesignerPlugin.getImage(CamelDesignerPlugin.IMPORT_PKG_ICON);
            default:
                return null;
            }
        }

//        @Override
        public Font getFont(Object element) {
            if (((ManifestItem) element).isBuiltIn()) {
                return builtInFont;
            }
            return null;
        }

        @Override
        public void update(ViewerCell cell) {
            ManifestItem item = (ManifestItem) cell.getElement();
            final String text = item.toString();
            cell.setText(text);
            cell.setImage(getImage(item));
            cell.setFont(getFont(item));
            if (filterString != null && !filterString.isEmpty()) {
                int filterIndex = text.indexOf(filterString);
                StyleRange styleRange = new StyleRange(filterIndex, filterString.length(), null, hightLight);
                cell.setStyleRanges(new StyleRange[] { styleRange });
            } else {
                cell.setStyleRanges(null);
            }
        }
    }

}
