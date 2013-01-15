// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;

import javax.wsdl.WSDLException;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.ui.swt.advanced.dataeditor.AbstractDataTableEditorView;
import org.talend.commons.ui.swt.extended.table.ExtendedTableModel;
import org.talend.commons.ui.swt.formtools.LabelledFileField;
import org.talend.commons.ui.swt.tableviewer.TableViewerCreator;
import org.talend.commons.ui.swt.tableviewer.TableViewerCreatorColumn;
import org.talend.commons.ui.utils.PathUtils;
import org.talend.commons.utils.data.bean.IBeanPropertyAccessors;
import org.talend.commons.utils.io.StreamCopier;
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
import org.talend.designer.esb.webservice.WebServiceComponentPlugin;
import org.talend.designer.esb.webservice.i18n.Messages;
import org.talend.designer.esb.webservice.ws.wsdlinfo.Function;
import org.talend.designer.esb.webservice.ws.wsdlinfo.OperationInfo;
import org.talend.designer.esb.webservice.ws.wsdlinfo.ServiceInfo;
import org.talend.designer.esb.webservice.ws.wsdlutil.ComponentBuilder;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.dialog.RepositoryReviewDialog;
import org.talend.repository.ui.utils.ConnectionContextHelper;

/**
 * gcui class global comment. Detailled comment
 */
public class WebServiceUI extends WizardPage implements AbstractWebService {

    private static final String METHOD = "METHOD";
    private static final String NEED_SSL_TO_TRUSTSERVER = "NEED_SSL_TO_TRUSTSERVER";
    private static final String PORT_NAME = "PORT_NAME";
    private static final String ENDPOINT = "ENDPOINT";

    protected int maximumRowsToPreview = CorePlugin.getDefault().getPreferenceStore()
            .getInt(ITalendCorePrefConstants.PREVIEW_LIMIT);

    private WebServiceComponent webServiceComponent;

    private LabelledFileField wsdlField;

    private Label operationLabel;

    private Label portNameLabel;

    private AbstractDataTableEditorView<Function> listTableView;

    private AbstractDataTableEditorView<String> portListTableView;

    private Button refreshbut;

    private Button servicebut;

    private Table listTable;

    private Table portListTable;

    private Button populateCheckbox;

    private List<String> allPortNames = new ArrayList<String>();

    private List<Function> allFunctions = new ArrayList<Function>();

    private String URLValue = "";

    private Function currentFunction;

    private String currentPortName;

    private WSDLSchemaConnection connection = null;

    public WebServiceUI(WebServiceComponent webServiceComponent) {
        super("WebServiceUI"); //$NON-NLS-1$
        setTitle("Configure component with Web Service operation");
        this.webServiceComponent = webServiceComponent;
        initWebserviceUI();
    }

    public WebServiceUI(WebServiceComponent webServiceComponent, ConnectionItem connectionItem) {
        this(webServiceComponent);
        this.connection = (WSDLSchemaConnection) connectionItem.getConnection();
    }

    private void initWebserviceUI() {
        IElementParameter METHODPara = webServiceComponent.getElementParameter(METHOD);
        Object obj = METHODPara.getValue();
        if (obj != null && obj instanceof String && !"".equals(obj)) {
            String currentPort = (String) webServiceComponent.getElementParameter(PORT_NAME).getValue(); //$NON-NLS-1$

            allPortNames.add(currentPort);
            currentFunction = new Function(obj.toString(), currentPort);
            allFunctions.add(currentFunction);
        }
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

        @SuppressWarnings("unchecked")
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


    public void createControl(Composite parent) {
        Composite wsdlComposite = new Composite(parent, SWT.NONE);
//        wsdlComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        // WSDL URL
        int wsdlUrlcompositeColumn = 4;
        if (WebServiceComponentPlugin.hasRepositoryServices()) {
            wsdlUrlcompositeColumn = 5;
        }
        GridLayout layout = new GridLayout(wsdlUrlcompositeColumn, false);
        wsdlComposite.setLayout(layout);

        // 3 columns
        wsdlField = new LabelledFileField(wsdlComposite, "WSDL:",
            new String[] { "*.wsdl", "*.*" }, //$NON-NLS-1$  //$NON-NLS-2$
            1, SWT.BORDER) {

            protected void setFileFieldValue(String result) {
                if (result != null) {
                    getTextControl().setText(TalendTextUtils.addQuotes(PathUtils.getPortablePath(result)));
                    refresh();
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
        TalendProposalUtils.installOn(wsdlField.getTextControl(), webServiceComponent.getProcess(), webServiceComponent);
        String wsdlUrl = (String) webServiceComponent.getElementParameter(ENDPOINT).getValue();
        if (wsdlUrl != null) {
            wsdlField.setText(wsdlUrl);
        }

        // TESB-3590，gliu
        if (WebServiceComponentPlugin.hasRepositoryServices()) {
            servicebut = new Button(wsdlComposite, SWT.PUSH | SWT.CENTER);
            servicebut.setText(Messages.getString("WebServiceUI.Services"));
        }

        refreshbut = new Button(wsdlComposite, SWT.PUSH | SWT.CENTER);
        refreshbut.setImage(ImageProvider.getImage(EImage.REFRESH_ICON));

        // add port name UI
        portNameLabel = new Label(wsdlComposite, SWT.NONE);
        portNameLabel.setText(Messages.getString("WebServiceUI.Port")); //$NON-NLS-1$
        portNameLabel.setLayoutData(new GridData(SWT.NONE, SWT.TOP, false, false));

        ExtendedTableModel<String> portModel = new ExtendedTableModel<String>("PORTNAMELIST", allPortNames); //$NON-NLS-1$
        portListTableView = new DataTableEditorView<String>(
                wsdlComposite,
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
        portListTableView.getMainComposite().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, wsdlUrlcompositeColumn - 1, 1));

        // WSDL Operation
        operationLabel = new Label(wsdlComposite, SWT.NONE);
        operationLabel.setText(Messages.getString("WebServiceUI.Operation")); //$NON-NLS-1$
        operationLabel.setLayoutData(new GridData(SWT.NONE, SWT.TOP, false, false));

        ExtendedTableModel<Function> funModel = new ExtendedTableModel<Function>("FUNCTIONLIST", allFunctions); //$NON-NLS-1$
        listTableView = new DataTableEditorView<Function>(
                wsdlComposite,
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
        listTableView.getMainComposite().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, wsdlUrlcompositeColumn - 1, 1));

        addListenerForWSDLCom();

        if (WebServiceComponentPlugin.hasRepositoryServices()) {
            populateCheckbox = new Button(wsdlComposite, SWT.CHECK);
            populateCheckbox.setText("Populate schema to repository on finish");
            populateCheckbox.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false, wsdlUrlcompositeColumn, 1));
        }

        setControl(wsdlComposite);
        setPageComplete(false);
    }

    /**
     * DOC gcui Comment method "useSSL".
     *
     * @return
     */
    private void useSSL() {
        String trustStoreFile = "";
        String trustStorePassword = "";
        IElementParameter trustserverFileParameter = webServiceComponent.getElementParameter(
                "SSL_TRUSTSERVER_TRUSTSTORE");
        IElementParameter trustserverPasswordParameter = webServiceComponent.getElementParameter(
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
    private String getWSDL() throws IOException {
        InputStream is = null;
        ByteArrayOutputStream wsdlOs = new ByteArrayOutputStream();
        OutputStream os = compressAndEncode(wsdlOs);
        try {
            //WSDLFactory.newInstance().newWSDLWriter().writeWSDL(def, os);
            String wsdlLocation = getRealWsdlLocation();
            URL wsdlURL;
            try {
                wsdlURL = new URL(wsdlLocation);
            } catch (MalformedURLException e) {
                wsdlURL = new File(wsdlLocation).toURI().toURL();
            }
            is = wsdlURL.openStream(); 
            StreamCopier.copy(is, os);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return new String(wsdlOs.toByteArray());
    }

//    public Definition getWSDL(String compressedAndEncodedWsdl) {
//        ByteArrayInputStream bais = new ByteArrayInputStream(compressedAndEncodedWsdl.getBytes());
//        InputStream wsdlIS = decodeAndUncompress(bais);
//        try {
//            return WSDLFactory.newInstance().newWSDLReader().readWSDL(null, new InputSource(wsdlIS));
//        } catch (Exception e) {
//            WebServiceComponentPlugin.getDefault().getLog().log(
//                    WebServiceComponentPlugin.getStatus("Unable to read wsdl content...", e));
//        } finally {
//            if (null != wsdlIS) {
//                try {
//                    wsdlIS.close();
//                } catch (IOException e) {
//                    // ignore
//                }
//            }
//        }
//        return null;
//    }

    private static OutputStream compressAndEncode(OutputStream os) {
        return new DeflaterOutputStream(new Base64OutputStream(os));
    }

//    public static InputStream decodeAndUncompress(InputStream is) {
//        return new InflaterInputStream(new Base64InputStream(is));
//    }

    private void refresh() {
        setErrorMessage(null);
        URLValue = wsdlField.getText();
        ExtendedTableModel<String> portListModel = portListTableView.getExtendedTableModel();
        portListModel.removeAll();
        //clear functions
        ExtendedTableModel<Function> listModel = listTableView.getExtendedTableModel();
        listModel.removeAll();

        try {
            getContainer().run(true, false, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Retrieve WSDL parameter from net.", IProgressMonitor.UNKNOWN);
                    try {
                        allFunctions = getFunctionsList(URLValue);
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });

            final Set<String> portNameList = new HashSet<String>();
            for (Function function : allFunctions) {
                if ((function != null) && (function.getPortName() != null)) {
                    portNameList.add(function.getPortName());
                }
            }
            //NO addAll(Collection) for ExtendedTableModel??? grrr
            for (String portName : portNameList) {
                portListModel.add(portName);
            }
            if (portNameList.size() == 1) { //only one porttype
                listModel.addAll(allFunctions);
            }

            if (portListTable.getItemCount() > 1) {
                portListTable.deselectAll();
                setPageComplete(false);
            } else {
                selectFirstFunction();
            }
        } catch (InvocationTargetException e) {
            setErrorMessage("Error getting service description: " + e.getCause().getMessage());
            setPageComplete(false);
        } catch (InterruptedException e) {
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
                                refresh();
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
                    setPageComplete(true);
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
            listTable.setSelection(0);
            currentFunction = (Function) listTable.getItem(0).getData();
            setPageComplete(true);
        } else {
            setPageComplete(false);
        }
    }

    private List<Function> getFunctionsList(String wsdlUrl) throws WSDLException {
        IElementParameter parameter = webServiceComponent.getElementParameter(NEED_SSL_TO_TRUSTSERVER);
        if ((parameter != null) && Boolean.parseBoolean(parameter.getValue().toString())) {
            useSSL();
        }

        ComponentBuilder builder = new ComponentBuilder();
        ServiceInfo[] services = builder.buildserviceinformation(getRealWsdlLocation());

        List<Function> functionsAvailable = new ArrayList<Function>();
        for (ServiceInfo serviceInfo : services) {
            for (OperationInfo oper : serviceInfo.getOperations()) {
                Function f = new Function(serviceInfo, oper);
                functionsAvailable.add(f);
            }
        }

        return functionsAvailable;
    }

    private String getRealWsdlLocation() {
        if (!URLValue.contains("\"")) {
            return parseContextParameter(URLValue);
        }
        return TalendTextUtils.removeQuotes(URLValue);
    }

    private String parseContextParameter(final String contextValue) {
        String url = "";
        IContextManager contextManager = null;
        if (webServiceComponent.getProcess() == null) {
            // contextManager = contextModeManager.getSelectedContextType().getContextParameter(); //
            // connection.get
            // IContextManager contextManager
            ContextType contextType = ConnectionContextHelper.getContextTypeForContextMode(connection);
            url = ConnectionContextHelper.getOriginalValue(contextType, contextValue);
        } else {
            contextManager = webServiceComponent.getProcess().getContextManager();
            String currentDefaultName = contextManager.getDefaultContext().getName();
            List<IContext> contextList = contextManager.getListContext();
            if ((contextList != null) && (contextList.size() > 1)) {
                currentDefaultName =
                    ConnectionContextHelper.getContextTypeForJob(getControl().getShell(), contextManager, false);
            }
            // ContextSetsSelectionDialog cssd=new ContextSetsSelectionDialog(shell,,false);
            // ContextType contextType=ConnectionContextHelper.getContextTypeForContextMode(connector);
            IContext context = contextManager.getContext(currentDefaultName);
            url = ContextParameterUtils.parseScriptContextCode(contextValue, context);

        }

        return url;
    }

    public Function getCurrentFunction() {
        return currentFunction;
    }

    public boolean performFinish() {
        if (!saveValue()) {
            return false;
        }
        return populateSchema();
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
                if (currentFunction.getServiceNameSpace() != null) {
                    connection.setServerNameSpace(currentFunction.getServiceNameSpace());
                }
                if (currentFunction.getServiceName() != null) {
                    connection.setServerName(currentFunction.getServiceName());
                }
                if (currentFunction.getServiceNameSpace() != null) {
                    connection.setPortNameSpace(currentFunction.getServiceNameSpace());
                }
            }
            updateConnection();

        }
    }

    private boolean populateSchema() {
        if (currentFunction == null || populateCheckbox == null
                || !populateCheckbox.getSelection()) {
            return true;
        }
        try {
            getContainer().run(false, false, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        Class<?> forName = Class.forName("org.talend.repository.services.action.PublishMetadataAction");
                        Object newInstance = forName.newInstance();
                        forName.getMethod("run", String.class).invoke(newInstance, getRealWsdlLocation());
                    } catch (InvocationTargetException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    }
                }
            });
        } catch (InvocationTargetException e) {
            setErrorMessage("Populate schema to repository:" + e.getCause().getMessage());
            return false;
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    private boolean saveValue() {
        IElementParameter ENDPOINTPara = webServiceComponent.getElementParameter("ENDPOINT");
        ENDPOINTPara.setValue(URLValue);

        String wsdlString;
        try {
            wsdlString = getWSDL();
        } catch (IOException e) {
            setErrorMessage("Unable to create wsdl content: " + e.getMessage());
            return false;
        }
        IElementParameter wsdlContentPara = webServiceComponent.getElementParameter("WSDL_CONTENT");
        wsdlContentPara.setValue(wsdlString);

        if (currentPortName != null) {
            IElementParameter Port_Name = webServiceComponent.getElementParameter("PORT_NAME");
            Port_Name.setValue(currentPortName);
        } else if (currentPortName == null && !allPortNames.isEmpty()) {
            currentPortName = allPortNames.get(0);
            IElementParameter Port_Name = webServiceComponent.getElementParameter("PORT_NAME");
            Port_Name.setValue(currentPortName);
        }

        Function function = getCurrentFunction();
        if (function != null) {
            if (function.getNameSpaceURI() != null) {
                IElementParameter METHODPara = webServiceComponent.getElementParameter("METHOD_NS");
                METHODPara.setValue(function.getNameSpaceURI());
            }
            if (function.getName() != null) {
                IElementParameter METHODPara = webServiceComponent.getElementParameter("METHOD");
                METHODPara.setValue(function.getName());
            }
            if (function.getServiceNameSpace() != null) {
                IElementParameter Service_NS = webServiceComponent.getElementParameter("SERVICE_NS");
                Service_NS.setValue(function.getServiceNameSpace());
            }
            if (function.getServiceName() != null) {
                IElementParameter Service_Name = webServiceComponent.getElementParameter("SERVICE_NAME");
                Service_Name.setValue(function.getServiceName());
            }
            if (function.getServiceNameSpace() != null) {
                IElementParameter Port_NS = webServiceComponent.getElementParameter("PORT_NS");
                Port_NS.setValue(function.getServiceNameSpace());
            }

            IElementParameter Soap_Action = webServiceComponent.getElementParameter("SOAP_ACTION");
            Soap_Action.setValue(function.getSoapAction());

            IElementParameter esbEndpoint = webServiceComponent.getElementParameter("ESB_ENDPOINT");
            if (esbEndpoint != null) {
                esbEndpoint.setValue(TalendTextUtils.addQuotes(function.getAddressLocation()));
            }
            IElementParameter commStyle = webServiceComponent.getElementParameter("COMMUNICATION_STYLE");
            if (commStyle != null) {
                commStyle.setValue(function.getCommunicationStyle());
            }

        }
        return true;
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

}
