// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.talend.camel.designer.i18n.Messages;
import org.talend.camel.designer.ui.wizards.actions.JavaCamelJobScriptsExportWSAction;
import org.talend.camel.designer.ui.wizards.actions.JavaCamelJobScriptsExportWithMavenAction;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.general.ModuleNeeded.ELibraryInstallStatus;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.runprocess.IProcessor;
import org.talend.librariesmanager.model.ModulesNeededProvider;
import org.talend.repository.documentation.ExportFileResource;
import org.talend.repository.ui.wizards.exportjob.ExportTreeViewer;
import org.talend.repository.ui.wizards.exportjob.JavaJobScriptsExportWSWizardPage;
import org.talend.repository.ui.wizards.exportjob.JobScriptsExportWizardPage;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager.ExportChoice;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManagerFactory;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.petals.ContextExportDialog;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.petals.ContextExportType;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.petals.ContextTypeDefinition;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.petals.PetalsExportException;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.petals.PetalsTemporaryOptionsKeeper;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.petals.TalendUtils;
import org.talend.resource.IExportRouteResourcesService;

/**
 * DOC x class global comment. Detailled comment <br/>
 * 
 */
@SuppressWarnings("restriction")
public class JavaCamelJobScriptsExportWSWizardPage extends JavaCamelJobScriptsExportWizardPage {

    public static final String EXPORTTYPE_POJO = "Autonomous Route"; //$NON-NLS-1$

    public static final String EXPORTTYPE_WSWAR = "Axis WebService (WAR)"; //$NON-NLS-1$

    public static final String EXPORTTYPE_WSZIP = "Axis WebService (ZIP)"; //$NON-NLS-1$

    public static final String EXPORTTYPE_JBOSSESB = "JBoss ESB"; //$NON-NLS-1$

    public static final String EXPORTTYPE_JBI = "JBI (JSR 208)"; //$NON-NLS-1$

    public static final String ESBTYPE_JBOSS_MQ = "JBoss MQ"; //$NON-NLS-1$

    public static final String ESBTYPE_JBOSS_MESSAGING = "JBoss Messaging"; //$NON-NLS-1$

    public static final String EXPORTTYPE_PETALSESB = "Petals ESB"; //$NON-NLS-1$

    public static final String EXPORTTYPE_OSGI = "OSGI Bundle For ESB"; //$NON-NLS-1$

    public static final String EXPORTTYPE_KAR = "ESB Runtime Kar File"; //$NON-NLS-1$

    protected Combo exportTypeCombo;

    protected Combo esbTypeCombo;

    protected Composite pageComposite;

    protected Composite optionsGroupComposite;

    protected Composite destinationNameFieldComposite;

    protected Composite destinationNameFieldInnerComposite;

    protected Button webXMLButton;

    protected Button configFileButton;

    protected Button axisLibButton;

    protected Button wsddButton;

    protected Button wsdlButton;

    protected Button chkButton;

    // private Button sourceButton, generateEndpointButton, singletonButton, validateByWsdlButton;
    protected Button singletonButton;

    protected Button generateEndpointButton;

    protected Button sourceButton;

    protected Button validateByWsdlButton;

    protected Text esbQueueMessageName;

    protected Text esbServiceName;

    protected Text esbCategory;

    public static final String STORE_EXPORTTYPE_ID = "JavaJobScriptsExportWizardPage.STORE_EXPORTTYPE_ID"; //$NON-NLS-1$

    public static final String STORE_WEBXML_ID = "JavaJobScriptsExportWizardPage.STORE_WEBXML_ID"; //$NON-NLS-1$

    public static final String STORE_CONFIGFILE_ID = "JavaJobScriptsExportWizardPage.STORE_CONFIGFILE_ID"; //$NON-NLS-1$

    public static final String STORE_AXISLIB_ID = "JavaJobScriptsExportWizardPage.STORE_AXISLIB_ID"; //$NON-NLS-1$

    public static final String STORE_WSDD_ID = "JavaJobScriptsExportWizardPage.STORE_WSDD_ID"; //$NON-NLS-1$

    public static final String STORE_WSDL_ID = "JavaJobScriptsExportWizardPage.STORE_WSDL_ID"; //$NON-NLS-1$

    public static final String EXTRACT_ZIP_FILE = "JavaJobScriptsExportWizardPage.EXTRACT_ZIP_FILE"; //$NON-NLS-1$

    protected String exportTypeFixed;

    private Map<String, List<ContextTypeDefinition>> ctxToTypeDefs = new HashMap<String, List<ContextTypeDefinition>>();

    private List<ContextTypeDefinition> currentCtxTypes;

    private String saDestinationFilePath;

    public static final String PETALS_EXPORT_DESTINATIONS = "org.ow2.petals.esbexport.destinations"; //$NON-NLS-1$

    public JavaCamelJobScriptsExportWSWizardPage(IStructuredSelection selection, String exportType) {
        super(selection);
        // there assign the manager again
        exportTypeFixed = exportType;
    }

    public String getCurrentExportType() {
        // TESB-5328
        //        if (exportTypeCombo != null && !exportTypeCombo.getText().equals("")) { //$NON-NLS-1$
        // return exportTypeCombo.getText();
        // } else {
        // IDialogSettings settings = getDialogSettings();
        // if (settings != null && settings.get(STORE_EXPORTTYPE_ID) != null) {
        // return settings.get(STORE_EXPORTTYPE_ID);
        // }
        // }
        // Fix but TESB-2944 set default export type to OSGI
        // return EXPORTTYPE_OSGI;
        return EXPORTTYPE_KAR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.WizardPage#setWizard(org.eclipse.jface.wizard.IWizard)
     */
    @Override
    public void setWizard(IWizard newWizard) {
        super.setWizard(newWizard);
        initialiseDefaultDialogSettings();
    }

    /**
     * this set default dialog settings if none already exists.
     */
    private void initialiseDefaultDialogSettings() {
        IDialogSettings dialogSettings = getDialogSettings();
        if (dialogSettings != null) {
            // set default export type according to system properties
            String exportType = dialogSettings.get(STORE_EXPORTTYPE_ID);
            String defaultExportType = System.getProperty("talend.export.route.default.type"); //$NON-NLS-1$
            if ((exportType == null || exportType.equals("")) && defaultExportType != null && !defaultExportType.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
                dialogSettings.put(STORE_EXPORTTYPE_ID,
                        JavaJobScriptsExportWSWizardPage.JobExportType.getTypeFromString(defaultExportType).label);
            }
        }// else ignors it
    }

    @Override
    public void createControl(Composite parent) {

        initializeDialogUnits(parent);
        GridLayout layout = new GridLayout();

        if (exportTypeFixed == null || !exportTypeFixed.equals(EXPORTTYPE_JBOSSESB)) {
            SashForm sash = createExportTree(parent);

            pageComposite = new Group(sash, 0);
            pageComposite.setLayout(layout);
            pageComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
            pageComposite.setFont(parent.getFont());
            setControl(sash);
            sash.setWeights(new int[] { 0, 1, 23 });
        } else {
            pageComposite = new Group(parent, 0);
            pageComposite.setLayout(layout);
            pageComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
            pageComposite.setFont(parent.getFont());
            setControl(parent);
        }
        layout = new GridLayout();
        destinationNameFieldComposite = new Composite(pageComposite, SWT.NONE);
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        destinationNameFieldComposite.setLayoutData(gridData);
        destinationNameFieldComposite.setLayout(layout);

        destinationNameFieldInnerComposite = new Composite(destinationNameFieldComposite, SWT.NONE);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        destinationNameFieldInnerComposite.setLayoutData(gridData);
        destinationNameFieldInnerComposite.setLayout(layout);

        createDestinationGroup(destinationNameFieldInnerComposite);
        // createExportTree(pageComposite);
        if (!isMultiNodes()) {
            createJobVersionGroup(pageComposite);
        }

        createExportTypeGroup(pageComposite);

        createOptionsGroupButtons(pageComposite);

        restoreResourceSpecificationWidgetValues(); // ie.- local

        updateWidgetEnablements();
        setPageComplete(determinePageCompletion());

        giveFocusToDestination();

    }

    protected void createExportTypeGroup(Composite parent) {
        // options group
        Group optionsGroup = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        optionsGroup.setLayout(layout);
        optionsGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        optionsGroup.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.BuildType")); //$NON-NLS-1$
        optionsGroup.setFont(parent.getFont());

        optionsGroup.setLayout(new GridLayout(1, true));

        Composite left = new Composite(optionsGroup, SWT.NONE);
        left.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
        left.setLayout(new GridLayout(3, false));

        Label label = new Label(left, SWT.NONE);
        label.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.BuildLabel")); //$NON-NLS-1$

        exportTypeCombo = new Combo(left, SWT.PUSH);
        GridData gd = new GridData();
        gd.horizontalSpan = 1;
        exportTypeCombo.setLayoutData(gd);

        // TESB-5328
        //        if (!Boolean.getBoolean("talend.export.route.2." + JavaJobScriptsExportWSWizardPage.JobExportType.POJO + ".hide")) { //$NON-NLS-1$ //$NON-NLS-2$
        // exportTypeCombo.add(EXPORTTYPE_POJO);
        // }

        // TESB-3222 Remove unnecessary export type in export wizard page LiXiaopeng
        //        if (!Boolean.getBoolean("talend.export.route.2." + JavaJobScriptsExportWSWizardPage.JobExportType.WSWAR + ".hide")) {//$NON-NLS-1$ //$NON-NLS-2$
        // exportTypeCombo.add(EXPORTTYPE_WSWAR);
        // }
        //        if (!Boolean.getBoolean("talend.export.route.2." + JavaJobScriptsExportWSWizardPage.JobExportType.WSZIP + ".hide")) {//$NON-NLS-1$ //$NON-NLS-2$
        // exportTypeCombo.add(EXPORTTYPE_WSZIP);
        // }
        //        if (!Boolean.getBoolean("talend.export.route.2." + JavaJobScriptsExportWSWizardPage.JobExportType.JBOSSESB + ".hide")) {//$NON-NLS-1$ //$NON-NLS-2$
        // exportTypeCombo.add(EXPORTTYPE_JBOSSESB);
        // }
        //        if (!Boolean.getBoolean("talend.export.route.2." + JavaJobScriptsExportWSWizardPage.JobExportType.PETALSESB + ".hide")) {//$NON-NLS-1$ //$NON-NLS-2$
        // exportTypeCombo.add(EXPORTTYPE_PETALSESB);
        // }
        if (!Boolean.getBoolean("talend.export.route.2." + JavaJobScriptsExportWSWizardPage.JobExportType.OSGI + ".hide")) {//$NON-NLS-1$ //$NON-NLS-2$
            // TESB-5328
            // exportTypeCombo.add(EXPORTTYPE_OSGI);
            exportTypeCombo.add(EXPORTTYPE_KAR);
            exportTypeCombo.setEnabled(false); // only can export kar file
        }

        // exportTypeCombo.add("JBI (JSR 208)");

        exportTypeCombo.setText(getCurrentExportType());
        if (exportTypeFixed != null) {
            left.setVisible(false);
            optionsGroup.setVisible(false);
            exportTypeCombo.setText(exportTypeFixed);
        }

        chkButton = new Button(left, SWT.CHECK);
        chkButton.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.extractZipFile")); //$NON-NLS-1$
        if (exportTypeCombo.getText().equals(EXPORTTYPE_WSWAR) || exportTypeCombo.getText().equals(EXPORTTYPE_PETALSESB)
                || exportTypeCombo.getText().equals(EXPORTTYPE_OSGI) || exportTypeCombo.getText().equals(EXPORTTYPE_KAR)) {
            chkButton.setVisible(false);
            zipOption = null;
        } else {
            chkButton.setVisible(true);
            zipOption = String.valueOf(chkButton.getSelection());
        }
        chkButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                chkButton.setSelection(chkButton.getSelection());
                zipOption = String.valueOf(chkButton.getSelection());
            }
        });
        exportTypeCombo.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {

            }

            public void widgetSelected(SelectionEvent e) {
                destinationNameFieldInnerComposite.dispose();
                GridLayout layout = new GridLayout();
                destinationNameFieldInnerComposite = new Composite(destinationNameFieldComposite, SWT.NONE);
                GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
                destinationNameFieldInnerComposite.setLayoutData(gridData);
                destinationNameFieldInnerComposite.setLayout(layout);
                createDestinationGroup(destinationNameFieldInnerComposite);

                destinationNameFieldComposite.layout();

                optionsGroupComposite.dispose();
                createOptionsGroupButtons(pageComposite);

                pageComposite.layout();
                if (exportTypeCombo.getText().equals(EXPORTTYPE_WSWAR) || exportTypeCombo.getText().equals(EXPORTTYPE_OSGI)
                        || exportTypeCombo.getText().equals(EXPORTTYPE_KAR)) {
                    chkButton.setVisible(false);
                    zipOption = null;
                } else {
                    chkButton.setVisible(true);
                    zipOption = String.valueOf(chkButton.getSelection());
                }
                checkExport();
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.wizards.exportjob.JavaJobScriptsExportWizardPage#createJobScriptsManager()
     */
    @Override
    public JobScriptsManager createJobScriptsManager() {
        String launcher = (launcherCombo == null || launcherCombo.isDisposed()) ? "all" : launcherCombo.getText();
        String context = (contextCombo == null || contextCombo.isDisposed()) ? "Default" : contextCombo.getText();
        return JobScriptsManagerFactory.createManagerInstance(getExportChoiceMap(), context, launcher, IProcessor.NO_STATISTICS,
                IProcessor.NO_TRACES, JavaJobScriptsExportWSWizardPage.JobExportType.getTypeFromString(getCurrentExportType()));
    }

    @Override
    protected String getOutputSuffix() {
        if (getCurrentExportType().equals(EXPORTTYPE_WSWAR)) {
            return FileConstants.WAR_FILE_SUFFIX;
        } else if (getCurrentExportType().equals(EXPORTTYPE_JBOSSESB)) {
            return FileConstants.ESB_FILE_SUFFIX;
        } else if (getCurrentExportType().equals(EXPORTTYPE_OSGI)) {
            return FileConstants.JAR_FILE_SUFFIX;
        } else if (getCurrentExportType().equals(EXPORTTYPE_KAR)) {
            return FileConstants.KAR_FILE_SUFFIX;
        } else {
            return FileConstants.ZIP_FILE_SUFFIX;
        }
    }

    protected String getPetalsDefaultSaName() {
        return "sa-talend-" + this.getDefaultFileName().get(0) + "Service-provide.zip"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Open an appropriate destination browser so that the user can specify a source to import from.
     */
    @Override
    protected void handleDestinationBrowseButtonPressed() {
        FileDialog dialog = new FileDialog(getContainer().getShell(), SWT.SAVE);
        if (getCurrentExportType().equals(EXPORTTYPE_WSWAR)) {
            dialog.setFilterExtensions(new String[] { "*.war", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
        } else if (getCurrentExportType().equals(EXPORTTYPE_JBOSSESB)) {
            dialog.setFilterExtensions(new String[] { "*.esb", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
        } else if (getCurrentExportType().equals(EXPORTTYPE_OSGI)) {
            dialog.setFilterExtensions(new String[] { "*.jar", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
        } else if (getCurrentExportType().equals(EXPORTTYPE_KAR)) {
            if (isAddMavenScript()) {
                dialog.setFilterExtensions(new String[] { "*" + FileConstants.ZIP_FILE_SUFFIX, "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                dialog.setFilterExtensions(new String[] { "*.kar", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else if (getCurrentExportType().equals(EXPORTTYPE_PETALSESB)) {
            dialog.setFilterExtensions(new String[] { "*.zip", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            dialog.setFilterExtensions(new String[] { "*.zip", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (getCurrentExportType().equals(EXPORTTYPE_PETALSESB)) {
            IPath destPath = new Path(this.saDestinationFilePath);
            String fileName, directory;
            if (destPath.toFile().isDirectory()) {
                fileName = getPetalsDefaultSaName();
                directory = destPath.toOSString();
            } else {
                fileName = destPath.lastSegment();
                directory = destPath.removeLastSegments(1).toOSString();
            }
            dialog.setFileName(fileName);
            dialog.setFilterPath(directory);
        } else {
            dialog.setText(""); //$NON-NLS-1$
            // this is changed by me shenhaize
            dialog.setFileName((String) this.getDefaultFileName().get(0));
            String currentSourceString = getDestinationValue();
            int lastSeparatorIndex = currentSourceString.lastIndexOf(File.separator);
            if (lastSeparatorIndex != -1) {
                dialog.setFilterPath(currentSourceString.substring(0, lastSeparatorIndex));
            }
        }

        String selectedFileName = dialog.open();
        if (selectedFileName == null) {
            return;
        }
        String idealSuffix;
        if (isAddMavenScript()) {
            idealSuffix = FileConstants.ZIP_FILE_SUFFIX;
        } else {
            idealSuffix = getOutputSuffix();
        }
        if (!selectedFileName.endsWith(idealSuffix)) {
            selectedFileName += idealSuffix;
        }
        // when user change the name of job,will add the version auto
        if (selectedFileName != null && !selectedFileName.endsWith(this.getSelectedJobVersion() + idealSuffix)) {
            String b = selectedFileName.substring(0, (selectedFileName.length() - 4));
            File file = new File(b);

            String str = file.getName();

            String s = (String) this.getDefaultFileName().get(0);

            if (str.equals(s)) {
                selectedFileName = b + "_" + this.getDefaultFileName().get(1) + idealSuffix; //$NON-NLS-1$
            } else {
                selectedFileName = b + idealSuffix;
            }

        }
        if (selectedFileName != null) {
            setErrorMessage(null);
            this.saDestinationFilePath = selectedFileName;
            setDestinationValue(selectedFileName);

            if (getDialogSettings() != null) {
                IDialogSettings section = getDialogSettings().getSection(DESTINATION_FILE);
                if (section == null) {
                    section = getDialogSettings().addNewSection(DESTINATION_FILE);
                }
                section.put(DESTINATION_FILE, selectedFileName);
            }

        }
    }

    @Override
    public void handleEvent(Event e) {
        super.handleEvent(e);
        Widget source = e.widget;
        if (source instanceof Combo) {
            String destination = ((Combo) source).getText();
            if (getDialogSettings() != null) {
                IDialogSettings section = getDialogSettings().getSection(DESTINATION_FILE);
                if (section == null) {
                    section = getDialogSettings().addNewSection(DESTINATION_FILE);
                }
                section.put(DESTINATION_FILE, destination);
            }
        }
    }

    @Override
    public boolean isAddMavenScript() {
        if (addBSButton != null) {
            return addBSButton.getSelection();
        }

        return false;
    }

    protected void restoreWidgetValuesForPetalsESB() {

        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            String[] directoryNames = settings.getArray(STORE_DESTINATION_NAMES_ID);
            if (directoryNames != null) {
                String filter = "sa-talend-(.)*-provide\\.zip"; //$NON-NLS-1$
                for (String directoryName : directoryNames) {
                    if (directoryName.toLowerCase().matches(filter)) {
                        addDestinationItem(directoryName);
                    }
                }
            }

            // setDefaultDestination();

            String saName = getPetalsDefaultSaName();
            String userDir = System.getProperty("user.dir"); //$NON-NLS-1$

            IPath path = new Path(userDir).append(saName);
            this.saDestinationFilePath = path.toOSString();
            setDestinationValue(this.saDestinationFilePath);

            sourceButton.setSelection(settings.getBoolean(STORE_SOURCE_ID));
            // userRoutineButton.setSelection(settings.getBoolean(STORE_USER_ROUTINE_ID));
            zipOption = "false"; // Do not extract the ZIP //$NON-NLS-1$
        }

        if (getProcessItem() != null) {
            List<String> contextNames = getJobContexts(getProcessItem());
            contextCombo.setItems(contextNames.toArray(new String[contextNames.size()]));
            contextCombo.setVisibleItemCount(contextNames.size());
            if (contextNames.size() > 0) {
                contextCombo.select(0);
            }
        }
    }

    protected void restoreWidgetValuesForESB() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            String[] directoryNames = settings.getArray(STORE_DESTINATION_NAMES_ID);
            if (directoryNames != null) {
                // destination
                String filterName = ".esb"; //$NON-NLS-1$

                for (String directoryName : directoryNames) {
                    if (directoryName.toLowerCase().endsWith(filterName)) {
                        addDestinationItem(directoryName);

                    }
                }
            }
            setDefaultDestination();

            IDialogSettings section = getDialogSettings().getSection(DESTINATION_FILE);
            if (section == null) {
                section = getDialogSettings().addNewSection(DESTINATION_FILE);
            }
            if (jobScriptButton != null && !jobScriptButton.isDisposed()) {
                jobScriptButton.setSelection(settings.getBoolean(STORE_SOURCE_ID));
            }
            if (contextButton != null && !contextButton.isDisposed()) {
                contextButton.setSelection(settings.getBoolean(STORE_CONTEXT_ID));
            }
            if (applyToChildrenButton != null && !applyToChildrenButton.isDisposed()) {
                applyToChildrenButton.setSelection(settings.getBoolean(APPLY_TO_CHILDREN_ID));
            }
            if (jobItemButton != null && !jobItemButton.isDisposed()) {
                jobItemButton.setSelection(settings.getBoolean(STORE_JOB_ID));
            }

            if (section.get(ESB_EXPORT_TYPE) != null) {
                esbTypeCombo.setText(section.get(ESB_EXPORT_TYPE));
                if (section.get(ESB_SERVICE_NAME) != null) {
                    esbServiceName.setText(section.get(ESB_SERVICE_NAME));
                }
                if (section.get(ESB_CATEGORY) != null) {
                    esbCategory.setText(section.get(ESB_CATEGORY));
                }
                if (section.get(QUERY_MESSAGE_NAME) != null) {
                    this.esbQueueMessageName.setText(section.get(QUERY_MESSAGE_NAME));
                }
            }
        }

        if (getProcessItem() != null && contextCombo != null) {
            try {
                setProcessItem((ProcessItem) ProxyRepositoryFactory.getInstance()
                        .getUptodateProperty(getProcessItem().getProperty()).getItem());
            } catch (PersistenceException e) {
                e.printStackTrace();
            }
            List<String> contextNames = getJobContexts(getProcessItem());
            contextCombo.setItems(contextNames.toArray(new String[contextNames.size()]));
            if (contextNames.size() > 0) {
                contextCombo.select(0);
            }
        }
    }

    protected void restoreWidgetValuesForWS() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            String[] directoryNames = settings.getArray(STORE_DESTINATION_NAMES_ID);
            if (directoryNames != null) {
                // destination
                String filterName = ".zip"; //$NON-NLS-1$

                if (exportTypeCombo.getText().equals(EXPORTTYPE_WSWAR)) {
                    filterName = ".war"; //$NON-NLS-1$
                } else {
                    filterName = ".zip"; //$NON-NLS-1$
                }

                for (String directoryName : directoryNames) {
                    if (directoryName.toLowerCase().endsWith(filterName)) {
                        addDestinationItem(directoryName);

                    }
                }
            }
            setDefaultDestination();

            webXMLButton.setSelection(settings.getBoolean(STORE_WEBXML_ID));
            configFileButton.setSelection(settings.getBoolean(STORE_CONFIGFILE_ID));
            axisLibButton.setSelection(settings.getBoolean(STORE_AXISLIB_ID));
            wsddButton.setSelection(settings.getBoolean(STORE_WSDD_ID));
            wsdlButton.setSelection(settings.getBoolean(STORE_WSDL_ID));
            jobScriptButton.setSelection(settings.getBoolean(STORE_SOURCE_ID));
            contextButton.setSelection(settings.getBoolean(STORE_CONTEXT_ID));
            applyToChildrenButton.setSelection(settings.getBoolean(APPLY_TO_CHILDREN_ID));
            chkButton.setSelection(settings.getBoolean(EXTRACT_ZIP_FILE));
            if (chkButton.isVisible()) {
                zipOption = String.valueOf(chkButton.getSelection());
            } else {
                zipOption = "false"; //$NON-NLS-1$
            }

        }

        if (getProcessItem() != null && contextCombo != null) {
            try {
                setProcessItem((ProcessItem) ProxyRepositoryFactory.getInstance()
                        .getUptodateProperty(getProcessItem().getProperty()).getItem());
            } catch (PersistenceException e) {
                e.printStackTrace();
            }
            List<String> contextNames = getJobContexts(getProcessItem());
            contextCombo.setItems(contextNames.toArray(new String[contextNames.size()]));
            if (contextNames.size() > 0) {
                contextCombo.select(0);
            }
        }
    }

    protected void restoreWidgetValuesForPOJO() {
        IDialogSettings settings = getDialogSettings();

        if (settings != null) {
            String[] directoryNames = settings.getArray(STORE_DESTINATION_NAMES_ID);

            if (directoryNames != null && directoryNames.length > 0) {
                String fileName = this.getDefaultFileName().get(0) + "_" + this.getDefaultFileName().get(1) + getOutputSuffix();
                for (String directoryName : directoryNames) {
                    String destination = new Path(directoryName).append(fileName).toOSString();
                    addDestinationItem(destination);
                    setDestinationValue(destination);
                }
            } else {
                setDefaultDestination();
            }

            shellLauncherButton.setSelection(settings.getBoolean(STORE_SHELL_LAUNCHER_ID));
            // systemRoutineButton.setSelection(settings.getBoolean(STORE_SYSTEM_ROUTINE_ID));
            // userRoutineButton.setSelection(settings.getBoolean(STORE_USER_ROUTINE_ID));
            jobItemButton.setSelection(settings.getBoolean(STORE_JOB_ID));

            jobScriptButton.setSelection(settings.getBoolean(STORE_SOURCE_ID));
            contextButton.setSelection(settings.getBoolean(STORE_CONTEXT_ID));
            applyToChildrenButton.setSelection(settings.getBoolean(APPLY_TO_CHILDREN_ID));
            chkButton.setSelection(settings.getBoolean(EXTRACT_ZIP_FILE));
            zipOption = String.valueOf(chkButton.getSelection());
            // genCodeButton.setSelection(settings.getBoolean(STORE_GENERATECODE_ID));
        }

        launcherCombo.setItems(JobScriptsManager.getLauncher());
        if (JobScriptsManager.getLauncher().length > 0) {
            launcherCombo.select(0);
        }
        if (getProcessItem() != null && contextCombo != null) {
            // don't update the property, this one will be automatically updated if needed when call the getItem()

            // try {
            // setProcessItem((ProcessItem) ProxyRepositoryFactory.getInstance().getUptodateProperty(
            // getProcessItem().getProperty()).getItem());
            // } catch (PersistenceException e) {
            // ExceptionHandler.process(e);
            // }
            ProcessItem item = getProcessItem();
            try {
                String id = item.getProperty().getId();
                IRepositoryViewObject lastVersion = ProxyRepositoryFactory.getInstance().getLastVersion(id);
                item = (ProcessItem) lastVersion.getProperty().getItem();
            } catch (PersistenceException e) {
                throw new RuntimeException(e);
            }
            List<String> contextNames;
            contextNames = getJobContexts(item);

            contextCombo.setItems(contextNames.toArray(new String[contextNames.size()]));
            if (contextNames.size() > 0) {
                contextCombo.select(0);
            }
        }

    }

    @Override
    protected void internalSaveWidgetValues() {
        // update directory names history
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            if (getCurrentExportType().equals(EXPORTTYPE_PETALSESB)) {
                String[] directoryNames = settings.getArray(PETALS_EXPORT_DESTINATIONS);
                if (directoryNames == null) {
                    directoryNames = new String[0];
                }

                directoryNames = addToHistory(directoryNames, this.saDestinationFilePath);
                settings.put(PETALS_EXPORT_DESTINATIONS, directoryNames);
                return;
            }
            // String[] directoryNames =
            // settings.getArray(STORE_DESTINATION_NAMES_ID);
            // if (directoryNames == null) {
            // directoryNames = new String[0];
            // }
            // String destinationValue = manager.getDestinationPath();
            // directoryNames = addToHistory(directoryNames, destinationValue);
            String[] directoryNames = new String[1];
            String destinationValue = manager.getDestinationPath();
            if (destinationValue != null) {
                IPath path = Path.fromOSString(destinationValue);
                destinationValue = path.removeLastSegments(1).toOSString();
            }
            directoryNames[0] = destinationValue;

            settings.put(STORE_EXPORTTYPE_ID, getCurrentExportType());
            settings.put(STORE_DESTINATION_NAMES_ID, directoryNames);
            if (getCurrentExportType().equals(EXPORTTYPE_OSGI) || getCurrentExportType().equals(EXPORTTYPE_KAR)) {
                return;
            }
            if (contextButton != null) {
                settings.put(STORE_CONTEXT_ID, contextButton.getSelection());
            }
            if (applyToChildrenButton != null) {
                settings.put(APPLY_TO_CHILDREN_ID, applyToChildrenButton.getSelection());
            }
            if (jobScriptButton != null && !jobScriptButton.isDisposed()) {
                settings.put(STORE_SOURCE_ID, jobScriptButton.getSelection());
            }
            if (jobItemButton != null && !jobItemButton.isDisposed()) {
                settings.put(STORE_JOB_ID, jobItemButton.getSelection());
            }

            if (getCurrentExportType().equals(EXPORTTYPE_POJO)) {
                settings.put(STORE_SHELL_LAUNCHER_ID, shellLauncherButton.getSelection());
                // settings.put(STORE_SYSTEM_ROUTINE_ID, systemRoutineButton.getSelection());
                // settings.put(STORE_USER_ROUTINE_ID, userRoutineButton.getSelection());
                settings.put(EXTRACT_ZIP_FILE, chkButton.getSelection());
                return;
            } else if (getCurrentExportType().equals(EXPORTTYPE_WSZIP)) {
                settings.put(STORE_WEBXML_ID, webXMLButton.getSelection());
                settings.put(STORE_CONFIGFILE_ID, configFileButton.getSelection());
                settings.put(STORE_AXISLIB_ID, axisLibButton.getSelection());
                settings.put(STORE_WSDD_ID, wsddButton.getSelection());
                settings.put(STORE_WSDL_ID, wsdlButton.getSelection());
                settings.put(EXTRACT_ZIP_FILE, chkButton.getSelection());
            }

        }
    }

    @Override
    protected Map<ExportChoice, Object> getExportChoiceMap() {
        return getExportChoiceMap(exportTypeCombo.getText());
    }

    protected Map<ExportChoice, Object> getExportChoiceMap(String exportType) {

        if (EXPORTTYPE_POJO.equals(exportType)) {
            return JavaCamelJobScriptsExportWSWizardPage.super.getExportChoiceMap();
        }
        Map<ExportChoice, Object> exportChoiceMap = new EnumMap<ExportChoice, Object>(ExportChoice.class);
        if (EXPORTTYPE_PETALSESB.equals(exportType)) {
            exportChoiceMap.put(ExportChoice.needSourceCode, sourceButton.getSelection());
            exportChoiceMap.put(ExportChoice.needDependencies, Boolean.TRUE);
            exportChoiceMap.put(ExportChoice.needUserRoutine, true);
            return exportChoiceMap;
        }
        exportChoiceMap.put(ExportChoice.needJobItem, false);
        exportChoiceMap.put(ExportChoice.needSourceCode, false);

        if (EXPORTTYPE_JBOSSESB.equals(exportType)) {
            exportChoiceMap.put(ExportChoice.needMetaInfo, true);
            exportChoiceMap.put(ExportChoice.needContext, contextButton.getSelection());
            exportChoiceMap.put(ExportChoice.esbQueueMessageName, esbQueueMessageName.getText());
            exportChoiceMap.put(ExportChoice.esbServiceName, esbServiceName.getText());
            exportChoiceMap.put(ExportChoice.esbCategory, esbCategory.getText());
            exportChoiceMap.put(ExportChoice.esbExportType, esbTypeCombo.getText());
            exportChoiceMap.put(ExportChoice.needDependencies, jobItemButton.getSelection());
            exportChoiceMap.put(ExportChoice.needJobItem, jobItemButton.getSelection());
            exportChoiceMap.put(ExportChoice.needSourceCode, jobItemButton.getSelection()); // take source code also
            // when take item
            return exportChoiceMap;
        }

        if (EXPORTTYPE_OSGI.equals(exportType) || EXPORTTYPE_KAR.equals(exportType)) {
            exportChoiceMap.put(ExportChoice.needMetaInfo, true);
            exportChoiceMap.put(ExportChoice.needContext, true);
            exportChoiceMap.put(ExportChoice.needJobItem, false);
            exportChoiceMap.put(ExportChoice.needSourceCode, false);
            if (addBSButton != null) {
                exportChoiceMap.put(ExportChoice.needMavenScript, addBSButton.getSelection());
            }
            return exportChoiceMap;
        }

        if (EXPORTTYPE_WSWAR.equals(exportType)) {
            exportChoiceMap.put(ExportChoice.needMetaInfo, true);
        } else {
            exportChoiceMap.put(ExportChoice.needMetaInfo, false);
        }

        // fix bug 9150, export items and code source, added by nma
        exportChoiceMap.put(ExportChoice.needJobItem, jobScriptButton.getSelection());
        exportChoiceMap.put(ExportChoice.needSourceCode, jobScriptButton.getSelection());

        exportChoiceMap.put(ExportChoice.needWEBXML, webXMLButton.getSelection());
        exportChoiceMap.put(ExportChoice.needCONFIGFILE, configFileButton.getSelection());
        exportChoiceMap.put(ExportChoice.needAXISLIB, axisLibButton.getSelection());
        exportChoiceMap.put(ExportChoice.needWSDD, wsddButton.getSelection());
        exportChoiceMap.put(ExportChoice.needWSDL, wsdlButton.getSelection());
        exportChoiceMap.put(ExportChoice.needJobScript, Boolean.TRUE);
        exportChoiceMap.put(ExportChoice.needContext, contextButton.getSelection());
        exportChoiceMap.put(ExportChoice.applyToChildren, applyToChildrenButton.getSelection());
        return exportChoiceMap;
    }

    protected void createOptionsGroupButtons(Composite parent) {

        GridLayout layout = new GridLayout();
        optionsGroupComposite = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        gridData.heightHint = 200;
        optionsGroupComposite.setLayoutData(gridData);
        optionsGroupComposite.setLayout(layout);
        // options group
        Group optionsGroup = new Group(optionsGroupComposite, SWT.NONE);

        optionsGroup.setLayout(layout);

        optionsGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        optionsGroup.setText(IDEWorkbenchMessages.WizardExportPage_options);
        optionsGroup.setFont(parent.getFont());

        Font font = optionsGroup.getFont();
        optionsGroup.setLayout(new GridLayout(1, true));

        Composite left = new Composite(optionsGroup, SWT.NONE);
        gridData = new GridData(SWT.LEFT, SWT.TOP, true, false);
        left.setLayoutData(gridData);
        left.setLayout(new GridLayout(3, true));

        if (getCurrentExportType().equals(EXPORTTYPE_POJO)) {
            createOptions(left, font);
            restoreWidgetValuesForPOJO();
        } else if (getCurrentExportType().equals(EXPORTTYPE_JBOSSESB)) {
            createOptionsForJbossESB(left, font);
            restoreWidgetValuesForESB();
        } else if (getCurrentExportType().equals(EXPORTTYPE_OSGI)) {
            restoreWidgetValuesForOSGI();
        } else if (getCurrentExportType().equals(EXPORTTYPE_KAR)) {
            createOptionsForKar(left, font);
            restoreWidgetValuesForKar();
        } else if (getCurrentExportType().equals(EXPORTTYPE_PETALSESB)) {
            createOptionsforPetalsESB(left, font);
            restoreWidgetValuesForPetalsESB();
            restoreWidgetValues();
        } else {
            createOptionsForWS(left, font);
        }

    }

    private void createOptionsForKar(Composite optionsGroup, Font font) {
        IExportRouteResourcesService resourcesService = null;
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IExportRouteResourcesService.class)) {
            resourcesService = (IExportRouteResourcesService) GlobalServiceRegister.getDefault().getService(
                    IExportRouteResourcesService.class);
        }
        if (resourcesService == null) {
            return;
        }

        addBSButton = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
        addBSButton.setText("Add maven script"); //$NON-NLS-1$
        addBSButton.setFont(font);
        addBSButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean show = addBSButton.getSelection();
                String destinationValue = getDestinationValue();
                if (destinationValue.endsWith(getOutputSuffix())) {
                    if (show) {
                        destinationValue = destinationValue.substring(0, destinationValue.indexOf(getOutputSuffix()))
                                + OUTPUT_FILE_SUFFIX;
                    }
                } else if (destinationValue.endsWith(OUTPUT_FILE_SUFFIX)) {
                    if (!show) {
                        destinationValue = destinationValue.substring(0, destinationValue.indexOf(OUTPUT_FILE_SUFFIX))
                                + getOutputSuffix();
                    }
                }
                setDestinationValue(destinationValue);
            }
        });

    }

    private void restoreWidgetValuesForKar() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            String[] directoryNames = settings.getArray(STORE_DESTINATION_NAMES_ID);
            if (directoryNames != null && directoryNames.length > 0) {
                String fileName = getDefaultFileName().get(0) + "_" + getDefaultFileName().get(1) + getOutputSuffix();
                for (String directoryName : directoryNames) {
                    String destination = new Path(directoryName).append(fileName).toOSString();
                    addDestinationItem(destination);
                    setDestinationValue(destination);
                }
            } else {
                setDefaultDestination();
            }
        } else {
            setDefaultDestination();
        }

    }

    protected void restoreWidgetValuesForOSGI() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            String[] directoryNames = settings.getArray(STORE_DESTINATION_NAMES_ID);
            if (directoryNames != null && directoryNames.length > 0) {
                String fileName = getDefaultFileName().get(0) + "_" + getDefaultFileName().get(1) + getOutputSuffix();
                for (String directoryName : directoryNames) {
                    String destination = new Path(directoryName).append(fileName).toOSString();
                    addDestinationItem(destination);
                    setDestinationValue(destination);
                }
            } else {
                setDefaultDestination();
            }
        } else {
            setDefaultDestination();
        }
    }

    @Override
    protected void restoreWidgetValues() {

        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            String[] directoryNames = settings.getArray(PETALS_EXPORT_DESTINATIONS);
            if (directoryNames == null || directoryNames.length == 0) {
                return;
            }

            if (directoryNames[0].endsWith(getPetalsDefaultSaName())) {
                setDestinationValue(directoryNames[0]);
                this.saDestinationFilePath = directoryNames[0];
            }

            for (String directoryName : directoryNames) {
                addDestinationItem(directoryName);
            }
        }
    }

    private void createOptionsforPetalsESB(Composite left, Font font) {
        GridLayout layout;
        // Buttons
        singletonButton = new Button(left, SWT.CHECK | SWT.LEFT);
        singletonButton.setText(Messages.getString("PetalsJobScriptsExportWizardPage.SingletonJob")); //$NON-NLS-1$
        singletonButton.setFont(font);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        singletonButton.setLayoutData(gd);
        singletonButton.setSelection(PetalsTemporaryOptionsKeeper.INSTANCE.isSingleton());
        singletonButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selection = singletonButton.getSelection();
                PetalsTemporaryOptionsKeeper.INSTANCE.setSingleton(selection);
            }
        });

        // userRoutineButton = new Button(left, SWT.CHECK | SWT.LEFT);
        //        userRoutineButton.setText("User Routines"); //$NON-NLS-1$
        // gd = new GridData(GridData.FILL_HORIZONTAL);
        // gd.horizontalSpan = 2;
        // userRoutineButton.setLayoutData(gd);
        // userRoutineButton.setSelection(true);
        // userRoutineButton.setFont(font);

        generateEndpointButton = new Button(left, SWT.CHECK | SWT.LEFT);
        generateEndpointButton.setText(Messages.getString("PetalsJobScriptsExportWizardPage.GenerateEndpoint")); //$NON-NLS-1$
        generateEndpointButton.setFont(font);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        generateEndpointButton.setLayoutData(gd);
        generateEndpointButton.setSelection(PetalsTemporaryOptionsKeeper.INSTANCE.isGenerateEndpoint());
        generateEndpointButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selection = generateEndpointButton.getSelection();
                PetalsTemporaryOptionsKeeper.INSTANCE.setGenerateEndpoint(selection);
            }
        });

        sourceButton = new Button(left, SWT.CHECK | SWT.LEFT);
        sourceButton.setText("Source Files"); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        sourceButton.setLayoutData(gd);
        sourceButton.setSelection(true);
        sourceButton.setFont(font);

        validateByWsdlButton = new Button(left, SWT.CHECK | SWT.LEFT);
        validateByWsdlButton.setText("Validate Petals messages"); //$NON-NLS-1$
        validateByWsdlButton.setFont(font);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        validateByWsdlButton.setLayoutData(gd);
        validateByWsdlButton.setSelection(PetalsTemporaryOptionsKeeper.INSTANCE.isValidateByWsdl());
        validateByWsdlButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selection = validateByWsdlButton.getSelection();
                PetalsTemporaryOptionsKeeper.INSTANCE.setValidateByWsdl(selection);
                validateOptionsGroup();
            }
        });

        // Default context
        left = new Composite(this.optionsGroupComposite, SWT.NONE);
        left.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginBottom = 7;
        layout.horizontalSpacing = 10;
        left.setLayout(layout);

        new Label(left, SWT.NONE).setText(Messages.getString("PetalsJobScriptsExportWizardPage.JobContext")); //$NON-NLS-1$
        contextCombo = new Combo(left, SWT.DROP_DOWN | SWT.READ_ONLY);
        gd = new GridData();
        gd.widthHint = 180;
        contextCombo.setLayoutData(gd);

        if (this.getProcessItem() != null) {
            List<String> contextNames = JobScriptsExportWizardPage.getJobContexts(this.getProcessItem());
            this.contextCombo.setItems(contextNames.toArray(new String[contextNames.size()]));
        }

        // Exposed contexts
        left = new Composite(this.optionsGroupComposite, SWT.NONE);
        left.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginBottom = 7;
        layout.horizontalSpacing = 10;
        left.setLayout(layout);

        final Link exposedContextsLink = new Link(left, SWT.NONE);
        exposedContextsLink.setText(Messages.getString("PetalsJobScriptsExportWizardPage.EditTheExposedContexts")); //$NON-NLS-1$
        exposedContextsLink.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                ContextExportDialog dlg = new ContextExportDialog(getShell(), currentCtxTypes);
                if (dlg.open() == Window.OK) {
                    currentCtxTypes = dlg.getContexts();
                    String contextName = contextCombo.getItem(contextCombo.getSelectionIndex());
                    ctxToTypeDefs.put(contextName, currentCtxTypes);
                    contextCombo.notifyListeners(SWT.Selection, new Event());
                    validateOptionsGroup();
                }
            }
        });

        // Additional listeners
        contextCombo.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e) {

                int index = contextCombo.getSelectionIndex();
                if (index < 0) {
                    return;
                }

                // Get the context types
                String value = contextCombo.getItem(index);
                currentCtxTypes = ctxToTypeDefs.get(value);
                if (currentCtxTypes == null) {
                    try {
                        currentCtxTypes = TalendUtils.getWsdlSchemaForContexts(getProcessItem(), value);

                    } catch (PetalsExportException e1) {

                        currentCtxTypes = new ArrayList<ContextTypeDefinition>(0);
                        MessageDialog.openError(getShell(), Messages.getString("PetalsJobScriptsExportWizardPage.ContextError"), //$NON-NLS-1$
                                Messages.getString("PetalsJobScriptsExportWizardPage.3")); //$NON-NLS-1$

                    } finally {
                        ctxToTypeDefs.put(value, currentCtxTypes);
                    }
                }

                PetalsTemporaryOptionsKeeper.INSTANCE.setContexts(currentCtxTypes);

                // Update the link label
                int exportedCtxCount = 0;
                for (ContextTypeDefinition ctx : currentCtxTypes) {
                    if (ctx.getExportType() != ContextExportType.NOT_EXPORTED) {
                        exportedCtxCount++;
                    }
                }

                exposedContextsLink.setText(Messages.getString("PetalsJobScriptsExportWizardPage.EditTheExposedContexts_") + exportedCtxCount + ")</a>"); //$NON-NLS-1$ //$NON-NLS-2$
                exposedContextsLink.setEnabled(currentCtxTypes.size() != 0);
            }
        });

        if (contextCombo.getItemCount() > 0) {
            contextCombo.select(0);
            contextCombo.notifyListeners(SWT.Selection, new Event());
        }
    }

    @Override
    protected boolean validateOptionsGroup() {

        boolean isValid = false;
        if (super.validateOptionsGroup()) {

            // WSDL-based validation can only be checked if there is no attachment
            boolean hasAttachment = false;
            for (int i = 0; !hasAttachment && currentCtxTypes != null && i < currentCtxTypes.size(); i++) {
                ContextTypeDefinition def = currentCtxTypes.get(i);
                hasAttachment = def.getExportType() != ContextExportType.NOT_EXPORTED
                        || def.getExportType() != ContextExportType.PARAMETER;
            }

            if (hasAttachment && PetalsTemporaryOptionsKeeper.INSTANCE.isValidateByWsdl()) {
                setErrorMessage(Messages.getString("PetalsJobScriptsExportWizardPage.WsdlBasedValidationNotSupported")); //$NON-NLS-1$
                isValid = false;
            } else {
                setErrorMessage(null);
                isValid = true;
            }
        }

        setPageComplete(isValid);
        return isValid;
    }

    private void createOptionsForJbossESB(Composite left, Font font) {
        contextButton = new Button(left, SWT.CHECK | SWT.LEFT);
        contextButton.setText(Messages.getString("JobScriptsExportWizardPage.contextPerlScripts")); //$NON-NLS-1$
        contextButton.setSelection(true);
        contextButton.setFont(font);

        String jobLabel = ""; //$NON-NLS-1$
        contextCombo = new Combo(left, SWT.PUSH);
        if (getProcessItem() != null) {
            try {
                setProcessItem((ProcessItem) ProxyRepositoryFactory.getInstance()
                        .getUptodateProperty(getProcessItem().getProperty()).getItem());
            } catch (PersistenceException e) {
                e.printStackTrace();
            }
            jobLabel = (getProcessItem()).getProperty().getLabel();
            List<String> contextNames = getJobContexts(getProcessItem());
            contextCombo.setItems(contextNames.toArray(new String[contextNames.size()]));
            if (contextNames.size() > 0) {
                contextCombo.select(0);
            }
        }

        applyToChildrenButton = new Button(left, SWT.CHECK | SWT.LEFT);
        applyToChildrenButton.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.ApplyToChildren")); //$NON-NLS-1$
        applyToChildrenButton.setSelection(true);

        jobItemButton = new Button(left, SWT.CHECK | SWT.LEFT);
        jobItemButton.setText(Messages.getString("JobScriptsExportWizardPage.sourceFiles")); //$NON-NLS-1$
        jobItemButton.setSelection(true);
        jobItemButton.setFont(font);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        jobItemButton.setLayoutData(gd);

        Label esbTypeLabel = new Label(left, SWT.None);
        esbTypeLabel.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.esbExportTypeLabel")); //$NON-NLS-1$

        esbTypeCombo = new Combo(left, SWT.PUSH);
        gd = new GridData();
        gd.horizontalSpan = 2;
        esbTypeCombo.setLayoutData(gd);

        esbTypeCombo.add(ESBTYPE_JBOSS_MQ);
        esbTypeCombo.add(ESBTYPE_JBOSS_MESSAGING);
        esbTypeCombo.select(0);

        Label esbServiceNameLabel = new Label(left, SWT.RIGHT);
        esbServiceNameLabel.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.esbServiceNameLabel")); //$NON-NLS-1$

        esbServiceName = new Text(left, SWT.BORDER);
        esbServiceName.setText("DefaultServiceName"); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        esbServiceName.setLayoutData(gd);

        Label esbCategoryLabel = new Label(left, SWT.None);
        esbCategoryLabel.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.esbCategoryLabel")); //$NON-NLS-1$

        esbCategory = new Text(left, SWT.BORDER);
        esbCategory.setText("DefaultCategory"); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        esbCategory.setLayoutData(gd);

        Label queueLabel = new Label(left, SWT.None);
        queueLabel.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.queueName")); //$NON-NLS-1$

        esbQueueMessageName = new Text(left, SWT.BORDER);
        esbQueueMessageName.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.actionRequest", jobLabel)); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        esbQueueMessageName.setLayoutData(gd);
    }

    protected void createOptionsForWS(Composite optionsGroup, Font font) {

        webXMLButton = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
        webXMLButton.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.WEBXML")); //$NON-NLS-1$
        webXMLButton.setFont(font);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        webXMLButton.setLayoutData(gd);
        webXMLButton.setSelection(true);

        configFileButton = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
        configFileButton.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.ServerConfigFile")); //$NON-NLS-1$
        configFileButton.setFont(font);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        configFileButton.setLayoutData(gd);
        configFileButton.setSelection(true);

        wsddButton = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
        wsddButton.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.WSDDFile")); //$NON-NLS-1$
        wsddButton.setFont(font);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        wsddButton.setLayoutData(gd);
        wsddButton.setSelection(true);

        wsdlButton = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
        wsdlButton.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.WSDLFile")); //$NON-NLS-1$
        wsdlButton.setFont(font);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        wsdlButton.setLayoutData(gd);
        wsdlButton.setSelection(true);

        jobScriptButton = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
        jobScriptButton.setText(Messages.getString("JobScriptsExportWizardPage.sourceFiles")); //$NON-NLS-1$
        jobScriptButton.setSelection(true);
        jobScriptButton.setFont(font);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        jobScriptButton.setLayoutData(gd);

        axisLibButton = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
        axisLibButton.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.AxisLib")); //$NON-NLS-1$
        axisLibButton.setFont(font);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        axisLibButton.setLayoutData(gd);
        axisLibButton.setSelection(true);

        contextButton = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
        contextButton.setText(Messages.getString("JobScriptsExportWizardPage.contextPerlScripts")); //$NON-NLS-1$
        contextButton.setSelection(true);
        contextButton.setFont(font);

        contextCombo = new Combo(optionsGroup, SWT.PUSH);

        applyToChildrenButton = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
        applyToChildrenButton.setText(Messages.getString("JavaJobScriptsExportWSWizardPage.ApplyToChildren")); //$NON-NLS-1$
        applyToChildrenButton.setSelection(true);

        restoreWidgetValuesForWS();

        if (exportTypeCombo.getText().equals(EXPORTTYPE_WSWAR)) {
            webXMLButton.setEnabled(false);
            webXMLButton.setSelection(true);
            configFileButton.setEnabled(false);
            configFileButton.setSelection(true);
            wsddButton.setEnabled(false);
            wsddButton.setSelection(true);
            wsdlButton.setEnabled(false);
            wsdlButton.setSelection(true);
            jobScriptButton.setEnabled(false);
            jobScriptButton.setSelection(true);
            axisLibButton.setEnabled(false);
            axisLibButton.setSelection(true);
            contextButton.setEnabled(false);
            contextButton.setSelection(true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.wizards.exportjob.JobScriptsExportWizardPage#setTopFolder(java.util.List,
     * java.lang.String)
     */
    @Override
    public void setTopFolder(List<ExportFileResource> resourcesToExport, String topFolder) {
        if (getCurrentExportType().equals(EXPORTTYPE_WSWAR) || getCurrentExportType().equals(EXPORTTYPE_WSZIP)
                || getCurrentExportType().equals(EXPORTTYPE_JBOSSESB)) {
            return;
        }
        for (ExportFileResource fileResource : resourcesToExport) {
            String directory = fileResource.getDirectoryName();
            fileResource.setDirectoryName(topFolder + "/" + directory); //$NON-NLS-1$
        }
    }

    public String getExtractOption() {
        if (chkButton != null) {
            return String.valueOf(chkButton.getSelection());
        } else {
            return null;
        }
    }

    @Override
    protected ExportTreeViewer getExportTree() {
        return new ExportCamelTreeViewer(selection, this) {

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.repository.ui.wizards.exportjob.ExportTreeViewer#checkSelection()
             */
            @Override
            protected void checkSelection() {
                checkExport();
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.wizards.exportjob.JobScriptsExportWizardPage#checkExport()
     */
    @Override
    public boolean checkExport() {
        boolean noError = true;
        this.setErrorMessage(null);
        this.setPageComplete(true);
        if (getCurrentExportType().equals(EXPORTTYPE_PETALSESB)) {
            chkButton.setVisible(false);
            zipOption = null;
            if (this.isMultiNodes()) {
                StringBuffer buff = new StringBuffer();
                buff.append(Messages.getString("JavaJobScriptsExportWSWizardPage.singleJobExport")); //$NON-NLS-1$
                this.setErrorMessage(buff.toString());
                this.setPageComplete(false);
                noError = false;
            }
            noError = validateOptionsGroup();
        }
        if (getCurrentExportType().equals(EXPORTTYPE_JBOSSESB)) {
            if (this.isMultiNodes()) {
                StringBuffer buff = new StringBuffer();
                buff.append(Messages.getString("JavaJobScriptsExportWSWizardPage.singleJobExport")); //$NON-NLS-1$
                this.setErrorMessage(buff.toString());
                this.setPageComplete(false);
                noError = false;
            }

            // check if the needed librairy is installed.
            String requiredJar = "jbossesb-rosetta.jar"; //$NON-NLS-1$

            List<ModuleNeeded> toCheck = ModulesNeededProvider.getModulesNeeded();
            for (ModuleNeeded current : toCheck) {
                if (requiredJar.equals(current.getModuleName())) {
                    if (current.getStatus() == ELibraryInstallStatus.NOT_INSTALLED) {
                        StringBuffer buff = new StringBuffer();
                        buff.append(Messages.getString("JavaJobScriptsExportWSWizardPage.exportForJBoss")); //$NON-NLS-1$
                        buff.append(Messages.getString("JavaJobScriptsExportWSWizardPage.checkVersion")); //$NON-NLS-1$

                        this.setErrorMessage(buff.toString());
                        this.setPageComplete(false);
                        noError = false;
                        break;
                    }
                }
            }
        }

        if (getCurrentExportType().equals(EXPORTTYPE_OSGI) || getCurrentExportType().equals(EXPORTTYPE_KAR)) {
            if (this.isMultiNodes()) {
                this.setErrorMessage("This type of export support actually only a single job export.");
                this.setPageComplete(false);
                noError = false;
            }
        }
        if (getCheckNodes().length == 0) {
            StringBuffer buff = new StringBuffer();
            buff.append(Messages.getString("JavaJobScriptsExportWSWizardPage.needOneJobSelected")); //$NON-NLS-1$
            this.setErrorMessage(buff.toString());
            this.setPageComplete(false);
            noError = false;
        }

        return noError;
    }

    // protected String getDestinationValueSU() {
    //        return this.suDestinationFilePath != null ? this.suDestinationFilePath : ""; //$NON-NLS-1$
    // }

    @Override
    public boolean finish() {
        // TESB-5328
        if (getCurrentExportType().equals(EXPORTTYPE_KAR)) {
            String version = getSelectedJobVersion();
            String destinationKar = getDestinationValue();
            if (new File(destinationKar).exists()) {
                boolean yes = MessageDialog.openQuestion(getShell(),
                        Messages.getString("JavaCamelJobScriptsExportWSWizardPage.OverwriteKarTitle"),
                        Messages.getString("JavaCamelJobScriptsExportWSWizardPage.OverwriteKarMessage"));
                if (!yes) {
                    return false;
                }
            }

            JavaCamelJobScriptsExportWSAction action = null;
            Map<ExportChoice, Object> exportChoiceMap = getExportChoiceMap();
            if (exportChoiceMap.containsKey(ExportChoice.needMavenScript)
                    && exportChoiceMap.get(ExportChoice.needMavenScript) == Boolean.TRUE) {
                action = new JavaCamelJobScriptsExportWithMavenAction(exportChoiceMap, nodes[0], version, destinationKar, false);
            } else {
                action = new JavaCamelJobScriptsExportWSAction(nodes[0], version, destinationKar, false);
            }

            try {
                getContainer().run(false, true, action);
            } catch (InvocationTargetException e) {
                MessageBoxExceptionHandler.process(e.getCause(), getShell());
                return false;
            } catch (InterruptedException e) {
                return false;
            }
            manager = action.getManager();
            // save output directory
            manager.setDestinationPath(destinationKar);
            saveWidgetValues();
            return true;
        } else {
            manager = createJobScriptsManager();
            manager.setMultiNodes(isMultiNodes());
            manager.setDestinationPath(getDestinationValue());

            return super.finish();
        }
    }

}
