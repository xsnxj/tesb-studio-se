package org.talend.repository.view.esb.doc.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.repository.documentation.actions.ExportAllJobsDocAction;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;

public class ExportAllRoutesDocAction extends ExportAllJobsDocAction {

    public ExportAllRoutesDocAction() {
        super();

        setText("Export All Routes Documentation"); //$NON-NLS-1$
        setToolTipText("Export All Routes Documentation"); //$NON-NLS-1$
        setImageDescriptor(ImageProvider.getImageDesc(ECoreImage.DOCUMENTATION_ICON));
    }
    
	@Override
	public void init(TreeViewer viewer, IStructuredSelection selection) {
		boolean canWork = false;
		if (selection.isEmpty() || selection.size() > 1) {
			canWork = false;
		} else if (ProxyRepositoryFactory.getInstance()
				.isUserReadOnlyOnCurrentProject()) {
			canWork = false;
		} else {
			Object o = selection.getFirstElement();
			RepositoryNode node = (RepositoryNode) o;
			Object property = node.getProperties(EProperties.CONTENT_TYPE);
			// avoid to add this action on the sub-folder of "Jobs" node.
			boolean isRoutesNode = CamelRepositoryNodeType.repositoryDocumentationsType
					.equals(property);
			if (isRoutesNode) {
				canWork = true;
			}
			project = node.getRoot().getProject();
		}
		setEnabled(canWork);
	}
	
	@Override
	protected void doRun() {
		doExport(CamelRepositoryNodeType.repositoryDocumentationsType, CamelRepositoryNodeType.repositoryDocumentationType);
	}

}
