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
package org.talend.repository.view.actions.wizards;

import org.eclipse.core.runtime.IPath;
import org.talend.camel.core.model.camelProperties.CamelPropertiesFactory;
import org.talend.camel.core.model.camelProperties.RouteletProcessItem;
import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.camel.model.CamelRepositoryNodeType;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.designer.core.ui.wizards.NewProcessWizard;
import org.talend.designer.core.ui.wizards.NewProcessWizardPage;

public class NewRouteletWizard extends NewProcessWizard {

    public NewRouteletWizard(IPath path) {
        super(path);

        setDefaultPageImageDescriptor(ImageProvider.getImageDesc(ECamelCoreImage.ROUTELET_ICON));
    }

    @Override
    protected ProcessItem createNewProcessItem() {
        return CamelPropertiesFactory.eINSTANCE.createRouteletProcessItem();
    }

    @Override
    protected NewProcessWizardPage createWizardPage(Property property, IPath destinationPath) {
        final NewProcessWizardPage page = new NewProcessWizardPage(property, destinationPath) {
            @Override
            public ERepositoryObjectType getRepositoryObjectType() {
                return CamelRepositoryNodeType.repositoryRouteletType;
            }
        };
        page.setTitle("New Routelet");
        page.setDescription("Add a Routelet in the repository");
        return page;
    }

    @Override
    public RouteletProcessItem getProcess() {
        return (RouteletProcessItem) super.getProcess();
    }
}
