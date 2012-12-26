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
package org.talend.designer.esb.webservice.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.EImage;
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
import org.talend.core.model.metadata.builder.connection.WSDLSchemaConnection;
import org.talend.core.model.process.IContext;
import org.talend.core.model.process.IContextManager;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.utils.ContextParameterUtils;
import org.talend.core.model.utils.TalendTextUtils;
import org.talend.core.prefs.ITalendCorePrefConstants;
import org.talend.core.ui.AbstractWebService;
import org.talend.core.ui.proposal.TalendProposalUtils;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;
import org.talend.designer.esb.webservice.WebServiceComponent;
import org.talend.designer.esb.webservice.WebServiceComponentMain;
import org.talend.designer.esb.webservice.WebServiceComponentPlugin;
import org.talend.designer.esb.webservice.data.ExternalWebServiceUIProperties;
import org.talend.designer.esb.webservice.i18n.Messages;
import org.talend.designer.esb.webservice.managers.WebServiceManager;
import org.talend.designer.esb.webservice.ws.WSDLDiscoveryHelper;
import org.talend.designer.esb.webservice.ws.wsdlinfo.Function;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.dialog.RepositoryReviewDialog;
import org.talend.repository.ui.utils.ConnectionContextHelper;
import org.xml.sax.InputSource;

/**
 * gcui class global comment. Detailled comment
 */
@SuppressWarnings("unchecked")
public class WebServiceUI extends AbstractWebService {

    private static final String METHOD = "METHOD";
    private static final String TRUE = "true";
    private static final String NEED_SSL_TO_TRUSTSERVER = "NEED_SSL_TO_TRUSTSERVER";
    private static final String PORT_NAME = "PORT_NAME";
    private static final String ENDPOINT = "ENDPOINT";

    private static final String ERROR_GETTING_WSDL = "Error getting service description";

    protected int maximumRowsToPreview = CorePlugin.getDefault().getPreferenceStore()
            .getInt(ITalendCorePrefConstants.PREVIEW_LIMIT);

    private WebServiceManager webServiceManager;

    private Composite uiParent;

    private LabelledFileField wsdlField;

    private Label operationLabel;

    private Label portNameLabel;

    private CTabFolder tabFolder;

    private CTabItem wsdlTabItem;

    private Composite wsdlComposite;

    private SashForm allContentForm;

    private AbstractDataTableEditorView<Function> listTableView;

    private AbstractDataTableEditorView<String> portListTableView;

    private Button refreshbut;

    private Button servicebut;

    private Table listTable;

    private Table portListTable;

    private WebServiceComponent connector;

    private static int DEFAULT_INDEX = 0;

    private int selectedColumnIndex = DEFAULT_INDEX;

    private List<Function> functionList = new ArrayList<Function>();

    private List<String> allPortNames = new ArrayList<String>();

    private String URLValue;

    private Function currentFunction;

    private String currentPortName;

    private WSDLSchemaConnection connection = null;

    private List<Function> allFunctions = new ArrayList<Function>();

    private Set<String> portNameList = new HashSet<String>();

    private Button wizardOkButton;

    private String parseUrl = "";

    private Button populateCheckbox;

    private boolean gotNewData = false;
    private Definition def;

    public WebServiceUI(Composite uiParent, WebServiceComponentMain webServiceMain) {
        super();
        this.uiParent = uiParent;
        this.webServiceManager = webServiceMain.getWebServiceManager();
        this.connector = webServiceMain.getWebServiceComponent();
        URLValue = new String();
        initWebserviceUI();
    }

    public WebServiceUI(Composite uiParent, WebServiceComponentMain webServiceMain, ConnectionItem connectionItem) {
        super();
        this.uiParent = uiParent;
        this.webServiceManager = webServiceMain.getWebServiceManager();
        this.connector = webServiceMain.getWebServiceComponent();
        this.connection = (WSDLSchemaConnection) connectionItem.getConnection();
        URLValue = new String();
        initWebserviceUI();
    }

    private void initWebserviceUI() {
        IElementParameter METHODPara = connector.getElementParameter(METHOD); //$NON-NLS-1$
        Object obj = METHODPara.getValue();
        if (obj == null) {
            return;
        }
        if (obj != null && obj instanceof String && !"".equals(obj)) {
            String currentPort = (String) connector.getElementParameter(PORT_NAME).getValue(); //$NON-NLS-1$

            allPortNames.clear();
            allPortNames.add(currentPort);

            currentFunction = new Function(obj.toString(), currentPort);
            functionList.clear();
            functionList.add(currentFunction);
            allFunctions.add(currentFunction);
        }
    }

    public final void openErrorDialog(String message, Throwable e) {
        final IStatus status = WebServiceComponentPlugin.getStatus(message, e);
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                ErrorDialog.openError(uiParent.getShell(), Messages.getString("Error"), null, status);
            }
        });
    }

    /**
     * DOC gcui Comment method "useSSL".
     *
     * @return
     */
    private void useSSL() {
        String trustStoreFile = "";
        String trustStorePassword = "";
        IElementParameter trustserverFileParameter = webServiceManager.getWebServiceComponent().getElementParameter(
                "SSL_TRUSTSERVER_TRUSTSTORE");
        IElementParameter trustserverPasswordParameter = webServiceManager.getWebServiceComponent().getElementParameter(
                "SSL_TRUSTSERVER_PASSWORD");
        if (trustserverFileParameter.getValue() != null) {
            trustStoreFile = trustserverFileParameter.getValue().toString();
            trustStoreFile = TalendTextUtils.removeQuotes(trustStoreFile);
        }
        if (trustserverPasswordParameter.getValue() != null) {
            trustStorePassword = trustserverPasswordParameter.getValue().toString();
            trustStorePassword = TalendTextUtils.removeQuotes(trustStorePassword);
        }

        // System.clearProperty("javax.net.ssl.trustStore");
        System.setProperty("javax.net.ssl.trustStore", trustStoreFile);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }

    /**
     * Gets WSDL as ZLIB-compressed and Base64-encoded String.
     *
     * @return WSDL as String object. Or null in case errors/not possible to create object.
     */
    public String getWSDL() {
        ByteArrayOutputStream wsdlOs = new ByteArrayOutputStream();
        OutputStream os = compressAndEncode(wsdlOs);
        try {
            WSDLFactory.newInstance().newWSDLWriter().writeWSDL(def, os);
            os.close();
            return new String(wsdlOs.toByteArray());
        } catch (Exception e) {
            WebServiceComponentPlugin.getDefault().getLog().log(
                    WebServiceComponentPlugin.getStatus("Unable to create wsdl content...", e));
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return null;
    }

    public Definition getWSDL(String compressedAndEncodedWsdl) {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedAndEncodedWsdl.getBytes());
        InputStream wsdlIS = decodeAndUncompress(bais);
        try {
            return WSDLFactory.newInstance().newWSDLReader().readWSDL(null, new InputSource(wsdlIS));
        } catch (Exception e) {
            WebServiceComponentPlugin.getDefault().getLog().log(
                    WebServiceComponentPlugin.getStatus("Unable to read wsdl content...", e));
        } finally {
            if (null != wsdlIS) {
                try {
                    wsdlIS.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return null;
    }

    public static OutputStream compressAndEncode(OutputStream os) {
        return new DeflaterOutputStream(new Base64OutputStream(os));
    }

    public static InputStream decodeAndUncompress(InputStream is) {
        return new InflaterInputStream(new Base64InputStream(is));
    }

    public void init() {
        uiParent.setLayout(new GridLayout());

        Composite composite = new Composite(uiParent, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setLayout(new FormLayout());

        allContentForm = new SashForm(composite, SWT.NONE);
        FormData formData = new FormData();
        formData.top = new FormAttachment(5, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        formData.bottom = new FormAttachment(100, -5);
        allContentForm.setLayoutData(formData);
        createViewers(allContentForm);
    }

    private void setOk(boolean enabled) {
        if (null != wizardOkButton) {
            wizardOkButton.setEnabled(enabled && gotNewData);
        }
    }

    protected WebServiceComponent getWebServiceComponent() {
        return getWebServiceManager().getWebServiceComponent();
    }

    protected WebServiceManager getWebServiceManager() {
        return this.webServiceManager;
    }

    private void createViewers(SashForm allContentForm) {
        createHeader(allContentForm);
    }

    private void createHeader(SashForm allContentForm) {
        //
        tabFolder = new CTabFolder(allContentForm, SWT.NONE);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

        wsdlTabItem = new CTabItem(tabFolder, SWT.NONE);
        wsdlTabItem.setText(ExternalWebServiceUIProperties.WSDL_LABEL);
        tabFolder.setSelection(wsdlTabItem);
        tabFolder.setSimple(false);
        wsdlTabItem.setControl(createWSDLStatus());
    }

    @SuppressWarnings("rawtypes")
    private class DataTableEditorView<T> extends AbstractDataTableEditorView<T>{

        private IBeanPropertyAccessors accessors;
        private TableViewerCreatorColumn rowColumn;

        public DataTableEditorView(Composite parent, int style, ExtendedTableModel<T> model, boolean b, boolean c, boolean d, IBeanPropertyAccessors accessors) {
            super(parent, style, model, false, true, false, false);
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
            rowColumn = new TableViewerCreatorColumn(tableViewerCreator);
            rowColumn.setTitle(Messages.getString("WebServiceUI.COLUMN")); //$NON-NLS-1$
            rowColumn.setBeanPropertyAccessors(accessors);
            rowColumn.setWeight(60);
            rowColumn.setModifiable(true);
            rowColumn.setMinimumWidth(60);
            rowColumn.setCellEditor(new TextCellEditor(tableViewerCreator.getTable()));
        }
    };


    private Composite createWSDLStatus() {
        wsdlComposite = new Composite(tabFolder, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 20;
        layout.marginHeight = 20;
        wsdlComposite.setLayout(layout);
        wsdlComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        // WSDL URL
        int wsdlUrlcompositeColumn = 4;
        if (WebServiceComponentPlugin.hasRepositoryServices()) {
            wsdlUrlcompositeColumn = 5;
        }
        Composite wsdlUrlcomposite = new Composite(wsdlComposite, SWT.NONE);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.verticalIndent = 2;
        layoutData.verticalSpan = 1;
        wsdlUrlcomposite.setLayoutData(layoutData);
        layout = new GridLayout(wsdlUrlcompositeColumn, false);
        wsdlUrlcomposite.setLayout(layout);

        wsdlField = new LabelledFileField(wsdlUrlcomposite, ExternalWebServiceUIProperties.FILE_LABEL,
                ExternalWebServiceUIProperties.FILE_EXTENSIONS, 1, SWT.BORDER) {

            protected void setFileFieldValue(String result) {
                if (result != null) {
                    getTextControl().setText(TalendTextUtils.addQuotes(PathUtils.getPortablePath(result)));
                    getDataFromNet();
                    if (portListTable.getItemCount() > 1) {
                        portListTable.deselectAll();
                        setOk(false);
                    }
                    if (listTable.getItemCount() == 1) {
                        selectFirstFunction();
                    }
                }
            }

        };
        wsdlField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                URLValue = wsdlField.getText();
                if (connection != null)
                    connection.setWSDL(URLValue);
            }
        });
        // add a listener for ctrl+space.
        TalendProposalUtils.installOn(wsdlField.getTextControl(), connector.getProcess(), connector);
        String wsdlUrl = (String) connector.getElementParameter(ENDPOINT).getValue(); //$NON-NLS-1$
        if (wsdlUrl != null) {
            wsdlField.setText(wsdlUrl);
        }

        // TESB-3590，gliu
        if (WebServiceComponentPlugin.hasRepositoryServices()) {
            servicebut = new Button(wsdlUrlcomposite, SWT.PUSH | SWT.CENTER);
            servicebut.setText(Messages.getString("WebServiceUI.Services"));
        }

        refreshbut = new Button(wsdlUrlcomposite, SWT.PUSH | SWT.CENTER);
        refreshbut.setImage(ImageProvider.getImage(EImage.REFRESH_ICON));
        GridData butData = new GridData();
        butData.verticalSpan = 1;
        refreshbut.setLayoutData(butData);

        // add port name UI
        Composite wsdlPortOperationComposite = new Composite(wsdlComposite, SWT.NONE);
        GridData portlayoutData = new GridData(GridData.FILL_HORIZONTAL);
        wsdlPortOperationComposite.setLayoutData(portlayoutData);
        layout = new GridLayout(2, false);
        wsdlPortOperationComposite.setLayout(layout);

        portNameLabel = new Label(wsdlPortOperationComposite, SWT.NONE);
        portNameLabel.setText(Messages.getString("WebServiceUI.Port")); //$NON-NLS-1$
        portNameLabel.setLayoutData(new GridData(SWT.NONE, SWT.TOP, false, false));

        ExtendedTableModel<String> portModel = new ExtendedTableModel<String>("PORTNAMELIST", allPortNames); //$NON-NLS-1$
        portListTableView = new DataTableEditorView<String>(
                wsdlPortOperationComposite,
                SWT.NONE, portModel, false, true, false,
                new IBeanPropertyAccessors<String, String>() {
                    public String get(String bean) {
                        return bean;
                    }

                    public void set(String bean, String value) {
                      //readonly
                    }
                }
        );

        // WSDL Operation
        operationLabel = new Label(wsdlPortOperationComposite, SWT.NONE);
        operationLabel.setText(Messages.getString("WebServiceUI.Operation")); //$NON-NLS-1$
        operationLabel.setLayoutData(new GridData(SWT.NONE, SWT.TOP, false, false));

        ExtendedTableModel<Function> funModel = new ExtendedTableModel<Function>("FUNCTIONLIST", functionList); //$NON-NLS-1$
        listTableView = new DataTableEditorView<Function>(
                wsdlPortOperationComposite,
                SWT.NONE, funModel, false, true, false,
                new IBeanPropertyAccessors<Function, String>() {
                    public String get(Function bean) {
                        return bean.getName();
                    }

                    public void set(Function bean, String value) {
                        //readonly
                    }
                }
        );

        addListenerForWSDLCom();

        if (WebServiceComponentPlugin.hasRepositoryServices()) {
            populateCheckbox = new Button(wsdlComposite, SWT.CHECK | SWT.CENTER);
            populateCheckbox.setLayoutData(new GridData());
            populateCheckbox.setText("Populate schema to repository on finish");
        }

        return wsdlComposite;
    }

    private void refresh() {
        final Job job = new Job("t") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                // monitor.setCanceled(true);
                monitor.beginTask("Retrieve WSDL parameter from net.", IProgressMonitor.UNKNOWN);
                getDataFromNet();
                monitor.done();
                return Status.OK_STATUS;
            }
        };
        job.setSystem(true);
        job.schedule();
        ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getDisplay()
                .getActiveShell().getShell());
        IRunnableWithProgress runnable = new IRunnableWithProgress() {

            public void run(final IProgressMonitor monitor) {
                monitor.beginTask("Retrieve WSDL parameter from net.", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
                boolean f = true;
                while (f) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (monitor.isCanceled()) {
                        job.done(Status.OK_STATUS);
                        job.cancel();
                    }
                    if (job.getResult() != null && job.getResult().isOK()) {
                        monitor.done();
                        f = false;
                    }
                }
            }
        };

        try {
            progressDialog.run(true, true, runnable);
        } catch (InvocationTargetException e1) {
            ExceptionHandler.process(e1);
        } catch (InterruptedException e1) {
            ExceptionHandler.process(e1);
        } catch (WebServiceCancelException e1) {
            return;
        }
        if (portListTable.getItemCount() > 1) {
            portListTable.deselectAll();
            setOk(false);
        }
        if (listTable.getItemCount() == 1) {
            selectFirstFunction();
        }
    }



    private void addListenerForWSDLCom() {
        wsdlField.getTextControl().addKeyListener(new KeyListener(){
            public void keyPressed(KeyEvent event) {
                switch (event.keyCode) {
                case 13:
                case SWT.KEYPAD_CR:
                    refresh();
                }

            }

            public void keyReleased(KeyEvent event) {
            }
        });

        if (servicebut != null) {
            servicebut.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    // TODO
                    RepositoryReviewDialog dialog = new RepositoryReviewDialog(
                            Display.getCurrent().getActiveShell(),
                            ERepositoryObjectType.METADATA,
                            "SERVICES:OPERATION") {
                        @Override
                        protected boolean isSelectionValid(
                                SelectionChangedEvent event) {
                            IStructuredSelection selection = (IStructuredSelection) event
                                    .getSelection();
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
                        if (GlobalServiceRegister.getDefault()
                                .isServiceRegistered(IESBService.class)) {
                            IESBService service = (IESBService) GlobalServiceRegister
                                    .getDefault().getService(IESBService.class);
                            String wsdlFilePath = service.getWsdlFilePath(item);
                            if (wsdlFilePath != null) {
                                wsdlField
                                        .getTextControl()
                                        .setText(
                                                TalendTextUtils.addQuotes(PathUtils
                                                        .getPortablePath(wsdlFilePath)));
                                getDataFromNet();
                                if (portListTable.getItemCount() > 1) {
                                    portListTable.deselectAll();
                                    setOk(false);
                                }
                                if (listTable.getItemCount() == 1) {
                                    selectFirstFunction();
                                }
                            }
                        }

                    }
                }

            });
        }

        refreshbut.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                refresh();
            }

        });
        listTable = listTableView.getTable();
        portListTable = portListTableView.getTable();

        listTable.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                TableItem[] item = listTable.getSelection();
                currentFunction = (Function) item[0].getData();
                if (currentFunction != null) {
                    setOk(true);
                }
            }
        });

        portListTable.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                TableItem[] item = portListTable.getSelection();
                currentPortName = (String) item[0].getData();
                if (connection != null) {
                    if (!updateConnection()) {
                        connection.setPortName("");
                    }
                }
                List<Function> portFunctions = new ArrayList<Function>();
                for (Function function : allFunctions) {
                    if (currentPortName.equals(function.getPortName())) {
                        portFunctions.add(function);
                    }
                }
                ExtendedTableModel<Function> listModel = listTableView.getExtendedTableModel();
                listModel.removeAll();
                listModel.addAll(portFunctions);
                selectFirstFunction();
            }
        });
    }

    private void selectFirstFunction() {
        if (listTable.getItemCount() > 0) {
            listTable.setSelection(new int[]{0});
            currentFunction = (Function) listTable.getItem(0).getData();
            setOk(true);
        } else {
            setOk(false);
        }
    }

    private void getDataFromNet() {
        allFunctions.clear();
        portNameList.clear();
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                URLValue = wsdlField.getText();
            }
        });

        if (URLValue == null) {
            URLValue = ""; //$NON-NLS-1$
        }
        allFunctions = getFunctionsList(URLValue);
        gotNewData = true;
        for (Function function : allFunctions) {
            if ((function != null) && (function.getPortName() != null)) {
                portNameList.add(function.getPortName());
            }
        }
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                ExtendedTableModel<String> portListModel = portListTableView.getExtendedTableModel();
                portListModel.removeAll();
                //NO addAll(Collection) for ExtendedTableModel??? grrr
                for (String portName : portNameList) {
                    portListModel.add(portName);
                }
                //clear functions
                ExtendedTableModel<Function> listModel = listTableView.getExtendedTableModel();
                listModel.removeAll();
                if (portNameList.size() == 1) { //only one porttype
                    listModel.addAll(allFunctions);
                }
            }

        });

    }

    private List<Function> getFunctionsList(String wsdlUrl) {
        List<Function> funList = new ArrayList<Function>();
        WSDLDiscoveryHelper ws = new WSDLDiscoveryHelper();
        WebServiceComponent webServiceComponent = webServiceManager.getWebServiceComponent();
        IElementParameter parameter = webServiceComponent.getElementParameter(NEED_SSL_TO_TRUSTSERVER);
        boolean isUseSSL = (parameter != null) && TRUE
                .equals(parameter.getValue().toString());

        if (isUseSSL) {
            useSSL();
        }

        try {
            if (wsdlUrl != null && !wsdlUrl.contains("\"")) {
                funList = ws.getFunctionsAvailable(parseContextParameter(wsdlUrl));
            } else {
                funList = ws.getFunctionsAvailable(wsdlUrl);
            }
            def = ws.getDefinition();
        } catch (Exception e) {
            openErrorDialog(ERROR_GETTING_WSDL, e);
        }
        return funList;
    }

    private String parseContextParameter(final String contextValue) {
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                String url = "";
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                IContextManager contextManager = null;
                if (connector.getProcess() == null) {
                    // contextManager = contextModeManager.getSelectedContextType().getContextParameter(); //
                    // connection.get
                    // IContextManager contextManager
                    ContextType contextType = ConnectionContextHelper.getContextTypeForContextMode(connection);
                    url = ConnectionContextHelper.getOriginalValue(contextType, contextValue);
                } else {
                    contextManager = connector.getProcess().getContextManager();
                    String currentDefaultName = contextManager.getDefaultContext().getName();
                    List<IContext> contextList = contextManager.getListContext();
                    if ((contextList != null) && (contextList.size() > 1)) {
                        currentDefaultName = ConnectionContextHelper.getContextTypeForJob(shell, contextManager, false);
                    }
                    // ContextSetsSelectionDialog cssd=new ContextSetsSelectionDialog(shell,,false);
                    // ContextType contextType=ConnectionContextHelper.getContextTypeForContextMode(connector);
                    IContext context = contextManager.getContext(currentDefaultName);
                    url = ContextParameterUtils.parseScriptContextCode(contextValue, context);

                }
                parseUrl = url;
            }

        });

        return parseUrl;
    }

    public CTabFolder getTabFolder() {
        return this.tabFolder;
    }

    public Composite getWsdlComposite() {
        return this.wsdlComposite;
    }

    // bug 14067
    public String getURL() {
        return URLValue;
    }

    public Function getCurrentFunction() {
        return currentFunction;
    }

    public String getCurrentPortName() {
        return currentPortName;
    }

    public List<String> getAllPortNames() {
        return this.allPortNames;
    }

    public int getSelectedColumnIndex() {
        return this.selectedColumnIndex;
    }

    public void saveProperties() {
        getWebServiceManager().savePropertiesToComponent();
        populateSchema();
    }

    public void prepareClosing(int dialogResponse) {

    }

    public Table getTable() {
        return listTable;
    }

    public LabelledFileField getWSDLLabel(Boolean b) {
        refreshbut.setEnabled(!b);
        return wsdlField;
    }

    public void saveInputValue() {
        if (connection != null) {
            if (currentFunction != null) {
                if (currentFunction.getName() != null) {
                    connection.setMethodName(currentFunction.getName());
                }
                if (currentFunction.getServerNameSpace() != null) {
                    connection.setServerNameSpace(currentFunction.getServerNameSpace());
                }
                if (currentFunction.getServerName() != null) {
                    connection.setServerName(currentFunction.getServerName());
                }
                if (currentFunction.getServerNameSpace() != null) {
                    connection.setPortNameSpace(currentFunction.getServerNameSpace());
                }
            }
            updateConnection();

        }
    }

    private void populateSchema() {
        if (currentFunction == null || populateCheckbox == null
                || !populateCheckbox.getSelection()) {
            return;
        }
        try {
            Class<?> forName = Class
                    .forName("org.talend.repository.services.action.PublishMetadataAction");
            Object newInstance = forName.newInstance();
            forName.getMethod("process", Definition.class, Map.class).invoke(
                    newInstance, def, Collections.emptyMap());
        } catch (Exception e) {
            WebServiceComponentPlugin.getDefault().getLog().log(
                    WebServiceComponentPlugin.getStatus(null, e));
        }
    }

    private boolean updateConnection() {
        if (currentPortName != null) {
            connection.setPortName(currentPortName);
        } else if (currentPortName == null && !allPortNames.isEmpty()) {
            currentPortName = allPortNames.get(0);
            connection.setPortName(currentPortName);
        } else {
            return false;
        }
        return true;
    }

    /**
     * @param okButton the wizardOkButton to set
     */
    public void setWizardOkButton(Button okButton) {
        this.wizardOkButton = okButton;
        setOk(false);
    }

}
