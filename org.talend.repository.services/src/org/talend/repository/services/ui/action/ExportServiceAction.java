package org.talend.repository.services.ui.action;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public class ExportServiceAction extends WorkspaceJob {

	public ExportServiceAction(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor arg0) throws CoreException {
		return null;
	}

}
