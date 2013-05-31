package org.talend.repository.services.action;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.properties.Item;
import org.talend.core.model.utils.RepositoryManagerHelper;
import org.talend.core.repository.seeker.RepositorySeekerManager;
import org.talend.core.repository.ui.actions.metadata.AbstractCreateAction;
import org.talend.core.ui.branding.IBrandingConfiguration;
import org.talend.designer.core.DesignerPlugin;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.util.EServiceCoreImage;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.services.utils.WSDLUtils;
import org.talend.repository.ui.views.IRepositoryView;

public class OpenWSDLEditorAction extends AbstractCreateAction implements IIntroAction {

    private final static String EDITOR_ID = "org.talend.repository.services.utils.LocalWSDLEditor";
    private final static String PERSPECTIVE_ID = IBrandingConfiguration.PERSPECTIVE_DI_ID;

    private ServiceItem serviceItem;

    public OpenWSDLEditorAction() {
        this.setText("Open WSDL Editor");
        this.setToolTipText("Open WSDL Editor");

        this.setImageDescriptor(ImageProvider.getImageDesc(EServiceCoreImage.SERVICE_ICON));
    }

    @Override
    protected void init(RepositoryNode node) {
        // disable context menu action except service node
        setEnabled(ESBRepositoryNodeType.SERVICES == node.getObjectType() && isLastVersion(node));

        // anyway initialize for double-click
        if (null != node.getObject()) {
            Item item = node.getObject().getProperty().getItem();
            if (item instanceof ServiceItem) {
                serviceItem = (ServiceItem) item;
            }
        }
    }

    @Override
    public Class<?> getClassForDoubleClick() {
        return ServiceItem.class;
    }

    @Override
    protected void doRun() {
        IFile file = WSDLUtils.getWsdlFile(serviceItem);
        ServiceEditorInput editorInput = new ServiceEditorInput(file, serviceItem);
        editorInput.setReadOnly(!DesignerPlugin.getDefault().getProxyRepositoryFactory().isEditableAndLockIfPossible(serviceItem));
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, EDITOR_ID, true);
        } catch (CoreException e) {
            ExceptionHandler.process(e);
        } 
    }

    public void setServiceItem(ServiceItem serviceItem) {
        this.serviceItem = serviceItem;
    }

    public void run(IIntroSite site, Properties params) {
        PlatformUI.getWorkbench().getIntroManager().closeIntro(PlatformUI.getWorkbench().getIntroManager().getIntro());

        IPerspectiveDescriptor currentPerspective = site.getPage().getPerspective();
        if (!PERSPECTIVE_ID.equals(currentPerspective.getId())) {
            // show required perspective
            IWorkbenchWindow workbenchWindow = site.getWorkbenchWindow();
            try {
                workbenchWindow.getWorkbench().showPerspective(PERSPECTIVE_ID, workbenchWindow);
            } catch (WorkbenchException e) {
                ExceptionHandler.process(e);
                return;
            }
        }

        // find repository node
        IRepositoryView view = RepositoryManagerHelper.getRepositoryView();
        IRepositoryNode repositoryNode = view.getRoot().getRootRepositoryNode(ESBRepositoryNodeType.SERVICES);
        if (null != repositoryNode) {
            setWorkbenchPart(view);
            // find node item
            IRepositoryNode nodeItem = RepositorySeekerManager.getInstance().searchRepoViewNode(params.getProperty("nodeId"), false);
            if (null != nodeItem) {
                // expand/select node item
                view.getViewer().setSelection(new StructuredSelection(nodeItem));
                init((RepositoryNode) nodeItem);
                doRun();
            }
        }
    }

}
