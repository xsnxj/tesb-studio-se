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

import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.talend.camel.designer.i18n.Messages;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.metadata.managment.ui.wizard.PropertiesWizardPage;
import org.talend.repository.model.IProxyRepositoryService;

/**
 * DOC zwzhao class global comment. Detailled comment
 */
public class EditRoutePropertiesWizardPage extends PropertiesWizardPage {

    protected EditRoutePropertiesWizardPage(String pageName, Property property, IPath destinationPath, boolean readOnly,
            boolean editPath, String lastVersionFound) {
        super(pageName, property, destinationPath, readOnly, editPath, lastVersionFound);
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        boolean alreadyEditedByUser = ((EditRoutePropertiesWizard) this.getWizard()).isAlreadyEditedByUser();
        if (alreadyEditedByUser) {
            Label label = new Label(container, SWT.NONE);
            label.setForeground(ColorConstants.red);
            label.setText(Messages.getString("EditRoutePropertiesWizardPage.alreadyLockedByUser")); //$NON-NLS-1$
            GridData gridData = new GridData();
            gridData.horizontalSpan = 2;
            label.setLayoutData(gridData);
        }

        super.createControl(container);
        setControl(container);
        updateContent();
        addListeners();
        setPageComplete(false);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.wizards.PropertiesWizardPage#getRepositoryObjectType()
     */
    @Override
    public ERepositoryObjectType getRepositoryObjectType() {
        // TODO Auto-generated method stub
        return CamelRepositoryNodeType.repositoryRoutesType;
    }
}
