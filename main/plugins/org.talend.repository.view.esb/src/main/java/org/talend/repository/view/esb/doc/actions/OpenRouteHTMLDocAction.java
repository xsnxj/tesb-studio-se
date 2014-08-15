package org.talend.repository.view.esb.doc.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.talend.camel.core.model.camelProperties.RouteDocumentItem;
import org.talend.core.GlobalServiceRegister;
import org.talend.designer.core.ICamelDesignerCoreService;
import org.talend.repository.documentation.actions.OpenJobHTMLDocInStudioAction;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.RepositoryNode;

public class OpenRouteHTMLDocAction extends OpenJobHTMLDocInStudioAction {
	
    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse.jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(TreeViewer viewer, IStructuredSelection selection) {
        boolean avaiable = !selection.isEmpty() && selection.size() == 1;
        boolean canWork = false;
        if (avaiable) {
            Object o = selection.getFirstElement();
            RepositoryNode node = (RepositoryNode) o;
            switch (node.getType()) {
            case REPOSITORY_ELEMENT:
               if (GlobalServiceRegister.getDefault().isServiceRegistered(ICamelDesignerCoreService.class)) {
                    ICamelDesignerCoreService camelService = (ICamelDesignerCoreService) GlobalServiceRegister.getDefault()
                            .getService(ICamelDesignerCoreService.class);
                    if(camelService.getRouteDocType().equals(node.getObjectType())
                    		|| camelService.getRoutes().equals(node.getObjectType())){
                    	canWork = true;
                    }
                } 
                break;
            default:
                canWork = false;
            }
            if (node.getRoot() != null) {
                project = node.getRoot().getProject();
            }
            if (canWork) {
                if (node.getObject().getRepositoryStatus() == ERepositoryStatus.DELETED) {
                    canWork = false;
                }
            }
        }
        setEnabled(canWork);
    }
    
	@Override
	public Class getClassForDoubleClick() {
		return RouteDocumentItem.class;
	}

}
