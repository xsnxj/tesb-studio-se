package org.talend.designer.camel.dependencies.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.UIJob;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.designer.ui.editor.CamelProcessEditorInput;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.ExportPackage;
import org.talend.designer.camel.dependencies.core.model.IDependencyItem;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;
import org.talend.designer.camel.dependencies.core.util.DependenciesCoreUtil;
import org.talend.designer.camel.dependencies.core.util.RouterOsgiDependenciesResolver;
import org.talend.designer.camel.dependencies.ui.Messages;
import org.talend.designer.camel.dependencies.ui.UIActivator;
import org.talend.designer.camel.dependencies.ui.dialog.RelativeEditorsSaveDialog;
import org.talend.designer.camel.dependencies.ui.editor.controls.SearchControl;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;

public class RouterDependenciesEditor extends EditorPart implements
		IRouterDependenciesChangedListener {

	public static final String ID = "org.talend.designer.camel.dependencies.ui.dependenciesEditor"; //$NON-NLS-1$

	//used to store all data
	private List<ImportPackage> importPackages = new ArrayList<ImportPackage>();
	private List<RequireBundle> requireBundles = new ArrayList<RequireBundle>();
	private List<BundleClasspath> bundleClasspaths = new ArrayList<BundleClasspath>();
	private List<ExportPackage> exportPackages = new ArrayList<ExportPackage>();
	
	//show all datas
	private RouterDependenciesTableViewer requireBundleViewer;
	private RouterDependenciesTableViewer bundleClasspathViewer;
	private RouterDependenciesTableViewer importPackageViewer;
	private RouterDependenciesTableViewer exportPackageViewer;

	// source
	private Property property;
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setInput(input);
		setSite(site);
		setPartName(input.getName());
		RepositoryNode repositoryNode = (RepositoryNode) input
				.getAdapter(RepositoryNode.class);
		property = repositoryNode.getObject().getProperty();
	}

	@Override
	public void createPartControl(Composite parent) {
		int columnCount = 1;
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		parent.setLayout(new GridLayout(1, false));
		
		Composite tmp = toolkit.createComposite(parent);
		tmp.setLayout(new FillLayout());
		tmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ScrolledComposite top = new ScrolledComposite(tmp, SWT.H_SCROLL | SWT.V_SCROLL);
		
		Composite composite = toolkit.createComposite(top);
		composite.setLayout(new GridLayout(columnCount, false));
		top.setContent(composite);

		//create search group, hide button and refresh button
		Composite toolsPanel = toolkit.createComposite(composite);
		toolsPanel.setLayout(new GridLayout(4, false));
		toolsPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolsPanel.setBackground(toolkit.getColors()
				.getColor(IFormColors.TB_BG));
		toolsPanel.setBackgroundMode(SWT.INHERIT_FORCE);

		Label filterLabel = toolkit.createLabel(toolsPanel,
				Messages.RouterDependenciesEditor_filterLabel);
		filterLabel.setBackground(toolsPanel.getBackground());

		SearchControl searchComposite = new SearchControl(toolsPanel, SWT.NONE);
		searchComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchComposite.setActiveImage(UIActivator
				.getImage(UIActivator.HIGHLIGHT_REM_ICON));
		searchComposite.setDeactiveImage(UIActivator
				.getImage(UIActivator.GRAY_REM_ICON));
		Text filterText = searchComposite.getText();

		Button hideBuiltIn = toolkit.createButton(toolsPanel,
				Messages.RouterDependenciesEditor_hideBuiltInItems, SWT.CHECK);
		hideBuiltIn.setBackground(toolsPanel.getBackground());

		Button refreshBtn = toolkit.createButton(toolsPanel, "", SWT.PUSH); //$NON-NLS-1$
		refreshBtn.setImage(UIActivator.getImage(UIActivator.REFRESH_ICON));
		refreshBtn
				.setToolTipText(Messages.RouterDependenciesEditor_refreshDependenciesTooltip);

		// create data tables
		SashForm sashForm = new SashForm(composite, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = columnCount;
		sashForm.setLayoutData(gd);
		
		SashForm leftPart = new SashForm(sashForm, SWT.VERTICAL);
		leftPart.setLayout(new GridLayout(1, false));
		importPackageViewer = createTableViewer(leftPart, toolkit,
				Messages.RouterDependenciesEditor_importPackageSec,
				IDependencyItem.IMPORT_PACKAGE, SWT.NONE);
		exportPackageViewer = createTableViewer(leftPart, toolkit,
				Messages.RouterDependenciesEditor_exportPackage, IDependencyItem.EXPORT_PACKAGE, SWT.NONE);

		SashForm rightPart = new SashForm(sashForm, SWT.VERTICAL);
		rightPart.setLayout(new GridLayout(1, false));
		bundleClasspathViewer = createTableViewer(rightPart, toolkit,
				Messages.RouterDependenciesEditor_classpathSec,
				IDependencyItem.CLASS_PATH, SWT.CHECK);
		requireBundleViewer = createTableViewer(rightPart, toolkit,
				Messages.RouterDependenciesEditor_requireBundleSec,
				IDependencyItem.REQUIRE_BUNDLE, SWT.NONE);
	
		toolkit.adapt(rightPart);
		toolkit.adapt(leftPart);
		toolkit.adapt(sashForm);
		toolkit.adapt(composite);

		top.setExpandHorizontal(true);
		top.setExpandVertical(true);

		Point minSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		top.setMinSize(minSize.x, minSize.y);
		
		//create status
		Composite statusComposite = toolkit.createComposite(parent);
		statusComposite.setLayout(new GridLayout(3, false));
		statusComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		statusLabel = toolkit.createLabel(statusComposite, ""); //$NON-NLS-1$
		statusLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		toolkit.createLabel(statusComposite, "|"); //$NON-NLS-1$
		
		Label shortCuts = toolkit.createLabel(statusComposite, "", SWT.SHADOW_OUT); //$NON-NLS-1$
		shortCuts.setText(Messages.RouterDependenciesEditor_KeyBindingw);
		
		// add filter listener
		filterText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text t = (Text) e.widget;
				final String filterString = t.getText().trim();
				t.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						importPackageViewer.setFilterString(filterString);
						requireBundleViewer.setFilterString(filterString);
						bundleClasspathViewer.setFilterString(filterString);
						exportPackageViewer.setFilterString(filterString);
					}
				});
			}
		});

		// add hide listener
		hideBuiltIn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.widget;
				final boolean show = b.getSelection();
				b.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						importPackageViewer.setShowBuiltIn(!show);
						requireBundleViewer.setShowBuiltIn(!show);
						bundleClasspathViewer.setShowBuiltIn(!show);
						exportPackageViewer.setShowBuiltIn(!show);
					}
				});
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
		if(message == null){
			message = "";
		}
		statusLabel.setText(message);
		statusLabel.setToolTipText(message);
	}

	protected void refreshDependencies() {
		try {
			List<IEditorPart> dirtyList = new ArrayList<IEditorPart>();
			
			IEditorPart processEditor = getCorrespondingProcessEditor();
			if (processEditor != null && processEditor.isDirty()) {
				dirtyList.add(processEditor);
			}
			if (isDirty()) {
				dirtyList.add(this);
			}
			if (!dirtyList.isEmpty()) {
				RelativeEditorsSaveDialog dialog = new RelativeEditorsSaveDialog(getSite().getShell(), dirtyList);
				int open = dialog.open();
				if( open != Dialog.OK){
					return;
				}
			}
			updateInput();
			setDirty(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateInput() {
		UIJob job = new UIJob(getSite().getShell().getDisplay(),
				Messages.RouterDependenciesEditor_refreshing) {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				//check camel editor and update the input source every time
				try {
					updatePropertyFromProcessEditor(getCorrespondingProcessEditor());
				} catch (PersistenceException e) {
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}
				
				//clear all datas first
				importPackages.clear();
				requireBundles.clear();
				bundleClasspaths.clear();
				exportPackages.clear();
				
				// re-calculate all datas
				RouterOsgiDependenciesResolver resolver = new RouterOsgiDependenciesResolver(
						(ProcessItem) property.getItem(),
						property.getAdditionalProperties());
				importPackages.addAll(resolver.getImportPackages());
				requireBundles.addAll(resolver.getRequireBundles());
				bundleClasspaths.addAll(resolver.getBundleClasspaths());
				exportPackages.addAll(resolver.getExportPackages());
				
				// set datas
				importPackageViewer.setInput(importPackages);
				requireBundleViewer.setInput(requireBundles);
				bundleClasspathViewer.setInput(bundleClasspaths);
				exportPackageViewer.setInput(exportPackages);
				
				// check the status of classpath items
				TableItem[] items = bundleClasspathViewer.getTable().getItems();
				for(TableItem ti: items){
					Object data = ti.getData();
					if(data !=null && data instanceof IDependencyItem){
						ti.setChecked(((IDependencyItem)data).isChecked());
					}
				}

				// show datas
				importPackageViewer.refresh();
				requireBundleViewer.refresh();
				bundleClasspathViewer.refresh();
				exportPackageViewer.refresh();
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	private RouterDependenciesTableViewer createTableViewer(Composite parent, FormToolkit toolkit, String title, int type, int style) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR );
		section.setText(title);
		section.setLayout(new GridLayout(1, false));
		RouterDependenciesPanel c = new RouterDependenciesPanel(section,
				style, type, toolkit);
		section.setClient(c);
		toolkit.adapt(c);
		c.addDependenciesChangedListener(this);
		c.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				int size = selection.size();
				if(size == 0){
					setStatus("");
				}else if(selection.size()==1){
					setStatus(((IDependencyItem)selection.getFirstElement()).getDescription());
				}else{
					setStatus(size+Messages.RouterDependenciesEditor_multiItemsSelectedStatusMsg);
				}
			}
		});
		return c.getTableViewer();
	}

	@Override
	public void dependencesChanged() {
		setDirty(true);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			//check editor and update the input source before saving
			IEditorPart processEditor = getCorrespondingProcessEditor();
			updatePropertyFromProcessEditor(processEditor);
			
			//save all datas
			DependenciesCoreUtil.saveToMap(property.getAdditionalProperties(),
					(List<BundleClasspath>) bundleClasspathViewer.getInput(),
					(List<ImportPackage>) importPackageViewer.getInput(),
					(List<RequireBundle>) requireBundleViewer.getInput(),
					(List<ExportPackage>)exportPackageViewer.getInput());
			
			// save model
			
			//record process editor dirty signal first
			boolean processEditorDirty = true;
			if(processEditor !=null){
				processEditorDirty = processEditor.isDirty();
			}
			
			// save current editor
			IProxyRepositoryFactory factory = ProxyRepositoryFactory
					.getInstance();
			factory.save(property.getItem());
			setDirty(false);
			
			// save process editor if it was un-dirty
			if(!processEditorDirty && processEditor!=null){
				processEditor.doSave(monitor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * return Editor if the process editor exist
	 * 
	 * @return
	 * @throws PersistenceException
	 */
	private IEditorPart getCorrespondingProcessEditor() throws PersistenceException {
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		Item item = property.getItem();
		CamelProcessEditorInput processEditorInput = new CamelProcessEditorInput(
				(CamelProcessItem) item, true, true, false);

		IEditorPart processEditor = activePage
				.findEditor((IEditorInput) processEditorInput);
		return processEditor;
	}
	
	/**
	 * check the corresponding editor is opened or not
	 * if it's opened, then reuse the property object with it
	 * else no changes
	 */
	private void updatePropertyFromProcessEditor(IEditorPart processEditor) {
		if (processEditor != null) {
			property = ((RepositoryNode) processEditor.getEditorInput()
					.getAdapter(RepositoryNode.class)).getObject()
					.getProperty().getItem().getProperty();
		}
	}

	private boolean isDirty = false;

	private Label statusLabel;

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		firePropertyChange(PROP_DIRTY);
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
	}
}
