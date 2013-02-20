package org.talend.camel.designer.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.talend.camel.designer.i18n.Messages;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;
import org.talend.designer.core.ui.wizards.NewProcessWizard;
import org.talend.repository.model.IProxyRepositoryFactory;

public class AssignJobWizard extends Wizard {

	private static String CHOICE_PAGE = "Choice_Page";
	private static String ASSIGN_JOB = "Assign_Job";

	private AssignJobPage assignJobPage;
	private IWizardPage createJobPage;
	private NewProcessWizard processWizard;
	private AssignChoicePage assignChoicePage;
	private String currentProcessId;
	private String selectedProcessId;	

	public String getSelectedProcessId() {
		return selectedProcessId;
	}

	public AssignJobWizard(String currentProcessId ) {
		this.currentProcessId = currentProcessId;
		setWindowTitle(Messages.getString("AssignJobWizard_windowTitle"));//$NON-NLS-1$
	}

	@Override
	public void addPages() {
		assignJobPage = new AssignJobPage(ASSIGN_JOB, currentProcessId);
	
		processWizard = new NewProcessWizard(null);
		if (processWizard != null) {
			processWizard.setContainer(getContainer());
			processWizard.addPages();
			createJobPage = processWizard.getPages()[0];
		}
		
		assignChoicePage = new AssignChoicePage(assignJobPage, createJobPage, CHOICE_PAGE);
		addPage(assignChoicePage);
		addPage(assignJobPage);
		addPage(createJobPage);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
	}

	@Override
	public boolean canFinish() {
		if(assignChoicePage == getContainer().getCurrentPage()){
			return false;
		}
		return super.canFinish();
	}
	
	@Override
	public boolean performFinish() {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == assignJobPage) {
			if (assignJobPage.finish()) {
				selectedProcessId = assignJobPage.getId();
				return true;				
			}
			return false;
		} else if (currentPage == createJobPage && processWizard != null) {
			if (processWizard.performFinish()) {
				addComponents(processWizard.getProcess().getProcess());
				saveChangedProcess(processWizard.getProcess());
				selectedProcessId = processWizard.getProcess().getProperty().getId();
				RepositoryManager.refreshCreatedNode(ERepositoryObjectType.PROCESS);				
				return true;
			}
			return false;
		}
		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == assignChoicePage) {
			return assignChoicePage.getNextPage();
		}
		return null;
	}

	private void saveChangedProcess (ProcessItem processItem) {
		IProxyRepositoryFactory proxyRepositoryFactory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
			try {
				proxyRepositoryFactory.save(processItem);
			} catch (PersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void addComponents (ProcessType process) {
        NodeType ntRouteInput = TalendFileFactory.eINSTANCE.createNodeType();
        ntRouteInput.setComponentName("tRouteInput");
        ntRouteInput.setPosX(100);            
        ntRouteInput.setPosY(100);            
        process.getNode().add(ntRouteInput);

        NodeType ntRouteOutput = TalendFileFactory.eINSTANCE.createNodeType();
        ntRouteOutput.setComponentName("tRouteOutput");
        ntRouteOutput.setPosX(400);            
        ntRouteOutput.setPosY(100);            
        process.getNode().add(ntRouteOutput);
        
        process.setDefaultContext("Default");
        ContextType defContext = TalendFileFactory.eINSTANCE.createContextType();
        defContext.setName("Default");
        defContext.setConfirmationNeeded(false);        
		process.getContext().add(defContext);        
    }

}
