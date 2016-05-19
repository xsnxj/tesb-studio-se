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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.talend.camel.designer.i18n.Messages;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.RoutineItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.designer.core.DesignerPlugin;
import org.talend.metadata.managment.ui.wizard.PropertiesWizardPage;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IProxyRepositoryService;
import org.talend.repository.model.RepositoryConstants;

/**
 * Page for new project details. <br/>
 * 
 * $Id: NewProcessWizardPage.java 52559 2010-12-13 04:14:06Z nrousseau $
 * 
 */
public class CamelNewProcessWizardPage extends PropertiesWizardPage {

    private static final String DESC = Messages.getString("NewProcessWizard.description"); //$NON-NLS-1$

    /**
     * Constructs a new NewProjectWizardPage.
     * 
     */
    public CamelNewProcessWizardPage(Property property, IPath destinationPath) {
        super("WizardPage", property, destinationPath); //$NON-NLS-1$

        setTitle(Messages.getString("NewProcessWizard.title")); //$NON-NLS-1$
        setDescription(DESC);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        super.createControl(container);

        setControl(container);
        updateContent();
        addListeners();
        setPageComplete(false);
    }

    public ERepositoryObjectType getRepositoryObjectType() {
        return CamelRepositoryNodeType.repositoryRoutesType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.wizards.PropertiesWizardPage#evaluateTextField()
     */
    protected void evaluateTextField() {
        super.evaluateTextField();
        if (nameStatus.getSeverity() == IStatus.OK) {
            evaluateName();
        }
    }

    /**
     * ftang Comment method "evaluateNameInRoutine".
     */
    protected void evaluateName() {
        String jobName = nameText.getText().trim();
        boolean isValid = isNameValidInRountine(jobName);

        // Fix the name evaluate bug temporary LiXP TESB-4533
        // New Route Wizard input name validation fails to notify an invalid
        // name
        if (!isValid || !Pattern.matches(RepositoryConstants.getPattern(ERepositoryObjectType.PROCESS), nameText.getText())
                || nameText.getText().trim().contains(" ")) {
            nameStatus = createStatus(IStatus.ERROR, "Name contains incorrect characters.");
            updatePageStatus();
        }
    }

    /**
     * ftang Comment method "isNameExistingInRountine".
     * 
     * @param jobName
     */
    private boolean isNameValidInRountine(String jobName) {
        Property property = PropertiesFactory.eINSTANCE.createProperty();

        IProxyRepositoryFactory repositoryFactory = DesignerPlugin.getDefault().getRepositoryService()
                .getProxyRepositoryFactory();
        property.setId(repositoryFactory.getNextId());
        RoutineItem routineItem = PropertiesFactory.eINSTANCE.createRoutineItem();
        routineItem.setProperty(property);
        boolean isValid = false;
        try {
            isValid = repositoryFactory.isNameAvailable(property.getItem(), jobName);
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
            return false;
        }
        return isValid;
    }

    @Override
    protected List<IRepositoryViewObject> loadRepViewObjectWithOtherTypes() throws PersistenceException {
        List<IRepositoryViewObject> list = new ArrayList<IRepositoryViewObject>();

        // List for m/r process
        ERepositoryObjectType mrRepObjType = ERepositoryObjectType.valueOf(ERepositoryObjectType.class, "PROCESS_MR");//$NON-NLS-1$
        if (mrRepObjType != null) {
            if (GlobalServiceRegister.getDefault().isServiceRegistered(IProxyRepositoryService.class)) {
                IProxyRepositoryService service = (IProxyRepositoryService) GlobalServiceRegister.getDefault().getService(
                        IProxyRepositoryService.class);

                List<IRepositoryViewObject> mrList = service.getProxyRepositoryFactory().getAll(mrRepObjType, true, false);
                if (mrList != null && mrList.size() > 0) {
                    list.addAll(mrList);
                }
            }
        }

        // List for routine
        if (ERepositoryObjectType.ROUTINES != null) {
            if (GlobalServiceRegister.getDefault().isServiceRegistered(IProxyRepositoryService.class)) {
                IProxyRepositoryService service = (IProxyRepositoryService) GlobalServiceRegister.getDefault().getService(
                        IProxyRepositoryService.class);

                List<IRepositoryViewObject> mrList = service.getProxyRepositoryFactory().getAll(ERepositoryObjectType.ROUTINES,
                        true, false);
                if (mrList != null && mrList.size() > 0) {
                    list.addAll(mrList);
                }
            }
        }

        // List for common process
        if (ERepositoryObjectType.PROCESS != null) {
            if (GlobalServiceRegister.getDefault().isServiceRegistered(IProxyRepositoryService.class)) {
                IProxyRepositoryService service = (IProxyRepositoryService) GlobalServiceRegister.getDefault().getService(
                        IProxyRepositoryService.class);

                List<IRepositoryViewObject> mrList = service.getProxyRepositoryFactory().getAll(ERepositoryObjectType.PROCESS,
                        true, false);
                if (mrList != null && mrList.size() > 0) {
                    list.addAll(mrList);
                }
            }
        }
        return list;
    }

}
