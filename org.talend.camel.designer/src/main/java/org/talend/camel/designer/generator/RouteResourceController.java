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
package org.talend.camel.designer.generator;

import java.beans.PropertyChangeEvent;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IControlCreator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.talend.camel.designer.dialog.RouteResourceSelectionDialog;
import org.talend.camel.designer.ui.editor.CamelMultiPageTalendEditor;
import org.talend.camel.designer.ui.editor.CamelProcessEditorInput;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.CorePlugin;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.properties.tab.IDynamicProperty;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.properties.controllers.AbstractElementPropertySectionController;
import org.talend.designer.core.ui.editor.properties.controllers.creator.SelectAllTextControlCreator;

/**
 * @author Xiaopeng Li
 * 
 */
public class RouteResourceController extends
		AbstractElementPropertySectionController {

	private static final String STRING = ":";

	public static final String COMMA = ";";

	private Text labelText;

	SelectionListener listenerSelection = new SelectionListener() {

		public void widgetDefaultSelected(SelectionEvent e) {
			// do nothing.
		}

		public void widgetSelected(SelectionEvent e) {
			Command cmd = createCommand(e);
			executeCommand(cmd);
		}
	};

	public RouteResourceController(IDynamicProperty dp) {
		super(dp);
	}

	/**
	 * DOC nrousseau Comment method "createButtonCommand".
	 * 
	 * @param source
	 * @return
	 */
	private Command createButtonCommand(Button button) {
		RouteResourceSelectionDialog dialog = new RouteResourceSelectionDialog(
				button.getShell());

		selectNodeIfExists(button, dialog);

		if (dialog.open() == Window.OK) {

			IRepositoryViewObject repositoryObject = dialog.getResult()
					.getObject();

			refreshItemeProperty(repositoryObject);

			final Item item = repositoryObject.getProperty().getItem();
			String id = item.getProperty().getId();
			String paramName = (String) button.getData(PARAMETER_NAME);

			return new RouteResourceChangeCommand(elem, paramName, id);
		}
		return null;
	}

	private Command createCommand(SelectionEvent selectionEvent) {
		if (selectionEvent.getSource() instanceof Button) {
			return createButtonCommand((Button) selectionEvent.getSource());
		}
		return null;
	}

	@Override
	public Control createControl(final Composite subComposite,
			final IElementParameter param, final int numInRow,
			final int nbInRow, final int top, final Control lastControl) {
		this.curParameter = param;
		this.paramFieldType = param.getFieldType();
		FormData data;

		IElementParameter processTypeParameter = param.getChildParameters()
				.get(EParameterName.ROUTE_RESOURCE_TYPE_ID.getName());

		final DecoratedField dField = new DecoratedField(subComposite,
				SWT.BORDER | SWT.READ_ONLY, new SelectAllTextControlCreator());
		if (param.isRequired()) {
			FieldDecoration decoration = FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
			dField.addFieldDecoration(decoration, SWT.RIGHT | SWT.TOP, false);
		}
		Control cLayout = dField.getLayoutControl();

		labelText = (Text) dField.getControl();

		labelText.setData(PARAMETER_NAME, param.getName());

		cLayout.setBackground(subComposite.getBackground());
		labelText.setEditable(false);
		if (elem instanceof Node) {
			labelText
					.setToolTipText(VARIABLE_TOOLTIP + param.getVariableName());
		}

		addDragAndDropTarget(labelText);

		CLabel labelLabel = getWidgetFactory().createCLabel(subComposite,
				param.getDisplayName());
		data = new FormData();
		if (lastControl != null) {
			data.left = new FormAttachment(lastControl, 0);
		} else {
			data.left = new FormAttachment(
					(((numInRow - 1) * MAX_PERCENT) / (nbInRow + 1)), 0);
		}
		data.top = new FormAttachment(0, top);
		labelLabel.setLayoutData(data);
		if (numInRow != 1) {
			labelLabel.setAlignment(SWT.RIGHT);
		}

		data = new FormData();
		int currentLabelWidth = STANDARD_LABEL_WIDTH;
		GC gc = new GC(labelLabel);
		Point labelSize = gc.stringExtent(param.getDisplayName());
		gc.dispose();
		if ((labelSize.x + ITabbedPropertyConstants.HSPACE) > currentLabelWidth) {
			currentLabelWidth = labelSize.x + ITabbedPropertyConstants.HSPACE;
		}

		if (numInRow == 1) {
			if (lastControl != null) {
				data.left = new FormAttachment(lastControl, currentLabelWidth);
			} else {
				data.left = new FormAttachment(0, currentLabelWidth);
			}

		} else {
			data.left = new FormAttachment(labelLabel, 0, SWT.RIGHT);
		}
		data.right = new FormAttachment((numInRow * MAX_PERCENT)
				/ (nbInRow + 1), 0);
		data.top = new FormAttachment(0, top);
		cLayout.setLayoutData(data);

		Button btn;
		Point btnSize;

		btn = getWidgetFactory().createButton(subComposite, "", SWT.PUSH); //$NON-NLS-1$
		btnSize = btn.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		btn.setImage(ImageProvider.getImage(CorePlugin
				.getImageDescriptor(DOTS_BUTTON)));

		btn.addSelectionListener(listenerSelection);
		btn.setData(PARAMETER_NAME, param.getName() + STRING
				+ processTypeParameter.getName()); //$NON-NLS-1$
		btn.setEnabled(!param.isReadOnly());
		data = new FormData();
		data.left = new FormAttachment(cLayout, 0);
		data.right = new FormAttachment(cLayout, STANDARD_BUTTON_WIDTH,
				SWT.RIGHT);
		data.top = new FormAttachment(0, top);
		data.height = STANDARD_HEIGHT - 2;
		btn.setLayoutData(data);

		hashCurControls.put(
				param.getName() + STRING + processTypeParameter.getName(),
				labelText);
		Point initialSize = dField.getLayoutControl().computeSize(SWT.DEFAULT,
				SWT.DEFAULT);

		dynamicProperty.setCurRowSize(Math.max(initialSize.y, btnSize.y)
				+ ITabbedPropertyConstants.VSPACE);
		return btn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.designer.core.ui.editor.properties.controllers.
	 * AbstractElementPropertySectionController#estimateRowSize
	 * (org.eclipse.swt.widgets.Composite,
	 * org.talend.core.model.process.IElementParameter)
	 */
	@Override
	public int estimateRowSize(Composite subComposite, IElementParameter param) {
		final DecoratedField dField = new DecoratedField(subComposite,
				SWT.BORDER, new IControlCreator() {

					public Control createControl(Composite parent, int style) {
						return getWidgetFactory().createButton(
								parent,
								EParameterName.ROUTE_RESOURCE_TYPE
										.getDisplayName(), SWT.None);
					}

				});
		Point initialSize = dField.getLayoutControl().computeSize(SWT.DEFAULT,
				SWT.DEFAULT);
		dField.getLayoutControl().dispose();

		return initialSize.y + ITabbedPropertyConstants.VSPACE;
	}

	protected String getlabel(Item item) {
		String label = item.getProperty().getDisplayName();
		String parentPaths = item.getState().getPath();
		if (parentPaths != null && !parentPaths.isEmpty()) {
			label = parentPaths + "/" + label;
		}
		return label;
	}

	public void propertyChange(PropertyChangeEvent arg0) {

	}

	@Override
	public void refresh(final IElementParameter param, boolean check) {
		new Thread() {
			@Override
			public void run() {

				Display.getDefault().syncExec(new Runnable() {

					public void run() {
						if (hashCurControls == null) {
							return;
						}
						IElementParameter processTypeParameter = param
								.getChildParameters().get(
										EParameterName.ROUTE_RESOURCE_TYPE_ID
												.getName());
						String value = (String) processTypeParameter.getValue();
						IRepositoryViewObject lastVersion;
						try {
							lastVersion = ProxyRepositoryFactory.getInstance()
									.getLastVersion(value);
							resetTextValue(lastVersion.getProperty().getItem());
						} catch (Exception e) {
						}

						if (elem != null && elem instanceof Node) {
							((Node) elem).checkAndRefreshNode();
						}
					}
				});

			}
		}.start();

	}

	private void refreshItemeProperty(IRepositoryViewObject repositoryObject) {

		IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (activeEditor != null
				&& activeEditor instanceof CamelMultiPageTalendEditor) {
			CamelMultiPageTalendEditor camelEdtior = (CamelMultiPageTalendEditor) activeEditor;
			IEditorInput editorInput = camelEdtior.getEditorInput();
			CamelProcessEditorInput input = (CamelProcessEditorInput) editorInput;
			Item item = input.getItem();

			EMap additionalProperties = item.getProperty()
					.getAdditionalProperties();

			String id = repositoryObject.getId();

			if (additionalProperties != null) {
				Object object = additionalProperties
						.get("ROUTE_RESOURCES_PROP");
				if (object == null) {
					additionalProperties.put("ROUTE_RESOURCES_PROP", id);
				} else {
					String idStrs = object.toString();
					String[] strings = idStrs.split(",");
					boolean contained = false;
					for (String str : strings) {
						if (str.trim().equals(id)) {
							contained = true;
						}
					}
					if (!contained) {
						idStrs = idStrs + "," + id;
						additionalProperties
								.put("ROUTE_RESOURCES_PROP", idStrs);
					}
				}
			}

			try {
				ProxyRepositoryFactory.getInstance().save(item, false);
			} catch (PersistenceException e) {
			}
		}

	}

	private void resetTextValue(final Item item) {
		StringBuffer sb = new StringBuffer();
		sb.append("Resource: ");
		sb.append(getlabel(item));
		labelText.setText(sb.toString());

	}

	/**
	 * see feature 0003664: tRunJob: When opening the tree dialog to select the
	 * job target, it could be useful to open it on previous selected job if
	 * exists.
	 * 
	 * @param button
	 * @param dialog
	 */
	private void selectNodeIfExists(Button button,
			RouteResourceSelectionDialog dialog) {
		try {
			if (elem != null && elem instanceof Node) {
				Node runJobNode = (Node) elem;
				String paramName = (String) button.getData(PARAMETER_NAME);
				String jobId = (String) runJobNode.getPropertyValue(paramName); // .getElementParameter(name).getValue();
				dialog.setSelectedNodeId(jobId);
			}
		} catch (Throwable e) {
			ExceptionHandler.process(e);
		}
	}
}
