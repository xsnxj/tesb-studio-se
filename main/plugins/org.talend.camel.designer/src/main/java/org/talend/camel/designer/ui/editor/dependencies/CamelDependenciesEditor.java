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
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.ManifestItem;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;
import org.talend.designer.camel.dependencies.core.util.DependenciesCoreUtil;
import org.talend.designer.core.ui.AbstractMultiPageTalendEditor;

public class CamelDependenciesEditor extends EditorPart implements IRouterDependenciesChangedListener {

    private final CommandStack commandStack;
    private final boolean isReadOnly;

    private Label statusLabel;

	//show all datas
	private CamelDependenciesPanel requireBundleViewer;
	private CamelDependenciesPanel bundleClasspathViewer;
	private CamelDependenciesPanel importPackageViewer;
	private CamelDependenciesPanel exportPackageViewer;

	private final ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            int size = selection.size();
            if (size == 0) {
                setStatus(null);
            } else if (selection.size() == 1) {
                setStatus(((ManifestItem) selection.getFirstElement()).getDescription());
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
				ManifestItem.IMPORT_PACKAGE);
		exportPackageViewer = createTableViewer(leftPart, toolkit,
				Messages.RouterDependenciesEditor_exportPackage, ManifestItem.EXPORT_PACKAGE);

		SashForm rightPart = new SashForm(sashForm, SWT.VERTICAL);
		rightPart.setLayout(new FillLayout(SWT.VERTICAL));
		bundleClasspathViewer = createTableViewer(rightPart, toolkit,
				Messages.RouterDependenciesEditor_classpathSec,
				ManifestItem.BUNDLE_CLASSPATH);
		requireBundleViewer = createTableViewer(rightPart, toolkit,
				Messages.RouterDependenciesEditor_requireBundleSec,
				ManifestItem.REQUIRE_BUNDLE);

        top.setContent(sashForm);
		top.setExpandHorizontal(true);
		top.setExpandVertical(true);
		top.setMinSize(sashForm.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		//create status
		Composite statusComposite = toolkit.createComposite(parent);
		statusComposite.setLayout(new GridLayout(2, false));
		statusComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		statusLabel = toolkit.createLabel(statusComposite, null);
		statusLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		if (isReadOnly()) {
//			statusLabel.setText(Messages.RouterDependenciesEditor_itemIsLockedByOther);
//		}

		Label shortCuts = toolkit.createLabel(statusComposite, Messages.RouterDependenciesEditor_KeyBindingw, SWT.SHADOW_OUT);
		shortCuts.setEnabled(false);

		// add filter listener
		filterText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				final String filterString = ((Text) e.widget).getText().trim();
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
				final boolean show = ((Button) e.widget).getSelection();
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
//		if (isReadOnly()) {
//			return;
//		}
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
        importPackageViewer.setInput(resolver.getImportPackages());
        requireBundleViewer.setInput(resolver.getRequireBundles());
        bundleClasspathViewer.setInput(resolver.getBundleClasspaths());
        exportPackageViewer.setInput(resolver.getExportPackages());
    }

	private CamelDependenciesPanel createTableViewer(Composite parent, FormToolkit toolkit, String title, String type) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(title);
		final CamelDependenciesPanel c = (type == ManifestItem.BUNDLE_CLASSPATH)
		    ? new CheckedCamelDependenciesPanel(section, type, toolkit, isReadOnly())
		    : new CamelDependenciesPanel(section, type, toolkit, isReadOnly());
		section.setClient(c);
		toolkit.adapt(c);
		c.addDependenciesChangedListener(this);
		c.addSelectionChangedListener(selectionChangedListener);
		return c;
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

    private void saveToMap(Map<Object, Object> additionalProperties) {
        //save all datas
        DependenciesCoreUtil.saveToMap(additionalProperties,
                (Collection<BundleClasspath>) bundleClasspathViewer.getInput(),
                (Collection<ImportPackage>) importPackageViewer.getInput(),
                (Collection<RequireBundle>) requireBundleViewer.getInput(),
                (Collection<ExportPackage>)exportPackageViewer.getInput());
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
//		importPackageViewer.getTableViewer().getTable().setFocus();
	}

    private boolean isReadOnly() {
        return isReadOnly;
    }

}
