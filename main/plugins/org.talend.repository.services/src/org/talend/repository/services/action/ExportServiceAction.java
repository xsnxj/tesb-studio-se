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
package org.talend.repository.services.action;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.Messages;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.ui.ServiceExportWizard;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.ui.actions.AContextualAction;

/**
 * Action used to export job scripts. <br/>
 * 
 * $Id: ExportJobScriptAction.java 1 2006-12-13 下午03:12:05 bqian
 * 
 */
public class ExportServiceAction extends AContextualAction {

    protected static final String EXPORT_SERVICE_LABEL = Messages.ExportServiceAction_Action_Label;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse.jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(TreeViewer viewer, IStructuredSelection selection) {
        boolean canWork = true;
        if (selection.isEmpty() || (selection.size() > 1)) {
            setEnabled(false);
            return;
        }

        @SuppressWarnings("unchecked")
        List<RepositoryNode> nodes = selection.toList();
        for (RepositoryNode node : nodes) {
            if (node.getType() != ENodeType.REPOSITORY_ELEMENT
                    || node.getProperties(EProperties.CONTENT_TYPE) != ESBRepositoryNodeType.SERVICES) {
                canWork = false;
                break;
            }
            if (canWork && node.getObject() != null
                    && ProxyRepositoryFactory.getInstance().getStatus(node.getObject()) == ERepositoryStatus.DELETED) {
                canWork = false;
                break;
            }
        }
        setEnabled(canWork);
    }

    @Override
    public boolean isVisible() {
        return isEnabled();
    }

    public ExportServiceAction() {
        super();
        this.setText(EXPORT_SERVICE_LABEL);
        this.setToolTipText(EXPORT_SERVICE_LABEL);
        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.EXPORT_JOB_ICON));
    }

    @Override
    protected void doRun() {
    	Shell activeShell = Display.getCurrent().getActiveShell();
    	
    	Iterator<?> iterator = ((IStructuredSelection)getSelection()).iterator();
    	try {
    		while (iterator.hasNext()) {
    			RepositoryNode node = (RepositoryNode) iterator.next();
    			Item item = node.getObject().getProperty().getItem();
    			if (!(item instanceof ServiceItem)) {
    				continue;
    			}
    			if (!isAllOperationsAssignedJob((ServiceItem) item)) {
    				boolean isContinue = MessageDialog
    						.openQuestion(activeShell, Messages.ExportServiceAction_noJobDialogTitle,
    								Messages.ExportServiceAction_noJobDialogMsg);
    				if (!isContinue) {
    					return;
    				}
    			}
    		}
    	} catch (PersistenceException e) {
    		e.printStackTrace();
    	}
		
        ServiceExportWizard processWizard = new ServiceExportWizard();
        IWorkbench workbench = getWorkbench();
        processWizard.setWindowTitle(EXPORT_SERVICE_LABEL);
        processWizard.init(workbench, (IStructuredSelection) this.getSelection());

        WizardDialog dialog = new WizardDialog(activeShell, processWizard);
        workbench.saveAllEditors(true);
        dialog.open();
    }
    
    private boolean isAllOperationsAssignedJob(ServiceItem serviceItem) throws PersistenceException{
    	ServiceConnection connection = (ServiceConnection) serviceItem.getConnection();
    	EList<ServicePort> servicePort = connection.getServicePort();
    	List<IRepositoryViewObject> jobs = ProxyRepositoryFactory.getInstance().getAll(ERepositoryObjectType.PROCESS);
    	for(ServicePort port: servicePort){
    		EList<ServiceOperation> serviceOperation = port.getServiceOperation();
    		for(ServiceOperation operation: serviceOperation){
    			String referenceJobId = operation.getReferenceJobId();
    			if(referenceJobId == null || referenceJobId.equals("")){ //$NON-NLS-1$
    				return false;
    			}

    			boolean found = false;
    			for (IRepositoryViewObject job : jobs) {
    				if (referenceJobId.equals(job.getId())) {
    					found = true;
    					break;
    				}
    			}
    			if(!found){
    				return false;
    			}
    		}
    	}
    	return true;
    }
}
