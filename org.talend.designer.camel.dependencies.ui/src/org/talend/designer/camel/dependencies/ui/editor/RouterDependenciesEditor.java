package org.talend.designer.camel.dependencies.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.camel.dependencies.core.model.IDependencyItem;
import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.camel.dependencies.core.model.RequireBundle;
import org.talend.designer.camel.dependencies.core.util.DependenciesCoreUtil;
import org.talend.designer.camel.dependencies.core.util.RouterOsgiDependenciesResolver;
import org.talend.designer.camel.dependencies.ui.Messages;
import org.talend.designer.camel.dependencies.ui.UIActivator;
import org.talend.designer.camel.dependencies.ui.dialog.RelativeEditorsSaveDialog;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;

public class RouterDependenciesEditor extends EditorPart implements
		IRouterDependenciesChangedListener {

	public static final String ID = "org.talend.designer.camel.dependencies.ui.dependenciesEditor"; //$NON-NLS-1$

	private List<ImportPackage> importPackages = new ArrayList<ImportPackage>();
	private List<RequireBundle> requireBundles = new ArrayList<RequireBundle>();
	private List<BundleClasspath> bundleClasspaths = new ArrayList<BundleClasspath>();
	private RouterDependenciesTableViewer requireBundleViewer;
	private RouterDependenciesTableViewer bundleClasspathViewer;
	private RouterDependenciesTableViewer importPackageViewer;

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
		parent.setLayout(new GridLayout(columnCount, false));

		Composite toolsPanel = toolkit.createComposite(parent);
		toolsPanel.setLayout(new GridLayout(4, false));
		toolsPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolsPanel.setBackground(toolkit.getColors()
				.getColor(IFormColors.TB_BG));
		toolsPanel.setBackgroundMode(SWT.INHERIT_FORCE);

		Label filterLabel = toolkit.createLabel(toolsPanel, Messages.RouterDependenciesEditor_filterLabel);
		filterLabel.setBackground(toolsPanel.getBackground());
		Text filterText = toolkit.createText(toolsPanel, ""); //$NON-NLS-1$
		filterText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button hideBuiltIn = toolkit.createButton(toolsPanel,
				Messages.RouterDependenciesEditor_hideBuiltInItems, SWT.CHECK);
		hideBuiltIn.setBackground(toolsPanel.getBackground());
		
		Button refreshBtn = toolkit.createButton(toolsPanel, "", SWT.PUSH); //$NON-NLS-1$
		refreshBtn.setImage(UIActivator.getImage(UIActivator.REFRESH_ICON));
		refreshBtn.setToolTipText(Messages.RouterDependenciesEditor_refreshDependenciesTooltip);

		SashForm sashForm = new SashForm(parent, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = columnCount;
		sashForm.setLayoutData(gd);
		createImportPackageSection(sashForm, toolkit);

		SashForm rightPart = new SashForm(sashForm, SWT.VERTICAL);
		createRequireBundleSection(rightPart, toolkit);
		createBundleClasspathSection(rightPart, toolkit);

		toolkit.adapt(rightPart);
		toolkit.adapt(sashForm);
		toolkit.adapt(parent);

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
					}
				});
			}
		});

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
		updateInput();
	}
	
	protected void refreshDependencies() {
		try {
			IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			Item item = property.getItem();
			CamelProcessEditorInput processEditorInput = new CamelProcessEditorInput((CamelProcessItem) item, true, true, false);

			IEditorPart processEditor = activePage
					.findEditor((IEditorInput) processEditorInput);

			final List<IEditorPart> dirtyList = new ArrayList<IEditorPart>();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateInput() {
		UIJob job = new UIJob(getSite().getShell().getDisplay(),Messages.RouterDependenciesEditor_refreshing) {
			
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				importPackages.clear();
				requireBundles.clear();
				bundleClasspaths.clear();
				RouterOsgiDependenciesResolver resolver = new RouterOsgiDependenciesResolver(
						(ProcessItem) property.getItem(),
						property.getAdditionalProperties());
				importPackages.addAll(resolver.getImportPackages());
				requireBundles.addAll(resolver.getRequireBundles());
				bundleClasspaths.addAll(resolver.getBundleClasspaths());
						importPackageViewer.setInput(importPackages);
						requireBundleViewer.setInput(requireBundles);
						bundleClasspathViewer.setInput(bundleClasspaths);
						
						importPackageViewer.refresh();
						requireBundleViewer.refresh();
						bundleClasspathViewer.refresh();
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	private void createImportPackageSection(Composite parent,
			FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(Messages.RouterDependenciesEditor_importPackageSec);
		importPackageViewer = createTableViewer(section, toolkit,
				IDependencyItem.IMPORT_PACKAGE);
	}

	private void createRequireBundleSection(Composite parent,
			FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(Messages.RouterDependenciesEditor_requireBundleSec);
		requireBundleViewer = createTableViewer(section, toolkit,
				IDependencyItem.REQUIRE_BUNDLE);
	}

	private void createBundleClasspathSection(Composite parent,
			FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(Messages.RouterDependenciesEditor_classpathSec);
		bundleClasspathViewer = createTableViewer(section, toolkit,
				IDependencyItem.CLASS_PATH);
	}

	private RouterDependenciesTableViewer createTableViewer(Section parent,
			FormToolkit toolkit, int type) {
		parent.setLayout(new GridLayout(1, false));
		RouterDependenciesPanel c = new RouterDependenciesPanel(parent,
				SWT.NONE, type, toolkit);
		parent.setClient(c);
		toolkit.adapt(c);
		c.addDependenciesChangedListener(this);
		return c.getTableViewer();
	}

	@Override
	public void dependencesChanged() {
		setDirty(true);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			DependenciesCoreUtil.saveToMap(property.getAdditionalProperties(),
					(List<BundleClasspath>) bundleClasspathViewer.getInput(),
					(List<ImportPackage>) importPackageViewer.getInput(),
					(List<RequireBundle>) requireBundleViewer.getInput());
			IProxyRepositoryFactory factory = ProxyRepositoryFactory
					.getInstance();
			factory.save(property.getItem());
			setDirty(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean isDirty = false;

	private Property property;

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
