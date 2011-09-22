package org.talend.repository.services.action;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.CorePlugin;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.ui.actions.metadata.AbstractCreateAction;
import org.talend.designer.core.DesignerPlugin;
import org.talend.designer.core.ui.MultiPageTalendEditor;
import org.talend.designer.core.ui.editor.ProcessEditorInput;
import org.talend.designer.core.ui.editor.cmd.CreateNodeContainerCommand;
import org.talend.designer.core.ui.editor.nodecontainer.NodeContainer;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.wizards.NewProcessWizard;
import org.talend.designer.runprocess.ItemCacheManager;
import org.talend.repository.model.ComponentsFactoryProvider;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.IRepositoryService;
import org.talend.repository.model.RepositoryConstants;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;

public class CreateNewJobAction extends AbstractCreateAction {

    private String createLabel = "Create New Job";

    private ERepositoryObjectType currentNodeType;

    private boolean creation = false;

    /** Created project. */
    private ProcessItem processItem;

    private Property property;

    private IProxyRepositoryFactory repositoryFactory;

    public CreateNewJobAction() {
        super();

        this.setText(createLabel);
        this.setToolTipText(createLabel);

        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.DEFAULT_IMAGE));

        currentNodeType = ERepositoryObjectType.SERVICESOPERATION;

        this.property = PropertiesFactory.eINSTANCE.createProperty();
        this.property.setAuthor(((RepositoryContext) CorePlugin.getContext().getProperty(Context.REPOSITORY_CONTEXT_KEY))
                .getUser());
        this.property.setVersion(VersionUtils.DEFAULT_VERSION);
        this.property.setStatusCode(""); //$NON-NLS-1$

        processItem = PropertiesFactory.eINSTANCE.createProcessItem();

        processItem.setProperty(property);

        repositoryFactory = DesignerPlugin.getDefault().getRepositoryService().getProxyRepositoryFactory();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.core.repository.ui.actions.metadata.AbstractCreateAction#init(org.talend.repository.model.RepositoryNode
     * )
     */
    @Override
    protected void init(RepositoryNode node) {
        ERepositoryObjectType nodeType = (ERepositoryObjectType) node.getProperties(EProperties.CONTENT_TYPE);
        if (!currentNodeType.equals(nodeType)) {
            return;
        }
        this.setText(createLabel);
        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.DEFAULT_IMAGE));
        //
        boolean flag = true;
        ServiceItem serviceItem = (ServiceItem) node.getParent().getObject().getProperty().getItem();
        EList<ServicePort> listPort = serviceItem.getServiceConnection().getServicePort();
        for (ServicePort port : listPort) {
            List<ServiceOperation> listOperation = port.getServiceOperation();
            for (ServiceOperation operation : listOperation) {
                if (operation.getLabel().equals(node.getLabel())) {
                    if (operation.getReferenceJobId() != null && !operation.getReferenceJobId().equals("")) {
                        flag = false;
                    }
                }
            }
        }
        setEnabled(flag);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.AContextualAction#doRun()
     */
    @Override
    protected void doRun() {
        RepositoryNode node = null;
        NewProcessWizard processWizard = null;
        if (isToolbar()) {
            processWizard = new NewProcessWizard(null);
        } else {
            ISelection selection = getSelection();
            if (selection == null) {
                return;
            }
            Object obj = ((IStructuredSelection) selection).getFirstElement();
            node = (RepositoryNode) obj;
            ItemCacheManager.clearCache();

            IRepositoryService service = DesignerPlugin.getDefault().getRepositoryService();
            IPath path = service.getRepositoryPath((RepositoryNode) node);
            if (RepositoryConstants.isSystemFolder(path.toString())) {
                // Not allowed to create in system folder.
                return;
            }
            String label = initLabel((RepositoryNode) node);
            processWizard = new NewProcessWizard(path, label);
        }

        WizardDialog dlg = new WizardDialog(Display.getCurrent().getActiveShell(), processWizard);
        if (dlg.open() == Window.OK) {
            if (processWizard.getProcess() == null) {
                return;
            }

            ProcessEditorInput fileEditorInput;
            try {
                // Set readonly to false since created job will always be editable.
                fileEditorInput = new ProcessEditorInput(processWizard.getProcess(), false, true, false);
                fileEditorInput.setView(getViewPart());
                IRepositoryNode repositoryNode = RepositoryNodeUtilities.getRepositoryNode(fileEditorInput.getItem()
                        .getProperty().getId(), false);
                fileEditorInput.setRepositoryNode(repositoryNode);

                IWorkbenchPage page = getActivePage();
                IEditorPart openEditor = page.openEditor(fileEditorInput, MultiPageTalendEditor.ID, true);
                CommandStack commandStack = (CommandStack) openEditor.getAdapter(CommandStack.class);

                Node node2 = new Node(ComponentsFactoryProvider.getInstance().get("tESBProviderRequest"),
                        (org.talend.designer.core.ui.editor.process.Process) fileEditorInput.getLoadedProcess());
                NodeContainer nc = new NodeContainer(node2);
                CreateNodeContainerCommand cNcc = new CreateNodeContainerCommand(
                        (org.talend.designer.core.ui.editor.process.Process) fileEditorInput.getLoadedProcess(), nc, new Point(
                                3 * Node.DEFAULT_SIZE, 4 * Node.DEFAULT_SIZE));
                cNcc.execute();
                node2 = new Node(ComponentsFactoryProvider.getInstance().get("tESBProviderResponse"),
                        (org.talend.designer.core.ui.editor.process.Process) fileEditorInput.getLoadedProcess());
                nc = new NodeContainer(node2);
                cNcc = new CreateNodeContainerCommand((org.talend.designer.core.ui.editor.process.Process) fileEditorInput
                        .getLoadedProcess(), nc, new Point(9 * Node.DEFAULT_SIZE, 4 * Node.DEFAULT_SIZE));
                commandStack.execute(cNcc);
                // openEditor.doSave(new NullProgressMonitor());
                String jobName = processWizard.getProcess().getProperty().getLabel();
                String jobID = processWizard.getProcess().getProperty().getId();
                ServiceItem serviceItem = (ServiceItem) node.getParent().getObject().getProperty().getItem();
                EList<ServicePort> listPort = serviceItem.getServiceConnection().getServicePort();
                for (ServicePort port : listPort) {
                    List<ServiceOperation> listOperation = port.getServiceOperation();
                    for (ServiceOperation operation : listOperation) {
                        if (operation.getLabel().equals(node.getLabel())) {
                            operation.setReferenceJobId(jobID);
                            operation.setLabel(operation.getOperationName() + "-" + jobName);
                        }
                    }
                }
                IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
                try {
                    factory.save(serviceItem);
                } catch (PersistenceException e) {
                    e.printStackTrace();
                }
                RepositoryManager.refreshSavedNode(node);
            } catch (PartInitException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                ExceptionHandler.process(e);
            } catch (PersistenceException e) {
                MessageBoxExceptionHandler.process(e);
            }
        }
    }

    private String initLabel(RepositoryNode node) {
        RepositoryNode parent = node.getParent();
        String parentName = "";
        out: for (IRepositoryNode cp : parent.getChildren()) {
            for (IRepositoryNode child : cp.getChildren()) {
                if (child.getLabel().equals(node.getLabel())) {
                    parentName = cp.getLabel();
                    break out;
                }
            }
        }
        return parentName + "_" + node.getLabel();
    }

}
