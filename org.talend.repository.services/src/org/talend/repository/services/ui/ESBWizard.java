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
package org.talend.repository.services.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.CorePlugin;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServicesFactory;
import org.talend.repository.services.utils.ESBImage;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.ui.wizards.metadata.connection.Step0WizardPage;

/**
 * hwang class global comment. Detailled comment
 */
public class ESBWizard extends Wizard {

    /** Main page. */
    private Step0WizardPage mainPage;

    /** Created project. */
    private ServiceItem serviceItem;

    private Property property;

    private IPath path;

    private IProxyRepositoryFactory repositoryFactory;

    /**
     * Constructs a new NewProjectWizard.
     * 
     * @param author Project author.
     * @param server
     * @param password
     */
    public ESBWizard(IPath path) {
        super();
        this.path = path;

        this.property = PropertiesFactory.eINSTANCE.createProperty();
        this.property.setAuthor(((RepositoryContext) CorePlugin.getContext().getProperty(Context.REPOSITORY_CONTEXT_KEY))
                .getUser());
        this.property.setVersion(VersionUtils.DEFAULT_VERSION);
        this.property.setStatusCode(""); //$NON-NLS-1$

        serviceItem = ServicesFactory.eINSTANCE.createServiceItem();

        serviceItem.setProperty(property);

        // ILibrariesService service = CorePlugin.getDefault().getLibrariesService();
        // URL url = service.getBeanTemplate();
        // ByteArray byteArray = PropertiesFactory.eINSTANCE.createByteArray();
        // InputStream stream = null;
        // try {
        // stream = url.openStream();
        // byte[] innerContent = new byte[stream.available()];
        // stream.read(innerContent);
        // stream.close();
        // byteArray.setInnerContent(innerContent);
        // } catch (IOException e) {
        // RuntimeExceptionHandler.process(e);
        // }
        //
        // serviceItem.setWSDLContent(byteArray);
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        mainPage = new Step0WizardPage(property, path, ESBRepositoryNodeType.SERVICES, false, true);
        addPage(mainPage);
        setWindowTitle(""); //$NON-NLS-1$
        setDefaultPageImageDescriptor(ImageProvider.getImageDesc(ESBImage.SERVICE_ICON));
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean performFinish() {
        IProxyRepositoryFactory repositoryFactory = ProxyRepositoryFactory.getInstance();
        try {
            property.setId(repositoryFactory.getNextId());
            serviceItem.setServiceConnection(ServicesFactory.eINSTANCE.createServiceConnection());
            repositoryFactory.create(serviceItem, mainPage.getDestinationPath());
        } catch (PersistenceException e) {
            MessageDialog.openError(getShell(), "", ""); //$NON-NLS-1$ //$NON-NLS-2$
            ExceptionHandler.process(e);
        }

        return serviceItem != null;
    }

    /**
     * Getter for project.
     * 
     * @return the project
     */
    public ServiceItem getBean() {
        return this.serviceItem;
    }
}
