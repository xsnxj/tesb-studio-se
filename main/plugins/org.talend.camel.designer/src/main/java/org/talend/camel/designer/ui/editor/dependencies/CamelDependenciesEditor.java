package org.talend.camel.designer.ui.editor.dependencies;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.talend.camel.designer.CamelDesignerPlugin;
import org.talend.camel.designer.ui.editor.CamelProcessEditorInput;
import org.talend.camel.designer.ui.editor.dependencies.controls.SearchControl;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.core.model.process.IProcess2;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.ui.editor.JobEditorInput;
import org.talend.designer.camel.dependencies.core.DependenciesResolver;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ExportPackage;
import org.talend.designer.camel.dependencies.core.model.IDependencyItem;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;
import org.talend.designer.camel.dependencies.core.util.DependenciesCoreUtil;
import org.talend.designer.core.ui.AbstractMultiPageTalendEditor;

public class CamelDependenciesEditor extends EditorPart implements IRouterDependenciesChangedListener {

    private final CommandStack commandStack;
    private final boolean isReadOnly;

    private Label statusLabel;

	//show all datas
	private FilterTableViewerAdapter requireBundleViewer;
	private FilterTableViewerAdapter bundleClasspathViewer;
	private FilterTableViewerAdapter importPackageViewer;
	private FilterTableViewerAdapter exportPackageViewer;
	
	private final ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            int size = selection.size();
            if (size == 0) {
                setStatus(""); //$NON-NLS-1$
            } else if (selection.size() == 1) {
                setStatus(((IDependencyItem) selection.getFirstElement()).getDescription());
            } else {
                setStatus(size + Messages.RouterDependenciesEditor_multiItemsSelectedStatusMsg);
            }
        }
    };

	public CamelDependenciesEditor(final AbstractMultiPageTalendEditor editor, boolean isReadOnly) {
	    commandStack = editor.getTalendEditor().getCommandStack();
	    this.isReadOnly = isReadOnly;
    }

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setInput(input);
		setSite(site);
	}

    @Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		parent.setLayout(new GridLayout());

        //create search group, hide button and refresh button
        Composite toolsPanel = toolkit.createComposite(parent);
        toolsPanel.setLayout(new GridLayout(4, false));
        toolsPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolsPanel.setBackgroundMode(SWT.INHERIT_FORCE);

        toolkit.createLabel(toolsPanel, Messages.RouterDependenciesEditor_filterLabel);

        SearchControl searchComposite = new SearchControl(toolsPanel, SWT.NONE);
        searchComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        searchComposite.setActiveImage(CamelDesignerPlugin
                .getImage(CamelDesignerPlugin.HIGHLIGHT_REM_ICON));
        searchComposite.setDeactiveImage(CamelDesignerPlugin
                .getImage(CamelDesignerPlugin.GRAY_REM_ICON));
        Text filterText = searchComposite.getText();

        Button hideBuiltIn = toolkit.createButton(toolsPanel,
                Messages.RouterDependenciesEditor_hideBuiltInItems, SWT.CHECK);

        Button refreshBtn = toolkit.createButton(toolsPanel, null, SWT.PUSH);
        refreshBtn.setImage(CamelDesignerPlugin.getImage(CamelDesignerPlugin.REFRESH_ICON));
        refreshBtn
                .setToolTipText(Messages.RouterDependenciesEditor_refreshDependenciesTooltip);
        refreshBtn.setEnabled(!isReadOnly());


		// create data tables
        ScrolledComposite top = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        top.setLayoutData(new GridData(GridData.FILL_BOTH));
		SashForm sashForm = new SashForm(top, SWT.NONE);

		SashForm leftPart = new SashForm(sashForm, SWT.VERTICAL);
		leftPart.setLayout(new FillLayout(SWT.VERTICAL));
		importPackageViewer = createTableViewer(leftPart, toolkit,
				Messages.RouterDependenciesEditor_importPackageSec,
				IDependencyItem.IMPORT_PACKAGE, false);
		exportPackageViewer = createTableViewer(leftPart, toolkit,
				Messages.RouterDependenciesEditor_exportPackage, IDependencyItem.EXPORT_PACKAGE, false);

		SashForm rightPart = new SashForm(sashForm, SWT.VERTICAL);
		rightPart.setLayout(new FillLayout(SWT.VERTICAL));
		bundleClasspathViewer = createTableViewer(rightPart, toolkit,
				Messages.RouterDependenciesEditor_classpathSec,
				IDependencyItem.CLASS_PATH, true);
		requireBundleViewer = createTableViewer(rightPart, toolkit,
				Messages.RouterDependenciesEditor_requireBundleSec,
				IDependencyItem.REQUIRE_BUNDLE, false);

        top.setContent(sashForm);

		toolkit.adapt(rightPart);
		toolkit.adapt(leftPart);
		toolkit.adapt(sashForm);

		top.setExpandHorizontal(true);
		top.setExpandVertical(true);

		Point minSize = sashForm.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		top.setMinSize(minSize.x, minSize.y);
		
		//create status
		Composite statusComposite = toolkit.createComposite(parent);
		statusComposite.setLayout(new GridLayout(2, false));
		statusComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		statusLabel = toolkit.createLabel(statusComposite, null);
		statusLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (isReadOnly()) {
			statusLabel.setText(Messages.RouterDependenciesEditor_itemIsLockedByOther);
		}

		Label shortCuts = toolkit.createLabel(statusComposite, Messages.RouterDependenciesEditor_KeyBindingw, SWT.SHADOW_OUT);
		shortCuts.setEnabled(false);

		// add filter listener
		filterText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text t = (Text) e.widget;
				final String filterString = t.getText().trim();
				importPackageViewer.setFilterString(filterString);
				requireBundleViewer.setFilterString(filterString);
				bundleClasspathViewer.setFilterString(filterString);
				exportPackageViewer.setFilterString(filterString);
			}
		});

		// add hide listener
		hideBuiltIn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.widget;
				final boolean show = b.getSelection();
				importPackageViewer.setShowBuiltIn(!show);
				requireBundleViewer.setShowBuiltIn(!show);
				bundleClasspathViewer.setShowBuiltIn(!show);
				exportPackageViewer.setShowBuiltIn(!show);
			}
		});

		refreshBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshDependencies();
			}
		});
		
		// update all tables input
		updateInput();
	}
	
	public void setStatus(String message){
		if (isReadOnly()) {
			return;
		}
		if(message == null){
			message = ""; //$NON-NLS-1$
		}
		statusLabel.setText(message);
		statusLabel.setToolTipText(message);
	}

	protected void refreshDependencies() {
		try {
			updateInput();
//			setDirty(false);
		} catch (Exception e) {
            ExceptionHandler.process(e);
		}
	}

    private ProcessItem fromEditorInput() {
        return (ProcessItem) ((CamelProcessEditorInput) getEditorInput()).getItem();
    }

	private void updateInput() {
		// re-calculate all datas
        final DependenciesResolver resolver = new DependenciesResolver((ProcessItem) fromEditorInput());
		// set datas
		importPackageViewer.getTableViewer().setInput(resolver.getImportPackages());
		requireBundleViewer.getTableViewer().setInput(resolver.getRequireBundles());
		bundleClasspathViewer.getTableViewer().setInput(resolver.getBundleClasspaths());
		exportPackageViewer.getTableViewer().setInput(resolver.getExportPackages());

		// show datas
		importPackageViewer.getTableViewer().refresh();
		requireBundleViewer.getTableViewer().refresh();
		bundleClasspathViewer.getTableViewer().refresh();
		exportPackageViewer.getTableViewer().refresh();
	}

	private FilterTableViewerAdapter createTableViewer(Composite parent, FormToolkit toolkit, String title, int type, boolean isCheck) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR );
		section.setText(title);
		section.setLayout(new FillLayout());
		CamelDependenciesPanel c = isCheck
		    ? new CheckedCamelDependenciesPanel(section, type, toolkit, isReadOnly())
		    : new CamelDependenciesPanel(section, type, toolkit, isReadOnly());
		section.setClient(c);
		toolkit.adapt(c);
		c.addDependenciesChangedListener(this);
		c.getTableViewer().addSelectionChangedListener(selectionChangedListener);
		return c.getFilterTableViewerAdapter();
	}

    @Override
	public void dependencesChanged() {
//		setDirty(true);
		doSave(new NullProgressMonitor());
	}

    @Override
	public void doSave(IProgressMonitor monitor) {
		try {
            //record process editor dirty signal first
		    commandStack.execute(new Command() {
                @Override
                public void execute() {
                    final IProcess2 process = ((JobEditorInput) getEditorInput()).getLoadedProcess();
                    saveToMap(process.getAdditionalProperties());
                    // update ProcessItem for unsaved editor
//                        saveToMap(process.getProperty().getAdditionalProperties().map());
                }
            });
//            } else {
                //check editor and update the input source before saving
//                saveToMap(((CamelProcessEditorInput) getEditorInput()).getLoadedProcess().getAdditionalProperties());
                // save model
//                ProxyRepositoryFactory.getInstance().save(processItem);

//            firePropertyChange(PROP_DIRTY);
		} catch (Exception e) {
		    ExceptionHandler.process(e);
		}
	}

    private void saveToMap(Map additionalProperties) {
        //save all datas
        DependenciesCoreUtil.saveToMap(additionalProperties,
                (Collection<BundleClasspath>) bundleClasspathViewer.getTableViewer().getInput(),
                (Collection<ImportPackage>) importPackageViewer.getTableViewer().getInput(),
                (Collection<RequireBundle>) requireBundleViewer.getTableViewer().getInput(),
                (Collection<ExportPackage>)exportPackageViewer.getTableViewer().getInput());
    }

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void setFocus() {
		importPackageViewer.getTableViewer().getTable().setFocus();
	}

    private boolean isReadOnly() {
        return isReadOnly;
    }

}
