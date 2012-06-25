// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.resource.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.camel.designer.dialog.RouteResourceSelectionDialog;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.resource.ui.providers.ResourceContentProvider;
import org.talend.designer.camel.resource.ui.providers.ResourceLabelProvider;
import org.talend.repository.model.RepositoryNode;

/**
 * @author xpli
 * 
 */
public class ManageRouteResourceDialog extends TitleAreaDialog {

	private static final String ROUTE_RESOURCES_PROP = "ROUTE_RESOURCES_PROP";

	private Button addBtn, delBtn;

	private List<RouteResourceItem> selectedItems;

	private TableViewer resourcesTV;

	private ISelection selection;

	private RepositoryNode node;

	public ManageRouteResourceDialog(Shell parentShell, ISelection iSelection) {
		super(parentShell);
		this.selection = iSelection;

		init();
	}

	protected void addData() {

		RouteResourceSelectionDialog dialog = new RouteResourceSelectionDialog(
				Display.getCurrent().getActiveShell());
		int open = dialog.open();
		if (open == Dialog.OK) {
			RepositoryNode result = dialog.getResult();
			Item item = result.getObject().getProperty().getItem();
			if (item instanceof RouteResourceItem
					&& !selectedItems.contains(item)) {
				selectedItems.add((RouteResourceItem) item);
				resourcesTV.refresh();
				resourcesTV.setSelection(new StructuredSelection(item));
			}
		}

	}

	private String buildValue() {
		StringBuffer sb = new StringBuffer();
		for (RouteResourceItem item : selectedItems) {
			sb.append(item.getProperty().getId());
			sb.append(",");
		}
		return sb.toString();
	}

	@Override
	protected void buttonPressed(int buttonId) {

		if (buttonId == IDialogConstants.OK_ID) {
			EMap additionalProperties = node.getObject().getProperty()
					.getAdditionalProperties();

			if (additionalProperties != null) {
				additionalProperties.put(ROUTE_RESOURCES_PROP, buildValue());
			}

			try {
				ProxyRepositoryFactory.getInstance().save(
						node.getObject().getProperty().getItem(), false);
			} catch (PersistenceException e) {
			}

		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Manage Route Resources");
	}

	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		setHelpAvailable(false);
		setTitle("Manage Route Resources");
		setMessage("Manage Route Resources");

		Composite container = (Composite) super.createDialogArea(parent);

		Composite area = new Composite(container, SWT.NONE);
		area.setLayout(new GridLayout(2, false));
		area.setLayoutData(new GridData(GridData.FILL_BOTH));

		resourcesTV = new TableViewer(area, SWT.BORDER | SWT.SINGLE);
		Table table = resourcesTV.getTable();
		table.setLinesVisible(true);
		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				resourcesTV, 300);
		tableViewerColumn.getColumn().setText("");
		tableViewerColumn.getColumn().setWidth(300);
		tableViewerColumn.getColumn().setAlignment(SWT.LEFT);
		resourcesTV.setLabelProvider(new ResourceLabelProvider());
		resourcesTV.setContentProvider(new ResourceContentProvider());
		resourcesTV.setInput(selectedItems);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(gridData);

		Composite buttonComp = new Composite(area, SWT.NONE);
		buttonComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		buttonComp.setLayout(new GridLayout(1, false));

		addBtn = new Button(buttonComp, SWT.PUSH);
		addBtn.setImage(ImageProvider.getImage(EImage.ADD_ICON));
		addBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addData();
			}
		});

		delBtn = new Button(buttonComp, SWT.PUSH);
		delBtn.setImage(ImageProvider.getImage(EImage.DELETE_ICON));
		delBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteData();
			}
		});

		return container;
	}

	protected void deleteData() {
		RouteResourceItem item = getSelectiedItem();
		if (item != null) {
			selectedItems.remove(item);
			resourcesTV.refresh();
		}
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 350);
	}

	private RouteResourceItem getSelectiedItem() {
		IStructuredSelection selection2 = (IStructuredSelection) resourcesTV
				.getSelection();
		return (RouteResourceItem) selection2.getFirstElement();
	}

	private void init() {

		selectedItems = new ArrayList<RouteResourceItem>();
		IStructuredSelection sselection = (IStructuredSelection) selection;
		Object element = sselection.getFirstElement();
		node = (RepositoryNode) element;

		Property property = (Property) node.getObject().getProperty();

		EMap additionalProperties = property.getAdditionalProperties();
		if (additionalProperties != null) {
			Object resourcesObj = additionalProperties
					.get(ROUTE_RESOURCES_PROP);
			if (resourcesObj != null) {
				String[] resourceIds = resourcesObj.toString().split(",");
				for (String id : resourceIds) {
					try {
						IRepositoryViewObject rvo = ProxyRepositoryFactory
								.getInstance().getLastVersion(id);
						if (rvo != null) {
							Item item = rvo.getProperty().getItem();
							selectedItems.add((RouteResourceItem) item);
						}
					} catch (PersistenceException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

}
