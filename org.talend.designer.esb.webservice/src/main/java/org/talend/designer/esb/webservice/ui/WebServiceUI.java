// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
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
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.WSDLParameter;
import org.talend.core.model.metadata.builder.connection.WSDLSchemaConnection;
import org.talend.core.model.process.IContext;
import org.talend.core.model.process.IContextManager;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.properties.ConnectionItem;
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
import org.talend.designer.esb.webservice.ws.wsdlinfo.ParameterInfo;
import org.talend.designer.esb.webservice.ws.wsdlinfo.PortNames;
import org.talend.repository.ui.utils.ConnectionContextHelper;
//import org.talend.ws.helper.conf.ServiceHelperConfiguration;

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

    private Composite outputComposite;

    private Composite inputComposite;

    private Composite wsdlComposite;

    private SashForm allContentForm;

    private AbstractDataTableEditorView<Function> listTableView;

    private AbstractDataTableEditorView<PortNames> portListTableView;

    private Button refreshbut;

    private Table listTable;

    private Table portListTable;

    private WebServiceComponent connector;

    private static int DEFAULT_INDEX = 0;

    private int selectedColumnIndex = DEFAULT_INDEX;

    private List<Function> allfunList = new ArrayList<Function>();

    private List<PortNames> allPortNames = new ArrayList<PortNames>();

    private String URLValue;

    private Function currentFunction;

    private PortNames currentPortName;

//    private ServiceHelperConfiguration serverConfig = null;

    private Boolean isFirst = true;

    private WSDLSchemaConnection connection = null;

    private List<Function> funList = new ArrayList<Function>();

    private List<PortNames> portNameList = new ArrayList<PortNames>();

    private String url = "";

    public WebServiceUI(Composite uiParent, WebServiceComponentMain webServiceMain) {
        super();
        this.uiParent = uiParent;
        this.webServiceManager = webServiceMain.getWebServiceManager();
        this.connector = webServiceMain.getWebServiceComponent();
        URLValue = new String();
        //getLastFunction();
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
            String currentURL = (String) connector.getElementParameter(PORT_NAME).getValue(); //$NON-NLS-1$

            PortNames retrivePortName = new PortNames();
            retrivePortName.setPortName(currentURL);
            allPortNames.clear();
            allPortNames.add(retrivePortName);
            retrivePortName.setPortName(currentURL);

            Function fun = new Function(obj.toString());
            allfunList.clear();
            allfunList.add(fun);
            if (fun != null) {
                currentFunction = fun;
            }
            initwebServiceMappingData(currentURL);
        }
    }

    private void initwebServiceMappingData(String currentURL) {
        if (currentURL != null && !currentURL.equals("")) {
            isFirst = false;
            Function fun = new Function(currentURL);
            IElementParameter METHODPara = this.connector.getElementParameter(METHOD);
            Object obj = METHODPara.getValue();
            if (obj == null) {
                return;
            }
            if (obj instanceof String) {
                String str = (String) obj;
                fun.setName(str);
            }
            currentFunction = fun;
        }
    }

    public void initWebserviceData() {
        ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell()
                .getShell());
        IRunnableWithProgress runnable = new IRunnableWithProgress() {

            public void run(final IProgressMonitor monitor) {
                monitor.beginTask("Retrieve WSDL parameter from net,please wait....", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
                Display.getDefault().syncExec(new Runnable() {

                    public void run() {
                        getLastFunction();
                        isFirst = false;
                    }
                });

                monitor.done();

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

    }

    private static IStatus[] getStatus(final Throwable e, final String pluginId) {
        List<IStatus> alStatus = new ArrayList<IStatus>();
        alStatus.add(new Status(IStatus.ERROR, pluginId, 0, e.getClass().getName(), e));
        for (int i = 0; i < e.getStackTrace().length; i++) {
            alStatus.add(new Status(IStatus.ERROR, pluginId, 0, e.getStackTrace()[i].toString(), null));
        }
        return alStatus.toArray(new IStatus[alStatus.size()]);
    }

    public final void openErrorDialog(String message, Throwable e) {
        String msg = (message != null) ? message : ((e.getMessage() != null) ? e.getMessage() : e.getClass().getName()); //$NON-NLS-1$
        String pluginId = WebServiceComponentPlugin.PLUGIN_ID;
        final IStatus status = new MultiStatus(pluginId, 0, getStatus(e, pluginId), msg, null);
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                ErrorDialog.openError(uiParent.getShell(), Messages.getString("Error"), null, status);
            }
        });
    }

    private void getLastFunction() {
        IElementParameter METHODPara = connector.getElementParameter(METHOD); //$NON-NLS-1$
        Object obj = METHODPara.getValue();
        if (obj == null) {
            return;
        }
        if (obj instanceof String) {
            String str = (String) obj;
            String wsdlUrl = (String) connector.getElementParameter(ENDPOINT).getValue(); //$NON-NLS-1$
            String currentURL = (String) connector.getElementParameter(PORT_NAME).getValue(); //$NON-NLS-1$
            List<Function> funList = getFunctionsList(wsdlUrl);

            PortNames retrivePortName = new PortNames();
            retrivePortName.setPortName(currentURL);
            allPortNames.clear();
            allPortNames.add(retrivePortName);

            for (Function fun : funList) {
                if (fun.getName().equals(str)) {
                    allfunList.clear();
                    allfunList.add(fun);
                    if (fun != null) {
                        currentFunction = fun;
                    }
                    return;
                }
            }

        }

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
        } catch (IOException e) {
            openErrorDialog(ERROR_GETTING_WSDL, e);
        }
        return funList;
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
        Composite wsdlUrlcomposite = new Composite(wsdlComposite, SWT.NONE);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.verticalIndent = 2;
        layoutData.verticalSpan = 1;
        wsdlUrlcomposite.setLayoutData(layoutData);
        layout = new GridLayout(4, false);
        wsdlUrlcomposite.setLayout(layout);

        wsdlField = new LabelledFileField(wsdlUrlcomposite, ExternalWebServiceUIProperties.FILE_LABEL,
                ExternalWebServiceUIProperties.FILE_EXTENSIONS, 1, SWT.BORDER) {

            protected void setFileFieldValue(String result) {
                if (result != null) {
                    getTextControl().setText(TalendTextUtils.addQuotes(PathUtils.getPortablePath(result)));
                    getDataFromNet();
                    isFirst = false;
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

        refreshbut = new Button(wsdlUrlcomposite, SWT.PUSH | SWT.CENTER);
        refreshbut.setImage(ImageProvider.getImage(EImage.REFRESH_ICON));
        GridData butData = new GridData();
        butData.verticalSpan = 1;
        refreshbut.setLayoutData(butData);

//        if (wsdlUrl != null && !wsdlUrl.contains("\"")) {
//            wsdlField.setReadOnly(true);
//            refreshbut.setEnabled(false);
//        }
        // add port name UI
        Composite wsdlPortOperationComposite = new Composite(wsdlComposite, SWT.NONE);
        GridData portlayoutData = new GridData(GridData.FILL_HORIZONTAL);
        wsdlPortOperationComposite.setLayoutData(portlayoutData);
        layout = new GridLayout(2, false);
        wsdlPortOperationComposite.setLayout(layout);

        portNameLabel = new Label(wsdlPortOperationComposite, SWT.NONE);
        portNameLabel.setText(Messages.getString("WebServiceUI.Port")); //$NON-NLS-1$
        portNameLabel.setLayoutData(new GridData(SWT.NONE, SWT.TOP, false, false));

        ExtendedTableModel<PortNames> portModel = new ExtendedTableModel<PortNames>("PORTNAMELIST", allPortNames); //$NON-NLS-1$
        portListTableView = new DataTableEditorView<PortNames>(
                wsdlPortOperationComposite, 
                SWT.NONE, portModel, false, true, false,
                new IBeanPropertyAccessors<PortNames, String>() {
                    public String get(PortNames bean) {
                        return bean.getPortName();
                    }

                    public void set(PortNames bean, String value) {
                        bean.setPortName(value);
                    }
                }
        );

        // WSDL Operation
        operationLabel = new Label(wsdlPortOperationComposite, SWT.NONE);
        operationLabel.setText(Messages.getString("WebServiceUI.Operation")); //$NON-NLS-1$
        operationLabel.setLayoutData(new GridData(SWT.NONE, SWT.TOP, false, false));

        ExtendedTableModel<Function> funModel = new ExtendedTableModel<Function>("FUNCTIONLIST", allfunList); //$NON-NLS-1$
        listTableView = new DataTableEditorView<Function>(
                wsdlPortOperationComposite, 
                SWT.NONE, funModel, false, true, false,
                new IBeanPropertyAccessors<Function, String>() {
                    public String get(Function bean) {
                        return bean.getName();
                    }

                    public void set(Function bean, String value) {
                        bean.setName(value);

                    }
                }
        ); 
        
        addListenerForWSDLCom();
        return wsdlComposite;
    }

    private void addListenerForWSDLCom() {
        refreshbut.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
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
                                Thread.sleep(1000);
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

                        // monitor.done();
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

                if (connection != null) {
                    if (listTable.getItemCount() > 0) {
                        listTable.setSelection(listTable.getItem(0));
                    }
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
                    if (currentPortName != null) {
                        connection.setPortName(currentPortName.getPortName());

                    } else if (currentPortName == null && !allPortNames.isEmpty()) {
                        currentPortName = allPortNames.get(0);
                        connection.setPortName(currentPortName.getPortName());
                    }
                }
                if (listTable.getItemCount() == 1) {
                    listTable.setSelection(0);
                    currentFunction = (Function) listTable.getItem(0).getData();
                }
                isFirst = false;
            }
        });
        // TableItem firstItem = listTable.getItem(0);
        // currentFunction = firstItem.getData();
        listTable = listTableView.getTable();
        portListTable = portListTableView.getTable();

        listTable.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                TableItem[] item = listTable.getSelection();
                currentFunction = (Function) item[0].getData();

                // if select the same as before ,don't change it
                // IElementParameter METHODPara = connector.getElementParameter("METHOD"); //$NON-NLS-1$
                // Object obj = METHODPara.getValue();
                // if (currentFunction.getName().equals(obj.toString())) {
                // return;
                // }
                if (connection != null) {
                    if (currentPortName != null) {
                        connection.setPortName(currentPortName.getPortName());

                    } else if (currentPortName == null && allPortNames != null) {
                        currentPortName = allPortNames.get(0);
                        connection.setPortName(currentPortName.getPortName());
                    }
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
                }
            }
        });

        portListTable.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                TableItem[] item = portListTable.getSelection();
                currentPortName = (PortNames) item[0].getData();
                if (connection != null) {
                    if (currentPortName != null) {
                        connection.setPortName(currentPortName.getPortName());

                    } else if (currentPortName == null && allPortNames != null) {
                        currentPortName = allPortNames.get(0);
                        connection.setPortName(currentPortName.getPortName());
                    } else {
                        connection.setPortName("");
                    }

                }
            }
        });
    }

    private void getDataFromNet() {
        funList.clear();
        portNameList.clear();
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                URLValue = wsdlField.getText();
            }

        });

        if (URLValue == null) {
            URLValue = ""; //$NON-NLS-1$
        }
        funList = getFunctionsList(URLValue); 
        if (!funList.isEmpty()) {
            if (funList.get(0) != null) {
                if (funList.get(0).getPortNames() != null) {
                    portNameList = funList.get(0).getPortNames();
                }
            }
        }
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                ExtendedTableModel<Function> listModel = listTableView.getExtendedTableModel();
                ExtendedTableModel<PortNames> portListModel = portListTableView.getExtendedTableModel();
                listModel.removeAll();
                listModel.addAll(funList);
                allfunList.clear();
                allfunList.addAll(funList);
                // getInputElementList();
                // getOutputElementList();
                portListModel.removeAll();
                portListModel.addAll(portNameList);
            }

        });

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
                    List contextList = contextManager.getListContext();
                    if (!contextList.isEmpty() && contextList.size() > 1) {
                        currentDefaultName = ConnectionContextHelper.getContextTypeForJob(shell, contextManager, false);
                    }
                    // ContextSetsSelectionDialog cssd=new ContextSetsSelectionDialog(shell,,false);
                    // ContextType contextType=ConnectionContextHelper.getContextTypeForContextMode(connector);
                    IContext context = contextManager.getContext(currentDefaultName);
                    url = ContextParameterUtils.parseScriptContextCode(contextValue, context);

                }
                setParseURL(url);
            }

        });

        return getparseURL();
    }

    private String getparseURL() {
        return url;
    }

    private void setParseURL(String url) {
        this.url = url;
    }

    public CTabFolder getTabFolder() {
        return this.tabFolder;
    }

    public Composite getOutputComposite() {
        return this.outputComposite;
    }

    public Composite getInputComposite() {
        return this.inputComposite;
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

    public PortNames getCurrentPortName() {
        return currentPortName;
    }

    public List<PortNames> getAllPortNames() {
        return this.allPortNames;
    }

    public int getSelectedColumnIndex() {
        return this.selectedColumnIndex;
    }

    public void saveProperties() {
        getWebServiceManager().savePropertiesToComponent();
    }

    public Boolean getIsFirst() {
        return this.isFirst;
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
            if (currentPortName != null) {
                connection.setPortName(currentPortName.getPortName());
            } else if (currentPortName == null && !allPortNames.isEmpty()) {
                currentPortName = allPortNames.get(0);
                connection.setPortName(currentPortName.getPortName());
            }

        }
        EList inputValue = connection.getParameterValue();

        IElementParameter INPUT_PARAMSPara = connector.getElementParameter("INPUT_PARAMS");
        List<Map<String, String>> inputparaValue = (List<Map<String, String>>) INPUT_PARAMSPara.getValue();
        if (inputparaValue != null) {
            inputValue.clear();
            if (currentFunction != null) {
                List inputParameter = currentFunction.getInputParameters();
                if (inputParameter != null) {

                    boolean mark = true;
                    List<ParameterInfo> ls = new ArrayList();
                    goin: for (Iterator iterator2 = inputParameter.iterator(); iterator2.hasNext();) {
                        ParameterInfo element = (ParameterInfo) iterator2.next();
                        WSDLParameter parameter = ConnectionFactory.eINSTANCE.createWSDLParameter();
                        parameter.setParameterInfo(element.getName());
                        if (element.getParent() == null) {
                            parameter.setParameterInfoParent("");
                        } else {
                            parameter.setParameterInfoParent(element.getParent().getName());
                        }
                        inputValue.add(parameter);
                        mark = false;
                        if (!element.getParameterInfos().isEmpty()) {
                            ls.addAll(new ParameterInfoUtil().getAllChildren(element));
                        }
                        break goin;
                    }
                    if (!mark) {
                        for (ParameterInfo para : ls) {
                            WSDLParameter parameter = ConnectionFactory.eINSTANCE.createWSDLParameter();
                            parameter.setParameterInfo(para.getName());
                            parameter.setParameterInfoParent(para.getParent().getName());
                            inputValue.add(parameter);
                        }
                    }

                }
            }
//            for (IMetadataColumn column : getInputValue()) {
//                WSDLParameter parameter = ConnectionFactory.eINSTANCE.createWSDLParameter();
//                if (column.getLabel() != null) {
//                    parameter.setColumn(column.getLabel());
//                    inputValue.add(parameter);
//                }
//            }
            String[] src = new String[]{"payload"};
            for (String insource : src) {
                WSDLParameter parameter = ConnectionFactory.eINSTANCE.createWSDLParameter();
                if (insource == null || "".equals(insource)) {
                    continue;
                }
                // Map<String, String> sourceMap = new HashMap<String, String>(1);
                parameter.setSource(insource);
                inputValue.add(parameter);
            }
        }
    }

    public void saveOutPutValue() {
        // save output
        EList outPutValue = connection.getOutputParameter();

        List<ParameterInfo> ls = new ArrayList();
        IElementParameter OUTPUT_PARAMSPara = connector.getElementParameter("OUTPUT_PARAMS");
        List<Map<String, String>> outputMap = (List<Map<String, String>>) OUTPUT_PARAMSPara.getValue();
        if (outputMap != null) {
            outPutValue.clear();
            if (currentFunction != null) {
                List<ParameterInfo> outputParameters = currentFunction.getOutputParameters();
                if (outputParameters != null) {
                    // for (int i = 0; i < inputParameter.size(); i++) {
                    boolean mark = true;
                    for (ParameterInfo element : outputParameters) {
                        WSDLParameter parameter = ConnectionFactory.eINSTANCE.createWSDLParameter();
                        parameter.setParameterInfo(element.getName());
                        if (element.getParent() == null) {
                            parameter.setParameterInfoParent("");
                        } else {
                            parameter.setParameterInfoParent(element.getParent().getName());
                        }
                        outPutValue.add(parameter);
                        // System.out.println(element.getParent() + " ppp");
                        mark = false;
                        if (!element.getParameterInfos().isEmpty()) {
                            ls.addAll(new ParameterInfoUtil().getAllChildren(element));
                        }
                    }
                    if (!mark) {
                        for (ParameterInfo para : ls) {
                            WSDLParameter parameter = ConnectionFactory.eINSTANCE.createWSDLParameter();
                            parameter.setParameterInfo(para.getName());
                            parameter.setParameterInfoParent(para.getParent().getName());
                            outPutValue.add(parameter);
                        }
                    }
                }
            }
        }
    }
}
