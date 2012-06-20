// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.resource.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.talend.core.model.properties.FileItem;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.resource.editors.input.RouteResourceInput;
import org.talend.designer.camel.resource.ui.util.RouteResourceUtil;

/**
 * @author xpli
 * 
 */
public class RouteResourceEditor extends TextEditor {

	public static final String ID = "org.talend.designer.camel.resource.editors.RouteResourceEditor";

	private RouteResourceInput rrInput;

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		try {
			ProxyRepositoryFactory.getInstance().lock(
					rrInput.getRepositoryNode().getObject());
		} catch (Exception e) {
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		try {
			ProxyRepositoryFactory.getInstance().unlock(
					rrInput.getRepositoryNode().getObject());
		} catch (Exception e) {
		}
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);

		try {
			RouteResourceUtil.copyResources((FileItem) rrInput.getItem());
		} catch (CoreException e) {
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		this.rrInput = (RouteResourceInput) input;
	}

}
