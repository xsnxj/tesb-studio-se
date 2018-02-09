// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.core.model.camelProperties.CamelPropertiesFactory;
import org.talend.camel.designer.i18n.Messages;
import org.talend.camel.designer.util.CamelSpringUtil;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.CorePlugin;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.relationship.RelationshipItemBuilder;
import org.talend.core.model.routines.RoutinesUtil;
import org.talend.designer.core.DesignerPlugin;
import org.talend.designer.core.model.utils.emf.talendfile.ParametersType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.model.utils.emf.talendfile.RoutinesParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;
import org.talend.repository.RepositoryWorkUnit;
import org.talend.repository.model.IProxyRepositoryFactory;

/**
 * Wizard for the creation of a new project. <br/>
 * 
 * $Id: NewProcessWizard.java 52559 2010-12-13 04:14:06Z nrousseau $
 * 
 */
public class CamelNewProcessWizard extends Wizard {

    /** Main page. */
    private CamelNewProcessWizardPage mainPage;

    /** Created project. */
    private CamelProcessItem processItem;

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
    public CamelNewProcessWizard(IPath path) {
        super();
        this.path = path;

        this.property = PropertiesFactory.eINSTANCE.createProperty();
        this.property.setAuthor(((RepositoryContext) CorePlugin.getContext().getProperty(Context.REPOSITORY_CONTEXT_KEY))
                .getUser());
        this.property.setVersion(VersionUtils.DEFAULT_VERSION);
        this.property.setStatusCode(""); //$NON-NLS-1$

        processItem = CamelPropertiesFactory.eINSTANCE.createCamelProcessItem();

        processItem.setProperty(property);

        repositoryFactory = DesignerPlugin.getDefault().getRepositoryService().getProxyRepositoryFactory();

        setDefaultPageImageDescriptor(ImageProvider.getImageDesc(ECoreImage.DEFAULT_WIZ));
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        mainPage = new CamelNewProcessWizardPage(property, path);
        addPage(mainPage);
        setWindowTitle(Messages.getString("NewProcessWizard.windowTitle")); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean performFinish() {
        try {

            property.setId(repositoryFactory.getNextId());

			// http://jira.talendforge.org/browse/TESB-5000 LiXiaopeng
			property.setLabel(property.getDisplayName());

            ProcessType process = TalendFileFactory.eINSTANCE.createProcessType();
            ParametersType parameterType = TalendFileFactory.eINSTANCE.createParametersType();
            // add depended routines.
            List<RoutinesParameterType> dependenciesInPreference = RoutinesUtil.createDependenciesInPreference();
            parameterType.getRoutinesParameter().addAll(dependenciesInPreference);
            process.setParameters(parameterType);
            processItem.setProcess(process);
            
           //ADDED for TESB-7887 By GangLiu
            processItem.setSpringContent(CamelSpringUtil.getDefaultContent(processItem));
            
            RepositoryWorkUnit<Object> workUnit = new RepositoryWorkUnit<Object>(this.getWindowTitle(), this) {

                @Override
                protected void run() throws LoginException, PersistenceException {
                    repositoryFactory.create(processItem, mainPage.getDestinationPath());
                    RelationshipItemBuilder.getInstance().addOrUpdateItem(processItem);
                }
            };
            workUnit.setAvoidUnloadResources(true);
            repositoryFactory.executeRepositoryWorkUnit(workUnit);
        } catch (PersistenceException e) {
            MessageDialog.openError(getShell(), Messages.getString("NewProcessWizard.failureTitle"), Messages //$NON-NLS-1$
                    .getString("NewProcessWizard.failureText") + " : " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
            ExceptionHandler.process(e);
        }
        return processItem != null;
    }

    /**
     * Getter for project.
     * 
     * @return the project
     */
    public CamelProcessItem getProcess() {
        return this.processItem;
    }
}
