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
package org.talend.repository.services.ui;

import java.io.File;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.Messages;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager.ExportChoice;
import org.talend.resource.IExportJobResourcesService;

/**
 * DOC x class global comment. Detailled comment <br/>
 * 
 */
public class ServiceExportWSWizardPage extends WizardPage {

    private String serviceName;

    private String serviceVersion;

    private String destinationValue;

    private Text destinationText;

    private Button addBSButton;

    private static final String OUTPUT_FILE_SUFFIX = ".zip"; //$NON-NLS-1$

    public ServiceExportWSWizardPage(IStructuredSelection selection) {
        super(org.talend.repository.services.Messages.ServiceExportWizard_Wizard_Title);
        @SuppressWarnings("unchecked")
        List<RepositoryNode> nodes = selection.toList();
        serviceName = "";
        serviceVersion = "";
        if (nodes.size() >= 1) {
            RepositoryNode node = nodes.get(0);
            if (node.getType() == ENodeType.REPOSITORY_ELEMENT) {
                IRepositoryViewObject repositoryObject = node.getObject();
                if (node.getProperties(EProperties.CONTENT_TYPE) == ESBRepositoryNodeType.SERVICES) {
                    serviceName = repositoryObject.getLabel();
                    serviceVersion = repositoryObject.getVersion();
                }
            }
        }
    }

    protected void handleDestinationBrowseButtonPressed() {
        FileDialog dialog = new FileDialog(getContainer().getShell(), SWT.SAVE);
        dialog.setFilterExtensions(new String[] { "*.kar", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
        File destination = new File(getDestinationValue());
        dialog.setFileName(destination.getName());
        dialog.setFilterPath(destination.getParent());
        String selectedFileName = dialog.open();
        if (selectedFileName == null) {
            return;
        }
        if (!selectedFileName.endsWith(getOutputSuffix())) {
            selectedFileName += getOutputSuffix();
        }
        checkDestination(selectedFileName);
        destinationValue = selectedFileName;
    }

    private void checkDestination(String fileName) {
        File destination;
        destination = new File(fileName);
        if (destination.exists()) {
            setMessage(Messages.ServiceExportWizard_WarningMessage_WillBeOverwritten);
        } else {
            setMessage(null);
        }
    }

    public String getDestinationValue() {
        if (null == destinationValue) {
            String bundleName = serviceName + "-" + serviceVersion + getOutputSuffix();
            String storedDir = getDialogSettings().get(getClass().getName());
            if (storedDir == null) {
                storedDir = System.getProperty("user.dir");
            }
            IPath path = new Path(storedDir).append(bundleName);
            destinationValue = path.toOSString();
        }
        return destinationValue;
    }

    protected String getOutputSuffix() {
        return FileConstants.KAR_FILE_SUFFIX;
    }

    public void createControl(Composite parent) {
        setControl(parent);
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        container.setLayout(new GridLayout(1, false));

        Group destinationGroup = new Group(container, SWT.NONE);
        destinationGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        destinationGroup.setText("Destination");
        destinationGroup.setLayout(new GridLayout(2, false));

        destinationText = new Text(destinationGroup, SWT.SINGLE | SWT.BORDER);
        destinationText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        destinationText.setText(getDestinationValue());
        destinationText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                destinationValue = destinationText.getText();
                checkDestination(destinationValue);
            }
        });
        Button browseButton = new Button(destinationGroup, SWT.PUSH);
        browseButton.setText("Browse");
        browseButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                handleDestinationBrowseButtonPressed();
                destinationText.setText(destinationValue);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                ;
            }
        });
        Group optionsGroup = new Group(container, SWT.NONE);
        GridData layoutData = new GridData(GridData.FILL_BOTH);
        optionsGroup.setLayoutData(layoutData);
        GridLayout layout = new GridLayout();
        optionsGroup.setLayout(layout);

        optionsGroup.setText(IDEWorkbenchMessages.WizardExportPage_options);
        optionsGroup.setFont(parent.getFont());

        createBSGroup(optionsGroup);
    }

    private void createBSGroup(Group optionsGroup) {
        IExportJobResourcesService resourcesService = null;
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IExportJobResourcesService.class)) {
            resourcesService = (IExportJobResourcesService) GlobalServiceRegister.getDefault().getService(
                    IExportJobResourcesService.class);
        }
        if (resourcesService == null) {
            return;
        }

        Font font = optionsGroup.getFont();
        optionsGroup.setLayout(new GridLayout(2, false));

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

    public Map<ExportChoice, Object> getExportChoiceMap() {
        Map<ExportChoice, Object> exportChoiceMap = new EnumMap<ExportChoice, Object>(ExportChoice.class);
        exportChoiceMap.put(ExportChoice.needLauncher, true);
        exportChoiceMap.put(ExportChoice.needSystemRoutine, true);
        exportChoiceMap.put(ExportChoice.needUserRoutine, true);
        exportChoiceMap.put(ExportChoice.needTalendLibraries, true);
        exportChoiceMap.put(ExportChoice.needJobItem, true);
        exportChoiceMap.put(ExportChoice.needJobScript, true);
        exportChoiceMap.put(ExportChoice.needContext, true);
        exportChoiceMap.put(ExportChoice.needSourceCode, true);
        exportChoiceMap.put(ExportChoice.applyToChildren, false);
        exportChoiceMap.put(ExportChoice.doNotCompileCode, false);
        if (addBSButton != null) {
            exportChoiceMap.put(ExportChoice.needMavenScript, addBSButton.getSelection());
        }

        return exportChoiceMap;
    }

    private void setDestinationValue(String destinationValue) {
        destinationText.setText(destinationValue.trim());
    }

    public void finish() {
        String destination = destinationText.getText().trim();
        if (!"".equals(destination)) {
            getDialogSettings().put(getClass().getName(), destination.substring(0, destination.lastIndexOf(File.separator)));
        }
    }

}
