package org.talend.camel.designer.ui.editor.dependencies;

import java.util.Collection;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;

/**
 * uneditable if readonly
 */
public class CheckedCamelDependenciesPanel extends CamelDependenciesPanel {

    private static final ICheckStateProvider checkStateProvider = new ICheckStateProvider() {
        @Override
        public boolean isChecked(Object element) {
            if (element instanceof BundleClasspath) {
                return ((BundleClasspath) element).isChecked();
            }
            return false;
        }
        @Override
        public boolean isGrayed(Object element) {
            return false;
        }
    };

    private Button selectAll;
	private Button deselectAll;

	public CheckedCamelDependenciesPanel(Composite parent, int type, FormToolkit toolkit, boolean isReadOnly) {
		super(parent, type, toolkit, isReadOnly);

		selectAll.setEnabled(!isReadOnly);
        deselectAll.setEnabled(!isReadOnly);
	}


	@Override
	public void widgetSelected(SelectionEvent e) {
	    super.widgetSelected(e);
		if (e.getSource() == selectAll) {
            selectAll(true);
		} else if (e.getSource() == deselectAll) {
			selectAll(false);
		}
	}

    private void selectAll(boolean state) {
        boolean hasChanged = false;
        Collection<BundleClasspath> input = (Collection<BundleClasspath>) getTableViewer().getInput();
        for (BundleClasspath bcp : input) {
            if (bcp.isChecked() == state) {
                continue;
            }
            hasChanged = true;
            bcp.setChecked(state);
        }
        if (hasChanged) {
            ((CheckboxTableViewer) getTableViewer()).setAllChecked(state);
            fireDependenciesChangedListener();
        }
    }

	@Override
	protected TableViewer createTableViewer() {
	    final CheckboxTableViewer viewer =
	        CheckboxTableViewer.newCheckList(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	    viewer.setCheckStateProvider(checkStateProvider);
	    viewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                ((BundleClasspath) event.getElement()).setChecked(event.getChecked());
                fireDependenciesChangedListener();
            }
        });
	    return viewer;
	}
	
	@Override
	protected Composite createButtonComposite(FormToolkit toolkit) {
	    Composite bc = super.createButtonComposite(toolkit);
        selectAll = toolkit.createButton(bc, Messages.RouterDependenciesPanel_selectAll, SWT.NONE);
        selectAll.addSelectionListener(this);

        deselectAll = toolkit.createButton(bc, Messages.RouterDependenciesPanel_deselectAll, SWT.NONE);
        deselectAll.addSelectionListener(this);
        
        return bc;
	}
}
