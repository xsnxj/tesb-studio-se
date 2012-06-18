package org.talend.designer.camel.dependencies.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
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
		initialize();
	}

	private void initialize() {
		RouterOsgiDependenciesResolver resolver = new RouterOsgiDependenciesResolver(
				((ProcessItem) property.getItem()).getProcess(),
				property.getAdditionalProperties());
		importPackages.addAll(resolver.getImportPackages());
		requireBundles.addAll(resolver.getRequireBundles());
		bundleClasspaths.addAll(resolver.getBundleClasspaths());
	}

	@Override
	public void createPartControl(Composite parent) {
		int columnCount = 1;
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		parent.setLayout(new GridLayout(columnCount, false));

		Composite toolsPanel = toolkit.createComposite(parent);
		toolsPanel.setLayout(new GridLayout(3, false));
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
	}

	private void createImportPackageSection(Composite parent,
			FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(Messages.RouterDependenciesEditor_importPackageSec);
		importPackageViewer = createTableViewer(section, toolkit,
				IDependencyItem.IMPORT_PACKAGE);
		importPackageViewer.setInput(importPackages);
	}

	private void createRequireBundleSection(Composite parent,
			FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(Messages.RouterDependenciesEditor_requireBundleSec);
		requireBundleViewer = createTableViewer(section, toolkit,
				IDependencyItem.REQUIRE_BUNDLE);
		requireBundleViewer.setInput(requireBundles);
	}

	private void createBundleClasspathSection(Composite parent,
			FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(Messages.RouterDependenciesEditor_classpathSec);
		bundleClasspathViewer = createTableViewer(section, toolkit,
				IDependencyItem.CLASS_PATH);
		bundleClasspathViewer.setInput(bundleClasspaths);
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
		isDirty = true;
		firePropertyChange(PROP_DIRTY);
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
			isDirty = false;
			firePropertyChange(PROP_DIRTY);
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
