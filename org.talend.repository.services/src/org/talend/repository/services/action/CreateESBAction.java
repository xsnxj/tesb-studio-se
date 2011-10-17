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
package org.talend.repository.services.action;

import java.util.Properties;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.ProjectRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.ui.ESBWizard;
import org.talend.repository.services.utils.ESBRepositoryNodeType;
import org.talend.repository.ui.actions.AContextualAction;
import org.talend.repository.ui.views.RepositoryView;

/**
 * DOC hwang class global comment. Detailled comment
 */
public class CreateESBAction extends AContextualAction implements IIntroAction {

    private static final String CREATE_LABEL = "Create Service";

    public CreateESBAction() {
        super();
        this.setText(CREATE_LABEL);
        this.setToolTipText(CREATE_LABEL);

        this.setImageDescriptor(ImageProvider.getImageDesc(ECoreImage.METADATA_WSDL_SCHEMA_ICON));
    }

    public CreateESBAction(boolean isToolbar) {
        this();
        setToolbar(isToolbar);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse.jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(TreeViewer viewer, IStructuredSelection selection) {
        boolean canWork = !selection.isEmpty() && selection.size() == 1;
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        if (factory.isUserReadOnlyOnCurrentProject()) {
            canWork = false;
        }
        if (canWork) {
            Object o = selection.getFirstElement();
            RepositoryNode node = (RepositoryNode) o;
            switch (node.getType()) {
            case SIMPLE_FOLDER:
            case SYSTEM_FOLDER:
                ERepositoryObjectType nodeType = (ERepositoryObjectType) node.getProperties(EProperties.CONTENT_TYPE);
                if (nodeType != ESBRepositoryNodeType.SERVICES) {
                    canWork = false;
                }
                if (node.getObject() != null && node.getObject().getProperty().getItem().getState().isDeleted()) {
                    canWork = false;
                }
                break;
            default:
                canWork = false;
            }
            if (canWork && !ProjectManager.getInstance().isInCurrentMainProject(node)) {
                canWork = false;
            }
        }
        setEnabled(canWork);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    protected void doRun() {
        // RepositoryNode codeNode = getViewPart().getRoot().getChildren().get(4);
        // RepositoryNode routineNode = codeNode.getChildren().get(0);
        RepositoryNode beanNode = getCurrentRepositoryNode();

        if (isToolbar()) {
            if (beanNode != null && beanNode.getContentType() != ESBRepositoryNodeType.SERVICES) {
                beanNode = null;
            }
            if (beanNode == null) {
                beanNode = getRepositoryNodeForDefault(ESBRepositoryNodeType.SERVICES);
            }
        }
        ISelection selection;
        IWorkbenchPage activePage = getActivePage();
        if (activePage == null) {
            selection = getSelection();
        } else {
            selection = getRepositorySelection();
        }
        ESBWizard beanWizard = new ESBWizard(PlatformUI.getWorkbench(), true, selection);
        WizardDialog dlg = new WizardDialog(Display.getCurrent().getActiveShell(), beanWizard);

        if (dlg.open() == Window.OK) {
            RepositoryManager.refreshCreatedNode(ESBRepositoryNodeType.SERVICES);
        }
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.eclipse.ui.intro.config.IIntroAction#run(org.eclipse.ui.intro.IIntroSite, java.util.Properties)
     */
    public void run(IIntroSite site, Properties params) {
        PlatformUI.getWorkbench().getIntroManager().closeIntro(PlatformUI.getWorkbench().getIntroManager().getIntro());
        selectRootObject(params);
        doRun();
    }

    private void selectRootObject(Properties params) {
        try {
            IViewPart findView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(RepositoryView.ID);
            if (findView == null) {
                findView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(RepositoryView.ID);
            }
            RepositoryView view = (RepositoryView) findView;

            Object type = params.get("type");
            if (ESBRepositoryNodeType.SERVICES.name().equals(type)) {
                IRepositoryNode processNode = ((ProjectRepositoryNode) view.getRoot()).getProcessNode();
                if (processNode != null) {
                    setWorkbenchPart(view);
                    view.getViewer().expandToLevel(processNode, 1);
                    view.getViewer().setSelection(new StructuredSelection(processNode));
                }

            }
        } catch (PartInitException e) {
            ExceptionHandler.process(e);
        }

    }

}
