// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.esb.webservice.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.ui.swt.advanced.dataeditor.AbstractDataTableEditorView;
import org.talend.commons.ui.swt.extended.table.ExtendedTableModel;
import org.talend.commons.ui.swt.formtools.LabelledFileField;
import org.talend.commons.ui.swt.tableviewer.TableViewerCreator;
import org.talend.commons.ui.swt.tableviewer.TableViewerCreatorColumn;
import org.talend.commons.ui.utils.PathUtils;
import org.talend.commons.utils.data.bean.IBeanPropertyAccessors;
import org.talend.core.CorePlugin;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.IESBService;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.utils.TalendTextUtils;
import org.talend.core.prefs.ITalendCorePrefConstants;
import org.talend.core.ui.proposal.TalendProposalUtils;
import org.talend.designer.esb.webservice.WebServiceComponentPlugin;
import org.talend.designer.esb.webservice.WebServiceNode;
import org.talend.designer.esb.webservice.ws.wsdlinfo.Function;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.dialog.RepositoryReviewDialog;

/**
 * gcui class global comment. Detailled comment
 */
public class WebServiceUI extends WizardPage {

	protected int maximumRowsToPreview = CorePlugin.getDefault().getPreferenceStore()
			.getInt(ITalendCorePrefConstants.PREVIEW_LIMIT);

	private WebServiceNode webServiceNode;

	private LabelledFileField wsdlField;

	private AbstractDataTableEditorView<String> portListTableView;
	private AbstractDataTableEditorView<Function> functionListTableView;

	private Button refreshbut;
	private Button servicebut;

	private Button populateCheckbox;

	private WebServiceUIPresenter presenter;

	public WebServiceUI(WebServiceNode webServiceNode) {
		super("WebServiceUI"); //$NON-NLS-1$
		setTitle("Configure component with Web Service operation");
		this.webServiceNode = webServiceNode;
		this.presenter = new WebServiceUIPresenter(this, webServiceNode);
	}

	private static abstract class ReadOnlyBeanStringPropertyAccessors<T> implements IBeanPropertyAccessors<T, String> {
		@Override
		public void set(T bean, String value) {
		}
	}

	private class DataTableEditorView<T> extends AbstractDataTableEditorView<T> {

		private IBeanPropertyAccessors<T, String> accessors;

		public DataTableEditorView(Composite parent, ExtendedTableModel<T> model,
				IBeanPropertyAccessors<T, String> accessors) {
			super(parent, SWT.NONE, model, false, true, false, false);
			this.accessors = accessors;
			initGraphicComponents();
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			gridData.minimumHeight = 150;
			this.getMainComposite().setLayoutData(gridData);
			GridLayout layout = (GridLayout) this.getMainComposite().getLayout();
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}

		protected void setTableViewerCreatorOptions(TableViewerCreator<T> newTableViewerCreator) {
			super.setTableViewerCreatorOptions(newTableViewerCreator);
			newTableViewerCreator.setHeaderVisible(false);
			newTableViewerCreator.setVerticalScroll(true);
			newTableViewerCreator.setReadOnly(true);
		}

		protected void createColumns(TableViewerCreator<T> tableViewerCreator, Table table) {
			TableViewerCreatorColumn<T, String> rowColumn = new TableViewerCreatorColumn<T, String>(tableViewerCreator);
			rowColumn.setBeanPropertyAccessors(accessors);
			rowColumn.setWeight(60);
			rowColumn.setModifiable(true);
			rowColumn.setMinimumWidth(60);
			rowColumn.setCellEditor(new TextCellEditor(tableViewerCreator.getTable()));
		}
	};

	public void createControl(Composite parent) {
		presenter.initWithCurrentSetting();

		Composite wsdlComposite = new Composite(parent, SWT.NONE);

		// WSDL URL
		// 3 columns
		createWsdlFieldControl(wsdlComposite);

		int wsdlUrlcompositeColumn = 4;
		// TESB-3590，gliu
		if (WebServiceComponentPlugin.hasRepositoryServices()) {
			wsdlUrlcompositeColumn = 5;
			servicebut = createPushButton(wsdlComposite, "WebServiceUI.Services", null);
		}
		refreshbut = createPushButton(wsdlComposite, null, EImage.REFRESH_ICON);

		GridLayout layout = new GridLayout(wsdlUrlcompositeColumn, false);
		wsdlComposite.setLayout(layout);

		// add port name UI
		portListTableView = createTableView(wsdlComposite, "WebServiceUI.Port", presenter.getPortTableModel(),
				new ReadOnlyBeanStringPropertyAccessors<String>() {
					public String get(String bean) {
						return bean;
					}
				});

		// WSDL Operation
		if (presenter.isFunctionRequired()) {
			functionListTableView = createTableView(wsdlComposite, "WebServiceUI.Operation",
					presenter.getFunctionTableModel(), new ReadOnlyBeanStringPropertyAccessors<Function>() {
						public String get(Function bean) {
							return bean.getName();
						}
					});
		}

		addListenerForWSDLCom();

		if (presenter.allowPopulateSchema()) {
			populateCheckbox = new Button(wsdlComposite, SWT.CHECK);
			populateCheckbox.setText("Populate schema to repository on finish");
			populateCheckbox.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false, wsdlUrlcompositeColumn, 1));
		}

		setControl(wsdlComposite);
		setPageComplete(false);
	}

	private Button createPushButton(Composite parent, String messageKey, IImage icon) {
		Button button = new Button(parent, SWT.PUSH | SWT.CENTER);
		if (messageKey != null) {
			button.setText(Messages.getString(messageKey));
		}
		if (icon != null) {
			button.setImage(ImageProvider.getImage(icon));
		}
		return button;
	}

	private void createWsdlFieldControl(Composite wsdlComposite) {
		wsdlField = new LabelledFileField(wsdlComposite, "WSDL:",
            new String[] { "*.wsdl", "*.*" }, 1, SWT.BORDER) {

            protected void setFileFieldValue(String result) {
                if (result != null) {
                    getTextControl().setText(TalendTextUtils.addQuotes(PathUtils.getPortablePath(result)));
                    presenter.refreshPageByWsdl(wsdlField.getText());
                }
            }
        };
        // add a listener for ctrl+space.
        TalendProposalUtils.installOn(wsdlField.getTextControl(), webServiceNode.getProcess(), webServiceNode);

        wsdlField.getTextControl().addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent event) {
                switch (event.keyCode) {
                case SWT.CR:
                case SWT.KEYPAD_CR:
                    presenter.refreshPageByWsdl(wsdlField.getText());
                }
            }
        });
        
        String initialWsdlLocation = presenter.getInitialWsdlLocation();
        if(initialWsdlLocation!=null) {
        	wsdlField.setText(initialWsdlLocation);
        }
	}

	/**
	 * Creates the table view with giving i18n titleKey, paramList, and accessor
	 * to String. The parent Composite need to be grid layout.
	 */
	private <T> DataTableEditorView<T> createTableView(Composite parent, String titleKey, ExtendedTableModel<T> model,
			IBeanPropertyAccessors<T, String> accessor) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.getString(titleKey));
		label.setLayoutData(new GridData(SWT.NONE, SWT.TOP, false, false));

		DataTableEditorView<T> view = new DataTableEditorView<T>(parent, model, accessor);

		int horizontalSpan = ((GridLayout) parent.getLayout()).numColumns - 1;
		view.getMainComposite().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, horizontalSpan, 1));
		return view;
	}

	public void runWithProgress(IRunnableWithProgress runnableWithProgress) throws InvocationTargetException,
			InterruptedException {
		getContainer().run(true, false, runnableWithProgress);
	}

	private void addListenerForWSDLCom() {

		if (servicebut != null) {
			servicebut.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					RepositoryReviewDialog dialog = new RepositoryReviewDialog(getShell(),
							ERepositoryObjectType.METADATA, "SERVICES:OPERATION") {
						@Override
						protected boolean isSelectionValid(SelectionChangedEvent event) {
							IStructuredSelection selection = (IStructuredSelection) event.getSelection();
							if (selection.size() == 1) {
								return true;
							}
							return false;
						}
					};
					int open = dialog.open();
					if (open == Dialog.OK) {
						RepositoryNode result = dialog.getResult();
						Item item = result.getObject().getProperty().getItem();
						IESBService service = (IESBService) GlobalServiceRegister.getDefault().getService(
								IESBService.class);
						String wsdlFilePath = service.getWsdlFilePath(item);
						if (wsdlFilePath != null) {
							wsdlField.getTextControl().setText(
									TalendTextUtils.addQuotes(PathUtils.getPortablePath(wsdlFilePath)));
							presenter.refreshPageByWsdl(wsdlField.getText());
						}
					}
				}
			});
		}

		refreshbut.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				presenter.refreshPageByWsdl(wsdlField.getText());
			}

		});

		if(functionListTableView != null) {
			functionListTableView.getTable().addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Function currentFunction = getSelectedFunction();
					if (currentFunction != null) {
						setPageComplete(true);
					}
				}
			});
		}

		portListTableView.getTable().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem[] item = portListTableView.getTable().getSelection();
				String currentPortName = (String) item[0].getData();
				presenter.portSelected(currentPortName);
			}
		});
	}

	public void selectFirstFunction() {
		if(functionListTableView == null) {
			//no need to select a funciton.
			setPageComplete(true);
		}else if (functionListTableView.getTable().getItemCount() > 0) {
			functionListTableView.getTable().setSelection(0);
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

	/**
	 * true to indicate the finish request was accepted, and false to indicate
	 * that the finish request was refused
	 */
	public boolean performFinish() {
		
		
		IStatus status = presenter.performFinishWithFunction(getSelectedFunction());
		if (!status.isOK()) {
			setErrorMessage(status.getMessage());
		}
		return status.isOK();
	}

	boolean needPopulateSchema() {
		return populateCheckbox != null && populateCheckbox.getSelection();
	}

	public void setWsdlLocation(String initialWsdlLocation) {
		if (initialWsdlLocation != null) {
			wsdlField.setText(initialWsdlLocation);
		}
	}

	private Function getSelectedFunction() {
		if(functionListTableView == null) {
			return null;
		}
		TableItem[] item = functionListTableView.getTable().getSelection();
		Function currentFunction = (Function) item[0].getData();
		return currentFunction;
	}

}
