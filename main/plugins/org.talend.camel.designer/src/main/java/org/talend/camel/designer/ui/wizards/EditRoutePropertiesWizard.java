package org.talend.camel.designer.ui.wizards;

import org.eclipse.core.runtime.IPath;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.camel.designer.i18n.Messages;
import org.talend.repository.ui.wizards.PropertiesWizard;
import org.talend.repository.ui.wizards.routines.EditRoutinePropertiesWizardPage;

/**
 * DOC zwzhao class global comment. Detailled comment
 */
public class EditRoutePropertiesWizard extends PropertiesWizard {

    public EditRoutePropertiesWizard(IRepositoryViewObject repositoryViewObject, IPath path, boolean useLastVersion) {
        super(repositoryViewObject, path, useLastVersion);
        setWindowTitle(Messages.getString("EditRoutePropertiesWizard.wizard.title"));//$NON-NLS-1$
    }

    @Override
    public void addPages() {
        mainPage = new EditRoutePropertiesWizardPage(Messages.getString("EditRoutePropertiesWizard.pageName"), //$NON-NLS-1$
                object.getProperty(), path, isReadOnly(), false, lastVersionFound);
        mainPage.setItem(object.getProperty().getItem());
        // If required to add a converter, then add here.
        addPage(mainPage);
    }

}
