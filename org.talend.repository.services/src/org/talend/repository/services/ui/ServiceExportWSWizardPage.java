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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.i18n.Messages;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.services.action.OpenWSDLEditorAction;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.ui.scriptmanager.ServiceExportManager;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.ui.wizards.exportjob.JavaJobScriptsExportWSWizardPage;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;

/**
 * DOC x class global comment. Detailled comment <br/>
 *
 */
public class ServiceExportWSWizardPage extends JavaJobScriptsExportWSWizardPage {

	private IStructuredSelection selection;
	private List<ServiceItem> services = new ArrayList<ServiceItem>();
	private String serviceName = "";

    public static final String PETALS_EXPORT_DESTINATIONS = "org.ow2.petals.esbexport.destinations"; //$NON-NLS-1$

    public ServiceExportWSWizardPage(IStructuredSelection selection, String serviceName) {
        super(getJobsSelection(selection), "OSGI Bundle For ESB");
        this.selection = selection;
        this.serviceName = serviceName;

    }

	private final static IStructuredSelection getJobsSelection(IStructuredSelection selection) { //DO NOT OVERRIDE!!! CALLED FROM CONSTRUCTOR!
		List<RepositoryNode> nodes = selection.toList();
		List<RepositoryNode> value = new ArrayList<RepositoryNode>();
		for (RepositoryNode node : nodes) {
            if ((node.getType() == ENodeType.REPOSITORY_ELEMENT) &&
            		(node.getProperties(EProperties.CONTENT_TYPE) == ESBRepositoryNodeType.SERVICES)) 
            {
                IRepositoryViewObject repositoryObject = node.getObject();
                ServiceItem serviceItem = (ServiceItem) node.getObject().getProperty().getItem();
                ServiceConnection serviceConnection = serviceItem.getServiceConnection();
				EList<ServicePort> listPort = serviceConnection.getServicePort();
                for (ServicePort port : listPort) {
                    List<ServiceOperation> listOperation = port.getServiceOperation();
                    for (ServiceOperation operation : listOperation) {
                        if (operation.getReferenceJobId() != null && !operation.getReferenceJobId().equals("")) {
                            value.add(RepositoryNodeUtilities.getRepositoryNode(operation.getReferenceJobId(), false));
                        }
                    }
                }
            }
        }
		return new StructuredSelection(value);
	}
	
	public JobScriptsManager createJobScriptsManager() {
        return new ServiceExportManager(serviceName, selection);
    }

    protected List<String> getDefaultFileName() {
        List<RepositoryNode> nodes = selection.toList();
        List<String> value = Arrays.asList(new String[]{"",""});
		if (nodes.size() >= 1) {
            RepositoryNode node = nodes.get(0);
            if (node.getType() == ENodeType.REPOSITORY_ELEMENT) {
                IRepositoryViewObject repositoryObject = node.getObject();
                if (node.getProperties(EProperties.CONTENT_TYPE) == ESBRepositoryNodeType.SERVICES) {
					value = new ArrayList<String>();
                    value.add(repositoryObject.getLabel());
                    value.add(repositoryObject.getVersion());
                }
            }
        }
        return value;
    }
    
	@Override
	protected String getOutputSuffix() {
		return ".jar";
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
            this.setErrorMessage(Messages.getString("ServiceExportWSWizardPage.needOneJobSelected"));
            this.setPageComplete(false);
            noError = false;
        }

        return noError;
    }
//
    @Override
    public boolean finish() {
        return super.finish();
    }

}
