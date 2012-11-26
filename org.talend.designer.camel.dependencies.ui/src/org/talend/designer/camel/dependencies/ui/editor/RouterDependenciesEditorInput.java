package org.talend.designer.camel.dependencies.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.talend.designer.camel.dependencies.ui.UIActivator;
import org.talend.repository.model.RepositoryNode;

public class RouterDependenciesEditorInput implements IEditorInput {

	private RepositoryNode node;
	private boolean isReadOnly;

	public RouterDependenciesEditorInput(RepositoryNode node, boolean readOnly) {
		this.node = node;
		this.isReadOnly = readOnly;
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == RepositoryNode.class) {
			return node;
		}
		return null;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return UIActivator.getImageDescriptor(UIActivator.DEPEN_ICON);
	}

	@Override
	public String getName() {
		return node.getObject().getLabel() + "_Dependencies"; //$NON-NLS-1$
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "Dependencies of " + node.getObject().getLabel(); //$NON-NLS-1$
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof RouterDependenciesEditorInput)) {
			return false;
		}

		return node.equals(((RouterDependenciesEditorInput) obj).node);
	}

	@Override
	public int hashCode() {
		return node.hashCode();
	}
	
	public boolean isReadOnly() {
		return isReadOnly;
	}
}
