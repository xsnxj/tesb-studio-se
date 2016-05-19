// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
import java.util.EnumMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.talend.camel.designer.i18n.Messages;
import org.talend.camel.designer.ui.wizards.actions.JavaCamelJobScriptsExportWSAction;
import org.talend.camel.designer.ui.wizards.actions.JavaCamelJobScriptsExportWithMavenAction;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.PluginChecker;
import org.talend.core.repository.constants.FileConstants;
import org.talend.designer.runprocess.IProcessor;
import org.talend.repository.ui.wizards.exportjob.ExportTreeViewer;
import org.talend.repository.ui.wizards.exportjob.JavaJobScriptsExportWSWizardPage;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager.ExportChoice;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManagerFactory;

/**
 * DOC x class global comment. Detailled comment <br/>
 * 
 */
@SuppressWarnings("restriction")
public class JavaCamelJobScriptsExportWSWizardPage extends JavaCamelJobScriptsExportWizardPage {

    private static final String EXPORTTYPE_KAR = Messages.getString("JavaCamelJobScriptsExportWSWizardPage.ExportKar");//$NON-NLS-1$

    protected Combo exportTypeCombo;

    protected Composite pageComposite;

    protected Composite optionsGroupComposite;

    protected Composite destinationNameFieldComposite;

    protected Composite destinationNameFieldInnerComposite;

    public JavaCamelJobScriptsExportWSWizardPage(IStructuredSelection selection) {
        super(selection);
    }

    @Override
    public void createControl(Composite parent) {

        initializeDialogUnits(parent);
        GridLayout layout = new GridLayout();

        // if (exportTypeFixed == null || !exportTypeFixed.equals(CamelExportType.EXPORTTYPE_JBOSSESB.label)) {
        SashForm sash = createExportTree(parent);

        pageComposite = new Group(sash, 0);
        pageComposite.setLayout(layout);
        pageComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        pageComposite.setFont(parent.getFont());
        setControl(sash);
        sash.setWeights(new int[] { 0, 1, 23 });
        // } else {
        // pageComposite = new Group(parent, 0);
        // pageComposite.setLayout(layout);
        // pageComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        // pageComposite.setFont(parent.getFont());
        // setControl(parent);
        // }
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
        // exportTypeCombo.add(CamelExportType.EXPORTTYPE_POJO.label);
        // }

        // TESB-3222 Remove unnecessary export type in export wizard page LiXiaopeng
        //        if (!Boolean.getBoolean("talend.export.route.2." + JavaJobScriptsExportWSWizardPage.JobExportType.WSWAR + ".hide")) {//$NON-NLS-1$ //$NON-NLS-2$
        // exportTypeCombo.add(CamelExportType.EXPORTTYPE_WSWAR.label);
        // }
        //        if (!Boolean.getBoolean("talend.export.route.2." + JavaJobScriptsExportWSWizardPage.JobExportType.WSZIP + ".hide")) {//$NON-NLS-1$ //$NON-NLS-2$
        // exportTypeCombo.add(CamelExportType.EXPORTTYPE_WSZIP.label);
        // }
        //        if (!Boolean.getBoolean("talend.export.route.2." + JavaJobScriptsExportWSWizardPage.JobExportType.JBOSSESB + ".hide")) {//$NON-NLS-1$ //$NON-NLS-2$
        // exportTypeCombo.add(CamelExportType.EXPORTTYPE_JBOSSESB.label);
        // }
        //        if (!Boolean.getBoolean("talend.export.route.2." + JavaJobScriptsExportWSWizardPage.JobExportType.PETALSESB + ".hide")) {//$NON-NLS-1$ //$NON-NLS-2$
        // exportTypeCombo.add(CamelExportType.EXPORTTYPE_PETALSESB.label);
        // }
        if (!Boolean.getBoolean("talend.export.route.2." + JavaJobScriptsExportWSWizardPage.JobExportType.OSGI + ".hide")) {//$NON-NLS-1$ //$NON-NLS-2$
            // TESB-5328
            // exportTypeCombo.add(CamelExportType.EXPORTTYPE_OSGI.label);
            exportTypeCombo.add(EXPORTTYPE_KAR);
            exportTypeCombo.setEnabled(false); // only can export kar file
        }

        // exportTypeCombo.add("JBI (JSR 208)");

        exportTypeCombo.setText(EXPORTTYPE_KAR);
        // if (exportTypeFixed != null) {
        // left.setVisible(false);
        // optionsGroup.setVisible(false);
        // exportTypeCombo.setText(exportTypeFixed);
        // }

        exportTypeCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }

            @Override
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
                IProcessor.NO_TRACES, JavaJobScriptsExportWSWizardPage.JobExportType.getTypeFromString(EXPORTTYPE_KAR));
    }

    /**
     * Answer the suffix that files exported from this wizard should have. If this suffix is a file extension (which is
     * typically the case) then it must include the leading period character.
     * 
     */
    @Override
    protected String getOutputSuffix() {
        return FileConstants.KAR_FILE_SUFFIX;
    }

    /**
     * Open an appropriate destination browser so that the user can specify a source to import from.
     */
    @Override
    protected void handleDestinationBrowseButtonPressed() {
        FileDialog dialog = new FileDialog(getContainer().getShell(), SWT.SAVE);
        if (isAddMavenScript()) {
            dialog.setFilterExtensions(new String[] { "*" + FileConstants.ZIP_FILE_SUFFIX, "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            dialog.setFilterExtensions(new String[] { "*.kar", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
        }
        dialog.setText(""); //$NON-NLS-1$
        // this is changed by me shenhaize
        dialog.setFileName(this.getDefaultFileName().get(0));
        String currentSourceString = getDestinationValue();
        int lastSeparatorIndex = currentSourceString.lastIndexOf(File.separator);
        if (lastSeparatorIndex != -1) {
            dialog.setFilterPath(currentSourceString.substring(0, lastSeparatorIndex));
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

            String s = this.getDefaultFileName().get(0);

            if (str.equals(s)) {
                selectedFileName = b + "_" + this.getDefaultFileName().get(1) + idealSuffix; //$NON-NLS-1$
            } else {
                selectedFileName = b + idealSuffix;
            }

        }
        if (selectedFileName != null) {
            setErrorMessage(null);
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

    public boolean isAddMavenScript() {
        if (addBSButton != null) {
            return addBSButton.getSelection();
        }

        return false;
    }

    @Override
    protected void internalSaveWidgetValues() {
        // update directory names history
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            String[] directoryNames = new String[1];
            String destinationValue = manager.getDestinationPath();
            if (destinationValue != null) {
                IPath path = Path.fromOSString(destinationValue);
                destinationValue = path.removeLastSegments(1).toOSString();
            }
            directoryNames[0] = destinationValue;

            settings.put(STORE_DESTINATION_NAMES_ID, directoryNames);
        }
    }

    @Override
    protected Map<ExportChoice, Object> getExportChoiceMap() {
        Map<ExportChoice, Object> exportChoiceMap = new EnumMap<ExportChoice, Object>(ExportChoice.class);
        exportChoiceMap.put(ExportChoice.needJobItem, false);
        exportChoiceMap.put(ExportChoice.needSourceCode, false);

        exportChoiceMap.put(ExportChoice.needMetaInfo, true);
        exportChoiceMap.put(ExportChoice.needContext, true);
        exportChoiceMap.put(ExportChoice.needJobItem, false);
        exportChoiceMap.put(ExportChoice.needSourceCode, false);
        if (addBSButton != null) {
            exportChoiceMap.put(ExportChoice.needMavenScript, addBSButton.getSelection());
        }
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

        createOptionsForKar(left, font);
        restoreWidgetValuesForKar();
    }

    private void createOptionsForKar(Composite optionsGroup, Font font) {
        if (!PluginChecker.isPluginLoaded(PluginChecker.EXPORT_ROUTE_PLUGIN_ID)) {
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
                String fileName = getDefaultFileName().get(0) + '_' + getDefaultFileName().get(1) + getOutputSuffix();
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
    protected ExportTreeViewer getExportTree() {
        return new ExportCamelTreeViewer(selection, this) {

            @Override
            protected void checkSelection() {
                JavaCamelJobScriptsExportWSWizardPage.this.checkExport();
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
        if (getCheckNodes().length == 0) {
            this.setErrorMessage(Messages.getString("JavaJobScriptsExportWSWizardPage.needOneJobSelected")); //$NON-NLS-1$
            this.setPageComplete(false);
            noError = false;
        }

        return noError;
    }

    @Override
    public boolean finish() {
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
    }

}
