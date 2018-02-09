// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProjectRepositoryNode;
import org.talend.core.repository.ui.utils.RecombineRepositoryNodeUtil;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.nodes.IProjectRepositoryNode;
import org.talend.repository.viewer.ui.provider.RepositoryViewerProvider;
import org.talend.repository.viewer.ui.viewer.RepositoryTreeViewer;

/**
 * @author xpli
 * 
 */
public class RouteResourceSelectionDialog extends Dialog {

	ERepositoryObjectType type;

	String repositoryType;

	private RepositoryNode result;

	private RepositoryTreeViewer repositoryTreeViewer;

	private String selectedNodeId;

	public RouteResourceSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	public void addFilter(ViewerFilter filter) {
		if (filter != null) {
			repositoryTreeViewer.addFilter(filter);
		}
	}

	/**
	 * Configures the shell
	 * 
	 * @param shell
	 *            the shell
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Select a Route Resource");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);

		selectNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);
		GridData data = (GridData) container.getLayoutData();
		data.minimumHeight = 400;
		data.heightHint = 400;
		data.minimumWidth = 500;
		data.widthHint = 500;
		container.setLayoutData(data);

		Composite viewContainer = new Composite(container, SWT.BORDER);
		viewContainer.setLayout(new GridLayout());
		viewContainer.setLayoutData(new GridData(GridData.FILL_BOTH));

		RepositoryViewerProvider provider = new RepositoryViewerProvider() {

			@Override
			protected TreeViewer createTreeViewer(Composite parent, int style) {
				return new RepositoryTreeViewer(parent, style);
			}

			@Override
			protected IRepositoryNode getInputRoot(
					IProjectRepositoryNode projectRepoNode) {
				return getInput();
			}

			@Override
			protected int getStyle() {
				// http://jira.talendforge.org/browse/TESB-6582 Xiaopeng Li
				return SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL;
			}

		};

		repositoryTreeViewer = (RepositoryTreeViewer) provider
				.createViewer(viewContainer);

		repositoryTreeViewer.getTree().setLayoutData(
				new GridData(GridData.FILL_BOTH));

		repositoryTreeViewer.expandAll();

		repositoryTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						boolean highlightOKButton = isSelectionValid(event);
						getButton(IDialogConstants.OK_ID).setEnabled(
								highlightOKButton);
					}

				});
		repositoryTreeViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				if (getButton(IDialogConstants.OK_ID).isEnabled()) {
					okPressed();
				}
			}
		});

		addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof IRepositoryNode) {
					return true;
				}
				return false;
			}
		});

		return container;
	}

	private IRepositoryNode getInput() {

		List<ERepositoryObjectType> types = new ArrayList<ERepositoryObjectType>();
		types.add(CamelRepositoryNodeType.repositoryRouteResourceType);
		IRepositoryNode root = RecombineRepositoryNodeUtil
				.getFixingTypesInputRoot(ProjectRepositoryNode.getInstance(),
						types);
		return root;

	}

	protected RepositoryTreeViewer getRepositoryTreeViewer() {
		return repositoryTreeViewer;
	}

	public RepositoryNode getResult() {
		return result;
	}

	protected boolean isSelectionValid(SelectionChangedEvent event) {
		boolean highlightOKButton = true;
		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		if (selection == null || selection.size() != 1) {
			highlightOKButton = false;
		} else {
			RepositoryNode node = (RepositoryNode) selection.getFirstElement();
			IRepositoryViewObject object = node.getObject();
			if (object == null) {
				highlightOKButton = false;
			}
			if (object != null
					&& object.getRepositoryObjectType() != CamelRepositoryNodeType.repositoryRouteResourceType) {
				highlightOKButton = false;
			}

		}
		return highlightOKButton;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		IStructuredSelection selection = (IStructuredSelection) getRepositoryTreeViewer()
				.getSelection();
		result = (RepositoryNode) selection.getFirstElement();
		super.okPressed();
	}


	public void selectItem(IRepositoryNode node) {
		if (node != null) {
			repositoryTreeViewer.setSelection(new StructuredSelection(node));
		}

	}

	private void selectNode() {
		RepositoryNode root = (RepositoryNode) getRepositoryTreeViewer()
				.getInput();
		selectNode(root, CamelRepositoryNodeType.repositoryRouteResourceType,
				this.selectedNodeId, true);
	}


	private void selectNode(RepositoryNode root,
			ERepositoryObjectType selectionType, String idOrLabel,
			boolean isSelectionId) {
		if (idOrLabel == null) {
			return;
		}

		boolean valid = false;
		if (isSelectionId) {
			IRepositoryViewObject object = root.getObject();
			if (object != null && idOrLabel.equals(object.getId())) {
				valid = true;
			}
		} else if (idOrLabel.equals(root.getProperties(EProperties.LABEL))) {
			valid = true;
		}
		if (valid) {
			getRepositoryTreeViewer().setSelection(
					new StructuredSelection(root), true);
		} else if (root.hasChildren()) {
			for (IRepositoryNode child : root.getChildren()) {
				selectNode((RepositoryNode) child, selectionType, idOrLabel,
						isSelectionId);
			}
		}
	}

	public void setSelectedNodeId(String selectionNode) {
		this.selectedNodeId = selectionNode;
	}

}
